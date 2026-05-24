package com.realestate.main.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import com.realestate.main.dto.response.AgentDashboardStats;
import com.realestate.main.dto.subscription.AgentPostingStatusResponse;
import com.realestate.main.entity.enums.BookingStatus;
import com.realestate.main.entity.enums.EnquiryStatus;
import com.realestate.main.repository.AgentReferralRepository;
import com.realestate.main.repository.PropertyBookingRepository;
import com.realestate.main.repository.PropertyEnquiryRepository;
import com.realestate.main.repository.PropertyRepository;
import com.realestate.main.service.subscription.AgentPostingLimitService;

import java.time.format.DateTimeFormatter;

@Service
public class AgentDashboardService {

	private final AgentReferralRepository agentReferralRepository;
	private final PropertyRepository propertyRepository;
	private final PropertyEnquiryRepository propertyEnquiryRepository;
	private final PropertyBookingRepository propertyBookingRepository;
	private final AgentBookingService agentBookingService;
	private final AgentPostingLimitService postingLimitService;

	private static final DateTimeFormatter EXPIRY_FMT = DateTimeFormatter.ofPattern("dd MMM yyyy");

	public AgentDashboardService(AgentReferralRepository agentReferralRepository,
			PropertyRepository propertyRepository, PropertyEnquiryRepository propertyEnquiryRepository,
			PropertyBookingRepository propertyBookingRepository, AgentBookingService agentBookingService,
			AgentPostingLimitService postingLimitService) {
		this.agentReferralRepository = agentReferralRepository;
		this.propertyRepository = propertyRepository;
		this.propertyEnquiryRepository = propertyEnquiryRepository;
		this.propertyBookingRepository = propertyBookingRepository;
		this.agentBookingService = agentBookingService;
		this.postingLimitService = postingLimitService;
	}

	@Transactional(readOnly = true)
	public AgentDashboardStats loadStats(Long agentId) {
		AgentDashboardStats stats = new AgentDashboardStats();
		stats.setTotalProperties((int) propertyRepository.countByAgentId(agentId));
		stats.setTotalLeads((int) propertyEnquiryRepository.countByAgentId(agentId));
		stats.setUnreadMessages((int) propertyEnquiryRepository.countByAgentIdAndStatus(agentId, EnquiryStatus.PENDING));
		AgentPostingStatusResponse posting = postingLimitService.getPostingStatus(agentId);
		stats.setCanPostProperty(posting.isCanPost());
		stats.setCurrentPlanName(posting.getCurrentPlanName() != null ? posting.getCurrentPlanName() : "Free Plan");
		stats.setRemainingFreePosts(Math.max(0, posting.getFreeLimit() - posting.getTotalPosted()));
		stats.setRemainingPosts(posting.isUnlimited() ? -1 : posting.getRemainingPosts());
		stats.setSubscriptionStatus(posting.isHasActiveSubscription() ? "Active" : "Free");
		if (posting.getSubscriptionExpiry() != null) {
			stats.setSubscriptionExpiry(posting.getSubscriptionExpiry().format(EXPIRY_FMT));
		} else {
			stats.setSubscriptionExpiry("—");
		}
		stats.setReferralEarnings(0);
		stats.setReferralCount(agentReferralRepository.countByReferrerAgentId(agentId));
		stats.setPropertyViews((int) propertyRepository.sumViewCountByAgentId(agentId));
		stats.setTotalBookings((int) propertyBookingRepository.countByAgentIdAndBookingStatusNot(agentId,
				BookingStatus.CANCELLED));
		stats.setPendingBookings((int) propertyBookingRepository.countByAgentIdAndBookingStatus(agentId,
				BookingStatus.PENDING));
		stats.setCompletedBookings(stats.getTotalBookings() - stats.getPendingBookings());
		stats.setTotalCollected(formatInr(agentBookingService.sumCollectedForAgent(agentId)));
		stats.setTotalPending(formatInr(agentBookingService.sumPendingForAgent(agentId)));
		return stats;
	}

	private static String formatInr(BigDecimal amount) {
		if (amount == null) {
			return "0";
		}
		double n = amount.doubleValue();
		if (n >= 10000000) {
			return String.format("%.2f Cr", n / 10000000);
		}
		if (n >= 100000) {
			return String.format("%.2f L", n / 100000);
		}
		return String.format("%,.0f", n);
	}
}
