package com.realestate.main.dto.response;

import java.time.LocalDateTime;

public class PgOwnerDetailResponse extends PgOwnerListItemResponse {

	private int experience;
	private String businessRegistrationNumber;
	private String officeAddress;
	private String pincode;
	private String profilePhoto;
	private String pgImages;
	private String governmentId;
	private String referralCodeUsed;
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

	public String getBusinessRegistrationNumber() {
		return businessRegistrationNumber;
	}

	public void setBusinessRegistrationNumber(String businessRegistrationNumber) {
		this.businessRegistrationNumber = businessRegistrationNumber;
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

	public String getPgImages() {
		return pgImages;
	}

	public void setPgImages(String pgImages) {
		this.pgImages = pgImages;
	}

	public String getGovernmentId() {
		return governmentId;
	}

	public void setGovernmentId(String governmentId) {
		this.governmentId = governmentId;
	}

	public String getReferralCodeUsed() {
		return referralCodeUsed;
	}

	public void setReferralCodeUsed(String referralCodeUsed) {
		this.referralCodeUsed = referralCodeUsed;
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
