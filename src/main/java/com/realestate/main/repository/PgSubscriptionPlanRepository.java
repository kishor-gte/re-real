package com.realestate.main.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.realestate.main.entity.PgSubscriptionPlan;
import com.realestate.main.entity.enums.SubscriptionPlanStatus;

public interface PgSubscriptionPlanRepository extends JpaRepository<PgSubscriptionPlan, Long> {

	Optional<PgSubscriptionPlan> findByPlanCode(String planCode);

	List<PgSubscriptionPlan> findAllByOrderBySortOrderAsc();

	List<PgSubscriptionPlan> findAllByStatusOrderBySortOrderAsc(SubscriptionPlanStatus status);
}
