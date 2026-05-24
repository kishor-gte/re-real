package com.realestate.main.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class PgOwnerRegisterRequest {

	@NotBlank(message = "Full name is required")
	@Size(min = 3, max = 50, message = "Name must be between 3 and 50 characters")
	@Pattern(regexp = "^[A-Za-z\\s]+$", message = "Name must contain only alphabets")
	private String fullName;

	@NotBlank(message = "Email is required")
	@Email(message = "Invalid email format")
	private String email;

	@NotBlank(message = "Mobile number is required")
	@Pattern(regexp = "^\\d{10}$", message = "Enter valid 10-digit mobile number")
	private String mobile;

	@Pattern(regexp = "^$|^[A-Za-z0-9][A-Za-z0-9\\s\\-/]{4,49}$", message = "Invalid business registration number format")
	private String businessRegistrationNumber;

	@NotNull(message = "Experience is required")
	@Min(value = 0, message = "Experience cannot be negative")
	@Max(value = 60, message = "Experience value is too high")
	private Integer experience;

	@NotBlank(message = "City is required")
	@Size(max = 80, message = "City is too long")
	private String city;

	@NotBlank(message = "State is required")
	@Size(max = 80, message = "State is too long")
	private String state;

	@NotBlank(message = "Pincode is required")
	@Pattern(regexp = "^\\d{6}$", message = "Pincode must be exactly 6 digits")
	private String pincode;

	@NotBlank(message = "Password is required")
	@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$", message = "Password does not meet security requirements")
	private String password;

	@NotBlank(message = "Confirm password is required")
	private String confirmPassword;

	private String referralCodeUsed;

	private boolean termsAccepted;

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

	public String getBusinessRegistrationNumber() {
		return businessRegistrationNumber;
	}

	public void setBusinessRegistrationNumber(String businessRegistrationNumber) {
		this.businessRegistrationNumber = businessRegistrationNumber;
	}

	public Integer getExperience() {
		return experience;
	}

	public void setExperience(Integer experience) {
		this.experience = experience;
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

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

	public String getReferralCodeUsed() {
		return referralCodeUsed;
	}

	public void setReferralCodeUsed(String referralCodeUsed) {
		this.referralCodeUsed = referralCodeUsed;
	}

	public boolean isTermsAccepted() {
		return termsAccepted;
	}

	public void setTermsAccepted(boolean termsAccepted) {
		this.termsAccepted = termsAccepted;
	}

	@AssertTrue(message = "Passwords do not match")
	public boolean isPasswordsMatch() {
		if (password == null || confirmPassword == null) {
			return false;
		}
		return password.equals(confirmPassword);
	}

	@AssertTrue(message = "You must accept Terms & Conditions")
	public boolean isTermsAcceptedValid() {
		return termsAccepted;
	}
}
