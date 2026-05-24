package com.realestate.main.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.realestate.main.entity.Agent;
import com.realestate.main.entity.enums.AccountStatus;
import com.realestate.main.repository.AgentRepository;
import com.realestate.main.entity.enums.AgentSpecialization;
import com.realestate.main.service.AgentLookupService;
import com.realestate.main.service.AgentDashboardService;
import com.realestate.main.service.AgentLoginService;
import com.realestate.main.service.AgentPasswordResetService;
import com.realestate.main.service.subscription.AgentPostingLimitService;
import com.realestate.main.dto.subscription.AgentPostingStatusResponse;
import com.realestate.main.util.AgentSessionConstants;
import com.realestate.main.util.PropertySpecializationSupport;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/agent")
public class AgentPageController {

	private final AgentLoginService agentLoginService;
	private final AgentRepository agentRepository;
	private final AgentDashboardService agentDashboardService;
	private final AgentPasswordResetService agentPasswordResetService;
	private final AgentPostingLimitService postingLimitService;

	public AgentPageController(AgentLoginService agentLoginService, AgentRepository agentRepository,
			AgentDashboardService agentDashboardService, AgentPasswordResetService agentPasswordResetService,
			AgentPostingLimitService postingLimitService) {
		this.agentLoginService = agentLoginService;
		this.agentRepository = agentRepository;
		this.agentDashboardService = agentDashboardService;
		this.agentPasswordResetService = agentPasswordResetService;
		this.postingLimitService = postingLimitService;
	}

	@GetMapping("/register")
	public String registerPage(HttpSession session) {
		if (isLoggedIn(session)) {
			return "redirect:/agent/dashboard";
		}
		return "agent/agent-register";
	}

	@GetMapping("/otp-verification")
	public String otpPage() {
		return "agent/agent-otp-verification";
	}

	@GetMapping("/login")
	public String loginPage(HttpSession session, Model model,
			@RequestParam(value = "pending", required = false) String pending) {
		if (isLoggedIn(session)) {
			return "redirect:/agent/dashboard";
		}
		if ("1".equals(pending)) {
			model.addAttribute("showPendingMessage", true);
		}
		return "agent/agent-login";
	}

	@GetMapping("/pending-approval")
	public String pendingApprovalPage(@RequestParam(value = "email", required = false) String email, Model model) {
		model.addAttribute("email", email != null ? AgentLookupService.normalizeEmail(email) : "");
		return "agent/agent-pending-approval";
	}

	@GetMapping("/forgot-password")
	public String forgotPasswordPage(HttpSession session) {
		if (isLoggedIn(session)) {
			return "redirect:/agent/dashboard";
		}
		return "agent/agent-forgot-password";
	}

	@GetMapping("/reset-password")
	public String resetPasswordPage(@RequestParam(value = "token", required = false) String token, Model model,
			HttpSession session) {
		if (isLoggedIn(session)) {
			return "redirect:/agent/dashboard";
		}
		String safeToken = token != null ? token.trim() : "";
		model.addAttribute("resetToken", safeToken);
		model.addAttribute("tokenValid", !safeToken.isEmpty() && agentPasswordResetService.isTokenValid(safeToken));
		return "agent/agent-reset-password";
	}

	@GetMapping("/dashboard")
	public String dashboard(HttpSession session, Model model, HttpServletRequest request) {
		String redirect = guardActiveAgent(session, request, model);
		if (redirect != null) {
			return redirect;
		}
		Long agentId = (Long) session.getAttribute(AgentSessionConstants.AGENT_ID);
		model.addAttribute("stats", agentDashboardService.loadStats(agentId));
		return "agent/agent-dashboard";
	}

	@GetMapping("/enquiries")
	public String enquiries(HttpSession session, Model model, HttpServletRequest request) {
		String redirect = guardActiveAgent(session, request, model);
		if (redirect != null) {
			return redirect;
		}
		return "agent/agent-enquiries";
	}

	@GetMapping("/bookings")
	public String bookings(HttpSession session, Model model, HttpServletRequest request) {
		String redirect = guardActiveAgent(session, request, model);
		if (redirect != null) {
			return redirect;
		}
		return "agent/agent-bookings";
	}

	@GetMapping("/subscription")
	public String subscription(HttpSession session, Model model, HttpServletRequest request) {
		String redirect = guardActiveAgent(session, request, model);
		if (redirect != null) {
			return redirect;
		}
		Long agentId = (Long) session.getAttribute(AgentSessionConstants.AGENT_ID);
		model.addAttribute("stats", agentDashboardService.loadStats(agentId));
		return "agent/agent-subscription";
	}

	@GetMapping("/subscription/plans")
	public String subscriptionPlans(HttpSession session, Model model, HttpServletRequest request) {
		String redirect = guardActiveAgent(session, request, model);
		if (redirect != null) {
			return redirect;
		}
		return "agent/agent-subscription-plans";
	}

	@GetMapping("/subscription/payment")
	public String subscriptionPayment(@RequestParam(value = "subscriptionId", required = false) Long subscriptionId,
			HttpSession session, Model model, HttpServletRequest request) {
		String redirect = guardActiveAgent(session, request, model);
		if (redirect != null) {
			return redirect;
		}
		model.addAttribute("subscriptionId", subscriptionId);
		return "agent/agent-subscription-payment";
	}

