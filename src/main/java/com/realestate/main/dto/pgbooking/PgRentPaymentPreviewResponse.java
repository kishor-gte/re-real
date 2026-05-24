package com.realestate.main.dto.pgbooking;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PgRentPaymentPreviewResponse {

	private Long rentDueId;
	private Long bookingId;
	private String bookingCode;
	private String pgName;
	private String pgLocation;
	private String roomNumber;
	private String periodLabel;
	private LocalDate dueDate;
	private BigDecimal amount;
	private BigDecimal monthlyRentAmount;
	private boolean razorpayConfigured;

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

	public String getBookingCode() {
		return bookingCode;
	}

	public void setBookingCode(String bookingCode) {
		this.bookingCode = bookingCode;
	}

	public String getPgName() {
		return pgName;
	}

	public void setPgName(String pgName) {
		this.pgName = pgName;
	}

	public String getPgLocation() {
		return pgLocation;
	}

	public void setPgLocation(String pgLocation) {
		this.pgLocation = pgLocation;
	}

	public String getRoomNumber() {
		return roomNumber;
	}

	public void setRoomNumber(String roomNumber) {
		this.roomNumber = roomNumber;
	}

	public String getPeriodLabel() {
		return periodLabel;
	}

	public void setPeriodLabel(String periodLabel) {
		this.periodLabel = periodLabel;
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

	public BigDecimal getMonthlyRentAmount() {
		return monthlyRentAmount;
	}

	public void setMonthlyRentAmount(BigDecimal monthlyRentAmount) {
		this.monthlyRentAmount = monthlyRentAmount;
	}

	public boolean isRazorpayConfigured() {
		return razorpayConfigured;
	}

	public void setRazorpayConfigured(boolean razorpayConfigured) {
		this.razorpayConfigured = razorpayConfigured;
	}
}
