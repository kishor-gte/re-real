package com.realestate.main.security;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.realestate.main.entity.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

	private static final Logger log = LoggerFactory.getLogger(JwtService.class);

	public static final String CLAIM_USER_ID = "uid";
	public static final String CLAIM_ROLE = "role";
	public static final String CLAIM_SESSION_ID = "sid";

	private final JwtProperties jwtProperties;
	private final SecretKey secretKey;

	public JwtService(JwtProperties jwtProperties) {
		this.jwtProperties = jwtProperties;
		String configured = jwtProperties.getSecret();
		if (configured == null || configured.isBlank() || configured.getBytes(StandardCharsets.UTF_8).length < 32) {
			// generate a secure random 256-bit key for local/dev when none provided
			byte[] key = new byte[32];
			new SecureRandom().nextBytes(key);
			this.secretKey = Keys.hmacShaKeyFor(key);
			log.warn("JWT secret is not configured or too weak — generated a temporary secure key. Set 'APP_JWT_SECRET' in production.");
			// keep the generated secret visible in logs only in dev; print Base64 to ease debugging when running locally
			log.debug("Generated JWT key (base64): {}", Base64.getEncoder().encodeToString(key));
		} else {
			this.secretKey = Keys.hmacShaKeyFor(configured.getBytes(StandardCharsets.UTF_8));
		}
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
