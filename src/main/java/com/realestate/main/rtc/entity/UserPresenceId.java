package com.realestate.main.rtc.entity;

import java.io.Serializable;
import java.util.Objects;

import com.realestate.main.rtc.enums.RtcParticipantType;

public class UserPresenceId implements Serializable {

	private static final long serialVersionUID = 1L;

	private RtcParticipantType participantType;
	private Long participantId;

	public UserPresenceId() {
	}

	public UserPresenceId(RtcParticipantType participantType, Long participantId) {
		this.participantType = participantType;
		this.participantId = participantId;
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

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof UserPresenceId that)) {
			return false;
		}
		return participantType == that.participantType && Objects.equals(participantId, that.participantId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(participantType, participantId);
	}
}
