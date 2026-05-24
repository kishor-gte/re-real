package com.realestate.main.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "pg_owner_otp_verification")
public class PgOwnerOtpVerification {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 150)
	private String email;

	@Column(name = "otp_hash", nullable = false, length = 255)
	private String otpHash;

	@Column(name = "expires_at", nullable = false)
	private LocalDateTime expiresAt;

	@Column(name = "resend_available_at")
	private LocalDateTime resendAvailableAt;

	@Column(nullable = false)
	private int attempts = 0;

	@Column(nullable = false)
	private boolean verified = false;

	@Column(name = "registration_payload", columnDefinition = "TEXT")
	private String registrationPayload;

	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	public PgOwnerOtpVerification() {
	}

	@PrePersist
	void onCreate() {
		createdAt = LocalDateTime.now();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getOtpHash() {
		return otpHash;
	}

	public void setOtpHash(String otpHash) {
		this.otpHash = otpHash;
	}

	public LocalDateTime getExpiresAt() {
		return expiresAt;
	}

	public void setExpiresAt(LocalDateTime expiresAt) {
		this.expiresAt = expiresAt;
	}

	public LocalDateTime getResendAvailableAt() {
		return resendAvailableAt;
	}

	public void setResendAvailableAt(LocalDateTime resendAvailableAt) {
		this.resendAvailableAt = resendAvailableAt;
	}

	public int getAttempts() {
		return attempts;
	}

	public void setAttempts(int attempts) {
		this.attempts = attempts;
	}

	public boolean isVerified() {
		return verified;
	}

	public void setVerified(boolean verified) {
		this.verified = verified;
	}

	public String getRegistrationPayload() {
		return registrationPayload;
	}

	public void setRegistrationPayload(String registrationPayload) {
		this.registrationPayload = registrationPayload;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
}
