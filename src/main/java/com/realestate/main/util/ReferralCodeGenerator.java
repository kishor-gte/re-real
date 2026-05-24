package com.realestate.main.util;

import java.security.SecureRandom;

import org.springframework.stereotype.Component;

@Component
public class ReferralCodeGenerator {

	private static final String CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
	private static final SecureRandom RANDOM = new SecureRandom();

	public String generate(int length) {
		StringBuilder sb = new StringBuilder(length);
		for (int i = 0; i < length; i++) {
			sb.append(CHARS.charAt(RANDOM.nextInt(CHARS.length())));
		}
		return sb.toString();
	}
}
