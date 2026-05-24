package com.realestate.main.rtc.controller;

import java.util.Map;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import com.realestate.main.exception.AuthException;
import com.realestate.main.rtc.dto.CallSignalPayload;
import com.realestate.main.rtc.dto.ChatSendPayload;
import com.realestate.main.rtc.dto.RtcPrincipal;
import com.realestate.main.rtc.enums.VoiceCallStatus;
import com.realestate.main.rtc.service.ChatService;
import com.realestate.main.rtc.service.PresenceService;
import com.realestate.main.rtc.service.VoiceCallService;

@Controller
public class RtcStompController {

	private final ChatService chatService;
	private final PresenceService presenceService;
	private final VoiceCallService voiceCallService;

	public RtcStompController(ChatService chatService, PresenceService presenceService,
			VoiceCallService voiceCallService) {
		this.chatService = chatService;
		this.presenceService = presenceService;
		this.voiceCallService = voiceCallService;
	}

	@MessageMapping("/chat.send")
	public void sendChat(@Payload ChatSendPayload payload, SimpMessageHeaderAccessor accessor) {
		chatService.send(payload, requirePrincipal(accessor));
	}

	@MessageMapping("/chat.typing")
	public void typing(@Payload Map<String, Object> payload, SimpMessageHeaderAccessor accessor) {
		RtcPrincipal me = requirePrincipal(accessor);
		Long roomId = payload.get("roomId") != null ? Long.valueOf(payload.get("roomId").toString()) : null;
		boolean typing = Boolean.TRUE.equals(payload.get("typing"));
		if (roomId != null) {
			presenceService.setTyping(me, roomId, typing);
		}
	}

	@MessageMapping("/chat.seen")
	public void seen(@Payload Map<String, Object> payload, SimpMessageHeaderAccessor accessor) {
		Long roomId = Long.valueOf(payload.get("roomId").toString());
		chatService.markSeen(roomId, requirePrincipal(accessor));
	}

	@MessageMapping("/chat.delivered")
	public void delivered(@Payload Map<String, Object> payload, SimpMessageHeaderAccessor accessor) {
		Long messageId = Long.valueOf(payload.get("messageId").toString());
		chatService.markDelivered(messageId, requirePrincipal(accessor));
	}

	@MessageMapping("/call.initiate")
	public void initiateCall(@Payload CallSignalPayload payload, SimpMessageHeaderAccessor accessor) {
		voiceCallService.initiate(payload, requirePrincipal(accessor));
	}

	@MessageMapping("/call.signal")
	public void callSignal(@Payload CallSignalPayload payload, SimpMessageHeaderAccessor accessor) {
		voiceCallService.relaySignal(payload, requirePrincipal(accessor));
	}

	@MessageMapping("/call.status")
	public void callStatus(@Payload Map<String, Object> payload, SimpMessageHeaderAccessor accessor) {
		String sessionId = String.valueOf(payload.get("sessionId"));
		String status = String.valueOf(payload.get("status"));
		voiceCallService.updateStatus(sessionId, VoiceCallStatus.valueOf(status), requirePrincipal(accessor));
	}

	private RtcPrincipal requirePrincipal(SimpMessageHeaderAccessor accessor) {
		if (accessor == null || accessor.getSessionAttributes() == null) {
			throw new AuthException("Not authenticated for real-time messaging");
		}
		Object p = accessor.getSessionAttributes().get("rtcPrincipal");
		if (p instanceof RtcPrincipal principal) {
			return principal;
		}
		throw new AuthException("Please sign in to use chat and voice calls");
	}
}
