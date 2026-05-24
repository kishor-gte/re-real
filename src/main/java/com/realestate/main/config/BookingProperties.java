package com.realestate.main.config;

import java.math.BigDecimal;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.booking")
public class BookingProperties {

	private boolean enabled = true;
	private BigDecimal gstPercent = new BigDecimal("3.33");
	private BigDecimal registrationPercent = new BigDecimal("1.11");
	private BigDecimal bookingChargesFlat = new BigDecimal("25000");
	private BigDecimal advancePercent = new BigDecimal("10");
	private BigDecimal emiDownPaymentPercent = new BigDecimal("10.58");
	private BigDecimal emiAnnualInterest = new BigDecimal("12");
	private int reservationMinutes = 30;
	private String invoiceDir = "uploads/invoices";

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public BigDecimal getGstPercent() {
		return gstPercent;
	}

	public void setGstPercent(BigDecimal gstPercent) {
		this.gstPercent = gstPercent;
	}

	public BigDecimal getRegistrationPercent() {
		return registrationPercent;
	}

	public void setRegistrationPercent(BigDecimal registrationPercent) {
		this.registrationPercent = registrationPercent;
	}

	public BigDecimal getBookingChargesFlat() {
		return bookingChargesFlat;
	}

	public void setBookingChargesFlat(BigDecimal bookingChargesFlat) {
		this.bookingChargesFlat = bookingChargesFlat;
	}

	public BigDecimal getAdvancePercent() {
		return advancePercent;
	}

	public void setAdvancePercent(BigDecimal advancePercent) {
		this.advancePercent = advancePercent;
	}

	public BigDecimal getEmiDownPaymentPercent() {
		return emiDownPaymentPercent;
	}

	public void setEmiDownPaymentPercent(BigDecimal emiDownPaymentPercent) {
		this.emiDownPaymentPercent = emiDownPaymentPercent;
	}

	public BigDecimal getEmiAnnualInterest() {
		return emiAnnualInterest;
	}

	public void setEmiAnnualInterest(BigDecimal emiAnnualInterest) {
		this.emiAnnualInterest = emiAnnualInterest;
	}

	public int getReservationMinutes() {
		return reservationMinutes;
	}

	public void setReservationMinutes(int reservationMinutes) {
		this.reservationMinutes = reservationMinutes;
	}

	public String getInvoiceDir() {
		return invoiceDir;
	}

	public void setInvoiceDir(String invoiceDir) {
		this.invoiceDir = invoiceDir;
	}
}
