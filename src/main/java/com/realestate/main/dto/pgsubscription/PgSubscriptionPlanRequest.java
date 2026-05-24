package com.realestate.main.dto.pgsubscription;

import java.math.BigDecimal;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class PgSubscriptionPlanRequest {

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
	private Integer maxPgListings;

	@NotNull
	private Integer maxRoomListings;

	@Min(0)
	private Integer featuredPgCount = 0;

	private boolean bedManagementAccess;

	private boolean tenantAnalyticsAccess;

	private boolean premiumBadge;

	private boolean prioritySupport;

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

	public Integer getMaxPgListings() {
		return maxPgListings;
	}

	public void setMaxPgListings(Integer maxPgListings) {
		this.maxPgListings = maxPgListings;
	}

	public Integer getMaxRoomListings() {
		return maxRoomListings;
	}

	public void setMaxRoomListings(Integer maxRoomListings) {
		this.maxRoomListings = maxRoomListings;
	}

	public Integer getFeaturedPgCount() {
		return featuredPgCount;
	}

	public void setFeaturedPgCount(Integer featuredPgCount) {
		this.featuredPgCount = featuredPgCount;
	}

	public boolean isBedManagementAccess() {
		return bedManagementAccess;
	}

	public void setBedManagementAccess(boolean bedManagementAccess) {
		this.bedManagementAccess = bedManagementAccess;
	}

	public boolean isTenantAnalyticsAccess() {
		return tenantAnalyticsAccess;
	}

	public void setTenantAnalyticsAccess(boolean tenantAnalyticsAccess) {
		this.tenantAnalyticsAccess = tenantAnalyticsAccess;
	}

	public boolean isPremiumBadge() {
		return premiumBadge;
	}

	public void setPremiumBadge(boolean premiumBadge) {
		this.premiumBadge = premiumBadge;
	}

	public boolean isPrioritySupport() {
		return prioritySupport;
	}

	public void setPrioritySupport(boolean prioritySupport) {
		this.prioritySupport = prioritySupport;
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
