package com.realestate.main.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.realestate.main.dto.response.AdminAgentPropertyGroupResponse;
import com.realestate.main.dto.response.ApiResponse;
import com.realestate.main.exception.AuthException;
import com.realestate.main.service.AdminPropertyService;
import com.realestate.main.util.AdminSessionConstants;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/admin/properties")
public class AdminPropertyApiController {

	private final AdminPropertyService adminPropertyService;

	public AdminPropertyApiController(AdminPropertyService adminPropertyService) {
		this.adminPropertyService = adminPropertyService;
	}

	@GetMapping
	public ResponseEntity<ApiResponse<List<AdminAgentPropertyGroupResponse>>> listByAgent(
			@RequestParam(value = "status", defaultValue = "ALL") String status,
			@RequestParam(value = "q", required = false) String query,
			HttpSession session) {
		requireAdmin(session);
		return ResponseEntity.ok(ApiResponse.ok("OK", adminPropertyService.listPropertiesByAgent(status, query)));
	}

	@GetMapping("/count")
	public ResponseEntity<ApiResponse<Long>> count(HttpSession session) {
		requireAdmin(session);
		return ResponseEntity.ok(ApiResponse.ok("OK", adminPropertyService.countAllProperties()));
	}

	private void requireAdmin(HttpSession session) {
		if (session == null || session.getAttribute(AdminSessionConstants.ADMIN_ID) == null) {
			throw new AuthException("Admin login required");
		}
	}
}
