package com.realestate.main.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.realestate.main.entity.PasswordResetToken;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

	Optional<PasswordResetToken> findByTokenHashAndUsedFalse(String tokenHash);

	@Modifying
	@Query("UPDATE PasswordResetToken t SET t.used = true WHERE t.userId = :userId AND t.used = false")
	void invalidateActiveTokensForUser(@Param("userId") Long userId);
}
