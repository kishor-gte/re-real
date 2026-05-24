package com.realestate.main.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.realestate.main.dto.request.EnquiryReplyRequest;
import com.realestate.main.dto.response.ApiResponse;
import com.realestate.main.dto.response.PropertyEnquiryResponse;
import com.realestate.main.service.PropertyEnquiryService;
import com.realestate.main.util.AgentSessionHelper;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/agent/enquiries")
@Validated
public class AgentPropertyEnquiryApiController {

	private final PropertyEnquiryService propertyEnquiryService;

	public AgentPropertyEnquiryApiController(PropertyEnquiryService propertyEnquiryService) {
		this.propertyEnquiryService = propertyEnquiryService;
	}

	@GetMapping
	public ResponseEntity<ApiResponse<List<PropertyEnquiryResponse>>> list(HttpSession session) {
		Long agentId = AgentSessionHelper.requireAgentId(session);
		return ResponseEntity.ok(ApiResponse.ok("OK", propertyEnquiryService.listForAgent(agentId)));
	}

	@PatchMapping("/{id}/accept")
	public ResponseEntity<ApiResponse<PropertyEnquiryResponse>> accept(@PathVariable Long id, HttpSession session) {
		Long agentId = AgentSessionHelper.requireAgentId(session);
		return ResponseEntity.ok(ApiResponse.ok("Enquiry accepted", propertyEnquiryService.accept(id, agentId)));
	}

	@PatchMapping("/{id}/decline")
	public ResponseEntity<ApiResponse<PropertyEnquiryResponse>> decline(@PathVariable Long id, HttpSession session) {
		Long agentId = AgentSessionHelper.requireAgentId(session);
		return ResponseEntity.ok(ApiResponse.ok("Enquiry declined", propertyEnquiryService.decline(id, agentId)));
	}

	@PatchMapping("/{id}/reply")
	public ResponseEntity<ApiResponse<PropertyEnquiryResponse>> reply(@PathVariable Long id,
			@Valid @RequestBody EnquiryReplyRequest request, HttpSession session) {
		Long agentId = AgentSessionHelper.requireAgentId(session);
		return ResponseEntity.ok(ApiResponse.ok("Reply sent to buyer", propertyEnquiryService.reply(id, agentId, request)));
	}
}
