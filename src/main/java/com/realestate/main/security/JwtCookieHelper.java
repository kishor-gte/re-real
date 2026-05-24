package com.realestate.main.security;

import org.springframework.http.ResponseCookie;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

public final class JwtCookieHelper {

	private JwtCookieHelper() {
	}

	public static String resolveToken(HttpServletRequest request, String cookieName) {
		String authHeader = request.getHeader("Authorization");
		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			return authHeader.substring(7).trim();
		}
		if (request.getCookies() != null) {
			for (Cookie cookie : request.getCookies()) {
				if (cookieName.equals(cookie.getName()) && cookie.getValue() != null && !cookie.getValue().isBlank()) {
					return cookie.getValue();
				}
			}
		}
		return null;
	}

	public static Cookie createTokenCookie(String cookieName, String token, int maxAgeSeconds) {
		Cookie cookie = new Cookie(cookieName, token);
		cookie.setHttpOnly(true);
		cookie.setSecure(false);
		cookie.setPath("/");
		cookie.setMaxAge(maxAgeSeconds);
		return cookie;
	}

	public static Cookie clearCookie(String cookieName) {
		Cookie cookie = new Cookie(cookieName, "");
		cookie.setHttpOnly(true);
		cookie.setPath("/");
		cookie.setMaxAge(0);
		return cookie;
	}

	public static ResponseCookie clearResponseCookie(String cookieName) {
		return ResponseCookie.from(cookieName, "")
				.httpOnly(true)
				.path("/")
				.maxAge(0)
				.build();
	}
}
