package com.realestate.main.controller;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.realestate.main.dto.request.PgPropertyCreateRequest;
import com.realestate.main.dto.request.PgPropertyCreateRequest.PgImageMetaRequest;
import com.realestate.main.dto.response.ApiResponse;
import com.realestate.main.dto.response.PgPropertyListItemResponse;
import com.realestate.main.dto.response.PgPropertyResponse;
import com.realestate.main.entity.PgOwner;
import com.realestate.main.exception.AuthException;
import com.realestate.main.repository.PgOwnerRepository;
import com.realestate.main.service.PgPropertyService;
import com.realestate.main.util.PgOwnerSessionHelper;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/pg-owner/properties")
public class PgOwnerPropertyApiController {

	private final PgPropertyService pgPropertyService;
	private final PgOwnerRepository pgOwnerRepository;
	private final ObjectMapper objectMapper;

	public PgOwnerPropertyApiController(PgPropertyService pgPropertyService, PgOwnerRepository pgOwnerRepository,
			ObjectMapper objectMapper) {
		this.pgPropertyService = pgPropertyService;
		this.pgOwnerRepository = pgOwnerRepository;
		this.objectMapper = objectMapper;
	}

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<ApiResponse<PgPropertyResponse>> create(@RequestParam("payload") String payloadJson,
			@RequestParam(value = "images", required = false) MultipartFile[] images, HttpSession session) {
		PgOwner owner = PgOwnerSessionHelper.requireActivePgOwner(session, pgOwnerRepository);
		PgPropertyCreateRequest request = parsePayload(payloadJson);
		List<MultipartFile> imageList = images != null ? Arrays.asList(images) : List.of();
		List<PgImageMetaRequest> meta = parseImageMeta(payloadJson);
		PgPropertyResponse created = pgPropertyService.createProperty(owner, request, imageList, meta);
		String msg = request.isPublish() ? "PG property published successfully" : "PG property saved as draft";
		return ResponseEntity.ok(ApiResponse.ok(msg, created));
	}

	@GetMapping
	public ResponseEntity<ApiResponse<List<PgPropertyListItemResponse>>> list(HttpSession session) {
		Long ownerId = PgOwnerSessionHelper.requirePgOwnerId(session);
		return ResponseEntity.ok(ApiResponse.ok("OK", pgPropertyService.listProperties(ownerId)));
	}

	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<PgPropertyResponse>> get(@PathVariable Long id, HttpSession session) {
		Long ownerId = PgOwnerSessionHelper.requirePgOwnerId(session);
		return ResponseEntity.ok(ApiResponse.ok("OK", pgPropertyService.getProperty(ownerId, id)));
	}

	@PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<ApiResponse<PgPropertyResponse>> update(@PathVariable Long id,
			@RequestParam("payload") String payloadJson,
			@RequestParam(value = "images", required = false) MultipartFile[] images, HttpSession session) {
		PgOwner owner = PgOwnerSessionHelper.requireActivePgOwner(session, pgOwnerRepository);
		PgPropertyCreateRequest request = parsePayload(payloadJson);
		List<MultipartFile> imageList = images != null ? Arrays.asList(images) : List.of();
		List<PgImageMetaRequest> meta = parseImageMeta(payloadJson);
		PgPropertyResponse updated = pgPropertyService.updateProperty(owner, id, request, imageList, meta);
		String msg = request.isPublish() ? "PG property updated and published" : "PG property updated";
		return ResponseEntity.ok(ApiResponse.ok(msg, updated));
	}

	@PostMapping("/{id}/publish")
	public ResponseEntity<ApiResponse<PgPropertyResponse>> publish(@PathVariable Long id, HttpSession session) {
		Long ownerId = PgOwnerSessionHelper.requirePgOwnerId(session);
		return ResponseEntity.ok(ApiResponse.ok("PG property published",
				pgPropertyService.publishProperty(ownerId, id)));
	}

	@PatchMapping("/{id}/status")
	public ResponseEntity<ApiResponse<PgPropertyResponse>> toggleStatus(@PathVariable Long id,
			@RequestParam(value = "active", required = false) Boolean active, HttpSession session) {
		Long ownerId = PgOwnerSessionHelper.requirePgOwnerId(session);
		PgPropertyResponse updated = active != null
				? pgPropertyService.setPropertyActive(ownerId, id, active)
				: pgPropertyService.toggleStatus(ownerId, id);
		return ResponseEntity.ok(ApiResponse.ok("PG property status updated", updated));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id, HttpSession session) {
		Long ownerId = PgOwnerSessionHelper.requirePgOwnerId(session);
		pgPropertyService.deleteProperty(ownerId, id);
		return ResponseEntity.ok(ApiResponse.ok("PG property deleted"));
	}

	private PgPropertyCreateRequest parsePayload(String payloadJson) {
		try {
			return objectMapper.readValue(payloadJson, PgPropertyCreateRequest.class);
		} catch (Exception e) {
			throw new AuthException("Invalid PG property data. Please check the form and try again.");
		}
	}

	private List<PgImageMetaRequest> parseImageMeta(String payloadJson) {
		try {
			var node = objectMapper.readTree(payloadJson);
			if (node.has("imageMeta") && node.get("imageMeta").isArray()) {
				return objectMapper.convertValue(node.get("imageMeta"), new TypeReference<List<PgImageMetaRequest>>() {
				});
			}
		} catch (Exception ignored) {
			// optional metadata
		}
		return Collections.emptyList();
	}
}
