package com.realestate.main.dto.pgbooking;

public class PgRentPaymentVerifyRequest {

	private Long rentDueId;
	private String razorpayOrderId;
	private String razorpayPaymentId;
	private String razorpaySignature;
	private boolean demoMode;

	public Long getRentDueId() {
		return rentDueId;
	}

	public void setRentDueId(Long rentDueId) {
		this.rentDueId = rentDueId;
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
