package com.realestate.main.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.realestate.main.entity.AgentSubscription;
import com.realestate.main.entity.enums.AgentSubscriptionStatus;

public interface AgentSubscriptionRepository extends JpaRepository<AgentSubscription, Long> {

	@Query("SELECT s FROM AgentSubscription s WHERE s.agentId = :agentId AND s.subscriptionStatus = :status "
			+ "AND s.expiryDate > :now ORDER BY s.expiryDate DESC")
	Optional<AgentSubscription> findTopActive(@Param("agentId") Long agentId,
			@Param("status") AgentSubscriptionStatus status, @Param("now") LocalDateTime now);

	default Optional<AgentSubscription> findActiveForAgent(Long agentId, LocalDateTime now) {
		return findTopActive(agentId, AgentSubscriptionStatus.ACTIVE, now);
	}

	List<AgentSubscription> findByAgentIdOrderByCreatedAtDesc(Long agentId);

	Optional<AgentSubscription> findByRazorpayOrderId(String razorpayOrderId);

	@Query("SELECT s FROM AgentSubscription s WHERE s.subscriptionStatus = :status AND s.expiryDate <= :before")
	List<AgentSubscription> findExpiringOrExpired(@Param("status") AgentSubscriptionStatus status,
			@Param("before") LocalDateTime before);

	long countBySubscriptionStatus(AgentSubscriptionStatus status);

	@Query("SELECT s FROM AgentSubscription s WHERE s.subscriptionStatus = :status ORDER BY s.createdAt DESC")
	List<AgentSubscription> findAllBySubscriptionStatus(@Param("status") AgentSubscriptionStatus status);
}
