package com.realestate.main.rtc.entity;

import java.time.LocalDateTime;

import com.realestate.main.rtc.enums.PresenceStatus;
import com.realestate.main.rtc.enums.RtcParticipantType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_presence")
@IdClass(UserPresenceId.class)
public class UserPresence {

	@Id
	@Enumerated(EnumType.STRING)
	@Column(name = "participant_type", length = 10)
	private RtcParticipantType participantType;

	@Id
	@Column(name = "participant_id")
	private Long participantId;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private PresenceStatus status = PresenceStatus.OFFLINE;

	@Column(name = "typing_room_id")
	private Long typingRoomId;

	@Column(name = "last_seen_at")
	private LocalDateTime lastSeenAt;

	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

	@PrePersist
	@PreUpdate
	void touch() {
		updatedAt = LocalDateTime.now();
		if (lastSeenAt == null) {
			lastSeenAt = updatedAt;
		}
	}

	public RtcParticipantType getParticipantType() {
		return participantType;
	}

	public void setParticipantType(RtcParticipantType participantType) {
		this.participantType = participantType;
	}

	public Long getParticipantId() {
		return participantId;
	}

	public void setParticipantId(Long participantId) {
		this.participantId = participantId;
	}

	public PresenceStatus getStatus() {
		return status;
	}

	public void setStatus(PresenceStatus status) {
		this.status = status;
	}

	public Long getTypingRoomId() {
		return typingRoomId;
	}

	public void setTypingRoomId(Long typingRoomId) {
		this.typingRoomId = typingRoomId;
	}

	public LocalDateTime getLastSeenAt() {
		return lastSeenAt;
	}

	public void setLastSeenAt(LocalDateTime lastSeenAt) {
		this.lastSeenAt = lastSeenAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}
}
