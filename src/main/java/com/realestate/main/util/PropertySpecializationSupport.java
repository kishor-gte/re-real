package com.realestate.main.util;

import java.util.List;
import java.util.Set;

import com.realestate.main.dto.request.PropertyCreateRequest;
import com.realestate.main.entity.enums.AgentSpecialization;
import com.realestate.main.exception.AuthException;

public final class PropertySpecializationSupport {

	private PropertySpecializationSupport() {
	}

	public static List<String> subTypesFor(AgentSpecialization specialization) {
		return switch (specialization) {
		case RESIDENTIAL -> List.of("Independent House", "Builder Floor", "Row House", "Penthouse", "Duplex");
		case APARTMENTS -> List.of("Studio", "1 BHK Apartment", "2 BHK Apartment", "3 BHK Apartment", "4 BHK Apartment",
				"5+ BHK Apartment");
		case LUXURY_VILLAS -> List.of("Luxury Villa", "Farmhouse", "Bungalow", "Waterfront Villa");
		case COMMERCIAL -> List.of("Office Space", "Retail Shop", "Showroom", "Warehouse", "Co-working Space",
				"Industrial Shed");
		case PLOTS_LANDS -> List.of("Residential Plot", "Commercial Plot", "Industrial Plot", "Agricultural Land",
				"Farm Land");
		case RENTAL -> List.of("Apartment for Rent", "Independent House for Rent", "Villa for Rent",
				"PG / Co-living", "Commercial Space for Rent");
		case CONSTRUCTION -> List.of("Residential Project", "Commercial Project", "Mixed-Use Project",
				"Plotted Development");
		};
	}

	public static boolean isValidSubType(AgentSpecialization specialization, String subType) {
		if (subType == null || subType.isBlank()) {
			return false;
		}
		String normalized = subType.trim();
		return subTypesFor(specialization).stream().anyMatch(s -> s.equalsIgnoreCase(normalized));
	}

	public static void validateForSpecialization(AgentSpecialization specialization, PropertyCreateRequest req) {
		if (!isValidSubType(specialization, req.getPropertySubType())) {
			throw new AuthException("Invalid property type for your specialization");
		}
		switch (specialization) {
		case RESIDENTIAL, APARTMENTS, RENTAL -> validateResidential(req);
		case LUXURY_VILLAS -> validateLuxury(req);
		case COMMERCIAL -> validateCommercial(req);
		case PLOTS_LANDS -> validatePlot(req);
		case CONSTRUCTION -> validateConstruction(req);
		}
	}

	private static void validateResidential(PropertyCreateRequest req) {
		requirePositive(req.getAreaSqFt(), "Built-up / carpet area (sq.ft) is required");
		if (req.getBhk() == null || req.getBhk().isBlank()) {
			throw new AuthException("BHK configuration is required");
		}
	}

	private static void validateLuxury(PropertyCreateRequest req) {
		requirePositive(req.getAreaSqFt(), "Built-up area (sq.ft) is required");
		if (req.getPlotAreaSqFt() != null && req.getPlotAreaSqFt() <= 0) {
			throw new AuthException("Plot area must be greater than zero");
		}
		if (req.getBhk() == null || req.getBhk().isBlank()) {
			throw new AuthException("BHK configuration is required");
		}
	}

	private static void validateCommercial(PropertyCreateRequest req) {
		if (req.getCommercialType() == null) {
			throw new AuthException("Commercial property type is required");
		}
		requirePositive(req.getAreaSqFt(), "Carpet / built-up area (sq.ft) is required");
	}

	private static void validatePlot(PropertyCreateRequest req) {
		requirePositive(req.getPlotAreaSqFt(), "Plot area (sq.ft) is required");
		if (req.getLandUse() == null) {
			throw new AuthException("Land use type is required");
		}
	}

	private static void validateConstruction(PropertyCreateRequest req) {
		if (req.getProjectName() == null || req.getProjectName().isBlank()) {
			throw new AuthException("Project name is required");
		}
		if (req.getBuilderName() == null || req.getBuilderName().isBlank()) {
			throw new AuthException("Builder name is required");
		}
		if (req.getConstructionStatus() == null) {
			throw new AuthException("Construction status is required");
		}
	}

	private static void requirePositive(Double value, String message) {
		if (value == null || value <= 0) {
			throw new AuthException(message);
		}
	}

	public static Set<String> amenitySuggestions(AgentSpecialization specialization) {
		return switch (specialization) {
		case COMMERCIAL -> Set.of("Lift", "Power Backup", "Parking", "Security", "Cafeteria", "Conference Room");
		case PLOTS_LANDS -> Set.of("Gated Community", "Water Connection", "Electricity", "Road Access", "Corner Plot");
		case CONSTRUCTION -> Set.of("Clubhouse", "Swimming Pool", "Gym", "Kids Play Area", "24x7 Security", "Power Backup");
		default -> Set.of("Lift", "Parking", "Power Backup", "Security", "Gym", "Swimming Pool", "Garden", "Clubhouse");
		};
	}

	public static String formatSpecialization(AgentSpecialization specialization) {
		return specialization.name().replace('_', ' ');
	}
}
