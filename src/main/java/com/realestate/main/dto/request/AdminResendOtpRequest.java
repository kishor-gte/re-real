package com.realestate.main.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class AdminResendOtpRequest {

	@NotBlank(message = "Email is required")
	@Email(message = "Invalid email format")
	private String officialEmail;

	public String getOfficialEmail() {
		return officialEmail;
	}

	public void setOfficialEmail(String officialEmail) {
		this.officialEmail = officialEmail;
	}
}
