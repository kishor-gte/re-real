package com.realestate.main.rtc.service;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.realestate.main.entity.PropertyBooking;
import com.realestate.main.entity.PropertyEnquiry;
import com.realestate.main.entity.PgBooking;
import com.realestate.main.repository.AgentRepository;
import com.realestate.main.repository.PgOwnerRepository;
import com.realestate.main.repository.UserRepository;
import com.realestate.main.rtc.dto.ChatMessageDto;
import com.realestate.main.rtc.dto.ChatRoomSessionDto;
import com.realestate.main.rtc.dto.OpenChatRequest;
import com.realestate.main.rtc.dto.RtcPrincipal;
import com.realestate.main.rtc.entity.ChatMessage;
import com.realestate.main.rtc.entity.ChatRoom;
import com.realestate.main.rtc.enums.PresenceStatus;
import com.realestate.main.rtc.enums.RtcParticipantType;
import com.realestate.main.rtc.repository.ChatMessageRepository;
import com.realestate.main.rtc.repository.ChatRoomRepository;

@Service
public class ChatRoomService {

	private final ChatRoomRepository roomRepository;
	private final ChatMessageRepository messageRepository;
	private final CommunicationAccessService accessService;
	private final PresenceService presenceService;
	private final AgentRepository agentRepository;
	private final UserRepository userRepository;
	private final PgOwnerRepository pgOwnerRepository;

	@Value("${app.rtc.stun.urls:stun:stun.l.google.com:19302,stun:stun1.l.google.com:19302}")
	private String stunUrls;

	public ChatRoomService(ChatRoomRepository roomRepository, ChatMessageRepository messageRepository,
			CommunicationAccessService accessService, PresenceService presenceService,
			AgentRepository agentRepository, UserRepository userRepository, PgOwnerRepository pgOwnerRepository) {
		this.roomRepository = roomRepository;
		this.messageRepository = messageRepository;
		this.accessService = accessService;
		this.presenceService = presenceService;
		this.agentRepository = agentRepository;
		this.userRepository = userRepository;
		this.pgOwnerRepository = pgOwnerRepository;
	}

	@Transactional
	public ChatRoomSessionDto openRoom(OpenChatRequest request, RtcPrincipal me) {
		ChatRoom room;
		String propertyTitle;
		RtcParticipantType peerType;
		Long peerId;
		String peerName;

		if (request.getEnquiryId() != null) {
			var ctx = accessService.requireEnquiryAccess(request.getEnquiryId(), me);
			PropertyEnquiry e = ctx.enquiry();
			propertyTitle = ctx.propertyTitle();
			room = roomRepository.findByEnquiryId(e.getId()).orElseGet(() -> createAgentRoom(e.getAgentId(), e.getUserId(),
					e.getPropertyId(), e.getId(), null));
			peerType = me.getType() == RtcParticipantType.AGENT ? RtcParticipantType.USER : RtcParticipantType.AGENT;
			peerId = peerType == RtcParticipantType.USER ? e.getUserId() : e.getAgentId();
		} else if (request.getBookingId() != null) {
			var ctx = accessService.requireBookingAccess(request.getBookingId(), me);
			PropertyBooking b = ctx.booking();
			propertyTitle = ctx.propertyTitle();
			room = roomRepository.findByBookingId(b.getId()).orElseGet(() -> createAgentRoom(b.getAgentId(), b.getUserId(),
					b.getPropertyId(), null, b.getId()));
			peerType = me.getType() == RtcParticipantType.AGENT ? RtcParticipantType.USER : RtcParticipantType.AGENT;
			peerId = peerType == RtcParticipantType.USER ? b.getUserId() : b.getAgentId();
		} else if (request.getPgBookingId() != null) {
			var ctx = accessService.requirePgBookingAccess(request.getPgBookingId(), me);
			PgBooking b = ctx.booking();
			propertyTitle = ctx.propertyTitle();
			room = roomRepository.findByPgBookingId(b.getId()).orElseGet(() -> createPgRoom(b.getPgOwnerId(),
					b.getUserId(), b.getPgPropertyId(), b.getId()));
			peerType = me.getType() == RtcParticipantType.PG_OWNER ? RtcParticipantType.USER
					: RtcParticipantType.PG_OWNER;
			peerId = peerType == RtcParticipantType.USER ? b.getUserId() : b.getPgOwnerId();
		} else {
			throw new com.realestate.main.exception.AuthException("enquiryId, bookingId or pgBookingId is required");
		}

		peerName = resolveName(peerType, peerId);
		PresenceStatus peerPresence = presenceService.getStatus(peerType, peerId);

		ChatRoomSessionDto dto = new ChatRoomSessionDto();
		dto.setRoomId(room.getId());
		dto.setRoomCode(room.getRoomCode());
		dto.setEnquiryId(room.getEnquiryId());
		dto.setBookingId(room.getBookingId());
		dto.setPgBookingId(room.getPgBookingId());
		dto.setPropertyTitle(propertyTitle);
		dto.setPeerType(peerType);
		dto.setPeerId(peerId);
		dto.setPeerName(peerName);
		dto.setPeerPresence(peerPresence);
		dto.setMe(me);
		dto.setStunServers(Arrays.stream(stunUrls.split(",")).map(String::trim).filter(s -> !s.isEmpty()).toList());

		List<ChatMessage> messages = messageRepository.findByRoomIdAndDeletedFalseOrderBySentAtAsc(room.getId());
		dto.setMessages(messages.stream()
				.map(m -> ChatMessageDto.from(m, me, resolveName(m.getSenderType(), m.getSenderId())))
				.collect(Collectors.toList()));
		return dto;
	}

