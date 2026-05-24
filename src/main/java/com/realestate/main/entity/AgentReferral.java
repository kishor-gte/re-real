package com.realestate.main.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "agent_referrals")
public class AgentReferral {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "referrer_agent_id", nullable = false)
	private Agent referrerAgent;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "referred_agent_id", nullable = false)
	private Agent referredAgent;

	@Column(name = "referral_code_used", nullable = false, length = 20)
	private String referralCodeUsed;

	@Column(nullable = false, length = 30)
	private String status = "REGISTERED";

	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	public AgentReferral() {
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

	public Agent getReferrerAgent() {
		return referrerAgent;
	}

	public void setReferrerAgent(Agent referrerAgent) {
		this.referrerAgent = referrerAgent;
	}

	public Agent getReferredAgent() {
		return referredAgent;
	}

	public void setReferredAgent(Agent referredAgent) {
		this.referredAgent = referredAgent;
	}

	public String getReferralCodeUsed() {
		return referralCodeUsed;
	}

	public void setReferralCodeUsed(String referralCodeUsed) {
		this.referralCodeUsed = referralCodeUsed;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
}
