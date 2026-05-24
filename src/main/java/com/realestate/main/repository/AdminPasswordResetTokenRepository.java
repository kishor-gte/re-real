package com.realestate.main.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.realestate.main.entity.AdminPasswordResetToken;

public interface AdminPasswordResetTokenRepository extends JpaRepository<AdminPasswordResetToken, Long> {

	Optional<AdminPasswordResetToken> findByTokenHashAndUsedFalse(String tokenHash);

	@Modifying
	@Query("UPDATE AdminPasswordResetToken t SET t.used = true WHERE t.adminId = :adminId AND t.used = false")
	void invalidateActiveTokensForAdmin(@Param("adminId") Long adminId);
}