	@Transactional(readOnly = true)
	public ChatRoom requireRoomAccess(Long roomId, RtcPrincipal principal) {
		ChatRoom room = roomRepository.findById(roomId)
				.orElseThrow(() -> new com.realestate.main.exception.AuthException("Chat room not found"));
		if (room.isPgBookingRoom()) {
			if (principal.getType() == RtcParticipantType.PG_OWNER
					&& !Objects.equals(room.getPgOwnerId(), principal.getId())) {
				throw new com.realestate.main.exception.AuthException("Access denied");
			}
			if (principal.getType() == RtcParticipantType.USER
					&& !Objects.equals(room.getUserId(), principal.getId())) {
				throw new com.realestate.main.exception.AuthException("Access denied");
			}
			if (principal.getType() == RtcParticipantType.AGENT) {
				throw new com.realestate.main.exception.AuthException("Access denied");
			}
			return room;
		}
		if (principal.getType() == RtcParticipantType.AGENT
				&& !Objects.equals(room.getAgentId(), principal.getId())) {
			throw new com.realestate.main.exception.AuthException("Access denied");
		}
		if (principal.getType() == RtcParticipantType.USER
				&& !Objects.equals(room.getUserId(), principal.getId())) {
			throw new com.realestate.main.exception.AuthException("Access denied");
		}
		if (principal.getType() == RtcParticipantType.PG_OWNER) {
			throw new com.realestate.main.exception.AuthException("Access denied");
		}
		return room;
	}

	public RtcParticipantType peerTypeFor(RtcPrincipal me, ChatRoom room) {
		if (room.isPgBookingRoom()) {
			return me.getType() == RtcParticipantType.PG_OWNER ? RtcParticipantType.USER : RtcParticipantType.PG_OWNER;
		}
		return me.getType() == RtcParticipantType.AGENT ? RtcParticipantType.USER : RtcParticipantType.AGENT;
	}

	public Long peerIdFor(RtcPrincipal me, ChatRoom room) {
		if (room.isPgBookingRoom()) {
			return me.getType() == RtcParticipantType.PG_OWNER ? room.getUserId() : room.getPgOwnerId();
		}
		return me.getType() == RtcParticipantType.AGENT ? room.getUserId() : room.getAgentId();
	}

	private ChatRoom createAgentRoom(Long agentId, Long userId, Long propertyId, Long enquiryId, Long bookingId) {
		ChatRoom room = new ChatRoom();
		room.setAgentId(agentId);
		room.setUserId(userId);
		room.setPropertyId(propertyId);
		room.setEnquiryId(enquiryId);
		room.setBookingId(bookingId);
		room.setRoomCode("room-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16));
		return roomRepository.save(room);
	}

	private ChatRoom createPgRoom(Long pgOwnerId, Long userId, Long propertyId, Long pgBookingId) {
		ChatRoom room = new ChatRoom();
		room.setPgOwnerId(pgOwnerId);
		room.setUserId(userId);
		room.setPropertyId(propertyId);
		room.setPgBookingId(pgBookingId);
		room.setRoomCode("room-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16));
		return roomRepository.save(room);
	}

	private String resolveName(RtcParticipantType type, Long id) {
		if (type == RtcParticipantType.AGENT) {
			return agentRepository.findById(id).map(a -> a.getFullName()).orElse("Agent");
		}
		if (type == RtcParticipantType.PG_OWNER) {
			return pgOwnerRepository.findById(id).map(o -> o.getFullName()).orElse("PG Owner");
		}
		return userRepository.findById(id).map(u -> u.getFullName()).orElse("User");
	}

	public String roomTopic(Long roomId) {
		return "/topic/chat.room." + roomId;
	}

	public String userQueue(RtcPrincipal principal) {
		return "/queue/user." + principal.destinationUser();
	}
}
