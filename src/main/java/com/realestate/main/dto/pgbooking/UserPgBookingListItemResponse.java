package com.realestate.main.dto.pgbooking;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.realestate.main.entity.enums.BookingStatus;
import com.realestate.main.entity.enums.PgOwnerApprovalStatus;
import com.realestate.main.entity.enums.PgStayStatus;

public class UserPgBookingListItemResponse {

	private Long bookingId;
	private String bookingCode;
	private BookingStatus status;
	private String statusLabel;
	private PgOwnerApprovalStatus ownerApprovalStatus;
	private String ownerApprovalLabel;
	private Long pgPropertyId;
	private String pgName;
	private String pgCode;
	private String pgImageUrl;
	private String pgLocation;
	private String roomNumber;
	private String sharingType;
	private String sharingLabel;
	private Integer bedCount;
	private String bedNumbers;
	private BigDecimal rentPerBed;
	private BigDecimal totalAmount;
	private BigDecimal paidAmount;
	private BigDecimal payableNow;
	private BigDecimal remainingAmount;
	private String transactionId;
	private LocalDateTime bookingDate;
	private boolean paymentReceived;
	private boolean ownerApproved;
	private boolean ownerRejected;
	private Long pgOwnerId;
	private String pgOwnerName;
	private boolean canContactOwner;
	private boolean awaitingOwnerApproval;
	private boolean canPayRemaining;
	private boolean paymentComplete;
	private BigDecimal securityDepositAmount;
	private boolean activeStay;
	private LocalDate moveInDate;
	private LocalDate nextRentDueDate;
	private LocalDate upcomingRentDueDate;
	private BigDecimal monthlyRentAmount;
	private Long currentRentDueId;
	private BigDecimal currentRentDueAmount;
	private String currentRentDuePeriod;
	private LocalDate currentRentDueDate;
	private String currentRentDueStatus;
	private boolean canPayMonthlyRent;
	private PgStayStatus stayStatus;
	private String stayStatusLabel;
	private LocalDateTime noticeRequestedAt;
	private LocalDate plannedVacateDate;
	private boolean canRequestVacate;
	private int noticePeriodDays;

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

	public String getPgImageUrl() {
		return pgImageUrl;
	}

	public void setPgImageUrl(String pgImageUrl) {
		this.pgImageUrl = pgImageUrl;
	}

	public String getPgLocation() {
		return pgLocation;
	}

	public void setPgLocation(String pgLocation) {
		this.pgLocation = pgLocation;
	}

	public String getRoomNumber() {
		return roomNumber;
	}

