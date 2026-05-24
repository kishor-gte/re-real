package com.realestate.main.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.realestate.main.entity.enums.BookingStatus;
import com.realestate.main.entity.enums.PaymentMethod;
import com.realestate.main.entity.enums.PgOwnerApprovalStatus;
import com.realestate.main.entity.enums.PgStayStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "pg_bookings")
public class PgBooking {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "pg_booking_id")
	private Long id;

	@Column(name = "booking_code", nullable = false, unique = true, length = 32)
	private String bookingCode;

	@Column(name = "user_id", nullable = false)
	private Long userId;

	@Column(name = "pg_property_id", nullable = false)
	private Long pgPropertyId;

	@Column(name = "pg_owner_id", nullable = false)
	private Long pgOwnerId;

	@Column(name = "room_id", nullable = false)
	private Long roomId;

	@Column(name = "room_number", nullable = false, length = 32)
	private String roomNumber;

	@Column(name = "bed_count", nullable = false)
	private Integer bedCount;

	@Column(name = "bed_numbers", length = 120)
	private String bedNumbers;

	@Enumerated(EnumType.STRING)
	@Column(name = "booking_status", nullable = false, length = 20)
	private BookingStatus bookingStatus = BookingStatus.PENDING;

	@Enumerated(EnumType.STRING)
	@Column(name = "owner_approval_status", length = 20)
	private PgOwnerApprovalStatus ownerApprovalStatus = PgOwnerApprovalStatus.PENDING;

	@Column(name = "owner_responded_at")
	private LocalDateTime ownerRespondedAt;

	@Column(name = "owner_rejection_reason", length = 500)
	private String ownerRejectionReason;

	@Enumerated(EnumType.STRING)
	@Column(name = "payment_method", length = 20)
	private PaymentMethod paymentMethod;

	@Column(name = "rent_per_bed", nullable = false, precision = 12, scale = 2)
	private BigDecimal rentPerBed;

	@Column(name = "security_deposit_amount", precision = 15, scale = 2)
	private BigDecimal securityDepositAmount = BigDecimal.ZERO;

	@Column(name = "total_amount", nullable = false, precision = 15, scale = 2)
	private BigDecimal totalAmount;

	@Column(name = "payable_now", nullable = false, precision = 15, scale = 2)
	private BigDecimal payableNow;

	@Column(name = "paid_amount", nullable = false, precision = 15, scale = 2)
	private BigDecimal paidAmount = BigDecimal.ZERO;

	@Column(name = "remaining_amount", nullable = false, precision = 15, scale = 2)
	private BigDecimal remainingAmount;

	@Column(name = "razorpay_order_id", length = 64)
	private String razorpayOrderId;

	@Column(name = "transaction_id", length = 64)
	private String transactionId;

	@Column(name = "reservation_expires_at")
	private LocalDateTime reservationExpiresAt;

	@Column(name = "booking_date", nullable = false)
	private LocalDateTime bookingDate;

	@Column(name = "move_in_date")
	private LocalDate moveInDate;

	@Enumerated(EnumType.STRING)
	@Column(name = "stay_status", length = 20)
	private PgStayStatus stayStatus;

	@Column(name = "next_rent_due_date")
	private LocalDate nextRentDueDate;

	@Column(name = "notice_requested_at")
	private LocalDateTime noticeRequestedAt;

	@Column(name = "planned_vacate_date")
	private LocalDate plannedVacateDate;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

	@PrePersist
	void onCreate() {
		LocalDateTime now = LocalDateTime.now();
		createdAt = now;
		updatedAt = now;
		if (bookingDate == null) {
			bookingDate = now;
		}
		if (bookingStatus == null) {
			bookingStatus = BookingStatus.PENDING;
		}
		if (paidAmount == null) {
			paidAmount = BigDecimal.ZERO;
		}
	}

	@PreUpdate
	void onUpdate() {
		updatedAt = LocalDateTime.now();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getBookingCode() {
		return bookingCode;
	}

	public void setBookingCode(String bookingCode) {
		this.bookingCode = bookingCode;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getPgPropertyId() {
		return pgPropertyId;
	}

	public void setPgPropertyId(Long pgPropertyId) {
		this.pgPropertyId = pgPropertyId;
	}

	public Long getPgOwnerId() {
		return pgOwnerId;
	}

	public void setPgOwnerId(Long pgOwnerId) {
		this.pgOwnerId = pgOwnerId;
	}

	public Long getRoomId() {
		return roomId;
	}

	public void setRoomId(Long roomId) {
		this.roomId = roomId;
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

	public BookingStatus getBookingStatus() {
		return bookingStatus;
	}

	public void setBookingStatus(BookingStatus bookingStatus) {
		this.bookingStatus = bookingStatus;
	}

	public PgOwnerApprovalStatus getOwnerApprovalStatus() {
		return ownerApprovalStatus;
	}

	public void setOwnerApprovalStatus(PgOwnerApprovalStatus ownerApprovalStatus) {
		this.ownerApprovalStatus = ownerApprovalStatus;
	}

	public LocalDateTime getOwnerRespondedAt() {
		return ownerRespondedAt;
	}

	public void setOwnerRespondedAt(LocalDateTime ownerRespondedAt) {
		this.ownerRespondedAt = ownerRespondedAt;
	}

	public String getOwnerRejectionReason() {
		return ownerRejectionReason;
	}

	public void setOwnerRejectionReason(String ownerRejectionReason) {
		this.ownerRejectionReason = ownerRejectionReason;
	}

	public PaymentMethod getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(PaymentMethod paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public BigDecimal getRentPerBed() {
		return rentPerBed;
	}

	public void setRentPerBed(BigDecimal rentPerBed) {
		this.rentPerBed = rentPerBed;
	}

	public BigDecimal getSecurityDepositAmount() {
		return securityDepositAmount;
	}

	public void setSecurityDepositAmount(BigDecimal securityDepositAmount) {
		this.securityDepositAmount = securityDepositAmount;
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

	public String getRazorpayOrderId() {
		return razorpayOrderId;
	}

	public void setRazorpayOrderId(String razorpayOrderId) {
		this.razorpayOrderId = razorpayOrderId;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public LocalDateTime getReservationExpiresAt() {
		return reservationExpiresAt;
	}

	public void setReservationExpiresAt(LocalDateTime reservationExpiresAt) {
		this.reservationExpiresAt = reservationExpiresAt;
	}

	public LocalDateTime getBookingDate() {
		return bookingDate;
	}

	public void setBookingDate(LocalDateTime bookingDate) {
		this.bookingDate = bookingDate;
	}

	public LocalDate getMoveInDate() {
		return moveInDate;
	}

	public void setMoveInDate(LocalDate moveInDate) {
		this.moveInDate = moveInDate;
	}

	public PgStayStatus getStayStatus() {
		return stayStatus;
	}

	public void setStayStatus(PgStayStatus stayStatus) {
		this.stayStatus = stayStatus;
	}

	public LocalDate getNextRentDueDate() {
		return nextRentDueDate;
	}

	public void setNextRentDueDate(LocalDate nextRentDueDate) {
		this.nextRentDueDate = nextRentDueDate;
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

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}
}
