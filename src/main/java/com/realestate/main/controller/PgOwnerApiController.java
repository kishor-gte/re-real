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

import com.realestate.main.dto.request.PgOwnerForgotPasswordRequest;
import com.realestate.main.dto.request.PgOwnerLoginRequest;
import com.realestate.main.dto.request.PgOwnerOtpVerifyRequest;
import com.realestate.main.dto.request.PgOwnerRegisterRequest;
import com.realestate.main.dto.request.PgOwnerResendOtpRequest;
import com.realestate.main.dto.request.PgOwnerResetPasswordRequest;
import com.realestate.main.dto.response.ApiResponse;
import com.realestate.main.dto.response.OtpSendResult;
import com.realestate.main.dto.response.PasswordResetRequestResult;
import com.realestate.main.service.PgOwnerLoginService;
import com.realestate.main.service.PgOwnerLookupService;
import com.realestate.main.service.PgOwnerPasswordResetService;
import com.realestate.main.service.PgOwnerRegistrationService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/pg-owner/auth")
public class PgOwnerApiController {

	private final PgOwnerRegistrationService pgOwnerRegistrationService;
	private final PgOwnerLoginService pgOwnerLoginService;
	private final PgOwnerLookupService pgOwnerLookupService;
	private final PgOwnerPasswordResetService pgOwnerPasswordResetService;

	public PgOwnerApiController(PgOwnerRegistrationService pgOwnerRegistrationService,
			PgOwnerLoginService pgOwnerLoginService, PgOwnerLookupService pgOwnerLookupService,
			PgOwnerPasswordResetService pgOwnerPasswordResetService) {
		this.pgOwnerRegistrationService = pgOwnerRegistrationService;
		this.pgOwnerLoginService = pgOwnerLoginService;
		this.pgOwnerLookupService = pgOwnerLookupService;
		this.pgOwnerPasswordResetService = pgOwnerPasswordResetService;
	}

	@PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<ApiResponse<OtpSendResult>> register(@Valid @ModelAttribute PgOwnerRegisterRequest request,
			@RequestParam(value = "profilePhoto", required = false) MultipartFile profilePhoto,
			@RequestParam(value = "governmentId", required = false) MultipartFile governmentId) {
		OtpSendResult result = pgOwnerRegistrationService.initiateRegistration(request, profilePhoto, governmentId);
		String msg = result.getMessage() != null ? result.getMessage()
				: "OTP sent. Please verify your email to complete registration.";
		return ResponseEntity.ok(ApiResponse.ok(msg, result));
	}

	@PostMapping("/verify-otp")
	public ResponseEntity<ApiResponse<Void>> verifyOtp(@Valid @RequestBody PgOwnerOtpVerifyRequest request) {
		pgOwnerRegistrationService.verifyOtp(request);
		return ResponseEntity.ok(ApiResponse.ok("Please login."));
	}

	@PostMapping("/resend-otp")
	public ResponseEntity<ApiResponse<OtpSendResult>> resendOtp(@Valid @RequestBody PgOwnerResendOtpRequest request) {
		OtpSendResult result = pgOwnerRegistrationService.resendOtp(request.getEmail());
		return ResponseEntity.ok(ApiResponse.ok(result.getMessage(), result));
	}

	@GetMapping("/check-email")
	public ResponseEntity<ApiResponse<Boolean>> checkEmail(@RequestParam String email) {
		boolean available = !pgOwnerLookupService.isEmailRegistered(email);
		return ResponseEntity.ok(ApiResponse.ok("OK", available));
	}

	@GetMapping("/check-mobile")
	public ResponseEntity<ApiResponse<Boolean>> checkMobile(@RequestParam String mobile) {
		boolean available = !pgOwnerLookupService.isMobileRegistered(mobile);
		return ResponseEntity.ok(ApiResponse.ok("OK", available));
	}

	@GetMapping("/check-referral")
	public ResponseEntity<ApiResponse<Boolean>> checkReferral(@RequestParam String referralCode) {
		if (referralCode == null || referralCode.isBlank()) {
			return ResponseEntity.ok(ApiResponse.ok("OK", true));
		}
		boolean valid = pgOwnerLookupService.isReferralCodeValid(referralCode);
		return ResponseEntity.ok(ApiResponse.ok("OK", valid));
	}

	@PostMapping("/login")
	public ResponseEntity<ApiResponse<Void>> login(@Valid @RequestBody PgOwnerLoginRequest request,
			HttpServletRequest httpRequest) {
		pgOwnerLoginService.login(request, httpRequest);
		return ResponseEntity.ok(ApiResponse.ok("PG owner login successful"));
	}

	@PostMapping("/logout")
	public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request) {
		pgOwnerLoginService.logout(request);
		return ResponseEntity.ok(ApiResponse.ok("Logged out"));
	}

	@PostMapping("/forgot-password")
	public ResponseEntity<ApiResponse<PasswordResetRequestResult>> forgotPassword(
			@Valid @RequestBody PgOwnerForgotPasswordRequest request, HttpServletRequest httpRequest) {
		PasswordResetRequestResult result = pgOwnerPasswordResetService.requestReset(request, httpRequest);
		return ResponseEntity.ok(ApiResponse.ok(result.getMessage(), result));
	}

	@PostMapping("/reset-password")
	public ResponseEntity<ApiResponse<Void>> resetPassword(@Valid @RequestBody PgOwnerResetPasswordRequest request) {
		pgOwnerPasswordResetService.resetPassword(request);
		return ResponseEntity.ok(ApiResponse.ok("Password updated. Please login."));
	}
}
