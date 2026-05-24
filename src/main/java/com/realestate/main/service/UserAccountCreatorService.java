package com.realestate.main.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.realestate.main.dto.PendingRegistrationData;
import com.realestate.main.entity.User;
import com.realestate.main.entity.enums.AccountStatus;
import com.realestate.main.exception.AuthException;
import com.realestate.main.repository.UserRepository;
import com.realestate.main.util.MobileUtils;
import com.realestate.main.util.ReferralCodeGenerator;

/**
 * Commits the new user in its own transaction so a later OTP/referral error cannot roll back the account.
 */
@Service
public class UserAccountCreatorService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final ReferralCodeGenerator referralCodeGenerator;

	public UserAccountCreatorService(UserRepository userRepository, PasswordEncoder passwordEncoder,
			ReferralCodeGenerator referralCodeGenerator) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.referralCodeGenerator = referralCodeGenerator;
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public User createAndCommit(PendingRegistrationData pending) {
		if (pending.getRole() == null) {
			throw new AuthException("Registration data incomplete. Please register again.");
		}

		for (int attempt = 0; attempt < 5; attempt++) {
			try {
				User user = buildUser(pending);
				User saved = userRepository.saveAndFlush(user);
				if (saved.getId() == null) {
					throw new AuthException("Failed to save account. Please try again.");
				}
				return saved;
			} catch (DataIntegrityViolationException e) {
				String cause = rootMessage(e);
				if (cause.contains("referral_code") || cause.contains("referral")) {
					continue;
				}
				throw e;
			}
		}
		throw new AuthException("Could not generate a unique referral code. Please try again.");
	}

	private User buildUser(PendingRegistrationData pending) {
		User user = new User();
		user.setFullName(pending.getFullName().trim());
		user.setEmail(UserLookupService.normalizeEmail(pending.getEmail()));
		user.setMobile(MobileUtils.normalize(pending.getMobile()));
		user.setPassword(passwordEncoder.encode(pending.getPassword()));
		user.setGender(pending.getGender());
		user.setDateOfBirth(pending.getDateOfBirth());
		user.setRole(pending.getRole());
		user.setAddress(emptyToNull(pending.getAddress()));
		user.setCity(emptyToNull(pending.getCity()));
		user.setState(emptyToNull(pending.getState()));
		user.setPincode(emptyToNull(pending.getPincode()));
		user.setProfileImage(emptyToNull(pending.getProfileImage()));
		user.setReferralCode(generateUniqueReferralCode());
		user.setReferredBy(emptyToNull(pending.getReferralCodeUsed()));
		user.setVerified(true);
		user.setAccountStatus(AccountStatus.ACTIVE);
		return user;
	}

	private String generateUniqueReferralCode() {
		String code;
		do {
			code = referralCodeGenerator.generate(8);
		} while (userRepository.findByReferralCode(code).isPresent());
		return code;
	}

	private String emptyToNull(String value) {
		return value == null || value.isBlank() ? null : value.trim();
	}

	private String rootMessage(Throwable e) {
		Throwable t = e;
		while (t.getCause() != null) {
			t = t.getCause();
		}
		return t.getMessage() != null ? t.getMessage().toLowerCase() : "";
	}
}
