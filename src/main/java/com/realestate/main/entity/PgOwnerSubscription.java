package com.realestate.main.entity;

import java.time.LocalDateTime;

import com.realestate.main.entity.enums.AgentSubscriptionStatus;
import com.realestate.main.entity.enums.SubscriptionPaymentStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "pg_owner_subscriptions")
public class PgOwnerSubscription {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "subscription_id")
	private Long id;

	@Column(name = "pg_owner_id", nullable = false)
	private Long pgOwnerId;

	@Column(name = "plan_id", nullable = false)
	private Long planId;

	@Column(name = "start_date")
	private LocalDateTime startDate;

	@Column(name = "expiry_date")
	private LocalDateTime expiryDate;

	@Column(name = "pg_used", nullable = false)
	private Integer pgUsed = 0;

	@Column(name = "max_pg_allowed", nullable = false)
	private Integer maxPgAllowed = 1;

	@Column(name = "remaining_pg", nullable = false)
	private Integer remainingPg = 1;

	@Enumerated(EnumType.STRING)
	@Column(name = "subscription_status", nullable = false, length = 20)
	private AgentSubscriptionStatus subscriptionStatus = AgentSubscriptionStatus.PENDING;

	@Enumerated(EnumType.STRING)
	@Column(name = "payment_status", nullable = false, length = 20)
	private SubscriptionPaymentStatus paymentStatus = SubscriptionPaymentStatus.PENDING;

	@Column(name = "razorpay_order_id", length = 100)
	private String razorpayOrderId;

	@Column(name = "auto_renew", nullable = false)
	private boolean autoRenew = false;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

	@PrePersist
	void onCreate() {
		LocalDateTime now = LocalDateTime.now();
		createdAt = now;
		updatedAt = now;
	}

	@PreUpdate
	void onUpdate() {
		updatedAt = LocalDateTime.now();
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

	public Long getPlanId() {
		return planId;
	}

	public void setPlanId(Long planId) {
		this.planId = planId;
	}

	public LocalDateTime getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDateTime startDate) {
		this.startDate = startDate;
	}

	public LocalDateTime getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(LocalDateTime expiryDate) {
		this.expiryDate = expiryDate;
	}

	public Integer getPgUsed() {
		return pgUsed;
	}

	public void setPgUsed(Integer pgUsed) {
		this.pgUsed = pgUsed;
	}

	public Integer getMaxPgAllowed() {
		return maxPgAllowed;
	}

	public void setMaxPgAllowed(Integer maxPgAllowed) {
		this.maxPgAllowed = maxPgAllowed;
	}

	public Integer getRemainingPg() {
		return remainingPg;
	}

	public void setRemainingPg(Integer remainingPg) {
		this.remainingPg = remainingPg;
	}

	public AgentSubscriptionStatus getSubscriptionStatus() {
		return subscriptionStatus;
	}

	public void setSubscriptionStatus(AgentSubscriptionStatus subscriptionStatus) {
		this.subscriptionStatus = subscriptionStatus;
	}

	public SubscriptionPaymentStatus getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(SubscriptionPaymentStatus paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	public String getRazorpayOrderId() {
		return razorpayOrderId;
	}

	public void setRazorpayOrderId(String razorpayOrderId) {
		this.razorpayOrderId = razorpayOrderId;
	}

	public boolean isAutoRenew() {
		return autoRenew;
	}

	public void setAutoRenew(boolean autoRenew) {
		this.autoRenew = autoRenew;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}
}
