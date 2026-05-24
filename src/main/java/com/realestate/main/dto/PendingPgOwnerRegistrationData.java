package com.realestate.main.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.realestate.main.dto.request.PgOwnerRegisterRequest;
import com.realestate.main.entity.enums.GenderAllowed;
import com.realestate.main.entity.enums.PgType;
import com.realestate.main.service.PgOwnerLookupService;
import com.realestate.main.util.MobileUtils;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PendingPgOwnerRegistrationData {

	private String fullName;
	private String email;
	private String mobile;
	private String pgName;
	private String businessRegistrationNumber;
	private int experience;
	private PgType pgType;
	private GenderAllowed genderAllowed;
	private String officeAddress;
	private String city;
	private String state;
	private String pincode;
	private String password;
	private String profilePhoto;
	private String pgImages;
	private String governmentId;
	private String referralCodeUsed;

	public PendingPgOwnerRegistrationData() {
	}

	public static PendingPgOwnerRegistrationData from(PgOwnerRegisterRequest r, String profilePhoto,
			String governmentId) {
		PendingPgOwnerRegistrationData d = new PendingPgOwnerRegistrationData();
		d.setFullName(r.getFullName().trim());
		d.setEmail(PgOwnerLookupService.normalizeEmail(r.getEmail()));
		d.setMobile(MobileUtils.normalize(r.getMobile()));
		if (r.getBusinessRegistrationNumber() != null && !r.getBusinessRegistrationNumber().isBlank()) {
			d.setBusinessRegistrationNumber(r.getBusinessRegistrationNumber().trim().toUpperCase());
		}
		d.setExperience(r.getExperience());
		d.setCity(r.getCity().trim());
		d.setState(r.getState().trim());
		d.setPincode(r.getPincode().trim());
		d.setPassword(r.getPassword());
		d.setProfilePhoto(profilePhoto);
		d.setGovernmentId(governmentId);
		if (r.getReferralCodeUsed() != null && !r.getReferralCodeUsed().isBlank()) {
			d.setReferralCodeUsed(r.getReferralCodeUsed().trim().toUpperCase());
		}
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
}
