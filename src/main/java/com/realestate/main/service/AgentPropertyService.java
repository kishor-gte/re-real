package com.realestate.main.service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.realestate.main.dto.request.PropertyCreateRequest;
import com.realestate.main.dto.response.PropertyResponse;
import com.realestate.main.entity.Agent;
import com.realestate.main.entity.Property;
import com.realestate.main.entity.enums.AgentSpecialization;
import com.realestate.main.entity.enums.PropertyStatus;
import com.realestate.main.exception.AuthException;
import com.realestate.main.repository.AgentRepository;
import com.realestate.main.repository.PropertyRepository;
import com.realestate.main.service.subscription.AgentPostingLimitService;
import com.realestate.main.util.PropertySpecializationSupport;

@Service
public class AgentPropertyService {

	private static final SecureRandom RANDOM = new SecureRandom();

	private final PropertyRepository propertyRepository;
	private final AgentRepository agentRepository;
	private final FileStorageService fileStorageService;
	private final AgentPostingLimitService postingLimitService;

	public AgentPropertyService(PropertyRepository propertyRepository, AgentRepository agentRepository,
			FileStorageService fileStorageService, AgentPostingLimitService postingLimitService) {
		this.propertyRepository = propertyRepository;
		this.agentRepository = agentRepository;
		this.fileStorageService = fileStorageService;
		this.postingLimitService = postingLimitService;
	}

	@Transactional
	public PropertyResponse createProperty(Long agentId, PropertyCreateRequest request, MultipartFile primaryImage,
			List<MultipartFile> galleryImages) {
		postingLimitService.assertCanPost(agentId);
		Agent agent = agentRepository.findById(agentId)
				.orElseThrow(() -> new AuthException("Agent account not found"));
		AgentSpecialization specialization = agent.getSpecialization();

		PropertySpecializationSupport.validateForSpecialization(specialization, request);

		if (primaryImage == null || primaryImage.isEmpty()) {
			throw new AuthException("Primary property image is required");
		}

		String primaryUrl = fileStorageService.storePropertyImage(primaryImage, agentId);
		List<String> galleryUrls = new ArrayList<>();
		if (galleryImages != null) {
			for (MultipartFile file : galleryImages) {
				if (file != null && !file.isEmpty()) {
					String url = fileStorageService.storePropertyImage(file, agentId);
					if (url != null) {
						galleryUrls.add(url);
					}
				}
			}
		}

		Property property = mapToEntity(request, agentId, specialization);
		property.setPropertyCode(generatePropertyCode());
		property.setPrimaryImageUrl(primaryUrl);
		if (!galleryUrls.isEmpty()) {
			property.setGalleryUrls(String.join(",", galleryUrls));
		}
		property.setStatus(PropertyStatus.ACTIVE);
		property.setViewCount(0);

		property = propertyRepository.save(property);
		postingLimitService.syncUsageAfterPropertyCreated(agentId);
		return toResponse(property);
	}

