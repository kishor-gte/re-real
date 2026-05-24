package com.realestate.main.dto.subscription;

import java.time.LocalDateTime;

public class AgentPostingStatusResponse {

	public static final String CODE_FREE_LIMIT = "FREE_LIMIT_REACHED";

	private boolean canPost;
	private int totalPosted;
	private int freeLimit;
	private int remainingPosts;
	private int maxAllowed;
	private boolean unlimited;
	private boolean hasActiveSubscription;
	private String currentPlanName;
	private Long currentPlanId;
	private LocalDateTime subscriptionExpiry;
	private String message;
	private String restrictionCode;

	public boolean isCanPost() {
		return canPost;
	}

	public void setCanPost(boolean canPost) {
		this.canPost = canPost;
	}

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

	public int getRemainingPosts() {
		return remainingPosts;
	}

	public void setRemainingPosts(int remainingPosts) {
		this.remainingPosts = remainingPosts;
	}

	public int getMaxAllowed() {
		return maxAllowed;
	}

	public void setMaxAllowed(int maxAllowed) {
		this.maxAllowed = maxAllowed;
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

	public String getCurrentPlanName() {
		return currentPlanName;
	}

	public void setCurrentPlanName(String currentPlanName) {
		this.currentPlanName = currentPlanName;
	}

	public Long getCurrentPlanId() {
		return currentPlanId;
	}

	public void setCurrentPlanId(Long currentPlanId) {
		this.currentPlanId = currentPlanId;
	}

	public LocalDateTime getSubscriptionExpiry() {
		return subscriptionExpiry;
	}

	public void setSubscriptionExpiry(LocalDateTime subscriptionExpiry) {
		this.subscriptionExpiry = subscriptionExpiry;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getRestrictionCode() {
		return restrictionCode;
	}

	public void setRestrictionCode(String restrictionCode) {
		this.restrictionCode = restrictionCode;
	}
}
