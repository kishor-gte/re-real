package com.realestate.main.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.realestate.main.dto.pgbooking.PgOwnerBookingListItemResponse;
import com.realestate.main.dto.pgbooking.PgOwnerBookingRejectRequest;
import com.realestate.main.dto.response.ApiResponse;
import com.realestate.main.service.booking.PgOwnerPgBookingService;
import com.realestate.main.util.PgOwnerSessionHelper;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/pg-owner/bookings")
public class PgOwnerBookingApiController {

	private final PgOwnerPgBookingService pgOwnerPgBookingService;

	public PgOwnerBookingApiController(PgOwnerPgBookingService pgOwnerPgBookingService) {
		this.pgOwnerPgBookingService = pgOwnerPgBookingService;
	}

	@GetMapping
	public ResponseEntity<ApiResponse<List<PgOwnerBookingListItemResponse>>> list(HttpSession session) {
		Long ownerId = PgOwnerSessionHelper.requirePgOwnerId(session);
		return ResponseEntity.ok(ApiResponse.ok("OK", pgOwnerPgBookingService.listForOwner(ownerId)));
	}

	@GetMapping("/pending-count")
	public ResponseEntity<ApiResponse<Map<String, Long>>> pendingCount(HttpSession session) {
		Long ownerId = PgOwnerSessionHelper.requirePgOwnerId(session);
		long count = pgOwnerPgBookingService.countPendingApprovals(ownerId);
		return ResponseEntity.ok(ApiResponse.ok("OK", Map.of("pendingCount", count)));
	}

	@PatchMapping("/{bookingId}/approve")
	public ResponseEntity<ApiResponse<PgOwnerBookingListItemResponse>> approve(@PathVariable Long bookingId,
			HttpSession session) {
		Long ownerId = PgOwnerSessionHelper.requirePgOwnerId(session);
		PgOwnerBookingListItemResponse updated = pgOwnerPgBookingService.approve(ownerId, bookingId);
		return ResponseEntity.ok(ApiResponse.ok("Booking approved", updated));
	}

	@PatchMapping("/{bookingId}/reject")
	public ResponseEntity<ApiResponse<PgOwnerBookingListItemResponse>> reject(@PathVariable Long bookingId,
			@RequestBody(required = false) PgOwnerBookingRejectRequest request, HttpSession session) {
		Long ownerId = PgOwnerSessionHelper.requirePgOwnerId(session);
		PgOwnerBookingListItemResponse updated = pgOwnerPgBookingService.reject(ownerId, bookingId, request);
		return ResponseEntity.ok(ApiResponse.ok("Booking rejected", updated));
	}
}
