package com.realestate.main.dto.response;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class AgentReferralEarningsResponse {

	private String referralCode;
	private long totalReferrals;
	private BigDecimal totalEarnings;
	private BigDecimal pendingEarnings;
	private BigDecimal paidEarnings;
	private List<ReferralRow> referrals = new ArrayList<>();

	public String getReferralCode() {
		return referralCode;
	}

	public void setReferralCode(String referralCode) {
		this.referralCode = referralCode;
	}

	public long getTotalReferrals() {
		return totalReferrals;
	}

	public void setTotalReferrals(long totalReferrals) {
		this.totalReferrals = totalReferrals;
	}

	public BigDecimal getTotalEarnings() {
		return totalEarnings;
	}

	public void setTotalEarnings(BigDecimal totalEarnings) {
		this.totalEarnings = totalEarnings;
	}

	public BigDecimal getPendingEarnings() {
		return pendingEarnings;
	}

	public void setPendingEarnings(BigDecimal pendingEarnings) {
		this.pendingEarnings = pendingEarnings;
	}

	public BigDecimal getPaidEarnings() {
		return paidEarnings;
	}

	public void setPaidEarnings(BigDecimal paidEarnings) {
		this.paidEarnings = paidEarnings;
	}

	public List<ReferralRow> getReferrals() {
		return referrals;
	}

	public void setReferrals(List<ReferralRow> referrals) {
		this.referrals = referrals;
	}

	public static class ReferralRow {
		private Long id;
		private String referredAgentName;
		private String referredAgentEmail;
		private String referredAgentCode;
		private String status;
		private String referralCodeUsed;
		private String registeredAt;
		private BigDecimal commissionAmount;
		private String commissionStatus;

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public String getReferredAgentName() {
			return referredAgentName;
		}

		public void setReferredAgentName(String referredAgentName) {
			this.referredAgentName = referredAgentName;
		}

		public String getReferredAgentEmail() {
			return referredAgentEmail;
		}

		public void setReferredAgentEmail(String referredAgentEmail) {
			this.referredAgentEmail = referredAgentEmail;
		}

		public String getReferredAgentCode() {
			return referredAgentCode;
		}

		public void setReferredAgentCode(String referredAgentCode) {
			this.referredAgentCode = referredAgentCode;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public String getReferralCodeUsed() {
			return referralCodeUsed;
		}

		public void setReferralCodeUsed(String referralCodeUsed) {
			this.referralCodeUsed = referralCodeUsed;
		}

		public String getRegisteredAt() {
			return registeredAt;
		}

		public void setRegisteredAt(String registeredAt) {
			this.registeredAt = registeredAt;
		}

		public BigDecimal getCommissionAmount() {
			return commissionAmount;
		}

		public void setCommissionAmount(BigDecimal commissionAmount) {
			this.commissionAmount = commissionAmount;
		}

		public String getCommissionStatus() {
			return commissionStatus;
		}

		public void setCommissionStatus(String commissionStatus) {
			this.commissionStatus = commissionStatus;
		}
	}
}
