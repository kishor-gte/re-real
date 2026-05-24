package com.realestate.main.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.realestate.main.dto.request.AgentForgotPasswordRequest;
import com.realestate.main.dto.request.AgentResetPasswordRequest;
import com.realestate.main.dto.response.PasswordResetRequestResult;
import com.realestate.main.entity.Agent;
import com.realestate.main.entity.AgentPasswordResetToken;
import com.realestate.main.exception.AuthException;
import com.realestate.main.repository.AgentPasswordResetTokenRepository;
import com.realestate.main.repository.AgentRepository;
import com.realestate.main.util.TokenHashUtil;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class AgentPasswordResetService {

	private final AgentLookupService agentLookup;
	private final AgentRepository agentRepository;
	private final AgentPasswordResetTokenRepository tokenRepository;
	private final EmailService emailService;
	private final PasswordEncoder passwordEncoder;
	private final ApplicationUrlService applicationUrlService;

	@Value("${app.password-reset.expiry-minutes:30}")
	private int expiryMinutes;

	@Value("${app.mail.expose-reset-token-in-response:true}")
	private boolean exposeResetTokenInResponse;

	public AgentPasswordResetService(AgentLookupService agentLookup, AgentRepository agentRepository,
			AgentPasswordResetTokenRepository tokenRepository, EmailService emailService,
			PasswordEncoder passwordEncoder, ApplicationUrlService applicationUrlService) {
		this.agentLookup = agentLookup;
		this.agentRepository = agentRepository;
		this.tokenRepository = tokenRepository;
		this.emailService = emailService;
		this.passwordEncoder = passwordEncoder;
		this.applicationUrlService = applicationUrlService;
	}

	@Transactional
	public PasswordResetRequestResult requestReset(AgentForgotPasswordRequest request, HttpServletRequest httpRequest) {
		Optional<Agent> agentOpt = agentLookup.findVerifiedAgentByEmailAndMobile(request.getEmail(), request.getMobile());
		if (agentOpt.isEmpty()) {
			throw new AuthException("Email and phone number do not match any verified agent account.");
		}

		Agent agent = agentOpt.get();
		tokenRepository.invalidateActiveTokensForAgent(agent.getId());

		String rawToken = TokenHashUtil.generateSecureToken();
		AgentPasswordResetToken entity = new AgentPasswordResetToken();
		entity.setAgentId(agent.getId());
		entity.setTokenHash(TokenHashUtil.hashToken(rawToken));
		entity.setExpiresAt(LocalDateTime.now().plusMinutes(expiryMinutes));
		entity.setUsed(false);
		tokenRepository.save(entity);

		String resetUrl = applicationUrlService.buildAgentResetPasswordUrl(httpRequest, rawToken,
				request.getClientOrigin());

		boolean emailSent = emailService.sendAgentPasswordResetEmail(agent.getEmail(), agent.getFullName(), resetUrl,
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
	public void resetPassword(AgentResetPasswordRequest request) {
		if (!request.isPasswordsMatch()) {
			throw new AuthException("Passwords do not match");
		}

		AgentPasswordResetToken token = findActive(request.getToken())
				.orElseThrow(() -> new AuthException("Invalid or expired reset link. Please request a new one."));

		Agent agent = agentRepository.findById(token.getAgentId())
				.orElseThrow(() -> new AuthException("Agent account not found"));

		agent.setPassword(passwordEncoder.encode(request.getPassword()));
		agent.setFailedLoginAttempts(0);
		agentRepository.save(agent);

		token.setUsed(true);
		tokenRepository.save(token);
		tokenRepository.invalidateActiveTokensForAgent(agent.getId());
	}

	private Optional<AgentPasswordResetToken> findActive(String rawToken) {
		if (rawToken == null || rawToken.isBlank()) {
			return Optional.empty();
		}
		Optional<AgentPasswordResetToken> tokenOpt = tokenRepository
				.findByTokenHashAndUsedFalse(TokenHashUtil.hashToken(rawToken));
		if (tokenOpt.isEmpty()) {
			return Optional.empty();
		}
		AgentPasswordResetToken token = tokenOpt.get();
		if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
			return Optional.empty();
		}
		return Optional.of(token);
	}
}
