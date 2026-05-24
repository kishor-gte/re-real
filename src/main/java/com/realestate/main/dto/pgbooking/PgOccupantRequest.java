package com.realestate.main.dto.pgbooking;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PgOccupantRequest {

	@NotBlank
	@Size(min = 2, max = 120)
	private String fullName;

	@NotBlank
	@Size(min = 10, max = 15)
	private String mobile;

	@Size(max = 120)
	private String email;

	private Integer bedNumber;

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Integer getBedNumber() {
		return bedNumber;
	}

	public void setBedNumber(Integer bedNumber) {
		this.bedNumber = bedNumber;
	}
}
