package com.realestate.main.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.realestate.main.dto.request.AdminRegisterRequest;
import com.realestate.main.service.AdminLookupService;
import com.realestate.main.util.MobileUtils;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PendingAdminRegistrationData {

	private String fullName;
	private String officialEmail;
	private String mobile;
	private String password;
	private String securityQuestion;
	private String securityAnswer;
	private String profileImage;

	public PendingAdminRegistrationData() {
	}

	public static PendingAdminRegistrationData from(AdminRegisterRequest r, String profileImage) {
		PendingAdminRegistrationData d = new PendingAdminRegistrationData();
		d.setFullName(r.getFullName());
		d.setOfficialEmail(AdminLookupService.normalizeEmail(r.getOfficialEmail()));
		d.setMobile(MobileUtils.normalize(r.getMobile()));
		d.setPassword(r.getPassword());
		d.setSecurityQuestion(r.getSecurityQuestion());
		d.setSecurityAnswer(r.getSecurityAnswer());
		d.setProfileImage(profileImage);
		return d;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getOfficialEmail() {
		return officialEmail;
	}

	public void setOfficialEmail(String officialEmail) {
		this.officialEmail = officialEmail;
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

	public String getSecurityQuestion() {
		return securityQuestion;
	}

	public void setSecurityQuestion(String securityQuestion) {
		this.securityQuestion = securityQuestion;
	}

	public String getSecurityAnswer() {
		return securityAnswer;
	}

	public void setSecurityAnswer(String securityAnswer) {
		this.securityAnswer = securityAnswer;
	}

	public String getProfileImage() {
		return profileImage;
	}

	public void setProfileImage(String profileImage) {
		this.profileImage = profileImage;
	}
}
