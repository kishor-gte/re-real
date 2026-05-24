package com.realestate.main.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.realestate.main.entity.Admin;

public interface AdminRepository extends JpaRepository<Admin, Long> {

	Optional<Admin> findByOfficialEmail(String officialEmail);

	Optional<Admin> findByEmployeeId(String employeeId);

	Optional<Admin> findByMobile(String mobile);

	boolean existsByOfficialEmail(String officialEmail);

	boolean existsByEmployeeId(String employeeId);

	boolean existsByMobile(String mobile);
}
