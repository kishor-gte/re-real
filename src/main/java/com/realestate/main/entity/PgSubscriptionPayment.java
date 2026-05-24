package com.realestate.main.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
@Table(name = "pg_subscription_payments")
public class PgSubscriptionPayment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "payment_id")
	private Long id;

	@Column(name = "subscription_id", nullable = false)
	private Long subscriptionId;

	@Column(name = "pg_owner_id", nullable = false)
	private Long pgOwnerId;

	@Column(name = "plan_id", nullable = false)
	private Long planId;

	@Column(name = "transaction_id", length = 120)
	private String transactionId;

	@Column(name = "razorpay_order_id", length = 100)
	private String razorpayOrderId;

	@Column(name = "razorpay_payment_id", length = 100)
	private String razorpayPaymentId;

	@Column(nullable = false, precision = 12, scale = 2)
	private BigDecimal amount;

	@Column(name = "payment_method", length = 40)
	private String paymentMethod;

	@Enumerated(EnumType.STRING)
	@Column(name = "payment_status", nullable = false, length = 20)
	private SubscriptionPaymentStatus paymentStatus = SubscriptionPaymentStatus.PENDING;

	@Column(name = "payment_date")
	private LocalDateTime paymentDate;

	@Column(name = "invoice_number", length = 60)
	private String invoiceNumber;

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

	public Long getSubscriptionId() {
		return subscriptionId;
	}

	public void setSubscriptionId(Long subscriptionId) {
		this.subscriptionId = subscriptionId;
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

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public String getRazorpayOrderId() {
		return razorpayOrderId;
	}

	public void setRazorpayOrderId(String razorpayOrderId) {
		this.razorpayOrderId = razorpayOrderId;
	}

	public String getRazorpayPaymentId() {
		return razorpayPaymentId;
	}

	public void setRazorpayPaymentId(String razorpayPaymentId) {
		this.razorpayPaymentId = razorpayPaymentId;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public SubscriptionPaymentStatus getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(SubscriptionPaymentStatus paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	public LocalDateTime getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(LocalDateTime paymentDate) {
		this.paymentDate = paymentDate;
	}

	public String getInvoiceNumber() {
		return invoiceNumber;
	}

	public void setInvoiceNumber(String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
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
