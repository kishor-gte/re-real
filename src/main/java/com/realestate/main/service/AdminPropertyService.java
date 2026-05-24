package com.realestate.main.service;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.realestate.main.dto.response.AdminAgentPropertyGroupResponse;
import com.realestate.main.dto.response.AdminPropertyListItemResponse;
import com.realestate.main.entity.Agent;
import com.realestate.main.entity.Property;
import com.realestate.main.entity.enums.PropertyStatus;
import com.realestate.main.exception.AuthException;
import com.realestate.main.repository.AgentRepository;
import com.realestate.main.repository.PropertyRepository;

@Service
public class AdminPropertyService {

	private final PropertyRepository propertyRepository;
	private final AgentRepository agentRepository;

	public AdminPropertyService(PropertyRepository propertyRepository, AgentRepository agentRepository) {
		this.propertyRepository = propertyRepository;
		this.agentRepository = agentRepository;
	}

	@Transactional(readOnly = true)
	public List<AdminAgentPropertyGroupResponse> listPropertiesByAgent(String statusFilter, String query) {
		String q = normalizeQuery(query);
		Map<Long, Agent> agentsById = agentRepository.findAll().stream()
				.collect(Collectors.toMap(Agent::getId, a -> a, (a, b) -> a));

		List<Property> properties = propertyRepository.findAllByOrderByCreatedAtDesc().stream()
				.filter(p -> matchesStatus(p, statusFilter))
				.filter(p -> matchesQuery(p, q, agentsById.get(p.getAgentId())))
				.collect(Collectors.toList());

		Map<Long, AdminAgentPropertyGroupResponse> groups = new LinkedHashMap<>();
		for (Property property : properties) {
			AdminAgentPropertyGroupResponse group = groups.computeIfAbsent(property.getAgentId(),
					agentId -> buildGroup(agentId, agentsById.get(agentId)));
			group.getProperties().add(toListItem(property));
		}

		return groups.values().stream()
				.sorted(Comparator.comparing(AdminAgentPropertyGroupResponse::getAgentName,
						Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)))
				.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public long countAllProperties() {
		return propertyRepository.count();
	}

	private AdminAgentPropertyGroupResponse buildGroup(Long agentId, Agent agent) {
		AdminAgentPropertyGroupResponse group = new AdminAgentPropertyGroupResponse();
		group.setAgentId(agentId);
		if (agent != null) {
			group.setAgentCode(agent.getAgentCode());
			group.setAgentName(agent.getFullName());
			group.setAgencyName(agent.getAgencyName());
			group.setAgentEmail(agent.getEmail());
			group.setAgentCity(agent.getCity());
		} else {
			group.setAgentCode("—");
			group.setAgentName("Unknown agent");
			group.setAgencyName("—");
			group.setAgentEmail("—");
			group.setAgentCity("—");
		}
		return group;
	}

	private boolean matchesStatus(Property property, String statusFilter) {
		if (statusFilter == null || statusFilter.isBlank() || "ALL".equalsIgnoreCase(statusFilter)) {
			return true;
		}
		try {
			return property.getStatus() == PropertyStatus.valueOf(statusFilter.trim().toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new AuthException("Invalid status filter");
		}
	}

	private boolean matchesQuery(Property property, String q, Agent agent) {
		if (q.isEmpty()) {
			return true;
		}
		if (contains(property.getTitle(), q)
				|| contains(property.getPropertyCode(), q)
				|| contains(property.getCity(), q)
				|| contains(property.getLocality(), q)
				|| contains(property.getPropertySubType(), q)
				|| (property.getListingType() != null && contains(property.getListingType().name(), q))
				|| (property.getCategory() != null && contains(property.getCategory().name(), q))) {
			return true;
		}
		if (agent == null) {
			return false;
		}
		return contains(agent.getFullName(), q)
				|| contains(agent.getAgentCode(), q)
				|| contains(agent.getAgencyName(), q)
				|| contains(agent.getEmail(), q);
	}

	private boolean contains(String value, String q) {
		return value != null && value.toLowerCase().contains(q);
	}

	private String normalizeQuery(String query) {
		return query == null ? "" : query.trim().toLowerCase();
	}

	private AdminPropertyListItemResponse toListItem(Property property) {
		AdminPropertyListItemResponse item = new AdminPropertyListItemResponse();
		item.setId(property.getId());
		item.setPropertyCode(property.getPropertyCode());
		item.setTitle(property.getTitle());
		item.setListingType(property.getListingType());
		item.setCategory(property.getCategory());
		item.setStatus(property.getStatus());
		item.setPrice(property.getPrice());
		item.setCity(property.getCity());
		item.setLocality(property.getLocality());
		item.setViewCount(property.getViewCount());
		item.setCreatedAt(property.getCreatedAt());
		return item;
	}
}
