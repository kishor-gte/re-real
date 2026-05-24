package com.realestate.main.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.realestate.main.entity.AgentOtpVerification;

public interface AgentOtpVerificationRepository extends JpaRepository<AgentOtpVerification, Long> {

	Optional<AgentOtpVerification> findTopByEmailAndVerifiedFalseOrderByCreatedAtDesc(String email);

	void deleteByEmailAndVerifiedFalse(String email);
}
