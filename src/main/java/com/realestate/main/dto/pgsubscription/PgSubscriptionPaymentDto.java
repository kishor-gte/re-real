package com.realestate.main.dto.pgsubscription;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.realestate.main.entity.PgSubscriptionPayment;
import com.realestate.main.entity.enums.SubscriptionPaymentStatus;

public class PgSubscriptionPaymentDto {

	private Long id;
	private Long subscriptionId;
	private String planName;
	private BigDecimal amount;
	private String transactionId;
	private String invoiceNumber;
	private SubscriptionPaymentStatus paymentStatus;
	private String paymentMethod;
	private LocalDateTime paymentDate;

	public static PgSubscriptionPaymentDto from(PgSubscriptionPayment payment, String planName) {
		PgSubscriptionPaymentDto dto = new PgSubscriptionPaymentDto();
		dto.setId(payment.getId());
		dto.setSubscriptionId(payment.getSubscriptionId());
		dto.setPlanName(planName);
		dto.setAmount(payment.getAmount());
		dto.setTransactionId(payment.getTransactionId());
		dto.setInvoiceNumber(payment.getInvoiceNumber());
		dto.setPaymentStatus(payment.getPaymentStatus());
		dto.setPaymentMethod(payment.getPaymentMethod());
		dto.setPaymentDate(payment.getPaymentDate());
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

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public String getInvoiceNumber() {
		return invoiceNumber;
	}

	public void setInvoiceNumber(String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}

	public SubscriptionPaymentStatus getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(SubscriptionPaymentStatus paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public LocalDateTime getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(LocalDateTime paymentDate) {
		this.paymentDate = paymentDate;
	}
}
