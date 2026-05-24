package com.realestate.main.entity;

import java.time.LocalDateTime;

import com.realestate.main.entity.enums.AccountStatus;
import com.realestate.main.entity.enums.GenderAllowed;
import com.realestate.main.entity.enums.PgType;

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
@Table(name = "pg_owners")
public class PgOwner {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "pg_owner_code", nullable = false, unique = true, length = 20)
	private String pgOwnerCode;

	@Column(name = "full_name", nullable = false, length = 50)
	private String fullName;

	@Column(nullable = false, unique = true, length = 150)
	private String email;

	@Column(nullable = false, unique = true, length = 15)
	private String mobile;

	@Column(name = "pg_name", length = 100)
	private String pgName;

	@Column(name = "business_registration_number", length = 50)
	private String businessRegistrationNumber;

	@Column(nullable = false)
	private int experience;

	@Enumerated(EnumType.STRING)
	@Column(name = "pg_type", length = 30)
	private PgType pgType;

	@Enumerated(EnumType.STRING)
	@Column(name = "gender_allowed", length = 20)
	private GenderAllowed genderAllowed;

	@Column(name = "office_address", length = 255)
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

	@Column(name = "pg_images", columnDefinition = "TEXT")
	private String pgImages;

	@Column(name = "government_id", nullable = false, length = 500)
	private String governmentId;

	@Column(name = "referral_code_used", length = 20)
	private String referralCodeUsed;

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

	public PgOwner() {
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

	public String getPgOwnerCode() {
		return pgOwnerCode;
	}

	public void setPgOwnerCode(String pgOwnerCode) {
		this.pgOwnerCode = pgOwnerCode;
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

	public String getPgName() {
		return pgName;
	}

	public void setPgName(String pgName) {
		this.pgName = pgName;
	}

	public String getBusinessRegistrationNumber() {
		return businessRegistrationNumber;
	}

	public void setBusinessRegistrationNumber(String businessRegistrationNumber) {
		this.businessRegistrationNumber = businessRegistrationNumber;
	}

	public int getExperience() {
		return experience;
	}

	public void setExperience(int experience) {
		this.experience = experience;
	}

	public PgType getPgType() {
		return pgType;
	}

	public void setPgType(PgType pgType) {
		this.pgType = pgType;
	}

	public GenderAllowed getGenderAllowed() {
		return genderAllowed;
	}

	public void setGenderAllowed(GenderAllowed genderAllowed) {
		this.genderAllowed = genderAllowed;
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
