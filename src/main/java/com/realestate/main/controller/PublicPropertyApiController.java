package com.realestate.main.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.realestate.main.dto.response.ApiResponse;
import com.realestate.main.dto.response.PublicPropertyCardResponse;
import com.realestate.main.dto.response.PublicPropertyDetailResponse;
import com.realestate.main.entity.enums.AgentSpecialization;
import com.realestate.main.entity.enums.ListingType;
import com.realestate.main.service.PublicPropertyBrowseService;

@RestController
@RequestMapping("/api/public/properties")
public class PublicPropertyApiController {

	private final PublicPropertyBrowseService publicPropertyBrowseService;

	public PublicPropertyApiController(PublicPropertyBrowseService publicPropertyBrowseService) {
		this.publicPropertyBrowseService = publicPropertyBrowseService;
	}

	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<PublicPropertyDetailResponse>> getById(@PathVariable Long id) {
		return ResponseEntity.ok(ApiResponse.ok("OK", publicPropertyBrowseService.getActiveDetail(id)));
	}

	@GetMapping
	public ResponseEntity<ApiResponse<List<PublicPropertyCardResponse>>> list(
			@RequestParam(value = "category", required = false) String category,
			@RequestParam(value = "listingType", required = false) String listingType) {
		AgentSpecialization cat = parseCategory(category);
		ListingType lt = parseListingType(listingType);
		List<PublicPropertyCardResponse> listings = publicPropertyBrowseService.browseActive(cat, lt);
		return ResponseEntity.ok(ApiResponse.ok("OK", listings));
	}

	private AgentSpecialization parseCategory(String raw) {
		if (raw == null || raw.isBlank() || "ALL".equalsIgnoreCase(raw.trim())) {
			return null;
		}
		try {
			return AgentSpecialization.valueOf(raw.trim().toUpperCase());
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	private ListingType parseListingType(String raw) {
		if (raw == null || raw.isBlank() || "ALL".equalsIgnoreCase(raw.trim())) {
			return null;
		}
		try {
			return ListingType.valueOf(raw.trim().toUpperCase());
		} catch (IllegalArgumentException e) {
			return null;
		}
	}
}
