package com.realestate.main.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class AdminRegisterRequest {

	@NotBlank(message = "Full name is required")
	@Size(min = 3, max = 50, message = "Name must be between 3 and 50 characters")
	@Pattern(regexp = "^[A-Za-z\\s]+$", message = "Name must contain only alphabets")
	private String fullName;

	@NotBlank(message = "Official email is required")
	@Email(message = "Invalid email format")
	private String officialEmail;

	@NotBlank(message = "Mobile number is required")
	@Pattern(regexp = "^\\d{10}$", message = "Enter valid 10-digit mobile number")
	private String mobile;

	@NotBlank(message = "Password is required")
	@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{10,}$", message = "Password does not meet security requirements")
	private String password;

	@NotBlank(message = "Confirm password is required")
	private String confirmPassword;

	@NotBlank(message = "Security question is required")
	private String securityQuestion;

	@NotBlank(message = "Security answer is required")
	@Size(min = 2, max = 100, message = "Security answer must be 2-100 characters")
	private String securityAnswer;

	private boolean termsAccepted;

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

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
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
