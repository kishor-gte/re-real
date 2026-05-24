package com.realestate.main.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.realestate.main.entity.AdminRole;

public interface AdminRoleRepository extends JpaRepository<AdminRole, Long> {

	Optional<AdminRole> findByRoleCode(String roleCode);
}
