package com.realestate.main.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.realestate.main.entity.PgOwnerOtpVerification;

public interface PgOwnerOtpVerificationRepository extends JpaRepository<PgOwnerOtpVerification, Long> {

	Optional<PgOwnerOtpVerification> findTopByEmailAndVerifiedFalseOrderByCreatedAtDesc(String email);

	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("DELETE FROM PgOwnerOtpVerification o WHERE o.email = :email AND o.verified = false")
	void deleteByEmailAndVerifiedFalse(@Param("email") String email);
}
