package com.realestate.main.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.realestate.main.dto.request.AdminAgentRejectRequest;
import com.realestate.main.dto.response.AgentDetailResponse;
import com.realestate.main.dto.response.AgentListItemResponse;
import com.realestate.main.entity.Agent;
import com.realestate.main.entity.enums.AccountStatus;
import com.realestate.main.exception.AuthException;
import com.realestate.main.repository.AgentRepository;

@Service
public class AdminAgentService {

	private final AgentRepository agentRepository;
	private final EmailService emailService;

	public AdminAgentService(AgentRepository agentRepository, EmailService emailService) {
		this.agentRepository = agentRepository;
		this.emailService = emailService;
	}

	@Transactional(readOnly = true)
	public List<AgentListItemResponse> listAgents(String statusFilter) {
		List<Agent> agents;
		if (statusFilter == null || statusFilter.isBlank() || "ALL".equalsIgnoreCase(statusFilter)) {
			agents = agentRepository.findAllByOrderByCreatedAtDesc();
		} else {
			AccountStatus status = parseStatus(statusFilter);
			agents = agentRepository.findByAccountStatusOrderByCreatedAtDesc(status);
		}
		return agents.stream().map(this::toListItem).collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public AgentDetailResponse getAgentDetail(Long agentId) {
		Agent agent = agentRepository.findById(agentId)
				.orElseThrow(() -> new AuthException("Agent not found"));
		return toDetail(agent);
	}

	@Transactional(readOnly = true)
	public long countPendingApproval() {
		return agentRepository.countByAccountStatus(AccountStatus.PENDING);
	}

	@Transactional
	public AgentDetailResponse approveAgent(Long agentId, Long adminId) {
		Agent agent = agentRepository.findById(agentId)
				.orElseThrow(() -> new AuthException("Agent not found"));
		if (agent.getAccountStatus() != AccountStatus.PENDING) {
			throw new AuthException("Only agents awaiting approval can be approved");
		}
		if (!agent.isVerified()) {
			throw new AuthException("Agent email is not verified yet");
		}

		agent.setAccountStatus(AccountStatus.ACTIVE);
		agent.setApprovedAt(LocalDateTime.now());
		agent.setApprovedByAdminId(adminId);
		agent.setRejectionReason(null);
		agent.setFailedLoginAttempts(0);
		agentRepository.save(agent);

		emailService.sendAgentApprovedEmail(agent.getEmail(), agent.getFullName(), agent.getAgentCode());
		return toDetail(agent);
	}

	@Transactional
	public AgentDetailResponse rejectAgent(Long agentId, Long adminId, AdminAgentRejectRequest request) {
		Agent agent = agentRepository.findById(agentId)
				.orElseThrow(() -> new AuthException("Agent not found"));
		if (agent.getAccountStatus() != AccountStatus.PENDING) {
			throw new AuthException("Only agents awaiting approval can be rejected");
		}

		agent.setAccountStatus(AccountStatus.DISABLED);
		agent.setRejectionReason(request.getReason().trim());
		agent.setApprovedByAdminId(adminId);
		agent.setApprovedAt(LocalDateTime.now());
		agentRepository.save(agent);

		emailService.sendAgentRejectedEmail(agent.getEmail(), agent.getFullName(), request.getReason().trim());
		return toDetail(agent);
	}

	@Transactional
	public AgentDetailResponse suspendAgent(Long agentId) {
		Agent agent = agentRepository.findById(agentId)
				.orElseThrow(() -> new AuthException("Agent not found"));
		if (agent.getAccountStatus() != AccountStatus.ACTIVE) {
			throw new AuthException("Only active agents can be suspended");
		}
		agent.setAccountStatus(AccountStatus.DISABLED);
		agentRepository.save(agent);
		return toDetail(agent);
	}

	@Transactional
	public AgentDetailResponse reactivateAgent(Long agentId, Long adminId) {
		Agent agent = agentRepository.findById(agentId)
				.orElseThrow(() -> new AuthException("Agent not found"));
		if (agent.getAccountStatus() != AccountStatus.DISABLED) {
			throw new AuthException("Only disabled agents can be reactivated");
		}
		agent.setAccountStatus(AccountStatus.ACTIVE);
		agent.setApprovedAt(LocalDateTime.now());
		agent.setApprovedByAdminId(adminId);
		agent.setRejectionReason(null);
		agent.setFailedLoginAttempts(0);
		agentRepository.save(agent);
		emailService.sendAgentApprovedEmail(agent.getEmail(), agent.getFullName(), agent.getAgentCode());
		return toDetail(agent);
	}

	private AccountStatus parseStatus(String filter) {
		try {
			return AccountStatus.valueOf(filter.trim().toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new AuthException("Invalid status filter");
		}
	}

	private AgentListItemResponse toListItem(Agent agent) {
		AgentListItemResponse item = new AgentListItemResponse();
		item.setId(agent.getId());
		item.setAgentCode(agent.getAgentCode());
		item.setFullName(agent.getFullName());
		item.setEmail(agent.getEmail());
		item.setMobile(agent.getMobile());
		item.setAgencyName(agent.getAgencyName());
		item.setReraNumber(agent.getReraNumber());
		item.setSpecialization(agent.getSpecialization());
		item.setCity(agent.getCity());
		item.setState(agent.getState());
		item.setAccountStatus(agent.getAccountStatus());
		item.setVerified(agent.isVerified());
		item.setCreatedAt(agent.getCreatedAt());
		return item;
	}

	private AgentDetailResponse toDetail(Agent agent) {
		AgentDetailResponse detail = new AgentDetailResponse();
		AgentListItemResponse base = toListItem(agent);
		detail.setId(base.getId());
		detail.setAgentCode(base.getAgentCode());
		detail.setFullName(base.getFullName());
		detail.setEmail(base.getEmail());
		detail.setMobile(base.getMobile());
		detail.setAgencyName(base.getAgencyName());
		detail.setReraNumber(base.getReraNumber());
		detail.setSpecialization(base.getSpecialization());
		detail.setCity(base.getCity());
		detail.setState(base.getState());
		detail.setAccountStatus(base.getAccountStatus());
		detail.setVerified(base.isVerified());
		detail.setCreatedAt(base.getCreatedAt());
		detail.setExperience(agent.getExperience());
		detail.setOfficeAddress(agent.getOfficeAddress());
		detail.setPincode(agent.getPincode());
		detail.setProfilePhoto(agent.getProfilePhoto());
		detail.setAgencyLogo(agent.getAgencyLogo());
		detail.setGovernmentId(agent.getGovernmentId());
		detail.setReferralCode(agent.getReferralCode());
		detail.setReferredByAgentId(agent.getReferredByAgentId());
		detail.setApprovedAt(agent.getApprovedAt());
		detail.setApprovedByAdminId(agent.getApprovedByAdminId());
		detail.setRejectionReason(agent.getRejectionReason());
		detail.setLastLogin(agent.getLastLogin());
		detail.setUpdatedAt(agent.getUpdatedAt());
		return detail;
	}
}
