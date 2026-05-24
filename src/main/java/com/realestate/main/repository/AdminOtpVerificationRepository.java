package com.realestate.main.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.realestate.main.entity.AdminOtpVerification;

public interface AdminOtpVerificationRepository extends JpaRepository<AdminOtpVerification, Long> {

	Optional<AdminOtpVerification> findTopByOfficialEmailAndVerifiedFalseOrderByCreatedAtDesc(String officialEmail);

	void deleteByOfficialEmailAndVerifiedFalse(String officialEmail);
}
