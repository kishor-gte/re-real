package com.realestate.main.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.realestate.main.entity.PgPropertyImage;

public interface PgPropertyImageRepository extends JpaRepository<PgPropertyImage, Long> {

	List<PgPropertyImage> findByPgPropertyIdOrderBySortOrderAscUploadedAtAsc(Long pgPropertyId);

	void deleteByPgPropertyId(Long pgPropertyId);

	long countByPgPropertyId(Long pgPropertyId);
}
