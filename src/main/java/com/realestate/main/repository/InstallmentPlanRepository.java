package com.realestate.main.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.realestate.main.entity.InstallmentPlan;

public interface InstallmentPlanRepository extends JpaRepository<InstallmentPlan, Long> {

	Optional<InstallmentPlan> findByBookingId(Long bookingId);
}
