package com.realestate.main.dto.booking;

import java.math.BigDecimal;

public class PriceBreakdownDto {

	private BigDecimal basePrice;
	private BigDecimal gstAmount;
	private BigDecimal registrationCharges;
	private BigDecimal bookingCharges;
	private BigDecimal discountAmount;
	private BigDecimal totalAmount;
	private BigDecimal payableNow;

	public BigDecimal getBasePrice() {
		return basePrice;
	}

	public void setBasePrice(BigDecimal basePrice) {
		this.basePrice = basePrice;
	}

	public BigDecimal getGstAmount() {
		return gstAmount;
	}

	public void setGstAmount(BigDecimal gstAmount) {
		this.gstAmount = gstAmount;
	}

	public BigDecimal getRegistrationCharges() {
		return registrationCharges;
	}

	public void setRegistrationCharges(BigDecimal registrationCharges) {
		this.registrationCharges = registrationCharges;
	}

	public BigDecimal getBookingCharges() {
		return bookingCharges;
	}

	public void setBookingCharges(BigDecimal bookingCharges) {
		this.bookingCharges = bookingCharges;
	}

	public BigDecimal getDiscountAmount() {
		return discountAmount;
	}

	public void setDiscountAmount(BigDecimal discountAmount) {
		this.discountAmount = discountAmount;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public BigDecimal getPayableNow() {
		return payableNow;
	}

	public void setPayableNow(BigDecimal payableNow) {
		this.payableNow = payableNow;
	}
}
