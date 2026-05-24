package com.realestate.main.dto.response;

/**
 * Result of OTP delivery — real email when SMTP works, or dev OTP for local fallback.
 */
public class OtpSendResult {

	private boolean emailSent;
	private String devOtp;
	private String message;

	public OtpSendResult() {
	}

	public OtpSendResult(boolean emailSent, String devOtp, String message) {
		this.emailSent = emailSent;
		this.devOtp = devOtp;
		this.message = message;
	}

	public static OtpSendResult sent(String email) {
		return new OtpSendResult(true, null, "OTP sent to " + email + ". Check your inbox and spam folder.");
	}

	public static OtpSendResult consoleFallback(String otp, String email) {
		return new OtpSendResult(false, otp,
				"Could not reach Gmail SMTP (network/firewall). Your OTP is shown on the next page.");
	}

	/** Returned when SMTP is slow/blocked — user verifies immediately; email may still arrive later. */
	public static OtpSendResult timedFallback(String otp, String email) {
		return new OtpSendResult(false, otp,
				"Email servers are slow or blocked on this network. Use the OTP below — we are still trying to send it to "
						+ email + ".");
	}

	public boolean isEmailSent() {
		return emailSent;
	}

	public void setEmailSent(boolean emailSent) {
		this.emailSent = emailSent;
	}

	public String getDevOtp() {
		return devOtp;
	}

	public void setDevOtp(String devOtp) {
		this.devOtp = devOtp;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
