package com.realestate.main.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.realestate.main.dto.response.ApiResponse;
import com.realestate.main.dto.response.PgPropertyResponse;
import com.realestate.main.dto.response.PublicPgCardResponse;
import com.realestate.main.service.PgPropertyService;
import com.realestate.main.service.PublicPgBrowseService;

@RestController
@RequestMapping("/api/public/pgs")
public class PublicPgApiController {

	private final PublicPgBrowseService publicPgBrowseService;
	private final PgPropertyService pgPropertyService;

	public PublicPgApiController(PublicPgBrowseService publicPgBrowseService, PgPropertyService pgPropertyService) {
		this.publicPgBrowseService = publicPgBrowseService;
		this.pgPropertyService = pgPropertyService;
	}

	@GetMapping
	public ResponseEntity<ApiResponse<List<PublicPgCardResponse>>> list(
			@RequestParam(value = "city", required = false) String city,
			@RequestParam(value = "state", required = false) String state) {
		List<PublicPgCardResponse> listings = publicPgBrowseService.browsePublished(city, state);
		return ResponseEntity.ok(ApiResponse.ok("OK", listings));
	}

	@GetMapping("/cities")
	public ResponseEntity<ApiResponse<List<String>>> cities() {
		return ResponseEntity.ok(ApiResponse.ok("OK", publicPgBrowseService.listPublishedCities()));
	}

	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<PgPropertyResponse>> getById(@PathVariable Long id) {
		return ResponseEntity.ok(ApiResponse.ok("OK", pgPropertyService.getPublishedProperty(id)));
	}
}
