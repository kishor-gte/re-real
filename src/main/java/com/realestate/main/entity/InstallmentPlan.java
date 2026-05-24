package com.realestate.main.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.realestate.main.entity.enums.BookingStatus;

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
@Table(name = "installment_plans")
public class InstallmentPlan {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "installment_id")
	private Long id;

	@Column(name = "booking_id", nullable = false)
	private Long bookingId;

	@Column(name = "emi_months", nullable = false)
	private Integer emiMonths;

	@Column(name = "monthly_amount", nullable = false, precision = 15, scale = 2)
	private BigDecimal monthlyAmount;

	@Column(name = "down_payment", nullable = false, precision = 15, scale = 2)
	private BigDecimal downPayment;

	@Column(name = "principal_amount", nullable = false, precision = 15, scale = 2)
	private BigDecimal principalAmount;

	@Column(name = "interest_rate", nullable = false, precision = 6, scale = 2)
	private BigDecimal interestRate;

	@Column(name = "total_interest", nullable = false, precision = 15, scale = 2)
	private BigDecimal totalInterest;

	@Enumerated(EnumType.STRING)
	@Column(name = "installment_status", nullable = false, length = 20)
	private BookingStatus installmentStatus = BookingStatus.EMI_ACTIVE;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

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

	public Long getBookingId() {
		return bookingId;
	}

	public void setBookingId(Long bookingId) {
		this.bookingId = bookingId;
	}

	public Integer getEmiMonths() {
		return emiMonths;
	}

	public void setEmiMonths(Integer emiMonths) {
		this.emiMonths = emiMonths;
	}

	public BigDecimal getMonthlyAmount() {
		return monthlyAmount;
	}

	public void setMonthlyAmount(BigDecimal monthlyAmount) {
		this.monthlyAmount = monthlyAmount;
	}

	public BigDecimal getDownPayment() {
		return downPayment;
	}

	public void setDownPayment(BigDecimal downPayment) {
		this.downPayment = downPayment;
	}

	public BigDecimal getPrincipalAmount() {
		return principalAmount;
	}

	public void setPrincipalAmount(BigDecimal principalAmount) {
		this.principalAmount = principalAmount;
	}

	public BigDecimal getInterestRate() {
		return interestRate;
	}

	public void setInterestRate(BigDecimal interestRate) {
		this.interestRate = interestRate;
	}

	public BigDecimal getTotalInterest() {
		return totalInterest;
	}

	public void setTotalInterest(BigDecimal totalInterest) {
		this.totalInterest = totalInterest;
	}

	public BookingStatus getInstallmentStatus() {
		return installmentStatus;
	}

	public void setInstallmentStatus(BookingStatus installmentStatus) {
		this.installmentStatus = installmentStatus;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
}
