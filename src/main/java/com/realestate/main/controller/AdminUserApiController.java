package com.realestate.main.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.realestate.main.dto.response.ApiResponse;
import com.realestate.main.dto.response.UserListItemResponse;
import com.realestate.main.exception.AuthException;
import com.realestate.main.service.AdminUserService;
import com.realestate.main.util.AdminSessionConstants;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserApiController {

	private final AdminUserService adminUserService;

	public AdminUserApiController(AdminUserService adminUserService) {
		this.adminUserService = adminUserService;
	}

	@GetMapping
	public ResponseEntity<ApiResponse<List<UserListItemResponse>>> listUsers(
			@RequestParam(value = "role", defaultValue = "ALL") String role,
			@RequestParam(value = "status", defaultValue = "ALL") String status,
			@RequestParam(value = "q", required = false) String query,
			HttpSession session) {
		requireAdmin(session);
		return ResponseEntity.ok(ApiResponse.ok("OK", adminUserService.listUsers(role, status, query)));
	}

	private void requireAdmin(HttpSession session) {
		if (session == null || session.getAttribute(AdminSessionConstants.ADMIN_ID) == null) {
			throw new AuthException("Admin login required");
		}
	}
}
