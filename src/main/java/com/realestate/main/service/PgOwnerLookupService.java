package com.realestate.main.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.realestate.main.entity.Agent;
import com.realestate.main.entity.PgOwner;
import com.realestate.main.entity.enums.AccountStatus;
import com.realestate.main.exception.AuthException;
import com.realestate.main.repository.AgentRepository;
import com.realestate.main.repository.PgOwnerRepository;
import com.realestate.main.util.MobileUtils;

@Service
public class PgOwnerLookupService {

	private static final Logger log = LoggerFactory.getLogger(PgOwnerLookupService.class);

	private final PgOwnerRepository pgOwnerRepository;
	private final AgentRepository agentRepository;
	private final PgOwnerApplicationCleanupService applicationCleanup;

	public PgOwnerLookupService(PgOwnerRepository pgOwnerRepository, AgentRepository agentRepository,
			PgOwnerApplicationCleanupService applicationCleanup) {
		this.pgOwnerRepository = pgOwnerRepository;
		this.agentRepository = agentRepository;
		this.applicationCleanup = applicationCleanup;
	}

	public static String normalizeEmail(String email) {
		if (email == null) {
			return "";
		}
		return email.toLowerCase().trim();
	}

	/** True only when an approved/locked PG owner account already uses this email. */
	public boolean isEmailRegistered(String email) {
		return findByEmail(email)
				.filter(PgOwnerLookupService::blocksNewRegistration)
				.isPresent();
	}

	/** True only when an approved/locked PG owner account already uses this mobile. */
	public boolean isMobileRegistered(String mobile) {
		String normalized = MobileUtils.normalize(mobile);
		if (!MobileUtils.isValidTenDigit(normalized)) {
			return false;
		}
		return findByMobile(normalized)
				.filter(PgOwnerLookupService::blocksNewRegistration)
				.isPresent();
	}

	/**
	 * Clears incomplete/rejected PG owner rows so the same person can register again.
	 * Blocks only ACTIVE or LOCKED accounts.
	 */
	public void prepareForRegistration(String email, String mobile) {
		String normalizedEmail = normalizeEmail(email);
		String normalizedMobile = MobileUtils.normalize(mobile);

		Optional<PgOwner> byEmail = pgOwnerRepository.findByEmail(normalizedEmail);
		if (byEmail.isPresent()) {
			PgOwner owner = byEmail.get();
			if (blocksNewRegistration(owner)) {
				if (owner.getAccountStatus() == AccountStatus.LOCKED) {
					throw new AuthException("This PG owner account is locked. Contact support.");
				}
				throw new AuthException("This email is already registered. Please sign in to your PG owner account.");
			}
			applicationCleanup.removeIncompleteApplication(owner);
			log.info("Removed prior PG owner application for {} (status={})", normalizedEmail,
					owner.getAccountStatus());
		}

		Optional<PgOwner> byMobile = pgOwnerRepository.findByMobile(normalizedMobile);
		if (byMobile.isPresent()) {
			PgOwner owner = byMobile.get();
			if (normalizedEmail.equals(normalizeEmail(owner.getEmail()))) {
				return;
			}
			if (blocksNewRegistration(owner)) {
				if (owner.getAccountStatus() == AccountStatus.LOCKED) {
					throw new AuthException("This mobile number is linked to a locked PG owner account.");
				}
				throw new AuthException(
						"This mobile number is already registered. Please sign in or use another number.");
			}
			throw new AuthException(
					"This mobile number is already used in another PG owner application. Use a different number or contact support.");
		}
	}

	public boolean isReferralCodeValid(String referralCode) {
		if (referralCode == null || referralCode.isBlank()) {
			return true;
		}
		return findReferrerAgentByCode(referralCode).isPresent();
	}

	public Optional<PgOwner> findByEmail(String email) {
		return pgOwnerRepository.findByEmail(normalizeEmail(email));
	}

	public Optional<PgOwner> findByMobile(String mobile) {
		String normalized = MobileUtils.normalize(mobile);
		if (!MobileUtils.isValidTenDigit(normalized)) {
			return Optional.empty();
		}
		return pgOwnerRepository.findByMobile(normalized);
	}

	public Optional<Agent> findReferrerAgentByCode(String referralCode) {
		if (referralCode == null || referralCode.isBlank()) {
			return Optional.empty();
		}
		return agentRepository.findByReferralCode(referralCode.trim().toUpperCase())
				.filter(a -> a.getAccountStatus() == AccountStatus.ACTIVE);
	}

	public Optional<PgOwner> findVerifiedPgOwnerByEmailAndMobile(String email, String mobile) {
		String normalizedEmail = normalizeEmail(email);
		String normalizedMobile = MobileUtils.normalize(mobile);
		if (!MobileUtils.isValidTenDigit(normalizedMobile)) {
			return Optional.empty();
		}
		return findByEmail(normalizedEmail)
				.filter(PgOwner::isVerified)
				.filter(o -> o.getAccountStatus() == AccountStatus.ACTIVE)
				.filter(o -> normalizedMobile.equals(MobileUtils.normalize(o.getMobile())));
	}

	/** Removes broken partial rows left by failed OTP activation (placeholder owner codes). */
	public void removePlaceholderApplicationIfPresent(String email, String mobile) {
		String normalizedEmail = normalizeEmail(email);
		String normalizedMobile = MobileUtils.normalize(mobile);

		findByEmail(normalizedEmail)
				.filter(PgOwnerLookupService::isPlaceholderApplication)
				.ifPresent(owner -> {
					applicationCleanup.removeIncompleteApplication(owner);
					log.info("Removed placeholder PG owner row for email {}", normalizedEmail);
				});

		findByMobile(normalizedMobile)
				.filter(PgOwnerLookupService::isPlaceholderApplication)
				.filter(owner -> !normalizedEmail.equals(normalizeEmail(owner.getEmail())))
				.ifPresent(owner -> {
					applicationCleanup.removeIncompleteApplication(owner);
					log.info("Removed placeholder PG owner row for mobile {}", normalizedMobile);
				});
	}

	public Optional<String> mobileConflictForRegistration(String pendingEmail, String mobile) {
		String normalizedEmail = normalizeEmail(pendingEmail);
		String normalizedMobile = MobileUtils.normalize(mobile);
		if (!MobileUtils.isValidTenDigit(normalizedMobile)) {
			return Optional.empty();
		}
		Optional<PgOwner> existing = findByMobile(normalizedMobile);
		if (existing.isEmpty()) {
			return Optional.empty();
		}
		PgOwner owner = existing.get();
		if (normalizedEmail.equals(normalizeEmail(owner.getEmail()))) {
			return Optional.empty();
		}
		if (isPlaceholderApplication(owner)) {
			return Optional.empty();
		}
		if (blocksNewRegistration(owner)) {
			return Optional.of("This mobile number is already registered. Please sign in or use another number.");
		}
		return Optional.of(
				"This mobile number is already used in another PG owner application. Use a different number or contact support.");
	}

	static boolean isPlaceholderApplication(PgOwner owner) {
		String code = owner.getPgOwnerCode();
		return code == null || "PENDING".equalsIgnoreCase(code) || code.startsWith("PG-TMP-");
	}

	static boolean isCompletePendingApplication(PgOwner owner) {
		return owner.getAccountStatus() == AccountStatus.PENDING
				&& owner.isVerified()
				&& !isPlaceholderApplication(owner);
	}

	private static boolean blocksNewRegistration(PgOwner owner) {
		AccountStatus status = owner.getAccountStatus();
		return status == AccountStatus.ACTIVE || status == AccountStatus.LOCKED;
	}
}
