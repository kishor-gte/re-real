package com.realestate.main.rtc.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.realestate.main.exception.AuthException;
import com.realestate.main.rtc.dto.CallSignalPayload;
import com.realestate.main.rtc.dto.RtcPrincipal;
import com.realestate.main.rtc.entity.ChatRoom;
import com.realestate.main.rtc.entity.VoiceCallHistory;
import com.realestate.main.rtc.enums.PresenceStatus;
import com.realestate.main.rtc.enums.RtcParticipantType;
import com.realestate.main.rtc.enums.VoiceCallStatus;
import com.realestate.main.rtc.repository.VoiceCallHistoryRepository;

@Service
public class VoiceCallService {

	private final VoiceCallHistoryRepository callRepository;
	private final ChatRoomService chatRoomService;
	private final PresenceService presenceService;
	private final SimpMessagingTemplate messagingTemplate;

	public VoiceCallService(VoiceCallHistoryRepository callRepository, ChatRoomService chatRoomService,
			PresenceService presenceService, SimpMessagingTemplate messagingTemplate) {
		this.callRepository = callRepository;
		this.chatRoomService = chatRoomService;
		this.presenceService = presenceService;
		this.messagingTemplate = messagingTemplate;
	}

	@Transactional
	public Map<String, Object> initiate(CallSignalPayload payload, RtcPrincipal caller) {
		ChatRoom room = resolveRoom(payload, caller);
		RtcParticipantType receiverType = chatRoomService.peerTypeFor(caller, room);
		Long receiverId = chatRoomService.peerIdFor(caller, room);

		PresenceStatus peerStatus = presenceService.getStatus(receiverType, receiverId);
		if (peerStatus == PresenceStatus.IN_CALL) {
			throw new AuthException("User is busy on another call");
		}
		if (callRepository.existsByReceiverTypeAndReceiverIdAndCallStatus(receiverType, receiverId,
				VoiceCallStatus.RINGING)) {
			throw new AuthException("User already has an incoming call");
		}

		String sessionId = payload.getSessionId();
		if (sessionId == null || sessionId.isBlank()) {
			sessionId = "call-" + UUID.randomUUID().toString().replace("-", "");
		}

		VoiceCallHistory call = new VoiceCallHistory();
		call.setSessionId(sessionId);
		call.setRoomId(room.getId());
		call.setCallerType(caller.getType());
		call.setCallerId(caller.getId());
		call.setReceiverType(receiverType);
		call.setReceiverId(receiverId);
		call.setEnquiryId(room.getEnquiryId());
		call.setBookingId(room.getBookingId());
		call.setCallStatus(VoiceCallStatus.RINGING);
		call.setStartedAt(LocalDateTime.now());
		callRepository.save(call);

		Map<String, Object> signal = callSignal("INCOMING", call, caller);
		signal.put("callerName", caller.getDisplayName());
		signal.put("peerOnline", peerStatus == PresenceStatus.ONLINE || peerStatus == PresenceStatus.TYPING);
		String receiverDest = receiverType.name() + "-" + receiverId;
		messagingTemplate.convertAndSendToUser(receiverDest, "/queue/call", (Object) signal);
		messagingTemplate.convertAndSend(RtcAuthService.callTopicFor(receiverType, receiverId), (Object) signal);
		return signal;
	}

	@Transactional
	public void updateStatus(String sessionId, VoiceCallStatus status, RtcPrincipal actor) {
		VoiceCallHistory call = callRepository.findBySessionId(sessionId)
				.orElseThrow(() -> new AuthException("Call not found"));
		assertCallParticipant(call, actor);

		call.setCallStatus(status);
		if (status == VoiceCallStatus.COMPLETED || status == VoiceCallStatus.MISSED
				|| status == VoiceCallStatus.REJECTED || status == VoiceCallStatus.CANCELLED) {
			call.setEndedAt(LocalDateTime.now());
			if (call.getStartedAt() != null && call.getEndedAt() != null) {
				int seconds = (int) Duration.between(call.getStartedAt(), call.getEndedAt()).getSeconds();
				call.setCallDuration(Math.max(seconds, 0));
			}
			presenceService.setInCall(actor, false);
		}
		if (status == VoiceCallStatus.ACCEPTED) {
			presenceService.setInCall(call.getCallerType(), call.getCallerId(), true);
			presenceService.setInCall(call.getReceiverType(), call.getReceiverId(), true);
		}
		callRepository.save(call);
		broadcastCallEvent(call, status.name(), actor);
	}

