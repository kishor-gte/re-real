package com.realestate.main.dto.response;

import java.time.LocalDateTime;

import com.realestate.main.entity.enums.AccountStatus;
import com.realestate.main.entity.enums.AgentSpecialization;

public class AgentListItemResponse {

	private Long id;
	private String agentCode;
	private String fullName;
	private String email;
	private String mobile;
	private String agencyName;
	private String reraNumber;
	private AgentSpecialization specialization;
	private String city;
	private String state;
	private AccountStatus accountStatus;
	private boolean verified;
	private LocalDateTime createdAt;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getAgentCode() {
		return agentCode;
	}

	public void setAgentCode(String agentCode) {
		this.agentCode = agentCode;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

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

	public String getAgencyName() {
		return agencyName;
	}

	public void setAgencyName(String agencyName) {
		this.agencyName = agencyName;
	}

	public String getReraNumber() {
		return reraNumber;
	}

	public void setReraNumber(String reraNumber) {
		this.reraNumber = reraNumber;
	}

	public AgentSpecialization getSpecialization() {
		return specialization;
	}

	public void setSpecialization(AgentSpecialization specialization) {
		this.specialization = specialization;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public AccountStatus getAccountStatus() {
		return accountStatus;
	}

	public void setAccountStatus(AccountStatus accountStatus) {
		this.accountStatus = accountStatus;
	}

	public boolean isVerified() {
		return verified;
	}

	public void setVerified(boolean verified) {
		this.verified = verified;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
}
