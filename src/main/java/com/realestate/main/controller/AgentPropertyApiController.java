package com.realestate.main.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.realestate.main.dto.request.PropertyCreateRequest;
import com.realestate.main.dto.response.ApiResponse;
import com.realestate.main.dto.response.PropertyResponse;
import com.realestate.main.entity.enums.PropertyStatus;
import com.realestate.main.service.AgentPropertyService;
import com.realestate.main.util.AgentSessionHelper;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/agent/properties")
@Validated
public class AgentPropertyApiController {

	private final AgentPropertyService agentPropertyService;

	public AgentPropertyApiController(AgentPropertyService agentPropertyService) {
		this.agentPropertyService = agentPropertyService;
	}

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<ApiResponse<PropertyResponse>> create(@Valid @ModelAttribute PropertyCreateRequest request,
			@RequestParam("primaryImage") MultipartFile primaryImage,
			@RequestParam(value = "galleryImages", required = false) MultipartFile[] galleryImages,
			HttpSession session) {
		Long agentId = AgentSessionHelper.requireAgentId(session);
		List<MultipartFile> gallery = galleryImages != null ? Arrays.asList(galleryImages) : List.of();
		PropertyResponse created = agentPropertyService.createProperty(agentId, request, primaryImage, gallery);
		return ResponseEntity.ok(ApiResponse.ok("Property listed successfully", created));
	}

	@GetMapping
	public ResponseEntity<ApiResponse<List<PropertyResponse>>> list(HttpSession session) {
		Long agentId = AgentSessionHelper.requireAgentId(session);
		return ResponseEntity.ok(ApiResponse.ok("OK", agentPropertyService.listAgentProperties(agentId)));
	}

	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<PropertyResponse>> get(@PathVariable Long id, HttpSession session) {
		Long agentId = AgentSessionHelper.requireAgentId(session);
		return ResponseEntity.ok(ApiResponse.ok("OK", agentPropertyService.getProperty(agentId, id)));
	}

	@PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<ApiResponse<PropertyResponse>> update(@PathVariable Long id,
			@Valid @ModelAttribute PropertyCreateRequest request,
			@RequestParam(value = "primaryImage", required = false) MultipartFile primaryImage,
			@RequestParam(value = "galleryImages", required = false) MultipartFile[] galleryImages,
			HttpSession session) {
		Long agentId = AgentSessionHelper.requireAgentId(session);
		List<MultipartFile> gallery = galleryImages != null ? Arrays.asList(galleryImages) : List.of();
		PropertyResponse updated = agentPropertyService.updateProperty(agentId, id, request, primaryImage, gallery);
		return ResponseEntity.ok(ApiResponse.ok("Property updated successfully", updated));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id, HttpSession session) {
		Long agentId = AgentSessionHelper.requireAgentId(session);
		agentPropertyService.deleteProperty(agentId, id);
		return ResponseEntity.ok(ApiResponse.ok("Property deleted successfully"));
	}

	@PatchMapping("/{id}/status")
	public ResponseEntity<ApiResponse<PropertyResponse>> toggleStatus(@PathVariable Long id, HttpSession session) {
		Long agentId = AgentSessionHelper.requireAgentId(session);
		PropertyResponse updated = agentPropertyService.togglePropertyStatus(agentId, id);
		String msg = updated.getStatus() == PropertyStatus.ACTIVE
				? "Property is active — available for rent/sale again"
				: "Property marked inactive — hidden from new listings";
		return ResponseEntity.ok(ApiResponse.ok(msg, updated));
	}
}
