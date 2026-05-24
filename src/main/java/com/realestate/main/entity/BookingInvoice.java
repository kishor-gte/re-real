package com.realestate.main.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "invoices")
public class BookingInvoice {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "invoice_number", nullable = false, unique = true, length = 32)
	private String invoiceNumber;

	@Column(name = "booking_id", nullable = false)
	private Long bookingId;

	@Column(name = "file_path", length = 500)
	private String filePath;

	@Column(name = "subtotal", nullable = false, precision = 15, scale = 2)
	private BigDecimal subtotal;

	@Column(name = "gst_amount", nullable = false, precision = 15, scale = 2)
	private BigDecimal gstAmount;

	@Column(name = "total_amount", nullable = false, precision = 15, scale = 2)
	private BigDecimal totalAmount;

	@Column(name = "issued_at", nullable = false)
	private LocalDateTime issuedAt;

	@PrePersist
	void onCreate() {
		if (issuedAt == null) {
			issuedAt = LocalDateTime.now();
		}
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getInvoiceNumber() {
		return invoiceNumber;
	}

	public void setInvoiceNumber(String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}

	public Long getBookingId() {
		return bookingId;
	}

	public void setBookingId(Long bookingId) {
		this.bookingId = bookingId;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public BigDecimal getSubtotal() {
		return subtotal;
	}

	public void setSubtotal(BigDecimal subtotal) {
		this.subtotal = subtotal;
	}

	public BigDecimal getGstAmount() {
		return gstAmount;
	}

	public void setGstAmount(BigDecimal gstAmount) {
		this.gstAmount = gstAmount;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public LocalDateTime getIssuedAt() {
		return issuedAt;
	}

	public void setIssuedAt(LocalDateTime issuedAt) {
		this.issuedAt = issuedAt;
	}
}
