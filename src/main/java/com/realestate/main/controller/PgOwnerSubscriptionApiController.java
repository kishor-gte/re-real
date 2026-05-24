package com.realestate.main.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.realestate.main.dto.pgsubscription.PgOwnerDashboardStatsResponse;
import com.realestate.main.dto.pgsubscription.PgOwnerPostingStatusResponse;
import com.realestate.main.dto.pgsubscription.PgOwnerSubscriptionOverviewResponse;
import com.realestate.main.dto.pgsubscription.PgSubscriptionCheckoutResponse;
import com.realestate.main.dto.pgsubscription.PgSubscriptionPaymentVerifyRequest;
import com.realestate.main.dto.pgsubscription.PgSubscriptionPlanDto;
import com.realestate.main.dto.response.ApiResponse;
import com.realestate.main.entity.PgSubscriptionNotification;
import com.realestate.main.repository.PgOwnerRepository;
import com.realestate.main.repository.PgSubscriptionNotificationRepository;
import com.realestate.main.service.pgsubscription.PgOwnerDashboardService;
import com.realestate.main.service.pgsubscription.PgOwnerPostingLimitService;
import com.realestate.main.service.pgsubscription.PgOwnerSubscriptionService;
import com.realestate.main.util.PgOwnerSessionHelper;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/pg-owner/subscription")
@Validated
public class PgOwnerSubscriptionApiController {

	private final PgOwnerSubscriptionService subscriptionService;
	private final PgOwnerPostingLimitService postingLimitService;
	private final PgOwnerDashboardService dashboardService;
	private final PgSubscriptionNotificationRepository notificationRepository;
	private final PgOwnerRepository pgOwnerRepository;

	public PgOwnerSubscriptionApiController(PgOwnerSubscriptionService subscriptionService,
			PgOwnerPostingLimitService postingLimitService, PgOwnerDashboardService dashboardService,
			PgSubscriptionNotificationRepository notificationRepository, PgOwnerRepository pgOwnerRepository) {
		this.subscriptionService = subscriptionService;
		this.postingLimitService = postingLimitService;
		this.dashboardService = dashboardService;
		this.notificationRepository = notificationRepository;
		this.pgOwnerRepository = pgOwnerRepository;
	}

	@GetMapping("/dashboard-stats")
	public ResponseEntity<ApiResponse<PgOwnerDashboardStatsResponse>> dashboardStats(HttpSession session) {
		Long ownerId = PgOwnerSessionHelper.requirePgOwnerId(session);
		return ResponseEntity.ok(ApiResponse.ok("OK", dashboardService.loadStats(ownerId)));
	}

	@GetMapping("/posting-status")
	public ResponseEntity<ApiResponse<PgOwnerPostingStatusResponse>> postingStatus(HttpSession session) {
		Long ownerId = PgOwnerSessionHelper.requirePgOwnerId(session);
		return ResponseEntity.ok(ApiResponse.ok("OK", postingLimitService.getPostingStatus(ownerId)));
	}

	@GetMapping("/overview")
	public ResponseEntity<ApiResponse<PgOwnerSubscriptionOverviewResponse>> overview(HttpSession session) {
		Long ownerId = PgOwnerSessionHelper.requirePgOwnerId(session);
		return ResponseEntity.ok(ApiResponse.ok("OK", subscriptionService.getOverview(ownerId)));
	}

	@GetMapping("/plans")
	public ResponseEntity<ApiResponse<List<PgSubscriptionPlanDto>>> plans(HttpSession session) {
		Long ownerId = PgOwnerSessionHelper.requirePgOwnerId(session);
		return ResponseEntity.ok(ApiResponse.ok("OK", subscriptionService.listActivePlansForOwner(ownerId)));
	}

	@PostMapping("/checkout/{planId}")
	public ResponseEntity<ApiResponse<PgSubscriptionCheckoutResponse>> checkout(@PathVariable Long planId,
			HttpSession session) {
		Long ownerId = PgOwnerSessionHelper.requirePgOwnerId(session);
		return ResponseEntity.ok(ApiResponse.ok("Checkout ready", subscriptionService.startCheckout(ownerId, planId)));
	}

	@PostMapping("/payment/verify")
	public ResponseEntity<ApiResponse<PgOwnerSubscriptionOverviewResponse>> verify(
			@Valid @RequestBody PgSubscriptionPaymentVerifyRequest request, HttpSession session) {
		Long ownerId = PgOwnerSessionHelper.requirePgOwnerId(session);
		return ResponseEntity.ok(ApiResponse.ok("Subscription activated",
				subscriptionService.verifyPayment(ownerId, request)));
	}

	@PostMapping("/payment/demo/{subscriptionId}")
	public ResponseEntity<ApiResponse<PgOwnerSubscriptionOverviewResponse>> demoPay(
			@PathVariable Long subscriptionId, HttpSession session) {
		Long ownerId = PgOwnerSessionHelper.requirePgOwnerId(session);
		return ResponseEntity.ok(ApiResponse.ok("Subscription activated (demo)",
				subscriptionService.activateDemoPayment(ownerId, subscriptionId)));
	}

	@GetMapping("/notifications")
	public ResponseEntity<ApiResponse<List<PgSubscriptionNotification>>> notifications(HttpSession session) {
		Long ownerId = PgOwnerSessionHelper.requirePgOwnerId(session);
		PgOwnerSessionHelper.requireActivePgOwner(session, pgOwnerRepository);
		return ResponseEntity.ok(ApiResponse.ok("OK",
				notificationRepository.findByPgOwnerIdOrderByCreatedAtDesc(ownerId)));
	}
}