	public void setRoomNumber(String roomNumber) {
		this.roomNumber = roomNumber;
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

	public BigDecimal getPaidAmount() {
		return paidAmount;
	}

	public void setPaidAmount(BigDecimal paidAmount) {
		this.paidAmount = paidAmount;
	}

	public BigDecimal getPayableNow() {
		return payableNow;
	}

	public void setPayableNow(BigDecimal payableNow) {
		this.payableNow = payableNow;
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

	public boolean isPaymentReceived() {
		return paymentReceived;
	}

	public void setPaymentReceived(boolean paymentReceived) {
		this.paymentReceived = paymentReceived;
	}

	public boolean isOwnerApproved() {
		return ownerApproved;
	}

	public void setOwnerApproved(boolean ownerApproved) {
		this.ownerApproved = ownerApproved;
	}

	public boolean isOwnerRejected() {
		return ownerRejected;
	}

	public void setOwnerRejected(boolean ownerRejected) {
		this.ownerRejected = ownerRejected;
	}

	public Long getPgOwnerId() {
		return pgOwnerId;
	}

	public void setPgOwnerId(Long pgOwnerId) {
		this.pgOwnerId = pgOwnerId;
	}

	public String getPgOwnerName() {
		return pgOwnerName;
	}

	public void setPgOwnerName(String pgOwnerName) {
		this.pgOwnerName = pgOwnerName;
	}

	public boolean isCanContactOwner() {
		return canContactOwner;
	}

	public void setCanContactOwner(boolean canContactOwner) {
		this.canContactOwner = canContactOwner;
	}

	public boolean isAwaitingOwnerApproval() {
		return awaitingOwnerApproval;
	}

	public void setAwaitingOwnerApproval(boolean awaitingOwnerApproval) {
		this.awaitingOwnerApproval = awaitingOwnerApproval;
	}

	public boolean isCanPayRemaining() {
		return canPayRemaining;
	}

	public void setCanPayRemaining(boolean canPayRemaining) {
		this.canPayRemaining = canPayRemaining;
	}

	public boolean isPaymentComplete() {
		return paymentComplete;
	}

	public void setPaymentComplete(boolean paymentComplete) {
		this.paymentComplete = paymentComplete;
	}

	public BigDecimal getSecurityDepositAmount() {
		return securityDepositAmount;
	}

	public void setSecurityDepositAmount(BigDecimal securityDepositAmount) {
		this.securityDepositAmount = securityDepositAmount;
	}

	public boolean isActiveStay() {
		return activeStay;
	}

	public void setActiveStay(boolean activeStay) {
		this.activeStay = activeStay;
	}

	public LocalDate getMoveInDate() {
		return moveInDate;
	}

	public void setMoveInDate(LocalDate moveInDate) {
		this.moveInDate = moveInDate;
	}

	public LocalDate getNextRentDueDate() {
		return nextRentDueDate;
	}

	public void setNextRentDueDate(LocalDate nextRentDueDate) {
		this.nextRentDueDate = nextRentDueDate;
	}

	public LocalDate getUpcomingRentDueDate() {
		return upcomingRentDueDate;
	}

	public void setUpcomingRentDueDate(LocalDate upcomingRentDueDate) {
		this.upcomingRentDueDate = upcomingRentDueDate;
	}

	public BigDecimal getMonthlyRentAmount() {
		return monthlyRentAmount;
	}

	public void setMonthlyRentAmount(BigDecimal monthlyRentAmount) {
		this.monthlyRentAmount = monthlyRentAmount;
	}

	public Long getCurrentRentDueId() {
		return currentRentDueId;
	}

	public void setCurrentRentDueId(Long currentRentDueId) {
		this.currentRentDueId = currentRentDueId;
	}

	public BigDecimal getCurrentRentDueAmount() {
		return currentRentDueAmount;
	}

	public void setCurrentRentDueAmount(BigDecimal currentRentDueAmount) {
		this.currentRentDueAmount = currentRentDueAmount;
	}

	public String getCurrentRentDuePeriod() {
		return currentRentDuePeriod;
	}

	public void setCurrentRentDuePeriod(String currentRentDuePeriod) {
		this.currentRentDuePeriod = currentRentDuePeriod;
	}

	public LocalDate getCurrentRentDueDate() {
		return currentRentDueDate;
	}

	public void setCurrentRentDueDate(LocalDate currentRentDueDate) {
		this.currentRentDueDate = currentRentDueDate;
	}

	public String getCurrentRentDueStatus() {
		return currentRentDueStatus;
	}

	public void setCurrentRentDueStatus(String currentRentDueStatus) {
		this.currentRentDueStatus = currentRentDueStatus;
	}

	public boolean isCanPayMonthlyRent() {
		return canPayMonthlyRent;
	}

	public void setCanPayMonthlyRent(boolean canPayMonthlyRent) {
		this.canPayMonthlyRent = canPayMonthlyRent;
	}

	public PgStayStatus getStayStatus() {
		return stayStatus;
	}

	public void setStayStatus(PgStayStatus stayStatus) {
		this.stayStatus = stayStatus;
	}

	public String getStayStatusLabel() {
		return stayStatusLabel;
	}

	public void setStayStatusLabel(String stayStatusLabel) {
		this.stayStatusLabel = stayStatusLabel;
	}

	public LocalDateTime getNoticeRequestedAt() {
		return noticeRequestedAt;
	}

	public void setNoticeRequestedAt(LocalDateTime noticeRequestedAt) {
		this.noticeRequestedAt = noticeRequestedAt;
	}

	public LocalDate getPlannedVacateDate() {
		return plannedVacateDate;
	}

	public void setPlannedVacateDate(LocalDate plannedVacateDate) {
		this.plannedVacateDate = plannedVacateDate;
	}

	public boolean isCanRequestVacate() {
		return canRequestVacate;
	}

	public void setCanRequestVacate(boolean canRequestVacate) {
		this.canRequestVacate = canRequestVacate;
	}

	public int getNoticePeriodDays() {
		return noticePeriodDays;
	}

	public void setNoticePeriodDays(int noticePeriodDays) {
		this.noticePeriodDays = noticePeriodDays;
	}
}
