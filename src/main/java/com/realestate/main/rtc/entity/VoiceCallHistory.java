package com.realestate.main.rtc.entity;

import java.time.LocalDateTime;

import com.realestate.main.rtc.enums.RtcParticipantType;
import com.realestate.main.rtc.enums.VoiceCallStatus;

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
@Table(name = "voice_call_history")
public class VoiceCallHistory {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "call_id")
	private Long id;

	@Column(name = "session_id", unique = true, length = 60)
	private String sessionId;

	@Column(name = "room_id")
	private Long roomId;

	@Enumerated(EnumType.STRING)
	@Column(name = "caller_type", nullable = false, length = 10)
	private RtcParticipantType callerType;

	@Column(name = "caller_id", nullable = false)
	private Long callerId;

	@Enumerated(EnumType.STRING)
	@Column(name = "receiver_type", nullable = false, length = 10)
	private RtcParticipantType receiverType;

	@Column(name = "receiver_id", nullable = false)
	private Long receiverId;

	@Column(name = "enquiry_id")
	private Long enquiryId;

	@Column(name = "booking_id")
	private Long bookingId;

	@Enumerated(EnumType.STRING)
	@Column(name = "call_status", nullable = false, length = 20)
	private VoiceCallStatus callStatus = VoiceCallStatus.RINGING;

	@Column(name = "call_duration")
	private Integer callDuration;

	@Column(name = "started_at")
	private LocalDateTime startedAt;

	@Column(name = "ended_at")
	private LocalDateTime endedAt;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@PrePersist
	void onCreate() {
		createdAt = LocalDateTime.now();
		if (startedAt == null) {
			startedAt = createdAt;
		}
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public Long getRoomId() {
		return roomId;
	}

	public void setRoomId(Long roomId) {
		this.roomId = roomId;
	}

	public RtcParticipantType getCallerType() {
		return callerType;
	}

	public void setCallerType(RtcParticipantType callerType) {
		this.callerType = callerType;
	}

	public Long getCallerId() {
		return callerId;
	}

	public void setCallerId(Long callerId) {
		this.callerId = callerId;
	}

	public RtcParticipantType getReceiverType() {
		return receiverType;
	}

	public void setReceiverType(RtcParticipantType receiverType) {
		this.receiverType = receiverType;
	}

	public Long getReceiverId() {
		return receiverId;
	}

	public void setReceiverId(Long receiverId) {
		this.receiverId = receiverId;
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

	public VoiceCallStatus getCallStatus() {
		return callStatus;
	}

	public void setCallStatus(VoiceCallStatus callStatus) {
		this.callStatus = callStatus;
	}

	public Integer getCallDuration() {
		return callDuration;
	}

	public void setCallDuration(Integer callDuration) {
		this.callDuration = callDuration;
	}

	public LocalDateTime getStartedAt() {
		return startedAt;
	}

	public void setStartedAt(LocalDateTime startedAt) {
		this.startedAt = startedAt;
	}

	public LocalDateTime getEndedAt() {
		return endedAt;
	}

	public void setEndedAt(LocalDateTime endedAt) {
		this.endedAt = endedAt;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
}
