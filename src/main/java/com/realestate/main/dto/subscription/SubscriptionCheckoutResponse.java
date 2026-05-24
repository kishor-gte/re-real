package com.realestate.main.dto.subscription;

import java.math.BigDecimal;

public class SubscriptionCheckoutResponse {

	private Long subscriptionId;
	private Long planId;
	private String planName;
	private BigDecimal amount;
	private String razorpayOrderId;
	private String razorpayKeyId;
	private boolean demoMode;

	public Long getSubscriptionId() {
		return subscriptionId;
	}

	public void setSubscriptionId(Long subscriptionId) {
		this.subscriptionId = subscriptionId;
	}

	public Long getPlanId() {
		return planId;
	}

	public void setPlanId(Long planId) {
		this.planId = planId;
	}

	public String getPlanName() {
		return planName;
	}

	public void setPlanName(String planName) {
		this.planName = planName;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getRazorpayOrderId() {
		return razorpayOrderId;
	}

	public void setRazorpayOrderId(String razorpayOrderId) {
		this.razorpayOrderId = razorpayOrderId;
	}

	public String getRazorpayKeyId() {
		return razorpayKeyId;
	}

	public void setRazorpayKeyId(String razorpayKeyId) {
		this.razorpayKeyId = razorpayKeyId;
	}

	public boolean isDemoMode() {
		return demoMode;
	}

	public void setDemoMode(boolean demoMode) {
		this.demoMode = demoMode;
	}
}
