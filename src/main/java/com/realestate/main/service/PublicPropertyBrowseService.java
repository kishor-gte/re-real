package com.realestate.main.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.realestate.main.dto.response.PublicPropertyCardResponse;
import com.realestate.main.dto.response.PublicPropertyDetailResponse;
import com.realestate.main.entity.Agent;
import com.realestate.main.entity.Property;
import com.realestate.main.entity.PropertyBooking;
import com.realestate.main.entity.enums.AgentSpecialization;
import com.realestate.main.entity.enums.BookingStatus;
import com.realestate.main.entity.enums.ListingType;
import com.realestate.main.entity.enums.PropertyStatus;
import com.realestate.main.exception.AuthException;
import com.realestate.main.repository.AgentRepository;
import com.realestate.main.repository.PropertyBookingRepository;
import com.realestate.main.repository.PropertyRepository;
import com.realestate.main.util.PropertySpecializationSupport;

@Service
public class PublicPropertyBrowseService {

	private static final List<BookingStatus> OCCUPIED_BOOKING_STATUSES = Arrays.asList(BookingStatus.CONFIRMED,
			BookingStatus.PARTIALLY_PAID, BookingStatus.FULLY_PAID, BookingStatus.EMI_ACTIVE);

	private final PropertyRepository propertyRepository;
	private final AgentRepository agentRepository;
	private final PropertyBookingRepository bookingRepository;

	public PublicPropertyBrowseService(PropertyRepository propertyRepository, AgentRepository agentRepository,
			PropertyBookingRepository bookingRepository) {
		this.propertyRepository = propertyRepository;
		this.agentRepository = agentRepository;
		this.bookingRepository = bookingRepository;
	}

	@Transactional(readOnly = true)
	public List<PublicPropertyCardResponse> browseActive(AgentSpecialization category, ListingType listingType) {
		return propertyRepository.searchActiveListings(PropertyStatus.ACTIVE, category, listingType).stream()
				.filter(p -> isVisibleOnPublicBrowse(p.getId()))
				.map(this::toCard)
				.collect(Collectors.toList());
	}

	@Transactional
	public PublicPropertyDetailResponse getActiveDetail(Long id) {
		Property property = propertyRepository.findById(id)
				.filter(p -> p.getStatus() == PropertyStatus.ACTIVE && isVisibleOnPublicBrowse(id))
				.orElseThrow(() -> new AuthException("Property not found or no longer available"));
		property.setViewCount(property.getViewCount() + 1);
		propertyRepository.save(property);
		Agent agent = agentRepository.findById(property.getAgentId()).orElse(null);
		return toDetail(property, agent);
	}

	@Transactional(readOnly = true)
	public PublicPropertyCardResponse toPublicCard(Property p) {
		return toCard(p);
	}

	private boolean isVisibleOnPublicBrowse(Long propertyId) {
		if (hasActivePendingReservation(propertyId)) {
			return false;
		}
		return !bookingRepository.existsByPropertyIdAndBookingStatusIn(propertyId, OCCUPIED_BOOKING_STATUSES);
	}

	private boolean hasActivePendingReservation(Long propertyId) {
		return bookingRepository.findTopByPropertyIdAndBookingStatusOrderByCreatedAtDesc(propertyId,
				BookingStatus.PENDING).map(this::pendingReservationActive).orElse(false);
	}

	private boolean pendingReservationActive(PropertyBooking pending) {
		return pending.getReservationExpiresAt() == null
				|| pending.getReservationExpiresAt().isAfter(LocalDateTime.now());
	}

	private PublicPropertyCardResponse toCard(Property p) {
		PublicPropertyCardResponse r = new PublicPropertyCardResponse();
		r.setId(p.getId());
		r.setPropertyCode(p.getPropertyCode());
		r.setTitle(p.getTitle());
		r.setListingType(p.getListingType());
		r.setCategory(p.getCategory());
		r.setCategoryLabel(PropertySpecializationSupport.formatSpecialization(p.getCategory()));
		r.setPropertySubType(p.getPropertySubType());
		r.setPrice(p.getPrice());
		r.setPriceNegotiable(p.isPriceNegotiable());
		r.setAreaSqFt(p.getAreaSqFt());
		r.setPlotAreaSqFt(p.getPlotAreaSqFt());
		r.setBhk(p.getBhk());
		r.setLocality(p.getLocality());
		r.setCity(p.getCity());
		r.setState(p.getState());
		r.setPrimaryImageUrl(p.getPrimaryImageUrl());
		r.setAmenities(p.getAmenities());
		r.setSpecsSummary(buildSpecsSummary(p));
		return r;
	}

