package com.realestate.main.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realestate.main.dto.PendingRegistrationData;
import com.realestate.main.dto.request.OtpVerifyRequest;
import com.realestate.main.dto.request.RegisterRequest;
import com.realestate.main.entity.OtpVerification;
import com.realestate.main.entity.Referral;
import com.realestate.main.entity.User;
import com.realestate.main.dto.response.OtpSendResult;
import com.realestate.main.exception.AuthException;
import com.realestate.main.repository.OtpVerificationRepository;
import com.realestate.main.repository.ReferralRepository;
import com.realestate.main.repository.UserRepository;
import com.realestate.main.util.MobileUtils;
import com.realestate.main.util.OtpGenerator;

@Service
public class RegistrationService {

	private static final Logger log = LoggerFactory.getLogger(RegistrationService.class);

	private final UserRepository userRepository;
	private final UserService userService;
	private final OtpVerificationRepository otpRepository;
	private final ReferralRepository referralRepository;
	private final PasswordEncoder passwordEncoder;
	private final EmailService emailService;
	private final OtpGenerator otpGenerator;
	private final ObjectMapper objectMapper;
	private final FileStorageService fileStorageService;
	private final EntityManager entityManager;
	private final UserAccountCreatorService userAccountCreator;
	private final UserLookupService userLookup;

	@Value("${app.otp.expiry-minutes:5}")
	private int otpExpiryMinutes;

	@Value("${app.otp.resend-seconds:30}")
	private int otpResendSeconds;

	@Value("${app.otp.max-attempts:5}")
	private int maxOtpAttempts;

	public RegistrationService(UserRepository userRepository, UserService userService,
			OtpVerificationRepository otpRepository, ReferralRepository referralRepository,
			PasswordEncoder passwordEncoder, EmailService emailService, OtpGenerator otpGenerator,
			ObjectMapper objectMapper, FileStorageService fileStorageService, EntityManager entityManager,
			UserAccountCreatorService userAccountCreator, UserLookupService userLookup) {
		this.userRepository = userRepository;
		this.userService = userService;
		this.otpRepository = otpRepository;
		this.referralRepository = referralRepository;
		this.passwordEncoder = passwordEncoder;
		this.emailService = emailService;
		this.otpGenerator = otpGenerator;
		this.objectMapper = objectMapper;
		this.fileStorageService = fileStorageService;
		this.entityManager = entityManager;
		this.userAccountCreator = userAccountCreator;
		this.userLookup = userLookup;
	}

	public static String normalizeEmail(String email) {
		return UserLookupService.normalizeEmail(email);
	}

	public static String normalizeMobile(String mobile) {
		return MobileUtils.normalize(mobile);
	}

	@Transactional
	public OtpSendResult initiateRegistration(RegisterRequest request, MultipartFile profileImage) {
		validateBusinessRules(request);

		String email = normalizeEmail(request.getEmail());
		String mobile = normalizeMobile(request.getMobile());

		if (userLookup.isEmailRegistered(email)) {
			throw new AuthException("Email already registered. Please login.");
		}
		if (userLookup.isMobileRegistered(mobile)) {
			throw new AuthException("Phone number already registered in users table. Please login or use another number.");
		}

		otpRepository.deleteByEmailAndVerifiedFalse(email);

		String profilePath = fileStorageService.storeProfileImage(profileImage);
		request.setEmail(email);
		request.setMobile(mobile);
		request.setReferralCode(request.getReferralCode() != null ? request.getReferralCode().trim() : null);

		try {
			PendingRegistrationData pending = PendingRegistrationData.from(request, profilePath);
			String json = objectMapper.writeValueAsString(pending);
			String otp = otpGenerator.generate();

			OtpVerification record = new OtpVerification();
			record.setEmail(email);
			record.setOtpHash(passwordEncoder.encode(otp));
			record.setExpiresAt(LocalDateTime.now().plusMinutes(otpExpiryMinutes));
			record.setResendAvailableAt(LocalDateTime.now().plusSeconds(otpResendSeconds));
			record.setRegistrationPayload(json);
			otpRepository.save(record);

			return emailService.sendOtpEmail(record.getEmail(), request.getFullName(), otp);
		} catch (AuthException e) {
			throw e;
		} catch (Exception e) {
			throw new AuthException("Registration failed. Please try again.");
		}
	}

