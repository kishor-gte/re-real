package com.realestate.main.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.realestate.main.dto.response.AdminDashboardStats;
import com.realestate.main.entity.enums.AccountStatus;
import com.realestate.main.entity.enums.UserRole;
import com.realestate.main.repository.AdminRepository;
import com.realestate.main.repository.AgentRepository;
import com.realestate.main.repository.PgOwnerRepository;
import com.realestate.main.repository.PropertyRepository;
import com.realestate.main.repository.UserRepository;

@Service
public class AdminDashboardService {

	private final UserRepository userRepository;
	private final AdminRepository adminRepository;
	private final AgentRepository agentRepository;
	private final PropertyRepository propertyRepository;
	private final PgOwnerRepository pgOwnerRepository;

	public AdminDashboardService(UserRepository userRepository, AdminRepository adminRepository,
			AgentRepository agentRepository, PropertyRepository propertyRepository,
			PgOwnerRepository pgOwnerRepository) {
		this.userRepository = userRepository;
		this.adminRepository = adminRepository;
		this.agentRepository = agentRepository;
		this.propertyRepository = propertyRepository;
		this.pgOwnerRepository = pgOwnerRepository;
	}

	@Transactional(readOnly = true)
	public AdminDashboardStats loadStats() {
		AdminDashboardStats stats = new AdminDashboardStats();
		stats.setTotalUsers(userRepository.count());
		stats.setTotalBuyers(userRepository.countByRole(UserRole.BUYER));
		stats.setTotalSellers(userRepository.countByRole(UserRole.SELLER));
		stats.setPendingVerifications(userRepository.countByVerifiedFalse());
		stats.setTotalProperties(propertyRepository.count());
		stats.setTotalServiceProviders(0);
		stats.setOpenComplaints(0);
		stats.setRevenueTotal(0);
		stats.setActiveSubscriptions(userRepository.countByVerifiedTrue());
		stats.setPendingAgentApprovals(agentRepository.countByAccountStatus(AccountStatus.PENDING));
		stats.setTotalAgents(agentRepository.count());
		stats.setActiveAgents(agentRepository.countByAccountStatus(AccountStatus.ACTIVE));
		stats.setPendingPgOwnerApprovals(pgOwnerRepository.countByAccountStatus(AccountStatus.PENDING));
		stats.setTotalPgOwners(pgOwnerRepository.count());
		stats.setActivePgOwners(pgOwnerRepository.countByAccountStatus(AccountStatus.ACTIVE));
		return stats;
	}

	public long countAdmins() {
		return adminRepository.count();
	}
}
