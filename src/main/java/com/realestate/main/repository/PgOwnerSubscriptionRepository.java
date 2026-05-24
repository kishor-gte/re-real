package com.realestate.main.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.realestate.main.entity.PgOwnerSubscription;
import com.realestate.main.entity.enums.AgentSubscriptionStatus;

public interface PgOwnerSubscriptionRepository extends JpaRepository<PgOwnerSubscription, Long> {

	@Query("SELECT s FROM PgOwnerSubscription s WHERE s.pgOwnerId = :pgOwnerId AND s.subscriptionStatus = :status "
			+ "AND s.expiryDate > :now ORDER BY s.expiryDate DESC")
	Optional<PgOwnerSubscription> findTopActive(@Param("pgOwnerId") Long pgOwnerId,
			@Param("status") AgentSubscriptionStatus status, @Param("now") LocalDateTime now);

	default Optional<PgOwnerSubscription> findActiveForPgOwner(Long pgOwnerId, LocalDateTime now) {
		return findTopActive(pgOwnerId, AgentSubscriptionStatus.ACTIVE, now);
	}

	List<PgOwnerSubscription> findByPgOwnerIdOrderByCreatedAtDesc(Long pgOwnerId);

	Optional<PgOwnerSubscription> findByRazorpayOrderId(String razorpayOrderId);

	@Query("SELECT s FROM PgOwnerSubscription s WHERE s.subscriptionStatus = :status AND s.expiryDate <= :before")
	List<PgOwnerSubscription> findExpiringOrExpired(@Param("status") AgentSubscriptionStatus status,
			@Param("before") LocalDateTime before);

	long countBySubscriptionStatus(AgentSubscriptionStatus status);
}
