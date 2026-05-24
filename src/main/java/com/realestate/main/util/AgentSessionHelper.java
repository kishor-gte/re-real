package com.realestate.main.util;

import com.realestate.main.entity.Agent;
import com.realestate.main.entity.enums.AccountStatus;
import com.realestate.main.exception.AuthException;
import com.realestate.main.repository.AgentRepository;

import jakarta.servlet.http.HttpSession;

public final class AgentSessionHelper {

	private AgentSessionHelper() {
	}

	public static Long requireAgentId(HttpSession session) {
		Long agentId = (Long) session.getAttribute(AgentSessionConstants.AGENT_ID);
		if (agentId == null) {
			throw new AuthException("Please sign in as an agent");
		}
		return agentId;
	}

	public static Agent requireActiveAgent(HttpSession session, AgentRepository agentRepository) {
		Long agentId = requireAgentId(session);
		Agent agent = agentRepository.findById(agentId)
				.orElseThrow(() -> new AuthException("Agent account not found"));
		if (agent.getAccountStatus() != AccountStatus.ACTIVE) {
			throw new AuthException("Your agent account is not active");
		}
		return agent;
	}
}
