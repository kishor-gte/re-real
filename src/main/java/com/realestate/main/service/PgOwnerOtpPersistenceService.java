package com.realestate.main.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.realestate.main.entity.PgOwnerOtpVerification;
import com.realestate.main.exception.AuthException;
import com.realestate.main.repository.PgOwnerOtpVerificationRepository;

/**
 * Commits PG owner OTP rows in an independent transaction so slow SMTP delivery
 * and outer request rollbacks cannot erase or lock OTP rows.
 */
@Service
public class PgOwnerOtpPersistenceService {

	private final PgOwnerOtpVerificationRepository otpRepository;

	public PgOwnerOtpPersistenceService(PgOwnerOtpVerificationRepository otpRepository) {
		this.otpRepository = otpRepository;
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void replacePendingRegistration(String email, String otpHash, String registrationPayload,
			LocalDateTime expiresAt, LocalDateTime resendAvailableAt) {
		otpRepository.deleteByEmailAndVerifiedFalse(email);
		PgOwnerOtpVerification record = new PgOwnerOtpVerification();
		record.setEmail(email);
		record.setOtpHash(otpHash);
		record.setRegistrationPayload(registrationPayload);
		record.setExpiresAt(expiresAt);
		record.setResendAvailableAt(resendAvailableAt);
		record.setAttempts(0);
		record.setVerified(false);
		otpRepository.saveAndFlush(record);
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void updatePendingOtp(Long recordId, String otpHash, LocalDateTime expiresAt,
			LocalDateTime resendAvailableAt) {
		PgOwnerOtpVerification record = otpRepository.findById(recordId)
				.orElseThrow(() -> new AuthException("No pending registration found"));
		record.setOtpHash(otpHash);
		record.setExpiresAt(expiresAt);
		record.setResendAvailableAt(resendAvailableAt);
		record.setAttempts(0);
		otpRepository.saveAndFlush(record);
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void incrementFailedAttempt(Long recordId) {
		PgOwnerOtpVerification record = otpRepository.findById(recordId)
				.orElseThrow(() -> new AuthException("No pending registration found"));
		record.setAttempts(record.getAttempts() + 1);
		otpRepository.saveAndFlush(record);
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void markVerified(Long recordId) {
		PgOwnerOtpVerification record = otpRepository.findById(recordId)
				.orElseThrow(() -> new AuthException("No pending registration found"));
		record.setVerified(true);
		otpRepository.saveAndFlush(record);
	}
}
