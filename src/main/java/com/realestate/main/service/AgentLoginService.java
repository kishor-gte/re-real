package com.realestate.main.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.realestate.main.dto.request.AgentLoginRequest;
import com.realestate.main.entity.Agent;
import com.realestate.main.entity.AgentLoginHistory;
import com.realestate.main.entity.AgentSession;
import com.realestate.main.entity.enums.AccountStatus;
import com.realestate.main.exception.AuthException;
import com.realestate.main.repository.AgentLoginHistoryRepository;
import com.realestate.main.repository.AgentRepository;
import com.realestate.main.repository.AgentSessionRepository;
import com.realestate.main.util.AgentSessionConstants;
import com.realestate.main.util.ClientInfoUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Service
public class AgentLoginService {

	private final AgentRepository agentRepository;
	private final AgentLoginHistoryRepository loginHistoryRepository;
	private final AgentSessionRepository agentSessionRepository;
	private final PasswordEncoder passwordEncoder;

	@Value("${app.agent.session-timeout-minutes:120}")
	private int sessionTimeoutMinutes;

	@Value("${app.agent.max-login-attempts:5}")
	private int maxLoginAttempts;

	public AgentLoginService(AgentRepository agentRepository, AgentLoginHistoryRepository loginHistoryRepository,
			AgentSessionRepository agentSessionRepository, PasswordEncoder passwordEncoder) {
		this.agentRepository = agentRepository;
		this.loginHistoryRepository = loginHistoryRepository;
		this.agentSessionRepository = agentSessionRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Transactional
	public Agent login(AgentLoginRequest request, HttpServletRequest httpRequest) {
		String email = AgentLookupService.normalizeEmail(request.getEmail());
		String ip = ClientInfoUtil.getClientIp(httpRequest);
		String ua = ClientInfoUtil.getUserAgent(httpRequest);

		Agent agent = agentRepository.findByEmail(email).orElse(null);
		if (agent == null || !passwordEncoder.matches(request.getPassword(), agent.getPassword())) {
			if (agent != null) {
				handleFailedAttempt(agent);
			}
			saveHistory(agent, email, ip, ua, false, "Invalid credentials");
			throw new AuthException("Invalid email or password");
		}

		if (agent.getAccountStatus() == AccountStatus.LOCKED) {
			saveHistory(agent, email, ip, ua, false, "Account locked");
			throw new AuthException("Account locked due to multiple failed attempts. Contact support.");
		}
		if (!agent.isVerified()) {
			saveHistory(agent, email, ip, ua, false, "Email not verified");
			throw new AuthException("Invalid email or password");
		}
		if (agent.getAccountStatus() == AccountStatus.PENDING) {
			saveHistory(agent, email, ip, ua, false, "Pending admin approval");
			throw new AuthException(
					"Your account is pending admin approval. You will be able to sign in once an administrator approves your application.");
		}
		if (agent.getAccountStatus() == AccountStatus.DISABLED) {
			saveHistory(agent, email, ip, ua, false, "Account disabled");
			String reason = agent.getRejectionReason();
			if (reason != null && !reason.isBlank()) {
				throw new AuthException("Your agent application was not approved. Reason: " + reason);
			}
			throw new AuthException("Your agent account is not active. Please contact support.");
		}
		if (agent.getAccountStatus() != AccountStatus.ACTIVE) {
			saveHistory(agent, email, ip, ua, false, "Account not active");
			throw new AuthException("Invalid email or password");
		}

		agent.setFailedLoginAttempts(0);
		agent.setLastLogin(LocalDateTime.now());
		agentRepository.save(agent);

		String dbSessionId = UUID.randomUUID().toString();
		long ttlMinutes = request.isRememberMe() ? sessionTimeoutMinutes * 4 : sessionTimeoutMinutes;

		AgentSession session = new AgentSession();
		session.setAgent(agent);
		session.setSessionId(dbSessionId);
		session.setIpAddress(ip);
		session.setExpiresAt(LocalDateTime.now().plusMinutes(ttlMinutes));
		agentSessionRepository.save(session);

		HttpSession httpSession = httpRequest.getSession(true);
		httpSession.setMaxInactiveInterval((int) (ttlMinutes * 60));
		httpSession.setAttribute(AgentSessionConstants.AGENT_ID, agent.getId());
		httpSession.setAttribute(AgentSessionConstants.AGENT_NAME, agent.getFullName());
		httpSession.setAttribute(AgentSessionConstants.AGENT_EMAIL, agent.getEmail());
		httpSession.setAttribute(AgentSessionConstants.AGENT_CODE, agent.getAgentCode());
		httpSession.setAttribute(AgentSessionConstants.AGENT_REFERRAL_CODE, agent.getReferralCode());
		httpSession.setAttribute(AgentSessionConstants.DB_SESSION_ID, dbSessionId);

		saveHistory(agent, email, ip, ua, true, null);
		return agent;
	}

	@Transactional
	public void logout(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			Object sid = session.getAttribute(AgentSessionConstants.DB_SESSION_ID);
			if (sid != null) {
				agentSessionRepository.findBySessionIdAndActiveTrue(sid.toString()).ifPresent(s -> {
					s.setActive(false);
					agentSessionRepository.save(s);
				});
			}
			session.invalidate();
		}
	}

	private void handleFailedAttempt(Agent agent) {
		int attempts = agent.getFailedLoginAttempts() + 1;
		agent.setFailedLoginAttempts(attempts);
		if (attempts >= maxLoginAttempts) {
			agent.setAccountStatus(AccountStatus.LOCKED);
		}
		agentRepository.save(agent);
	}

	private void saveHistory(Agent agent, String email, String ip, String ua, boolean success, String reason) {
		AgentLoginHistory history = new AgentLoginHistory();
		history.setAgent(agent);
		history.setEmail(email);
		history.setIpAddress(ip);
		history.setUserAgent(ua);
		history.setSuccess(success);
		history.setFailureReason(reason);
		loginHistoryRepository.save(history);
	}
}
