package com.realestate.main.dto.pgbooking;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class PgPaymentVerifyRequest {

	@NotNull
	private Long bookingId;

	@NotBlank
	private String razorpayOrderId;

	private String razorpayPaymentId;

	private String razorpaySignature;

	private boolean demoMode;

	public Long getBookingId() {
		return bookingId;
	}

	public void setBookingId(Long bookingId) {
		this.bookingId = bookingId;
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

	public String getRazorpaySignature() {
		return razorpaySignature;
	}

	public void setRazorpaySignature(String razorpaySignature) {
		this.razorpaySignature = razorpaySignature;
	}

	public boolean isDemoMode() {
		return demoMode;
	}

	public void setDemoMode(boolean demoMode) {
		this.demoMode = demoMode;
	}
}
