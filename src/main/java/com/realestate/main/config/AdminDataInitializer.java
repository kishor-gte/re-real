package com.realestate.main.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.realestate.main.entity.AdminRole;
import com.realestate.main.repository.AdminRoleRepository;

@Component
public class AdminDataInitializer implements CommandLineRunner {

	private final AdminRoleRepository adminRoleRepository;

	public AdminDataInitializer(AdminRoleRepository adminRoleRepository) {
		this.adminRoleRepository = adminRoleRepository;
	}

	@Override
	public void run(String... args) {
		seedRole("SUPER_ADMIN", "Super Administrator", "Full platform access");
		seedRole("OPERATIONS_ADMIN", "Operations Admin", "Operations and listings");
		seedRole("FINANCE_ADMIN", "Finance Admin", "Revenue and subscriptions");
		seedRole("SUPPORT_ADMIN", "Support Admin", "User support and complaints");
		seedRole("CONTENT_ADMIN", "Content Admin", "Content and marketing");
	}

	private void seedRole(String code, String name, String description) {
		if (adminRoleRepository.findByRoleCode(code).isEmpty()) {
			adminRoleRepository.save(new AdminRole(code, name, description));
		}
	}
}
