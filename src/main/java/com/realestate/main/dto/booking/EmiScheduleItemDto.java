package com.realestate.main.dto.booking;

import java.math.BigDecimal;
import java.time.LocalDate;

public class EmiScheduleItemDto {

	private int installmentNumber;
	private LocalDate dueDate;
	private BigDecimal amount;

	public int getInstallmentNumber() {
		return installmentNumber;
	}

	public void setInstallmentNumber(int installmentNumber) {
		this.installmentNumber = installmentNumber;
	}

	public LocalDate getDueDate() {
		return dueDate;
	}

	public void setDueDate(LocalDate dueDate) {
		this.dueDate = dueDate;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
}
