package com.realestate.main.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.realestate.main.entity.PgOwnerSession;

public interface PgOwnerSessionRepository extends JpaRepository<PgOwnerSession, Long> {

	Optional<PgOwnerSession> findBySessionIdAndActiveTrue(String sessionId);

	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("DELETE FROM PgOwnerSession s WHERE s.pgOwner.id = :pgOwnerId")
	void deleteByPgOwnerId(@Param("pgOwnerId") Long pgOwnerId);
}
