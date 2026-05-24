package com.realestate.main.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.realestate.main.dto.response.PublicPgCardResponse;
import com.realestate.main.entity.PgProperty;
import com.realestate.main.entity.enums.GenderAllowed;
import com.realestate.main.entity.enums.PgType;
import com.realestate.main.repository.PgPropertyImageRepository;
import com.realestate.main.repository.PgPropertyRepository;

@Service
public class PublicPgBrowseService {

	private final PgPropertyRepository pgPropertyRepository;
	private final PgPropertyImageRepository pgPropertyImageRepository;

	public PublicPgBrowseService(PgPropertyRepository pgPropertyRepository,
			PgPropertyImageRepository pgPropertyImageRepository) {
		this.pgPropertyRepository = pgPropertyRepository;
		this.pgPropertyImageRepository = pgPropertyImageRepository;
	}

	@Transactional(readOnly = true)
	public List<PublicPgCardResponse> browsePublished(String city, String state) {
		String cityFilter = normalizeFilter(city);
		String stateFilter = normalizeFilter(state);
		return pgPropertyRepository.searchPublished(cityFilter, stateFilter).stream()
				.map(this::toCard)
				.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public List<String> listPublishedCities() {
		return pgPropertyRepository.findDistinctPublishedCities();
	}

	@Transactional(readOnly = true)
	public PublicPgCardResponse getPublishedCard(Long pgPropertyId) {
		return pgPropertyRepository.findById(pgPropertyId)
				.filter(p -> p.getStatus() == com.realestate.main.entity.enums.PgPropertyStatus.PUBLISHED)
				.map(this::toCard)
				.orElse(null);
	}

	private PublicPgCardResponse toCard(PgProperty property) {
		PublicPgCardResponse card = new PublicPgCardResponse();
		card.setId(property.getId());
		card.setPgCode(property.getPgCode());
		card.setPgName(property.getPgName());
		card.setPgType(property.getPgType());
		card.setPgTypeLabel(formatPgType(property.getPgType()));
		card.setGenderAllowed(property.getGenderAllowed());
		card.setGenderLabel(formatGender(property.getGenderAllowed()));
		card.setCity(property.getCity());
		card.setState(property.getState());
		card.setAddress(property.getAddress());
		card.setLandmark(property.getLandmark());
		card.setTotalRooms(property.getTotalRooms());
		card.setAvailableBeds(property.getAvailableBeds());
		card.setMonthlyRent(property.getMonthlyRent());
		card.setSpecsSummary(buildSpecsSummary(property));
		pgPropertyImageRepository.findByPgPropertyIdOrderBySortOrderAscUploadedAtAsc(property.getId()).stream()
				.findFirst()
				.ifPresent(img -> card.setCoverImageUrl(img.getImagePath()));
		return card;
	}

	private String buildSpecsSummary(PgProperty property) {
		StringBuilder sb = new StringBuilder();
		sb.append(formatPgType(property.getPgType()));
		if (property.getAvailableBeds() != null && property.getAvailableBeds() > 0) {
			sb.append(" · ").append(property.getAvailableBeds()).append(" bed(s) available");
		}
		if (property.getTotalRooms() != null && property.getTotalRooms() > 0) {
			sb.append(" · ").append(property.getTotalRooms()).append(" room(s)");
		}
		return sb.toString();
	}

	private static String normalizeFilter(String value) {
		if (value == null || value.isBlank() || "ALL".equalsIgnoreCase(value.trim())) {
			return null;
		}
		return value.trim();
	}

	private static String formatPgType(PgType pgType) {
		if (pgType == null) {
			return "PG";
		}
		return switch (pgType) {
		case BOYS_PG -> "Boys PG";
		case GIRLS_PG -> "Girls PG";
		case CO_LIVING -> "Co-living";
		};
	}

	private static String formatGender(GenderAllowed gender) {
		if (gender == null) {
			return null;
		}
		return switch (gender) {
		case MALE -> "Male only";
		case FEMALE -> "Female only";
		case BOTH -> "Male & Female";
		};
	}
}
