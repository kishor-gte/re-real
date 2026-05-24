package com.realestate.main.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.realestate.main.dto.pgsubscription.PgSubscriptionAnalyticsResponse;
import com.realestate.main.dto.pgsubscription.PgSubscriptionPaymentDto;
import com.realestate.main.dto.pgsubscription.PgSubscriptionPlanDto;
import com.realestate.main.dto.pgsubscription.PgSubscriptionPlanRequest;
import com.realestate.main.dto.response.ApiResponse;
import com.realestate.main.entity.enums.AgentSubscriptionStatus;
import com.realestate.main.entity.enums.SubscriptionPaymentStatus;
import com.realestate.main.entity.enums.SubscriptionPlanStatus;
import com.realestate.main.service.pgsubscription.AdminPgSubscriptionService;
import com.realestate.main.service.pgsubscription.AdminPgSubscriptionService.PgOwnerSubscriptionAdminView;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/pg-subscriptions")
@Validated
public class AdminPgSubscriptionApiController {

	private final AdminPgSubscriptionService adminPgSubscriptionService;

	public AdminPgSubscriptionApiController(AdminPgSubscriptionService adminPgSubscriptionService) {
		this.adminPgSubscriptionService = adminPgSubscriptionService;
	}

	@GetMapping("/plans")
	public ResponseEntity<ApiResponse<List<PgSubscriptionPlanDto>>> listPlans() {
		return ResponseEntity.ok(ApiResponse.ok("OK", adminPgSubscriptionService.listAllPlans()));
	}

	@PostMapping("/plans")
	public ResponseEntity<ApiResponse<PgSubscriptionPlanDto>> createPlan(
			@Valid @RequestBody PgSubscriptionPlanRequest request) {
		return ResponseEntity.ok(ApiResponse.ok("Plan created", adminPgSubscriptionService.createPlan(request)));
	}

	@PutMapping("/plans/{id}")
	public ResponseEntity<ApiResponse<PgSubscriptionPlanDto>> updatePlan(@PathVariable Long id,
			@Valid @RequestBody PgSubscriptionPlanRequest request) {
		return ResponseEntity.ok(ApiResponse.ok("Plan updated", adminPgSubscriptionService.updatePlan(id, request)));
	}

	@PatchMapping("/plans/{id}/status")
	public ResponseEntity<ApiResponse<PgSubscriptionPlanDto>> planStatus(@PathVariable Long id,
			@RequestParam SubscriptionPlanStatus status) {
		return ResponseEntity.ok(ApiResponse.ok("Plan status updated",
				adminPgSubscriptionService.setPlanStatus(id, status)));
	}

	@GetMapping("/owner-subscriptions")
	public ResponseEntity<ApiResponse<List<PgOwnerSubscriptionAdminView>>> ownerSubscriptions() {
		return ResponseEntity.ok(ApiResponse.ok("OK", adminPgSubscriptionService.listOwnerSubscriptions()));
	}

	@GetMapping("/active")
	public ResponseEntity<ApiResponse<List<PgOwnerSubscriptionAdminView>>> active() {
		return ResponseEntity.ok(ApiResponse.ok("OK", adminPgSubscriptionService.listActiveSubscriptions()));
	}

	@GetMapping("/payments")
	public ResponseEntity<ApiResponse<List<PgSubscriptionPaymentDto>>> payments() {
		return ResponseEntity.ok(ApiResponse.ok("OK", adminPgSubscriptionService.listPayments()));
	}

	@PatchMapping("/payments/{id}/status")
	public ResponseEntity<ApiResponse<PgSubscriptionPaymentDto>> paymentStatus(@PathVariable Long id,
			@RequestParam SubscriptionPaymentStatus status) {
		return ResponseEntity.ok(ApiResponse.ok("Payment updated",
				adminPgSubscriptionService.setPaymentStatus(id, status)));
	}

	@PatchMapping("/owner-subscriptions/{id}/status")
	public ResponseEntity<ApiResponse<PgOwnerSubscriptionAdminView>> subscriptionStatus(@PathVariable Long id,
			@RequestParam AgentSubscriptionStatus status) {
		return ResponseEntity.ok(ApiResponse.ok("Subscription updated",
				adminPgSubscriptionService.setSubscriptionStatus(id, status)));
	}

	@GetMapping("/analytics")
	public ResponseEntity<ApiResponse<PgSubscriptionAnalyticsResponse>> analytics() {
		return ResponseEntity.ok(ApiResponse.ok("OK", adminPgSubscriptionService.analytics()));
	}
}
