package com.realestate.main.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.realestate.main.entity.AdminSession;

public interface AdminSessionRepository extends JpaRepository<AdminSession, Long> {

	Optional<AdminSession> findBySessionIdAndActiveTrue(String sessionId);
}