	@Transactional(readOnly = true)
	public List<PropertyResponse> listAgentProperties(Long agentId) {
		return propertyRepository.findByAgentIdOrderByCreatedAtDesc(agentId).stream()
				.map(this::toResponse)
				.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public PropertyResponse getProperty(Long agentId, Long propertyId) {
		Property property = findOwnedProperty(agentId, propertyId);
		return toResponse(property);
	}

	@Transactional
	public PropertyResponse updateProperty(Long agentId, Long propertyId, PropertyCreateRequest request,
			MultipartFile primaryImage, List<MultipartFile> galleryImages) {
		Agent agent = agentRepository.findById(agentId)
				.orElseThrow(() -> new AuthException("Agent account not found"));
		Property property = findOwnedProperty(agentId, propertyId);

		PropertySpecializationSupport.validateForSpecialization(agent.getSpecialization(), request);
		applyRequestToProperty(property, request, agent.getSpecialization());

		if (primaryImage != null && !primaryImage.isEmpty()) {
			property.setPrimaryImageUrl(fileStorageService.storePropertyImage(primaryImage, agentId));
		}

		if (galleryImages != null && !galleryImages.isEmpty()) {
			List<String> existing = new ArrayList<>();
			if (property.getGalleryUrls() != null && !property.getGalleryUrls().isBlank()) {
				existing.addAll(Arrays.asList(property.getGalleryUrls().split(",")));
			}
			for (MultipartFile file : galleryImages) {
				if (file != null && !file.isEmpty()) {
					String url = fileStorageService.storePropertyImage(file, agentId);
					if (url != null) {
						existing.add(url);
					}
				}
			}
			if (!existing.isEmpty()) {
				property.setGalleryUrls(String.join(",", existing));
			}
		}

		property = propertyRepository.save(property);
		return toResponse(property);
	}

	@Transactional
	public void deleteProperty(Long agentId, Long propertyId) {
		Property property = findOwnedProperty(agentId, propertyId);
		propertyRepository.delete(property);
	}

	@Transactional
	public PropertyResponse togglePropertyStatus(Long agentId, Long propertyId) {
		Property property = findOwnedProperty(agentId, propertyId);
		if (property.getStatus() == PropertyStatus.ACTIVE) {
			property.setStatus(PropertyStatus.INACTIVE);
		} else {
			property.setStatus(PropertyStatus.ACTIVE);
		}
		property = propertyRepository.save(property);
		return toResponse(property);
	}

	private Property findOwnedProperty(Long agentId, Long propertyId) {
		return propertyRepository.findByIdAndAgentId(propertyId, agentId)
				.orElseThrow(() -> new AuthException("Property not found"));
	}

	private void applyRequestToProperty(Property p, PropertyCreateRequest req, AgentSpecialization specialization) {
		p.setTitle(req.getTitle().trim());
		p.setDescription(trimToNull(req.getDescription()));
		p.setListingType(req.getListingType());
		p.setCategory(specialization);
		p.setPropertySubType(req.getPropertySubType().trim());
		p.setPrice(req.getPrice());
		p.setPriceNegotiable(req.isPriceNegotiable());
		p.setAreaSqFt(req.getAreaSqFt());
		p.setPlotAreaSqFt(req.getPlotAreaSqFt());
		p.setSuperBuiltUpSqFt(req.getSuperBuiltUpSqFt());
		p.setAddressLine(req.getAddressLine().trim());
		p.setLocality(req.getLocality().trim());
		p.setCity(req.getCity().trim());
		p.setState(req.getState().trim());
		p.setPincode(req.getPincode().trim());
		p.setBhk(trimToNull(req.getBhk()));
		p.setBathrooms(req.getBathrooms());
		p.setBalconies(req.getBalconies());
		p.setFloorNumber(req.getFloorNumber());
		p.setTotalFloors(req.getTotalFloors());
		p.setFurnishing(req.getFurnishing());
		p.setPropertyAgeYears(req.getPropertyAgeYears());
		p.setParkingSlots(req.getParkingSlots());
		p.setCommercialType(req.getCommercialType());
		p.setSeatsCapacity(req.getSeatsCapacity());
		p.setPlotLengthFt(req.getPlotLengthFt());
		p.setPlotWidthFt(req.getPlotWidthFt());
		p.setFacing(trimToNull(req.getFacing()));
		p.setLandUse(req.getLandUse());
		p.setBoundaryWall(req.getBoundaryWall());
		p.setCornerPlot(req.getCornerPlot());
		p.setProjectName(trimToNull(req.getProjectName()));
		p.setBuilderName(trimToNull(req.getBuilderName()));
		p.setConstructionStatus(req.getConstructionStatus());
		p.setPossessionDate(req.getPossessionDate());
		p.setTotalUnits(req.getTotalUnits());
		p.setUnitConfiguration(trimToNull(req.getUnitConfiguration()));
		p.setPrivatePool(req.getPrivatePool());
		p.setPrivateGarden(req.getPrivateGarden());
		p.setAmenities(trimToNull(req.getAmenities()));
	}

	private Property mapToEntity(PropertyCreateRequest req, Long agentId, AgentSpecialization specialization) {
		Property p = new Property();
		p.setAgentId(agentId);
		applyRequestToProperty(p, req, specialization);
		return p;
	}

	private PropertyResponse toResponse(Property p) {
		PropertyResponse r = new PropertyResponse();
		r.setId(p.getId());
		r.setPropertyCode(p.getPropertyCode());
		r.setTitle(p.getTitle());
		r.setDescription(p.getDescription());
		r.setListingType(p.getListingType());
		r.setCategory(p.getCategory());
		r.setPropertySubType(p.getPropertySubType());
		r.setStatus(p.getStatus());
		r.setPrice(p.getPrice());
		r.setPriceNegotiable(p.isPriceNegotiable());
		r.setAreaSqFt(p.getAreaSqFt());
		r.setPlotAreaSqFt(p.getPlotAreaSqFt());
		r.setAddressLine(p.getAddressLine());
		r.setLocality(p.getLocality());
		r.setCity(p.getCity());
		r.setState(p.getState());
		r.setPincode(p.getPincode());
		r.setBhk(p.getBhk());
		r.setBathrooms(p.getBathrooms());
		r.setBalconies(p.getBalconies());
		r.setFloorNumber(p.getFloorNumber());
		r.setTotalFloors(p.getTotalFloors());
		r.setFurnishing(p.getFurnishing());
		r.setPropertyAgeYears(p.getPropertyAgeYears());
		r.setParkingSlots(p.getParkingSlots());
		r.setCommercialType(p.getCommercialType());
		r.setSeatsCapacity(p.getSeatsCapacity());
		r.setPlotLengthFt(p.getPlotLengthFt());
		r.setPlotWidthFt(p.getPlotWidthFt());
		r.setFacing(p.getFacing());
		r.setLandUse(p.getLandUse());
		r.setBoundaryWall(p.getBoundaryWall());
		r.setCornerPlot(p.getCornerPlot());
		r.setProjectName(p.getProjectName());
		r.setBuilderName(p.getBuilderName());
		r.setConstructionStatus(p.getConstructionStatus());
		r.setPossessionDate(p.getPossessionDate());
		r.setTotalUnits(p.getTotalUnits());
		r.setUnitConfiguration(p.getUnitConfiguration());
		r.setPrivatePool(p.getPrivatePool());
		r.setPrivateGarden(p.getPrivateGarden());
		r.setAmenities(p.getAmenities());
		r.setPrimaryImageUrl(p.getPrimaryImageUrl());
		if (p.getGalleryUrls() != null && !p.getGalleryUrls().isBlank()) {
			r.setGalleryUrls(Arrays.asList(p.getGalleryUrls().split(",")));
		}
		r.setViewCount(p.getViewCount());
		r.setCreatedAt(p.getCreatedAt());
		return r;
	}

	private String generatePropertyCode() {
		for (int i = 0; i < 20; i++) {
			String code = "PROP" + (100000 + RANDOM.nextInt(900000));
			if (!propertyRepository.existsByPropertyCode(code)) {
				return code;
			}
		}
		throw new AuthException("Could not generate property code. Please try again.");
	}

	private String trimToNull(String value) {
		if (value == null) {
			return null;
		}
		String t = value.trim();
		return t.isEmpty() ? null : t;
	}
}
