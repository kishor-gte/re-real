package com.realestate.main.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.realestate.main.entity.PropertyBooking;
import com.realestate.main.entity.enums.BookingStatus;

public interface PropertyBookingRepository extends JpaRepository<PropertyBooking, Long> {

	Optional<PropertyBooking> findByBookingCode(String bookingCode);

	Optional<PropertyBooking> findByIdAndUserId(Long id, Long userId);

	List<PropertyBooking> findByUserIdOrderByCreatedAtDesc(Long userId);

	boolean existsByPropertyIdAndBookingStatusIn(Long propertyId, List<BookingStatus> statuses);

	boolean existsByUserIdAndPropertyIdAndBookingStatusIn(Long userId, Long propertyId, List<BookingStatus> statuses);

	Optional<PropertyBooking> findTopByUserIdAndPropertyIdAndBookingStatusOrderByCreatedAtDesc(Long userId,
			Long propertyId, BookingStatus status);

	Optional<PropertyBooking> findTopByPropertyIdAndBookingStatusOrderByCreatedAtDesc(Long propertyId,
			BookingStatus status);

	List<PropertyBooking> findByUserIdAndBookingStatusNotOrderByCreatedAtDesc(Long userId, BookingStatus status);

	List<PropertyBooking> findByAgentIdOrderByCreatedAtDesc(Long agentId);

	List<PropertyBooking> findByAgentIdOrderByBookingDateDesc(Long agentId);

	long countByAgentIdAndBookingStatus(Long agentId, BookingStatus status);

	long countByAgentIdAndBookingStatusNot(Long agentId, BookingStatus status);
}
