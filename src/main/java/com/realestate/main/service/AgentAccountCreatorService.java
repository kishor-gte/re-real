package com.realestate.main.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.realestate.main.dto.PendingAgentRegistrationData;
import com.realestate.main.entity.Agent;
import com.realestate.main.entity.AgentReferral;
import com.realestate.main.entity.enums.AccountStatus;
import com.realestate.main.repository.AgentReferralRepository;
import com.realestate.main.repository.AgentRepository;
import com.realestate.main.util.AgentIdentifiers;

@Service
public class AgentAccountCreatorService {

	private final AgentRepository agentRepository;
	private final AgentReferralRepository agentReferralRepository;
	private final PasswordEncoder passwordEncoder;

	public AgentAccountCreatorService(AgentRepository agentRepository, AgentReferralRepository agentReferralRepository,
			PasswordEncoder passwordEncoder) {
		this.agentRepository = agentRepository;
		this.agentReferralRepository = agentReferralRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Agent createAndCommit(PendingAgentRegistrationData pending) {
		Agent agent = new Agent();
		agent.setAgentCode("PENDING");
		agent.setFullName(pending.getFullName());
		agent.setEmail(pending.getEmail());
		agent.setMobile(pending.getMobile());
		agent.setAgencyName(pending.getAgencyName());
		agent.setReraNumber(pending.getReraNumber());
		agent.setExperience(pending.getExperience());
		agent.setSpecialization(pending.getSpecialization());
		agent.setOfficeAddress(pending.getOfficeAddress());
		agent.setCity(pending.getCity());
		agent.setState(pending.getState());
		agent.setPincode(pending.getPincode());
		agent.setPassword(passwordEncoder.encode(pending.getPassword()));
		agent.setProfilePhoto(pending.getProfilePhoto());
		agent.setAgencyLogo(pending.getAgencyLogo());
		agent.setGovernmentId(pending.getGovernmentId());
		agent.setReferralCode(generateUniqueReferralCode());
		agent.setReferredByAgentId(pending.getReferrerAgentId());
		agent.setVerified(true);
		agent.setAccountStatus(AccountStatus.PENDING);
		agent.setFailedLoginAttempts(0);

		agent = agentRepository.saveAndFlush(agent);
		agent.setAgentCode(AgentIdentifiers.formatAgentCode(agent.getId()));
		agent = agentRepository.saveAndFlush(agent);

		if (pending.getReferrerAgentId() != null) {
			Agent referrer = agentRepository.findById(pending.getReferrerAgentId()).orElse(null);
			if (referrer != null) {
				AgentReferral referral = new AgentReferral();
				referral.setReferrerAgent(referrer);
				referral.setReferredAgent(agent);
				referral.setReferralCodeUsed(pending.getUsedReferralCode());
				agentReferralRepository.save(referral);
			}
		}
		return agent;
	}

	private String generateUniqueReferralCode() {
		for (int i = 0; i < 20; i++) {
			String code = AgentIdentifiers.generateReferralCode();
			if (!agentRepository.existsByReferralCode(code)) {
				return code;
			}
		}
		throw new com.realestate.main.exception.AuthException("Could not generate referral code. Please try again.");
	}
}
