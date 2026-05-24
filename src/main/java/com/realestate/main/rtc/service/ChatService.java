package com.realestate.main.rtc.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.realestate.main.exception.AuthException;
import com.realestate.main.rtc.dto.ChatMessageDto;
import com.realestate.main.rtc.dto.ChatSendPayload;
import com.realestate.main.rtc.dto.ChatUnreadSummaryDto;
import com.realestate.main.rtc.dto.RtcPrincipal;
import com.realestate.main.rtc.entity.ChatMessage;
import com.realestate.main.rtc.entity.ChatRoom;
import com.realestate.main.rtc.enums.ChatMessageStatus;
import com.realestate.main.rtc.enums.RtcParticipantType;
import com.realestate.main.rtc.repository.ChatMessageRepository;
import com.realestate.main.rtc.repository.ChatRoomRepository;

@Service
public class ChatService {

	private final ChatMessageRepository messageRepository;
	private final ChatRoomRepository roomRepository;
	private final ChatRoomService chatRoomService;
	private final SimpMessagingTemplate messagingTemplate;

	public ChatService(ChatMessageRepository messageRepository, ChatRoomRepository roomRepository,
			ChatRoomService chatRoomService, SimpMessagingTemplate messagingTemplate) {
		this.messageRepository = messageRepository;
		this.roomRepository = roomRepository;
		this.chatRoomService = chatRoomService;
		this.messagingTemplate = messagingTemplate;
	}

	@Transactional
	public ChatMessageDto send(ChatSendPayload payload, RtcPrincipal sender) {
		if (payload.getRoomId() == null) {
			throw new AuthException("roomId is required");
		}
		String text = payload.getMessage() != null ? payload.getMessage().trim() : "";
		if (!StringUtils.hasText(text)) {
			throw new AuthException("Message cannot be empty");
		}
		if (text.length() > 4000) {
			throw new AuthException("Message is too long");
		}
		ChatRoom room = chatRoomService.requireRoomAccess(payload.getRoomId(), sender);

		ChatMessage msg = new ChatMessage();
		msg.setRoomId(room.getId());
		msg.setSenderType(sender.getType());
		msg.setSenderId(sender.getId());
		msg.setMessage(text);
		msg.setReplyToMessageId(payload.getReplyToMessageId());
		msg.setMessageStatus(ChatMessageStatus.SENT);
		msg.setSentAt(LocalDateTime.now());
		msg = messageRepository.save(msg);

		room.setLastMessageAt(msg.getSentAt());
		roomRepository.save(room);

		String senderName = sender.getDisplayName() != null ? sender.getDisplayName() : "User";
		ChatMessageDto dto = ChatMessageDto.fromEntity(msg, senderName);
		messagingTemplate.convertAndSend(chatRoomService.roomTopic(room.getId()), dto);
		notifyPeerNewMessage(room, sender, dto);
		return dto;
	}

	@Transactional
	public ChatMessageDto sendWithAttachment(Long roomId, RtcPrincipal sender, String message, String attachmentUrl,
			String attachmentType, Long replyToId) {
		ChatRoom room = chatRoomService.requireRoomAccess(roomId, sender);
		ChatMessage msg = new ChatMessage();
		msg.setRoomId(room.getId());
		msg.setSenderType(sender.getType());
		msg.setSenderId(sender.getId());
		msg.setMessage(StringUtils.hasText(message) ? message.trim() : "[Attachment]");
		msg.setAttachmentUrl(attachmentUrl);
		msg.setAttachmentType(attachmentType);
		msg.setReplyToMessageId(replyToId);
		msg.setMessageStatus(ChatMessageStatus.SENT);
		msg.setSentAt(LocalDateTime.now());
		msg = messageRepository.save(msg);
		room.setLastMessageAt(msg.getSentAt());
		roomRepository.save(room);
		String senderName = sender.getDisplayName() != null ? sender.getDisplayName() : "User";
		ChatMessageDto dto = ChatMessageDto.fromEntity(msg, senderName);
		messagingTemplate.convertAndSend(chatRoomService.roomTopic(room.getId()), dto);
		notifyPeerNewMessage(room, sender, dto);
		return dto;
	}

	@Transactional
	public void markDelivered(Long messageId, RtcPrincipal reader) {
		ChatMessage msg = messageRepository.findById(messageId).orElseThrow(() -> new AuthException("Message not found"));
		chatRoomService.requireRoomAccess(msg.getRoomId(), reader);
		if (msg.getSenderType() == reader.getType() && msg.getSenderId().equals(reader.getId())) {
			return;
		}
		if (msg.getMessageStatus() == ChatMessageStatus.SENT) {
			msg.setMessageStatus(ChatMessageStatus.DELIVERED);
			messageRepository.save(msg);
			broadcastStatus(msg);
		}
	}

