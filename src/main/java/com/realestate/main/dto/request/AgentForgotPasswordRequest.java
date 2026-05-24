package com.realestate.main.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class AgentForgotPasswordRequest {

	@NotBlank(message = "Email is required")
	@Email(message = "Invalid email format")
	private String email;

	@NotBlank(message = "Mobile number is required")
	@Pattern(regexp = "^\\d{10}$", message = "Enter valid 10-digit mobile number")
	private String mobile;

	private String clientOrigin;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getClientOrigin() {
		return clientOrigin;
	}

	public void setClientOrigin(String clientOrigin) {
		this.clientOrigin = clientOrigin;
	}
}
