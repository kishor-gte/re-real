package com.realestate.main.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class AdminOtpVerifyRequest {

	@NotBlank(message = "Email is required")
	@Email(message = "Invalid email format")
	private String officialEmail;

	@NotBlank(message = "OTP is required")
	@Pattern(regexp = "^\\d{6}$", message = "OTP must be 6 digits")
	private String otp;

	public String getOfficialEmail() {
		return officialEmail;
	}

	public void setOfficialEmail(String officialEmail) {
		this.officialEmail = officialEmail;
	}

	public String getOtp() {
		return otp;
	}

	public void setOtp(String otp) {
		this.otp = otp;
	}
}
