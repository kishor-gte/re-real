package com.realestate.main.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.realestate.main.dto.PendingAgentRegistrationData;
import com.realestate.main.entity.Agent;
import com.realestate.main.entity.enums.AccountStatus;
import com.realestate.main.repository.AgentRepository;
import com.realestate.main.util.MobileUtils;

@Service
public class AgentLookupService {

	private final AgentRepository agentRepository;

	public AgentLookupService(AgentRepository agentRepository) {
		this.agentRepository = agentRepository;
	}

	public static String normalizeEmail(String email) {
		if (email == null) {
			return "";
		}
		return email.toLowerCase().trim();
	}

	public boolean isEmailRegistered(String email) {
		return agentRepository.existsByEmail(normalizeEmail(email));
	}

	public boolean isMobileRegistered(String mobile) {
		String normalized = MobileUtils.normalize(mobile);
		if (!MobileUtils.isValidTenDigit(normalized)) {
			return false;
		}
		return agentRepository.existsByMobile(normalized);
	}

	public boolean isReraRegistered(String reraNumber) {
		if (reraNumber == null || reraNumber.isBlank()) {
			return false;
		}
		return agentRepository.existsByReraNumber(PendingAgentRegistrationData.normalizeRera(reraNumber));
	}

	public boolean isReferralCodeValid(String referralCode) {
		if (referralCode == null || referralCode.isBlank()) {
			return true;
		}
		return agentRepository.findByReferralCode(referralCode.trim().toUpperCase()).isPresent();
	}

	public Optional<Agent> findByEmail(String email) {
		return agentRepository.findByEmail(normalizeEmail(email));
	}

	public Optional<Agent> findReferrerByCode(String referralCode) {
		if (referralCode == null || referralCode.isBlank()) {
			return Optional.empty();
		}
		return agentRepository.findByReferralCode(referralCode.trim().toUpperCase())
				.filter(a -> a.getAccountStatus() == AccountStatus.ACTIVE);
	}

	public Optional<Agent> findVerifiedAgentByEmailAndMobile(String email, String mobile) {
		String normalizedEmail = normalizeEmail(email);
		String normalizedMobile = MobileUtils.normalize(mobile);
		if (!MobileUtils.isValidTenDigit(normalizedMobile)) {
			return Optional.empty();
		}
		return findByEmail(normalizedEmail)
				.filter(Agent::isVerified)
				.filter(a -> a.getAccountStatus() == AccountStatus.ACTIVE)
				.filter(a -> normalizedMobile.equals(MobileUtils.normalize(a.getMobile())));
	}
}
