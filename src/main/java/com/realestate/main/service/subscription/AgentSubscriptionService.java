package com.realestate.main.service.subscription;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.realestate.main.dto.subscription.AgentPostingStatusResponse;
import com.realestate.main.dto.subscription.AgentSubscriptionOverviewResponse;
import com.realestate.main.dto.subscription.SubscriptionCheckoutResponse;
import com.realestate.main.dto.subscription.SubscriptionPaymentDto;
import com.realestate.main.dto.subscription.SubscriptionPaymentVerifyRequest;
import com.realestate.main.dto.subscription.SubscriptionPlanDto;
import com.realestate.main.entity.AgentSubscription;
import com.realestate.main.entity.SubscriptionPayment;
import com.realestate.main.entity.SubscriptionPlan;
import com.realestate.main.entity.enums.AgentSubscriptionStatus;
import com.realestate.main.entity.enums.SubscriptionPaymentStatus;
import com.realestate.main.entity.enums.SubscriptionPlanStatus;
import com.realestate.main.exception.AuthException;
import com.realestate.main.repository.AgentRepository;
import com.realestate.main.repository.AgentSubscriptionRepository;
import com.realestate.main.repository.PropertyRepository;
import com.realestate.main.repository.SubscriptionPaymentRepository;
import com.realestate.main.repository.SubscriptionPlanRepository;
import com.realestate.main.service.booking.RazorpayPaymentService;

@Service
public class AgentSubscriptionService {

	private final SubscriptionPlanRepository planRepository;
	private final AgentSubscriptionRepository subscriptionRepository;
	private final SubscriptionPaymentRepository paymentRepository;
	private final AgentRepository agentRepository;
	private final PropertyRepository propertyRepository;
	private final RazorpayPaymentService razorpayPaymentService;
	private final AgentPostingLimitService postingLimitService;
	private final SubscriptionNotificationService notificationService;

	public AgentSubscriptionService(SubscriptionPlanRepository planRepository,
			AgentSubscriptionRepository subscriptionRepository,
			SubscriptionPaymentRepository paymentRepository, AgentRepository agentRepository,
			PropertyRepository propertyRepository, RazorpayPaymentService razorpayPaymentService,
			AgentPostingLimitService postingLimitService,
			SubscriptionNotificationService notificationService) {
		this.planRepository = planRepository;
		this.subscriptionRepository = subscriptionRepository;
		this.paymentRepository = paymentRepository;
		this.agentRepository = agentRepository;
		this.propertyRepository = propertyRepository;
		this.razorpayPaymentService = razorpayPaymentService;
		this.postingLimitService = postingLimitService;
		this.notificationService = notificationService;
	}

