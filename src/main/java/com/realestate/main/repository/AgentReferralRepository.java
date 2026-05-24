package com.realestate.main.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.realestate.main.entity.AgentReferral;

public interface AgentReferralRepository extends JpaRepository<AgentReferral, Long> {

	long countByReferrerAgentId(Long referrerAgentId);

	@Query("SELECT r FROM AgentReferral r JOIN FETCH r.referredAgent JOIN FETCH r.referrerAgent "
			+ "WHERE r.referrerAgent.id = :agentId ORDER BY r.createdAt DESC")
	List<AgentReferral> findByReferrerAgentIdWithDetails(@Param("agentId") Long agentId);
}
