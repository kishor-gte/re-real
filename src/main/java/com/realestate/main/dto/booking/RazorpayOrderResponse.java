package com.realestate.main.dto.booking;

import java.math.BigDecimal;

public class RazorpayOrderResponse {

	private Long bookingId;
	private String bookingCode;
	private String razorpayOrderId;
	private String razorpayKeyId;
	private long amountPaise;
	private BigDecimal amount;
	private String currency;
	private boolean demoMode;
	private String userName;
	private String userEmail;
	private String userMobile;
	private boolean balancePayment;

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

	public long getAmountPaise() {
		return amountPaise;
	}

	public void setAmountPaise(long amountPaise) {
		this.amountPaise = amountPaise;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public boolean isDemoMode() {
		return demoMode;
	}

	public void setDemoMode(boolean demoMode) {
		this.demoMode = demoMode;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public String getUserMobile() {
		return userMobile;
	}

	public void setUserMobile(String userMobile) {
		this.userMobile = userMobile;
	}

	public boolean isBalancePayment() {
		return balancePayment;
	}

	public void setBalancePayment(boolean balancePayment) {
		this.balancePayment = balancePayment;
	}
}
