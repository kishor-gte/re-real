package com.realestate.main.dto.subscription;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.realestate.main.entity.SubscriptionPayment;
import com.realestate.main.entity.enums.SubscriptionPaymentStatus;

public class SubscriptionPaymentDto {

	private Long id;
	private Long subscriptionId;
	private String planName;
	private BigDecimal amount;
	private String paymentMethod;
	private SubscriptionPaymentStatus paymentStatus;
	private String invoiceNumber;
	private LocalDateTime paymentDate;
	private String transactionId;

	public static SubscriptionPaymentDto from(SubscriptionPayment payment, String planName) {
		SubscriptionPaymentDto dto = new SubscriptionPaymentDto();
		dto.setId(payment.getId());
		dto.setSubscriptionId(payment.getSubscriptionId());
		dto.setPlanName(planName);
		dto.setAmount(payment.getAmount());
		dto.setPaymentMethod(payment.getPaymentMethod());
		dto.setPaymentStatus(payment.getPaymentStatus());
		dto.setInvoiceNumber(payment.getInvoiceNumber());
		dto.setPaymentDate(payment.getPaymentDate());
		dto.setTransactionId(payment.getTransactionId());
		return dto;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getSubscriptionId() {
		return subscriptionId;
	}

	public void setSubscriptionId(Long subscriptionId) {
		this.subscriptionId = subscriptionId;
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

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public SubscriptionPaymentStatus getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(SubscriptionPaymentStatus paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	public String getInvoiceNumber() {
		return invoiceNumber;
	}

	public void setInvoiceNumber(String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}

	public LocalDateTime getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(LocalDateTime paymentDate) {
		this.paymentDate = paymentDate;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
}