	@Transactional(readOnly = true)
	public List<SubscriptionPlanDto> listActivePlansForAgent(Long agentId) {
		AgentPostingStatusResponse posting = postingLimitService.getPostingStatus(agentId);
		if (!canAgentUpgradePlans(agentId, posting)) {
			return List.of();
		}
		Long currentPlanId = posting.getCurrentPlanId();
		return planRepository.findAllByStatusOrderBySortOrderAsc(SubscriptionPlanStatus.ACTIVE).stream()
				.filter(p -> p.getPrice().compareTo(BigDecimal.ZERO) > 0)
				.filter(p -> currentPlanId == null || !currentPlanId.equals(p.getId()))
				.map(SubscriptionPlanDto::from)
				.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public AgentSubscriptionOverviewResponse getOverview(Long agentId) {
		AgentPostingStatusResponse posting = postingLimitService.getPostingStatus(agentId);
		AgentSubscriptionOverviewResponse overview = new AgentSubscriptionOverviewResponse();
		overview.setPostingStatus(posting);
		boolean canUpgrade = canAgentUpgradePlans(agentId, posting);
		overview.setCanUpgradePlans(canUpgrade);
		overview.setUpgradeBlockedMessage(upgradeBlockedMessage(posting, canUpgrade));
		overview.setAvailablePlans(canUpgrade ? listActivePlansForAgent(agentId) : List.of());

		subscriptionRepository.findActiveForAgent(agentId, LocalDateTime.now()).ifPresent(sub -> {
			overview.setActiveSubscriptionId(sub.getId());
			overview.setStartDate(sub.getStartDate());
			overview.setExpiryDate(sub.getExpiryDate());
			overview.setSubscriptionStatusLabel("Active");
			planRepository.findById(sub.getPlanId()).ifPresent(plan -> overview.setActivePlan(SubscriptionPlanDto.from(plan)));
		});

		if (overview.getActivePlan() == null) {
			overview.setSubscriptionStatusLabel(posting.isHasActiveSubscription() ? "Active" : "Free Plan");
			planRepository.findByPlanCode("FREE").ifPresent(plan -> overview.setActivePlan(SubscriptionPlanDto.from(plan)));
		}

		List<SubscriptionPaymentDto> history = paymentRepository.findByAgentIdOrderByCreatedAtDesc(agentId).stream()
				.map(p -> {
					String name = planRepository.findById(p.getPlanId()).map(SubscriptionPlan::getPlanName)
							.orElse("Plan");
					return SubscriptionPaymentDto.from(p, name);
				})
				.collect(Collectors.toList());
		overview.setPaymentHistory(history);
		return overview;
	}

	@Transactional
	public SubscriptionCheckoutResponse startCheckout(Long agentId, Long planId) {
		agentRepository.findById(agentId).orElseThrow(() -> new AuthException("Agent not found"));
		AgentPostingStatusResponse posting = postingLimitService.getPostingStatus(agentId);
		if (!canAgentUpgradePlans(agentId, posting)) {
			throw new AuthException(upgradeBlockedMessage(posting, false));
		}

		SubscriptionPlan plan = planRepository.findById(planId)
				.orElseThrow(() -> new AuthException("Subscription plan not found"));
		if (plan.getStatus() != SubscriptionPlanStatus.ACTIVE) {
			throw new AuthException("This plan is not available");
		}
		if (plan.getPrice().compareTo(BigDecimal.ZERO) <= 0 || "FREE".equalsIgnoreCase(plan.getPlanCode())) {
			throw new AuthException("Free plan does not require payment");
		}

		subscriptionRepository.findActiveForAgent(agentId, LocalDateTime.now()).ifPresent(existing -> {
			if (Objects.equals(existing.getPlanId(), planId)) {
				throw new AuthException("You already have an active subscription for this plan");
			}
		});

		AgentSubscription subscription = new AgentSubscription();
		subscription.setAgentId(agentId);
		subscription.setPlanId(planId);
		subscription.setSubscriptionStatus(AgentSubscriptionStatus.PENDING);
		subscription.setPaymentStatus(SubscriptionPaymentStatus.PENDING);
		int posted = (int) propertyRepository.countByAgentId(agentId);
		subscription.setPropertiesUsed(posted);
		int max = plan.isUnlimitedProperties() ? -1 : plan.getMaxProperties();
		subscription.setMaxPropertiesAllowed(max);
		subscription.setRemainingProperties(max < 0 ? -1 : Math.max(0, max - posted));
		subscription = subscriptionRepository.save(subscription);

		String receipt = "sub_" + subscription.getId();
		String orderId = razorpayPaymentService.createOrder(plan.getPrice(), receipt);
		subscription.setRazorpayOrderId(orderId);
		subscriptionRepository.save(subscription);

		SubscriptionPayment payment = new SubscriptionPayment();
		payment.setSubscriptionId(subscription.getId());
		payment.setAgentId(agentId);
		payment.setPlanId(planId);
		payment.setAmount(plan.getPrice());
		payment.setRazorpayOrderId(orderId);
		payment.setPaymentStatus(SubscriptionPaymentStatus.PENDING);
		paymentRepository.save(payment);

		SubscriptionCheckoutResponse response = new SubscriptionCheckoutResponse();
		response.setSubscriptionId(subscription.getId());
		response.setPlanId(planId);
		response.setPlanName(plan.getPlanName());
		response.setAmount(plan.getPrice());
		response.setRazorpayOrderId(orderId);
		response.setRazorpayKeyId(razorpayPaymentService.getKeyId());
		response.setDemoMode(!razorpayPaymentService.isConfigured());
		return response;
	}

	@Transactional
	public AgentSubscriptionOverviewResponse verifyPayment(Long agentId, SubscriptionPaymentVerifyRequest request) {
		AgentSubscription subscription = subscriptionRepository.findById(request.getSubscriptionId())
				.orElseThrow(() -> new AuthException("Subscription not found"));
		if (!Objects.equals(subscription.getAgentId(), agentId)) {
			throw new AuthException("Unauthorized");
		}
		if (subscription.getSubscriptionStatus() == AgentSubscriptionStatus.ACTIVE) {
			return getOverview(agentId);
		}
		if (!Objects.equals(subscription.getRazorpayOrderId(), request.getRazorpayOrderId())) {
			throw new AuthException("Order mismatch");
		}

		razorpayPaymentService.verifySignature(request.getRazorpayOrderId(), request.getRazorpayPaymentId(),
				request.getRazorpaySignature());

		SubscriptionPlan plan = planRepository.findById(subscription.getPlanId())
				.orElseThrow(() -> new AuthException("Plan not found"));

		expireActiveSubscriptions(agentId, subscription.getId());

		LocalDateTime now = LocalDateTime.now();
		subscription.setStartDate(now);
		subscription.setExpiryDate(now.plusDays(plan.getDurationDays()));
		subscription.setSubscriptionStatus(AgentSubscriptionStatus.ACTIVE);
		subscription.setPaymentStatus(SubscriptionPaymentStatus.SUCCESS);
		int posted = (int) propertyRepository.countByAgentId(agentId);
		subscription.setPropertiesUsed(posted);
		int max = plan.isUnlimitedProperties() ? -1 : plan.getMaxProperties();
		subscription.setMaxPropertiesAllowed(max);
		subscription.setRemainingProperties(max < 0 ? -1 : Math.max(0, max - posted));
		subscriptionRepository.save(subscription);

		SubscriptionPayment payment = paymentRepository.findByRazorpayOrderId(request.getRazorpayOrderId())
				.orElseThrow(() -> new AuthException("Payment record not found"));
		payment.setPaymentStatus(SubscriptionPaymentStatus.SUCCESS);
		payment.setRazorpayPaymentId(request.getRazorpayPaymentId());
		payment.setTransactionId(request.getRazorpayPaymentId());
		payment.setPaymentMethod("Razorpay");
		payment.setPaymentDate(now);
		payment.setInvoiceNumber(generateInvoiceNumber(payment.getId()));
		paymentRepository.save(payment);

		notificationService.notifySubscriptionActivated(agentId, subscription.getId(), plan);
		notificationService.notifyPaymentSuccess(agentId, subscription.getId(), plan.getPlanName(),
				payment.getInvoiceNumber());

		return getOverview(agentId);
	}

	@Transactional
	public AgentSubscriptionOverviewResponse activateDemoPayment(Long agentId, Long subscriptionId) {
		if (razorpayPaymentService.isConfigured()) {
			throw new AuthException("Use Razorpay checkout in production mode");
		}
		SubscriptionPaymentVerifyRequest req = new SubscriptionPaymentVerifyRequest();
		req.setSubscriptionId(subscriptionId);
		AgentSubscription sub = subscriptionRepository.findById(subscriptionId)
				.orElseThrow(() -> new AuthException("Subscription not found"));
		req.setRazorpayOrderId(sub.getRazorpayOrderId());
		req.setRazorpayPaymentId("demo_pay_" + subscriptionId);
		req.setRazorpaySignature("demo");
		return verifyPayment(agentId, req);
	}

	private void expireActiveSubscriptions(Long agentId, Long exceptId) {
		LocalDateTime now = LocalDateTime.now();
		subscriptionRepository.findActiveForAgent(agentId, now).ifPresent(existing -> {
			if (!Objects.equals(existing.getId(), exceptId)) {
				existing.setSubscriptionStatus(AgentSubscriptionStatus.EXPIRED);
				subscriptionRepository.save(existing);
			}
		});
	}

	private static String generateInvoiceNumber(Long paymentId) {
		return "SUB-INV-" + String.format("%06d", paymentId) + "-" + System.currentTimeMillis() % 100000;
	}

	/**
	 * Agents may purchase a new plan only when current plan restrictions are exhausted
	 * (posting limit reached on free or paid plan). Unlimited active plans block upgrades until expiry.
	 */
	private boolean canAgentUpgradePlans(Long agentId, AgentPostingStatusResponse posting) {
		if (posting.isCanPost()) {
			return false;
		}
		if (!posting.isHasActiveSubscription()) {
			return true;
		}
		return AgentPostingStatusResponse.CODE_FREE_LIMIT.equals(posting.getRestrictionCode())
				|| "PLAN_LIMIT_REACHED".equals(posting.getRestrictionCode());
	}

	private String upgradeBlockedMessage(AgentPostingStatusResponse posting, boolean canUpgrade) {
		if (canUpgrade) {
			return null;
		}
		if (posting.isHasActiveSubscription()) {
			if (posting.isUnlimited()) {
				String expiry = posting.getSubscriptionExpiry() != null
						? posting.getSubscriptionExpiry().toLocalDate().toString()
						: "expiry";
				return "Your unlimited plan is still active until " + expiry
						+ ". You can change plans after it expires or when you reach your listing limit.";
			}
			int remaining = posting.getRemainingPosts();
			String slots = remaining < 0 ? "unlimited" : String.valueOf(remaining);
			return "You are on " + posting.getCurrentPlanName() + " with " + slots
					+ " listing slot(s) remaining. Use your current plan before upgrading to another.";
		}
		int freeLeft = Math.max(0, posting.getFreeLimit() - posting.getTotalPosted());
		if (freeLeft > 0) {
			return "You still have " + freeLeft + " free listing(s) on the Free plan. Post those properties first, then upgrade when you need more.";
		}
		return posting.getMessage() != null ? posting.getMessage() : "Upgrade is not available yet.";
	}
}
