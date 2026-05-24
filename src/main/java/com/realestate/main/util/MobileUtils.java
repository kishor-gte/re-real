package com.realestate.main.util;

/**
 * Normalizes Indian mobile numbers to 10 digits for consistent DB lookups.
 */
public final class MobileUtils {

	private MobileUtils() {
	}

	public static String normalize(String mobile) {
		if (mobile == null) {
			return "";
		}
		String digits = mobile.replaceAll("\\D", "");
		if (digits.length() > 10) {
			return digits.substring(digits.length() - 10);
		}
		return digits;
	}

	public static boolean isValidTenDigit(String mobile) {
		String n = normalize(mobile);
		return n.length() == 10 && n.matches("\\d{10}");
	}
}
