package com.realestate.main.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.realestate.main.dto.request.PgOwnerLoginRequest;
import com.realestate.main.entity.PgOwner;
import com.realestate.main.entity.PgOwnerLoginHistory;
import com.realestate.main.entity.PgOwnerSession;
import com.realestate.main.entity.enums.AccountStatus;
import com.realestate.main.exception.AuthException;
import com.realestate.main.repository.PgOwnerLoginHistoryRepository;
import com.realestate.main.repository.PgOwnerRepository;
import com.realestate.main.repository.PgOwnerSessionRepository;
import com.realestate.main.util.ClientInfoUtil;
import com.realestate.main.util.PgOwnerSessionConstants;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Service
public class PgOwnerLoginService {

	private final PgOwnerRepository pgOwnerRepository;
	private final PgOwnerLoginHistoryRepository loginHistoryRepository;
	private final PgOwnerSessionRepository pgOwnerSessionRepository;
	private final PasswordEncoder passwordEncoder;

	@Value("${app.pg-owner.session-timeout-minutes:120}")
	private int sessionTimeoutMinutes;

	@Value("${app.pg-owner.max-login-attempts:5}")
	private int maxLoginAttempts;

	public PgOwnerLoginService(PgOwnerRepository pgOwnerRepository,
			PgOwnerLoginHistoryRepository loginHistoryRepository,
			PgOwnerSessionRepository pgOwnerSessionRepository, PasswordEncoder passwordEncoder) {
		this.pgOwnerRepository = pgOwnerRepository;
		this.loginHistoryRepository = loginHistoryRepository;
		this.pgOwnerSessionRepository = pgOwnerSessionRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Transactional
	public PgOwner login(PgOwnerLoginRequest request, HttpServletRequest httpRequest) {
		String email = PgOwnerLookupService.normalizeEmail(request.getEmail());
		String ip = ClientInfoUtil.getClientIp(httpRequest);
		String ua = ClientInfoUtil.getUserAgent(httpRequest);

		PgOwner owner = pgOwnerRepository.findByEmail(email).orElse(null);
		if (owner == null || !passwordEncoder.matches(request.getPassword(), owner.getPassword())) {
			if (owner != null) {
				handleFailedAttempt(owner);
			}
			saveHistory(owner, email, ip, ua, false, "Invalid credentials");
			throw new AuthException("Invalid email or password");
		}

		if (owner.getAccountStatus() == AccountStatus.LOCKED) {
			saveHistory(owner, email, ip, ua, false, "Account locked");
			throw new AuthException("Account locked due to multiple failed attempts. Contact support.");
		}
		if (!owner.isVerified()) {
			saveHistory(owner, email, ip, ua, false, "Email not verified");
			throw new AuthException("Invalid email or password");
		}
		if (owner.getAccountStatus() == AccountStatus.PENDING) {
			saveHistory(owner, email, ip, ua, false, "Pending admin approval");
			throw new AuthException(
					"Your account is pending admin approval. You will be able to sign in once an administrator approves your application.");
		}
		if (owner.getAccountStatus() == AccountStatus.DISABLED) {
			saveHistory(owner, email, ip, ua, false, "Account disabled");
			String reason = owner.getRejectionReason();
			if (reason != null && !reason.isBlank()) {
				throw new AuthException("Your PG owner application was not approved. Reason: " + reason);
			}
			throw new AuthException("Your PG owner account is not active. Please contact support.");
		}
		if (owner.getAccountStatus() != AccountStatus.ACTIVE) {
			saveHistory(owner, email, ip, ua, false, "Account not active");
			throw new AuthException("Invalid email or password");
		}

		owner.setFailedLoginAttempts(0);
		owner.setLastLogin(LocalDateTime.now());
		pgOwnerRepository.save(owner);

		String dbSessionId = UUID.randomUUID().toString();
		long ttlMinutes = request.isRememberMe() ? sessionTimeoutMinutes * 4 : sessionTimeoutMinutes;

		PgOwnerSession session = new PgOwnerSession();
		session.setPgOwner(owner);
		session.setSessionId(dbSessionId);
		session.setIpAddress(ip);
		session.setExpiresAt(LocalDateTime.now().plusMinutes(ttlMinutes));
		pgOwnerSessionRepository.save(session);

		HttpSession httpSession = httpRequest.getSession(true);
		httpSession.setMaxInactiveInterval((int) (ttlMinutes * 60));
		httpSession.setAttribute(PgOwnerSessionConstants.PG_OWNER_ID, owner.getId());
		httpSession.setAttribute(PgOwnerSessionConstants.PG_OWNER_NAME, owner.getFullName());
		httpSession.setAttribute(PgOwnerSessionConstants.PG_OWNER_EMAIL, owner.getEmail());
		httpSession.setAttribute(PgOwnerSessionConstants.PG_OWNER_CODE, owner.getPgOwnerCode());
		httpSession.setAttribute(PgOwnerSessionConstants.PG_NAME, owner.getPgName());
		httpSession.setAttribute(PgOwnerSessionConstants.DB_SESSION_ID, dbSessionId);

		saveHistory(owner, email, ip, ua, true, null);
		return owner;
	}

	@Transactional
	public void logout(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			Object sid = session.getAttribute(PgOwnerSessionConstants.DB_SESSION_ID);
			if (sid != null) {
				pgOwnerSessionRepository.findBySessionIdAndActiveTrue(sid.toString()).ifPresent(s -> {
					s.setActive(false);
					pgOwnerSessionRepository.save(s);
				});
			}
			session.invalidate();
		}
	}

	private void handleFailedAttempt(PgOwner owner) {
		int attempts = owner.getFailedLoginAttempts() + 1;
		owner.setFailedLoginAttempts(attempts);
		if (attempts >= maxLoginAttempts) {
			owner.setAccountStatus(AccountStatus.LOCKED);
		}
		pgOwnerRepository.save(owner);
	}

	private void saveHistory(PgOwner owner, String email, String ip, String ua, boolean success, String reason) {
		PgOwnerLoginHistory history = new PgOwnerLoginHistory();
		history.setPgOwner(owner);
		history.setEmail(email);
		history.setIpAddress(ip);
		history.setUserAgent(ua);
		history.setSuccess(success);
		history.setFailureReason(reason);
		loginHistoryRepository.save(history);
	}
}
