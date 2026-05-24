package com.realestate.main.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.realestate.main.dto.booking.RazorpayOrderResponse;
import com.realestate.main.dto.pgbooking.PgBookingCheckoutPreviewResponse;
import com.realestate.main.dto.pgbooking.PgBookingConfirmationResponse;
import com.realestate.main.dto.pgbooking.PgBookingDraftRequest;
import com.realestate.main.dto.pgbooking.PgBookingRoomOptionResponse;
import com.realestate.main.dto.pgbooking.PgMonthlyRentDueResponse;
import com.realestate.main.dto.pgbooking.PgPaymentVerifyRequest;
import com.realestate.main.dto.pgbooking.PgRentPaymentPreviewResponse;
import com.realestate.main.dto.pgbooking.PgRentPaymentVerifyRequest;
import com.realestate.main.dto.pgbooking.PgVacateNoticeRequest;
import com.realestate.main.dto.pgbooking.PgVacateNoticeResponse;
import com.realestate.main.dto.pgbooking.UserPgBookingListItemResponse;
import com.realestate.main.dto.response.ApiResponse;
import com.realestate.main.entity.PgBooking;
import com.realestate.main.service.UserService;
import com.realestate.main.service.booking.PgBookingService;
import com.realestate.main.service.booking.PgMonthlyRentService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/user/pg-bookings")
@Validated
public class UserPgBookingApiController {

	private final PgBookingService pgBookingService;
	private final PgMonthlyRentService pgMonthlyRentService;
	private final UserService userService;

	public UserPgBookingApiController(PgBookingService pgBookingService, PgMonthlyRentService pgMonthlyRentService,
			UserService userService) {
		this.pgBookingService = pgBookingService;
		this.pgMonthlyRentService = pgMonthlyRentService;
		this.userService = userService;
	}

	@GetMapping("/rooms")
	public ResponseEntity<ApiResponse<List<PgBookingRoomOptionResponse>>> rooms(@RequestParam Long pgId) {
		return ResponseEntity.ok(ApiResponse.ok("OK", pgBookingService.listBookableRooms(pgId)));
	}

	@GetMapping
	public ResponseEntity<ApiResponse<List<UserPgBookingListItemResponse>>> myBookings() {
		Long userId = userService.getLoggedInUser().getId();
		return ResponseEntity.ok(ApiResponse.ok("OK", pgBookingService.listMyBookings(userId)));
	}

	@PostMapping("/draft")
	public ResponseEntity<ApiResponse<PgBooking>> createDraft(@Valid @RequestBody PgBookingDraftRequest request) {
		Long userId = userService.getLoggedInUser().getId();
		PgBooking booking = pgBookingService.createDraft(userId, request);
		return ResponseEntity.ok(ApiResponse.ok("PG booking saved", booking));
	}

	@GetMapping("/{bookingId}/checkout-preview")
	public ResponseEntity<ApiResponse<PgBookingCheckoutPreviewResponse>> preview(@PathVariable Long bookingId) {
		Long userId = userService.getLoggedInUser().getId();
		return ResponseEntity.ok(ApiResponse.ok("OK", pgBookingService.checkoutPreview(userId, bookingId)));
	}

	@GetMapping("/{bookingId}/balance-preview")
	public ResponseEntity<ApiResponse<PgBookingCheckoutPreviewResponse>> balancePreview(@PathVariable Long bookingId) {
		Long userId = userService.getLoggedInUser().getId();
		return ResponseEntity.ok(ApiResponse.ok("OK", pgBookingService.balancePaymentPreview(userId, bookingId)));
	}

	@PostMapping("/{bookingId}/pay")
	public ResponseEntity<ApiResponse<RazorpayOrderResponse>> pay(@PathVariable Long bookingId) {
		Long userId = userService.getLoggedInUser().getId();
		return ResponseEntity.ok(ApiResponse.ok("Payment order created",
				pgBookingService.createPaymentOrder(userId, bookingId)));
	}

	@PostMapping("/payment/verify")
	public ResponseEntity<ApiResponse<PgBookingConfirmationResponse>> verify(
			@Valid @RequestBody PgPaymentVerifyRequest request) {
		Long userId = userService.getLoggedInUser().getId();
		return ResponseEntity.ok(ApiResponse.ok("Payment verified",
				pgBookingService.verifyPayment(userId, request)));
	}

	@GetMapping("/{bookingId}/confirmation")
	public ResponseEntity<ApiResponse<PgBookingConfirmationResponse>> confirmation(@PathVariable Long bookingId) {
		Long userId = userService.getLoggedInUser().getId();
		return ResponseEntity.ok(ApiResponse.ok("OK", pgBookingService.getConfirmation(userId, bookingId)));
	}

	@PostMapping("/{bookingId}/request-vacate")
	public ResponseEntity<ApiResponse<PgVacateNoticeResponse>> requestVacate(@PathVariable Long bookingId,
			@RequestBody(required = false) PgVacateNoticeRequest request) {
		Long userId = userService.getLoggedInUser().getId();
		return ResponseEntity.ok(ApiResponse.ok("Vacate notice submitted",
				pgMonthlyRentService.toVacateResponse(
						pgMonthlyRentService.requestVacate(userId, bookingId, request))));
	}

	@GetMapping("/{bookingId}/rent-dues")
	public ResponseEntity<ApiResponse<List<PgMonthlyRentDueResponse>>> rentDues(@PathVariable Long bookingId) {
		Long userId = userService.getLoggedInUser().getId();
		return ResponseEntity.ok(ApiResponse.ok("OK", pgMonthlyRentService.listRentDues(userId, bookingId)));
	}

	@GetMapping("/rent-dues/{rentDueId}/checkout-preview")
	public ResponseEntity<ApiResponse<PgRentPaymentPreviewResponse>> rentCheckoutPreview(
			@PathVariable Long rentDueId) {
		Long userId = userService.getLoggedInUser().getId();
		return ResponseEntity.ok(ApiResponse.ok("OK", pgMonthlyRentService.rentCheckoutPreview(userId, rentDueId)));
	}

	@PostMapping("/rent-dues/{rentDueId}/pay")
	public ResponseEntity<ApiResponse<RazorpayOrderResponse>> payRent(@PathVariable Long rentDueId) {
		Long userId = userService.getLoggedInUser().getId();
		return ResponseEntity.ok(ApiResponse.ok("Rent payment order created",
				pgMonthlyRentService.createRentPaymentOrder(userId, rentDueId)));
	}

	@PostMapping("/rent-payment/verify")
	public ResponseEntity<ApiResponse<PgMonthlyRentDueResponse>> verifyRentPayment(
			@Valid @RequestBody PgRentPaymentVerifyRequest request) {
		Long userId = userService.getLoggedInUser().getId();
		return ResponseEntity.ok(ApiResponse.ok("Rent payment verified",
				pgMonthlyRentService.verifyRentPayment(userId, request)));
	}
}
