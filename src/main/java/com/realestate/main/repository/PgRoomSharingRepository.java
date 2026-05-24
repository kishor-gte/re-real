package com.realestate.main.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.realestate.main.entity.PgRoomSharing;

public interface PgRoomSharingRepository extends JpaRepository<PgRoomSharing, Long> {

	List<PgRoomSharing> findByPgPropertyId(Long pgPropertyId);

	void deleteByPgPropertyId(Long pgPropertyId);
}
