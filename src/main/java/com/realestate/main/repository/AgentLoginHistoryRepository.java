package com.realestate.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.realestate.main.entity.AgentLoginHistory;

public interface AgentLoginHistoryRepository extends JpaRepository<AgentLoginHistory, Long> {
}
