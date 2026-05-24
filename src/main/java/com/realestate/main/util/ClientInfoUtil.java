package com.realestate.main.util;

import jakarta.servlet.http.HttpServletRequest;

public final class ClientInfoUtil {

	private ClientInfoUtil() {
	}

	public static String getClientIp(HttpServletRequest request) {
		String forwarded = request.getHeader("X-Forwarded-For");
		if (forwarded != null && !forwarded.isBlank()) {
			return forwarded.split(",")[0].trim();
		}
		return request.getRemoteAddr();
	}

	public static String getUserAgent(HttpServletRequest request) {
		String ua = request.getHeader("User-Agent");
		return ua != null ? ua : "Unknown";
	}
}