	@Transactional
	public void verifyOtp(OtpVerifyRequest request) {
		String email = normalizeEmail(request.getEmail());
		String otpDigits = normalizeOtp(request.getOtp());

		if (userLookup.findUserByEmail(email).isPresent()) {
			log.info("Account already exists for {}, OTP verify treated as success", email);
			return;
		}

		OtpVerification record = otpRepository.findTopByEmailAndVerifiedFalseOrderByCreatedAtDesc(email)
				.orElseThrow(() -> new AuthException("No pending registration found. Please register again."));

		if (record.getExpiresAt().isBefore(LocalDateTime.now())) {
			throw new AuthException("OTP expired. Please resend OTP.");
		}
		if (record.getAttempts() >= maxOtpAttempts) {
			throw new AuthException("Maximum OTP attempts exceeded");
		}
		if (!passwordEncoder.matches(otpDigits, record.getOtpHash())) {
			record.setAttempts(record.getAttempts() + 1);
			otpRepository.save(record);
			throw new AuthException("Invalid OTP");
		}

		PendingRegistrationData pending = parseRegistrationPayload(record.getRegistrationPayload());
		String pendingEmail = normalizeEmail(pending.getEmail());
		String pendingMobile = normalizeMobile(pending.getMobile());

		assertPendingEmailMatches(email, pendingEmail);
		userLookup.mobileConflictForRegistration(pendingEmail, pendingMobile)
				.ifPresent(msg -> { throw new AuthException(msg); });

		try {
			User savedUser = userAccountCreator.createAndCommit(pending);

			record.setVerified(true);
			otpRepository.save(record);

			saveReferralIfPresent(pending, savedUser);
			emailService.sendWelcomeEmail(savedUser.getEmail(), savedUser.getFullName(), savedUser.getReferralCode());
			log.info("User registered successfully: id={}, email={}", savedUser.getId(), savedUser.getEmail());
		} catch (AuthException e) {
			throw e;
		} catch (DataIntegrityViolationException e) {
			entityManager.clear();
			if (userLookup.findUserByEmail(pendingEmail).isPresent()) {
				log.info("Recovered existing account for {}", pendingEmail);
				return;
			}
			throw mapIntegrityException(e);
		} catch (PersistenceException e) {
			entityManager.clear();
			if (userLookup.findUserByEmail(pendingEmail).isPresent()) {
				return;
			}
			log.error("Persistence error during activation for {}", pendingEmail, e);
			throw new AuthException("Account activation failed. Please register again.");
		} catch (Exception e) {
			entityManager.clear();
			if (userLookup.findUserByEmail(pendingEmail).isPresent()) {
				return;
			}
			log.error("Account activation failed for {}", pendingEmail, e);
			throw new AuthException("Account activation failed. Please register again.");
		}
	}

	private AuthException mapIntegrityException(DataIntegrityViolationException e) {
		String msg = rootMessage(e);
		log.error("Database constraint violation: {}", msg, e);
		if (isDuplicateKey(msg, "email")) {
			return new AuthException("This email is already registered. Please login.");
		}
		if (isDuplicateKey(msg, "mobile") || isDuplicateKey(msg, "phone")) {
			return new AuthException(
					"This phone number is already registered. Please login or use a different number.");
		}
		if (msg.contains("field 'phone'") && msg.contains("default value")) {
			return new AuthException("Database schema error (phone column). Restart the application after migration.");
		}
		return new AuthException("Could not complete registration. Please register again with a fresh OTP.");
	}

	private boolean isDuplicateKey(String msg, String column) {
		if (msg == null || msg.isBlank()) {
			return false;
		}
		boolean duplicate = msg.contains("duplicate") || msg.contains("unique constraint")
				|| msg.contains("unique key");
		boolean columnMatch = msg.contains(column) || msg.contains("users." + column)
				|| msg.contains("'" + column + "'");
		return duplicate && columnMatch;
	}

