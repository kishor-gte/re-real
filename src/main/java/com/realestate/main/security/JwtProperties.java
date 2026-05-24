package com.realestate.main.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.jwt")
public class JwtProperties {

	private String secret = "ChangeMeToASecureSecretKeyAtLeast32Characters";
	private long expirationSeconds = 86400;
	private long rememberMeExpirationSeconds = 2592000;
	private String cookieName = "ESTATEVAULT_TOKEN";

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public long getExpirationSeconds() {
		return expirationSeconds;
	}

	public void setExpirationSeconds(long expirationSeconds) {
		this.expirationSeconds = expirationSeconds;
	}

	public long getRememberMeExpirationSeconds() {
		return rememberMeExpirationSeconds;
	}

	public void setRememberMeExpirationSeconds(long rememberMeExpirationSeconds) {
		this.rememberMeExpirationSeconds = rememberMeExpirationSeconds;
	}

	public String getCookieName() {
		return cookieName;
	}

	public void setCookieName(String cookieName) {
		this.cookieName = cookieName;
	}
}
