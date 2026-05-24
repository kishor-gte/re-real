package com.realestate.main.dto.booking;

import java.math.BigDecimal;

import com.realestate.main.dto.response.PublicPropertyCardResponse;

public class BalancePaymentPreviewResponse {

	private Long bookingId;
	private String bookingCode;
	private String statusLabel;
	private BigDecimal totalAmount;
	private BigDecimal paidAmount;
	private BigDecimal remainingAmount;
	private PublicPropertyCardResponse property;
	private boolean razorpayConfigured;

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

	public String getStatusLabel() {
		return statusLabel;
	}

	public void setStatusLabel(String statusLabel) {
		this.statusLabel = statusLabel;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public BigDecimal getPaidAmount() {
		return paidAmount;
	}

	public void setPaidAmount(BigDecimal paidAmount) {
		this.paidAmount = paidAmount;
	}

	public BigDecimal getRemainingAmount() {
		return remainingAmount;
	}

	public void setRemainingAmount(BigDecimal remainingAmount) {
		this.remainingAmount = remainingAmount;
	}

	public PublicPropertyCardResponse getProperty() {
		return property;
	}

	public void setProperty(PublicPropertyCardResponse property) {
		this.property = property;
	}

	public boolean isRazorpayConfigured() {
		return razorpayConfigured;
	}

	public void setRazorpayConfigured(boolean razorpayConfigured) {
		this.razorpayConfigured = razorpayConfigured;
	}
}
