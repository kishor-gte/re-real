package com.realestate.main.util;

import java.security.SecureRandom;

public final class AgentIdentifiers {

	private static final SecureRandom RANDOM = new SecureRandom();
	private static final String REFERRAL_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";

	private AgentIdentifiers() {
	}

	public static String formatAgentCode(long id) {
		return String.format("AGT%06d", id);
	}

	public static String generateReferralCode() {
		StringBuilder sb = new StringBuilder("EV-");
		for (int i = 0; i < 8; i++) {
			sb.append(REFERRAL_CHARS.charAt(RANDOM.nextInt(REFERRAL_CHARS.length())));
		}
		return sb.toString();
	}
}
