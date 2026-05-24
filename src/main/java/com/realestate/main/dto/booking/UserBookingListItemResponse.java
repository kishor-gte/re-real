package com.realestate.main.dto.booking;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.realestate.main.entity.enums.BookingStatus;
import com.realestate.main.entity.enums.PaymentPlanType;

public class UserBookingListItemResponse {

	private Long bookingId;
	private String bookingCode;
	private BookingStatus status;
	private String statusLabel;
	private PaymentPlanType paymentPlanType;
	private Long propertyId;
	private Long agentId;
	private String agentName;
	private String propertyTitle;
	private String propertyCode;
	private String propertyImageUrl;
	private String propertyLocation;
	private BigDecimal totalAmount;
	private BigDecimal paidAmount;
	private BigDecimal remainingAmount;
	private String transactionId;
	private LocalDateTime bookingDate;
	private String invoiceDownloadUrl;
	private boolean paymentComplete;
	private boolean canPayRemaining;
	private BigDecimal nextPayableAmount;

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

	public Long getPropertyId() {
		return propertyId;
	}

	public void setPropertyId(Long propertyId) {
		this.propertyId = propertyId;
	}

	public Long getAgentId() {
		return agentId;
	}

	public void setAgentId(Long agentId) {
		this.agentId = agentId;
	}

	public String getAgentName() {
		return agentName;
	}

	public void setAgentName(String agentName) {
		this.agentName = agentName;
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

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public LocalDateTime getBookingDate() {
		return bookingDate;
	}

	public void setBookingDate(LocalDateTime bookingDate) {
		this.bookingDate = bookingDate;
	}

	public String getInvoiceDownloadUrl() {
		return invoiceDownloadUrl;
	}

	public void setInvoiceDownloadUrl(String invoiceDownloadUrl) {
		this.invoiceDownloadUrl = invoiceDownloadUrl;
	}

	public boolean isPaymentComplete() {
		return paymentComplete;
	}

	public void setPaymentComplete(boolean paymentComplete) {
		this.paymentComplete = paymentComplete;
	}

	public boolean isCanPayRemaining() {
		return canPayRemaining;
	}

	public void setCanPayRemaining(boolean canPayRemaining) {
		this.canPayRemaining = canPayRemaining;
	}

	public BigDecimal getNextPayableAmount() {
		return nextPayableAmount;
	}

	public void setNextPayableAmount(BigDecimal nextPayableAmount) {
		this.nextPayableAmount = nextPayableAmount;
	}
}
