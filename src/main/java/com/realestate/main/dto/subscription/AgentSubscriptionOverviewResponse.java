package com.realestate.main.dto.subscription;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AgentSubscriptionOverviewResponse {

	private AgentPostingStatusResponse postingStatus;
	private SubscriptionPlanDto activePlan;
	private Long activeSubscriptionId;
	private LocalDateTime startDate;
	private LocalDateTime expiryDate;
	private String subscriptionStatusLabel;
	private List<SubscriptionPlanDto> availablePlans = new ArrayList<>();
	private List<SubscriptionPaymentDto> paymentHistory = new ArrayList<>();
	private boolean canUpgradePlans;
	private String upgradeBlockedMessage;

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

	public AgentPostingStatusResponse getPostingStatus() {
		return postingStatus;
	}

	public void setPostingStatus(AgentPostingStatusResponse postingStatus) {
		this.postingStatus = postingStatus;
	}

	public SubscriptionPlanDto getActivePlan() {
		return activePlan;
	}

	public void setActivePlan(SubscriptionPlanDto activePlan) {
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

	public List<SubscriptionPlanDto> getAvailablePlans() {
		return availablePlans;
	}

	public void setAvailablePlans(List<SubscriptionPlanDto> availablePlans) {
		this.availablePlans = availablePlans;
	}

	public List<SubscriptionPaymentDto> getPaymentHistory() {
		return paymentHistory;
	}

	public void setPaymentHistory(List<SubscriptionPaymentDto> paymentHistory) {
		this.paymentHistory = paymentHistory;
	}
}
