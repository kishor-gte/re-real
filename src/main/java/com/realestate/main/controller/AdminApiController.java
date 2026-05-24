package com.realestate.main.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.realestate.main.dto.request.AdminForgotPasswordRequest;
import com.realestate.main.dto.request.AdminLoginRequest;
import com.realestate.main.dto.request.AdminOtpVerifyRequest;
import com.realestate.main.dto.request.AdminRegisterRequest;
import com.realestate.main.dto.request.AdminResendOtpRequest;
import com.realestate.main.dto.request.AdminResetPasswordRequest;
import com.realestate.main.dto.response.ApiResponse;
import com.realestate.main.dto.response.OtpSendResult;
import com.realestate.main.dto.response.PasswordResetRequestResult;
import com.realestate.main.service.AdminLookupService;
import com.realestate.main.service.AdminLoginService;
import com.realestate.main.service.AdminPasswordResetService;
import com.realestate.main.service.AdminRegistrationService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/auth")
public class AdminApiController {

	private final AdminRegistrationService adminRegistrationService;
	private final AdminLoginService adminLoginService;
	private final AdminLookupService adminLookupService;
	private final AdminPasswordResetService adminPasswordResetService;

	public AdminApiController(AdminRegistrationService adminRegistrationService, AdminLoginService adminLoginService,
			AdminLookupService adminLookupService, AdminPasswordResetService adminPasswordResetService) {
		this.adminRegistrationService = adminRegistrationService;
		this.adminLoginService = adminLoginService;
		this.adminLookupService = adminLookupService;
		this.adminPasswordResetService = adminPasswordResetService;
	}

	@PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<ApiResponse<OtpSendResult>> register(@Valid @ModelAttribute AdminRegisterRequest request,
			@RequestParam(value = "profileImage", required = false) MultipartFile profileImage) {
		OtpSendResult result = adminRegistrationService.initiateRegistration(request, profileImage);
		return ResponseEntity.ok(ApiResponse.ok(result.getMessage(), result));
	}

	@PostMapping("/verify-otp")
	public ResponseEntity<ApiResponse<Void>> verifyOtp(@Valid @RequestBody AdminOtpVerifyRequest request) {
		adminRegistrationService.verifyOtp(request);
		return ResponseEntity.ok(ApiResponse.ok("Admin account activated. Please login."));
	}

	@PostMapping("/resend-otp")
	public ResponseEntity<ApiResponse<OtpSendResult>> resendOtp(@Valid @RequestBody AdminResendOtpRequest request) {
		OtpSendResult result = adminRegistrationService.resendOtp(request.getOfficialEmail());
		return ResponseEntity.ok(ApiResponse.ok(result.getMessage(), result));
	}

	@GetMapping("/check-email")
	public ResponseEntity<ApiResponse<Boolean>> checkEmail(@RequestParam String email) {
		boolean available = !adminLookupService.isEmailRegistered(email);
		return ResponseEntity.ok(ApiResponse.ok("OK", available));
	}

	@GetMapping("/check-mobile")
	public ResponseEntity<ApiResponse<Boolean>> checkMobile(@RequestParam String mobile) {
		boolean available = !adminLookupService.isMobileRegistered(mobile);
		return ResponseEntity.ok(ApiResponse.ok("OK", available));
	}

	@PostMapping("/login")
	public ResponseEntity<ApiResponse<Void>> login(@Valid @RequestBody AdminLoginRequest request,
			HttpServletRequest httpRequest) {
		adminLoginService.login(request, httpRequest);
		return ResponseEntity.ok(ApiResponse.ok("Admin login successful"));
	}

	@PostMapping("/logout")
	public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request) {
		adminLoginService.logout(request);
		return ResponseEntity.ok(ApiResponse.ok("Logged out"));
	}

	@PostMapping("/forgot-password")
	public ResponseEntity<ApiResponse<PasswordResetRequestResult>> forgotPassword(
			@Valid @RequestBody AdminForgotPasswordRequest request, HttpServletRequest httpRequest) {
		PasswordResetRequestResult result = adminPasswordResetService.requestReset(request, httpRequest);
		return ResponseEntity.ok(ApiResponse.ok(result.getMessage(), result));
	}

	@PostMapping("/reset-password")
	public ResponseEntity<ApiResponse<Void>> resetPassword(@Valid @RequestBody AdminResetPasswordRequest request) {
		adminPasswordResetService.resetPassword(request);
		return ResponseEntity.ok(ApiResponse.ok("Password updated. Please login."));
	}
}
