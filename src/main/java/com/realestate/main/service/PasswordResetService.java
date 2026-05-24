package com.realestate.main.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpServletRequest;

import com.realestate.main.dto.request.ForgotPasswordRequest;
import com.realestate.main.dto.request.ResetPasswordRequest;
import com.realestate.main.dto.response.PasswordResetRequestResult;
import com.realestate.main.entity.PasswordResetToken;
import com.realestate.main.entity.User;
import com.realestate.main.exception.AuthException;
import com.realestate.main.repository.PasswordResetTokenRepository;
import com.realestate.main.repository.UserRepository;
import com.realestate.main.util.TokenHashUtil;

@Service
public class PasswordResetService {

	private final UserLookupService userLookupService;
	private final UserRepository userRepository;
	private final PasswordResetTokenRepository tokenRepository;
	private final EmailService emailService;
	private final PasswordEncoder passwordEncoder;
	private final ApplicationUrlService applicationUrlService;

	@Value("${app.password-reset.expiry-minutes:30}")
	private int expiryMinutes;

	@Value("${app.mail.expose-reset-token-in-response:true}")
	private boolean exposeResetTokenInResponse;

	public PasswordResetService(UserLookupService userLookupService, UserRepository userRepository,
			PasswordResetTokenRepository tokenRepository, EmailService emailService,
			PasswordEncoder passwordEncoder, ApplicationUrlService applicationUrlService) {
		this.userLookupService = userLookupService;
		this.userRepository = userRepository;
		this.tokenRepository = tokenRepository;
		this.emailService = emailService;
		this.passwordEncoder = passwordEncoder;
		this.applicationUrlService = applicationUrlService;
	}

	@Transactional
	public PasswordResetRequestResult requestPasswordReset(ForgotPasswordRequest request, HttpServletRequest httpRequest) {
		Optional<User> userOpt = userLookupService.findVerifiedUserByEmailAndMobile(request.getEmail(),
				request.getMobile());
		if (userOpt.isEmpty()) {
			throw new AuthException(
					"Email and phone number do not match any verified account. Please check your details.");
		}

		User user = userOpt.get();
		tokenRepository.invalidateActiveTokensForUser(user.getId());

		String rawToken = TokenHashUtil.generateSecureToken();
		String tokenHash = TokenHashUtil.hashToken(rawToken);

		PasswordResetToken entity = new PasswordResetToken();
		entity.setUserId(user.getId());
		entity.setTokenHash(tokenHash);
		entity.setExpiresAt(LocalDateTime.now().plusMinutes(expiryMinutes));
		entity.setUsed(false);
		tokenRepository.save(entity);

		String resetUrl = applicationUrlService.buildResetPasswordUrl(httpRequest, rawToken, request.getClientOrigin());
		boolean emailSent = emailService.sendPasswordResetEmail(user.getEmail(), user.getFullName(), resetUrl,
				expiryMinutes);

		String message = emailSent
				? "Password reset link sent to your registered email."
				: "Reset link generated. Check server logs or the message below (SMTP may be blocked).";

		String exposedToken = exposeResetTokenInResponse && !emailSent ? rawToken : null;
		String exposedUrl = exposeResetTokenInResponse && !emailSent ? resetUrl : null;

		return new PasswordResetRequestResult(emailSent, message, exposedToken, exposedUrl);
	}

	@Transactional(readOnly = true)
	public boolean isResetTokenValid(String rawToken) {
		return findActiveToken(rawToken).isPresent();
	}

	@Transactional
	public void resetPassword(ResetPasswordRequest request) {
		if (!request.isPasswordsMatch()) {
			throw new AuthException("Passwords do not match");
		}

		PasswordResetToken token = findActiveToken(request.getToken())
				.orElseThrow(() -> new AuthException("Invalid or expired reset link. Please request a new one."));

		User user = userRepository.findById(token.getUserId())
				.orElseThrow(() -> new AuthException("User account not found"));

		user.setPassword(passwordEncoder.encode(request.getPassword()));
		userRepository.save(user);

		token.setUsed(true);
		tokenRepository.save(token);
		tokenRepository.invalidateActiveTokensForUser(user.getId());
	}

	private Optional<PasswordResetToken> findActiveToken(String rawToken) {
		if (rawToken == null || rawToken.isBlank()) {
			return Optional.empty();
		}
		String hash = TokenHashUtil.hashToken(rawToken);
		Optional<PasswordResetToken> tokenOpt = tokenRepository.findByTokenHashAndUsedFalse(hash);
		if (tokenOpt.isEmpty()) {
			return Optional.empty();
		}
		PasswordResetToken token = tokenOpt.get();
		if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
			return Optional.empty();
		}
		return Optional.of(token);
	}

}
