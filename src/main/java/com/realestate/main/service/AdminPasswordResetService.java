package com.realestate.main.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.realestate.main.dto.request.AdminForgotPasswordRequest;
import com.realestate.main.dto.request.AdminResetPasswordRequest;
import com.realestate.main.dto.response.PasswordResetRequestResult;
import com.realestate.main.entity.Admin;
import com.realestate.main.entity.AdminPasswordResetToken;
import com.realestate.main.exception.AuthException;
import com.realestate.main.repository.AdminPasswordResetTokenRepository;
import com.realestate.main.repository.AdminRepository;
import com.realestate.main.util.TokenHashUtil;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class AdminPasswordResetService {

	private final AdminLookupService adminLookup;
	private final AdminRepository adminRepository;
	private final AdminPasswordResetTokenRepository tokenRepository;
	private final EmailService emailService;
	private final PasswordEncoder passwordEncoder;
	private final ApplicationUrlService applicationUrlService;

	@Value("${app.password-reset.expiry-minutes:30}")
	private int expiryMinutes;

	@Value("${app.mail.expose-reset-token-in-response:true}")
	private boolean exposeResetTokenInResponse;

	public AdminPasswordResetService(AdminLookupService adminLookup, AdminRepository adminRepository,
			AdminPasswordResetTokenRepository tokenRepository, EmailService emailService,
			PasswordEncoder passwordEncoder, ApplicationUrlService applicationUrlService) {
		this.adminLookup = adminLookup;
		this.adminRepository = adminRepository;
		this.tokenRepository = tokenRepository;
		this.emailService = emailService;
		this.passwordEncoder = passwordEncoder;
		this.applicationUrlService = applicationUrlService;
	}

	@Transactional
	public PasswordResetRequestResult requestReset(AdminForgotPasswordRequest request, HttpServletRequest httpRequest) {
		Optional<Admin> adminOpt = adminLookup.findVerifiedAdminByEmailAndMobile(request.getOfficialEmail(),
				request.getMobile());
		if (adminOpt.isEmpty()) {
			throw new AuthException("Email and phone number do not match any verified admin account.");
		}

		Admin admin = adminOpt.get();
		tokenRepository.invalidateActiveTokensForAdmin(admin.getId());

		String rawToken = TokenHashUtil.generateSecureToken();
		AdminPasswordResetToken entity = new AdminPasswordResetToken();
		entity.setAdminId(admin.getId());
		entity.setTokenHash(TokenHashUtil.hashToken(rawToken));
		entity.setExpiresAt(LocalDateTime.now().plusMinutes(expiryMinutes));
		entity.setUsed(false);
		tokenRepository.save(entity);

		String resetUrl = applicationUrlService.buildAdminResetPasswordUrl(httpRequest, rawToken,
				request.getClientOrigin());

		boolean emailSent = emailService.sendAdminPasswordResetEmail(admin.getOfficialEmail(), admin.getFullName(),
				resetUrl, expiryMinutes);

		String message = emailSent ? "Password reset link sent to your official email."
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
	public void resetPassword(AdminResetPasswordRequest request) {
		if (!request.isPasswordsMatch()) {
			throw new AuthException("Passwords do not match");
		}

		AdminPasswordResetToken token = findActive(request.getToken())
				.orElseThrow(() -> new AuthException("Invalid or expired reset link. Please request a new one."));

		Admin admin = adminRepository.findById(token.getAdminId())
				.orElseThrow(() -> new AuthException("Admin account not found"));

		admin.setPassword(passwordEncoder.encode(request.getPassword()));
		admin.setFailedLoginAttempts(0);
		adminRepository.save(admin);

		token.setUsed(true);
		tokenRepository.save(token);
		tokenRepository.invalidateActiveTokensForAdmin(admin.getId());
	}

	private Optional<AdminPasswordResetToken> findActive(String rawToken) {
		if (rawToken == null || rawToken.isBlank()) {
			return Optional.empty();
		}
		Optional<AdminPasswordResetToken> tokenOpt = tokenRepository
				.findByTokenHashAndUsedFalse(TokenHashUtil.hashToken(rawToken));
		if (tokenOpt.isEmpty()) {
			return Optional.empty();
		}
		AdminPasswordResetToken token = tokenOpt.get();
		if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
			return Optional.empty();
		}
		return Optional.of(token);
	}
}
