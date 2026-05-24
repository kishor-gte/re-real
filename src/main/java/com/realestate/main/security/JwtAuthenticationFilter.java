package com.realestate.main.security;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.realestate.main.entity.User;
import com.realestate.main.entity.UserSession;
import com.realestate.main.repository.UserRepository;
import com.realestate.main.repository.UserSessionRepository;
import com.realestate.main.util.SessionConstants;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtService jwtService;
	private final UserRepository userRepository;
	private final UserSessionRepository userSessionRepository;

	public JwtAuthenticationFilter(JwtService jwtService, UserRepository userRepository,
			UserSessionRepository userSessionRepository) {
		this.jwtService = jwtService;
		this.userRepository = userRepository;
		this.userSessionRepository = userSessionRepository;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		if (SecurityContextHolder.getContext().getAuthentication() != null) {
			filterChain.doFilter(request, response);
			return;
		}

		String token = JwtCookieHelper.resolveToken(request, jwtService.getCookieName());
		if (token != null) {
			authenticateToken(token, request);
		}

		filterChain.doFilter(request, response);
	}

	private void authenticateToken(String token, HttpServletRequest request) {
		Optional<Claims> claimsOpt = jwtService.parseClaims(token);
		if (claimsOpt.isEmpty()) {
			return;
		}
		Claims claims = claimsOpt.get();
		String sessionId = claims.get(JwtService.CLAIM_SESSION_ID, String.class);
		Long userId = claims.get(JwtService.CLAIM_USER_ID, Long.class);

		if (sessionId == null || userId == null) {
			return;
		}

		Optional<UserSession> dbSession = userSessionRepository.findBySessionIdAndActiveTrue(sessionId);
		if (dbSession.isEmpty() || dbSession.get().getExpiresAt().isBefore(LocalDateTime.now())) {
			return;
		}

		Optional<User> userOpt = userRepository.findById(userId);
		if (userOpt.isEmpty() || !userOpt.get().isVerified()) {
			return;
		}

		User user = userOpt.get();
		JwtUserPrincipal principal = JwtUserPrincipal.from(user, sessionId);
		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(principal, null,
				principal.getAuthorities());
		authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
		SecurityContextHolder.getContext().setAuthentication(authentication);

		syncHttpSession(request, user, sessionId);
	}

	private void syncHttpSession(HttpServletRequest request, User user, String sessionId) {
		HttpSession session = request.getSession(true);
		session.setAttribute(SessionConstants.USER_ID, user.getId());
		session.setAttribute(SessionConstants.USER_NAME, user.getFullName());
		session.setAttribute(SessionConstants.USER_EMAIL, user.getEmail());
		session.setAttribute(SessionConstants.USER_ROLE, user.getRole().name());
		session.setAttribute(SessionConstants.DB_SESSION_ID, sessionId);
	}
}
