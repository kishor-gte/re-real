package com.realestate.main.util;

import com.realestate.main.exception.AuthException;

import jakarta.servlet.http.HttpSession;

public final class AdminSessionHelper {

	private AdminSessionHelper() {
	}

	public static Long requireAdminId(HttpSession session) {
		Long adminId = (Long) session.getAttribute(AdminSessionConstants.ADMIN_ID);
		if (adminId == null) {
			throw new AuthException("Please sign in as admin");
		}
		return adminId;
	}
}
