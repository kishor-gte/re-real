package com.realestate.main.service.pgsubscription;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.realestate.main.dto.pgsubscription.PgOwnerPostingStatusResponse;
import com.realestate.main.dto.pgsubscription.PgOwnerSubscriptionOverviewResponse;
import com.realestate.main.dto.pgsubscription.PgSubscriptionCheckoutResponse;
import com.realestate.main.dto.pgsubscription.PgSubscriptionPaymentDto;
import com.realestate.main.dto.pgsubscription.PgSubscriptionPaymentVerifyRequest;
import com.realestate.main.dto.pgsubscription.PgSubscriptionPlanDto;
import com.realestate.main.entity.PgOwnerSubscription;
import com.realestate.main.entity.PgSubscriptionPayment;
import com.realestate.main.entity.PgSubscriptionPlan;
import com.realestate.main.entity.enums.AgentSubscriptionStatus;
import com.realestate.main.entity.enums.SubscriptionPaymentStatus;
import com.realestate.main.entity.enums.SubscriptionPlanStatus;
import com.realestate.main.exception.AuthException;
import com.realestate.main.repository.PgOwnerRepository;
import com.realestate.main.repository.PgOwnerSubscriptionRepository;
import com.realestate.main.repository.PgPropertyRepository;
import com.realestate.main.repository.PgSubscriptionPaymentRepository;
import com.realestate.main.repository.PgSubscriptionPlanRepository;
import com.realestate.main.service.booking.RazorpayPaymentService;

@Service
public class PgOwnerSubscriptionService {

	private final PgSubscriptionPlanRepository planRepository;
	private final PgOwnerSubscriptionRepository subscriptionRepository;
	private final PgSubscriptionPaymentRepository paymentRepository;
	private final PgOwnerRepository pgOwnerRepository;
	private final PgPropertyRepository pgPropertyRepository;
	private final RazorpayPaymentService razorpayPaymentService;
	private final PgOwnerPostingLimitService postingLimitService;
	private final PgSubscriptionNotificationService notificationService;

	public PgOwnerSubscriptionService(PgSubscriptionPlanRepository planRepository,
			PgOwnerSubscriptionRepository subscriptionRepository,
			PgSubscriptionPaymentRepository paymentRepository, PgOwnerRepository pgOwnerRepository,
			PgPropertyRepository pgPropertyRepository, RazorpayPaymentService razorpayPaymentService,
			PgOwnerPostingLimitService postingLimitService,
			PgSubscriptionNotificationService notificationService) {
		this.planRepository = planRepository;
		this.subscriptionRepository = subscriptionRepository;
		this.paymentRepository = paymentRepository;
		this.pgOwnerRepository = pgOwnerRepository;
		this.pgPropertyRepository = pgPropertyRepository;
		this.razorpayPaymentService = razorpayPaymentService;
		this.postingLimitService = postingLimitService;
		this.notificationService = notificationService;
	}

