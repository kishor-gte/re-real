package com.realestate.main.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class PgOwnerResetPasswordRequest {

	@NotBlank(message = "Reset token is required")
	private String token;

	@NotBlank(message = "Password is required")
	@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$", message = "Password does not meet security requirements")
	private String password;

	@NotBlank(message = "Confirm password is required")
	private String confirmPassword;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
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

	@AssertTrue(message = "Passwords do not match")
	public boolean isPasswordsMatch() {
		if (password == null || confirmPassword == null) {
			return false;
		}
		return password.equals(confirmPassword);
	}
}
