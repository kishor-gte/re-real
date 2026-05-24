package com.realestate.main.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.realestate.main.entity.SubscriptionNotification;

public interface SubscriptionNotificationRepository extends JpaRepository<SubscriptionNotification, Long> {

	List<SubscriptionNotification> findTop20ByAgentIdOrderByCreatedAtDesc(Long agentId);

	List<SubscriptionNotification> findByAgentIdOrderByCreatedAtDesc(Long agentId);
}
