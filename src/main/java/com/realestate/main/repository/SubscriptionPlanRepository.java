package com.realestate.main.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.realestate.main.entity.SubscriptionPlan;
import com.realestate.main.entity.enums.SubscriptionPlanStatus;

public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlan, Long> {

	Optional<SubscriptionPlan> findByPlanCode(String planCode);

	List<SubscriptionPlan> findAllByStatusOrderBySortOrderAsc(SubscriptionPlanStatus status);

	List<SubscriptionPlan> findAllByOrderBySortOrderAsc();
}
