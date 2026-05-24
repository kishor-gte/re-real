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
import com.realestate.main.dto.response.ApiResponse;
import com.realestate.main.dto.response.PgOwnerDetailResponse;
import com.realestate.main.dto.response.PgOwnerListItemResponse;
import com.realestate.main.exception.AuthException;
import com.realestate.main.service.AdminPgOwnerService;
import com.realestate.main.util.AdminSessionConstants;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/pg-owners")
public class AdminPgOwnerApiController {

	private final AdminPgOwnerService adminPgOwnerService;

	public AdminPgOwnerApiController(AdminPgOwnerService adminPgOwnerService) {
		this.adminPgOwnerService = adminPgOwnerService;
	}

	@GetMapping
	public ResponseEntity<ApiResponse<List<PgOwnerListItemResponse>>> listPgOwners(
			@RequestParam(value = "status", defaultValue = "PENDING") String status, HttpSession session) {
		requireAdmin(session);
		return ResponseEntity.ok(ApiResponse.ok("OK", adminPgOwnerService.listPgOwners(status)));
	}

	@GetMapping("/pending-count")
	public ResponseEntity<ApiResponse<Long>> pendingCount(HttpSession session) {
		requireAdmin(session);
		return ResponseEntity.ok(ApiResponse.ok("OK", adminPgOwnerService.countPendingApproval()));
	}

	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<PgOwnerDetailResponse>> getPgOwner(@PathVariable Long id, HttpSession session) {
		requireAdmin(session);
		return ResponseEntity.ok(ApiResponse.ok("OK", adminPgOwnerService.getPgOwnerDetail(id)));
	}

	@PostMapping("/{id}/approve")
	public ResponseEntity<ApiResponse<PgOwnerDetailResponse>> approve(@PathVariable Long id, HttpSession session) {
		Long adminId = requireAdmin(session);
		PgOwnerDetailResponse owner = adminPgOwnerService.approvePgOwner(id, adminId);
		return ResponseEntity.ok(ApiResponse.ok("PG owner approved successfully", owner));
	}

	@PostMapping("/{id}/reject")
	public ResponseEntity<ApiResponse<PgOwnerDetailResponse>> reject(@PathVariable Long id,
			@Valid @RequestBody AdminAgentRejectRequest request, HttpSession session) {
		Long adminId = requireAdmin(session);
		PgOwnerDetailResponse owner = adminPgOwnerService.rejectPgOwner(id, adminId, request);
		return ResponseEntity.ok(ApiResponse.ok("PG owner application rejected", owner));
	}

	@PostMapping("/{id}/suspend")
	public ResponseEntity<ApiResponse<PgOwnerDetailResponse>> suspend(@PathVariable Long id, HttpSession session) {
		requireAdmin(session);
		return ResponseEntity.ok(ApiResponse.ok("PG owner suspended", adminPgOwnerService.suspendPgOwner(id)));
	}

	@PostMapping("/{id}/reactivate")
	public ResponseEntity<ApiResponse<PgOwnerDetailResponse>> reactivate(@PathVariable Long id, HttpSession session) {
		Long adminId = requireAdmin(session);
		return ResponseEntity.ok(ApiResponse.ok("PG owner reactivated",
				adminPgOwnerService.reactivatePgOwner(id, adminId)));
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
