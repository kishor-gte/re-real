package com.realestate.main.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.realestate.main.entity.PgPropertyAmenity;

public interface PgPropertyAmenityRepository extends JpaRepository<PgPropertyAmenity, Long> {

	List<PgPropertyAmenity> findByPgPropertyId(Long pgPropertyId);

	void deleteByPgPropertyId(Long pgPropertyId);
}
