package com.realestate.main.dto.pgbooking;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.realestate.main.entity.enums.BookingStatus;
import com.realestate.main.entity.enums.PgOwnerApprovalStatus;

public class PgOwnerBookingListItemResponse {

	private Long bookingId;
	private String bookingCode;
	private BookingStatus bookingStatus;
	private String bookingStatusLabel;
	private PgOwnerApprovalStatus ownerApprovalStatus;
	private String ownerApprovalLabel;
	private Long pgPropertyId;
	private String pgName;
	private String pgCode;
	private String roomNumber;
	private Integer bedCount;
	private String bedNumbers;
	private String sharingType;
	private String sharingLabel;
	private BigDecimal rentPerBed;
	private BigDecimal payableNow;
	private BigDecimal totalAmount;
	private BigDecimal securityDepositAmount;
	private boolean paymentComplete;
	private BigDecimal paidAmount;
	private BigDecimal remainingAmount;
	private LocalDateTime bookingDate;
	private Long userId;
	private String userName;
	private String userEmail;
	private String userMobile;
	private boolean awaitingOwnerAction;
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

	public BookingStatus getBookingStatus() {
		return bookingStatus;
	}

	public void setBookingStatus(BookingStatus bookingStatus) {
		this.bookingStatus = bookingStatus;
	}

	public String getBookingStatusLabel() {
		return bookingStatusLabel;
	}

	public void setBookingStatusLabel(String bookingStatusLabel) {
		this.bookingStatusLabel = bookingStatusLabel;
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

	public Long getPgPropertyId() {
		return pgPropertyId;
	}

	public void setPgPropertyId(Long pgPropertyId) {
		this.pgPropertyId = pgPropertyId;
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

	public String getSharingType() {
		return sharingType;
	}

	public void setSharingType(String sharingType) {
		this.sharingType = sharingType;
	}

	public String getSharingLabel() {
		return sharingLabel;
	}

	public void setSharingLabel(String sharingLabel) {
		this.sharingLabel = sharingLabel;
	}

	public BigDecimal getRentPerBed() {
		return rentPerBed;
	}

	public void setRentPerBed(BigDecimal rentPerBed) {
		this.rentPerBed = rentPerBed;
	}

	public BigDecimal getPayableNow() {
		return payableNow;
	}

	public void setPayableNow(BigDecimal payableNow) {
		this.payableNow = payableNow;
	}

	public BigDecimal getSecurityDepositAmount() {
		return securityDepositAmount;
	}

	public void setSecurityDepositAmount(BigDecimal securityDepositAmount) {
		this.securityDepositAmount = securityDepositAmount;
	}

	public boolean isPaymentComplete() {
		return paymentComplete;
	}

	public void setPaymentComplete(boolean paymentComplete) {
		this.paymentComplete = paymentComplete;
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

	public LocalDateTime getBookingDate() {
		return bookingDate;
	}

	public void setBookingDate(LocalDateTime bookingDate) {
		this.bookingDate = bookingDate;
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

	public boolean isAwaitingOwnerAction() {
		return awaitingOwnerAction;
	}

	public void setAwaitingOwnerAction(boolean awaitingOwnerAction) {
		this.awaitingOwnerAction = awaitingOwnerAction;
	}

	public List<PgOccupantSummaryDto> getOccupants() {
		return occupants;
	}

	public void setOccupants(List<PgOccupantSummaryDto> occupants) {
		this.occupants = occupants;
	}
}
