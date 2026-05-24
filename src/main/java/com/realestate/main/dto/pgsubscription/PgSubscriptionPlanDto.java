package com.realestate.main.dto.pgsubscription;

import java.math.BigDecimal;

import com.realestate.main.entity.PgSubscriptionPlan;
import com.realestate.main.entity.enums.SubscriptionPlanStatus;

public class PgSubscriptionPlanDto {

	private Long id;
	private String planName;
	private String planCode;
	private BigDecimal price;
	private Integer durationDays;
	private Integer maxPgListings;
	private Integer maxRoomListings;
	private Integer featuredPgCount;
	private boolean bedManagementAccess;
	private boolean tenantAnalyticsAccess;
	private boolean premiumBadge;
	private boolean prioritySupport;
	private String description;
	private boolean recommended;
	private Integer sortOrder;
	private SubscriptionPlanStatus status;
	private boolean unlimitedPgListings;
	private boolean unlimitedRooms;

	public static PgSubscriptionPlanDto from(PgSubscriptionPlan plan) {
		PgSubscriptionPlanDto dto = new PgSubscriptionPlanDto();
		dto.setId(plan.getId());
		dto.setPlanName(plan.getPlanName());
		dto.setPlanCode(plan.getPlanCode());
		dto.setPrice(plan.getPrice());
		dto.setDurationDays(plan.getDurationDays());
		dto.setMaxPgListings(plan.getMaxPgListings());
		dto.setMaxRoomListings(plan.getMaxRoomListings());
		dto.setFeaturedPgCount(plan.getFeaturedPgCount());
		dto.setBedManagementAccess(plan.isBedManagementAccess());
		dto.setTenantAnalyticsAccess(plan.isTenantAnalyticsAccess());
		dto.setPremiumBadge(plan.isPremiumBadge());
		dto.setPrioritySupport(plan.isPrioritySupport());
		dto.setDescription(plan.getDescription());
		dto.setRecommended(plan.isRecommended());
		dto.setSortOrder(plan.getSortOrder());
		dto.setStatus(plan.getStatus());
		dto.setUnlimitedPgListings(plan.isUnlimitedPgListings());
		dto.setUnlimitedRooms(plan.isUnlimitedRooms());
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

	public SubscriptionPlanStatus getStatus() {
		return status;
	}

	public void setStatus(SubscriptionPlanStatus status) {
		this.status = status;
	}

	public boolean isUnlimitedPgListings() {
		return unlimitedPgListings;
	}

	public void setUnlimitedPgListings(boolean unlimitedPgListings) {
		this.unlimitedPgListings = unlimitedPgListings;
	}

	public boolean isUnlimitedRooms() {
		return unlimitedRooms;
	}

	public void setUnlimitedRooms(boolean unlimitedRooms) {
		this.unlimitedRooms = unlimitedRooms;
	}
}
