package com.realestate.main.dto.pgsubscription;

import java.time.LocalDateTime;

public class PgOwnerPostingStatusResponse {

	public static final String CODE_FREE_LIMIT = "FREE_PG_LIMIT_REACHED";

	private int totalPosted;
	private int freeLimit;
	private int maxAllowed;
	private int remainingPosts;
	private boolean canPost;
	private boolean unlimited;
	private boolean hasActiveSubscription;
	private Long currentPlanId;
	private String currentPlanName;
	private LocalDateTime subscriptionExpiry;
	private String restrictionCode;
	private String message;

	public int getTotalPosted() {
		return totalPosted;
	}

	public void setTotalPosted(int totalPosted) {
		this.totalPosted = totalPosted;
	}

	public int getFreeLimit() {
		return freeLimit;
	}

	public void setFreeLimit(int freeLimit) {
		this.freeLimit = freeLimit;
	}

	public int getMaxAllowed() {
		return maxAllowed;
	}

	public void setMaxAllowed(int maxAllowed) {
		this.maxAllowed = maxAllowed;
	}

	public int getRemainingPosts() {
		return remainingPosts;
	}

	public void setRemainingPosts(int remainingPosts) {
		this.remainingPosts = remainingPosts;
	}

	public boolean isCanPost() {
		return canPost;
	}

	public void setCanPost(boolean canPost) {
		this.canPost = canPost;
	}

	public boolean isUnlimited() {
		return unlimited;
	}

	public void setUnlimited(boolean unlimited) {
		this.unlimited = unlimited;
	}

	public boolean isHasActiveSubscription() {
		return hasActiveSubscription;
	}

	public void setHasActiveSubscription(boolean hasActiveSubscription) {
		this.hasActiveSubscription = hasActiveSubscription;
	}

	public Long getCurrentPlanId() {
		return currentPlanId;
	}

	public void setCurrentPlanId(Long currentPlanId) {
		this.currentPlanId = currentPlanId;
	}

	public String getCurrentPlanName() {
		return currentPlanName;
	}

	public void setCurrentPlanName(String currentPlanName) {
		this.currentPlanName = currentPlanName;
	}

	public LocalDateTime getSubscriptionExpiry() {
		return subscriptionExpiry;
	}

	public void setSubscriptionExpiry(LocalDateTime subscriptionExpiry) {
		this.subscriptionExpiry = subscriptionExpiry;
	}

	public String getRestrictionCode() {
		return restrictionCode;
	}

	public void setRestrictionCode(String restrictionCode) {
		this.restrictionCode = restrictionCode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
