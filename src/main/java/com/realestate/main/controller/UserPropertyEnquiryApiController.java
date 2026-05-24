package com.realestate.main.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.realestate.main.dto.request.EnquiryCreateRequest;
import com.realestate.main.dto.response.ApiResponse;
import com.realestate.main.dto.response.PropertyEnquiryResponse;
import com.realestate.main.service.PropertyEnquiryService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/user/enquiries")
@Validated
public class UserPropertyEnquiryApiController {

	private final PropertyEnquiryService propertyEnquiryService;

	public UserPropertyEnquiryApiController(PropertyEnquiryService propertyEnquiryService) {
		this.propertyEnquiryService = propertyEnquiryService;
	}

	@PostMapping
	public ResponseEntity<ApiResponse<PropertyEnquiryResponse>> submit(@Valid @RequestBody EnquiryCreateRequest request) {
		PropertyEnquiryResponse created = propertyEnquiryService.submit(request);
		return ResponseEntity.ok(ApiResponse.ok("Enquiry submitted. The agent will respond soon.", created));
	}

	@GetMapping
	public ResponseEntity<ApiResponse<List<PropertyEnquiryResponse>>> listMine() {
		return ResponseEntity.ok(ApiResponse.ok("OK", propertyEnquiryService.listForUser()));
	}
}
