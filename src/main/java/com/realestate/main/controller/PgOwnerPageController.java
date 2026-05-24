package com.realestate.main.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.realestate.main.entity.PgOwner;
import com.realestate.main.entity.enums.AccountStatus;
import com.realestate.main.repository.PgOwnerRepository;
import com.realestate.main.service.PgOwnerLoginService;
import com.realestate.main.service.PgOwnerLookupService;
import com.realestate.main.service.PgOwnerPasswordResetService;
import com.realestate.main.service.pgsubscription.PgOwnerPostingLimitService;
import com.realestate.main.dto.pgsubscription.PgOwnerPostingStatusResponse;
import com.realestate.main.util.PgOwnerSessionConstants;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/pg-owner")
public class PgOwnerPageController {

	private final PgOwnerLoginService pgOwnerLoginService;
	private final PgOwnerRepository pgOwnerRepository;
	private final PgOwnerPasswordResetService pgOwnerPasswordResetService;
	private final PgOwnerPostingLimitService postingLimitService;

	public PgOwnerPageController(PgOwnerLoginService pgOwnerLoginService, PgOwnerRepository pgOwnerRepository,
			PgOwnerPasswordResetService pgOwnerPasswordResetService,
			PgOwnerPostingLimitService postingLimitService) {
		this.pgOwnerLoginService = pgOwnerLoginService;
		this.pgOwnerRepository = pgOwnerRepository;
		this.pgOwnerPasswordResetService = pgOwnerPasswordResetService;
		this.postingLimitService = postingLimitService;
	}

	@GetMapping("/register")
	public String registerPage(HttpSession session) {
		if (isLoggedIn(session)) {
			return "redirect:/pg-owner/dashboard";
		}
		return "pg-owner/pg-owner-register";
	}

	@GetMapping("/otp-verification")
	public String otpPage() {
		return "pg-owner/pg-owner-otp-verification";
	}

	@GetMapping("/login")
	public String loginPage(HttpSession session, Model model,
			@RequestParam(value = "pending", required = false) String pending) {
		if (isLoggedIn(session)) {
			return "redirect:/pg-owner/dashboard";
		}
		if ("1".equals(pending)) {
			model.addAttribute("showPendingMessage", true);
		}
		return "pg-owner/pg-owner-login";
	}

	@GetMapping("/pending-approval")
	public String pendingApprovalPage(@RequestParam(value = "email", required = false) String email, Model model) {
		model.addAttribute("email", email != null ? PgOwnerLookupService.normalizeEmail(email) : "");
		return "pg-owner/pg-owner-pending-approval";
	}

	@GetMapping("/forgot-password")
	public String forgotPasswordPage(HttpSession session) {
		if (isLoggedIn(session)) {
			return "redirect:/pg-owner/dashboard";
		}
		return "pg-owner/pg-owner-forgot-password";
	}

	@GetMapping("/reset-password")
	public String resetPasswordPage(@RequestParam(value = "token", required = false) String token, Model model,
			HttpSession session) {
		if (isLoggedIn(session)) {
			return "redirect:/pg-owner/dashboard";
		}
		String safeToken = token != null ? token.trim() : "";
		model.addAttribute("resetToken", safeToken);
		model.addAttribute("tokenValid", !safeToken.isEmpty() && pgOwnerPasswordResetService.isTokenValid(safeToken));
		return "pg-owner/pg-owner-reset-password";
	}

	@GetMapping("/dashboard")
	public String dashboard(HttpSession session, Model model, HttpServletRequest request) {
		String redirect = guardActivePgOwner(session, request, model);
		if (redirect != null) {
			return redirect;
		}
		return "pg-owner/pg-owner-dashboard";
	}

	@GetMapping("/properties")
	public String manageProperties(HttpSession session, Model model, HttpServletRequest request) {
		String redirect = guardActivePgOwner(session, request, model);
		if (redirect != null) {
			return redirect;
		}
		return "pg-owner/manage-pg";
	}

	@GetMapping("/properties/add")
	public String addProperty(HttpSession session, Model model, HttpServletRequest request) {
		String redirect = guardActivePgOwner(session, request, model);
		if (redirect != null) {
			return redirect;
		}
		Long pgOwnerId = (Long) session.getAttribute(PgOwnerSessionConstants.PG_OWNER_ID);
		PgOwnerPostingStatusResponse posting = postingLimitService.getPostingStatus(pgOwnerId);
		if (!posting.isCanPost()) {
			return "redirect:/pg-owner/subscription/plans?limit=1";
		}
		model.addAttribute("editMode", false);
		model.addAttribute("propertyId", null);
		return "pg-owner/add-pg";
	}

	@GetMapping("/subscription")
	public String subscription(HttpSession session, Model model, HttpServletRequest request) {
		String redirect = guardActivePgOwner(session, request, model);
		if (redirect != null) {
			return redirect;
		}
		return "pg-owner/pgowner-subscriptions";
	}

	@GetMapping("/subscription/plans")
	public String subscriptionPlans(HttpSession session, Model model, HttpServletRequest request) {
		String redirect = guardActivePgOwner(session, request, model);
		if (redirect != null) {
			return redirect;
		}
		return "pg-owner/pgowner-subscription-plans";
	}

