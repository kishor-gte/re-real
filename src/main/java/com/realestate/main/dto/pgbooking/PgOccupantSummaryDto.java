package com.realestate.main.dto.pgbooking;

public class PgOccupantSummaryDto {

	private String fullName;
	private String mobile;
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
