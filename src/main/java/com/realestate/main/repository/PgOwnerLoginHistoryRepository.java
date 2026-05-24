package com.realestate.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.realestate.main.entity.PgOwnerLoginHistory;

public interface PgOwnerLoginHistoryRepository extends JpaRepository<PgOwnerLoginHistory, Long> {

	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("DELETE FROM PgOwnerLoginHistory h WHERE h.pgOwner.id = :pgOwnerId")
	void deleteByPgOwnerId(@Param("pgOwnerId") Long pgOwnerId);
}
