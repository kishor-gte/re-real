package com.realestate.main.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.realestate.main.entity.enums.BookingStatus;
import com.realestate.main.entity.enums.PaymentMethod;
import com.realestate.main.entity.enums.PaymentPlanType;

public class AgentBookingDetailResponse {

	private Long bookingId;
	private String bookingCode;
	private BookingStatus status;
	private String statusLabel;
	private PaymentPlanType paymentPlanType;
	private String paymentPlanLabel;
	private Long propertyId;
	private String propertyTitle;
	private String propertyCode;
	private String propertyImageUrl;
	private String propertyLocation;
	private Long userId;
	private String userName;
	private String userEmail;
	private String userMobile;
	private BigDecimal basePrice;
	private BigDecimal gstAmount;
	private BigDecimal registrationCharges;
	private BigDecimal bookingCharges;
	private BigDecimal discountAmount;
	private BigDecimal totalAmount;
	private BigDecimal payableNow;
	private BigDecimal paidAmount;
	private BigDecimal remainingAmount;
	private PaymentMethod paymentMethod;
	private String paymentMethodLabel;
	private String transactionId;
	private Integer emiMonths;
	private BigDecimal monthlyEmi;
	private LocalDateTime bookingDate;
	private boolean paymentReceived;

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

	public BookingStatus getStatus() {
		return status;
	}

	public void setStatus(BookingStatus status) {
		this.status = status;
	}

	public String getStatusLabel() {
		return statusLabel;
	}

	public void setStatusLabel(String statusLabel) {
		this.statusLabel = statusLabel;
	}

	public PaymentPlanType getPaymentPlanType() {
		return paymentPlanType;
	}

	public void setPaymentPlanType(PaymentPlanType paymentPlanType) {
		this.paymentPlanType = paymentPlanType;
	}

	public String getPaymentPlanLabel() {
		return paymentPlanLabel;
	}

	public void setPaymentPlanLabel(String paymentPlanLabel) {
		this.paymentPlanLabel = paymentPlanLabel;
	}

	public Long getPropertyId() {
		return propertyId;
	}

	public void setPropertyId(Long propertyId) {
		this.propertyId = propertyId;
	}

	public String getPropertyTitle() {
		return propertyTitle;
	}

	public void setPropertyTitle(String propertyTitle) {
		this.propertyTitle = propertyTitle;
	}

	public String getPropertyCode() {
		return propertyCode;
	}

	public void setPropertyCode(String propertyCode) {
		this.propertyCode = propertyCode;
	}

	public String getPropertyImageUrl() {
		return propertyImageUrl;
	}

	public void setPropertyImageUrl(String propertyImageUrl) {
		this.propertyImageUrl = propertyImageUrl;
	}

	public String getPropertyLocation() {
		return propertyLocation;
	}

	public void setPropertyLocation(String propertyLocation) {
		this.propertyLocation = propertyLocation;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
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

	public PaymentMethod getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(PaymentMethod paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public String getPaymentMethodLabel() {
		return paymentMethodLabel;
	}

	public void setPaymentMethodLabel(String paymentMethodLabel) {
		this.paymentMethodLabel = paymentMethodLabel;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public Integer getEmiMonths() {
		return emiMonths;
	}

	public void setEmiMonths(Integer emiMonths) {
		this.emiMonths = emiMonths;
	}

	public BigDecimal getMonthlyEmi() {
		return monthlyEmi;
	}

	public void setMonthlyEmi(BigDecimal monthlyEmi) {
		this.monthlyEmi = monthlyEmi;
	}

	public LocalDateTime getBookingDate() {
		return bookingDate;
	}

	public void setBookingDate(LocalDateTime bookingDate) {
		this.bookingDate = bookingDate;
	}

	public boolean isPaymentReceived() {
		return paymentReceived;
	}

	public void setPaymentReceived(boolean paymentReceived) {
		this.paymentReceived = paymentReceived;
	}
}
