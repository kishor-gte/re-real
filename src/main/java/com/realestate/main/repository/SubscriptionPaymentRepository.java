package com.realestate.main.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.realestate.main.entity.SubscriptionPayment;
import com.realestate.main.entity.enums.SubscriptionPaymentStatus;

public interface SubscriptionPaymentRepository extends JpaRepository<SubscriptionPayment, Long> {

	List<SubscriptionPayment> findByAgentIdOrderByCreatedAtDesc(Long agentId);

	List<SubscriptionPayment> findAllByOrderByCreatedAtDesc();

	Optional<SubscriptionPayment> findByRazorpayOrderId(String razorpayOrderId);

	long countByPaymentStatus(SubscriptionPaymentStatus status);
}
