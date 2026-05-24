package com.realestate.main.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.realestate.main.entity.Admin;
import com.realestate.main.repository.AdminRepository;
import com.realestate.main.service.AdminAgentService;
import com.realestate.main.service.AdminPgOwnerService;
import com.realestate.main.service.AdminDashboardService;
import com.realestate.main.service.AdminLoginService;
import com.realestate.main.service.AdminPasswordResetService;
import com.realestate.main.util.AdminSessionConstants;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminPageController {

	private final AdminLoginService adminLoginService;
	private final AdminRepository adminRepository;
	private final AdminDashboardService adminDashboardService;
	private final AdminPasswordResetService adminPasswordResetService;
	private final AdminAgentService adminAgentService;
	private final AdminPgOwnerService adminPgOwnerService;

	public AdminPageController(AdminLoginService adminLoginService, AdminRepository adminRepository,
			AdminDashboardService adminDashboardService, AdminPasswordResetService adminPasswordResetService,
			AdminAgentService adminAgentService, AdminPgOwnerService adminPgOwnerService) {
		this.adminLoginService = adminLoginService;
		this.adminRepository = adminRepository;
		this.adminDashboardService = adminDashboardService;
		this.adminPasswordResetService = adminPasswordResetService;
		this.adminAgentService = adminAgentService;
		this.adminPgOwnerService = adminPgOwnerService;
	}

	@GetMapping("/register")
	public String registerPage(HttpSession session) {
		if (isLoggedIn(session)) {
			return "redirect:/admin/dashboard";
		}
		if (adminRepository.count() > 0) {
			return "redirect:/admin/login";
		}
		return "admin/admin-register";
	}

	@GetMapping("/otp-verification")
	public String otpPage() {
		return "admin/admin-otp-verification";
	}

	@GetMapping("/login")
	public String loginPage(HttpSession session) {
		if (isLoggedIn(session)) {
			return "redirect:/admin/dashboard";
		}
		return "admin/admin-login";
	}

	@GetMapping("/forgot-password")
	public String forgotPasswordPage(HttpSession session) {
		if (isLoggedIn(session)) {
			return "redirect:/admin/dashboard";
		}
		return "admin/admin-forgot-password";
	}

	@GetMapping("/reset-password")
	public String resetPasswordPage(@RequestParam(value = "token", required = false) String token, Model model,
			HttpSession session) {
		if (isLoggedIn(session)) {
			return "redirect:/admin/dashboard";
		}
		String safeToken = token != null ? token.trim() : "";
		model.addAttribute("resetToken", safeToken);
		model.addAttribute("tokenValid", !safeToken.isEmpty() && adminPasswordResetService.isTokenValid(safeToken));
		return "admin/admin-reset-password";
	}

	@GetMapping("/dashboard")
	public String dashboard(HttpSession session, Model model) {
		if (!isLoggedIn(session)) {
			return "redirect:/admin/login";
		}
		populateAdminModel(session, model);
		model.addAttribute("lastLogin", adminRepository.findById((Long) session.getAttribute(AdminSessionConstants.ADMIN_ID))
				.map(Admin::getLastLogin).orElse(null));
		model.addAttribute("stats", adminDashboardService.loadStats());
		return "admin/admin-dashboard";
	}

	@GetMapping("/agents")
	public String agentsList(@RequestParam(value = "status", defaultValue = "PENDING") String status,
			HttpSession session, Model model) {
		if (!isLoggedIn(session)) {
			return "redirect:/admin/login";
		}
		populateAdminModel(session, model);
		model.addAttribute("statusFilter", status);
		return "admin/admin-agents";
	}

	@GetMapping("/agents/{id}")
	public String agentDetail(@org.springframework.web.bind.annotation.PathVariable Long id, HttpSession session,
			Model model) {
		if (!isLoggedIn(session)) {
			return "redirect:/admin/login";
		}
		populateAdminModel(session, model);
		model.addAttribute("agent", adminAgentService.getAgentDetail(id));
		return "admin/admin-agent-detail";
	}

	@GetMapping("/pg-owners")
	public String pgOwnersList(@RequestParam(value = "status", defaultValue = "PENDING") String status,
			HttpSession session, Model model) {
		if (!isLoggedIn(session)) {
			return "redirect:/admin/login";
		}
		populateAdminModel(session, model);
		model.addAttribute("statusFilter", status);
		return "admin/admin-pg-owners";
	}

	@GetMapping("/pg-owners/{id}")
	public String pgOwnerDetail(@org.springframework.web.bind.annotation.PathVariable Long id, HttpSession session,
			Model model) {
		if (!isLoggedIn(session)) {
			return "redirect:/admin/login";
		}
		populateAdminModel(session, model);
		model.addAttribute("pgOwner", adminPgOwnerService.getPgOwnerDetail(id));
		return "admin/admin-pg-owner-detail";
	}

	@GetMapping("/users")
	public String usersList(@RequestParam(value = "role", defaultValue = "ALL") String role,
			@RequestParam(value = "status", defaultValue = "ALL") String status, HttpSession session, Model model) {
		if (!isLoggedIn(session)) {
			return "redirect:/admin/login";
		}
		populateAdminModel(session, model);
		model.addAttribute("roleFilter", role);
		model.addAttribute("statusFilter", status);
		return "admin/admin-users";
	}

	@GetMapping("/properties")
	public String propertiesList(@RequestParam(value = "status", defaultValue = "ALL") String status,
			HttpSession session, Model model) {
		if (!isLoggedIn(session)) {
			return "redirect:/admin/login";
		}
		populateAdminModel(session, model);
		model.addAttribute("statusFilter", status);
		return "admin/admin-properties";
	}

	@GetMapping("/subscriptions/plans")
	public String subscriptionPlans(HttpSession session, Model model) {
		if (!isLoggedIn(session)) {
			return "redirect:/admin/login";
		}
		populateAdminModel(session, model);
		return "admin/admin-subscription-plans";
	}

	@GetMapping("/subscriptions/agents")
	public String agentSubscriptions(HttpSession session, Model model) {
		if (!isLoggedIn(session)) {
			return "redirect:/admin/login";
		}
		populateAdminModel(session, model);
		return "admin/admin-agent-subscriptions";
	}

	@GetMapping("/subscriptions/active")
	public String activeSubscriptions(HttpSession session, Model model) {
		if (!isLoggedIn(session)) {
			return "redirect:/admin/login";
		}
		populateAdminModel(session, model);
		return "admin/admin-active-subscriptions";
	}

	@GetMapping("/subscriptions/payments")
	public String subscriptionPayments(HttpSession session, Model model) {
		if (!isLoggedIn(session)) {
			return "redirect:/admin/login";
		}
		populateAdminModel(session, model);
		return "admin/admin-subscription-payments";
	}

	@GetMapping("/subscriptions/analytics")
	public String subscriptionAnalytics(HttpSession session, Model model) {
		if (!isLoggedIn(session)) {
			return "redirect:/admin/login";
		}
		populateAdminModel(session, model);
		return "admin/admin-subscription-analytics";
	}

	@GetMapping("/pg-subscriptions/plans")
	public String pgSubscriptionPlans(HttpSession session, Model model) {
		if (!isLoggedIn(session)) {
			return "redirect:/admin/login";
		}
		populateAdminModel(session, model);
		return "admin/admin-pg-subscription-plans";
	}

	@GetMapping("/pg-subscriptions/owners")
	public String pgOwnerSubscriptions(HttpSession session, Model model) {
		if (!isLoggedIn(session)) {
			return "redirect:/admin/login";
		}
		populateAdminModel(session, model);
		return "admin/admin-pg-owner-subscriptions";
	}

	@GetMapping("/pg-subscriptions/active")
	public String pgActiveSubscriptions(HttpSession session, Model model) {
		if (!isLoggedIn(session)) {
			return "redirect:/admin/login";
		}
		populateAdminModel(session, model);
		return "admin/admin-pg-active-subscriptions";
	}

	@GetMapping("/pg-subscriptions/payments")
	public String pgSubscriptionPayments(HttpSession session, Model model) {
		if (!isLoggedIn(session)) {
			return "redirect:/admin/login";
		}
		populateAdminModel(session, model);
		return "admin/admin-pg-subscription-payments";
	}

	@GetMapping("/pg-subscriptions/analytics")
	public String pgSubscriptionAnalytics(HttpSession session, Model model) {
		if (!isLoggedIn(session)) {
			return "redirect:/admin/login";
		}
		populateAdminModel(session, model);
		return "admin/admin-pg-subscription-analytics";
	}

	@GetMapping("/logout")
	public String logout(HttpServletRequest request) {
		adminLoginService.logout(request);
		return "redirect:/admin/login?logout=success";
	}

	private void populateAdminModel(HttpSession session, Model model) {
		Long adminId = (Long) session.getAttribute(AdminSessionConstants.ADMIN_ID);
		Admin admin = adminRepository.findById(adminId).orElse(null);
		if (admin != null) {
			model.addAttribute("adminName", admin.getFullName());
			model.addAttribute("adminEmail", admin.getOfficialEmail());
			model.addAttribute("adminRole", "Administrator");
		}
		model.addAttribute("pendingAgentCount", adminAgentService.countPendingApproval());
		model.addAttribute("pendingPgOwnerCount", adminPgOwnerService.countPendingApproval());
	}

	private boolean isLoggedIn(HttpSession session) {
		return session != null && session.getAttribute(AdminSessionConstants.ADMIN_ID) != null;
	}
}
