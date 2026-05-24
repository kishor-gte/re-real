package com.realestate.main.rtc.config;

import java.util.Map;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import com.realestate.main.rtc.dto.RtcPrincipal;
import com.realestate.main.rtc.service.RtcAuthService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Component
public class RtcHandshakeInterceptor implements HandshakeInterceptor {

	public static final String HTTP_SESSION = "httpSession";
	public static final String RTC_PRINCIPAL = "rtcPrincipal";

	private final RtcAuthService rtcAuthService;

	public RtcHandshakeInterceptor(RtcAuthService rtcAuthService) {
		this.rtcAuthService = rtcAuthService;
	}

	@Override
	public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
			Map<String, Object> attributes) {
		if (request instanceof ServletServerHttpRequest servletRequest) {
			HttpServletRequest httpRequest = servletRequest.getServletRequest();
			HttpSession session = httpRequest.getSession(true);
			attributes.put(HTTP_SESSION, session);
			RtcPrincipal principal = rtcAuthService.resolveForRequest(httpRequest);
			if (principal != null) {
				attributes.put(RTC_PRINCIPAL, principal);
			}
		}
		return true;
	}

	@Override
	public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
			Exception exception) {
		// no-op
	}
}
