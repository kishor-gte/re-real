package com.realestate.main.rtc.dto;

import java.time.LocalDateTime;

import com.realestate.main.rtc.entity.ChatMessage;
import com.realestate.main.rtc.enums.RtcParticipantType;

public class ChatMessageDto {

	private Long id;
	private Long roomId;
	private RtcParticipantType senderType;
	private Long senderId;
	private String senderName;
	private String message;
	private String attachmentUrl;
	private String attachmentType;
	private Long replyToMessageId;
	private String messageStatus;
	private LocalDateTime sentAt;
	private LocalDateTime seenAt;
	private boolean mine;

	public static ChatMessageDto from(ChatMessage m, RtcPrincipal viewer, String senderName) {
		ChatMessageDto dto = fromEntity(m, senderName);
		if (viewer != null) {
			dto.setMine(viewer.getType() == m.getSenderType() && viewer.getId().equals(m.getSenderId()));
		}
		return dto;
	}

	/** Broadcast payload — clients compute {@code mine} locally. */
	public static ChatMessageDto fromEntity(ChatMessage m, String senderName) {
		ChatMessageDto dto = new ChatMessageDto();
		dto.setId(m.getId());
		dto.setRoomId(m.getRoomId());
		dto.setSenderType(m.getSenderType());
		dto.setSenderId(m.getSenderId());
		dto.setSenderName(senderName);
		dto.setMessage(m.getMessage());
		dto.setAttachmentUrl(m.getAttachmentUrl());
		dto.setAttachmentType(m.getAttachmentType());
		dto.setReplyToMessageId(m.getReplyToMessageId());
		dto.setMessageStatus(m.getMessageStatus().name());
		dto.setSentAt(m.getSentAt());
		dto.setSeenAt(m.getSeenAt());
		dto.setMine(false);
		return dto;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getRoomId() {
		return roomId;
	}

	public void setRoomId(Long roomId) {
		this.roomId = roomId;
	}

	public RtcParticipantType getSenderType() {
		return senderType;
	}

	public void setSenderType(RtcParticipantType senderType) {
		this.senderType = senderType;
	}

	public Long getSenderId() {
		return senderId;
	}

	public void setSenderId(Long senderId) {
		this.senderId = senderId;
	}

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getAttachmentUrl() {
		return attachmentUrl;
	}

	public void setAttachmentUrl(String attachmentUrl) {
		this.attachmentUrl = attachmentUrl;
	}

	public String getAttachmentType() {
		return attachmentType;
	}

	public void setAttachmentType(String attachmentType) {
		this.attachmentType = attachmentType;
	}

	public Long getReplyToMessageId() {
		return replyToMessageId;
	}

	public void setReplyToMessageId(Long replyToMessageId) {
		this.replyToMessageId = replyToMessageId;
	}

	public String getMessageStatus() {
		return messageStatus;
	}

	public void setMessageStatus(String messageStatus) {
		this.messageStatus = messageStatus;
	}

	public LocalDateTime getSentAt() {
		return sentAt;
	}

	public void setSentAt(LocalDateTime sentAt) {
		this.sentAt = sentAt;
	}

	public LocalDateTime getSeenAt() {
		return seenAt;
	}

	public void setSeenAt(LocalDateTime seenAt) {
		this.seenAt = seenAt;
	}

	public boolean isMine() {
		return mine;
	}

	public void setMine(boolean mine) {
		this.mine = mine;
	}
}
