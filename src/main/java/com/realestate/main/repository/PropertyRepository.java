package com.realestate.main.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.realestate.main.entity.Property;
import com.realestate.main.entity.enums.AgentSpecialization;
import com.realestate.main.entity.enums.ListingType;
import com.realestate.main.entity.enums.PropertyStatus;

public interface PropertyRepository extends JpaRepository<Property, Long> {

	long countByAgentId(Long agentId);

	boolean existsByPropertyCode(String propertyCode);

	Optional<Property> findByIdAndAgentId(Long id, Long agentId);

	List<Property> findByAgentIdOrderByCreatedAtDesc(Long agentId);

	List<Property> findAllByOrderByCreatedAtDesc();

	@Query("SELECT COALESCE(SUM(p.viewCount), 0) FROM Property p WHERE p.agentId = :agentId")
	long sumViewCountByAgentId(@Param("agentId") Long agentId);

	@Query("""
			SELECT p FROM Property p
			WHERE p.status = :status
			AND (:category IS NULL OR p.category = :category)
			AND (:listingType IS NULL OR p.listingType = :listingType)
			ORDER BY p.createdAt DESC
			""")
	List<Property> searchActiveListings(@Param("status") PropertyStatus status,
			@Param("category") AgentSpecialization category, @Param("listingType") ListingType listingType);
}
