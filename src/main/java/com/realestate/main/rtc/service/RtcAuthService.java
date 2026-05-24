package com.realestate.main.rtc.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.realestate.main.entity.User;
import com.realestate.main.entity.UserSession;
import com.realestate.main.exception.AuthException;
import com.realestate.main.repository.AgentRepository;
import com.realestate.main.repository.UserRepository;
import com.realestate.main.repository.UserSessionRepository;
import com.realestate.main.rtc.dto.RtcPrincipal;
import com.realestate.main.rtc.enums.RtcParticipantType;
import com.realestate.main.security.JwtCookieHelper;
import com.realestate.main.security.JwtService;
import com.realestate.main.security.JwtUserPrincipal;
import com.realestate.main.util.AgentSessionConstants;
import com.realestate.main.util.PgOwnerSessionConstants;
import com.realestate.main.util.SessionConstants;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Service
public class RtcAuthService {

	private final AgentRepository agentRepository;
	private final UserRepository userRepository;
	private final UserSessionRepository userSessionRepository;
	private final JwtService jwtService;

	public RtcAuthService(AgentRepository agentRepository, UserRepository userRepository,
			UserSessionRepository userSessionRepository, JwtService jwtService) {
		this.agentRepository = agentRepository;
		this.userRepository = userRepository;
		this.userSessionRepository = userSessionRepository;
		this.jwtService = jwtService;
	}

	public RtcPrincipal requirePrincipal(HttpSession session) {
		RtcPrincipal p = resolvePrincipal(session);
		if (p == null) {
			throw new AuthException("Please sign in to use chat and voice calls");
		}
		return p;
	}

	/**
	 * Resolves principal for HTTP or WebSocket handshake (cookie, query token, Bearer).
	 */
	public RtcPrincipal resolveForRequest(HttpServletRequest request) {
		if (request == null) {
			return resolvePrincipal(null);
		}
		syncUserSessionFromJwt(request);
		return resolvePrincipal(request.getSession(false));
	}

	public RtcPrincipal resolveFromBearer(String bearerToken, HttpSession session) {
		if (bearerToken == null || bearerToken.isBlank()) {
			return resolvePrincipal(session);
		}
		Optional<RtcPrincipal> fromJwt = principalFromJwt(bearerToken.trim());
		if (fromJwt.isPresent() && session != null) {
			User user = userRepository.findById(fromJwt.get().getId()).orElse(null);
			if (user != null) {
				syncUserSession(session, user, jwtService.extractSessionId(bearerToken).orElse(""));
			}
		}
		return fromJwt.orElseGet(() -> resolvePrincipal(session));
	}

	public void syncUserSessionFromJwt(HttpServletRequest request) {
		String paramToken = request.getParameter("token");
		String token = (paramToken != null && !paramToken.isBlank()) ? paramToken
				: JwtCookieHelper.resolveToken(request, jwtService.getCookieName());
		if (token == null || token.isBlank()) {
			return;
		}
		final String jwtToken = token;
		principalFromJwt(jwtToken).ifPresent(p -> {
			User user = userRepository.findById(p.getId()).orElse(null);
			if (user != null) {
				HttpSession session = request.getSession(true);
				syncUserSession(session, user, jwtService.extractSessionId(jwtToken).orElse(""));
			}
		});
	}

	public RtcPrincipal resolvePrincipal(HttpSession session) {
		RtcPrincipal fromSession = principalFromHttpSession(session);
		if (fromSession != null) {
			return fromSession;
		}
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null && auth.getPrincipal() instanceof JwtUserPrincipal jwt) {
			return new RtcPrincipal(RtcParticipantType.USER, jwt.getUserId(), jwt.getFullName());
		}
		return null;
	}

	private RtcPrincipal principalFromHttpSession(HttpSession session) {
		if (session == null) {
			return null;
		}
		Long agentId = (Long) session.getAttribute(AgentSessionConstants.AGENT_ID);
		if (agentId != null) {
			String name = (String) session.getAttribute(AgentSessionConstants.AGENT_NAME);
			if (name == null) {
				name = agentRepository.findById(agentId).map(a -> a.getFullName()).orElse("Agent");
			}
			return new RtcPrincipal(RtcParticipantType.AGENT, agentId, name);
		}
		Long pgOwnerId = (Long) session.getAttribute(PgOwnerSessionConstants.PG_OWNER_ID);
		if (pgOwnerId != null) {
			String name = (String) session.getAttribute(PgOwnerSessionConstants.PG_OWNER_NAME);
			if (name == null || name.isBlank()) {
				name = "PG Owner";
			}
			return new RtcPrincipal(RtcParticipantType.PG_OWNER, pgOwnerId, name);
		}
		Long userId = (Long) session.getAttribute(SessionConstants.USER_ID);
		if (userId != null) {
			String name = (String) session.getAttribute(SessionConstants.USER_NAME);
			if (name == null) {
				name = userRepository.findById(userId).map(u -> u.getFullName()).orElse("User");
			}
			return new RtcPrincipal(RtcParticipantType.USER, userId, name);
		}
		return null;
	}

	private Optional<RtcPrincipal> principalFromJwt(String token) {
		Optional<Claims> claimsOpt = jwtService.parseClaims(token);
		if (claimsOpt.isEmpty()) {
			return Optional.empty();
		}
		Claims claims = claimsOpt.get();
		String sessionId = claims.get(JwtService.CLAIM_SESSION_ID, String.class);
		Long userId = claims.get(JwtService.CLAIM_USER_ID, Long.class);
		if (sessionId == null || userId == null) {
			return Optional.empty();
		}
		Optional<UserSession> dbSession = userSessionRepository.findBySessionIdAndActiveTrue(sessionId);
		if (dbSession.isEmpty() || dbSession.get().getExpiresAt().isBefore(LocalDateTime.now())) {
			return Optional.empty();
		}
		Optional<User> userOpt = userRepository.findById(userId);
		if (userOpt.isEmpty() || !userOpt.get().isVerified()) {
			return Optional.empty();
		}
		User user = userOpt.get();
		return Optional.of(new RtcPrincipal(RtcParticipantType.USER, user.getId(), user.getFullName()));
	}

	private void syncUserSession(HttpSession session, User user, String sessionId) {
		session.setAttribute(SessionConstants.USER_ID, user.getId());
		session.setAttribute(SessionConstants.USER_NAME, user.getFullName());
		session.setAttribute(SessionConstants.USER_EMAIL, user.getEmail());
		session.setAttribute(SessionConstants.USER_ROLE, user.getRole().name());
		if (sessionId != null && !sessionId.isBlank()) {
			session.setAttribute(SessionConstants.DB_SESSION_ID, sessionId);
		}
	}

	public static String callTopicFor(RtcParticipantType type, Long id) {
		return "/topic/rtc.call." + type.name() + "-" + id;
	}
}
