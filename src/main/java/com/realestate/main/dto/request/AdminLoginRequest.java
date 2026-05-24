package com.realestate.main.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class AdminLoginRequest {

	@NotBlank(message = "Email is required")
	@Email(message = "Invalid email format")
	private String officialEmail;

	@NotBlank(message = "Password is required")
	private String password;

	private boolean rememberMe;

	public String getOfficialEmail() {
		return officialEmail;
	}

	public void setOfficialEmail(String officialEmail) {
		this.officialEmail = officialEmail;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isRememberMe() {
		return rememberMe;
	}

	public void setRememberMe(boolean rememberMe) {
		this.rememberMe = rememberMe;
	}
}
