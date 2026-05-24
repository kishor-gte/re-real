package com.realestate.main.dto.subscription;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class SubscriptionPaymentVerifyRequest {

	@NotNull
	private Long subscriptionId;

	@NotBlank
	private String razorpayOrderId;

	@NotBlank
	private String razorpayPaymentId;

	@NotBlank
	private String razorpaySignature;

	public Long getSubscriptionId() {
		return subscriptionId;
	}

	public void setSubscriptionId(Long subscriptionId) {
		this.subscriptionId = subscriptionId;
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
}