	@GetMapping("/subscription/payment")
	public String subscriptionPayment(@RequestParam(value = "subscriptionId", required = false) Long subscriptionId,
			HttpSession session, Model model, HttpServletRequest request) {
		String redirect = guardActivePgOwner(session, request, model);
		if (redirect != null) {
			return redirect;
		}
		model.addAttribute("subscriptionId", subscriptionId);
		return "pg-owner/pg-subscription-payment";
	}

	@GetMapping("/subscription/success")
	public String subscriptionSuccess(HttpSession session, Model model, HttpServletRequest request) {
		String redirect = guardActivePgOwner(session, request, model);
		if (redirect != null) {
			return redirect;
		}
		return "pg-owner/pg-subscription-success";
	}

	@GetMapping("/notifications")
	public String notifications(HttpSession session, Model model, HttpServletRequest request) {
		String redirect = guardActivePgOwner(session, request, model);
		if (redirect != null) {
			return redirect;
		}
		return "pg-owner/pg-owner-notifications";
	}

	@GetMapping("/settings")
	public String settings(HttpSession session, Model model, HttpServletRequest request) {
		String redirect = guardActivePgOwner(session, request, model);
		if (redirect != null) {
			return redirect;
		}
		return "pg-owner/pg-owner-settings";
	}

	@GetMapping("/tenants")
	public String tenants(HttpSession session, Model model, HttpServletRequest request) {
		String redirect = guardActivePgOwner(session, request, model);
		if (redirect != null) {
			return redirect;
		}
		return "pg-owner/pg-owner-tenants";
	}

	@GetMapping("/properties/{id}")
	public String propertyDetails(@PathVariable Long id, HttpSession session,
			Model model, HttpServletRequest request) {
		String redirect = guardActivePgOwner(session, request, model);
		if (redirect != null) {
			return redirect;
		}
		model.addAttribute("propertyId", id);
		return "pg-owner/pg-details";
	}

	@GetMapping("/properties/{id}/edit")
	public String editProperty(@PathVariable Long id, HttpSession session,
			Model model, HttpServletRequest request) {
		String redirect = guardActivePgOwner(session, request, model);
		if (redirect != null) {
			return redirect;
		}
		model.addAttribute("editMode", true);
		model.addAttribute("propertyId", id);
		return "pg-owner/edit-pg";
	}

	@GetMapping("/properties/{id}/gallery")
	public String propertyGallery(@PathVariable Long id, HttpSession session,
			Model model, HttpServletRequest request) {
		String redirect = guardActivePgOwner(session, request, model);
		if (redirect != null) {
			return redirect;
		}
		model.addAttribute("propertyId", id);
		return "pg-owner/pg-gallery";
	}

	@GetMapping("/bookings")
	public String userBookings(HttpSession session, Model model, HttpServletRequest request) {
		String redirect = guardActivePgOwner(session, request, model);
		if (redirect != null) {
			return redirect;
		}
		return "pg-owner/pg-owner-user-bookings";
	}

	@GetMapping("/logout")
	public String logout(HttpServletRequest request) {
		pgOwnerLoginService.logout(request);
		return "redirect:/pg-owner/login?logout=success";
	}

	private boolean isLoggedIn(HttpSession session) {
		return session != null && session.getAttribute(PgOwnerSessionConstants.PG_OWNER_ID) != null;
	}

	private String guardActivePgOwner(HttpSession session, HttpServletRequest request, Model model) {
		Long pgOwnerId = (Long) session.getAttribute(PgOwnerSessionConstants.PG_OWNER_ID);
		if (pgOwnerId == null) {
			return "redirect:/pg-owner/login";
		}
		PgOwner owner = pgOwnerRepository.findById(pgOwnerId).orElse(null);
		if (owner == null) {
			pgOwnerLoginService.logout(request);
			return "redirect:/pg-owner/login";
		}
		if (owner.getAccountStatus() != AccountStatus.ACTIVE) {
			pgOwnerLoginService.logout(request);
			if (owner.getAccountStatus() == AccountStatus.PENDING) {
				return "redirect:/pg-owner/pending-approval?email=" + owner.getEmail();
			}
			return "redirect:/pg-owner/login";
		}
		populatePgOwnerModel(model, owner);
		return null;
	}

	private void populatePgOwnerModel(Model model, PgOwner owner) {
		model.addAttribute("pgOwnerName", owner.getFullName());
		model.addAttribute("pgOwnerEmail", owner.getEmail());
		model.addAttribute("pgOwnerMobile", owner.getMobile());
		model.addAttribute("pgOwnerCode", owner.getPgOwnerCode());
		model.addAttribute("pgName", owner.getPgName() != null ? owner.getPgName() : "Not added yet");
		model.addAttribute("pgType", owner.getPgType() != null ? owner.getPgType().name().replace('_', ' ') : "—");
		model.addAttribute("genderAllowed", owner.getGenderAllowed() != null ? owner.getGenderAllowed().name() : "—");
		model.addAttribute("lastLogin", owner.getLastLogin());
		model.addAttribute("profilePhoto", owner.getProfilePhoto());
	}
}
