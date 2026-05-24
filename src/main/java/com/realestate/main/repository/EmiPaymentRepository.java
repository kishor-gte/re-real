package com.realestate.main.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.realestate.main.entity.EmiPayment;

public interface EmiPaymentRepository extends JpaRepository<EmiPayment, Long> {

	List<EmiPayment> findByInstallmentPlanIdOrderByInstallmentNumberAsc(Long installmentPlanId);
}
