package com.realestate.main.dto.subscription;

import java.math.BigDecimal;

import com.realestate.main.entity.SubscriptionPlan;
import com.realestate.main.entity.enums.SubscriptionPlanStatus;

public class SubscriptionPlanDto {

	private Long id;
	private String planName;
	private String planCode;
	private BigDecimal price;
	private Integer durationDays;
	private Integer maxProperties;
	private Integer featuredListings;
	private Integer propertyImagesLimit;
	private boolean premiumBadge;
	private Integer leadsLimit;
	private boolean whatsappLeadAccess;
	private boolean analyticsAccess;
	private String description;
	private boolean recommended;
	private Integer sortOrder;
	private SubscriptionPlanStatus status;
	private boolean unlimitedProperties;

	public static SubscriptionPlanDto from(SubscriptionPlan plan) {
		SubscriptionPlanDto dto = new SubscriptionPlanDto();
		dto.setId(plan.getId());
		dto.setPlanName(plan.getPlanName());
		dto.setPlanCode(plan.getPlanCode());
		dto.setPrice(plan.getPrice());
		dto.setDurationDays(plan.getDurationDays());
		dto.setMaxProperties(plan.getMaxProperties());
		dto.setFeaturedListings(plan.getFeaturedListings());
		dto.setPropertyImagesLimit(plan.getPropertyImagesLimit());
		dto.setPremiumBadge(plan.isPremiumBadge());
		dto.setLeadsLimit(plan.getLeadsLimit());
		dto.setWhatsappLeadAccess(plan.isWhatsappLeadAccess());
		dto.setAnalyticsAccess(plan.isAnalyticsAccess());
		dto.setDescription(plan.getDescription());
		dto.setRecommended(plan.isRecommended());
		dto.setSortOrder(plan.getSortOrder());
		dto.setStatus(plan.getStatus());
		dto.setUnlimitedProperties(plan.isUnlimitedProperties());
		return dto;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

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

	public SubscriptionPlanStatus getStatus() {
		return status;
	}

	public void setStatus(SubscriptionPlanStatus status) {
		this.status = status;
	}

	public boolean isUnlimitedProperties() {
		return unlimitedProperties;
	}

	public void setUnlimitedProperties(boolean unlimitedProperties) {
		this.unlimitedProperties = unlimitedProperties;
	}
}
