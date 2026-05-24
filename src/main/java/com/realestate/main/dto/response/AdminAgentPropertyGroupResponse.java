package com.realestate.main.dto.response;

import java.util.ArrayList;
import java.util.List;

public class AdminAgentPropertyGroupResponse {

	private Long agentId;
	private String agentCode;
	private String agentName;
	private String agencyName;
	private String agentEmail;
	private String agentCity;
	private List<AdminPropertyListItemResponse> properties = new ArrayList<>();

	public Long getAgentId() {
		return agentId;
	}

	public void setAgentId(Long agentId) {
		this.agentId = agentId;
	}

	public String getAgentCode() {
		return agentCode;
	}

	public void setAgentCode(String agentCode) {
		this.agentCode = agentCode;
	}

	public String getAgentName() {
		return agentName;
	}

	public void setAgentName(String agentName) {
		this.agentName = agentName;
	}

	public String getAgencyName() {
		return agencyName;
	}

	public void setAgencyName(String agencyName) {
		this.agencyName = agencyName;
	}

	public String getAgentEmail() {
		return agentEmail;
	}

	public void setAgentEmail(String agentEmail) {
		this.agentEmail = agentEmail;
	}

	public String getAgentCity() {
		return agentCity;
	}

	public void setAgentCity(String agentCity) {
		this.agentCity = agentCity;
	}

	public List<AdminPropertyListItemResponse> getProperties() {
		return properties;
	}

	public void setProperties(List<AdminPropertyListItemResponse> properties) {
		this.properties = properties;
	}
}
