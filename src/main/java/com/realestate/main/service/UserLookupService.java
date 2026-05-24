package com.realestate.main.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.realestate.main.entity.User;
import com.realestate.main.repository.UserRepository;
import com.realestate.main.util.MobileUtils;

/**
 * Reads email/mobile availability only from the {@code users} table (not OTP or other tables).
 */
@Service
public class UserLookupService {

	private static final Logger log = LoggerFactory.getLogger(UserLookupService.class);

	private final UserRepository userRepository;

	public UserLookupService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public boolean isEmailRegistered(String email) {
		if (email == null || email.isBlank()) {
			return false;
		}
		return userRepository.countByEmailInUsers(normalizeEmail(email)) > 0;
	}

	public boolean isMobileRegistered(String mobile) {
		String normalized = MobileUtils.normalize(mobile);
		if (!MobileUtils.isValidTenDigit(normalized)) {
			return false;
		}
		long exact = userRepository.countByMobileInUsers(normalized);
		long legacy = userRepository.countByMobileDigitsLegacy(normalized);
		log.debug("users table mobile check: digits={}, exact={}, legacy={}", normalized, exact, legacy);
		return exact > 0 || legacy > 0;
	}

	public Optional<User> findUserByEmail(String email) {
		if (email == null || email.isBlank()) {
			return Optional.empty();
		}
		return userRepository.findByEmail(normalizeEmail(email));
	}

	/**
	 * Returns a verified user only when email and mobile belong to the same account.
	 */
	public Optional<User> findVerifiedUserByEmailAndMobile(String email, String mobile) {
		if (email == null || email.isBlank() || mobile == null || mobile.isBlank()) {
			return Optional.empty();
		}
		String normalizedMobile = MobileUtils.normalize(mobile);
		if (!MobileUtils.isValidTenDigit(normalizedMobile)) {
			return Optional.empty();
		}

		Optional<User> byEmail = findUserByEmail(email);
		if (byEmail.isPresent()) {
			User user = byEmail.get();
			if (user.isVerified() && mobileMatchesUser(user, normalizedMobile)) {
				return Optional.of(user);
			}
			return Optional.empty();
		}

		return findUsersByMobileDigits(mobile).stream()
				.filter(User::isVerified)
				.filter(u -> u.getEmail() != null && normalizeEmail(email).equals(normalizeEmail(u.getEmail())))
				.findFirst();
	}

	private boolean mobileMatchesUser(User user, String normalizedMobile) {
		if (user.getMobile() == null) {
			return false;
		}
		return normalizedMobile.equals(MobileUtils.normalize(user.getMobile()));
	}

	public List<User> findUsersByMobileDigits(String mobile) {
		String normalized = MobileUtils.normalize(mobile);
		if (!MobileUtils.isValidTenDigit(normalized)) {
			return List.of();
		}

		List<User> result = new ArrayList<>();
		userRepository.findByMobile(normalized).ifPresent(result::add);

		List<Long> ids = userRepository.findUserIdsByMobileDigits(normalized);
		for (Long id : ids) {
			userRepository.findById(id).ifPresent(u -> {
				if (result.stream().noneMatch(x -> x.getId().equals(u.getId()))) {
					result.add(u);
				}
			});
		}
		return result;
	}

	public Optional<String> mobileConflictForRegistration(String pendingEmail, String mobile) {
		List<User> owners = findUsersByMobileDigits(mobile);
		if (owners.isEmpty()) {
			return Optional.empty();
		}

		String email = normalizeEmail(pendingEmail);
		for (User owner : owners) {
			if (owner.getEmail() != null && !owner.getEmail().equalsIgnoreCase(email)) {
				log.warn("Mobile {} taken by user id={} email={}", MobileUtils.normalize(mobile), owner.getId(),
						owner.getEmail());
				return Optional.of("This phone number is already registered with another account. Please login.");
			}
		}
		return Optional.empty();
	}

	public static String normalizeEmail(String email) {
		if (email == null) {
			return "";
		}
		return email.toLowerCase().trim();
	}
}
