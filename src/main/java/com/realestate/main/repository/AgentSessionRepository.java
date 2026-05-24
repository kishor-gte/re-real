package com.realestate.main.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.realestate.main.entity.AgentSession;

public interface AgentSessionRepository extends JpaRepository<AgentSession, Long> {

	Optional<AgentSession> findBySessionIdAndActiveTrue(String sessionId);
}
