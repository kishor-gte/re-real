package com.realestate.main.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class EnquiryCreateRequest {

	@NotNull(message = "Property is required")
	private Long propertyId;

	@NotBlank(message = "Please enter your message")
	@Size(min = 10, max = 2000, message = "Message must be between 10 and 2000 characters")
	private String message;

	public Long getPropertyId() {
		return propertyId;
	}

	public void setPropertyId(Long propertyId) {
		this.propertyId = propertyId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
