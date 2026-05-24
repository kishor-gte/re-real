package com.realestate.main.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.realestate.main.dto.request.EnquiryCreateRequest;
import com.realestate.main.dto.request.EnquiryReplyRequest;
import com.realestate.main.dto.response.PropertyEnquiryResponse;
import com.realestate.main.entity.Agent;
import com.realestate.main.entity.Property;
import com.realestate.main.entity.PropertyEnquiry;
import com.realestate.main.entity.User;
import com.realestate.main.entity.enums.EnquiryStatus;
import com.realestate.main.entity.enums.PropertyStatus;
import com.realestate.main.exception.AuthException;
import com.realestate.main.repository.AgentRepository;
import com.realestate.main.repository.PropertyEnquiryRepository;
import com.realestate.main.repository.PropertyRepository;

@Service
public class PropertyEnquiryService {

	private final PropertyEnquiryRepository enquiryRepository;
	private final PropertyRepository propertyRepository;
	private final AgentRepository agentRepository;
	private final UserService userService;

	public PropertyEnquiryService(PropertyEnquiryRepository enquiryRepository, PropertyRepository propertyRepository,
			AgentRepository agentRepository, UserService userService) {
		this.enquiryRepository = enquiryRepository;
		this.propertyRepository = propertyRepository;
		this.agentRepository = agentRepository;
		this.userService = userService;
	}

	@Transactional
	public PropertyEnquiryResponse submit(EnquiryCreateRequest request) {
		User user = userService.getLoggedInUser();
		Property property = propertyRepository.findById(request.getPropertyId())
				.orElseThrow(() -> new AuthException("Property not found"));
		if (property.getStatus() != PropertyStatus.ACTIVE) {
			throw new AuthException("This property is not available for enquiry");
		}
		if (enquiryRepository.existsByUserIdAndPropertyIdAndStatus(user.getId(), property.getId(),
				EnquiryStatus.PENDING)) {
			throw new AuthException("You already have a pending enquiry for this property");
		}

		PropertyEnquiry enquiry = new PropertyEnquiry();
		enquiry.setPropertyId(property.getId());
		enquiry.setAgentId(property.getAgentId());
		enquiry.setUserId(user.getId());
		enquiry.setUserName(user.getFullName());
		enquiry.setUserEmail(user.getEmail());
		enquiry.setUserMobile(user.getMobile());
		enquiry.setMessage(request.getMessage().trim());
		enquiry.setStatus(EnquiryStatus.PENDING);

		return toResponse(enquiryRepository.save(enquiry), property, null);
	}

	@Transactional(readOnly = true)
	public List<PropertyEnquiryResponse> listForUser() {
		Long userId = userService.getLoggedInUser().getId();
		return enquiryRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
				.map(e -> toResponse(e, loadProperty(e.getPropertyId()), loadAgent(e.getAgentId())))
				.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public List<PropertyEnquiryResponse> listForAgent(Long agentId) {
		return enquiryRepository.findByAgentIdOrderByCreatedAtDesc(agentId).stream()
				.map(e -> toResponse(e, loadProperty(e.getPropertyId()), loadAgent(e.getAgentId())))
				.collect(Collectors.toList());
	}

	@Transactional
	public PropertyEnquiryResponse accept(Long enquiryId, Long agentId) {
		PropertyEnquiry enquiry = requireAgentEnquiry(enquiryId, agentId);
		if (enquiry.getStatus() != EnquiryStatus.PENDING) {
			throw new AuthException("Only pending enquiries can be accepted");
		}
		enquiry.setStatus(EnquiryStatus.ACCEPTED);
		enquiry.setAcceptedAt(LocalDateTime.now());
		return toResponse(enquiryRepository.save(enquiry), loadProperty(enquiry.getPropertyId()),
				loadAgent(enquiry.getAgentId()));
	}

	@Transactional
	public PropertyEnquiryResponse decline(Long enquiryId, Long agentId) {
		PropertyEnquiry enquiry = requireAgentEnquiry(enquiryId, agentId);
		if (enquiry.getStatus() != EnquiryStatus.PENDING) {
			throw new AuthException("Only pending enquiries can be declined");
		}
		enquiry.setStatus(EnquiryStatus.DECLINED);
		return toResponse(enquiryRepository.save(enquiry), loadProperty(enquiry.getPropertyId()),
				loadAgent(enquiry.getAgentId()));
	}

	@Transactional
	public PropertyEnquiryResponse reply(Long enquiryId, Long agentId, EnquiryReplyRequest request) {
		PropertyEnquiry enquiry = requireAgentEnquiry(enquiryId, agentId);
		if (enquiry.getStatus() != EnquiryStatus.ACCEPTED) {
			throw new AuthException("Accept the enquiry before sending a reply");
		}
		enquiry.setAgentReply(request.getReply().trim());
		enquiry.setStatus(EnquiryStatus.REPLIED);
		enquiry.setRepliedAt(LocalDateTime.now());
		PropertyEnquiry saved = enquiryRepository.save(enquiry);
		return toResponse(saved, loadProperty(saved.getPropertyId()), loadAgent(saved.getAgentId()));
	}

	@Transactional(readOnly = true)
	public long countPendingForAgent(Long agentId) {
		return enquiryRepository.countByAgentIdAndStatus(agentId, EnquiryStatus.PENDING);
	}

	private PropertyEnquiry requireAgentEnquiry(Long enquiryId, Long agentId) {
		return enquiryRepository.findByIdAndAgentId(enquiryId, agentId)
				.orElseThrow(() -> new AuthException("Enquiry not found"));
	}

	private Property loadProperty(Long propertyId) {
		return propertyRepository.findById(propertyId).orElse(null);
	}

	private Agent loadAgent(Long agentId) {
		return agentRepository.findById(agentId).orElse(null);
	}

	private PropertyEnquiryResponse toResponse(PropertyEnquiry enquiry, Property property, Agent agent) {
		PropertyEnquiryResponse r = new PropertyEnquiryResponse();
		r.setId(enquiry.getId());
		r.setPropertyId(enquiry.getPropertyId());
		r.setAgentId(enquiry.getAgentId());
		r.setUserId(enquiry.getUserId());
		r.setUserName(enquiry.getUserName());
		r.setUserEmail(enquiry.getUserEmail());
		r.setUserMobile(enquiry.getUserMobile());
		r.setMessage(enquiry.getMessage());
		r.setStatus(enquiry.getStatus());
		r.setStatusLabel(formatStatus(enquiry.getStatus()));
		r.setAgentReply(enquiry.getAgentReply());
		r.setCreatedAt(enquiry.getCreatedAt());
		r.setAcceptedAt(enquiry.getAcceptedAt());
		r.setRepliedAt(enquiry.getRepliedAt());
		if (property != null) {
			r.setPropertyCode(property.getPropertyCode());
			r.setPropertyTitle(property.getTitle());
			r.setPropertyImageUrl(property.getPrimaryImageUrl());
		}
		if (agent != null) {
			r.setAgentName(agent.getFullName());
		}
		return r;
	}

	private static String formatStatus(EnquiryStatus status) {
		return switch (status) {
		case PENDING -> "Waiting for agent";
		case ACCEPTED -> "Accepted — reply pending";
		case REPLIED -> "Agent replied";
		case DECLINED -> "Declined";
		};
	}
}
