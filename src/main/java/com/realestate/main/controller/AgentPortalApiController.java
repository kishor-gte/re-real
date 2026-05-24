package com.realestate.main.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.realestate.main.dto.response.AgentAnalyticsResponse;
import com.realestate.main.dto.response.AgentEarningsResponse;
import com.realestate.main.dto.response.AgentNotificationsSummaryResponse;
import com.realestate.main.dto.response.AgentReferralEarningsResponse;
import com.realestate.main.dto.response.ApiResponse;
import com.realestate.main.service.AgentAnalyticsService;
import com.realestate.main.service.AgentEarningsService;
import com.realestate.main.service.AgentNotificationFeedService;
import com.realestate.main.service.AgentReferralEarningsService;
import com.realestate.main.util.AgentSessionHelper;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/agent/portal")
public class AgentPortalApiController {

	private final AgentAnalyticsService analyticsService;
	private final AgentEarningsService earningsService;
	private final AgentReferralEarningsService referralEarningsService;
	private final AgentNotificationFeedService notificationFeedService;

	public AgentPortalApiController(AgentAnalyticsService analyticsService,
			AgentEarningsService earningsService, AgentReferralEarningsService referralEarningsService,
			AgentNotificationFeedService notificationFeedService) {
		this.analyticsService = analyticsService;
		this.earningsService = earningsService;
		this.referralEarningsService = referralEarningsService;
		this.notificationFeedService = notificationFeedService;
	}

	@GetMapping("/analytics")
	public ResponseEntity<ApiResponse<AgentAnalyticsResponse>> analytics(HttpSession session) {
		Long agentId = AgentSessionHelper.requireAgentId(session);
		return ResponseEntity.ok(ApiResponse.ok("OK", analyticsService.loadAnalytics(agentId)));
	}

	@GetMapping("/earnings")
	public ResponseEntity<ApiResponse<AgentEarningsResponse>> earnings(HttpSession session) {
		Long agentId = AgentSessionHelper.requireAgentId(session);
		return ResponseEntity.ok(ApiResponse.ok("OK", earningsService.loadEarnings(agentId)));
	}

	@GetMapping("/referrals")
	public ResponseEntity<ApiResponse<AgentReferralEarningsResponse>> referrals(HttpSession session) {
		Long agentId = AgentSessionHelper.requireAgentId(session);
		return ResponseEntity.ok(ApiResponse.ok("OK", referralEarningsService.loadReferralEarnings(agentId)));
	}

	@GetMapping("/notifications")
	public ResponseEntity<ApiResponse<AgentNotificationsSummaryResponse>> notifications(
			@RequestParam(value = "category", required = false, defaultValue = "ALL") String category,
			HttpSession session) {
		Long agentId = AgentSessionHelper.requireAgentId(session);
		return ResponseEntity.ok(ApiResponse.ok("OK", notificationFeedService.loadFeed(agentId, category)));
	}
}
