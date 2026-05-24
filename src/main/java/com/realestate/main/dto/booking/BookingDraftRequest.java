package com.realestate.main.dto.booking;

import com.realestate.main.entity.enums.PaymentMethod;
import com.realestate.main.entity.enums.PaymentPlanType;

import jakarta.validation.constraints.NotNull;

public class BookingDraftRequest {

	@NotNull
	private Long propertyId;

	@NotNull
	private PaymentPlanType paymentPlanType;

	private PaymentMethod paymentMethod;

	private Integer emiMonths;

	private String couponCode;

	public Long getPropertyId() {
		return propertyId;
	}

	public void setPropertyId(Long propertyId) {
		this.propertyId = propertyId;
	}

	public PaymentPlanType getPaymentPlanType() {
		return paymentPlanType;
	}

	public void setPaymentPlanType(PaymentPlanType paymentPlanType) {
		this.paymentPlanType = paymentPlanType;
	}

	public PaymentMethod getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(PaymentMethod paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public Integer getEmiMonths() {
		return emiMonths;
	}

	public void setEmiMonths(Integer emiMonths) {
		this.emiMonths = emiMonths;
	}

	public String getCouponCode() {
		return couponCode;
	}

	public void setCouponCode(String couponCode) {
		this.couponCode = couponCode;
	}
}
