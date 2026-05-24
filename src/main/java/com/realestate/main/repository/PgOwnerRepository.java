package com.realestate.main.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.realestate.main.entity.PgOwner;
import com.realestate.main.entity.enums.AccountStatus;

public interface PgOwnerRepository extends JpaRepository<PgOwner, Long> {

	Optional<PgOwner> findByEmail(String email);

	Optional<PgOwner> findByMobile(String mobile);

	boolean existsByEmail(String email);

	boolean existsByMobile(String mobile);

	boolean existsByPgOwnerCode(String pgOwnerCode);

	long countByAccountStatus(AccountStatus accountStatus);

	List<PgOwner> findByAccountStatusOrderByCreatedAtDesc(AccountStatus accountStatus);

	List<PgOwner> findAllByOrderByCreatedAtDesc();
}
