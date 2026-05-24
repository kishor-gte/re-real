package com.realestate.main.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.realestate.main.entity.PgSubscriptionPayment;
import com.realestate.main.entity.enums.SubscriptionPaymentStatus;

public interface PgSubscriptionPaymentRepository extends JpaRepository<PgSubscriptionPayment, Long> {

	List<PgSubscriptionPayment> findByPgOwnerIdOrderByCreatedAtDesc(Long pgOwnerId);

	List<PgSubscriptionPayment> findAllByOrderByCreatedAtDesc();

	Optional<PgSubscriptionPayment> findByRazorpayOrderId(String razorpayOrderId);

	long countByPaymentStatus(SubscriptionPaymentStatus status);
}
