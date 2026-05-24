package com.realestate.main.dto.response;

import java.time.LocalDateTime;

import com.realestate.main.entity.enums.EnquiryStatus;

public class PropertyEnquiryResponse {

	private Long id;
	private Long propertyId;
	private String propertyCode;
	private String propertyTitle;
	private String propertyImageUrl;
	private Long agentId;
	private String agentName;
	private Long userId;
	private String userName;
	private String userEmail;
	private String userMobile;
	private String message;
	private EnquiryStatus status;
	private String statusLabel;
	private String agentReply;
	private LocalDateTime createdAt;
	private LocalDateTime acceptedAt;
	private LocalDateTime repliedAt;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getPropertyId() {
		return propertyId;
	}

	public void setPropertyId(Long propertyId) {
		this.propertyId = propertyId;
	}

	public String getPropertyCode() {
		return propertyCode;
	}

	public void setPropertyCode(String propertyCode) {
		this.propertyCode = propertyCode;
	}

	public String getPropertyTitle() {
		return propertyTitle;
	}

	public void setPropertyTitle(String propertyTitle) {
		this.propertyTitle = propertyTitle;
	}

	public String getPropertyImageUrl() {
		return propertyImageUrl;
	}

	public void setPropertyImageUrl(String propertyImageUrl) {
		this.propertyImageUrl = propertyImageUrl;
	}

	public Long getAgentId() {
		return agentId;
	}

	public void setAgentId(Long agentId) {
		this.agentId = agentId;
	}

	public String getAgentName() {
		return agentName;
	}

	public void setAgentName(String agentName) {
		this.agentName = agentName;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public String getUserMobile() {
		return userMobile;
	}

	public void setUserMobile(String userMobile) {
		this.userMobile = userMobile;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public EnquiryStatus getStatus() {
		return status;
	}

	public void setStatus(EnquiryStatus status) {
		this.status = status;
	}

	public String getStatusLabel() {
		return statusLabel;
	}

	public void setStatusLabel(String statusLabel) {
		this.statusLabel = statusLabel;
	}

	public String getAgentReply() {
		return agentReply;
	}

	public void setAgentReply(String agentReply) {
		this.agentReply = agentReply;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getAcceptedAt() {
		return acceptedAt;
	}

	public void setAcceptedAt(LocalDateTime acceptedAt) {
		this.acceptedAt = acceptedAt;
	}

	public LocalDateTime getRepliedAt() {
		return repliedAt;
	}

	public void setRepliedAt(LocalDateTime repliedAt) {
		this.repliedAt = repliedAt;
	}
}
