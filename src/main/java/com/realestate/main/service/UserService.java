package com.realestate.main.service;

import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.realestate.main.dto.response.UserResponse;
import com.realestate.main.entity.User;
import com.realestate.main.exception.AuthException;
import com.realestate.main.repository.UserRepository;
import com.realestate.main.security.JwtUserPrincipal;

import jakarta.servlet.http.HttpServletRequest;

@Service
@Transactional(readOnly = true)
public class UserService {

	private final UserRepository userRepository;
	private final UserLookupService userLookup;

	public UserService(UserRepository userRepository, UserLookupService userLookup) {
		this.userRepository = userRepository;
		this.userLookup = userLookup;
	}

	public Optional<User> findById(Long id) {
		return userRepository.findById(id);
	}

	public Optional<User> findByEmail(String email) {
		return userRepository.findByEmail(UserLookupService.normalizeEmail(email));
	}

	public User getById(Long id) {
		return userRepository.findById(id)
				.orElseThrow(() -> new AuthException("User not found"));
	}

	public User getByEmail(String email) {
		return userRepository.findByEmail(UserLookupService.normalizeEmail(email))
				.orElseThrow(() -> new AuthException("User not found"));
	}

	public boolean isEmailAvailable(String email) {
		return !userLookup.isEmailRegistered(email);
	}

	public boolean isMobileAvailable(String mobile) {
		return !userLookup.isMobileRegistered(mobile);
	}

	public boolean existsByEmail(String email) {
		return userLookup.isEmailRegistered(email);
	}

	public boolean existsByMobile(String mobile) {
		return userLookup.isMobileRegistered(mobile);
	}

	public UserResponse toUserResponse(User user) {
		return UserResponse.from(user);
	}

	public UserResponse getLoggedInUserResponse(HttpServletRequest request) {
		return UserResponse.from(getLoggedInUser());
	}

	public User getLoggedInUser() {
		JwtUserPrincipal principal = currentPrincipal();
		return userRepository.findById(principal.getUserId())
				.orElseThrow(() -> new AuthException("Not logged in"));
	}

	public JwtUserPrincipal currentPrincipal() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null && auth.getPrincipal() instanceof JwtUserPrincipal principal) {
			return principal;
		}
		throw new AuthException("Not logged in");
	}

	public Optional<User> findByReferralCode(String code) {
		if (code == null || code.isBlank()) {
			return Optional.empty();
		}
		return userRepository.findByReferralCode(code.trim().toUpperCase());
	}
}
