package com.realestate.main.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AdminAgentRejectRequest {

	@NotBlank(message = "Rejection reason is required")
	@Size(max = 500, message = "Reason must be at most 500 characters")
	private String reason;

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}
}
