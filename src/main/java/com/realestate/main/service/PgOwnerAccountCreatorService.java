package com.realestate.main.service;

import java.util.UUID;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.realestate.main.dto.PendingPgOwnerRegistrationData;
import com.realestate.main.entity.PgOwner;
import com.realestate.main.entity.enums.AccountStatus;
import com.realestate.main.entity.enums.GenderAllowed;
import com.realestate.main.entity.enums.PgType;
import com.realestate.main.exception.AuthException;
import com.realestate.main.repository.PgOwnerRepository;
import com.realestate.main.util.PgOwnerIdentifiers;

@Service
public class PgOwnerAccountCreatorService {

	private final PgOwnerRepository pgOwnerRepository;
	private final PasswordEncoder passwordEncoder;

	public PgOwnerAccountCreatorService(PgOwnerRepository pgOwnerRepository, PasswordEncoder passwordEncoder) {
		this.pgOwnerRepository = pgOwnerRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public PgOwner createAndCommit(PendingPgOwnerRegistrationData pending) {
		PgOwner owner = new PgOwner();
		owner.setPgOwnerCode(uniquePlaceholderCode());
		owner.setFullName(pending.getFullName());
		owner.setEmail(pending.getEmail());
		owner.setMobile(pending.getMobile());
		owner.setPgName(defaultPgName(pending));
		owner.setBusinessRegistrationNumber(pending.getBusinessRegistrationNumber());
		owner.setExperience(pending.getExperience());
		owner.setPgType(pending.getPgType() != null ? pending.getPgType() : PgType.CO_LIVING);
		owner.setGenderAllowed(pending.getGenderAllowed() != null ? pending.getGenderAllowed() : GenderAllowed.BOTH);
		owner.setOfficeAddress(defaultOfficeAddress(pending));
		owner.setCity(pending.getCity());
		owner.setState(pending.getState());
		owner.setPincode(pending.getPincode());
		owner.setPassword(passwordEncoder.encode(pending.getPassword()));
		owner.setProfilePhoto(pending.getProfilePhoto());
		owner.setGovernmentId(pending.getGovernmentId());
		owner.setReferralCodeUsed(pending.getReferralCodeUsed());
		owner.setVerified(true);
		owner.setAccountStatus(AccountStatus.PENDING);
		owner.setFailedLoginAttempts(0);

		try {
			owner = pgOwnerRepository.saveAndFlush(owner);
			owner.setPgOwnerCode(PgOwnerIdentifiers.formatPgOwnerCode(owner.getId()));
			return pgOwnerRepository.saveAndFlush(owner);
		} catch (DataIntegrityViolationException e) {
			throw mapIntegrityException(e);
		}
	}

	private String uniquePlaceholderCode() {
		for (int attempt = 0; attempt < 5; attempt++) {
			String code = "PG-TMP-" + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
			if (!pgOwnerRepository.existsByPgOwnerCode(code)) {
				return code;
			}
		}
		throw new AuthException("Could not allocate owner ID. Please try again.");
	}

	private String defaultPgName(PendingPgOwnerRegistrationData pending) {
		if (pending.getPgName() != null && !pending.getPgName().isBlank()) {
			return pending.getPgName().trim();
		}
		return pending.getFullName().trim();
	}

	private String defaultOfficeAddress(PendingPgOwnerRegistrationData pending) {
		if (pending.getOfficeAddress() != null && !pending.getOfficeAddress().isBlank()) {
			return pending.getOfficeAddress().trim();
		}
		return pending.getCity() + ", " + pending.getState();
	}

	private AuthException mapIntegrityException(DataIntegrityViolationException e) {
		String msg = rootMessage(e);
		if (isDuplicateKey(msg, "email")) {
			return new AuthException("This email is already registered. Please sign in.");
		}
		if (isDuplicateKey(msg, "mobile")) {
			return new AuthException("This mobile number is already registered. Please sign in or use another number.");
		}
		if (isDuplicateKey(msg, "pg_owner_code")) {
			return new AuthException("Registration could not be completed. Please try verifying OTP again.");
		}
		if (msg.contains("doesn't have a default value") || msg.contains("cannot be null")) {
			return new AuthException("Registration could not be saved. Please register again from the start.");
		}
		return new AuthException("Could not complete registration. Please try again.");
	}

	private boolean isDuplicateKey(String msg, String column) {
		if (msg == null || msg.isBlank()) {
			return false;
		}
		boolean duplicate = msg.contains("duplicate") || msg.contains("unique constraint")
				|| msg.contains("unique key");
		return duplicate && msg.contains(column);
	}

	private String rootMessage(Throwable e) {
		Throwable t = e;
		while (t.getCause() != null) {
			t = t.getCause();
		}
		return t.getMessage() != null ? t.getMessage().toLowerCase() : "";
	}
}
