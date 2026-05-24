package com.realestate.main.dto.pgbooking;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.realestate.main.entity.enums.PgRentDueStatus;
import com.realestate.main.entity.enums.PgStayStatus;

public class PgMonthlyRentDueResponse {

	private Long rentDueId;
	private Long bookingId;
	private LocalDate dueDate;
	private String periodLabel;
	private BigDecimal amount;
	private PgRentDueStatus dueStatus;
	private String dueStatusLabel;
	private boolean overdue;

	public Long getRentDueId() {
		return rentDueId;
	}

	public void setRentDueId(Long rentDueId) {
		this.rentDueId = rentDueId;
	}

	public Long getBookingId() {
		return bookingId;
	}

	public void setBookingId(Long bookingId) {
		this.bookingId = bookingId;
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

	public String getDueStatusLabel() {
		return dueStatusLabel;
	}

	public void setDueStatusLabel(String dueStatusLabel) {
		this.dueStatusLabel = dueStatusLabel;
	}

	public boolean isOverdue() {
		return overdue;
	}

	public void setOverdue(boolean overdue) {
		this.overdue = overdue;
	}
}
