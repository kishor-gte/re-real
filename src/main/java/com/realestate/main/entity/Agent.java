package com.realestate.main.entity;

import java.time.LocalDateTime;

import com.realestate.main.entity.enums.AccountStatus;
import com.realestate.main.entity.enums.AgentSpecialization;

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
@Table(name = "agents")
public class Agent {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "agent_code", nullable = false, unique = true, length = 20)
	private String agentCode;

	@Column(name = "full_name", nullable = false, length = 50)
	private String fullName;

	@Column(nullable = false, unique = true, length = 150)
	private String email;

	@Column(nullable = false, unique = true, length = 15)
	private String mobile;

	@Column(name = "agency_name", nullable = false, length = 100)
	private String agencyName;

	@Column(name = "rera_number", nullable = false, unique = true, length = 50)
	private String reraNumber;

	@Column(nullable = false)
	private int experience;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 30)
	private AgentSpecialization specialization;

	@Column(name = "office_address", nullable = false, length = 255)
	private String officeAddress;

	@Column(nullable = false, length = 80)
	private String city;

	@Column(nullable = false, length = 80)
	private String state;

	@Column(nullable = false, length = 6)
	private String pincode;

	@Column(nullable = false, length = 255)
	private String password;

	@Column(name = "profile_photo", length = 500)
	private String profilePhoto;

	@Column(name = "agency_logo", length = 500)
	private String agencyLogo;

	@Column(name = "government_id", nullable = false, length = 500)
	private String governmentId;

	@Column(name = "referral_code", nullable = false, unique = true, length = 20)
	private String referralCode;

	@Column(name = "referred_by_agent_id")
	private Long referredByAgentId;

	@Column(name = "is_verified", nullable = false)
	private boolean verified = false;

	@Enumerated(EnumType.STRING)
	@Column(name = "account_status", nullable = false, length = 20)
	private AccountStatus accountStatus = AccountStatus.PENDING;

	@Column(name = "failed_login_attempts", nullable = false)
	private int failedLoginAttempts = 0;

	@Column(name = "last_login")
	private LocalDateTime lastLogin;

	@Column(name = "approved_at")
	private LocalDateTime approvedAt;

	@Column(name = "approved_by_admin_id")
	private Long approvedByAdminId;

	@Column(name = "rejection_reason", length = 500)
	private String rejectionReason;

	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	public Agent() {
	}

	@PrePersist
	void onCreate() {
		createdAt = LocalDateTime.now();
		updatedAt = LocalDateTime.now();
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

	public int getExperience() {
		return experience;
	}

	public void setExperience(int experience) {
		this.experience = experience;
	}

	public AgentSpecialization getSpecialization() {
		return specialization;
	}

	public void setSpecialization(AgentSpecialization specialization) {
		this.specialization = specialization;
	}

	public String getOfficeAddress() {
		return officeAddress;
	}

	public void setOfficeAddress(String officeAddress) {
		this.officeAddress = officeAddress;
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

	public String getPincode() {
		return pincode;
	}

	public void setPincode(String pincode) {
		this.pincode = pincode;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getProfilePhoto() {
		return profilePhoto;
	}

	public void setProfilePhoto(String profilePhoto) {
		this.profilePhoto = profilePhoto;
	}

	public String getAgencyLogo() {
		return agencyLogo;
	}

	public void setAgencyLogo(String agencyLogo) {
		this.agencyLogo = agencyLogo;
	}

	public String getGovernmentId() {
		return governmentId;
	}

	public void setGovernmentId(String governmentId) {
		this.governmentId = governmentId;
	}

	public String getReferralCode() {
		return referralCode;
	}

	public void setReferralCode(String referralCode) {
		this.referralCode = referralCode;
	}

	public Long getReferredByAgentId() {
		return referredByAgentId;
	}

	public void setReferredByAgentId(Long referredByAgentId) {
		this.referredByAgentId = referredByAgentId;
	}

	public boolean isVerified() {
		return verified;
	}

	public void setVerified(boolean verified) {
		this.verified = verified;
	}

	public AccountStatus getAccountStatus() {
		return accountStatus;
	}

	public void setAccountStatus(AccountStatus accountStatus) {
		this.accountStatus = accountStatus;
	}

	public int getFailedLoginAttempts() {
		return failedLoginAttempts;
	}

	public void setFailedLoginAttempts(int failedLoginAttempts) {
		this.failedLoginAttempts = failedLoginAttempts;
	}

	public LocalDateTime getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(LocalDateTime lastLogin) {
		this.lastLogin = lastLogin;
	}

	public LocalDateTime getApprovedAt() {
		return approvedAt;
	}

	public void setApprovedAt(LocalDateTime approvedAt) {
		this.approvedAt = approvedAt;
	}

	public Long getApprovedByAdminId() {
		return approvedByAdminId;
	}

	public void setApprovedByAdminId(Long approvedByAdminId) {
		this.approvedByAdminId = approvedByAdminId;
	}

	public String getRejectionReason() {
		return rejectionReason;
	}

	public void setRejectionReason(String rejectionReason) {
		this.rejectionReason = rejectionReason;
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
