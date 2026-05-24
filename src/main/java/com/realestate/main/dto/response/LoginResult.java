package com.realestate.main.dto.response;

import com.realestate.main.entity.User;

public class LoginResult {

	private final User user;
	private final String accessToken;
	private final long expiresInSeconds;

	public LoginResult(User user, String accessToken, long expiresInSeconds) {
		this.user = user;
		this.accessToken = accessToken;
		this.expiresInSeconds = expiresInSeconds;
	}

	public User getUser() {
		return user;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public long getExpiresInSeconds() {
		return expiresInSeconds;
	}
}
