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
import com.realestate.main.dto.PendingAdminRegistrationData;
import com.realestate.main.dto.request.AdminOtpVerifyRequest;
import com.realestate.main.dto.request.AdminRegisterRequest;
import com.realestate.main.dto.response.OtpSendResult;
import com.realestate.main.entity.Admin;
import com.realestate.main.entity.AdminOtpVerification;
import com.realestate.main.exception.AuthException;
import com.realestate.main.repository.AdminOtpVerificationRepository;
import com.realestate.main.util.OtpGenerator;

import jakarta.persistence.EntityManager;

@Service
public class AdminRegistrationService {

	private static final Logger log = LoggerFactory.getLogger(AdminRegistrationService.class);

	private final AdminOtpVerificationRepository otpRepository;
	private final AdminLookupService adminLookup;
	private final PasswordEncoder passwordEncoder;
	private final EmailService emailService;
	private final OtpGenerator otpGenerator;
	private final ObjectMapper objectMapper;
	private final FileStorageService fileStorageService;
	private final EntityManager entityManager;
	private final AdminAccountCreatorService adminAccountCreator;

	@Value("${app.otp.expiry-minutes:5}")
	private int otpExpiryMinutes;

	@Value("${app.otp.resend-seconds:30}")
	private int otpResendSeconds;

	@Value("${app.otp.max-attempts:5}")
	private int maxOtpAttempts;

	public AdminRegistrationService(AdminOtpVerificationRepository otpRepository, AdminLookupService adminLookup,
			PasswordEncoder passwordEncoder, EmailService emailService, OtpGenerator otpGenerator,
			ObjectMapper objectMapper, FileStorageService fileStorageService, EntityManager entityManager,
			AdminAccountCreatorService adminAccountCreator) {
		this.otpRepository = otpRepository;
		this.adminLookup = adminLookup;
		this.passwordEncoder = passwordEncoder;
		this.emailService = emailService;
		this.otpGenerator = otpGenerator;
		this.objectMapper = objectMapper;
		this.fileStorageService = fileStorageService;
		this.entityManager = entityManager;
		this.adminAccountCreator = adminAccountCreator;
	}

	@Transactional
	public OtpSendResult initiateRegistration(AdminRegisterRequest request, MultipartFile profileImage) {
		if (!request.isPasswordsMatch()) {
			throw new AuthException("Passwords do not match");
		}
		if (!request.isTermsAccepted()) {
			throw new AuthException("Please accept Terms & Conditions");
		}

		if (adminLookup.hasRegisteredAdmin()) {
			throw new AuthException("An admin account already exists. Please sign in.");
		}

		String email = AdminLookupService.normalizeEmail(request.getOfficialEmail());
		String mobile = com.realestate.main.util.MobileUtils.normalize(request.getMobile());

		if (adminLookup.isEmailRegistered(email)) {
			throw new AuthException("Official email already registered");
		}
		if (adminLookup.isMobileRegistered(mobile)) {
			throw new AuthException("Mobile number already registered");
		}

		otpRepository.deleteByOfficialEmailAndVerifiedFalse(email);

		String profilePath = fileStorageService.storeAdminProfileImage(profileImage);
		request.setOfficialEmail(email);
		request.setMobile(mobile);

		try {
			PendingAdminRegistrationData pending = PendingAdminRegistrationData.from(request, profilePath);
			String json = objectMapper.writeValueAsString(pending);
			String otp = otpGenerator.generate();

			AdminOtpVerification record = new AdminOtpVerification();
			record.setOfficialEmail(email);
			record.setOtpHash(passwordEncoder.encode(otp));
			record.setExpiresAt(LocalDateTime.now().plusMinutes(otpExpiryMinutes));
			record.setResendAvailableAt(LocalDateTime.now().plusSeconds(otpResendSeconds));
			record.setRegistrationPayload(json);
			otpRepository.save(record);

			return emailService.sendAdminOtpEmail(email, request.getFullName(), otp);
		} catch (AuthException e) {
			throw e;
		} catch (Exception e) {
			log.error("Admin registration failed", e);
			throw new AuthException("Registration failed. Please try again.");
		}
	}

	@Transactional
	public void verifyOtp(AdminOtpVerifyRequest request) {
		String email = AdminLookupService.normalizeEmail(request.getOfficialEmail());
		String otpDigits = normalizeOtp(request.getOtp());

		if (adminLookup.findByEmail(email).isPresent()) {
			return;
		}

		AdminOtpVerification record = otpRepository.findTopByOfficialEmailAndVerifiedFalseOrderByCreatedAtDesc(email)
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

		PendingAdminRegistrationData pending = parsePayload(record.getRegistrationPayload());
		if (!email.equals(pending.getOfficialEmail())) {
			throw new AuthException("Registration session mismatch. Please register again.");
		}

		try {
			Admin saved = adminAccountCreator.createAndCommit(pending);
			record.setVerified(true);
			otpRepository.save(record);
			emailService.sendAdminWelcomeEmail(saved.getOfficialEmail(), saved.getFullName(), "Administrator");
			log.info("Admin registered: id={}, email={}", saved.getId(), saved.getOfficialEmail());
		} catch (DataIntegrityViolationException e) {
			entityManager.clear();
			if (adminLookup.findByEmail(email).isPresent()) {
				return;
			}
			throw new AuthException("Could not complete admin registration. Email or employee ID may already exist.");
		}
	}

	@Transactional
	public OtpSendResult resendOtp(String email) {
		String normalized = AdminLookupService.normalizeEmail(email);
		AdminOtpVerification record = otpRepository.findTopByOfficialEmailAndVerifiedFalseOrderByCreatedAtDesc(normalized)
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

		String name = "Admin";
		try {
			name = parsePayload(record.getRegistrationPayload()).getFullName();
		} catch (Exception ignored) {
		}
		return emailService.sendAdminOtpEmail(normalized, name, otp);
	}

	private PendingAdminRegistrationData parsePayload(String json) {
		if (json == null || json.isBlank()) {
			throw new AuthException("Registration session expired. Please register again.");
		}
		try {
			return objectMapper.readValue(json, PendingAdminRegistrationData.class);
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
