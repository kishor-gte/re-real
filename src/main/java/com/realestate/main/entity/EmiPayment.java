package com.realestate.main.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.realestate.main.entity.enums.InstallmentStatus;

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
@Table(name = "emi_payments")
public class EmiPayment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "installment_plan_id", nullable = false)
	private Long installmentPlanId;

	@Column(name = "installment_number", nullable = false)
	private Integer installmentNumber;

	@Column(name = "due_date", nullable = false)
	private LocalDate dueDate;

	@Column(nullable = false, precision = 15, scale = 2)
	private BigDecimal amount;

	@Enumerated(EnumType.STRING)
	@Column(name = "installment_status", nullable = false, length = 20)
	private InstallmentStatus installmentStatus = InstallmentStatus.SCHEDULED;

	@Column(name = "paid_at")
	private LocalDateTime paidAt;

	@Column(name = "payment_transaction_id")
	private Long paymentTransactionId;

	@PrePersist
	void onCreate() {
		if (installmentStatus == null) {
			installmentStatus = InstallmentStatus.SCHEDULED;
		}
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getInstallmentPlanId() {
		return installmentPlanId;
	}

	public void setInstallmentPlanId(Long installmentPlanId) {
		this.installmentPlanId = installmentPlanId;
	}

	public Integer getInstallmentNumber() {
		return installmentNumber;
	}

	public void setInstallmentNumber(Integer installmentNumber) {
		this.installmentNumber = installmentNumber;
	}

	public LocalDate getDueDate() {
		return dueDate;
	}

	public void setDueDate(LocalDate dueDate) {
		this.dueDate = dueDate;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public InstallmentStatus getInstallmentStatus() {
		return installmentStatus;
	}

	public void setInstallmentStatus(InstallmentStatus installmentStatus) {
		this.installmentStatus = installmentStatus;
	}

	public LocalDateTime getPaidAt() {
		return paidAt;
	}

	public void setPaidAt(LocalDateTime paidAt) {
		this.paidAt = paidAt;
	}

	public Long getPaymentTransactionId() {
		return paymentTransactionId;
	}

	public void setPaymentTransactionId(Long paymentTransactionId) {
		this.paymentTransactionId = paymentTransactionId;
	}
}
