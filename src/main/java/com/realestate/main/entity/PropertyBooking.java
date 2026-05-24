package com.realestate.main.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.realestate.main.entity.enums.BookingStatus;
import com.realestate.main.entity.enums.PaymentMethod;
import com.realestate.main.entity.enums.PaymentPlanType;

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
@Table(name = "property_bookings")
public class PropertyBooking {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "booking_id")
	private Long id;

	@Column(name = "booking_code", nullable = false, unique = true, length = 32)
	private String bookingCode;

	@Column(name = "user_id", nullable = false)
	private Long userId;

	@Column(name = "property_id", nullable = false)
	private Long propertyId;

	@Column(name = "agent_id", nullable = false)
	private Long agentId;

	@Column(name = "booking_date", nullable = false)
	private LocalDateTime bookingDate;

	@Enumerated(EnumType.STRING)
	@Column(name = "booking_status", nullable = false, length = 20)
	private BookingStatus bookingStatus = BookingStatus.PENDING;

	@Enumerated(EnumType.STRING)
	@Column(name = "payment_type", nullable = false, length = 20)
	private PaymentPlanType paymentType;

	@Enumerated(EnumType.STRING)
	@Column(name = "payment_method", length = 20)
	private PaymentMethod paymentMethod;

	@Column(name = "base_price", nullable = false, precision = 15, scale = 2)
	private BigDecimal basePrice;

	@Column(name = "gst_amount", nullable = false, precision = 15, scale = 2)
	private BigDecimal gstAmount;

	@Column(name = "registration_charges", nullable = false, precision = 15, scale = 2)
	private BigDecimal registrationCharges;

	@Column(name = "booking_charges", nullable = false, precision = 15, scale = 2)
	private BigDecimal bookingCharges;

	@Column(name = "discount_amount", nullable = false, precision = 15, scale = 2)
	private BigDecimal discountAmount = BigDecimal.ZERO;

	@Column(name = "total_amount", nullable = false, precision = 15, scale = 2)
	private BigDecimal totalAmount;

	@Column(name = "payable_now", nullable = false, precision = 15, scale = 2)
	private BigDecimal payableNow;

	@Column(name = "paid_amount", nullable = false, precision = 15, scale = 2)
	private BigDecimal paidAmount = BigDecimal.ZERO;

	@Column(name = "remaining_amount", nullable = false, precision = 15, scale = 2)
	private BigDecimal remainingAmount;

	@Column(name = "emi_months")
	private Integer emiMonths;

	@Column(name = "coupon_code", length = 40)
	private String couponCode;

	@Column(name = "razorpay_order_id", length = 64)
	private String razorpayOrderId;

	@Column(name = "transaction_id", length = 64)
	private String transactionId;

	@Column(name = "reservation_expires_at")
	private LocalDateTime reservationExpiresAt;

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
		if (discountAmount == null) {
			discountAmount = BigDecimal.ZERO;
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

	public Long getPropertyId() {
		return propertyId;
	}

	public void setPropertyId(Long propertyId) {
		this.propertyId = propertyId;
	}

	public Long getAgentId() {
		return agentId;
	}

	public void setAgentId(Long agentId) {
		this.agentId = agentId;
	}

	public LocalDateTime getBookingDate() {
		return bookingDate;
	}

	public void setBookingDate(LocalDateTime bookingDate) {
		this.bookingDate = bookingDate;
	}

	public BookingStatus getBookingStatus() {
		return bookingStatus;
	}

	public void setBookingStatus(BookingStatus bookingStatus) {
		this.bookingStatus = bookingStatus;
	}

	public PaymentPlanType getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(PaymentPlanType paymentType) {
		this.paymentType = paymentType;
	}

	public PaymentMethod getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(PaymentMethod paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public BigDecimal getBasePrice() {
		return basePrice;
	}

	public void setBasePrice(BigDecimal basePrice) {
		this.basePrice = basePrice;
	}

	public BigDecimal getGstAmount() {
		return gstAmount;
	}

	public void setGstAmount(BigDecimal gstAmount) {
		this.gstAmount = gstAmount;
	}

	public BigDecimal getRegistrationCharges() {
		return registrationCharges;
	}

	public void setRegistrationCharges(BigDecimal registrationCharges) {
		this.registrationCharges = registrationCharges;
	}

	public BigDecimal getBookingCharges() {
		return bookingCharges;
	}

	public void setBookingCharges(BigDecimal bookingCharges) {
		this.bookingCharges = bookingCharges;
	}

	public BigDecimal getDiscountAmount() {
		return discountAmount;
	}

	public void setDiscountAmount(BigDecimal discountAmount) {
		this.discountAmount = discountAmount;
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

	public Integer getEmiMonths() {
		return emiMonths;
	}

	public void setEmiMonths(Integer emiMonths) {
		this.emiMonths = emiMonths;
	}

	public String getCouponCode() {
		return couponCode;
	}

	public void setCouponCode(String couponCode) {
		this.couponCode = couponCode;
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
