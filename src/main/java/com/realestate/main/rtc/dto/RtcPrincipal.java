package com.realestate.main.rtc.dto;

import com.realestate.main.rtc.enums.RtcParticipantType;

public class RtcPrincipal {

	private RtcParticipantType type;
	private Long id;
	private String displayName;

	public RtcPrincipal() {
	}

	public RtcPrincipal(RtcParticipantType type, Long id, String displayName) {
		this.type = type;
		this.id = id;
		this.displayName = displayName;
	}

	public String destinationUser() {
		return type.name() + "-" + id;
	}

	public RtcParticipantType getType() {
		return type;
	}

	public void setType(RtcParticipantType type) {
		this.type = type;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
}
