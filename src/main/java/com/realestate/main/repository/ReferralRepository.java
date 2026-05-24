package com.realestate.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.realestate.main.entity.Referral;

public interface ReferralRepository extends JpaRepository<Referral, Long> {
}
