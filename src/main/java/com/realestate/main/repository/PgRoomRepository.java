package com.realestate.main.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.realestate.main.entity.PgRoom;

public interface PgRoomRepository extends JpaRepository<PgRoom, Long> {

	List<PgRoom> findByPgPropertyIdOrderByFloorIdAscRoomNumberAsc(Long pgPropertyId);

	List<PgRoom> findByFloorIdOrderByRoomNumberAsc(Long floorId);

	void deleteByPgPropertyId(Long pgPropertyId);
}
