package com.realestate.main.util;

import java.security.SecureRandom;

public final class PgOwnerIdentifiers {

	private static final SecureRandom RANDOM = new SecureRandom();
	private static final String ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";

	private PgOwnerIdentifiers() {
	}

	public static String formatPgOwnerCode(long id) {
		return String.format("PG-%05d", id);
	}

	public static String generateReferralCode() {
		StringBuilder sb = new StringBuilder(8);
		for (int i = 0; i < 8; i++) {
			sb.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
		}
		return sb.toString();
	}
}
