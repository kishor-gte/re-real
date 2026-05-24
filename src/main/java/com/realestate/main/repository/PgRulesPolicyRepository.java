package com.realestate.main.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.realestate.main.entity.PgRulesPolicy;

public interface PgRulesPolicyRepository extends JpaRepository<PgRulesPolicy, Long> {

	Optional<PgRulesPolicy> findByPgPropertyId(Long pgPropertyId);

	void deleteByPgPropertyId(Long pgPropertyId);
}