	@Transactional(readOnly = true)
	public List<PgSubscriptionPlanDto> listActivePlansForOwner(Long pgOwnerId) {
		PgOwnerPostingStatusResponse posting = postingLimitService.getPostingStatus(pgOwnerId);
		if (!canOwnerUpgradePlans(posting)) {
			return List.of();
		}
		Long currentPlanId = posting.getCurrentPlanId();
		return planRepository.findAllByStatusOrderBySortOrderAsc(SubscriptionPlanStatus.ACTIVE).stream()
				.filter(p -> p.getPrice().compareTo(BigDecimal.ZERO) > 0)
				.filter(p -> !"PG_FREE".equalsIgnoreCase(p.getPlanCode()))
				.filter(p -> currentPlanId == null || !currentPlanId.equals(p.getId()))
				.map(PgSubscriptionPlanDto::from)
				.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public PgOwnerSubscriptionOverviewResponse getOverview(Long pgOwnerId) {
		PgOwnerPostingStatusResponse posting = postingLimitService.getPostingStatus(pgOwnerId);
		PgOwnerSubscriptionOverviewResponse overview = new PgOwnerSubscriptionOverviewResponse();
		overview.setPostingStatus(posting);
		boolean canUpgrade = canOwnerUpgradePlans(posting);
		overview.setCanUpgradePlans(canUpgrade);
		overview.setUpgradeBlockedMessage(upgradeBlockedMessage(posting, canUpgrade));
		overview.setAvailablePlans(canUpgrade ? listActivePlansForOwner(pgOwnerId) : List.of());

		subscriptionRepository.findActiveForPgOwner(pgOwnerId, LocalDateTime.now()).ifPresent(sub -> {
			overview.setActiveSubscriptionId(sub.getId());
			overview.setStartDate(sub.getStartDate());
			overview.setExpiryDate(sub.getExpiryDate());
			overview.setSubscriptionStatusLabel("Active");
			planRepository.findById(sub.getPlanId()).ifPresent(plan -> overview.setActivePlan(PgSubscriptionPlanDto.from(plan)));
		});

		if (overview.getActivePlan() == null) {
			overview.setSubscriptionStatusLabel(posting.isHasActiveSubscription() ? "Active" : "Free Plan");
			planRepository.findByPlanCode("PG_FREE").ifPresent(plan -> overview.setActivePlan(PgSubscriptionPlanDto.from(plan)));
		}

		List<PgSubscriptionPaymentDto> history = paymentRepository.findByPgOwnerIdOrderByCreatedAtDesc(pgOwnerId).stream()
				.map(p -> {
					String name = planRepository.findById(p.getPlanId()).map(PgSubscriptionPlan::getPlanName)
							.orElse("Plan");
					return PgSubscriptionPaymentDto.from(p, name);
				})
				.collect(Collectors.toList());
		overview.setPaymentHistory(history);
		return overview;
	}

	@Transactional
	public PgSubscriptionCheckoutResponse startCheckout(Long pgOwnerId, Long planId) {
		pgOwnerRepository.findById(pgOwnerId).orElseThrow(() -> new AuthException("PG owner not found"));
		PgOwnerPostingStatusResponse posting = postingLimitService.getPostingStatus(pgOwnerId);
		if (!canOwnerUpgradePlans(posting)) {
			throw new AuthException(upgradeBlockedMessage(posting, false));
		}

		PgSubscriptionPlan plan = planRepository.findById(planId)
				.orElseThrow(() -> new AuthException("Subscription plan not found"));
		if (plan.getStatus() != SubscriptionPlanStatus.ACTIVE) {
			throw new AuthException("This plan is not available");
		}
		if (plan.getPrice().compareTo(BigDecimal.ZERO) <= 0 || "PG_FREE".equalsIgnoreCase(plan.getPlanCode())) {
			throw new AuthException("Free plan does not require payment");
		}

		subscriptionRepository.findActiveForPgOwner(pgOwnerId, LocalDateTime.now()).ifPresent(existing -> {
			if (Objects.equals(existing.getPlanId(), planId)) {
				throw new AuthException("You already have an active subscription for this plan");
			}
		});

		PgOwnerSubscription subscription = new PgOwnerSubscription();
		subscription.setPgOwnerId(pgOwnerId);
		subscription.setPlanId(planId);
		subscription.setSubscriptionStatus(AgentSubscriptionStatus.PENDING);
		subscription.setPaymentStatus(SubscriptionPaymentStatus.PENDING);
		int posted = (int) pgPropertyRepository.countByOwnerId(pgOwnerId);
		subscription.setPgUsed(posted);
		int max = plan.isUnlimitedPgListings() ? -1 : plan.getMaxPgListings();
		subscription.setMaxPgAllowed(max);
		subscription.setRemainingPg(max < 0 ? -1 : Math.max(0, max - posted));
		subscription = subscriptionRepository.save(subscription);

		String receipt = "pgsub_" + subscription.getId();
		String orderId = razorpayPaymentService.createOrder(plan.getPrice(), receipt);
		subscription.setRazorpayOrderId(orderId);
		subscriptionRepository.save(subscription);

		PgSubscriptionPayment payment = new PgSubscriptionPayment();
		payment.setSubscriptionId(subscription.getId());
		payment.setPgOwnerId(pgOwnerId);
		payment.setPlanId(planId);
		payment.setAmount(plan.getPrice());
		payment.setRazorpayOrderId(orderId);
		payment.setPaymentStatus(SubscriptionPaymentStatus.PENDING);
		paymentRepository.save(payment);

		PgSubscriptionCheckoutResponse response = new PgSubscriptionCheckoutResponse();
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
	public PgOwnerSubscriptionOverviewResponse verifyPayment(Long pgOwnerId,
			PgSubscriptionPaymentVerifyRequest request) {
		PgOwnerSubscription subscription = subscriptionRepository.findById(request.getSubscriptionId())
				.orElseThrow(() -> new AuthException("Subscription not found"));
		if (!Objects.equals(subscription.getPgOwnerId(), pgOwnerId)) {
			throw new AuthException("Unauthorized");
		}
		if (subscription.getSubscriptionStatus() == AgentSubscriptionStatus.ACTIVE) {
			return getOverview(pgOwnerId);
		}
		if (!Objects.equals(subscription.getRazorpayOrderId(), request.getRazorpayOrderId())) {
			throw new AuthException("Order mismatch");
		}

		if (razorpayPaymentService.isConfigured()) {
			razorpayPaymentService.verifySignature(request.getRazorpayOrderId(), request.getRazorpayPaymentId(),
					request.getRazorpaySignature());
		}

		PgSubscriptionPlan plan = planRepository.findById(subscription.getPlanId())
				.orElseThrow(() -> new AuthException("Plan not found"));

		expireActiveSubscriptions(pgOwnerId, subscription.getId());

		LocalDateTime now = LocalDateTime.now();
		subscription.setStartDate(now);
		subscription.setExpiryDate(now.plusDays(plan.getDurationDays()));
		subscription.setSubscriptionStatus(AgentSubscriptionStatus.ACTIVE);
		subscription.setPaymentStatus(SubscriptionPaymentStatus.SUCCESS);
		int posted = (int) pgPropertyRepository.countByOwnerId(pgOwnerId);
		subscription.setPgUsed(posted);
		int max = plan.isUnlimitedPgListings() ? -1 : plan.getMaxPgListings();
		subscription.setMaxPgAllowed(max);
		subscription.setRemainingPg(max < 0 ? -1 : Math.max(0, max - posted));
		subscriptionRepository.save(subscription);

		PgSubscriptionPayment payment = paymentRepository.findByRazorpayOrderId(request.getRazorpayOrderId())
				.orElseThrow(() -> new AuthException("Payment record not found"));
		payment.setPaymentStatus(SubscriptionPaymentStatus.SUCCESS);
		payment.setRazorpayPaymentId(request.getRazorpayPaymentId());
		payment.setTransactionId(request.getRazorpayPaymentId());
		payment.setPaymentMethod("Razorpay");
		payment.setPaymentDate(now);
		payment.setInvoiceNumber(generateInvoiceNumber(payment.getId()));
		paymentRepository.save(payment);

		notificationService.notifySubscriptionActivated(pgOwnerId, subscription.getId(), plan);
		notificationService.notifyPaymentSuccess(pgOwnerId, subscription.getId(), plan.getPlanName(),
				payment.getInvoiceNumber());

		return getOverview(pgOwnerId);
	}

	@Transactional
	public PgOwnerSubscriptionOverviewResponse activateDemoPayment(Long pgOwnerId, Long subscriptionId) {
		if (razorpayPaymentService.isConfigured()) {
			throw new AuthException("Use Razorpay checkout in production mode");
		}
		PgSubscriptionPaymentVerifyRequest req = new PgSubscriptionPaymentVerifyRequest();
		req.setSubscriptionId(subscriptionId);
		PgOwnerSubscription sub = subscriptionRepository.findById(subscriptionId)
				.orElseThrow(() -> new AuthException("Subscription not found"));
		req.setRazorpayOrderId(sub.getRazorpayOrderId());
		req.setRazorpayPaymentId("demo_pg_sub_" + subscriptionId);
		req.setRazorpaySignature("demo");
		return verifyPayment(pgOwnerId, req);
	}

	private void expireActiveSubscriptions(Long pgOwnerId, Long exceptId) {
		LocalDateTime now = LocalDateTime.now();
		subscriptionRepository.findActiveForPgOwner(pgOwnerId, now).ifPresent(existing -> {
			if (!Objects.equals(existing.getId(), exceptId)) {
				existing.setSubscriptionStatus(AgentSubscriptionStatus.EXPIRED);
				subscriptionRepository.save(existing);
			}
		});
	}

	private static String generateInvoiceNumber(Long paymentId) {
		return "PG-SUB-INV-" + String.format("%06d", paymentId) + "-" + System.currentTimeMillis() % 100000;
	}

	private boolean canOwnerUpgradePlans(PgOwnerPostingStatusResponse posting) {
		if (posting.isCanPost()) {
			return false;
		}
		if (!posting.isHasActiveSubscription()) {
			return true;
		}
		return PgOwnerPostingStatusResponse.CODE_FREE_LIMIT.equals(posting.getRestrictionCode())
				|| "PLAN_LIMIT_REACHED".equals(posting.getRestrictionCode());
	}

	private String upgradeBlockedMessage(PgOwnerPostingStatusResponse posting, boolean canUpgrade) {
		if (canUpgrade) {
			return null;
		}
		if (posting.isHasActiveSubscription()) {
			if (posting.isUnlimited()) {
				String expiry = posting.getSubscriptionExpiry() != null
						? posting.getSubscriptionExpiry().toLocalDate().toString()
						: "expiry";
				return "Your unlimited plan is still active until " + expiry + ".";
			}
			int remaining = posting.getRemainingPosts();
			String slots = remaining < 0 ? "unlimited" : String.valueOf(remaining);
			return "You are on " + posting.getCurrentPlanName() + " with " + slots
					+ " PG slot(s) remaining. Use your current plan before upgrading.";
		}
		int freeLeft = Math.max(0, posting.getFreeLimit() - posting.getTotalPosted());
		if (freeLeft > 0) {
			return "You still have " + freeLeft + " free PG listing(s). Add your PG first, then upgrade when you need more.";
		}
		return posting.getMessage() != null ? posting.getMessage() : "Upgrade is not available yet.";
	}
}