	@Transactional
	public void relaySignal(CallSignalPayload payload, RtcPrincipal sender) {
		VoiceCallHistory call = callRepository.findBySessionId(payload.getSessionId())
				.orElseThrow(() -> new AuthException("Call not found"));
		assertCallParticipant(call, sender);

		RtcParticipantType targetType = sender.getType().equals(call.getCallerType()) ? call.getReceiverType()
				: call.getCallerType();
		Long targetId = sender.getType().equals(call.getCallerType()) ? call.getReceiverId() : call.getCallerId();

		Map<String, Object> signal = new HashMap<>();
		signal.put("sessionId", payload.getSessionId());
		signal.put("roomId", call.getRoomId());
		signal.put("type", payload.getType());
		signal.put("sdp", payload.getSdp());
		signal.put("candidate", payload.getCandidate());
		signal.put("action", payload.getAction());
		signal.put("fromType", sender.getType().name());
		signal.put("fromId", sender.getId());
		String targetDest = targetType.name() + "-" + targetId;
		messagingTemplate.convertAndSendToUser(targetDest, "/queue/call", (Object) signal);
		messagingTemplate.convertAndSend(RtcAuthService.callTopicFor(targetType, targetId), (Object) signal);
	}

	@Transactional(readOnly = true)
	public List<Map<String, Object>> historyForPrincipal(RtcPrincipal principal) {
		return callRepository
				.findTop50ByCallerTypeAndCallerIdOrReceiverTypeAndReceiverIdOrderByCreatedAtDesc(principal.getType(),
						principal.getId(), principal.getType(), principal.getId())
				.stream()
				.map(this::toHistoryMap)
				.collect(Collectors.toList());
	}

	private ChatRoom resolveRoom(CallSignalPayload payload, RtcPrincipal caller) {
		if (payload.getRoomId() != null) {
			return chatRoomService.requireRoomAccess(payload.getRoomId(), caller);
		}
		com.realestate.main.rtc.dto.OpenChatRequest req = new com.realestate.main.rtc.dto.OpenChatRequest();
		req.setEnquiryId(payload.getEnquiryId());
		req.setBookingId(payload.getBookingId());
		req.setPgBookingId(payload.getPgBookingId());
		if (req.getEnquiryId() == null && req.getBookingId() == null && req.getPgBookingId() == null) {
			throw new AuthException("roomId, enquiryId, bookingId or pgBookingId required");
		}
		Long roomId = chatRoomService.openRoom(req, caller).getRoomId();
		return chatRoomService.requireRoomAccess(roomId, caller);
	}

	private void assertCallParticipant(VoiceCallHistory call, RtcPrincipal p) {
		boolean ok = (call.getCallerType() == p.getType() && call.getCallerId().equals(p.getId()))
				|| (call.getReceiverType() == p.getType() && call.getReceiverId().equals(p.getId()));
		if (!ok) {
			throw new AuthException("Not a participant in this call");
		}
	}

	private Map<String, Object> callSignal(String type, VoiceCallHistory call, RtcPrincipal caller) {
		Map<String, Object> m = new HashMap<>();
		m.put("type", type);
		m.put("sessionId", call.getSessionId());
		m.put("roomId", call.getRoomId());
		m.put("callerType", call.getCallerType().name());
		m.put("callerId", call.getCallerId());
		m.put("receiverType", call.getReceiverType().name());
		m.put("receiverId", call.getReceiverId());
		m.put("status", call.getCallStatus().name());
		return m;
	}

	private void broadcastCallEvent(VoiceCallHistory call, String event, RtcPrincipal actor) {
		Map<String, Object> m = callSignal(event, call, actor);
		sendCallSignal(call.getCallerType(), call.getCallerId(), m);
		sendCallSignal(call.getReceiverType(), call.getReceiverId(), m);
	}

	private void sendCallSignal(RtcParticipantType type, Long id, Map<String, Object> signal) {
		String dest = type.name() + "-" + id;
		messagingTemplate.convertAndSendToUser(dest, "/queue/call", (Object) signal);
		messagingTemplate.convertAndSend(RtcAuthService.callTopicFor(type, id), (Object) signal);
	}

	private Map<String, Object> toHistoryMap(VoiceCallHistory c) {
		Map<String, Object> m = new HashMap<>();
		m.put("callId", c.getId());
		m.put("sessionId", c.getSessionId());
		m.put("roomId", c.getRoomId());
		m.put("status", c.getCallStatus().name());
		m.put("duration", c.getCallDuration());
		m.put("startedAt", c.getStartedAt());
		m.put("endedAt", c.getEndedAt());
		m.put("callerType", c.getCallerType().name());
		m.put("callerId", c.getCallerId());
		m.put("receiverType", c.getReceiverType().name());
		m.put("receiverId", c.getReceiverId());
		return m;
	}
}
