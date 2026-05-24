package com.realestate.main.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.realestate.main.entity.enums.AccountStatus;
import com.realestate.main.entity.enums.Gender;
import com.realestate.main.entity.enums.UserRole;

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
@Table(name = "users")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "full_name", nullable = false, length = 50)
	private String fullName;

	@Column(nullable = false, unique = true, length = 150)
	private String email;

	/** DB column is {@code phone} (legacy schema); Java field remains {@code mobile}. */
	@Column(name = "phone", nullable = false, unique = true, length = 15)
	private String mobile;

	@Column(nullable = false, length = 255)
	private String password;

	@Enumerated(EnumType.STRING)
	@Column(length = 15)
	private Gender gender;

	@Column(name = "date_of_birth")
	private LocalDate dateOfBirth;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private UserRole role;

	@Column(length = 255)
	private String address;

	@Column(length = 100)
	private String city;

	@Column(length = 100)
	private String state;

	@Column(length = 6)
	private String pincode;

	@Column(name = "referral_code", unique = true, length = 12)
	private String referralCode;

	@Column(name = "referred_by", length = 12)
	private String referredBy;

	@Column(name = "profile_image", length = 500)
	private String profileImage;

	@Column(name = "is_verified", nullable = false)
	private boolean verified = false;

	@Enumerated(EnumType.STRING)
	@Column(name = "account_status", nullable = false, length = 20)
	private AccountStatus accountStatus = AccountStatus.PENDING;

	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	public User() {
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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public LocalDate getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(LocalDate dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public UserRole getRole() {
		return role;
	}

	public void setRole(UserRole role) {
		this.role = role;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
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

	public String getReferralCode() {
		return referralCode;
	}

	public void setReferralCode(String referralCode) {
		this.referralCode = referralCode;
	}

	public String getReferredBy() {
		return referredBy;
	}

	public void setReferredBy(String referredBy) {
		this.referredBy = referredBy;
	}

	public String getProfileImage() {
		return profileImage;
	}

	public void setProfileImage(String profileImage) {
		this.profileImage = profileImage;
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
