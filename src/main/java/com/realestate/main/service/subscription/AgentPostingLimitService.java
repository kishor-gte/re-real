package com.realestate.main.service.subscription;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.realestate.main.dto.subscription.AgentPostingStatusResponse;
import com.realestate.main.entity.AgentSubscription;
import com.realestate.main.entity.SubscriptionPlan;
import com.realestate.main.exception.AuthException;
import com.realestate.main.repository.AgentSubscriptionRepository;
import com.realestate.main.repository.PropertyRepository;
import com.realestate.main.repository.SubscriptionPlanRepository;

@Service
public class AgentPostingLimitService {

	public static final int FREE_PROPERTY_LIMIT = 2;

	private final PropertyRepository propertyRepository;
	private final AgentSubscriptionRepository agentSubscriptionRepository;
	private final SubscriptionPlanRepository subscriptionPlanRepository;

	public AgentPostingLimitService(PropertyRepository propertyRepository,
			AgentSubscriptionRepository agentSubscriptionRepository,
			SubscriptionPlanRepository subscriptionPlanRepository) {
		this.propertyRepository = propertyRepository;
		this.agentSubscriptionRepository = agentSubscriptionRepository;
		this.subscriptionPlanRepository = subscriptionPlanRepository;
	}

	@Transactional(readOnly = true)
	public AgentPostingStatusResponse getPostingStatus(Long agentId) {
		int posted = (int) propertyRepository.countByAgentId(agentId);
		LocalDateTime now = LocalDateTime.now();
		Optional<AgentSubscription> activeSub = agentSubscriptionRepository.findActiveForAgent(agentId, now);

		AgentPostingStatusResponse status = new AgentPostingStatusResponse();
		status.setTotalPosted(posted);
		status.setFreeLimit(FREE_PROPERTY_LIMIT);

		int maxAllowed = FREE_PROPERTY_LIMIT;
		boolean unlimited = false;
		String planName = "Free (2 listings)";

		if (activeSub.isPresent()) {
			AgentSubscription sub = activeSub.get();
			SubscriptionPlan plan = subscriptionPlanRepository.findById(sub.getPlanId()).orElse(null);
			if (plan != null) {
				status.setHasActiveSubscription(true);
				status.setCurrentPlanId(plan.getId());
				status.setCurrentPlanName(plan.getPlanName());
				status.setSubscriptionExpiry(sub.getExpiryDate());
				planName = plan.getPlanName();
				if (plan.isUnlimitedProperties()) {
					unlimited = true;
					maxAllowed = Integer.MAX_VALUE;
				} else {
					maxAllowed = plan.getMaxProperties();
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
			if (!status.isHasActiveSubscription() && posted >= FREE_PROPERTY_LIMIT) {
				status.setRestrictionCode(AgentPostingStatusResponse.CODE_FREE_LIMIT);
				status.setMessage(
						"You have used your 2 free property listings. Upgrade your subscription to post more.");
			} else if (status.isHasActiveSubscription()) {
				status.setRestrictionCode("PLAN_LIMIT_REACHED");
				status.setMessage("Your current plan allows up to " + maxAllowed
						+ " listings. Upgrade to a higher plan for more.");
			} else {
				status.setRestrictionCode(AgentPostingStatusResponse.CODE_FREE_LIMIT);
				status.setMessage("Property posting limit reached. Please upgrade your subscription.");
			}
		} else if (!status.isHasActiveSubscription()) {
			status.setMessage("You have " + Math.max(0, FREE_PROPERTY_LIMIT - posted) + " free listing(s) remaining.");
		} else {
			status.setMessage(unlimited ? "Unlimited listings on your " + planName + " plan."
					: remaining + " listing slot(s) remaining on " + planName + ".");
		}

		return status;
	}

	@Transactional(readOnly = true)
	public void assertCanPost(Long agentId) {
		AgentPostingStatusResponse status = getPostingStatus(agentId);
		if (!status.isCanPost()) {
			throw new AuthException(status.getMessage());
		}
	}

	@Transactional
	public void syncUsageAfterPropertyCreated(Long agentId) {
		agentSubscriptionRepository.findActiveForAgent(agentId, LocalDateTime.now()).ifPresent(sub -> {
			int posted = (int) propertyRepository.countByAgentId(agentId);
			sub.setPropertiesUsed(posted);
			if (sub.getMaxPropertiesAllowed() != null && sub.getMaxPropertiesAllowed() >= 0) {
				sub.setRemainingProperties(Math.max(0, sub.getMaxPropertiesAllowed() - posted));
			}
			agentSubscriptionRepository.save(sub);
		});
	}
}
