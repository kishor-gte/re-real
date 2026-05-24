package com.realestate.main.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.realestate.main.dto.response.ApiResponse;
import com.realestate.main.dto.response.UserResponse;
import com.realestate.main.service.UserService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/user")
public class UserController {

	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@GetMapping("/profile")
	public ResponseEntity<ApiResponse<UserResponse>> getProfile(HttpServletRequest request) {
		UserResponse user = userService.getLoggedInUserResponse(request);
		return ResponseEntity.ok(ApiResponse.ok("Profile loaded", user));
	}

	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {
		UserResponse user = userService.toUserResponse(userService.getById(id));
		return ResponseEntity.ok(ApiResponse.ok("User found", user));
	}

	@GetMapping("/check-email")
	public ResponseEntity<ApiResponse<Boolean>> checkEmail(@RequestParam String email) {
		boolean available = userService.isEmailAvailable(email);
		return ResponseEntity.ok(ApiResponse.ok("OK", available));
	}

	@GetMapping("/check-mobile")
	public ResponseEntity<ApiResponse<Boolean>> checkMobile(@RequestParam String mobile) {
		boolean available = userService.isMobileAvailable(mobile);
		return ResponseEntity.ok(ApiResponse.ok("OK", available));
	}
}
