package com.realestate.main.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.realestate.main.entity.User;
import com.realestate.main.entity.enums.AccountStatus;
import com.realestate.main.entity.enums.Gender;
import com.realestate.main.entity.enums.UserRole;

/**
 * Safe API view of a user (no password).
 */
public class UserResponse {

	private Long id;
	private String fullName;
	private String email;
	private String mobile;
	private Gender gender;
	private LocalDate dateOfBirth;
	private UserRole role;
	private String address;
	private String city;
	private String state;
	private String pincode;
	private String referralCode;
	private String profileImage;
	private boolean verified;
	private AccountStatus accountStatus;
	private LocalDateTime createdAt;

	public static UserResponse from(User user) {
		if (user == null) {
			return null;
		}
		UserResponse r = new UserResponse();
		r.setId(user.getId());
		r.setFullName(user.getFullName());
		r.setEmail(user.getEmail());
		r.setMobile(user.getMobile());
		r.setGender(user.getGender());
		r.setDateOfBirth(user.getDateOfBirth());
		r.setRole(user.getRole());
		r.setAddress(user.getAddress());
		r.setCity(user.getCity());
		r.setState(user.getState());
		r.setPincode(user.getPincode());
		r.setReferralCode(user.getReferralCode());
		r.setProfileImage(user.getProfileImage());
		r.setVerified(user.isVerified());
		r.setAccountStatus(user.getAccountStatus());
		r.setCreatedAt(user.getCreatedAt());
		return r;
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
}