	private String rootMessage(Throwable e) {
		Throwable t = e;
		while (t.getCause() != null) {
			t = t.getCause();
		}
		return t.getMessage() != null ? t.getMessage().toLowerCase() : "";
	}

	@Transactional
	public OtpSendResult resendOtp(String email) {
		String normalized = normalizeEmail(email);
		OtpVerification record = otpRepository.findTopByEmailAndVerifiedFalseOrderByCreatedAtDesc(normalized)
				.orElseThrow(() -> new AuthException("No pending registration found"));

		if (record.getResendAvailableAt() != null && record.getResendAvailableAt().isAfter(LocalDateTime.now())) {
			throw new AuthException("Please wait 30 seconds before resending OTP");
		}

		String otp = otpGenerator.generate();
		record.setOtpHash(passwordEncoder.encode(otp));
		record.setExpiresAt(LocalDateTime.now().plusMinutes(otpExpiryMinutes));
		record.setResendAvailableAt(LocalDateTime.now().plusSeconds(otpResendSeconds));
		record.setAttempts(0);
		otpRepository.save(record);

		String name = "User";
		try {
			PendingRegistrationData pending = parseRegistrationPayload(record.getRegistrationPayload());
			name = pending.getFullName();
		} catch (Exception ignored) {
		}
		return emailService.sendOtpEmail(normalized, name, otp);
	}

	private void assertPendingEmailMatches(String otpEmail, String pendingEmail) {
		if (!otpEmail.equals(pendingEmail)) {
			throw new AuthException("Registration session mismatch. Please register again.");
		}
	}

	private void validateBusinessRules(RegisterRequest request) {
		if (!request.getPassword().equals(request.getConfirmPassword())) {
			throw new AuthException("Passwords do not match");
		}
		if (!request.isTermsAccepted()) {
			throw new AuthException("Please accept Terms & Conditions");
		}
		if (request.getDateOfBirth() != null) {
			if (request.getDateOfBirth().isAfter(LocalDate.now())) {
				throw new AuthException("Date of birth cannot be in the future");
			}
			int age = Period.between(request.getDateOfBirth(), LocalDate.now()).getYears();
			if (age < 18) {
				throw new AuthException("You must be at least 18 years old");
			}
		}
	}

	private PendingRegistrationData parseRegistrationPayload(String json) {
		if (json == null || json.isBlank()) {
			throw new AuthException("Registration session expired. Please register again.");
		}
		try {
			PendingRegistrationData data = objectMapper.readValue(json, PendingRegistrationData.class);
			data.setEmail(UserLookupService.normalizeEmail(data.getEmail()));
			data.setMobile(MobileUtils.normalize(data.getMobile()));
			return data;
		} catch (Exception e) {
			log.error("Could not read registration payload", e);
			throw new AuthException("Registration session expired. Please register again.");
		}
	}

	private void saveReferralIfPresent(PendingRegistrationData pending, User newUser) {
		String code = pending.getReferralCodeUsed();
		if (code == null || code.isBlank() || newUser.getId() == null) {
			return;
		}
		try {
			userService.findByReferralCode(code.trim()).ifPresent(referrer -> {
				if (referrer.getId() == null || referrer.getId().equals(newUser.getId())) {
					return;
				}
				Referral ref = new Referral();
				ref.setReferrer(referrer);
				ref.setReferredUser(userRepository.getReferenceById(newUser.getId()));
				ref.setReferralCodeUsed(code.trim());
				referralRepository.save(ref);
			});
		} catch (Exception e) {
			log.warn("Referral link skipped for {}: {}", newUser.getEmail(), e.getMessage());
		}
	}

	private String normalizeOtp(String otp) {
		if (otp == null) {
			throw new AuthException("OTP is required");
		}
		String digits = otp.replaceAll("\\D", "");
		if (digits.length() != 6) {
			throw new AuthException("OTP must be 6 digits");
		}
		return digits;
	}
}
