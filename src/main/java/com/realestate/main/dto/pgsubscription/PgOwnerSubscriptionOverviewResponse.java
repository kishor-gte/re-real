package com.realestate.main.dto.pgsubscription;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PgOwnerSubscriptionOverviewResponse {

	private PgOwnerPostingStatusResponse postingStatus;
	private PgSubscriptionPlanDto activePlan;
	private Long activeSubscriptionId;
	private LocalDateTime startDate;
	private LocalDateTime expiryDate;
	private String subscriptionStatusLabel;
	private List<PgSubscriptionPlanDto> availablePlans = new ArrayList<>();
	private List<PgSubscriptionPaymentDto> paymentHistory = new ArrayList<>();
	private boolean canUpgradePlans;
	private String upgradeBlockedMessage;

	public PgOwnerPostingStatusResponse getPostingStatus() {
		return postingStatus;
	}

	public void setPostingStatus(PgOwnerPostingStatusResponse postingStatus) {
		this.postingStatus = postingStatus;
	}

	public PgSubscriptionPlanDto getActivePlan() {
		return activePlan;
	}

	public void setActivePlan(PgSubscriptionPlanDto activePlan) {
		this.activePlan = activePlan;
	}

	public Long getActiveSubscriptionId() {
		return activeSubscriptionId;
	}

	public void setActiveSubscriptionId(Long activeSubscriptionId) {
		this.activeSubscriptionId = activeSubscriptionId;
	}

	public LocalDateTime getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDateTime startDate) {
		this.startDate = startDate;
	}

	public LocalDateTime getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(LocalDateTime expiryDate) {
		this.expiryDate = expiryDate;
	}

	public String getSubscriptionStatusLabel() {
		return subscriptionStatusLabel;
	}

	public void setSubscriptionStatusLabel(String subscriptionStatusLabel) {
		this.subscriptionStatusLabel = subscriptionStatusLabel;
	}

	public List<PgSubscriptionPlanDto> getAvailablePlans() {
		return availablePlans;
	}

	public void setAvailablePlans(List<PgSubscriptionPlanDto> availablePlans) {
		this.availablePlans = availablePlans;
	}

	public List<PgSubscriptionPaymentDto> getPaymentHistory() {
		return paymentHistory;
	}

	public void setPaymentHistory(List<PgSubscriptionPaymentDto> paymentHistory) {
		this.paymentHistory = paymentHistory;
	}

	public boolean isCanUpgradePlans() {
		return canUpgradePlans;
	}

	public void setCanUpgradePlans(boolean canUpgradePlans) {
		this.canUpgradePlans = canUpgradePlans;
	}

	public String getUpgradeBlockedMessage() {
		return upgradeBlockedMessage;
	}

	public void setUpgradeBlockedMessage(String upgradeBlockedMessage) {
		this.upgradeBlockedMessage = upgradeBlockedMessage;
	}
}
