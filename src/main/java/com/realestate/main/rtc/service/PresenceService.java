package com.realestate.main.rtc.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.realestate.main.rtc.dto.RtcPrincipal;
import com.realestate.main.rtc.entity.UserPresence;
import com.realestate.main.rtc.entity.UserPresenceId;
import com.realestate.main.rtc.enums.PresenceStatus;
import com.realestate.main.rtc.enums.RtcParticipantType;
import com.realestate.main.rtc.repository.UserPresenceRepository;

@Service
public class PresenceService {

	public static final String PRESENCE_TOPIC = "/topic/presence";

	private final UserPresenceRepository presenceRepository;
	private final SimpMessagingTemplate messagingTemplate;

	public PresenceService(UserPresenceRepository presenceRepository,
			@Lazy SimpMessagingTemplate messagingTemplate) {
		this.presenceRepository = presenceRepository;
		this.messagingTemplate = messagingTemplate;
	}

	@Transactional
	public void setOnline(RtcPrincipal principal) {
		saveStatus(principal, PresenceStatus.ONLINE, null);
		broadcast(principal, PresenceStatus.ONLINE);
	}

	@Transactional
	public void setOffline(RtcPrincipal principal) {
		saveStatus(principal, PresenceStatus.OFFLINE, null);
		broadcast(principal, PresenceStatus.OFFLINE);
	}

	@Transactional
	public void setTyping(RtcPrincipal principal, Long roomId, boolean typing) {
		PresenceStatus status = typing ? PresenceStatus.TYPING : PresenceStatus.ONLINE;
		saveStatus(principal, status, typing ? roomId : null);
		Map<String, Object> payload = presencePayload(principal, status);
		payload.put("roomId", roomId);
		payload.put("typing", typing);
		messagingTemplate.convertAndSend(PRESENCE_TOPIC, (Object) payload);
		if (roomId != null) {
			messagingTemplate.convertAndSend("/topic/chat.room." + roomId, (Object) payload);
		}
	}

	@Transactional
	public void setInCall(RtcPrincipal principal, boolean inCall) {
		PresenceStatus status = inCall ? PresenceStatus.IN_CALL : PresenceStatus.ONLINE;
		saveStatus(principal, status, null);
		broadcast(principal, status);
	}

	@Transactional
	public void setInCall(RtcParticipantType type, Long id, boolean inCall) {
		UserPresenceId pk = new UserPresenceId(type, id);
		UserPresence p = presenceRepository.findById(pk).orElseGet(() -> {
			UserPresence np = new UserPresence();
			np.setParticipantType(type);
			np.setParticipantId(id);
			return np;
		});
		PresenceStatus status = inCall ? PresenceStatus.IN_CALL : PresenceStatus.ONLINE;
		p.setStatus(status);
		p.setLastSeenAt(LocalDateTime.now());
		presenceRepository.save(p);
		Map<String, Object> payload = new HashMap<>();
		payload.put("participantType", type.name());
		payload.put("participantId", id);
		payload.put("status", status.name());
		messagingTemplate.convertAndSend(PRESENCE_TOPIC, (Object) payload);
	}

	@Transactional(readOnly = true)
	public PresenceStatus getStatus(RtcParticipantType type, Long id) {
		return presenceRepository.findByParticipantTypeAndParticipantId(type, id)
				.map(UserPresence::getStatus)
				.orElse(PresenceStatus.OFFLINE);
	}

	private void saveStatus(RtcPrincipal principal, PresenceStatus status, Long typingRoomId) {
		UserPresenceId pk = new UserPresenceId(principal.getType(), principal.getId());
		UserPresence p = presenceRepository.findById(pk).orElseGet(() -> {
			UserPresence np = new UserPresence();
			np.setParticipantType(principal.getType());
			np.setParticipantId(principal.getId());
			return np;
		});
		p.setStatus(status);
		p.setTypingRoomId(typingRoomId);
		p.setLastSeenAt(LocalDateTime.now());
		presenceRepository.save(p);
	}

	private void broadcast(RtcPrincipal principal, PresenceStatus status) {
		messagingTemplate.convertAndSend(PRESENCE_TOPIC, (Object) presencePayload(principal, status));
	}

	private Map<String, Object> presencePayload(RtcPrincipal principal, PresenceStatus status) {
		Map<String, Object> m = new HashMap<>();
		m.put("participantType", principal.getType().name());
		m.put("participantId", principal.getId());
		m.put("status", status.name());
		return m;
	}

	public Optional<UserPresence> find(RtcParticipantType type, Long id) {
		return presenceRepository.findByParticipantTypeAndParticipantId(type, id);
	}
}
