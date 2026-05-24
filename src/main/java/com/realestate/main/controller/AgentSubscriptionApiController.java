package com.realestate.main.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.realestate.main.dto.response.ApiResponse;
import com.realestate.main.dto.subscription.AgentPostingStatusResponse;
import com.realestate.main.dto.subscription.AgentSubscriptionOverviewResponse;
import com.realestate.main.dto.subscription.SubscriptionCheckoutResponse;
import com.realestate.main.dto.subscription.SubscriptionPaymentVerifyRequest;
import com.realestate.main.dto.subscription.SubscriptionPlanDto;
import com.realestate.main.service.subscription.AgentPostingLimitService;
import com.realestate.main.service.subscription.AgentSubscriptionService;
import com.realestate.main.util.AgentSessionHelper;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/agent/subscription")
@Validated
public class AgentSubscriptionApiController {

	private final AgentSubscriptionService subscriptionService;
	private final AgentPostingLimitService postingLimitService;

	public AgentSubscriptionApiController(AgentSubscriptionService subscriptionService,
			AgentPostingLimitService postingLimitService) {
		this.subscriptionService = subscriptionService;
		this.postingLimitService = postingLimitService;
	}

	@GetMapping("/posting-status")
	public ResponseEntity<ApiResponse<AgentPostingStatusResponse>> postingStatus(HttpSession session) {
		Long agentId = AgentSessionHelper.requireAgentId(session);
		return ResponseEntity.ok(ApiResponse.ok("OK", postingLimitService.getPostingStatus(agentId)));
	}

	@GetMapping("/overview")
	public ResponseEntity<ApiResponse<AgentSubscriptionOverviewResponse>> overview(HttpSession session) {
		Long agentId = AgentSessionHelper.requireAgentId(session);
		return ResponseEntity.ok(ApiResponse.ok("OK", subscriptionService.getOverview(agentId)));
	}

	@GetMapping("/plans")
	public ResponseEntity<ApiResponse<List<SubscriptionPlanDto>>> plans(HttpSession session) {
		Long agentId = AgentSessionHelper.requireAgentId(session);
		return ResponseEntity.ok(ApiResponse.ok("OK", subscriptionService.listActivePlansForAgent(agentId)));
	}

	@PostMapping("/checkout/{planId}")
	public ResponseEntity<ApiResponse<SubscriptionCheckoutResponse>> checkout(@PathVariable Long planId,
			HttpSession session) {
		Long agentId = AgentSessionHelper.requireAgentId(session);
		return ResponseEntity.ok(ApiResponse.ok("Checkout ready", subscriptionService.startCheckout(agentId, planId)));
	}

	@PostMapping("/payment/verify")
	public ResponseEntity<ApiResponse<AgentSubscriptionOverviewResponse>> verify(
			@Valid @RequestBody SubscriptionPaymentVerifyRequest request, HttpSession session) {
		Long agentId = AgentSessionHelper.requireAgentId(session);
		return ResponseEntity
				.ok(ApiResponse.ok("Subscription activated", subscriptionService.verifyPayment(agentId, request)));
	}

	@PostMapping("/payment/demo/{subscriptionId}")
	public ResponseEntity<ApiResponse<AgentSubscriptionOverviewResponse>> demoPay(@PathVariable Long subscriptionId,
			HttpSession session) {
		Long agentId = AgentSessionHelper.requireAgentId(session);
		return ResponseEntity.ok(ApiResponse.ok("Demo payment completed",
				subscriptionService.activateDemoPayment(agentId, subscriptionId)));
	}
}
