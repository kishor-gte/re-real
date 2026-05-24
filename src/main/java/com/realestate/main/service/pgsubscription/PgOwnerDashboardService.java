package com.realestate.main.service.pgsubscription;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.realestate.main.dto.pgsubscription.PgOwnerDashboardStatsResponse;
import com.realestate.main.dto.pgsubscription.PgOwnerPostingStatusResponse;
import com.realestate.main.entity.PgBooking;
import com.realestate.main.entity.PgProperty;
import com.realestate.main.entity.PgRoom;
import com.realestate.main.entity.enums.PgOwnerApprovalStatus;
import com.realestate.main.repository.PgBookingRepository;
import com.realestate.main.repository.PgPropertyRepository;
import com.realestate.main.repository.PgRoomRepository;
import com.realestate.main.repository.PgSubscriptionNotificationRepository;

@Service
public class PgOwnerDashboardService {

	private final PgPropertyRepository pgPropertyRepository;
	private final PgRoomRepository pgRoomRepository;
	private final PgBookingRepository pgBookingRepository;
	private final PgOwnerPostingLimitService postingLimitService;
	private final PgSubscriptionNotificationRepository notificationRepository;

	public PgOwnerDashboardService(PgPropertyRepository pgPropertyRepository, PgRoomRepository pgRoomRepository,
			PgBookingRepository pgBookingRepository, PgOwnerPostingLimitService postingLimitService,
			PgSubscriptionNotificationRepository notificationRepository) {
		this.pgPropertyRepository = pgPropertyRepository;
		this.pgRoomRepository = pgRoomRepository;
		this.pgBookingRepository = pgBookingRepository;
		this.postingLimitService = postingLimitService;
		this.notificationRepository = notificationRepository;
	}

	@Transactional(readOnly = true)
	public PgOwnerDashboardStatsResponse loadStats(Long pgOwnerId) {
		PgOwnerPostingStatusResponse posting = postingLimitService.getPostingStatus(pgOwnerId);
		PgOwnerDashboardStatsResponse stats = new PgOwnerDashboardStatsResponse();
		stats.setPostingStatus(posting);
		stats.setTotalPgListings(posting.getTotalPosted());
		stats.setRemainingFreeListings(Math.max(0, posting.getFreeLimit() - posting.getTotalPosted()));
		stats.setActiveSubscriptionPlan(
				posting.isHasActiveSubscription() ? posting.getCurrentPlanName() : "Free (1 PG)");
		stats.setSubscriptionExpiry(posting.getSubscriptionExpiry());
		stats.setCanPostPg(posting.isCanPost());
		stats.setShowUpgradeButton(!posting.isCanPost() || posting.getTotalPosted() >= posting.getFreeLimit());

		int totalBeds = 0;
		int occupiedBeds = 0;
		List<PgProperty> properties = pgPropertyRepository.findByOwnerIdOrderByCreatedAtDesc(pgOwnerId);
		for (PgProperty property : properties) {
			List<PgRoom> rooms = pgRoomRepository.findByPgPropertyIdOrderByFloorIdAscRoomNumberAsc(property.getId());
			for (PgRoom room : rooms) {
				totalBeds += room.getTotalBeds() != null ? room.getTotalBeds() : 0;
				occupiedBeds += room.getOccupiedBeds() != null ? room.getOccupiedBeds() : 0;
			}
		}
		stats.setTotalBedsAvailable(totalBeds);
		stats.setOccupiedBeds(occupiedBeds);

		YearMonth month = YearMonth.now();
		LocalDateTime monthStart = month.atDay(1).atStartOfDay();
		LocalDateTime monthEnd = month.atEndOfMonth().atTime(23, 59, 59);
		BigDecimal earnings = pgBookingRepository.findByPgOwnerIdOrderByCreatedAtDesc(pgOwnerId).stream()
				.filter(b -> b.getPaidAmount() != null && b.getPaidAmount().compareTo(BigDecimal.ZERO) > 0)
				.filter(b -> b.getBookingDate() != null && !b.getBookingDate().isBefore(monthStart)
						&& !b.getBookingDate().isAfter(monthEnd))
				.map(PgBooking::getPaidAmount)
				.reduce(BigDecimal.ZERO, BigDecimal::add);
		stats.setMonthlyEarnings(earnings);

		long inquiries = pgBookingRepository.countByPgOwnerIdAndOwnerApprovalStatus(pgOwnerId,
				PgOwnerApprovalStatus.PENDING);
		stats.setPgInquiries(inquiries);
		return stats;
	}
}
