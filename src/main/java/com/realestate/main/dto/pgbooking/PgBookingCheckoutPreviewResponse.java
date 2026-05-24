package com.realestate.main.dto.pgbooking;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.realestate.main.dto.response.PublicPgCardResponse;

public class PgBookingCheckoutPreviewResponse {

	private Long bookingId;
	private String bookingCode;
	private PublicPgCardResponse pg;
	private String roomNumber;
	private Integer bedCount;
	private String bedNumbers;
	private BigDecimal rentPerBed;
	private BigDecimal totalAmount;
	private BigDecimal payableNow;
	private BigDecimal paidAmount;
	private BigDecimal remainingAmount;
	private int reservationMinutes;
	private boolean razorpayConfigured;
	private boolean balancePayment;
	private boolean ownerApproved;
	private BigDecimal securityDepositAmount;
	private String sharingLabel;
	private List<PgOccupantSummaryDto> occupants = new ArrayList<>();

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

	public PublicPgCardResponse getPg() {
		return pg;
	}

	public void setPg(PublicPgCardResponse pg) {
		this.pg = pg;
	}

	public String getRoomNumber() {
		return roomNumber;
	}

	public void setRoomNumber(String roomNumber) {
		this.roomNumber = roomNumber;
	}

	public Integer getBedCount() {
		return bedCount;
	}

	public void setBedCount(Integer bedCount) {
		this.bedCount = bedCount;
	}

	public String getBedNumbers() {
		return bedNumbers;
	}

	public void setBedNumbers(String bedNumbers) {
		this.bedNumbers = bedNumbers;
	}

	public BigDecimal getRentPerBed() {
		return rentPerBed;
	}

	public void setRentPerBed(BigDecimal rentPerBed) {
		this.rentPerBed = rentPerBed;
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

	public int getReservationMinutes() {
		return reservationMinutes;
	}

	public void setReservationMinutes(int reservationMinutes) {
		this.reservationMinutes = reservationMinutes;
	}

	public boolean isRazorpayConfigured() {
		return razorpayConfigured;
	}

	public void setRazorpayConfigured(boolean razorpayConfigured) {
		this.razorpayConfigured = razorpayConfigured;
	}

	public boolean isBalancePayment() {
		return balancePayment;
	}

	public void setBalancePayment(boolean balancePayment) {
		this.balancePayment = balancePayment;
	}

	public boolean isOwnerApproved() {
		return ownerApproved;
	}

	public void setOwnerApproved(boolean ownerApproved) {
		this.ownerApproved = ownerApproved;
	}

	public BigDecimal getSecurityDepositAmount() {
		return securityDepositAmount;
	}

	public void setSecurityDepositAmount(BigDecimal securityDepositAmount) {
		this.securityDepositAmount = securityDepositAmount;
	}

	public String getSharingLabel() {
		return sharingLabel;
	}

	public void setSharingLabel(String sharingLabel) {
		this.sharingLabel = sharingLabel;
	}

	public List<PgOccupantSummaryDto> getOccupants() {
		return occupants;
	}

	public void setOccupants(List<PgOccupantSummaryDto> occupants) {
		this.occupants = occupants;
	}
}
