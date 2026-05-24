package com.realestate.main.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.realestate.main.dto.PendingAdminRegistrationData;
import com.realestate.main.entity.Admin;
import com.realestate.main.entity.enums.AccountStatus;
import com.realestate.main.entity.enums.AdminDepartment;
import com.realestate.main.entity.enums.AdminRoleType;
import com.realestate.main.repository.AdminRepository;

@Service
public class AdminAccountCreatorService {

	private static final String DEFAULT_EMPLOYEE_ID = "ADM1001";
	private static final AdminDepartment DEFAULT_DEPARTMENT = AdminDepartment.IT;
	private static final AdminRoleType DEFAULT_ADMIN_ROLE = AdminRoleType.SUPER_ADMIN;

	private final AdminRepository adminRepository;
	private final PasswordEncoder passwordEncoder;

	public AdminAccountCreatorService(AdminRepository adminRepository, PasswordEncoder passwordEncoder) {
		this.adminRepository = adminRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Admin createAndCommit(PendingAdminRegistrationData pending) {
		Admin admin = new Admin();
		admin.setFullName(pending.getFullName());
		admin.setOfficialEmail(pending.getOfficialEmail());
		admin.setMobile(pending.getMobile());
		admin.setEmployeeId(DEFAULT_EMPLOYEE_ID);
		admin.setDepartment(DEFAULT_DEPARTMENT);
		admin.setAdminRole(DEFAULT_ADMIN_ROLE);
		admin.setPassword(passwordEncoder.encode(pending.getPassword()));
		admin.setSecurityQuestion(pending.getSecurityQuestion());
		admin.setSecurityAnswerHash(passwordEncoder.encode(pending.getSecurityAnswer().trim().toLowerCase()));
		admin.setProfileImage(pending.getProfileImage());
		admin.setVerified(true);
		admin.setAccountStatus(AccountStatus.ACTIVE);
		admin.setFailedLoginAttempts(0);
		return adminRepository.saveAndFlush(admin);
	}
}
