package com.realestate.main.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.realestate.main.entity.enums.PaymentMethod;
import com.realestate.main.entity.enums.PaymentTransactionStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "pg_payment_transactions")
public class PgPaymentTransaction {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "transaction_id")
	private Long id;

	@Column(name = "transaction_code", nullable = false, unique = true, length = 40)
	private String transactionCode;

	@Column(name = "pg_booking_id", nullable = false)
	private Long pgBookingId;

	@Enumerated(EnumType.STRING)
	@Column(name = "payment_method", nullable = false, length = 20)
	private PaymentMethod paymentMethod;

	@Column(nullable = false, precision = 15, scale = 2)
	private BigDecimal amount;

	@Enumerated(EnumType.STRING)
	@Column(name = "payment_status", nullable = false, length = 20)
	private PaymentTransactionStatus paymentStatus = PaymentTransactionStatus.PENDING;

	@Column(name = "payment_date")
	private LocalDateTime paymentDate;

	@Column(name = "razorpay_payment_id", length = 64)
	private String razorpayPaymentId;

	@Column(name = "razorpay_order_id", length = 64)
	private String razorpayOrderId;

	@Column(name = "gateway_response", columnDefinition = "TEXT")
	private String gatewayResponse;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@PrePersist
	void onCreate() {
		createdAt = LocalDateTime.now();
		if (paymentStatus == null) {
			paymentStatus = PaymentTransactionStatus.PENDING;
		}
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTransactionCode() {
		return transactionCode;
	}

	public void setTransactionCode(String transactionCode) {
		this.transactionCode = transactionCode;
	}

	public Long getPgBookingId() {
		return pgBookingId;
	}

	public void setPgBookingId(Long pgBookingId) {
		this.pgBookingId = pgBookingId;
	}

	public PaymentMethod getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(PaymentMethod paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public PaymentTransactionStatus getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(PaymentTransactionStatus paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	public LocalDateTime getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(LocalDateTime paymentDate) {
		this.paymentDate = paymentDate;
	}

	public String getRazorpayPaymentId() {
		return razorpayPaymentId;
	}

	public void setRazorpayPaymentId(String razorpayPaymentId) {
		this.razorpayPaymentId = razorpayPaymentId;
	}

	public String getRazorpayOrderId() {
		return razorpayOrderId;
	}

	public void setRazorpayOrderId(String razorpayOrderId) {
		this.razorpayOrderId = razorpayOrderId;
	}

	public String getGatewayResponse() {
		return gatewayResponse;
	}

	public void setGatewayResponse(String gatewayResponse) {
		this.gatewayResponse = gatewayResponse;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
}
