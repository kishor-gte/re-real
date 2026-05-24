package com.realestate.main.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.realestate.main.dto.request.AgentRegisterRequest;
import com.realestate.main.entity.enums.AgentSpecialization;
import com.realestate.main.service.AgentLookupService;
import com.realestate.main.util.MobileUtils;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PendingAgentRegistrationData {

	private String fullName;
	private String email;
	private String mobile;
	private String agencyName;
	private String reraNumber;
	private int experience;
	private AgentSpecialization specialization;
	private String officeAddress;
	private String city;
	private String state;
	private String pincode;
	private String password;
	private String profilePhoto;
	private String agencyLogo;
	private String governmentId;
	private String usedReferralCode;
	private Long referrerAgentId;

	public PendingAgentRegistrationData() {
	}

	public static PendingAgentRegistrationData from(AgentRegisterRequest r, String profilePhoto, String agencyLogo,
			String governmentId, Long referrerAgentId) {
		PendingAgentRegistrationData d = new PendingAgentRegistrationData();
		d.setFullName(r.getFullName().trim());
		d.setEmail(AgentLookupService.normalizeEmail(r.getEmail()));
		d.setMobile(MobileUtils.normalize(r.getMobile()));
		d.setAgencyName(r.getAgencyName().trim());
		d.setReraNumber(normalizeRera(r.getReraNumber()));
		d.setExperience(r.getExperience());
		d.setSpecialization(r.getSpecialization());
		d.setOfficeAddress(r.getOfficeAddress().trim());
		d.setCity(r.getCity().trim());
		d.setState(r.getState().trim());
		d.setPincode(r.getPincode().trim());
		d.setPassword(r.getPassword());
		d.setProfilePhoto(profilePhoto);
		d.setAgencyLogo(agencyLogo);
		d.setGovernmentId(governmentId);
		if (r.getReferralCode() != null && !r.getReferralCode().isBlank()) {
			d.setUsedReferralCode(r.getReferralCode().trim().toUpperCase());
		}
		d.setReferrerAgentId(referrerAgentId);
		return d;
	}

	public static String normalizeRera(String rera) {
		if (rera == null) {
			return "";
		}
		return rera.trim().toUpperCase().replaceAll("\\s+", " ");
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

	public String getUsedReferralCode() {
		return usedReferralCode;
	}

	public void setUsedReferralCode(String usedReferralCode) {
		this.usedReferralCode = usedReferralCode;
	}

	public Long getReferrerAgentId() {
		return referrerAgentId;
	}

	public void setReferrerAgentId(Long referrerAgentId) {
		this.referrerAgentId = referrerAgentId;
	}
}
