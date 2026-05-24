package com.realestate.main.dto.response;

import com.realestate.main.entity.User;

public class AuthResponse {

	private String redirectUrl;
	private String accessToken;
	private String tokenType = "Bearer";
	private long expiresInSeconds;
	private String fullName;
	private String email;
	private String role;
	private UserResponse user;

	public static AuthResponse from(LoginResult result, String redirectUrl) {
		User user = result.getUser();
		AuthResponse r = new AuthResponse();
		r.setRedirectUrl(redirectUrl);
		r.setAccessToken(result.getAccessToken());
		r.setExpiresInSeconds(result.getExpiresInSeconds());
		r.setFullName(user.getFullName());
		r.setEmail(user.getEmail());
		r.setRole(user.getRole().name());
		r.setUser(UserResponse.from(user));
		return r;
	}

	public String getRedirectUrl() {
		return redirectUrl;
	}

	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getTokenType() {
		return tokenType;
	}

	public void setTokenType(String tokenType) {
		this.tokenType = tokenType;
	}

	public long getExpiresInSeconds() {
		return expiresInSeconds;
	}

	public void setExpiresInSeconds(long expiresInSeconds) {
		this.expiresInSeconds = expiresInSeconds;
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

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public UserResponse getUser() {
		return user;
	}

	public void setUser(UserResponse user) {
		this.user = user;
	}
}
