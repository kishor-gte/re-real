package com.realestate.main.rtc.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "chat_rooms")
public class ChatRoom {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "room_id")
	private Long id;

	@Column(name = "room_code", nullable = false, unique = true, length = 40)
	private String roomCode;

	@Column(name = "agent_id")
	private Long agentId;

	@Column(name = "pg_owner_id")
	private Long pgOwnerId;

	@Column(name = "user_id", nullable = false)
	private Long userId;

	@Column(name = "property_id")
	private Long propertyId;

	@Column(name = "enquiry_id")
	private Long enquiryId;

	@Column(name = "booking_id")
	private Long bookingId;

	@Column(name = "pg_booking_id")
	private Long pgBookingId;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "last_message_at")
	private LocalDateTime lastMessageAt;

	@PrePersist
	void onCreate() {
		LocalDateTime now = LocalDateTime.now();
		createdAt = now;
		lastMessageAt = now;
	}

	@PreUpdate
	void onUpdate() {
		// lastMessageAt updated explicitly
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getRoomCode() {
		return roomCode;
	}

	public void setRoomCode(String roomCode) {
		this.roomCode = roomCode;
	}

	public Long getAgentId() {
		return agentId;
	}

	public void setAgentId(Long agentId) {
		this.agentId = agentId;
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

	public Long getEnquiryId() {
		return enquiryId;
	}

	public void setEnquiryId(Long enquiryId) {
		this.enquiryId = enquiryId;
	}

	public Long getBookingId() {
		return bookingId;
	}

	public void setBookingId(Long bookingId) {
		this.bookingId = bookingId;
	}

	public Long getPgOwnerId() {
		return pgOwnerId;
	}

	public void setPgOwnerId(Long pgOwnerId) {
		this.pgOwnerId = pgOwnerId;
	}

	public Long getPgBookingId() {
		return pgBookingId;
	}

	public void setPgBookingId(Long pgBookingId) {
		this.pgBookingId = pgBookingId;
	}

	public boolean isPgBookingRoom() {
		return pgBookingId != null;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getLastMessageAt() {
		return lastMessageAt;
	}

	public void setLastMessageAt(LocalDateTime lastMessageAt) {
		this.lastMessageAt = lastMessageAt;
	}
}
