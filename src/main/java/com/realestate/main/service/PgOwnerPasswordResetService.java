package com.realestate.main.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.realestate.main.dto.request.PgOwnerForgotPasswordRequest;
import com.realestate.main.dto.request.PgOwnerResetPasswordRequest;
import com.realestate.main.dto.response.PasswordResetRequestResult;
import com.realestate.main.entity.PgOwner;
import com.realestate.main.entity.PgOwnerPasswordResetToken;
import com.realestate.main.exception.AuthException;
import com.realestate.main.repository.PgOwnerPasswordResetTokenRepository;
import com.realestate.main.repository.PgOwnerRepository;
import com.realestate.main.util.TokenHashUtil;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class PgOwnerPasswordResetService {

	private final PgOwnerLookupService pgOwnerLookup;
	private final PgOwnerRepository pgOwnerRepository;
	private final PgOwnerPasswordResetTokenRepository tokenRepository;
	private final EmailService emailService;
	private final PasswordEncoder passwordEncoder;
	private final ApplicationUrlService applicationUrlService;

	@Value("${app.password-reset.expiry-minutes:30}")
	private int expiryMinutes;

	@Value("${app.mail.expose-reset-token-in-response:true}")
	private boolean exposeResetTokenInResponse;

	public PgOwnerPasswordResetService(PgOwnerLookupService pgOwnerLookup, PgOwnerRepository pgOwnerRepository,
			PgOwnerPasswordResetTokenRepository tokenRepository, EmailService emailService,
			PasswordEncoder passwordEncoder, ApplicationUrlService applicationUrlService) {
		this.pgOwnerLookup = pgOwnerLookup;
		this.pgOwnerRepository = pgOwnerRepository;
		this.tokenRepository = tokenRepository;
		this.emailService = emailService;
		this.passwordEncoder = passwordEncoder;
		this.applicationUrlService = applicationUrlService;
	}

	@Transactional
	public PasswordResetRequestResult requestReset(PgOwnerForgotPasswordRequest request,
			HttpServletRequest httpRequest) {
		Optional<PgOwner> ownerOpt = pgOwnerLookup.findVerifiedPgOwnerByEmailAndMobile(request.getEmail(),
				request.getMobile());
		if (ownerOpt.isEmpty()) {
			throw new AuthException("Email and phone number do not match any verified PG owner account.");
		}

		PgOwner owner = ownerOpt.get();
		tokenRepository.invalidateActiveTokensForPgOwner(owner.getId());

		String rawToken = TokenHashUtil.generateSecureToken();
		PgOwnerPasswordResetToken entity = new PgOwnerPasswordResetToken();
		entity.setPgOwnerId(owner.getId());
		entity.setTokenHash(TokenHashUtil.hashToken(rawToken));
		entity.setExpiresAt(LocalDateTime.now().plusMinutes(expiryMinutes));
		entity.setUsed(false);
		tokenRepository.save(entity);

		String resetUrl = applicationUrlService.buildPgOwnerResetPasswordUrl(httpRequest, rawToken,
				request.getClientOrigin());

		boolean emailSent = emailService.sendPgOwnerPasswordResetEmail(owner.getEmail(), owner.getFullName(), resetUrl,
				expiryMinutes);

		String message = emailSent ? "Password reset link sent to your email."
				: "Reset link generated. Check server logs or below if SMTP is blocked.";

		String exposedUrl = exposeResetTokenInResponse && !emailSent ? resetUrl : null;
		String exposedToken = exposeResetTokenInResponse && !emailSent ? rawToken : null;

		return new PasswordResetRequestResult(emailSent, message, exposedToken, exposedUrl);
	}

	@Transactional(readOnly = true)
	public boolean isTokenValid(String rawToken) {
		return findActive(rawToken).isPresent();
	}

	@Transactional
	public void resetPassword(PgOwnerResetPasswordRequest request) {
		if (!request.isPasswordsMatch()) {
			throw new AuthException("Passwords do not match");
		}

		PgOwnerPasswordResetToken token = findActive(request.getToken())
				.orElseThrow(() -> new AuthException("Invalid or expired reset link. Please request a new one."));

		PgOwner owner = pgOwnerRepository.findById(token.getPgOwnerId())
				.orElseThrow(() -> new AuthException("PG owner account not found"));

		owner.setPassword(passwordEncoder.encode(request.getPassword()));
		owner.setFailedLoginAttempts(0);
		pgOwnerRepository.save(owner);

		token.setUsed(true);
		tokenRepository.save(token);
		tokenRepository.invalidateActiveTokensForPgOwner(owner.getId());
	}

	private Optional<PgOwnerPasswordResetToken> findActive(String rawToken) {
		if (rawToken == null || rawToken.isBlank()) {
			return Optional.empty();
		}
		Optional<PgOwnerPasswordResetToken> tokenOpt = tokenRepository
				.findByTokenHashAndUsedFalse(TokenHashUtil.hashToken(rawToken));
		if (tokenOpt.isEmpty()) {
			return Optional.empty();
		}
		PgOwnerPasswordResetToken token = tokenOpt.get();
		if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
			return Optional.empty();
		}
		return Optional.of(token);
	}
}
