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

import com.realestate.main.dto.booking.BalancePaymentPreviewResponse;
import com.realestate.main.dto.booking.BookingCheckoutPreviewResponse;
import com.realestate.main.dto.booking.BookingConfirmationResponse;
import com.realestate.main.dto.booking.BookingDraftRequest;
import com.realestate.main.dto.booking.PaymentVerifyRequest;
import com.realestate.main.dto.booking.RazorpayOrderResponse;
import com.realestate.main.dto.booking.UserBookingListItemResponse;
import com.realestate.main.dto.response.ApiResponse;
import com.realestate.main.entity.PropertyBooking;
import com.realestate.main.service.UserService;
import com.realestate.main.service.booking.PropertyBookingService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/user/bookings")
@Validated
public class UserBookingApiController {

	private final PropertyBookingService bookingService;
	private final UserService userService;

	public UserBookingApiController(PropertyBookingService bookingService, UserService userService) {
		this.bookingService = bookingService;
		this.userService = userService;
	}

	@GetMapping
	public ResponseEntity<ApiResponse<List<UserBookingListItemResponse>>> myBookings() {
		Long userId = userService.getLoggedInUser().getId();
		return ResponseEntity.ok(ApiResponse.ok("OK", bookingService.listMyBookings(userId)));
	}

	@GetMapping("/checkout-preview")
	public ResponseEntity<ApiResponse<BookingCheckoutPreviewResponse>> preview(
			@RequestParam Long propertyId,
			@RequestParam(required = false) String couponCode) {
		return ResponseEntity.ok(ApiResponse.ok("OK", bookingService.checkoutPreview(propertyId, couponCode)));
	}

	@PostMapping("/draft")
	public ResponseEntity<ApiResponse<PropertyBooking>> createDraft(@Valid @RequestBody BookingDraftRequest request) {
		Long userId = userService.getLoggedInUser().getId();
		PropertyBooking booking = bookingService.createDraft(userId, request);
		return ResponseEntity.ok(ApiResponse.ok("Booking draft created", booking));
	}

	@PostMapping("/{bookingId}/pay")
	public ResponseEntity<ApiResponse<RazorpayOrderResponse>> pay(@PathVariable Long bookingId) {
		Long userId = userService.getLoggedInUser().getId();
		return ResponseEntity.ok(ApiResponse.ok("Payment order created",
				bookingService.createPaymentOrder(userId, bookingId)));
	}

	@PostMapping("/payment/verify")
	public ResponseEntity<ApiResponse<BookingConfirmationResponse>> verify(
			@Valid @RequestBody PaymentVerifyRequest request) {
		Long userId = userService.getLoggedInUser().getId();
		return ResponseEntity.ok(ApiResponse.ok("Payment verified",
				bookingService.verifyPayment(userId, request)));
	}

	@GetMapping("/{bookingId}/confirmation")
	public ResponseEntity<ApiResponse<BookingConfirmationResponse>> confirmation(@PathVariable Long bookingId) {
		Long userId = userService.getLoggedInUser().getId();
		return ResponseEntity.ok(ApiResponse.ok("OK", bookingService.getConfirmation(userId, bookingId)));
	}

	@GetMapping("/{bookingId}/balance-preview")
	public ResponseEntity<ApiResponse<BalancePaymentPreviewResponse>> balancePreview(@PathVariable Long bookingId) {
		Long userId = userService.getLoggedInUser().getId();
		return ResponseEntity.ok(ApiResponse.ok("OK", bookingService.balancePaymentPreview(userId, bookingId)));
	}
}
