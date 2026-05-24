package com.realestate.main.dto.response;

import java.util.ArrayList;
import java.util.List;

public class AgentAnalyticsResponse {

	private int totalProperties;
	private long totalViews;
	private long totalLeads;
	private long pendingLeads;
	private long acceptedLeads;
	private int totalBookings;
	private int pendingBookings;
	private int completedBookings;
	private String totalCollected;
	private String totalPending;
	private double leadToBookingRate;
	private List<PropertyViewStat> topPropertiesByViews = new ArrayList<>();
	private List<StatusCount> leadsByStatus = new ArrayList<>();
	private List<StatusCount> bookingsByStatus = new ArrayList<>();
	private List<MonthlyCount> monthlyLeads = new ArrayList<>();

	public int getTotalProperties() {
		return totalProperties;
	}

	public void setTotalProperties(int totalProperties) {
		this.totalProperties = totalProperties;
	}

	public long getTotalViews() {
		return totalViews;
	}

	public void setTotalViews(long totalViews) {
		this.totalViews = totalViews;
	}

	public long getTotalLeads() {
		return totalLeads;
	}

	public void setTotalLeads(long totalLeads) {
		this.totalLeads = totalLeads;
	}

	public long getPendingLeads() {
		return pendingLeads;
	}

	public void setPendingLeads(long pendingLeads) {
		this.pendingLeads = pendingLeads;
	}

	public long getAcceptedLeads() {
		return acceptedLeads;
	}

	public void setAcceptedLeads(long acceptedLeads) {
		this.acceptedLeads = acceptedLeads;
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

	public double getLeadToBookingRate() {
		return leadToBookingRate;
	}

	public void setLeadToBookingRate(double leadToBookingRate) {
		this.leadToBookingRate = leadToBookingRate;
	}

	public List<PropertyViewStat> getTopPropertiesByViews() {
		return topPropertiesByViews;
	}

	public void setTopPropertiesByViews(List<PropertyViewStat> topPropertiesByViews) {
		this.topPropertiesByViews = topPropertiesByViews;
	}

	public List<StatusCount> getLeadsByStatus() {
		return leadsByStatus;
	}

	public void setLeadsByStatus(List<StatusCount> leadsByStatus) {
		this.leadsByStatus = leadsByStatus;
	}

	public List<StatusCount> getBookingsByStatus() {
		return bookingsByStatus;
	}

	public void setBookingsByStatus(List<StatusCount> bookingsByStatus) {
		this.bookingsByStatus = bookingsByStatus;
	}

	public List<MonthlyCount> getMonthlyLeads() {
		return monthlyLeads;
	}

	public void setMonthlyLeads(List<MonthlyCount> monthlyLeads) {
		this.monthlyLeads = monthlyLeads;
	}

	public static class PropertyViewStat {
		private Long propertyId;
		private String title;
		private String propertyCode;
		private int views;
		private int enquiries;

		public Long getPropertyId() {
			return propertyId;
		}

		public void setPropertyId(Long propertyId) {
			this.propertyId = propertyId;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getPropertyCode() {
			return propertyCode;
		}

		public void setPropertyCode(String propertyCode) {
			this.propertyCode = propertyCode;
		}

		public int getViews() {
			return views;
		}

		public void setViews(int views) {
			this.views = views;
		}

		public int getEnquiries() {
			return enquiries;
		}

		public void setEnquiries(int enquiries) {
			this.enquiries = enquiries;
		}
	}

	public static class StatusCount {
		private String label;
		private long count;

		public StatusCount() {
		}

		public StatusCount(String label, long count) {
			this.label = label;
			this.count = count;
		}

		public String getLabel() {
			return label;
		}

		public void setLabel(String label) {
			this.label = label;
		}

		public long getCount() {
			return count;
		}

		public void setCount(long count) {
			this.count = count;
		}
	}

	public static class MonthlyCount {
		private String month;
		private long count;

		public MonthlyCount() {
		}

		public MonthlyCount(String month, long count) {
			this.month = month;
			this.count = count;
		}

		public String getMonth() {
			return month;
		}

		public void setMonth(String month) {
			this.month = month;
		}

		public long getCount() {
			return count;
		}

		public void setCount(long count) {
			this.count = count;
		}
	}
}
