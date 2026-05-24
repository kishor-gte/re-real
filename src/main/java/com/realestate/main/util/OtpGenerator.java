package com.realestate.main.util;

import java.security.SecureRandom;

import org.springframework.stereotype.Component;

@Component
public class OtpGenerator {

	private static final SecureRandom RANDOM = new SecureRandom();

	public String generate() {
		return String.valueOf(100000 + RANDOM.nextInt(900000));
	}
}