	@Transactional
	public void markSeen(Long roomId, RtcPrincipal reader) {
		chatRoomService.requireRoomAccess(roomId, reader);
		messageRepository.markSeenInRoom(roomId, reader.getType(), ChatMessageStatus.SEEN);
		Map<String, Object> payload = new HashMap<>();
		payload.put("type", "SEEN");
		payload.put("roomId", roomId);
		payload.put("readerType", reader.getType().name());
		payload.put("readerId", reader.getId());
		messagingTemplate.convertAndSend(chatRoomService.roomTopic(roomId), (Object) payload);
	}

	@Transactional
	public void deleteMessage(Long messageId, RtcPrincipal principal) {
		ChatMessage msg = messageRepository.findById(messageId).orElseThrow(() -> new AuthException("Message not found"));
		if (msg.getSenderType() != principal.getType() || !msg.getSenderId().equals(principal.getId())) {
			throw new AuthException("You can only delete your own messages");
		}
		msg.setDeleted(true);
		messageRepository.save(msg);
		Map<String, Object> payload = new HashMap<>();
		payload.put("type", "DELETED");
		payload.put("messageId", messageId);
		payload.put("roomId", msg.getRoomId());
		messagingTemplate.convertAndSend(chatRoomService.roomTopic(msg.getRoomId()), (Object) payload);
	}

	@Transactional(readOnly = true)
	public long unreadCount(Long roomId, RtcPrincipal principal) {
		chatRoomService.requireRoomAccess(roomId, principal);
		return messageRepository.countByRoomIdAndSenderTypeNotAndMessageStatusNotAndDeletedFalse(roomId,
				principal.getType(), ChatMessageStatus.SEEN);
	}

	@Transactional(readOnly = true)
	public ChatUnreadSummaryDto unreadSummary(RtcPrincipal principal) {
		List<Object[]> rows = switch (principal.getType()) {
		case AGENT -> messageRepository.unreadGroupedForAgent(principal.getId(), principal.getType(),
				ChatMessageStatus.SEEN);
		case USER -> messageRepository.unreadGroupedForUser(principal.getId(), principal.getType(),
				ChatMessageStatus.SEEN);
		case PG_OWNER -> messageRepository.unreadGroupedForPgOwner(principal.getId(), principal.getType(),
				ChatMessageStatus.SEEN);
		};
		ChatUnreadSummaryDto summary = new ChatUnreadSummaryDto();
		long total = 0;
		for (Object[] row : rows) {
			long count = ((Number) row[row.length - 1]).longValue();
			total += count;
			Long enquiryId = (Long) row[1];
			Long bookingId = (Long) row[2];
			Long pgBookingId = row.length >= 5 ? (Long) row[3] : null;
			if (enquiryId != null) {
				String key = String.valueOf(enquiryId);
				summary.getEnquiries().merge(key, count, Long::sum);
			}
			if (bookingId != null) {
				String key = String.valueOf(bookingId);
				summary.getBookings().merge(key, count, Long::sum);
			}
			if (pgBookingId != null) {
				String key = String.valueOf(pgBookingId);
				summary.getPgBookings().merge(key, count, Long::sum);
			}
		}
		summary.setTotal(total);
		return summary;
	}

	private void notifyPeerNewMessage(ChatRoom room, RtcPrincipal sender, ChatMessageDto dto) {
		RtcParticipantType peerType = chatRoomService.peerTypeFor(sender, room);
		Long peerId = chatRoomService.peerIdFor(sender, room);
		RtcPrincipal peer = new RtcPrincipal(peerType, peerId, null);
		long roomUnread = unreadCount(room.getId(), peer);
		ChatUnreadSummaryDto peerSummary = unreadSummary(peer);

		Map<String, Object> notify = new HashMap<>();
		notify.put("type", "NEW_MESSAGE");
		notify.put("roomId", room.getId());
		notify.put("enquiryId", room.getEnquiryId());
		notify.put("bookingId", room.getBookingId());
		notify.put("pgBookingId", room.getPgBookingId());
		notify.put("roomUnread", roomUnread);
		notify.put("totalUnread", peerSummary.getTotal());
		notify.put("unreadSummary", peerSummary);
		notify.put("message", dto);
		messagingTemplate.convertAndSendToUser(peerType.name() + "-" + peerId, "/queue/notifications", (Object) notify);
	}

	private void broadcastStatus(ChatMessage msg) {
		Map<String, Object> payload = new HashMap<>();
		payload.put("type", "STATUS");
		payload.put("messageId", msg.getId());
		payload.put("roomId", msg.getRoomId());
		payload.put("status", msg.getMessageStatus().name());
		messagingTemplate.convertAndSend(chatRoomService.roomTopic(msg.getRoomId()), (Object) payload);
	}
}
