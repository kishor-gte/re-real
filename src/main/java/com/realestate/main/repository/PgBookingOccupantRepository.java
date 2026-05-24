package com.realestate.main.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.realestate.main.entity.PgBookingOccupant;

public interface PgBookingOccupantRepository extends JpaRepository<PgBookingOccupant, Long> {

	List<PgBookingOccupant> findByPgBookingIdOrderByOccupantIndexAsc(Long pgBookingId);

	void deleteByPgBookingId(Long pgBookingId);
}