	private String buildSpecsSummary(Property p) {
		StringBuilder sb = new StringBuilder();
		if (p.getBhk() != null && !p.getBhk().isBlank()) {
			sb.append(p.getBhk());
		}
		if (p.getAreaSqFt() != null && p.getAreaSqFt() > 0) {
			if (sb.length() > 0) {
				sb.append(" • ");
			}
			sb.append(Math.round(p.getAreaSqFt())).append(" sq.ft");
		}
		if (p.getPlotAreaSqFt() != null && p.getPlotAreaSqFt() > 0) {
			if (sb.length() > 0) {
				sb.append(" • ");
			}
			sb.append(Math.round(p.getPlotAreaSqFt())).append(" plot sq.ft");
		}
		if (p.getCommercialType() != null) {
			if (sb.length() > 0) {
				sb.append(" • ");
			}
			sb.append(p.getCommercialType().name().replace('_', ' '));
		}
		if (p.getProjectName() != null && !p.getProjectName().isBlank()) {
			if (sb.length() > 0) {
				sb.append(" • ");
			}
			sb.append(p.getProjectName());
		}
		if (sb.length() == 0 && p.getPropertySubType() != null) {
			sb.append(p.getPropertySubType());
		}
		return sb.toString();
	}

	private PublicPropertyDetailResponse toDetail(Property p, Agent agent) {
		PublicPropertyDetailResponse r = new PublicPropertyDetailResponse();
		r.setId(p.getId());
		r.setPropertyCode(p.getPropertyCode());
		r.setTitle(p.getTitle());
		r.setDescription(p.getDescription());
		r.setListingType(p.getListingType());
		r.setListingTypeLabel(formatListingType(p.getListingType()));
		r.setCategory(p.getCategory());
		r.setCategoryLabel(PropertySpecializationSupport.formatSpecialization(p.getCategory()));
		r.setPropertySubType(p.getPropertySubType());
		r.setPrice(p.getPrice());
		r.setPriceNegotiable(p.isPriceNegotiable());
		r.setAreaSqFt(p.getAreaSqFt());
		r.setPlotAreaSqFt(p.getPlotAreaSqFt());
		r.setSuperBuiltUpSqFt(p.getSuperBuiltUpSqFt());
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
		r.setFurnishingLabel(formatEnum(p.getFurnishing()));
		r.setPropertyAgeYears(p.getPropertyAgeYears());
		r.setParkingSlots(p.getParkingSlots());
		r.setCommercialType(p.getCommercialType());
		r.setCommercialTypeLabel(formatEnum(p.getCommercialType()));
		r.setSeatsCapacity(p.getSeatsCapacity());
		r.setPlotLengthFt(p.getPlotLengthFt());
		r.setPlotWidthFt(p.getPlotWidthFt());
		r.setFacing(p.getFacing());
		r.setLandUse(p.getLandUse());
		r.setLandUseLabel(formatEnum(p.getLandUse()));
		r.setBoundaryWall(p.getBoundaryWall());
		r.setCornerPlot(p.getCornerPlot());
		r.setProjectName(p.getProjectName());
		r.setBuilderName(p.getBuilderName());
		r.setConstructionStatus(p.getConstructionStatus());
		r.setConstructionStatusLabel(formatEnum(p.getConstructionStatus()));
		r.setPossessionDate(p.getPossessionDate());
		r.setTotalUnits(p.getTotalUnits());
		r.setUnitConfiguration(p.getUnitConfiguration());
		r.setPrivatePool(p.getPrivatePool());
		r.setPrivateGarden(p.getPrivateGarden());
		r.setAmenities(p.getAmenities());
		r.setPrimaryImageUrl(p.getPrimaryImageUrl());
		if (p.getGalleryUrls() != null && !p.getGalleryUrls().isBlank()) {
			r.setGalleryUrls(Arrays.stream(p.getGalleryUrls().split(","))
					.map(String::trim)
					.filter(s -> !s.isEmpty())
					.collect(Collectors.toList()));
		}
		r.setViewCount(p.getViewCount());
		if (agent != null) {
			r.setAgentName(agent.getFullName());
		}
		return r;
	}

	private static String formatListingType(ListingType listingType) {
		if (listingType == null) {
			return null;
		}
		return switch (listingType) {
		case SALE -> "For Sale";
		case RENT -> "For Rent";
		};
	}

	private static String formatEnum(Enum<?> value) {
		if (value == null) {
			return null;
		}
		String name = value.name().replace('_', ' ').toLowerCase();
		if (name.isEmpty()) {
			return null;
		}
		return Character.toUpperCase(name.charAt(0)) + name.substring(1);
	}
}
