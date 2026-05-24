package com.realestate.main.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.realestate.main.entity.Admin;
import com.realestate.main.repository.AdminRepository;
import com.realestate.main.util.MobileUtils;

@Service
public class AdminLookupService {

	private final AdminRepository adminRepository;

	public AdminLookupService(AdminRepository adminRepository) {
		this.adminRepository = adminRepository;
	}

	public static String normalizeEmail(String email) {
		if (email == null) {
			return "";
		}
		return email.toLowerCase().trim();
	}

	public boolean hasRegisteredAdmin() {
		return adminRepository.count() > 0;
	}

	public boolean isEmailRegistered(String email) {
		return adminRepository.existsByOfficialEmail(normalizeEmail(email));
	}

	public boolean isEmployeeIdRegistered(String employeeId) {
		if (employeeId == null || employeeId.isBlank()) {
			return false;
		}
		return adminRepository.existsByEmployeeId(employeeId.trim().toUpperCase());
	}

	public boolean isMobileRegistered(String mobile) {
		String normalized = MobileUtils.normalize(mobile);
		if (!MobileUtils.isValidTenDigit(normalized)) {
			return false;
		}
		return adminRepository.existsByMobile(normalized);
	}

	public Optional<Admin> findByEmail(String email) {
		return adminRepository.findByOfficialEmail(normalizeEmail(email));
	}

	public Optional<Admin> findVerifiedAdminByEmailAndMobile(String email, String mobile) {
		String normalizedEmail = normalizeEmail(email);
		String normalizedMobile = MobileUtils.normalize(mobile);
		if (!MobileUtils.isValidTenDigit(normalizedMobile)) {
			return Optional.empty();
		}
		return findByEmail(normalizedEmail)
				.filter(Admin::isVerified)
				.filter(a -> normalizedMobile.equals(MobileUtils.normalize(a.getMobile())));
	}
}
