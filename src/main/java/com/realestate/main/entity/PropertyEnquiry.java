package com.realestate.main.entity;

import java.time.LocalDateTime;

import com.realestate.main.entity.enums.EnquiryStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "property_enquiries")
public class PropertyEnquiry {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "property_id", nullable = false)
	private Long propertyId;

	@Column(name = "agent_id", nullable = false)
	private Long agentId;

	@Column(name = "user_id", nullable = false)
	private Long userId;

	@Column(name = "user_name", nullable = false, length = 120)
	private String userName;

	@Column(name = "user_email", nullable = false, length = 150)
	private String userEmail;

	@Column(name = "user_mobile", length = 15)
	private String userMobile;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String message;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private EnquiryStatus status = EnquiryStatus.PENDING;

	@Column(name = "agent_reply", columnDefinition = "TEXT")
	private String agentReply;

	@Column(name = "accepted_at")
	private LocalDateTime acceptedAt;

	@Column(name = "replied_at")
	private LocalDateTime repliedAt;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

	@PrePersist
	void onCreate() {
		LocalDateTime now = LocalDateTime.now();
		createdAt = now;
		updatedAt = now;
		if (status == null) {
			status = EnquiryStatus.PENDING;
		}
	}

	@PreUpdate
	void onUpdate() {
		updatedAt = LocalDateTime.now();
	}

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

	public Long getAgentId() {
		return agentId;
	}

	public void setAgentId(Long agentId) {
		this.agentId = agentId;
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

	public String getAgentReply() {
		return agentReply;
	}

	public void setAgentReply(String agentReply) {
		this.agentReply = agentReply;
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

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}
}
