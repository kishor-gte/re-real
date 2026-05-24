package com.realestate.main.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.realestate.main.entity.PropertyEnquiry;
import com.realestate.main.entity.enums.EnquiryStatus;

public interface PropertyEnquiryRepository extends JpaRepository<PropertyEnquiry, Long> {

	List<PropertyEnquiry> findByUserIdOrderByCreatedAtDesc(Long userId);

	List<PropertyEnquiry> findByAgentIdOrderByCreatedAtDesc(Long agentId);

	Optional<PropertyEnquiry> findByIdAndUserId(Long id, Long userId);

	Optional<PropertyEnquiry> findByIdAndAgentId(Long id, Long agentId);

	boolean existsByUserIdAndPropertyIdAndStatus(Long userId, Long propertyId, EnquiryStatus status);

	long countByAgentId(Long agentId);

	long countByAgentIdAndStatus(Long agentId, EnquiryStatus status);

	long countByAgentIdAndStatusIn(Long agentId, List<EnquiryStatus> statuses);
}
