package com.realestate.main.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.realestate.main.entity.User;
import com.realestate.main.entity.enums.UserRole;

public interface UserRepository extends JpaRepository<User, Long> {

	long countByRole(UserRole role);

	long countByVerifiedFalse();

	long countByVerifiedTrue();

	Optional<User> findByEmail(String email);

	Optional<User> findByMobile(String mobile);

	Optional<User> findByReferralCode(String referralCode);

	boolean existsByEmail(String email);

	boolean existsByMobile(String mobile);

	List<User> findAllByOrderByCreatedAtDesc();

	@Query(value = "SELECT COUNT(*) FROM users WHERE phone = :mobile", nativeQuery = true)
	long countByMobileInUsers(@Param("mobile") String mobile);

	@Query(value = "SELECT COUNT(*) FROM users WHERE LOWER(email) = LOWER(:email)", nativeQuery = true)
	long countByEmailInUsers(@Param("email") String email);

	@Query(value = """
			SELECT COUNT(*) FROM users
			WHERE RIGHT(REPLACE(REPLACE(REPLACE(REPLACE(COALESCE(phone, ''), ' ', ''), '-', ''), '+', ''), '.', ''), 10) = :digits
			  AND COALESCE(phone, '') <> ''
			""", nativeQuery = true)
	long countByMobileDigitsLegacy(@Param("digits") String digits);

	@Query(value = """
			SELECT id FROM users
			WHERE phone = :digits
			   OR RIGHT(REPLACE(REPLACE(REPLACE(REPLACE(COALESCE(phone, ''), ' ', ''), '-', ''), '+', ''), '.', ''), 10) = :digits
			""", nativeQuery = true)
	List<Long> findUserIdsByMobileDigits(@Param("digits") String digits);
}
