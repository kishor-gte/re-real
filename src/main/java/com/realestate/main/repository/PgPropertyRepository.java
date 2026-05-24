package com.realestate.main.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.realestate.main.entity.PgProperty;

public interface PgPropertyRepository extends JpaRepository<PgProperty, Long> {

	List<PgProperty> findByOwnerIdOrderByCreatedAtDesc(Long ownerId);

	Optional<PgProperty> findByIdAndOwnerId(Long id, Long ownerId);

	long countByOwnerId(Long ownerId);

	boolean existsByOwnerIdAndPgNameIgnoreCase(Long ownerId, String pgName);

	boolean existsByOwnerIdAndPgNameIgnoreCaseAndIdNot(Long ownerId, String pgName, Long id);

	Optional<PgProperty> findTopByOrderByIdDesc();

	boolean existsByPgCode(String pgCode);

	long count();

	@Query("""
			SELECT p FROM PgProperty p
			WHERE p.status = com.realestate.main.entity.enums.PgPropertyStatus.PUBLISHED
			AND (:city IS NULL OR LOWER(TRIM(p.city)) = LOWER(TRIM(:city)))
			AND (:state IS NULL OR LOWER(TRIM(p.state)) = LOWER(TRIM(:state)))
			ORDER BY p.city ASC, p.createdAt DESC
			""")
	List<PgProperty> searchPublished(@Param("city") String city, @Param("state") String state);

	@Query("""
			SELECT DISTINCT p.city FROM PgProperty p
			WHERE p.status = com.realestate.main.entity.enums.PgPropertyStatus.PUBLISHED
			AND p.city IS NOT NULL AND TRIM(p.city) <> ''
			ORDER BY p.city ASC
			""")
	List<String> findDistinctPublishedCities();
}
