package com.realestate.main.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.realestate.main.dto.request.AdminAgentRejectRequest;
import com.realestate.main.dto.response.AgentDetailResponse;
import com.realestate.main.dto.response.AgentListItemResponse;
import com.realestate.main.dto.response.ApiResponse;
import com.realestate.main.exception.AuthException;
import com.realestate.main.service.AdminAgentService;
import com.realestate.main.util.AdminSessionConstants;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/agents")
public class AdminAgentApiController {

	private final AdminAgentService adminAgentService;

	public AdminAgentApiController(AdminAgentService adminAgentService) {
		this.adminAgentService = adminAgentService;
	}

	@GetMapping
	public ResponseEntity<ApiResponse<List<AgentListItemResponse>>> listAgents(
			@RequestParam(value = "status", defaultValue = "PENDING") String status, HttpSession session) {
		requireAdmin(session);
		return ResponseEntity.ok(ApiResponse.ok("OK", adminAgentService.listAgents(status)));
	}

	@GetMapping("/pending-count")
	public ResponseEntity<ApiResponse<Long>> pendingCount(HttpSession session) {
		requireAdmin(session);
		return ResponseEntity.ok(ApiResponse.ok("OK", adminAgentService.countPendingApproval()));
	}

	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<AgentDetailResponse>> getAgent(@PathVariable Long id, HttpSession session) {
		requireAdmin(session);
		return ResponseEntity.ok(ApiResponse.ok("OK", adminAgentService.getAgentDetail(id)));
	}

	@PostMapping("/{id}/approve")
	public ResponseEntity<ApiResponse<AgentDetailResponse>> approve(@PathVariable Long id, HttpSession session) {
		Long adminId = requireAdmin(session);
		AgentDetailResponse agent = adminAgentService.approveAgent(id, adminId);
		return ResponseEntity.ok(ApiResponse.ok("Agent approved successfully", agent));
	}

	@PostMapping("/{id}/reject")
	public ResponseEntity<ApiResponse<AgentDetailResponse>> reject(@PathVariable Long id,
			@Valid @RequestBody AdminAgentRejectRequest request, HttpSession session) {
		Long adminId = requireAdmin(session);
		AgentDetailResponse agent = adminAgentService.rejectAgent(id, adminId, request);
		return ResponseEntity.ok(ApiResponse.ok("Agent application rejected", agent));
	}

	@PostMapping("/{id}/suspend")
	public ResponseEntity<ApiResponse<AgentDetailResponse>> suspend(@PathVariable Long id, HttpSession session) {
		requireAdmin(session);
		return ResponseEntity.ok(ApiResponse.ok("Agent suspended", adminAgentService.suspendAgent(id)));
	}

	@PostMapping("/{id}/reactivate")
	public ResponseEntity<ApiResponse<AgentDetailResponse>> reactivate(@PathVariable Long id, HttpSession session) {
		Long adminId = requireAdmin(session);
		return ResponseEntity.ok(ApiResponse.ok("Agent reactivated", adminAgentService.reactivateAgent(id, adminId)));
	}

	private Long requireAdmin(HttpSession session) {
		if (session == null) {
			throw new AuthException("Admin login required");
		}
		Long adminId = (Long) session.getAttribute(AdminSessionConstants.ADMIN_ID);
		if (adminId == null) {
			throw new AuthException("Admin login required");
		}
		return adminId;
	}
}
