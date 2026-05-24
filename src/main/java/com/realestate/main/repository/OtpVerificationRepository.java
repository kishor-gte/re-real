package com.realestate.main.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.realestate.main.entity.OtpVerification;

public interface OtpVerificationRepository extends JpaRepository<OtpVerification, Long> {

	Optional<OtpVerification> findTopByEmailAndVerifiedFalseOrderByCreatedAtDesc(String email);

	void deleteByEmailAndVerifiedFalse(String email);
}
