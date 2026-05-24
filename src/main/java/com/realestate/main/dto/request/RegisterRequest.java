package com.realestate.main.dto.request;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import com.realestate.main.entity.enums.Gender;
import com.realestate.main.entity.enums.UserRole;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class RegisterRequest {

	@NotBlank(message = "Full Name is required")
	@Size(min = 3, max = 50, message = "Name must be between 3 and 50 characters")
	@Pattern(regexp = "^[A-Za-z\\s]+$", message = "Name must contain only alphabets")
	private String fullName;

	@NotBlank(message = "Email is required")
	@Email(message = "Invalid email format")
	private String email;

	@NotBlank(message = "Mobile number is required")
	@Pattern(regexp = "^\\d{10}$", message = "Enter valid 10-digit mobile number")
	private String mobile;

	@NotBlank(message = "Password is required")
	@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$", message = "Password is too weak")
	private String password;

	@NotBlank(message = "Confirm password is required")
	private String confirmPassword;

	@NotNull(message = "Gender is required")
	private Gender gender;

	@NotNull(message = "Date of birth is required")
	@Past(message = "Invalid date of birth")
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	private LocalDate dateOfBirth;

	@NotNull(message = "Role is required")
	private UserRole role;

	private String address;
	private String city;
	private String state;

	@Pattern(regexp = "^$|^\\d{6}$", message = "Pincode must be 6 digits")
	private String pincode;

	private String referralCode;

	@AssertTrue(message = "Please accept Terms & Conditions")
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

	public boolean isTermsAccepted() {
		return termsAccepted;
	}

	public void setTermsAccepted(boolean termsAccepted) {
		this.termsAccepted = termsAccepted;
	}
}
