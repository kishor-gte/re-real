package com.realestate.main.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "referrals")
public class Referral {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "referrer_id", nullable = false, foreignKey = @ForeignKey(name = "fk_referrals_referrer_id"))
	private User referrer;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "referred_user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_referrals_referred_user_id"))
	private User referredUser;

	@Column(name = "referral_code_used", nullable = false, length = 12)
	private String referralCodeUsed;

	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	public Referral() {
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

	public User getReferrer() {
		return referrer;
	}

	public void setReferrer(User referrer) {
		this.referrer = referrer;
	}

	public User getReferredUser() {
		return referredUser;
	}

	public void setReferredUser(User referredUser) {
		this.referredUser = referredUser;
	}

	public String getReferralCodeUsed() {
		return referralCodeUsed;
	}

	public void setReferralCodeUsed(String referralCodeUsed) {
		this.referralCodeUsed = referralCodeUsed;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
}
