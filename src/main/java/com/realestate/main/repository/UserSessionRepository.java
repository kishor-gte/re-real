package com.realestate.main.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.realestate.main.entity.UserSession;

public interface UserSessionRepository extends JpaRepository<UserSession, Long> {

	Optional<UserSession> findBySessionIdAndActiveTrue(String sessionId);
}
