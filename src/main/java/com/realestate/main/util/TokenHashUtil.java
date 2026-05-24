package com.realestate.main.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public final class TokenHashUtil {

	private static final SecureRandom SECURE_RANDOM = new SecureRandom();

	private TokenHashUtil() {
	}

	public static String generateSecureToken() {
		byte[] bytes = new byte[32];
		SECURE_RANDOM.nextBytes(bytes);
		return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
	}

	public static String hashToken(String rawToken) {
		if (rawToken == null || rawToken.isBlank()) {
			throw new IllegalArgumentException("Token is empty");
		}
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(rawToken.trim().getBytes(StandardCharsets.UTF_8));
			StringBuilder sb = new StringBuilder(hash.length * 2);
			for (byte b : hash) {
				sb.append(String.format("%02x", b));
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException("SHA-256 not available", e);
		}
	}
}
