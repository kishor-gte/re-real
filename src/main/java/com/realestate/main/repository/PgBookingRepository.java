package com.realestate.main.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.realestate.main.entity.PgBooking;
import com.realestate.main.entity.enums.BookingStatus;
import com.realestate.main.entity.enums.PgOwnerApprovalStatus;

public interface PgBookingRepository extends JpaRepository<PgBooking, Long> {

	Optional<PgBooking> findByIdAndUserId(Long id, Long userId);

	Optional<PgBooking> findByIdAndPgOwnerId(Long id, Long pgOwnerId);

	List<PgBooking> findByUserIdOrderByCreatedAtDesc(Long userId);

	List<PgBooking> findByPgOwnerIdOrderByCreatedAtDesc(Long pgOwnerId);

	long countByPgOwnerIdAndOwnerApprovalStatus(Long pgOwnerId, PgOwnerApprovalStatus ownerApprovalStatus);

	@Query("""
			SELECT COUNT(b) FROM PgBooking b
			WHERE b.pgOwnerId = :ownerId
			  AND b.paidAmount > 0
			  AND b.bookingStatus <> com.realestate.main.entity.enums.BookingStatus.CANCELLED
			  AND (b.ownerApprovalStatus = com.realestate.main.entity.enums.PgOwnerApprovalStatus.PENDING
			       OR b.ownerApprovalStatus IS NULL)
			""")
	long countPendingOwnerApprovals(@Param("ownerId") Long ownerId);

	Optional<PgBooking> findTopByUserIdAndPgPropertyIdAndBookingStatusOrderByCreatedAtDesc(Long userId,
			Long pgPropertyId, BookingStatus status);
}
