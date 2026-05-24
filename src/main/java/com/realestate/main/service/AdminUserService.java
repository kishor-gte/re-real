package com.realestate.main.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.realestate.main.dto.response.UserListItemResponse;
import com.realestate.main.entity.User;
import com.realestate.main.entity.enums.AccountStatus;
import com.realestate.main.entity.enums.UserRole;
import com.realestate.main.exception.AuthException;
import com.realestate.main.repository.UserRepository;

@Service
public class AdminUserService {

	private final UserRepository userRepository;

	public AdminUserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Transactional(readOnly = true)
	public List<UserListItemResponse> listUsers(String roleFilter, String statusFilter, String query) {
		String q = normalizeQuery(query);
		return userRepository.findAllByOrderByCreatedAtDesc().stream()
				.filter(user -> matchesRole(user, roleFilter))
				.filter(user -> matchesStatus(user, statusFilter))
				.filter(user -> matchesQuery(user, q))
				.map(this::toListItem)
				.collect(Collectors.toList());
	}

	private boolean matchesRole(User user, String roleFilter) {
		if (roleFilter == null || roleFilter.isBlank() || "ALL".equalsIgnoreCase(roleFilter)) {
			return true;
		}
		try {
			return user.getRole() == UserRole.valueOf(roleFilter.trim().toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new AuthException("Invalid role filter");
		}
	}

	private boolean matchesStatus(User user, String statusFilter) {
		if (statusFilter == null || statusFilter.isBlank() || "ALL".equalsIgnoreCase(statusFilter)) {
			return true;
		}
		try {
			return user.getAccountStatus() == AccountStatus.valueOf(statusFilter.trim().toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new AuthException("Invalid status filter");
		}
	}

	private boolean matchesQuery(User user, String q) {
		if (q.isEmpty()) {
			return true;
		}
		return contains(user.getFullName(), q)
				|| contains(user.getEmail(), q)
				|| contains(user.getMobile(), q)
				|| contains(user.getCity(), q)
				|| contains(user.getState(), q)
				|| contains(user.getReferralCode(), q)
				|| (user.getRole() != null && contains(user.getRole().name(), q));
	}

	private boolean contains(String value, String q) {
		return value != null && value.toLowerCase().contains(q);
	}

	private String normalizeQuery(String query) {
		return query == null ? "" : query.trim().toLowerCase();
	}

	private UserListItemResponse toListItem(User user) {
		UserListItemResponse item = new UserListItemResponse();
		item.setId(user.getId());
		item.setFullName(user.getFullName());
		item.setEmail(user.getEmail());
		item.setMobile(user.getMobile());
		item.setRole(user.getRole());
		item.setCity(user.getCity());
		item.setState(user.getState());
		item.setAccountStatus(user.getAccountStatus());
		item.setVerified(user.isVerified());
		item.setCreatedAt(user.getCreatedAt());
		return item;
	}
}
