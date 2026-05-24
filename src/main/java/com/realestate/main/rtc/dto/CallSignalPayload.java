package com.realestate.main.rtc.dto;

public class CallSignalPayload {

	private String sessionId;
	private Long roomId;
	private Long enquiryId;
	private Long bookingId;
	private Long pgBookingId;
	private String type;
	private String sdp;
	private Object candidate;
	private String action;

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

	public Long getPgBookingId() {
		return pgBookingId;
	}

	public void setPgBookingId(Long pgBookingId) {
		this.pgBookingId = pgBookingId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSdp() {
		return sdp;
	}

	public void setSdp(String sdp) {
		this.sdp = sdp;
	}

	public Object getCandidate() {
		return candidate;
	}

	public void setCandidate(Object candidate) {
		this.candidate = candidate;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}
}
