package com.realestate.main.dto.response;

import java.time.LocalDateTime;

import com.realestate.main.entity.enums.AccountStatus;
import com.realestate.main.entity.enums.AgentSpecialization;

public class AgentDetailResponse extends AgentListItemResponse {

	private int experience;
	private String officeAddress;
	private String pincode;
	private String profilePhoto;
	private String agencyLogo;
	private String governmentId;
	private String referralCode;
	private Long referredByAgentId;
	private LocalDateTime approvedAt;
	private Long approvedByAdminId;
	private String rejectionReason;
	private LocalDateTime lastLogin;
	private LocalDateTime updatedAt;

	public int getExperience() {
		return experience;
	}

	public void setExperience(int experience) {
		this.experience = experience;
	}

	public String getOfficeAddress() {
		return officeAddress;
	}

	public void setOfficeAddress(String officeAddress) {
		this.officeAddress = officeAddress;
	}

	public String getPincode() {
		return pincode;
	}

	public void setPincode(String pincode) {
		this.pincode = pincode;
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

	public LocalDateTime getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(LocalDateTime lastLogin) {
		this.lastLogin = lastLogin;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}
}
