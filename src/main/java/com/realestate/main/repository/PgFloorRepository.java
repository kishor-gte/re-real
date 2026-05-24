package com.realestate.main.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.realestate.main.entity.PgFloor;

public interface PgFloorRepository extends JpaRepository<PgFloor, Long> {

	List<PgFloor> findByPgPropertyIdOrderBySortOrderAscFloorNumberAsc(Long pgPropertyId);

	void deleteByPgPropertyId(Long pgPropertyId);
}
