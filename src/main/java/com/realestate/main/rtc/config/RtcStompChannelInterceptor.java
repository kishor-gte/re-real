package com.realestate.main.rtc.config;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import com.realestate.main.exception.AuthException;
import com.realestate.main.rtc.dto.RtcPrincipal;
import com.realestate.main.rtc.service.PresenceService;
import com.realestate.main.rtc.service.RtcAuthService;

import jakarta.servlet.http.HttpSession;

@Component
public class RtcStompChannelInterceptor implements ChannelInterceptor {

	private final RtcAuthService rtcAuthService;
	private final PresenceService presenceService;

	public RtcStompChannelInterceptor(RtcAuthService rtcAuthService, PresenceService presenceService) {
		this.rtcAuthService = rtcAuthService;
		this.presenceService = presenceService;
	}

	@Override
	public Message<?> preSend(Message<?> message, MessageChannel channel) {
		StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
		if (accessor == null) {
			return message;
		}
		if (StompCommand.CONNECT.equals(accessor.getCommand())) {
			Map<String, Object> attrs = accessor.getSessionAttributes();
			if (attrs == null) {
				throw new AuthException("WebSocket session unavailable");
			}
			HttpSession httpSession = (HttpSession) attrs.get(RtcHandshakeInterceptor.HTTP_SESSION);
			RtcPrincipal principal = (RtcPrincipal) attrs.get(RtcHandshakeInterceptor.RTC_PRINCIPAL);
			if (principal == null) {
				principal = rtcAuthService.resolvePrincipal(httpSession);
			}
			if (principal == null) {
				List<String> authHeaders = accessor.getNativeHeader("Authorization");
				if (authHeaders != null && !authHeaders.isEmpty()) {
					String header = authHeaders.get(0);
					if (header != null && header.startsWith("Bearer ")) {
						principal = rtcAuthService.resolveFromBearer(header.substring(7).trim(), httpSession);
					}
				}
			}
			if (principal == null) {
				throw new AuthException("Please sign in to connect real-time chat");
			}
			accessor.setUser(principal::destinationUser);
			attrs.put("rtcPrincipal", principal);
			attrs.put(RtcHandshakeInterceptor.RTC_PRINCIPAL, principal);
			presenceService.setOnline(principal);
		} else if (StompCommand.SEND.equals(accessor.getCommand()) || StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
			ensureRtcPrincipal(accessor);
		} else if (StompCommand.DISCONNECT.equals(accessor.getCommand())) {
			Object p = accessor.getSessionAttributes() != null ? accessor.getSessionAttributes().get("rtcPrincipal")
					: null;
			if (p instanceof RtcPrincipal principal) {
				presenceService.setOffline(principal);
			}
		}
		return message;
	}

	private void ensureRtcPrincipal(StompHeaderAccessor accessor) {
		Map<String, Object> attrs = accessor.getSessionAttributes();
		if (attrs == null) {
			return;
		}
		if (attrs.get("rtcPrincipal") instanceof RtcPrincipal existing) {
			if (accessor.getUser() == null) {
				accessor.setUser(existing::destinationUser);
			}
			return;
		}
		Object stored = attrs.get(RtcHandshakeInterceptor.RTC_PRINCIPAL);
		if (stored instanceof RtcPrincipal principal) {
			attrs.put("rtcPrincipal", principal);
			accessor.setUser(principal::destinationUser);
		}
	}
}
