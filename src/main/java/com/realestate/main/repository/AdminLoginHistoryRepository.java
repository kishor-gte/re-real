package com.realestate.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.realestate.main.entity.AdminLoginHistory;

public interface AdminLoginHistoryRepository extends JpaRepository<AdminLoginHistory, Long> {
}
