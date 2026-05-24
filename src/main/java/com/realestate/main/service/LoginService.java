package com.realestate.main.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.realestate.main.dto.request.LoginRequest;
import com.realestate.main.dto.response.LoginResult;
import com.realestate.main.entity.LoginHistory;
import com.realestate.main.entity.User;
import com.realestate.main.entity.UserSession;
import com.realestate.main.entity.enums.AccountStatus;
import com.realestate.main.exception.AuthException;
import com.realestate.main.repository.LoginHistoryRepository;
import com.realestate.main.repository.UserRepository;
import com.realestate.main.repository.UserSessionRepository;
import com.realestate.main.security.JwtCookieHelper;
import com.realestate.main.security.JwtService;
import com.realestate.main.util.ClientInfoUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Service
public class LoginService {

	private final UserRepository userRepository;
	private final LoginHistoryRepository loginHistoryRepository;
	private final UserSessionRepository userSessionRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;

	public LoginService(UserRepository userRepository, LoginHistoryRepository loginHistoryRepository,
			UserSessionRepository userSessionRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
		this.userRepository = userRepository;
		this.loginHistoryRepository = loginHistoryRepository;
		this.userSessionRepository = userSessionRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtService = jwtService;
	}

	@Transactional
	public LoginResult login(LoginRequest request, HttpServletRequest httpRequest) {
		String email = UserLookupService.normalizeEmail(request.getEmail());
		String ip = ClientInfoUtil.getClientIp(httpRequest);
		String ua = ClientInfoUtil.getUserAgent(httpRequest);

		User user = userRepository.findByEmail(email).orElse(null);
		if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
			saveLoginHistory(null, email, ip, ua, false, "Invalid credentials");
			throw new AuthException("Invalid email or password");
		}
		if (!user.isVerified() || user.getAccountStatus() != AccountStatus.ACTIVE) {
			saveLoginHistory(user, email, ip, ua, false, "Account not verified");
			throw new AuthException("Invalid email or password");
		}

		String dbSessionId = UUID.randomUUID().toString();
		long ttlSeconds = jwtService.getExpirationSeconds(request.isRememberMe());

		UserSession userSession = new UserSession();
		userSession.setUser(user);
		userSession.setSessionId(dbSessionId);
		userSession.setIpAddress(ip);
		userSession.setExpiresAt(LocalDateTime.now().plusSeconds(ttlSeconds));
		userSessionRepository.save(userSession);

		String token = jwtService.createToken(user, dbSessionId, request.isRememberMe());
		saveLoginHistory(user, email, ip, ua, true, null);

		return new LoginResult(user, token, ttlSeconds);
	}

	@Transactional
	public void logout(HttpServletRequest request) {
		String token = JwtCookieHelper.resolveToken(request, jwtService.getCookieName());
		if (token != null) {
			jwtService.extractSessionId(token).ifPresent(this::deactivateSession);
		}

		HttpSession session = request.getSession(false);
		if (session != null) {
			session.invalidate();
		}
	}

	private void deactivateSession(String sessionId) {
		userSessionRepository.findBySessionIdAndActiveTrue(sessionId).ifPresent(s -> {
			s.setActive(false);
			userSessionRepository.save(s);
		});
	}

	private void saveLoginHistory(User user, String email, String ip, String ua, boolean success, String reason) {
		LoginHistory history = new LoginHistory();
		history.setUser(user);
		history.setEmail(email);
		history.setIpAddress(ip);
		history.setUserAgent(ua);
		history.setSuccess(success);
		history.setFailureReason(reason);
		loginHistoryRepository.save(history);
	}
}
