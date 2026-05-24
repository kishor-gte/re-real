package com.realestate.main.service.pgsubscription;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.realestate.main.dto.pgsubscription.PgOwnerPostingStatusResponse;
import com.realestate.main.entity.PgOwnerSubscription;
import com.realestate.main.entity.PgSubscriptionPlan;
import com.realestate.main.exception.AuthException;
import com.realestate.main.repository.PgOwnerSubscriptionRepository;
import com.realestate.main.repository.PgPropertyRepository;
import com.realestate.main.repository.PgSubscriptionPlanRepository;

@Service
public class PgOwnerPostingLimitService {

	public static final int FREE_PG_LIMIT = 1;

	private final PgPropertyRepository pgPropertyRepository;
	private final PgOwnerSubscriptionRepository subscriptionRepository;
	private final PgSubscriptionPlanRepository planRepository;
	private final PgSubscriptionNotificationService notificationService;

	public PgOwnerPostingLimitService(PgPropertyRepository pgPropertyRepository,
			PgOwnerSubscriptionRepository subscriptionRepository,
			PgSubscriptionPlanRepository planRepository,
			PgSubscriptionNotificationService notificationService) {
		this.pgPropertyRepository = pgPropertyRepository;
		this.subscriptionRepository = subscriptionRepository;
		this.planRepository = planRepository;
		this.notificationService = notificationService;
	}

	@Transactional(readOnly = true)
	public PgOwnerPostingStatusResponse getPostingStatus(Long pgOwnerId) {
		int posted = (int) pgPropertyRepository.countByOwnerId(pgOwnerId);
		LocalDateTime now = LocalDateTime.now();
		Optional<PgOwnerSubscription> activeSub = subscriptionRepository.findActiveForPgOwner(pgOwnerId, now);

		PgOwnerPostingStatusResponse status = new PgOwnerPostingStatusResponse();
		status.setTotalPosted(posted);
		status.setFreeLimit(FREE_PG_LIMIT);

		int maxAllowed = FREE_PG_LIMIT;
		boolean unlimited = false;
		String planName = "Free (1 PG listing)";

		if (activeSub.isPresent()) {
			PgOwnerSubscription sub = activeSub.get();
			PgSubscriptionPlan plan = planRepository.findById(sub.getPlanId()).orElse(null);
			if (plan != null) {
				status.setHasActiveSubscription(true);
				status.setCurrentPlanId(plan.getId());
				status.setCurrentPlanName(plan.getPlanName());
				status.setSubscriptionExpiry(sub.getExpiryDate());
				planName = plan.getPlanName();
				if (plan.isUnlimitedPgListings()) {
					unlimited = true;
					maxAllowed = Integer.MAX_VALUE;
				} else {
					maxAllowed = plan.getMaxPgListings();
				}
			}
		} else {
			status.setHasActiveSubscription(false);
			status.setCurrentPlanName(planName);
		}

		status.setUnlimited(unlimited);
		status.setMaxAllowed(unlimited ? -1 : maxAllowed);

		int remaining;
		boolean canPost;
		if (unlimited) {
			remaining = Integer.MAX_VALUE;
			canPost = true;
		} else {
			remaining = Math.max(0, maxAllowed - posted);
			canPost = posted < maxAllowed;
		}

		status.setRemainingPosts(remaining == Integer.MAX_VALUE ? -1 : remaining);
		status.setCanPost(canPost);

		if (!canPost) {
			if (!status.isHasActiveSubscription() && posted >= FREE_PG_LIMIT) {
				status.setRestrictionCode(PgOwnerPostingStatusResponse.CODE_FREE_LIMIT);
				status.setMessage(
						"You have used your 1 free PG listing. Upgrade your subscription to add more PG properties.");
			} else if (status.isHasActiveSubscription()) {
				status.setRestrictionCode("PLAN_LIMIT_REACHED");
				status.setMessage("Your current plan allows up to " + maxAllowed
						+ " PG listings. Upgrade to a higher plan for more.");
			} else {
				status.setRestrictionCode(PgOwnerPostingStatusResponse.CODE_FREE_LIMIT);
				status.setMessage("PG posting limit reached. Please upgrade your subscription.");
			}
		} else if (!status.isHasActiveSubscription()) {
			status.setMessage("You have " + Math.max(0, FREE_PG_LIMIT - posted) + " free PG listing(s) remaining.");
		} else {
			status.setMessage(unlimited ? "Unlimited PG listings on your " + planName + " plan."
					: remaining + " PG listing slot(s) remaining on " + planName + ".");
		}

		return status;
	}

	@Transactional(readOnly = true)
	public void assertCanPost(Long pgOwnerId) {
		PgOwnerPostingStatusResponse status = getPostingStatus(pgOwnerId);
		if (!status.isCanPost()) {
			notificationService.notifyListingLimitReached(pgOwnerId);
			throw new AuthException(status.getMessage());
		}
	}

	@Transactional
	public void syncUsageAfterPgCreated(Long pgOwnerId) {
		subscriptionRepository.findActiveForPgOwner(pgOwnerId, LocalDateTime.now()).ifPresent(sub -> {
			int posted = (int) pgPropertyRepository.countByOwnerId(pgOwnerId);
			sub.setPgUsed(posted);
			if (sub.getMaxPgAllowed() != null && sub.getMaxPgAllowed() >= 0) {
				sub.setRemainingPg(Math.max(0, sub.getMaxPgAllowed() - posted));
			}
			subscriptionRepository.save(sub);
		});
	}
}
