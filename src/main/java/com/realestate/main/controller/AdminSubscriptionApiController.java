package com.realestate.main.controller;

import java.util.List;
import java.util.Map;

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

import com.realestate.main.dto.response.ApiResponse;
import com.realestate.main.dto.subscription.SubscriptionAnalyticsResponse;
import com.realestate.main.dto.subscription.SubscriptionPaymentDto;
import com.realestate.main.dto.subscription.SubscriptionPlanDto;
import com.realestate.main.dto.subscription.SubscriptionPlanRequest;
import com.realestate.main.entity.enums.AgentSubscriptionStatus;
import com.realestate.main.entity.enums.SubscriptionPaymentStatus;
import com.realestate.main.entity.enums.SubscriptionPlanStatus;
import com.realestate.main.service.subscription.AdminSubscriptionService;
import com.realestate.main.service.subscription.AdminSubscriptionService.AgentSubscriptionAdminView;
import com.realestate.main.util.AdminSessionHelper;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/subscriptions")
@Validated
public class AdminSubscriptionApiController {

	private final AdminSubscriptionService adminSubscriptionService;

	public AdminSubscriptionApiController(AdminSubscriptionService adminSubscriptionService) {
		this.adminSubscriptionService = adminSubscriptionService;
	}

	@GetMapping("/plans")
	public ResponseEntity<ApiResponse<List<SubscriptionPlanDto>>> listPlans(HttpSession session) {
		AdminSessionHelper.requireAdminId(session);
		return ResponseEntity.ok(ApiResponse.ok("OK", adminSubscriptionService.listAllPlans()));
	}

	@PostMapping("/plans")
	public ResponseEntity<ApiResponse<SubscriptionPlanDto>> createPlan(@Valid @RequestBody SubscriptionPlanRequest request,
			HttpSession session) {
		AdminSessionHelper.requireAdminId(session);
		return ResponseEntity.ok(ApiResponse.ok("Plan created", adminSubscriptionService.createPlan(request)));
	}

	@PutMapping("/plans/{id}")
	public ResponseEntity<ApiResponse<SubscriptionPlanDto>> updatePlan(@PathVariable Long id,
			@Valid @RequestBody SubscriptionPlanRequest request, HttpSession session) {
		AdminSessionHelper.requireAdminId(session);
		return ResponseEntity.ok(ApiResponse.ok("Plan updated", adminSubscriptionService.updatePlan(id, request)));
	}

	@PatchMapping("/plans/{id}/status")
	public ResponseEntity<ApiResponse<SubscriptionPlanDto>> planStatus(@PathVariable Long id,
			@RequestParam SubscriptionPlanStatus status, HttpSession session) {
		AdminSessionHelper.requireAdminId(session);
		return ResponseEntity.ok(ApiResponse.ok("Plan status updated", adminSubscriptionService.setPlanStatus(id, status)));
	}

	@GetMapping("/agent-subscriptions")
	public ResponseEntity<ApiResponse<List<AgentSubscriptionAdminView>>> agentSubscriptions(HttpSession session) {
		AdminSessionHelper.requireAdminId(session);
		return ResponseEntity.ok(ApiResponse.ok("OK", adminSubscriptionService.listAgentSubscriptions()));
	}

	@GetMapping("/active")
	public ResponseEntity<ApiResponse<List<AgentSubscriptionAdminView>>> active(HttpSession session) {
		AdminSessionHelper.requireAdminId(session);
		return ResponseEntity.ok(ApiResponse.ok("OK", adminSubscriptionService.listActiveSubscriptions()));
	}

	@PatchMapping("/agent-subscriptions/{id}/status")
	public ResponseEntity<ApiResponse<AgentSubscriptionAdminView>> subscriptionStatus(@PathVariable Long id,
			@RequestParam AgentSubscriptionStatus status, HttpSession session) {
		AdminSessionHelper.requireAdminId(session);
		return ResponseEntity.ok(ApiResponse.ok("Updated",
				adminSubscriptionService.setSubscriptionStatus(id, status)));
	}

	@GetMapping("/payments")
	public ResponseEntity<ApiResponse<List<SubscriptionPaymentDto>>> payments(HttpSession session) {
		AdminSessionHelper.requireAdminId(session);
		return ResponseEntity.ok(ApiResponse.ok("OK", adminSubscriptionService.listPayments()));
	}

	@PatchMapping("/payments/{id}/status")
	public ResponseEntity<ApiResponse<SubscriptionPaymentDto>> paymentStatus(@PathVariable Long id,
			@RequestParam SubscriptionPaymentStatus status, HttpSession session) {
		AdminSessionHelper.requireAdminId(session);
		return ResponseEntity.ok(ApiResponse.ok("Payment updated", adminSubscriptionService.setPaymentStatus(id, status)));
	}

	@GetMapping("/analytics")
	public ResponseEntity<ApiResponse<SubscriptionAnalyticsResponse>> analytics(HttpSession session) {
		AdminSessionHelper.requireAdminId(session);
		return ResponseEntity.ok(ApiResponse.ok("OK", adminSubscriptionService.analytics()));
	}
}
