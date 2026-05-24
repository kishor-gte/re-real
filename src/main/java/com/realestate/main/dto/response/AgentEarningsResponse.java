package com.realestate.main.dto.response;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class AgentEarningsResponse {

	private BigDecimal totalCollected;
	private BigDecimal totalPending;
	private BigDecimal totalBookingValue;
	private int bookingCount;
	private List<EarningsRow> rows = new ArrayList<>();
	private List<MonthlyEarnings> monthlyBreakdown = new ArrayList<>();

	public BigDecimal getTotalCollected() {
		return totalCollected;
	}

	public void setTotalCollected(BigDecimal totalCollected) {
		this.totalCollected = totalCollected;
	}

	public BigDecimal getTotalPending() {
		return totalPending;
	}

	public void setTotalPending(BigDecimal totalPending) {
		this.totalPending = totalPending;
	}

	public BigDecimal getTotalBookingValue() {
		return totalBookingValue;
	}

	public void setTotalBookingValue(BigDecimal totalBookingValue) {
		this.totalBookingValue = totalBookingValue;
	}

	public int getBookingCount() {
		return bookingCount;
	}

	public void setBookingCount(int bookingCount) {
		this.bookingCount = bookingCount;
	}

	public List<EarningsRow> getRows() {
		return rows;
	}

	public void setRows(List<EarningsRow> rows) {
		this.rows = rows;
	}

	public List<MonthlyEarnings> getMonthlyBreakdown() {
		return monthlyBreakdown;
	}

	public void setMonthlyBreakdown(List<MonthlyEarnings> monthlyBreakdown) {
		this.monthlyBreakdown = monthlyBreakdown;
	}

	public static class EarningsRow {
		private Long bookingId;
		private String bookingCode;
		private String propertyTitle;
		private String buyerName;
		private String statusLabel;
		private BigDecimal totalAmount;
		private BigDecimal paidAmount;
		private BigDecimal pendingAmount;
		private String bookingDate;

		public Long getBookingId() {
			return bookingId;
		}

		public void setBookingId(Long bookingId) {
			this.bookingId = bookingId;
		}

		public String getBookingCode() {
			return bookingCode;
		}

		public void setBookingCode(String bookingCode) {
			this.bookingCode = bookingCode;
		}

		public String getPropertyTitle() {
			return propertyTitle;
		}

		public void setPropertyTitle(String propertyTitle) {
			this.propertyTitle = propertyTitle;
		}

		public String getBuyerName() {
			return buyerName;
		}

		public void setBuyerName(String buyerName) {
			this.buyerName = buyerName;
		}

		public String getStatusLabel() {
			return statusLabel;
		}

		public void setStatusLabel(String statusLabel) {
			this.statusLabel = statusLabel;
		}

		public BigDecimal getTotalAmount() {
			return totalAmount;
		}

		public void setTotalAmount(BigDecimal totalAmount) {
			this.totalAmount = totalAmount;
		}

		public BigDecimal getPaidAmount() {
			return paidAmount;
		}

		public void setPaidAmount(BigDecimal paidAmount) {
			this.paidAmount = paidAmount;
		}

		public BigDecimal getPendingAmount() {
			return pendingAmount;
		}

		public void setPendingAmount(BigDecimal pendingAmount) {
			this.pendingAmount = pendingAmount;
		}

		public String getBookingDate() {
			return bookingDate;
		}

		public void setBookingDate(String bookingDate) {
			this.bookingDate = bookingDate;
		}
	}

	public static class MonthlyEarnings {
		private String month;
		private BigDecimal collected;

		public MonthlyEarnings() {
		}

		public MonthlyEarnings(String month, BigDecimal collected) {
			this.month = month;
			this.collected = collected;
		}

		public String getMonth() {
			return month;
		}

		public void setMonth(String month) {
			this.month = month;
		}

		public BigDecimal getCollected() {
			return collected;
		}

		public void setCollected(BigDecimal collected) {
			this.collected = collected;
		}
	}
}
