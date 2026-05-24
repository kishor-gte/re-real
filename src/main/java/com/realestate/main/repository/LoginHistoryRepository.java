package com.realestate.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.realestate.main.entity.LoginHistory;

public interface LoginHistoryRepository extends JpaRepository<LoginHistory, Long> {
}
