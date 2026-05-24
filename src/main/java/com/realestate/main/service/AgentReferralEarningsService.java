package com.realestate.main.service;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.realestate.main.dto.response.AgentReferralEarningsResponse;
import com.realestate.main.dto.response.AgentReferralEarningsResponse.ReferralRow;
import com.realestate.main.entity.Agent;
import com.realestate.main.entity.AgentReferral;
import com.realestate.main.entity.enums.AccountStatus;
import com.realestate.main.repository.AgentReferralRepository;
import com.realestate.main.repository.AgentRepository;

@Service
public class AgentReferralEarningsService {

	private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");

	private final AgentReferralRepository agentReferralRepository;
	private final AgentRepository agentRepository;

	@Value("${app.agent.referral-commission:500}")
	private BigDecimal referralCommission;

	public AgentReferralEarningsService(AgentReferralRepository agentReferralRepository,
			AgentRepository agentRepository) {
		this.agentReferralRepository = agentReferralRepository;
		this.agentRepository = agentRepository;
	}

	@Transactional(readOnly = true)
	public AgentReferralEarningsResponse loadReferralEarnings(Long agentId) {
		Agent agent = agentRepository.findById(agentId).orElseThrow();
		List<AgentReferral> referrals = agentReferralRepository.findByReferrerAgentIdWithDetails(agentId);

		AgentReferralEarningsResponse r = new AgentReferralEarningsResponse();
		r.setReferralCode(agent.getReferralCode());
		r.setTotalReferrals(referrals.size());

		BigDecimal total = BigDecimal.ZERO;
		BigDecimal pending = BigDecimal.ZERO;
		BigDecimal paid = BigDecimal.ZERO;
		List<ReferralRow> rows = new ArrayList<>();

		for (AgentReferral ref : referrals) {
			Agent referred = ref.getReferredAgent();
			ReferralRow row = new ReferralRow();
			row.setId(ref.getId());
			row.setReferredAgentName(referred.getFullName());
			row.setReferredAgentEmail(referred.getEmail());
			row.setReferredAgentCode(referred.getAgentCode());
			row.setStatus(ref.getStatus());
			row.setReferralCodeUsed(ref.getReferralCodeUsed());
			row.setRegisteredAt(ref.getCreatedAt() != null ? ref.getCreatedAt().format(DATE_FMT) : "—");

			row.setCommissionAmount(referralCommission);
			if (referred.getAccountStatus() == AccountStatus.ACTIVE) {
				row.setCommissionStatus("CREDITED");
				paid = paid.add(referralCommission);
			} else {
				row.setCommissionStatus("PENDING");
				pending = pending.add(referralCommission);
			}
			total = total.add(referralCommission);
			rows.add(row);
		}

		r.setReferrals(rows);
		r.setTotalEarnings(total);
		r.setPendingEarnings(pending);
		r.setPaidEarnings(paid);
		return r;
	}
}
