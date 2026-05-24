package com.realestate.main.service;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realestate.main.dto.PendingPgOwnerRegistrationData;
import com.realestate.main.dto.request.PgOwnerOtpVerifyRequest;
import com.realestate.main.dto.request.PgOwnerRegisterRequest;
import com.realestate.main.dto.response.OtpSendResult;
import com.realestate.main.entity.PgOwner;
import com.realestate.main.entity.PgOwnerOtpVerification;
import com.realestate.main.entity.enums.AccountStatus;
import com.realestate.main.exception.AuthException;
import com.realestate.main.exception.EmailSendException;
import com.realestate.main.repository.PgOwnerOtpVerificationRepository;
import com.realestate.main.util.MobileUtils;
import com.realestate.main.util.OtpGenerator;

import jakarta.persistence.EntityManager;

@Service
public class PgOwnerRegistrationService {

	private static final Logger log = LoggerFactory.getLogger(PgOwnerRegistrationService.class);

	private final PgOwnerOtpVerificationRepository otpRepository;
	private final PgOwnerOtpPersistenceService otpPersistence;
	private final PgOwnerLookupService pgOwnerLookup;
	private final PasswordEncoder passwordEncoder;
	private final EmailService emailService;
	private final OtpGenerator otpGenerator;
	private final ObjectMapper objectMapper;
	private final FileStorageService fileStorageService;
	private final EntityManager entityManager;
	private final PgOwnerAccountCreatorService pgOwnerAccountCreator;

	@Value("${app.otp.expiry-minutes:5}")
	private int otpExpiryMinutes;

	@Value("${app.otp.resend-seconds:30}")
	private int otpResendSeconds;

	@Value("${app.otp.max-attempts:5}")
	private int maxOtpAttempts;

	public PgOwnerRegistrationService(PgOwnerOtpVerificationRepository otpRepository,
			PgOwnerOtpPersistenceService otpPersistence, PgOwnerLookupService pgOwnerLookup,
			PasswordEncoder passwordEncoder, EmailService emailService, OtpGenerator otpGenerator,
			ObjectMapper objectMapper, FileStorageService fileStorageService, EntityManager entityManager,
			PgOwnerAccountCreatorService pgOwnerAccountCreator) {
		this.otpRepository = otpRepository;
		this.otpPersistence = otpPersistence;
		this.pgOwnerLookup = pgOwnerLookup;
		this.passwordEncoder = passwordEncoder;
		this.emailService = emailService;
		this.otpGenerator = otpGenerator;
		this.objectMapper = objectMapper;
		this.fileStorageService = fileStorageService;
		this.entityManager = entityManager;
		this.pgOwnerAccountCreator = pgOwnerAccountCreator;
	}

	public OtpSendResult initiateRegistration(PgOwnerRegisterRequest request, MultipartFile profilePhoto,
			MultipartFile governmentId) {
		if (!request.isPasswordsMatch()) {
			throw new AuthException("Passwords do not match");
		}
		if (!request.isTermsAccepted()) {
			throw new AuthException("Please accept Terms & Conditions");
		}
		if (governmentId == null || governmentId.isEmpty()) {
			throw new AuthException("Government ID proof is required");
		}

		String email = PgOwnerLookupService.normalizeEmail(request.getEmail());
		String mobile = MobileUtils.normalize(request.getMobile());

		pgOwnerLookup.prepareForRegistration(email, mobile);

		if (request.getReferralCodeUsed() != null && !request.getReferralCodeUsed().isBlank()) {
			String code = request.getReferralCodeUsed().trim().toUpperCase();
			pgOwnerLookup.findReferrerAgentByCode(code)
					.orElseThrow(() -> new AuthException("Invalid referral code"));
		}

		String profilePath = fileStorageService.storePgOwnerProfilePhoto(profilePhoto);
		String govtPath = fileStorageService.storePgOwnerGovernmentId(governmentId);

		request.setEmail(email);
		request.setMobile(mobile);

		try {
			PendingPgOwnerRegistrationData pending = PendingPgOwnerRegistrationData.from(request, profilePath, govtPath);
			String json = objectMapper.writeValueAsString(pending);
			String otp = otpGenerator.generate();
			LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(otpExpiryMinutes);
			LocalDateTime resendAt = LocalDateTime.now().plusSeconds(otpResendSeconds);

			// Persist in an independent transaction first, then email — avoids row locks
			// while SMTP is slow and guarantees the OTP in the inbox matches the database.
			otpPersistence.replacePendingRegistration(email, passwordEncoder.encode(otp), json, expiresAt, resendAt);
			log.info("PG owner OTP persisted for {}", email);

			deliverPgOwnerOtp(email, request.getFullName(), otp);

			return OtpSendResult.sent(email);
		} catch (AuthException e) {
			throw e;
		} catch (EmailSendException e) {
			throw e;
		} catch (Exception e) {
			log.error("PG owner registration failed", e);
			throw new AuthException("Registration failed. Please try again.");
		}
	}

