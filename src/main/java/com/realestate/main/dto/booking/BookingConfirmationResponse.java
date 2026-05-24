package com.realestate.main.dto.booking;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.realestate.main.entity.enums.BookingStatus;
import com.realestate.main.entity.enums.PaymentPlanType;

public class BookingConfirmationResponse {

	private Long bookingId;
	private String bookingCode;
	private BookingStatus status;
	private String statusLabel;
	private PaymentPlanType paymentPlanType;
	private BigDecimal totalAmount;
	private BigDecimal paidAmount;
	private BigDecimal remainingAmount;
	private String propertyTitle;
	private String propertyCode;
	private String invoiceNumber;
	private String invoiceDownloadUrl;
	private String transactionCode;
	private LocalDateTime bookingDate;
	private List<EmiScheduleItemDto> emiSchedule;

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

	public String getInvoiceNumber() {
		return invoiceNumber;
	}

	public void setInvoiceNumber(String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}

	public String getInvoiceDownloadUrl() {
		return invoiceDownloadUrl;
	}

	public void setInvoiceDownloadUrl(String invoiceDownloadUrl) {
		this.invoiceDownloadUrl = invoiceDownloadUrl;
	}

	public String getTransactionCode() {
		return transactionCode;
	}

	public void setTransactionCode(String transactionCode) {
		this.transactionCode = transactionCode;
	}

	public LocalDateTime getBookingDate() {
		return bookingDate;
	}

	public void setBookingDate(LocalDateTime bookingDate) {
		this.bookingDate = bookingDate;
	}

	public List<EmiScheduleItemDto> getEmiSchedule() {
		return emiSchedule;
	}

	public void setEmiSchedule(List<EmiScheduleItemDto> emiSchedule) {
		this.emiSchedule = emiSchedule;
	}
}
