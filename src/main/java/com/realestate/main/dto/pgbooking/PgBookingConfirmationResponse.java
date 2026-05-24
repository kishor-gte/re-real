package com.realestate.main.dto.pgbooking;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.realestate.main.entity.enums.BookingStatus;
import com.realestate.main.entity.enums.PgOwnerApprovalStatus;

public class PgBookingConfirmationResponse {

	private Long bookingId;
	private String bookingCode;
	private BookingStatus status;
	private String statusLabel;
	private PgOwnerApprovalStatus ownerApprovalStatus;
	private String ownerApprovalLabel;
	private String pgName;
	private String pgCode;
	private String roomNumber;
	private Integer bedCount;
	private String bedNumbers;
	private BigDecimal totalAmount;
	private BigDecimal paidAmount;
	private BigDecimal remainingAmount;
	private String transactionCode;
	private LocalDateTime bookingDate;
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

	public PgOwnerApprovalStatus getOwnerApprovalStatus() {
		return ownerApprovalStatus;
	}

	public void setOwnerApprovalStatus(PgOwnerApprovalStatus ownerApprovalStatus) {
		this.ownerApprovalStatus = ownerApprovalStatus;
	}

	public String getOwnerApprovalLabel() {
		return ownerApprovalLabel;
	}

	public void setOwnerApprovalLabel(String ownerApprovalLabel) {
		this.ownerApprovalLabel = ownerApprovalLabel;
	}

	public String getPgName() {
		return pgName;
	}

	public void setPgName(String pgName) {
		this.pgName = pgName;
	}

	public String getPgCode() {
		return pgCode;
	}

	public void setPgCode(String pgCode) {
		this.pgCode = pgCode;
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

	public List<PgOccupantSummaryDto> getOccupants() {
		return occupants;
	}

	public void setOccupants(List<PgOccupantSummaryDto> occupants) {
		this.occupants = occupants;
	}
}
