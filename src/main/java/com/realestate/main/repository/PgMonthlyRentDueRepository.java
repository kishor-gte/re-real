package com.realestate.main.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.realestate.main.entity.PgMonthlyRentDue;
import com.realestate.main.entity.enums.PgRentDueStatus;

public interface PgMonthlyRentDueRepository extends JpaRepository<PgMonthlyRentDue, Long> {

	List<PgMonthlyRentDue> findByPgBookingIdOrderByDueDateAsc(Long pgBookingId);

	Optional<PgMonthlyRentDue> findByPgBookingIdAndDueDate(Long pgBookingId, LocalDate dueDate);

	Optional<PgMonthlyRentDue> findByRazorpayOrderId(String razorpayOrderId);

	Optional<PgMonthlyRentDue> findFirstByPgBookingIdAndDueStatusInOrderByDueDateAsc(Long pgBookingId,
			List<PgRentDueStatus> statuses);

	long countByPgBookingIdAndDueStatusIn(Long pgBookingId, List<PgRentDueStatus> statuses);
}
