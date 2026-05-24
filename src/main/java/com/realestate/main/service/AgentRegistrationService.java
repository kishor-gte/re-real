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
import com.realestate.main.dto.PendingAgentRegistrationData;
import com.realestate.main.dto.request.AgentOtpVerifyRequest;
import com.realestate.main.dto.request.AgentRegisterRequest;
import com.realestate.main.dto.response.OtpSendResult;
import com.realestate.main.entity.Agent;
import com.realestate.main.entity.AgentOtpVerification;
import com.realestate.main.exception.AuthException;
import com.realestate.main.repository.AgentOtpVerificationRepository;
import com.realestate.main.util.OtpGenerator;

import jakarta.persistence.EntityManager;

@Service
public class AgentRegistrationService {

	private static final Logger log = LoggerFactory.getLogger(AgentRegistrationService.class);

	private final AgentOtpVerificationRepository otpRepository;
	private final AgentLookupService agentLookup;
	private final PasswordEncoder passwordEncoder;
	private final EmailService emailService;
	private final OtpGenerator otpGenerator;
	private final ObjectMapper objectMapper;
	private final FileStorageService fileStorageService;
	private final EntityManager entityManager;
	private final AgentAccountCreatorService agentAccountCreator;

	@Value("${app.otp.expiry-minutes:5}")
	private int otpExpiryMinutes;

	@Value("${app.otp.resend-seconds:30}")
	private int otpResendSeconds;

	@Value("${app.otp.max-attempts:5}")
	private int maxOtpAttempts;

	public AgentRegistrationService(AgentOtpVerificationRepository otpRepository, AgentLookupService agentLookup,
			PasswordEncoder passwordEncoder, EmailService emailService, OtpGenerator otpGenerator,
			ObjectMapper objectMapper, FileStorageService fileStorageService, EntityManager entityManager,
			AgentAccountCreatorService agentAccountCreator) {
		this.otpRepository = otpRepository;
		this.agentLookup = agentLookup;
		this.passwordEncoder = passwordEncoder;
		this.emailService = emailService;
		this.otpGenerator = otpGenerator;
		this.objectMapper = objectMapper;
		this.fileStorageService = fileStorageService;
		this.entityManager = entityManager;
		this.agentAccountCreator = agentAccountCreator;
	}

	@Transactional
	public OtpSendResult initiateRegistration(AgentRegisterRequest request, MultipartFile profilePhoto,
			MultipartFile agencyLogo, MultipartFile governmentId) {
		if (!request.isPasswordsMatch()) {
			throw new AuthException("Passwords do not match");
		}
		if (!request.isTermsAccepted()) {
			throw new AuthException("Please accept Terms & Conditions");
		}
		if (governmentId == null || governmentId.isEmpty()) {
			throw new AuthException("Government ID proof is required");
		}

		String email = AgentLookupService.normalizeEmail(request.getEmail());
		String mobile = com.realestate.main.util.MobileUtils.normalize(request.getMobile());
		String rera = PendingAgentRegistrationData.normalizeRera(request.getReraNumber());

		if (agentLookup.isEmailRegistered(email)) {
			throw new AuthException("Email already registered");
		}
		if (agentLookup.isMobileRegistered(mobile)) {
			throw new AuthException("Mobile number already registered");
		}
		if (agentLookup.isReraRegistered(rera)) {
			throw new AuthException("RERA number already registered");
		}

		Long referrerId = null;
		if (request.getReferralCode() != null && !request.getReferralCode().isBlank()) {
			String code = request.getReferralCode().trim().toUpperCase();
			referrerId = agentLookup.findReferrerByCode(code).map(Agent::getId)
					.orElseThrow(() -> new AuthException("Invalid referral code"));
		}

		otpRepository.deleteByEmailAndVerifiedFalse(email);

		String profilePath = fileStorageService.storeAgentProfilePhoto(profilePhoto);
		String logoPath = fileStorageService.storeAgentAgencyLogo(agencyLogo);
		String govtPath = fileStorageService.storeAgentGovernmentId(governmentId);

		request.setEmail(email);
		request.setMobile(mobile);
		request.setReraNumber(rera);

		try {
			PendingAgentRegistrationData pending = PendingAgentRegistrationData.from(request, profilePath, logoPath,
					govtPath, referrerId);
			String json = objectMapper.writeValueAsString(pending);
			String otp = otpGenerator.generate();

			AgentOtpVerification record = new AgentOtpVerification();
			record.setEmail(email);
			record.setOtpHash(passwordEncoder.encode(otp));
			record.setExpiresAt(LocalDateTime.now().plusMinutes(otpExpiryMinutes));
			record.setResendAvailableAt(LocalDateTime.now().plusSeconds(otpResendSeconds));
			record.setRegistrationPayload(json);
			otpRepository.save(record);

			return deliverAgentOtp(email, request.getFullName(), otp);
		} catch (AuthException e) {
			throw e;
		} catch (Exception e) {
			log.error("Agent registration failed", e);
			throw new AuthException("Registration failed. Please try again.");
		}
	}

	@Transactional
	public Agent verifyOtp(AgentOtpVerifyRequest request) {
		String email = AgentLookupService.normalizeEmail(request.getEmail());
		String otpDigits = normalizeOtp(request.getOtp());

		if (agentLookup.findByEmail(email).isPresent()) {
			return agentLookup.findByEmail(email).orElseThrow();
		}

		AgentOtpVerification record = otpRepository.findTopByEmailAndVerifiedFalseOrderByCreatedAtDesc(email)
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

		PendingAgentRegistrationData pending = parsePayload(record.getRegistrationPayload());
		if (!email.equals(pending.getEmail())) {
			throw new AuthException("Registration session mismatch. Please register again.");
		}

		try {
			Agent saved = agentAccountCreator.createAndCommit(pending);
			record.setVerified(true);
			otpRepository.save(record);
			emailService.sendAgentPendingApprovalEmail(saved.getEmail(), saved.getFullName(), saved.getAgentCode());
			emailService.sendAdminNewAgentAlertEmail(saved);
			log.info("Agent registered: id={}, code={}, email={}", saved.getId(), saved.getAgentCode(), saved.getEmail());
			return saved;
		} catch (DataIntegrityViolationException e) {
			entityManager.clear();
			return agentLookup.findByEmail(email).orElseThrow(
					() -> new AuthException("Could not complete registration. Email, mobile, or RERA may already exist."));
		}
	}

	@Transactional
	public OtpSendResult resendOtp(String email) {
		String normalized = AgentLookupService.normalizeEmail(email);
		AgentOtpVerification record = otpRepository.findTopByEmailAndVerifiedFalseOrderByCreatedAtDesc(normalized)
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

		String name = "Agent";
		try {
			name = parsePayload(record.getRegistrationPayload()).getFullName();
		} catch (Exception ignored) {
		}
		return deliverAgentOtp(normalized, name, otp);
	}

	private OtpSendResult deliverAgentOtp(String email, String fullName, String otp) {
		try {
			return emailService.sendAgentOtpEmail(email, fullName, otp);
		} catch (Exception ex) {
			log.warn("Agent OTP email error for {}: {}", email, ex.getMessage());
			return emailService.agentOtpFallback(email, otp);
		}
	}

	private PendingAgentRegistrationData parsePayload(String json) {
		if (json == null || json.isBlank()) {
			throw new AuthException("Registration session expired. Please register again.");
		}
		try {
			return objectMapper.readValue(json, PendingAgentRegistrationData.class);
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
