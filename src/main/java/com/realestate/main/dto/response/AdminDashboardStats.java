package com.realestate.main.dto.response;

public class AdminDashboardStats {

	private long totalUsers;
	private long totalBuyers;
	private long totalSellers;
	private long totalProperties;
	private long totalServiceProviders;
	private long pendingVerifications;
	private long openComplaints;
	private double revenueTotal;
	private long activeSubscriptions;
	private long pendingAgentApprovals;
	private long totalAgents;
	private long activeAgents;

	private long pendingPgOwnerApprovals;
	private long totalPgOwners;
	private long activePgOwners;

	public long getPendingPgOwnerApprovals() {
		return pendingPgOwnerApprovals;
	}

	public void setPendingPgOwnerApprovals(long pendingPgOwnerApprovals) {
		this.pendingPgOwnerApprovals = pendingPgOwnerApprovals;
	}

	public long getTotalPgOwners() {
		return totalPgOwners;
	}

	public void setTotalPgOwners(long totalPgOwners) {
		this.totalPgOwners = totalPgOwners;
	}

	public long getActivePgOwners() {
		return activePgOwners;
	}

	public void setActivePgOwners(long activePgOwners) {
		this.activePgOwners = activePgOwners;
	}

	public long getTotalUsers() {
		return totalUsers;
	}

	public void setTotalUsers(long totalUsers) {
		this.totalUsers = totalUsers;
	}

	public long getTotalBuyers() {
		return totalBuyers;
	}

	public void setTotalBuyers(long totalBuyers) {
		this.totalBuyers = totalBuyers;
	}

	public long getTotalSellers() {
		return totalSellers;
	}

	public void setTotalSellers(long totalSellers) {
		this.totalSellers = totalSellers;
	}

	public long getTotalProperties() {
		return totalProperties;
	}

	public void setTotalProperties(long totalProperties) {
		this.totalProperties = totalProperties;
	}

	public long getTotalServiceProviders() {
		return totalServiceProviders;
	}

	public void setTotalServiceProviders(long totalServiceProviders) {
		this.totalServiceProviders = totalServiceProviders;
	}

	public long getPendingVerifications() {
		return pendingVerifications;
	}

	public void setPendingVerifications(long pendingVerifications) {
		this.pendingVerifications = pendingVerifications;
	}

	public long getOpenComplaints() {
		return openComplaints;
	}

	public void setOpenComplaints(long openComplaints) {
		this.openComplaints = openComplaints;
	}

	public double getRevenueTotal() {
		return revenueTotal;
	}

	public void setRevenueTotal(double revenueTotal) {
		this.revenueTotal = revenueTotal;
	}

	public long getActiveSubscriptions() {
		return activeSubscriptions;
	}

	public void setActiveSubscriptions(long activeSubscriptions) {
		this.activeSubscriptions = activeSubscriptions;
	}

	public long getPendingAgentApprovals() {
		return pendingAgentApprovals;
	}

	public void setPendingAgentApprovals(long pendingAgentApprovals) {
		this.pendingAgentApprovals = pendingAgentApprovals;
	}

	public long getTotalAgents() {
		return totalAgents;
	}

	public void setTotalAgents(long totalAgents) {
		this.totalAgents = totalAgents;
	}

	public long getActiveAgents() {
		return activeAgents;
	}

	public void setActiveAgents(long activeAgents) {
		this.activeAgents = activeAgents;
	}
}
