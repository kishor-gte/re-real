package com.realestate.main.security;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import com.realestate.main.entity.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

	public static final String CLAIM_USER_ID = "uid";
	public static final String CLAIM_ROLE = "role";
	public static final String CLAIM_SESSION_ID = "sid";

	private final JwtProperties jwtProperties;
	private final SecretKey secretKey;

	public JwtService(JwtProperties jwtProperties) {
		this.jwtProperties = jwtProperties;
		this.secretKey = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
	}

	public String createToken(User user, String sessionId, boolean rememberMe) {
		long ttl = rememberMe ? jwtProperties.getRememberMeExpirationSeconds() : jwtProperties.getExpirationSeconds();
		Instant now = Instant.now();
		return Jwts.builder()
				.subject(user.getEmail())
				.claim(CLAIM_USER_ID, user.getId())
				.claim(CLAIM_ROLE, user.getRole().name())
				.claim(CLAIM_SESSION_ID, sessionId)
				.issuedAt(Date.from(now))
				.expiration(Date.from(now.plusSeconds(ttl)))
				.signWith(secretKey)
				.compact();
	}

	public long getExpirationSeconds(boolean rememberMe) {
		return rememberMe ? jwtProperties.getRememberMeExpirationSeconds() : jwtProperties.getExpirationSeconds();
	}

	public Optional<Claims> parseClaims(String token) {
		try {
			Claims claims = Jwts.parser()
					.verifyWith(secretKey)
					.build()
					.parseSignedClaims(token)
					.getPayload();
			return Optional.of(claims);
		} catch (JwtException | IllegalArgumentException ex) {
			return Optional.empty();
		}
	}

	public Optional<String> extractSessionId(String token) {
		return parseClaims(token).map(c -> c.get(CLAIM_SESSION_ID, String.class));
	}

	public Optional<Long> extractUserId(String token) {
		return parseClaims(token).map(c -> c.get(CLAIM_USER_ID, Long.class));
	}

	public String getCookieName() {
		return jwtProperties.getCookieName();
	}
}
