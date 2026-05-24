package com.realestate.main.dto.subscription;

import java.math.BigDecimal;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class SubscriptionPlanRequest {

	@NotBlank
	private String planName;

	@NotBlank
	private String planCode;

	@NotNull
	@Min(0)
	private BigDecimal price;

	@NotNull
	@Min(1)
	private Integer durationDays;

	@NotNull
	private Integer maxProperties;

	@Min(0)
	private Integer featuredListings = 0;

	@Min(1)
	private Integer propertyImagesLimit = 5;

	private boolean premiumBadge;

	@Min(0)
	private Integer leadsLimit = 0;

	private boolean whatsappLeadAccess;

	private boolean analyticsAccess;

	private String description;

	private boolean recommended;

	private Integer sortOrder = 0;

	public String getPlanName() {
		return planName;
	}

	public void setPlanName(String planName) {
		this.planName = planName;
	}

	public String getPlanCode() {
		return planCode;
	}

	public void setPlanCode(String planCode) {
		this.planCode = planCode;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public Integer getDurationDays() {
		return durationDays;
	}

	public void setDurationDays(Integer durationDays) {
		this.durationDays = durationDays;
	}

	public Integer getMaxProperties() {
		return maxProperties;
	}

	public void setMaxProperties(Integer maxProperties) {
		this.maxProperties = maxProperties;
	}

	public Integer getFeaturedListings() {
		return featuredListings;
	}

	public void setFeaturedListings(Integer featuredListings) {
		this.featuredListings = featuredListings;
	}

	public Integer getPropertyImagesLimit() {
		return propertyImagesLimit;
	}

	public void setPropertyImagesLimit(Integer propertyImagesLimit) {
		this.propertyImagesLimit = propertyImagesLimit;
	}

	public boolean isPremiumBadge() {
		return premiumBadge;
	}

	public void setPremiumBadge(boolean premiumBadge) {
		this.premiumBadge = premiumBadge;
	}

	public Integer getLeadsLimit() {
		return leadsLimit;
	}

	public void setLeadsLimit(Integer leadsLimit) {
		this.leadsLimit = leadsLimit;
	}

	public boolean isWhatsappLeadAccess() {
		return whatsappLeadAccess;
	}

	public void setWhatsappLeadAccess(boolean whatsappLeadAccess) {
		this.whatsappLeadAccess = whatsappLeadAccess;
	}

	public boolean isAnalyticsAccess() {
		return analyticsAccess;
	}

	public void setAnalyticsAccess(boolean analyticsAccess) {
		this.analyticsAccess = analyticsAccess;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isRecommended() {
		return recommended;
	}

	public void setRecommended(boolean recommended) {
		this.recommended = recommended;
	}

	public Integer getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(Integer sortOrder) {
		this.sortOrder = sortOrder;
	}
}
