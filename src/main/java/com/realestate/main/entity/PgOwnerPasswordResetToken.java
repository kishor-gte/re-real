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
@Table(name = "pg_owner_password_reset_tokens")
public class PgOwnerPasswordResetToken {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "pg_owner_id", nullable = false)
	private Long pgOwnerId;

	@Column(name = "token_hash", nullable = false, unique = true, length = 64)
	private String tokenHash;

	@Column(name = "expires_at", nullable = false)
	private LocalDateTime expiresAt;

	@Column(nullable = false)
	private boolean used = false;

	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	public PgOwnerPasswordResetToken() {
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

	public Long getPgOwnerId() {
		return pgOwnerId;
	}

	public void setPgOwnerId(Long pgOwnerId) {
		this.pgOwnerId = pgOwnerId;
	}

	public String getTokenHash() {
		return tokenHash;
	}

	public void setTokenHash(String tokenHash) {
		this.tokenHash = tokenHash;
	}

	public LocalDateTime getExpiresAt() {
		return expiresAt;
	}

	public void setExpiresAt(LocalDateTime expiresAt) {
		this.expiresAt = expiresAt;
	}

	public boolean isUsed() {
		return used;
	}

	public void setUsed(boolean used) {
		this.used = used;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
}