	@GetMapping("/properties")
	public String myProperties(HttpSession session, Model model, HttpServletRequest request) {
		String redirect = guardActiveAgent(session, request, model);
		if (redirect != null) {
			return redirect;
		}
		return "agent/agent-properties";
	}

	@GetMapping("/properties/add")
	public String addProperty(HttpSession session, Model model, HttpServletRequest request) {
		String redirect = guardActiveAgent(session, request, model);
		if (redirect != null) {
			return redirect;
		}
		Long agentId = (Long) session.getAttribute(AgentSessionConstants.AGENT_ID);
		AgentPostingStatusResponse posting = postingLimitService.getPostingStatus(agentId);
		if (!posting.isCanPost()) {
			return "redirect:/agent/subscription/plans?limit=1";
		}
		Agent agent = agentRepository.findById(agentId).orElseThrow();
		AgentSpecialization spec = agent.getSpecialization();
		model.addAttribute("specializationKey", spec.name());
		model.addAttribute("propertySubTypes", PropertySpecializationSupport.subTypesFor(spec));
		model.addAttribute("amenitySuggestionsText",
				String.join(", ", PropertySpecializationSupport.amenitySuggestions(spec)));
		model.addAttribute("defaultCity", agent.getCity());
		model.addAttribute("defaultState", agent.getState());
		model.addAttribute("postingStatus", posting);
		return "agent/agent-add-property";
	}

	@GetMapping("/properties/{id}/edit")
	public String editProperty(@PathVariable Long id, HttpSession session, Model model, HttpServletRequest request) {
		String redirect = guardActiveAgent(session, request, model);
		if (redirect != null) {
			return redirect;
		}
		Long agentId = (Long) session.getAttribute(AgentSessionConstants.AGENT_ID);
		Agent agent = agentRepository.findById(agentId).orElseThrow();
		AgentSpecialization spec = agent.getSpecialization();
		model.addAttribute("specializationKey", spec.name());
		model.addAttribute("propertySubTypes", PropertySpecializationSupport.subTypesFor(spec));
		model.addAttribute("defaultCity", agent.getCity());
		model.addAttribute("defaultState", agent.getState());
		model.addAttribute("propertyId", id);
		model.addAttribute("editMode", true);
		return "agent/agent-add-property";
	}

	@GetMapping("/analytics")
	public String analytics(HttpSession session, Model model, HttpServletRequest request) {
		String redirect = guardActiveAgent(session, request, model);
		return redirect != null ? redirect : "agent/agent-analytics";
	}

	@GetMapping("/earnings")
	public String earnings(HttpSession session, Model model, HttpServletRequest request) {
		String redirect = guardActiveAgent(session, request, model);
		return redirect != null ? redirect : "agent/agent-earnings";
	}

	@GetMapping("/referrals")
	public String referrals(HttpSession session, Model model, HttpServletRequest request) {
		String redirect = guardActiveAgent(session, request, model);
		return redirect != null ? redirect : "agent/agent-referrals";
	}

	@GetMapping("/messages")
	public String messages(HttpSession session, Model model, HttpServletRequest request) {
		String redirect = guardActiveAgent(session, request, model);
		return redirect != null ? redirect : "agent/agent-messages";
	}

	@GetMapping("/notifications")
	public String notifications(HttpSession session, Model model, HttpServletRequest request) {
		String redirect = guardActiveAgent(session, request, model);
		return redirect != null ? redirect : "agent/agent-notifications";
	}

	@GetMapping("/logout")
	public String logout(HttpServletRequest request) {
		agentLoginService.logout(request);
		return "redirect:/agent/login?logout=success";
	}

	private boolean isLoggedIn(HttpSession session) {
		return session != null && session.getAttribute(AgentSessionConstants.AGENT_ID) != null;
	}

	private String guardActiveAgent(HttpSession session, HttpServletRequest request, Model model) {
		Long agentId = (Long) session.getAttribute(AgentSessionConstants.AGENT_ID);
		if (agentId == null) {
			return "redirect:/agent/login";
		}
		Agent agent = agentRepository.findById(agentId).orElse(null);
		if (agent == null) {
			agentLoginService.logout(request);
			return "redirect:/agent/login";
		}
		if (agent.getAccountStatus() != AccountStatus.ACTIVE) {
			agentLoginService.logout(request);
			if (agent.getAccountStatus() == AccountStatus.PENDING) {
				return "redirect:/agent/pending-approval?email=" + agent.getEmail();
			}
			return "redirect:/agent/login";
		}
		populateAgentModel(model, agent);
		return null;
	}

	private void populateAgentModel(Model model, Agent agent) {
		model.addAttribute("agentName", agent.getFullName());
		model.addAttribute("agentEmail", agent.getEmail());
		model.addAttribute("agentCode", agent.getAgentCode());
		model.addAttribute("agencyName", agent.getAgencyName());
		model.addAttribute("referralCode", agent.getReferralCode());
		model.addAttribute("specialization", formatSpecialization(agent.getSpecialization().name()));
		model.addAttribute("lastLogin", agent.getLastLogin());
		model.addAttribute("profilePhoto", agent.getProfilePhoto());
	}

	private String formatSpecialization(String raw) {
		return raw.replace('_', ' ');
	}
}
