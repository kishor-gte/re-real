package com.realestate.main.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.realestate.main.dto.request.AdminLoginRequest;
import com.realestate.main.entity.Admin;
import com.realestate.main.entity.AdminLoginHistory;
import com.realestate.main.entity.AdminSession;
import com.realestate.main.entity.enums.AccountStatus;
import com.realestate.main.exception.AuthException;
import com.realestate.main.repository.AdminLoginHistoryRepository;
import com.realestate.main.repository.AdminRepository;
import com.realestate.main.repository.AdminSessionRepository;
import com.realestate.main.util.AdminSessionConstants;
import com.realestate.main.util.ClientInfoUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Service
public class AdminLoginService {

	private final AdminRepository adminRepository;
	private final AdminLoginHistoryRepository loginHistoryRepository;
	private final AdminSessionRepository adminSessionRepository;
	private final PasswordEncoder passwordEncoder;

	@Value("${app.admin.session-timeout-minutes:120}")
	private int sessionTimeoutMinutes;

	@Value("${app.admin.max-login-attempts:5}")
	private int maxLoginAttempts;

	public AdminLoginService(AdminRepository adminRepository, AdminLoginHistoryRepository loginHistoryRepository,
			AdminSessionRepository adminSessionRepository, PasswordEncoder passwordEncoder) {
		this.adminRepository = adminRepository;
		this.loginHistoryRepository = loginHistoryRepository;
		this.adminSessionRepository = adminSessionRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Transactional
	public Admin login(AdminLoginRequest request, HttpServletRequest httpRequest) {
		String email = AdminLookupService.normalizeEmail(request.getOfficialEmail());
		String ip = ClientInfoUtil.getClientIp(httpRequest);
		String ua = ClientInfoUtil.getUserAgent(httpRequest);

		Admin admin = adminRepository.findByOfficialEmail(email).orElse(null);
		if (admin == null || !passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
			if (admin != null) {
				handleFailedAttempt(admin);
			}
			saveHistory(admin, email, ip, ua, false, "Invalid credentials");
			throw new AuthException("Invalid admin email or password");
		}

		if (admin.getAccountStatus() == AccountStatus.LOCKED) {
			saveHistory(admin, email, ip, ua, false, "Account locked");
			throw new AuthException("Account locked due to multiple failed attempts. Contact support.");
		}
		if (!admin.isVerified() || admin.getAccountStatus() != AccountStatus.ACTIVE) {
			saveHistory(admin, email, ip, ua, false, "Account not active");
			throw new AuthException("Invalid admin email or password");
		}

		admin.setFailedLoginAttempts(0);
		admin.setLastLogin(LocalDateTime.now());
		adminRepository.save(admin);

		String dbSessionId = UUID.randomUUID().toString();
		long ttlMinutes = request.isRememberMe() ? sessionTimeoutMinutes * 4 : sessionTimeoutMinutes;

		AdminSession session = new AdminSession();
		session.setAdmin(admin);
		session.setSessionId(dbSessionId);
		session.setIpAddress(ip);
		session.setExpiresAt(LocalDateTime.now().plusMinutes(ttlMinutes));
		adminSessionRepository.save(session);

		HttpSession httpSession = httpRequest.getSession(true);
		httpSession.setMaxInactiveInterval((int) (ttlMinutes * 60));
		httpSession.setAttribute(AdminSessionConstants.ADMIN_ID, admin.getId());
		httpSession.setAttribute(AdminSessionConstants.ADMIN_NAME, admin.getFullName());
		httpSession.setAttribute(AdminSessionConstants.ADMIN_EMAIL, admin.getOfficialEmail());
		httpSession.setAttribute(AdminSessionConstants.ADMIN_ROLE, admin.getAdminRole().name());
		httpSession.setAttribute(AdminSessionConstants.ADMIN_EMPLOYEE_ID, admin.getEmployeeId());
		httpSession.setAttribute(AdminSessionConstants.DB_SESSION_ID, dbSessionId);

		saveHistory(admin, email, ip, ua, true, null);
		return admin;
	}

	@Transactional
	public void logout(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			Object sid = session.getAttribute(AdminSessionConstants.DB_SESSION_ID);
			if (sid != null) {
				adminSessionRepository.findBySessionIdAndActiveTrue(sid.toString()).ifPresent(s -> {
					s.setActive(false);
					adminSessionRepository.save(s);
				});
			}
			session.invalidate();
		}
	}

	private void handleFailedAttempt(Admin admin) {
		int attempts = admin.getFailedLoginAttempts() + 1;
		admin.setFailedLoginAttempts(attempts);
		if (attempts >= maxLoginAttempts) {
			admin.setAccountStatus(AccountStatus.LOCKED);
		}
		adminRepository.save(admin);
	}

	private void saveHistory(Admin admin, String email, String ip, String ua, boolean success, String reason) {
		AdminLoginHistory history = new AdminLoginHistory();
		history.setAdmin(admin);
		history.setOfficialEmail(email);
		history.setIpAddress(ip);
		history.setUserAgent(ua);
		history.setSuccess(success);
		history.setFailureReason(reason);
		loginHistoryRepository.save(history);
	}
}
