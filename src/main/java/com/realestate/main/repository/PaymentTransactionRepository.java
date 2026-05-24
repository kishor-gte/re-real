package com.realestate.main.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.realestate.main.entity.PaymentTransaction;

public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {

	List<PaymentTransaction> findByBookingIdOrderByCreatedAtDesc(Long bookingId);

	Optional<PaymentTransaction> findByRazorpayOrderId(String razorpayOrderId);

	Optional<PaymentTransaction> findByRazorpayPaymentId(String razorpayPaymentId);
}
