package com.realestate.main.dto.response;

public class AgentDashboardStats {

	private int totalProperties;
	private int totalLeads;
	private int unreadMessages;
	private String subscriptionStatus;
	private String currentPlanName;
	private int remainingFreePosts;
	private int remainingPosts;
	private String subscriptionExpiry;
	private boolean canPostProperty;
	private double referralEarnings;
	private long referralCount;
	private int propertyViews;
	private int totalBookings;
	private int pendingBookings;
	private int completedBookings;
	private String totalCollected;
	private String totalPending;

	public int getTotalProperties() {
		return totalProperties;
	}

	public void setTotalProperties(int totalProperties) {
		this.totalProperties = totalProperties;
	}

	public int getTotalLeads() {
		return totalLeads;
	}

	public void setTotalLeads(int totalLeads) {
		this.totalLeads = totalLeads;
	}

	public int getUnreadMessages() {
		return unreadMessages;
	}

	public void setUnreadMessages(int unreadMessages) {
		this.unreadMessages = unreadMessages;
	}

	public String getSubscriptionStatus() {
		return subscriptionStatus;
	}

	public void setSubscriptionStatus(String subscriptionStatus) {
		this.subscriptionStatus = subscriptionStatus;
	}

	public String getCurrentPlanName() {
		return currentPlanName;
	}

	public void setCurrentPlanName(String currentPlanName) {
		this.currentPlanName = currentPlanName;
	}

	public int getRemainingFreePosts() {
		return remainingFreePosts;
	}

	public void setRemainingFreePosts(int remainingFreePosts) {
		this.remainingFreePosts = remainingFreePosts;
	}

	public int getRemainingPosts() {
		return remainingPosts;
	}

	public void setRemainingPosts(int remainingPosts) {
		this.remainingPosts = remainingPosts;
	}

	public String getSubscriptionExpiry() {
		return subscriptionExpiry;
	}

	public void setSubscriptionExpiry(String subscriptionExpiry) {
		this.subscriptionExpiry = subscriptionExpiry;
	}

	public boolean isCanPostProperty() {
		return canPostProperty;
	}

	public void setCanPostProperty(boolean canPostProperty) {
		this.canPostProperty = canPostProperty;
	}

	public double getReferralEarnings() {
		return referralEarnings;
	}

	public void setReferralEarnings(double referralEarnings) {
		this.referralEarnings = referralEarnings;
	}

	public long getReferralCount() {
		return referralCount;
	}

	public void setReferralCount(long referralCount) {
		this.referralCount = referralCount;
	}

	public int getPropertyViews() {
		return propertyViews;
	}

	public void setPropertyViews(int propertyViews) {
		this.propertyViews = propertyViews;
	}

	public int getTotalBookings() {
		return totalBookings;
	}

	public void setTotalBookings(int totalBookings) {
		this.totalBookings = totalBookings;
	}

	public int getPendingBookings() {
		return pendingBookings;
	}

	public void setPendingBookings(int pendingBookings) {
		this.pendingBookings = pendingBookings;
	}

	public int getCompletedBookings() {
		return completedBookings;
	}

	public void setCompletedBookings(int completedBookings) {
		this.completedBookings = completedBookings;
	}

	public String getTotalCollected() {
		return totalCollected;
	}

	public void setTotalCollected(String totalCollected) {
		this.totalCollected = totalCollected;
	}

	public String getTotalPending() {
		return totalPending;
	}

	public void setTotalPending(String totalPending) {
		this.totalPending = totalPending;
	}
}
