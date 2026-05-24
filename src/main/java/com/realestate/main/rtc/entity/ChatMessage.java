package com.realestate.main.rtc.entity;

import java.time.LocalDateTime;

import com.realestate.main.rtc.enums.ChatMessageStatus;
import com.realestate.main.rtc.enums.RtcParticipantType;

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
@Table(name = "chat_messages")
public class ChatMessage {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "message_id")
	private Long id;

	@Column(name = "room_id", nullable = false)
	private Long roomId;

	@Enumerated(EnumType.STRING)
	@Column(name = "sender_type", nullable = false, length = 10)
	private RtcParticipantType senderType;

	@Column(name = "sender_id", nullable = false)
	private Long senderId;

	@Column(columnDefinition = "TEXT")
	private String message;

	@Column(name = "attachment_url", length = 500)
	private String attachmentUrl;

	@Column(name = "attachment_type", length = 40)
	private String attachmentType;

	@Column(name = "reply_to_message_id")
	private Long replyToMessageId;

	@Enumerated(EnumType.STRING)
	@Column(name = "message_status", nullable = false, length = 20)
	private ChatMessageStatus messageStatus = ChatMessageStatus.SENT;

	@Column(name = "sent_at", nullable = false)
	private LocalDateTime sentAt;

	@Column(name = "seen_at")
	private LocalDateTime seenAt;

	@Column(name = "deleted", nullable = false)
	private boolean deleted = false;

	@PrePersist
	void onCreate() {
		if (sentAt == null) {
			sentAt = LocalDateTime.now();
		}
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

	public ChatMessageStatus getMessageStatus() {
		return messageStatus;
	}

	public void setMessageStatus(ChatMessageStatus messageStatus) {
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

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
}
