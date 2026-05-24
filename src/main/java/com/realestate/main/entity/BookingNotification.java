package com.realestate.main.entity;

import java.time.LocalDateTime;

import com.realestate.main.entity.enums.BookingNotificationType;
import com.realestate.main.entity.enums.NotificationRecipientType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "booking_notifications")
public class BookingNotification {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "booking_id", nullable = false)
	private Long bookingId;

	@Enumerated(EnumType.STRING)
	@Column(name = "recipient_type", nullable = false, length = 10)
	private NotificationRecipientType recipientType;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 10)
	private BookingNotificationType channel;

	@Column(length = 200)
	private String subject;

	@Column(columnDefinition = "TEXT")
	private String body;

	@Column(name = "recipient_email", length = 150)
	private String recipientEmail;

	@Column(name = "sent_at")
	private LocalDateTime sentAt;

	@Column(nullable = false, length = 20)
	private String status = "PENDING";

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@PrePersist
	void onCreate() {
		createdAt = LocalDateTime.now();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getBookingId() {
		return bookingId;
	}

	public void setBookingId(Long bookingId) {
		this.bookingId = bookingId;
	}

	public NotificationRecipientType getRecipientType() {
		return recipientType;
	}

	public void setRecipientType(NotificationRecipientType recipientType) {
		this.recipientType = recipientType;
	}

	public BookingNotificationType getChannel() {
		return channel;
	}

	public void setChannel(BookingNotificationType channel) {
		this.channel = channel;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getRecipientEmail() {
		return recipientEmail;
	}

	public void setRecipientEmail(String recipientEmail) {
		this.recipientEmail = recipientEmail;
	}

	public LocalDateTime getSentAt() {
		return sentAt;
	}

	public void setSentAt(LocalDateTime sentAt) {
		this.sentAt = sentAt;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
}