	@Transactional
	public PgOwner verifyOtp(PgOwnerOtpVerifyRequest request) {
		String email = PgOwnerLookupService.normalizeEmail(request.getEmail());
		String otpDigits = normalizeOtp(request.getOtp());

		PgOwnerOtpVerification record = otpRepository.findTopByEmailAndVerifiedFalseOrderByCreatedAtDesc(email)
				.orElseThrow(() -> new AuthException("No pending registration found. Please register again."));

		if (record.getExpiresAt().isBefore(LocalDateTime.now())) {
			throw new AuthException("OTP expired. Please resend OTP.");
		}
		if (record.getAttempts() >= maxOtpAttempts) {
			throw new AuthException("Maximum OTP attempts exceeded");
		}
		if (!passwordEncoder.matches(otpDigits, record.getOtpHash())) {
			otpPersistence.incrementFailedAttempt(record.getId());
			throw new AuthException("Invalid OTP");
		}

		PendingPgOwnerRegistrationData pending = parsePayload(record.getRegistrationPayload());
		if (!email.equals(pending.getEmail())) {
			throw new AuthException("Registration session mismatch. Please register again.");
		}

		pgOwnerLookup.removePlaceholderApplicationIfPresent(pending.getEmail(), pending.getMobile());

		if (pgOwnerLookup.findByEmail(email)
				.filter(o -> o.getAccountStatus() == AccountStatus.ACTIVE)
				.isPresent()) {
			return pgOwnerLookup.findByEmail(email).orElseThrow();
		}

		if (pgOwnerLookup.findByEmail(email)
				.filter(PgOwnerLookupService::isCompletePendingApplication)
				.isPresent()) {
			throw new AuthException(
					"Your PG owner application is already submitted and pending admin approval. Please wait for approval before signing in.");
		}

		pgOwnerLookup.mobileConflictForRegistration(pending.getEmail(), pending.getMobile())
				.ifPresent(msg -> {
					throw new AuthException(msg);
				});

		try {
			PgOwner saved = pgOwnerAccountCreator.createAndCommit(pending);
			otpPersistence.markVerified(record.getId());
			emailService.sendPgOwnerPendingApprovalEmail(saved.getEmail(), saved.getFullName(), saved.getPgOwnerCode());
			emailService.sendAdminNewPgOwnerAlertEmail(saved);
			log.info("PG owner registered: id={}, code={}, email={}", saved.getId(), saved.getPgOwnerCode(),
					saved.getEmail());
			return saved;
		} catch (AuthException e) {
			throw e;
		} catch (DataIntegrityViolationException e) {
			entityManager.clear();
			log.error("PG owner activation integrity error for {}", email, e);
			return pgOwnerLookup.findByEmail(email)
					.filter(PgOwnerLookupService::isCompletePendingApplication)
					.orElseThrow(() -> mapVerifyIntegrityException(e));
		}
	}

	private AuthException mapVerifyIntegrityException(DataIntegrityViolationException e) {
		String msg = rootIntegrityMessage(e);
		if (msg.contains("duplicate") && msg.contains("email")) {
			return new AuthException("This email is already registered. Please sign in.");
		}
		if (msg.contains("duplicate") && msg.contains("mobile")) {
			return new AuthException("This mobile number is already registered. Please sign in or use another number.");
		}
		if (msg.contains("duplicate") && msg.contains("pg_owner_code")) {
			return new AuthException("Registration could not be completed. Please try verifying OTP again.");
		}
		return new AuthException("Could not complete registration. Please try again.");
	}

	private String rootIntegrityMessage(Throwable e) {
		Throwable t = e;
		while (t.getCause() != null) {
			t = t.getCause();
		}
		return t.getMessage() != null ? t.getMessage().toLowerCase() : "";
	}

	public OtpSendResult resendOtp(String email) {
		String normalized = PgOwnerLookupService.normalizeEmail(email);
		PgOwnerOtpVerification record = otpRepository.findTopByEmailAndVerifiedFalseOrderByCreatedAtDesc(normalized)
				.orElseThrow(() -> new AuthException("No pending registration found"));

		if (record.getResendAvailableAt() != null && record.getResendAvailableAt().isAfter(LocalDateTime.now())) {
			throw new AuthException("Please wait 30 seconds before resending OTP");
		}

		String otp = otpGenerator.generate();
		LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(otpExpiryMinutes);
		LocalDateTime resendAt = LocalDateTime.now().plusSeconds(otpResendSeconds);

		String name = "PG Owner";
		try {
			name = parsePayload(record.getRegistrationPayload()).getFullName();
		} catch (Exception ignored) {
		}

		otpPersistence.updatePendingOtp(record.getId(), passwordEncoder.encode(otp), expiresAt, resendAt);
		log.info("PG owner OTP resent and persisted for {}", normalized);

		deliverPgOwnerOtp(normalized, name, otp);

		return OtpSendResult.sent(normalized);
	}

	private OtpSendResult deliverPgOwnerOtp(String email, String fullName, String otp) {
		OtpSendResult result = emailService.sendPgOwnerOtpEmail(email, fullName, otp);
		if (!result.isEmailSent()) {
			throw new EmailSendException(
					"Could not send OTP to your email. Please check the address and try Resend OTP.");
		}
		return result;
	}

	private PendingPgOwnerRegistrationData parsePayload(String json) {
		if (json == null || json.isBlank()) {
			throw new AuthException("Registration session expired. Please register again.");
		}
		try {
			return objectMapper.readValue(json, PendingPgOwnerRegistrationData.class);
		} catch (Exception e) {
			throw new AuthException("Registration session expired. Please register again.");
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
