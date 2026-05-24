package com.realestate.main.rtc.dto;

public class ChatSendPayload {

	private Long roomId;
	private String message;
	private Long replyToMessageId;

	public Long getRoomId() {
		return roomId;
	}

	public void setRoomId(Long roomId) {
		this.roomId = roomId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Long getReplyToMessageId() {
		return replyToMessageId;
	}

	public void setReplyToMessageId(Long replyToMessageId) {
		this.replyToMessageId = replyToMessageId;
	}
}
