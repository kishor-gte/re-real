package com.realestate.main.dto.pgsubscription;

import java.math.BigDecimal;

public class PgSubscriptionAnalyticsResponse {

	private long activeSubscriptions;
	private long expiredSubscriptions;
	private long successfulPayments;
	private long pendingPayments;
	private BigDecimal totalRevenue;
	private long activePlans;
	private long inactivePlans;

	public long getActiveSubscriptions() {
		return activeSubscriptions;
	}

	public void setActiveSubscriptions(long activeSubscriptions) {
		this.activeSubscriptions = activeSubscriptions;
	}

	public long getExpiredSubscriptions() {
		return expiredSubscriptions;
	}

	public void setExpiredSubscriptions(long expiredSubscriptions) {
		this.expiredSubscriptions = expiredSubscriptions;
	}

	public long getSuccessfulPayments() {
		return successfulPayments;
	}

	public void setSuccessfulPayments(long successfulPayments) {
		this.successfulPayments = successfulPayments;
	}

	public long getPendingPayments() {
		return pendingPayments;
	}

	public void setPendingPayments(long pendingPayments) {
		this.pendingPayments = pendingPayments;
	}

	public BigDecimal getTotalRevenue() {
		return totalRevenue;
	}

	public void setTotalRevenue(BigDecimal totalRevenue) {
		this.totalRevenue = totalRevenue;
	}

	public long getActivePlans() {
		return activePlans;
	}

	public void setActivePlans(long activePlans) {
		this.activePlans = activePlans;
	}

	public long getInactivePlans() {
		return inactivePlans;
	}

	public void setInactivePlans(long inactivePlans) {
		this.inactivePlans = inactivePlans;
	}
}
