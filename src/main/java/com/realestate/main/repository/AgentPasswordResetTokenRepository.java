package com.realestate.main.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.realestate.main.entity.AgentPasswordResetToken;

public interface AgentPasswordResetTokenRepository extends JpaRepository<AgentPasswordResetToken, Long> {

	Optional<AgentPasswordResetToken> findByTokenHashAndUsedFalse(String tokenHash);

	@Modifying
	@Query("UPDATE AgentPasswordResetToken t SET t.used = true WHERE t.agentId = :agentId AND t.used = false")
	void invalidateActiveTokensForAgent(@Param("agentId") Long agentId);
}
