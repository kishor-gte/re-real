package com.realestate.main.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.realestate.main.entity.Agent;
import com.realestate.main.entity.enums.AccountStatus;

public interface AgentRepository extends JpaRepository<Agent, Long> {

	Optional<Agent> findByEmail(String email);

	Optional<Agent> findByReferralCode(String referralCode);

	boolean existsByEmail(String email);

	boolean existsByMobile(String mobile);

	boolean existsByReraNumber(String reraNumber);

	boolean existsByReferralCode(String referralCode);

	boolean existsByAgentCode(String agentCode);

	long countByAccountStatus(AccountStatus accountStatus);

	List<Agent> findByAccountStatusOrderByCreatedAtDesc(AccountStatus accountStatus);

	List<Agent> findAllByOrderByCreatedAtDesc();
}
