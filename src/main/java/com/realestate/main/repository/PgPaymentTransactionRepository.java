package com.realestate.main.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.realestate.main.entity.PgPaymentTransaction;

public interface PgPaymentTransactionRepository extends JpaRepository<PgPaymentTransaction, Long> {

	Optional<PgPaymentTransaction> findByRazorpayOrderId(String razorpayOrderId);
}
