package com.realestate.main.dto.booking;

import java.math.BigDecimal;
import java.util.List;

public class EmiPlanOptionDto {

	private int months;
	private BigDecimal downPayment;
	private BigDecimal principalAmount;
	private BigDecimal monthlyEmi;
	private BigDecimal totalInterest;
	private BigDecimal payableNow;
	private List<EmiScheduleItemDto> schedule;

	public int getMonths() {
		return months;
	}

	public void setMonths(int months) {
		this.months = months;
	}

	public BigDecimal getDownPayment() {
		return downPayment;
	}

	public void setDownPayment(BigDecimal downPayment) {
		this.downPayment = downPayment;
	}

	public BigDecimal getPrincipalAmount() {
		return principalAmount;
	}

	public void setPrincipalAmount(BigDecimal principalAmount) {
		this.principalAmount = principalAmount;
	}

	public BigDecimal getMonthlyEmi() {
		return monthlyEmi;
	}

	public void setMonthlyEmi(BigDecimal monthlyEmi) {
		this.monthlyEmi = monthlyEmi;
	}

	public BigDecimal getTotalInterest() {
		return totalInterest;
	}

	public void setTotalInterest(BigDecimal totalInterest) {
		this.totalInterest = totalInterest;
	}

	public BigDecimal getPayableNow() {
		return payableNow;
	}

	public void setPayableNow(BigDecimal payableNow) {
		this.payableNow = payableNow;
	}

	public List<EmiScheduleItemDto> getSchedule() {
		return schedule;
	}

	public void setSchedule(List<EmiScheduleItemDto> schedule) {
		this.schedule = schedule;
	}
}
