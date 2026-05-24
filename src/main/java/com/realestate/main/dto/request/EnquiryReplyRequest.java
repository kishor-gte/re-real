package com.realestate.main.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class EnquiryReplyRequest {

	@NotBlank(message = "Reply message is required")
	@Size(min = 10, max = 2000, message = "Reply must be between 10 and 2000 characters")
	private String reply;

	public String getReply() {
		return reply;
	}

	public void setReply(String reply) {
		this.reply = reply;
	}
}
