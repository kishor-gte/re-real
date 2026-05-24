package com.realestate.main.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.realestate.main.entity.enums.SubscriptionPlanStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "pg_subscription_plans")
public class PgSubscriptionPlan {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "plan_id")
	private Long id;

	@Column(name = "plan_name", nullable = false, length = 80)
	private String planName;

	@Column(name = "plan_code", nullable = false, unique = true, length = 40)
	private String planCode;

	@Column(nullable = false, precision = 12, scale = 2)
	private BigDecimal price = BigDecimal.ZERO;

	@Column(name = "duration_days", nullable = false)
	private Integer durationDays = 30;

	@Column(name = "max_pg_listings", nullable = false)
	private Integer maxPgListings = 1;

	@Column(name = "max_room_listings", nullable = false)
	private Integer maxRoomListings = 10;

	@Column(name = "featured_pg_count", nullable = false)
	private Integer featuredPgCount = 0;

	@Column(name = "bed_management_access", nullable = false)
	private boolean bedManagementAccess = false;

	@Column(name = "tenant_analytics_access", nullable = false)
	private boolean tenantAnalyticsAccess = false;

	@Column(name = "premium_badge", nullable = false)
	private boolean premiumBadge = false;

	@Column(name = "priority_support", nullable = false)
	private boolean prioritySupport = false;

	@Column(columnDefinition = "TEXT")
	private String description;

	@Column(name = "recommended", nullable = false)
	private boolean recommended = false;

	@Column(name = "sort_order", nullable = false)
	private Integer sortOrder = 0;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private SubscriptionPlanStatus status = SubscriptionPlanStatus.ACTIVE;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

	@PrePersist
	void onCreate() {
		LocalDateTime now = LocalDateTime.now();
		createdAt = now;
		updatedAt = now;
	}

	@PreUpdate
	void onUpdate() {
		updatedAt = LocalDateTime.now();
	}

	public boolean isUnlimitedPgListings() {
		return maxPgListings != null && maxPgListings < 0;
	}

	public boolean isUnlimitedRooms() {
		return maxRoomListings != null && maxRoomListings < 0;
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

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}
}
