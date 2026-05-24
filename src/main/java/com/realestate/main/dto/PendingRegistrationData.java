package com.realestate.main.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.realestate.main.dto.request.RegisterRequest;
import com.realestate.main.entity.enums.Gender;
import com.realestate.main.entity.enums.UserRole;

/**
 * Serialized in otp_verification.registration_payload — must deserialize reliably with Jackson.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PendingRegistrationData {

	private String fullName;
	private String email;
	private String mobile;
	private String password;
	private Gender gender;
	private LocalDate dateOfBirth;
	private UserRole role;
	private String address;
	private String city;
	private String state;
	private String pincode;
	@JsonAlias("referralCode")
	private String referralCodeUsed;
	private String profileImage;

	public PendingRegistrationData() {
	}

	public static PendingRegistrationData from(RegisterRequest r, String profileImage) {
		PendingRegistrationData d = new PendingRegistrationData();
		d.setFullName(r.getFullName());
		d.setEmail(com.realestate.main.service.UserLookupService.normalizeEmail(r.getEmail()));
		d.setMobile(com.realestate.main.util.MobileUtils.normalize(r.getMobile()));
		d.setPassword(r.getPassword());
		d.setGender(r.getGender());
		d.setDateOfBirth(r.getDateOfBirth());
		d.setRole(r.getRole());
		d.setAddress(r.getAddress());
		d.setCity(r.getCity());
		d.setState(r.getState());
		d.setPincode(r.getPincode());
		d.setReferralCodeUsed(r.getReferralCode());
		d.setProfileImage(profileImage);
		return d;
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

	public String getReferralCodeUsed() {
		return referralCodeUsed;
	}

	public void setReferralCodeUsed(String referralCodeUsed) {
		this.referralCodeUsed = referralCodeUsed;
	}

	public String getProfileImage() {
		return profileImage;
	}

	public void setProfileImage(String profileImage) {
		this.profileImage = profileImage;
	}
}
