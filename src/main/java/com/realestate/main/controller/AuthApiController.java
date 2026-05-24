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

import com.realestate.main.dto.request.ForgotPasswordRequest;
import com.realestate.main.dto.request.LoginRequest;
import com.realestate.main.dto.request.OtpVerifyRequest;
import com.realestate.main.dto.request.RegisterRequest;
import com.realestate.main.dto.request.ResendOtpRequest;
import com.realestate.main.dto.request.ResetPasswordRequest;
import com.realestate.main.dto.response.ApiResponse;
import com.realestate.main.dto.response.AuthResponse;
import com.realestate.main.dto.response.LoginResult;
import com.realestate.main.dto.response.OtpSendResult;
import com.realestate.main.dto.response.PasswordResetRequestResult;
import com.realestate.main.service.PasswordResetService;
import com.realestate.main.security.JwtCookieHelper;
import com.realestate.main.security.JwtService;
import com.realestate.main.service.LoginService;
import com.realestate.main.service.RegistrationService;
import com.realestate.main.service.UserService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthApiController {

	private final RegistrationService registrationService;
	private final LoginService loginService;
	private final UserService userService;
	private final JwtService jwtService;
	private final PasswordResetService passwordResetService;

	public AuthApiController(RegistrationService registrationService, LoginService loginService,
			UserService userService, JwtService jwtService, PasswordResetService passwordResetService) {
		this.registrationService = registrationService;
		this.loginService = loginService;
		this.userService = userService;
		this.jwtService = jwtService;
		this.passwordResetService = passwordResetService;
	}

	@PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<ApiResponse<OtpSendResult>> register(@Valid @ModelAttribute RegisterRequest request,
			@RequestParam(value = "profileImage", required = false) MultipartFile profileImage) {
		OtpSendResult result = registrationService.initiateRegistration(request, profileImage);
		return ResponseEntity.ok(ApiResponse.ok(result.getMessage(), result));
	}

	@PostMapping("/verify-otp")
	public ResponseEntity<ApiResponse<Void>> verifyOtp(@Valid @RequestBody OtpVerifyRequest request) {
		registrationService.verifyOtp(request);
		return ResponseEntity.ok(ApiResponse.ok("Account activated. Please login."));
	}

	@PostMapping("/resend-otp")
	public ResponseEntity<ApiResponse<OtpSendResult>> resendOtp(@Valid @RequestBody ResendOtpRequest request) {
		OtpSendResult result = registrationService.resendOtp(request.getEmail());
		return ResponseEntity.ok(ApiResponse.ok(result.getMessage(), result));
	}

	@GetMapping("/check-email")
	public ResponseEntity<ApiResponse<Boolean>> checkEmail(@RequestParam String email) {
		return ResponseEntity.ok(ApiResponse.ok("OK", userService.isEmailAvailable(email)));
	}

	@GetMapping("/check-mobile")
	public ResponseEntity<ApiResponse<Boolean>> checkMobile(@RequestParam String mobile) {
		return ResponseEntity.ok(ApiResponse.ok("OK", userService.isMobileAvailable(mobile)));
	}

	@PostMapping("/login")
	public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request,
			HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
		LoginResult result = loginService.login(request, httpRequest);
		Cookie cookie = JwtCookieHelper.createTokenCookie(jwtService.getCookieName(), result.getAccessToken(),
				(int) result.getExpiresInSeconds());
		httpResponse.addCookie(cookie);
		AuthResponse body = AuthResponse.from(result, "/user/dashboard");
		return ResponseEntity.ok(ApiResponse.ok("Login successful", body));
	}

	@PostMapping("/logout")
	public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request, HttpServletResponse response) {
		loginService.logout(request);
		response.addCookie(JwtCookieHelper.clearCookie(jwtService.getCookieName()));
		return ResponseEntity.ok(ApiResponse.ok("Logged out"));
	}

	@PostMapping("/forgot-password")
	public ResponseEntity<ApiResponse<PasswordResetRequestResult>> forgotPassword(
			@Valid @RequestBody ForgotPasswordRequest request, HttpServletRequest httpRequest) {
		PasswordResetRequestResult result = passwordResetService.requestPasswordReset(request, httpRequest);
		return ResponseEntity.ok(ApiResponse.ok(result.getMessage(), result));
	}

	@GetMapping("/validate-reset-token")
	public ResponseEntity<ApiResponse<Boolean>> validateResetToken(@RequestParam String token) {
		boolean valid = passwordResetService.isResetTokenValid(token);
		return ResponseEntity.ok(ApiResponse.ok(valid ? "Token is valid" : "Token is invalid or expired", valid));
	}

	@PostMapping("/reset-password")
	public ResponseEntity<ApiResponse<Void>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
		passwordResetService.resetPassword(request);
		return ResponseEntity.ok(ApiResponse.ok("Password updated successfully. Please login with your new password."));
	}
}
