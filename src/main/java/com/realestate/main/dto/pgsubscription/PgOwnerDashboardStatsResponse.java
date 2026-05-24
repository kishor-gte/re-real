package com.realestate.main.dto.pgsubscription;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PgOwnerDashboardStatsResponse {

	private int totalPgListings;
	private int remainingFreeListings;
	private String activeSubscriptionPlan;
	private LocalDateTime subscriptionExpiry;
	private int totalBedsAvailable;
	private int occupiedBeds;
	private BigDecimal monthlyEarnings;
	private long pgInquiries;
	private boolean canPostPg;
	private boolean showUpgradeButton;
	private PgOwnerPostingStatusResponse postingStatus;

	public int getTotalPgListings() {
		return totalPgListings;
	}

	public void setTotalPgListings(int totalPgListings) {
		this.totalPgListings = totalPgListings;
	}

	public int getRemainingFreeListings() {
		return remainingFreeListings;
	}

	public void setRemainingFreeListings(int remainingFreeListings) {
		this.remainingFreeListings = remainingFreeListings;
	}

	public String getActiveSubscriptionPlan() {
		return activeSubscriptionPlan;
	}

	public void setActiveSubscriptionPlan(String activeSubscriptionPlan) {
		this.activeSubscriptionPlan = activeSubscriptionPlan;
	}

	public LocalDateTime getSubscriptionExpiry() {
		return subscriptionExpiry;
	}

	public void setSubscriptionExpiry(LocalDateTime subscriptionExpiry) {
		this.subscriptionExpiry = subscriptionExpiry;
	}

	public int getTotalBedsAvailable() {
		return totalBedsAvailable;
	}

	public void setTotalBedsAvailable(int totalBedsAvailable) {
		this.totalBedsAvailable = totalBedsAvailable;
	}

	public int getOccupiedBeds() {
		return occupiedBeds;
	}

	public void setOccupiedBeds(int occupiedBeds) {
		this.occupiedBeds = occupiedBeds;
	}

	public BigDecimal getMonthlyEarnings() {
		return monthlyEarnings;
	}

	public void setMonthlyEarnings(BigDecimal monthlyEarnings) {
		this.monthlyEarnings = monthlyEarnings;
	}

	public long getPgInquiries() {
		return pgInquiries;
	}

	public void setPgInquiries(long pgInquiries) {
		this.pgInquiries = pgInquiries;
	}

	public boolean isCanPostPg() {
		return canPostPg;
	}

	public void setCanPostPg(boolean canPostPg) {
		this.canPostPg = canPostPg;
	}

	public boolean isShowUpgradeButton() {
		return showUpgradeButton;
	}

	public void setShowUpgradeButton(boolean showUpgradeButton) {
		this.showUpgradeButton = showUpgradeButton;
	}

	public PgOwnerPostingStatusResponse getPostingStatus() {
		return postingStatus;
	}

	public void setPostingStatus(PgOwnerPostingStatusResponse postingStatus) {
		this.postingStatus = postingStatus;
	}
}
