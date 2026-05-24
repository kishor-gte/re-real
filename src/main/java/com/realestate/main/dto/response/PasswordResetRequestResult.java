package com.realestate.main.dto.response;

public class PasswordResetRequestResult {

	private final boolean emailSent;
	private final String message;
	private final String resetToken;
	private final String resetUrl;

	public PasswordResetRequestResult(boolean emailSent, String message, String resetToken, String resetUrl) {
		this.emailSent = emailSent;
		this.message = message;
		this.resetToken = resetToken;
		this.resetUrl = resetUrl;
	}

	public boolean isEmailSent() {
		return emailSent;
	}

	public String getMessage() {
		return message;
	}

	public String getResetToken() {
		return resetToken;
	}

	public String getResetUrl() {
		return resetUrl;
	}
}
