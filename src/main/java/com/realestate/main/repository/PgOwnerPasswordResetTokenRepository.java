package com.realestate.main.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.realestate.main.entity.PgOwnerPasswordResetToken;

public interface PgOwnerPasswordResetTokenRepository extends JpaRepository<PgOwnerPasswordResetToken, Long> {

	Optional<PgOwnerPasswordResetToken> findByTokenHashAndUsedFalse(String tokenHash);

	@Modifying
	@Query("UPDATE PgOwnerPasswordResetToken t SET t.used = true WHERE t.pgOwnerId = :pgOwnerId AND t.used = false")
	void invalidateActiveTokensForPgOwner(@Param("pgOwnerId") Long pgOwnerId);

	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("DELETE FROM PgOwnerPasswordResetToken t WHERE t.pgOwnerId = :pgOwnerId")
	void deleteByPgOwnerId(@Param("pgOwnerId") Long pgOwnerId);
}
