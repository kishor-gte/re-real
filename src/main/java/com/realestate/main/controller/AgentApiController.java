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

import com.realestate.main.dto.request.AgentForgotPasswordRequest;
import com.realestate.main.dto.request.AgentLoginRequest;
import com.realestate.main.dto.request.AgentOtpVerifyRequest;
import com.realestate.main.dto.request.AgentRegisterRequest;
import com.realestate.main.dto.request.AgentResendOtpRequest;
import com.realestate.main.dto.request.AgentResetPasswordRequest;
import com.realestate.main.dto.response.ApiResponse;
import com.realestate.main.dto.response.OtpSendResult;
import com.realestate.main.dto.response.PasswordResetRequestResult;
import com.realestate.main.service.AgentLookupService;
import com.realestate.main.service.AgentLoginService;
import com.realestate.main.service.AgentPasswordResetService;
import com.realestate.main.service.AgentRegistrationService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/agent/auth")
public class AgentApiController {

	private final AgentRegistrationService agentRegistrationService;
	private final AgentLoginService agentLoginService;
	private final AgentLookupService agentLookupService;
	private final AgentPasswordResetService agentPasswordResetService;

	public AgentApiController(AgentRegistrationService agentRegistrationService, AgentLoginService agentLoginService,
			AgentLookupService agentLookupService, AgentPasswordResetService agentPasswordResetService) {
		this.agentRegistrationService = agentRegistrationService;
		this.agentLoginService = agentLoginService;
		this.agentLookupService = agentLookupService;
		this.agentPasswordResetService = agentPasswordResetService;
	}

	@PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<ApiResponse<OtpSendResult>> register(@Valid @ModelAttribute AgentRegisterRequest request,
			@RequestParam(value = "profilePhoto", required = false) MultipartFile profilePhoto,
			@RequestParam(value = "agencyLogo", required = false) MultipartFile agencyLogo,
			@RequestParam(value = "governmentId", required = false) MultipartFile governmentId) {
		OtpSendResult result = agentRegistrationService.initiateRegistration(request, profilePhoto, agencyLogo,
				governmentId);
		String msg = result.getMessage() != null ? result.getMessage()
				: "OTP sent. Please verify your email to activate your agent account.";
		return ResponseEntity.ok(ApiResponse.ok(msg, result));
	}

	@PostMapping("/verify-otp")
	public ResponseEntity<ApiResponse<Void>> verifyOtp(@Valid @RequestBody AgentOtpVerifyRequest request) {
		agentRegistrationService.verifyOtp(request);
		return ResponseEntity.ok(ApiResponse.ok("Agent account activated. Please login."));
	}

	@PostMapping("/resend-otp")
	public ResponseEntity<ApiResponse<OtpSendResult>> resendOtp(@Valid @RequestBody AgentResendOtpRequest request) {
		OtpSendResult result = agentRegistrationService.resendOtp(request.getEmail());
		return ResponseEntity.ok(ApiResponse.ok(result.getMessage(), result));
	}

	@GetMapping("/check-email")
	public ResponseEntity<ApiResponse<Boolean>> checkEmail(@RequestParam String email) {
		boolean available = !agentLookupService.isEmailRegistered(email);
		return ResponseEntity.ok(ApiResponse.ok("OK", available));
	}

	@GetMapping("/check-mobile")
	public ResponseEntity<ApiResponse<Boolean>> checkMobile(@RequestParam String mobile) {
		boolean available = !agentLookupService.isMobileRegistered(mobile);
		return ResponseEntity.ok(ApiResponse.ok("OK", available));
	}

	@GetMapping("/check-rera")
	public ResponseEntity<ApiResponse<Boolean>> checkRera(@RequestParam String reraNumber) {
		boolean available = !agentLookupService.isReraRegistered(reraNumber);
		return ResponseEntity.ok(ApiResponse.ok("OK", available));
	}

	@GetMapping("/check-referral")
	public ResponseEntity<ApiResponse<Boolean>> checkReferral(@RequestParam String referralCode) {
		if (referralCode == null || referralCode.isBlank()) {
			return ResponseEntity.ok(ApiResponse.ok("OK", true));
		}
		boolean valid = agentLookupService.isReferralCodeValid(referralCode);
		return ResponseEntity.ok(ApiResponse.ok("OK", valid));
	}

	@PostMapping("/login")
	public ResponseEntity<ApiResponse<Void>> login(@Valid @RequestBody AgentLoginRequest request,
			HttpServletRequest httpRequest) {
		agentLoginService.login(request, httpRequest);
		return ResponseEntity.ok(ApiResponse.ok("Agent login successful"));
	}

	@PostMapping("/logout")
	public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request) {
		agentLoginService.logout(request);
		return ResponseEntity.ok(ApiResponse.ok("Logged out"));
	}

	@PostMapping("/forgot-password")
	public ResponseEntity<ApiResponse<PasswordResetRequestResult>> forgotPassword(
			@Valid @RequestBody AgentForgotPasswordRequest request, HttpServletRequest httpRequest) {
		PasswordResetRequestResult result = agentPasswordResetService.requestReset(request, httpRequest);
		return ResponseEntity.ok(ApiResponse.ok(result.getMessage(), result));
	}

	@PostMapping("/reset-password")
	public ResponseEntity<ApiResponse<Void>> resetPassword(@Valid @RequestBody AgentResetPasswordRequest request) {
		agentPasswordResetService.resetPassword(request);
		return ResponseEntity.ok(ApiResponse.ok("Password updated. Please login."));
	}
}
