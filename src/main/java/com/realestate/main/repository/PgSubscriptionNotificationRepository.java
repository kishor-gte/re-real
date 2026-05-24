package com.realestate.main.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.realestate.main.entity.PgSubscriptionNotification;

public interface PgSubscriptionNotificationRepository extends JpaRepository<PgSubscriptionNotification, Long> {

	List<PgSubscriptionNotification> findByPgOwnerIdOrderByCreatedAtDesc(Long pgOwnerId);

	long countByPgOwnerIdAndReadFalse(Long pgOwnerId);
}
