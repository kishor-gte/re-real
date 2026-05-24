package com.realestate.main.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.realestate.main.entity.enums.PgRentDueStatus;

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
@Table(name = "pg_monthly_rent_dues")
public class PgMonthlyRentDue {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "rent_due_id")
	private Long id;

	@Column(name = "pg_booking_id", nullable = false)
	private Long pgBookingId;

	@Column(name = "due_date", nullable = false)
	private LocalDate dueDate;

	@Column(name = "period_label", nullable = false, length = 32)
	private String periodLabel;

	@Column(nullable = false, precision = 15, scale = 2)
	private BigDecimal amount;

	@Enumerated(EnumType.STRING)
	@Column(name = "due_status", nullable = false, length = 20)
	private PgRentDueStatus dueStatus = PgRentDueStatus.PENDING;

	@Column(name = "razorpay_order_id", length = 64)
	private String razorpayOrderId;

	@Column(name = "transaction_code", length = 40)
	private String transactionCode;

	@Column(name = "paid_at")
	private LocalDateTime paidAt;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@PrePersist
	void onCreate() {
		createdAt = LocalDateTime.now();
		if (dueStatus == null) {
			dueStatus = PgRentDueStatus.PENDING;
		}
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getPgBookingId() {
		return pgBookingId;
	}

	public void setPgBookingId(Long pgBookingId) {
		this.pgBookingId = pgBookingId;
	}

	public LocalDate getDueDate() {
		return dueDate;
	}

	public void setDueDate(LocalDate dueDate) {
		this.dueDate = dueDate;
	}

	public String getPeriodLabel() {
		return periodLabel;
	}

	public void setPeriodLabel(String periodLabel) {
		this.periodLabel = periodLabel;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public PgRentDueStatus getDueStatus() {
		return dueStatus;
	}

	public void setDueStatus(PgRentDueStatus dueStatus) {
		this.dueStatus = dueStatus;
	}

	public String getRazorpayOrderId() {
		return razorpayOrderId;
	}

	public void setRazorpayOrderId(String razorpayOrderId) {
		this.razorpayOrderId = razorpayOrderId;
	}

	public String getTransactionCode() {
		return transactionCode;
	}

	public void setTransactionCode(String transactionCode) {
		this.transactionCode = transactionCode;
	}

	public LocalDateTime getPaidAt() {
		return paidAt;
	}

	public void setPaidAt(LocalDateTime paidAt) {
		this.paidAt = paidAt;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
}
