package com.realestate.main.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.realestate.main.entity.BookingNotification;
import com.realestate.main.entity.enums.NotificationRecipientType;

public interface BookingNotificationRepository extends JpaRepository<BookingNotification, Long> {

	List<BookingNotification> findByBookingIdOrderByCreatedAtDesc(Long bookingId);

	@Query("SELECT n FROM BookingNotification n WHERE n.bookingId IN :bookingIds "
			+ "AND n.recipientType = :recipientType ORDER BY n.createdAt DESC")
	List<BookingNotification> findByBookingIdInAndRecipientType(@Param("bookingIds") Collection<Long> bookingIds,
			@Param("recipientType") NotificationRecipientType recipientType);
}
