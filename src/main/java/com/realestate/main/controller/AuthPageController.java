package com.realestate.main.controller;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.realestate.main.entity.User;
import com.realestate.main.entity.enums.AccountStatus;
import com.realestate.main.entity.enums.AgentSpecialization;
import com.realestate.main.entity.enums.UserRole;
import com.realestate.main.repository.UserRepository;
import com.realestate.main.security.JwtUserPrincipal;
import com.realestate.main.service.LoginService;
import com.realestate.main.service.PasswordResetService;
import com.realestate.main.util.PropertySpecializationSupport;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/user")
public class AuthPageController {

	private static final DateTimeFormatter MEMBER_SINCE_FMT = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH);

	private final LoginService loginService;
	private final UserRepository userRepository;
	private final PasswordResetService passwordResetService;

	public AuthPageController(LoginService loginService, UserRepository userRepository,
			PasswordResetService passwordResetService) {
		this.loginService = loginService;
		this.userRepository = userRepository;
		this.passwordResetService = passwordResetService;
	}

	@GetMapping("/register")
	public String registerPage() {
		if (isLoggedIn()) {
			return "redirect:/user/dashboard";
		}
		return "user/register";
	}

	@GetMapping("/otp-verification")
	public String otpVerificationPage() {
		return "user/otp-verification";
	}

	@GetMapping("/login")
	public String loginPage() {
		if (isLoggedIn()) {
			return "redirect:/user/dashboard";
		}
		return "user/login";
	}

	@GetMapping("/forgot-password")
	public String forgotPasswordPage() {
		if (isLoggedIn()) {
			return "redirect:/user/dashboard";
		}
		return "user/forgot-password";
	}

	@GetMapping("/reset-password")
	public String resetPasswordPage(@RequestParam(value = "token", required = false) String token, Model model) {
		if (isLoggedIn()) {
			return "redirect:/user/dashboard";
		}
		String safeToken = token != null ? token.trim() : "";
		boolean tokenValid = !safeToken.isEmpty() && passwordResetService.isResetTokenValid(safeToken);
		model.addAttribute("resetToken", safeToken);
		model.addAttribute("tokenValid", tokenValid);
		return "user/reset-password";
	}

	@GetMapping("/dashboard")
	public String dashboard(Model model, @RequestParam(value = "welcome", required = false) String welcome) {
		populateUserNav(model);
		JwtUserPrincipal principal = currentPrincipal();
		User user = userRepository.findById(principal.getUserId())
				.orElseThrow(() -> new IllegalStateException("User not found"));

		String fullName = safeDisplay(user.getFullName());
		String firstName = extractFirstName(fullName);

		model.addAttribute("userName", fullName);
		model.addAttribute("firstName", firstName);
		model.addAttribute("userInitial", initialLetter(firstName));
		model.addAttribute("userEmail", safeDisplay(user.getEmail()));
		model.addAttribute("userMobile", safeDisplay(user.getMobile()));
		model.addAttribute("userRole", formatRole(user.getRole()));
		model.addAttribute("userRoleCode", user.getRole() != null ? user.getRole().name() : "");
		model.addAttribute("userCity", hasText(user.getCity()) ? user.getCity().trim() : "Not set");
		model.addAttribute("userState", hasText(user.getState()) ? user.getState().trim() : "—");
		model.addAttribute("referralCode", hasText(user.getReferralCode()) ? user.getReferralCode() : "—");
		model.addAttribute("accountStatus", formatAccountStatus(user.getAccountStatus()));
		model.addAttribute("accountStatusCode", user.getAccountStatus() != null ? user.getAccountStatus().name() : "ACTIVE");
		model.addAttribute("verified", user.isVerified());
		model.addAttribute("memberSince",
				user.getCreatedAt() != null ? MEMBER_SINCE_FMT.format(user.getCreatedAt()) : "—");
		model.addAttribute("showWelcome", "1".equals(welcome) || "true".equalsIgnoreCase(welcome));
		return "user/user-dashboard";
	}

	@GetMapping("/enquiries")
	public String myEnquiries(Model model) {
		populateUserNav(model);
		return "user/user-enquiries";
	}

	@GetMapping("/properties")
	public String exploreProperties(Model model) {
		List<SpecializationOption> specializationOptions = Arrays.stream(AgentSpecialization.values())
				.map(s -> new SpecializationOption(s.name(),
						PropertySpecializationSupport.formatSpecialization(s)))
				.collect(Collectors.toList());
		model.addAttribute("specializationOptions", specializationOptions);
		boolean loggedIn = isLoggedIn();
		model.addAttribute("loggedIn", loggedIn);
		if (loggedIn) {
			try {
				User user = userRepository.findById(currentPrincipal().getUserId()).orElse(null);
				if (user != null) {
					model.addAttribute("userName", user.getFullName());
				}
			} catch (Exception ignored) {
				model.addAttribute("loggedIn", false);
			}
		}
		return "user/user-explore-properties";
	}

	@GetMapping("/pgs")
	public String explorePgs(Model model) {
		boolean loggedIn = isLoggedIn();
		model.addAttribute("loggedIn", loggedIn);
		String userCity = "";
		String userState = "";
		if (loggedIn) {
			try {
				User user = userRepository.findById(currentPrincipal().getUserId()).orElse(null);
				if (user != null) {
					model.addAttribute("userName", user.getFullName());
					userCity = hasText(user.getCity()) ? user.getCity().trim() : "";
					userState = hasText(user.getState()) ? user.getState().trim() : "";
				}
			} catch (Exception ignored) {
				model.addAttribute("loggedIn", false);
			}
		}
		model.addAttribute("userCity", userCity);
		model.addAttribute("userState", userState);
		return "user/user-explore-pgs";
	}

	@GetMapping("/pgs/{id}/book")
	public String bookPg(@PathVariable Long id, Model model) {
		model.addAttribute("pgId", id);
		boolean loggedIn = isLoggedIn();
		model.addAttribute("loggedIn", loggedIn);
		if (loggedIn) {
			try {
				User user = userRepository.findById(currentPrincipal().getUserId()).orElse(null);
				if (user != null) {
					model.addAttribute("userName", user.getFullName());
				}
			} catch (Exception ignored) {
				model.addAttribute("loggedIn", false);
			}
		}
		return "user/user-pg-book";
	}

	@GetMapping("/pgs/{id}/booking")
	public String pgBookingForm(@PathVariable Long id, Model model) {
		populateUserNav(model);
		model.addAttribute("pgId", id);
		return "user/user-pg-booking-form";
	}

	@GetMapping("/pg-booking/payment")
	public String pgBookingPayment(@RequestParam(required = false) Long bookingId,
			@RequestParam(required = false) Long rentDueId,
			@RequestParam(value = "balance", required = false) String balance, Model model) {
		populateUserNav(model);
		model.addAttribute("bookingId", bookingId != null ? bookingId : 0);
		model.addAttribute("rentDueId", rentDueId != null ? rentDueId : 0);
		model.addAttribute("balancePaymentMode", "1".equals(balance) || "true".equalsIgnoreCase(balance));
		model.addAttribute("rentPaymentMode", rentDueId != null && rentDueId > 0);
		return "user/user-pg-booking-payment";
	}

	@GetMapping("/pg-booking/success")
	public String pgBookingSuccess(@RequestParam Long bookingId, Model model) {
		populateUserNav(model);
		model.addAttribute("bookingId", bookingId);
		return "user/user-pg-booking-success";
	}

	@GetMapping("/properties/{id}/book")
	public String bookProperty(@PathVariable Long id, Model model) {
		List<SpecializationOption> specializationOptions = Arrays.stream(AgentSpecialization.values())
				.map(s -> new SpecializationOption(s.name(),
						PropertySpecializationSupport.formatSpecialization(s)))
				.collect(Collectors.toList());
		model.addAttribute("specializationOptions", specializationOptions);
		model.addAttribute("propertyId", id);
		boolean loggedIn = isLoggedIn();
		model.addAttribute("loggedIn", loggedIn);
		if (loggedIn) {
			try {
				User user = userRepository.findById(currentPrincipal().getUserId()).orElse(null);
				if (user != null) {
					model.addAttribute("userName", user.getFullName());
				}
			} catch (Exception ignored) {
				model.addAttribute("loggedIn", false);
			}
		}
		return "user/user-property-book";
	}

	@GetMapping("/booking/payment")
	public String bookingPayment(@RequestParam(required = false) Long propertyId,
			@RequestParam(required = false) Long bookingId, Model model) {
		populateUserNav(model);
		if (bookingId != null) {
			model.addAttribute("bookingId", bookingId);
			model.addAttribute("balancePaymentMode", Boolean.TRUE);
			return "user/booking-payment";
		}
		if (propertyId == null) {
			return "redirect:/user/bookings";
		}
		model.addAttribute("propertyId", propertyId);
		model.addAttribute("balancePaymentMode", Boolean.FALSE);
		model.addAttribute("razorpayKeyId", "");
		return "user/booking-payment";
	}

	@GetMapping("/booking/success")
	public String bookingSuccess(@RequestParam(required = false) Long bookingId, Model model) {
		populateUserNav(model);
		model.addAttribute("bookingId", bookingId);
		return "user/booking-success";
	}

	@GetMapping("/bookings")
	public String myBookings(Model model) {
		populateUserNav(model);
		return "user/user-my-bookings";
	}

	private static String extractFirstName(String fullName) {
		if (!hasText(fullName)) {
			return "User";
		}
		String trimmed = fullName.trim();
		int space = trimmed.indexOf(' ');
		return space > 0 ? trimmed.substring(0, space) : trimmed;
	}

	private static String initialLetter(String name) {
		if (!hasText(name)) {
			return "U";
		}
		return String.valueOf(Character.toUpperCase(name.trim().charAt(0)));
	}

	private static String formatRole(UserRole role) {
		if (role == null) {
			return "Member";
		}
		return switch (role) {
			case BUYER -> "Property Buyer";
			case SELLER -> "Property Seller";
		};
	}

	private static String formatAccountStatus(AccountStatus status) {
		if (status == null) {
			return "Active";
		}
		return switch (status) {
			case ACTIVE -> "Active";
			case PENDING -> "Pending";
			case LOCKED -> "Locked";
			case DISABLED -> "Disabled";
		};
	}

	private static String safeDisplay(String value) {
		return hasText(value) ? value.trim() : "—";
	}

	private static boolean hasText(String value) {
		return value != null && !value.isBlank();
	}

	@GetMapping("/logout")
	public String logout(HttpServletRequest request) {
		loginService.logout(request);
		return "redirect:/user/login?logout=success";
	}

	public static final class SpecializationOption {
		private final String code;
		private final String label;

		public SpecializationOption(String code, String label) {
			this.code = code;
			this.label = label;
		}

		public String getCode() {
			return code;
		}

		public String getLabel() {
			return label;
		}
	}

	private void populateUserNav(Model model) {
		User user = userRepository.findById(currentPrincipal().getUserId())
				.orElseThrow(() -> new IllegalStateException("User not found"));
		String fullName = safeDisplay(user.getFullName());
		model.addAttribute("userName", fullName);
		model.addAttribute("userInitial", initialLetter(extractFirstName(fullName)));
	}

	private boolean isLoggedIn() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof JwtUserPrincipal;
	}

	private JwtUserPrincipal currentPrincipal() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null && auth.getPrincipal() instanceof JwtUserPrincipal principal) {
			return principal;
		}
		throw new IllegalStateException("Not authenticated");
	}
}
