package com.realestate.main.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.realestate.main.dto.response.AgentBookingDetailResponse;
import com.realestate.main.dto.response.ApiResponse;
import com.realestate.main.service.AgentBookingService;
import com.realestate.main.util.AgentSessionHelper;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/agent/bookings")
public class AgentBookingApiController {

	private final AgentBookingService agentBookingService;

	public AgentBookingApiController(AgentBookingService agentBookingService) {
		this.agentBookingService = agentBookingService;
	}

	@GetMapping
	public ResponseEntity<ApiResponse<List<AgentBookingDetailResponse>>> list(HttpSession session) {
		Long agentId = AgentSessionHelper.requireAgentId(session);
		return ResponseEntity.ok(ApiResponse.ok("OK", agentBookingService.listForAgent(agentId)));
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<ApiResponse<AgentBookingDetailResponse>> get(@PathVariable Long bookingId,
			HttpSession session) {
		Long agentId = AgentSessionHelper.requireAgentId(session);
		return ResponseEntity.ok(ApiResponse.ok("OK", agentBookingService.getForAgent(agentId, bookingId)));
	}
}
