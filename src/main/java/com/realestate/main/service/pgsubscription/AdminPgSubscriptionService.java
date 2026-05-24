package com.realestate.main.service.pgsubscription;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.realestate.main.dto.pgsubscription.PgSubscriptionAnalyticsResponse;
import com.realestate.main.dto.pgsubscription.PgSubscriptionPaymentDto;
import com.realestate.main.dto.pgsubscription.PgSubscriptionPlanDto;
import com.realestate.main.dto.pgsubscription.PgSubscriptionPlanRequest;
import com.realestate.main.entity.PgOwner;
import com.realestate.main.entity.PgOwnerSubscription;
import com.realestate.main.entity.PgSubscriptionPayment;
import com.realestate.main.entity.PgSubscriptionPlan;
import com.realestate.main.entity.enums.AgentSubscriptionStatus;
import com.realestate.main.entity.enums.SubscriptionPaymentStatus;
import com.realestate.main.entity.enums.SubscriptionPlanStatus;
import com.realestate.main.exception.AuthException;
import com.realestate.main.repository.PgOwnerRepository;
import com.realestate.main.repository.PgOwnerSubscriptionRepository;
import com.realestate.main.repository.PgSubscriptionPaymentRepository;
import com.realestate.main.repository.PgSubscriptionPlanRepository;

@Service
public class AdminPgSubscriptionService {

	private final PgSubscriptionPlanRepository planRepository;
	private final PgOwnerSubscriptionRepository subscriptionRepository;
	private final PgSubscriptionPaymentRepository paymentRepository;
	private final PgOwnerRepository pgOwnerRepository;

	public AdminPgSubscriptionService(PgSubscriptionPlanRepository planRepository,
			PgOwnerSubscriptionRepository subscriptionRepository,
			PgSubscriptionPaymentRepository paymentRepository, PgOwnerRepository pgOwnerRepository) {
		this.planRepository = planRepository;
		this.subscriptionRepository = subscriptionRepository;
		this.paymentRepository = paymentRepository;
		this.pgOwnerRepository = pgOwnerRepository;
	}

	@Transactional(readOnly = true)
	public List<PgSubscriptionPlanDto> listAllPlans() {
		return planRepository.findAllByOrderBySortOrderAsc().stream().map(PgSubscriptionPlanDto::from)
				.collect(Collectors.toList());
	}

	@Transactional
	public PgSubscriptionPlanDto createPlan(PgSubscriptionPlanRequest request) {
		if (planRepository.findByPlanCode(request.getPlanCode().trim().toUpperCase()).isPresent()) {
			throw new AuthException("Plan code already exists");
		}
		PgSubscriptionPlan plan = mapRequest(new PgSubscriptionPlan(), request);
		plan.setPlanCode(request.getPlanCode().trim().toUpperCase());
		plan.setStatus(SubscriptionPlanStatus.ACTIVE);
		return PgSubscriptionPlanDto.from(planRepository.save(plan));
	}

	@Transactional
	public PgSubscriptionPlanDto updatePlan(Long planId, PgSubscriptionPlanRequest request) {
		PgSubscriptionPlan plan = planRepository.findById(planId)
				.orElseThrow(() -> new AuthException("Plan not found"));
		mapRequest(plan, request);
		return PgSubscriptionPlanDto.from(planRepository.save(plan));
	}

	@Transactional
	public PgSubscriptionPlanDto setPlanStatus(Long planId, SubscriptionPlanStatus status) {
		PgSubscriptionPlan plan = planRepository.findById(planId)
				.orElseThrow(() -> new AuthException("Plan not found"));
		plan.setStatus(status);
		return PgSubscriptionPlanDto.from(planRepository.save(plan));
	}

	@Transactional(readOnly = true)
	public List<PgOwnerSubscriptionAdminView> listOwnerSubscriptions() {
		return subscriptionRepository.findAll().stream().map(this::toAdminView).collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public List<PgOwnerSubscriptionAdminView> listActiveSubscriptions() {
		LocalDateTime now = LocalDateTime.now();
		return subscriptionRepository.findAll().stream()
				.filter(s -> s.getSubscriptionStatus() == AgentSubscriptionStatus.ACTIVE
						&& s.getExpiryDate() != null && s.getExpiryDate().isAfter(now))
				.map(this::toAdminView)
				.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public List<PgSubscriptionPaymentDto> listPayments() {
		return paymentRepository.findAllByOrderByCreatedAtDesc().stream().map(p -> {
			String planName = planRepository.findById(p.getPlanId()).map(PgSubscriptionPlan::getPlanName).orElse("—");
			return PgSubscriptionPaymentDto.from(p, planName);
		}).collect(Collectors.toList());
	}

	@Transactional
	public PgSubscriptionPaymentDto setPaymentStatus(Long paymentId, SubscriptionPaymentStatus status) {
		PgSubscriptionPayment payment = paymentRepository.findById(paymentId)
				.orElseThrow(() -> new AuthException("Payment not found"));
		payment.setPaymentStatus(status);
		if (status == SubscriptionPaymentStatus.SUCCESS && payment.getPaymentDate() == null) {
			payment.setPaymentDate(LocalDateTime.now());
		}
		paymentRepository.save(payment);

		if (status == SubscriptionPaymentStatus.SUCCESS) {
			PgOwnerSubscription sub = subscriptionRepository.findById(payment.getSubscriptionId()).orElse(null);
			if (sub != null && sub.getSubscriptionStatus() != AgentSubscriptionStatus.ACTIVE) {
				PgSubscriptionPlan plan = planRepository.findById(sub.getPlanId()).orElse(null);
				if (plan != null) {
					LocalDateTime now = LocalDateTime.now();
					sub.setStartDate(now);
					sub.setExpiryDate(now.plusDays(plan.getDurationDays()));
					sub.setSubscriptionStatus(AgentSubscriptionStatus.ACTIVE);
					sub.setPaymentStatus(SubscriptionPaymentStatus.SUCCESS);
					subscriptionRepository.save(sub);
				}
			}
		}
		String planName = planRepository.findById(payment.getPlanId()).map(PgSubscriptionPlan::getPlanName).orElse("—");
		return PgSubscriptionPaymentDto.from(payment, planName);
	}

	@Transactional
	public PgOwnerSubscriptionAdminView setSubscriptionStatus(Long subscriptionId, AgentSubscriptionStatus status) {
		PgOwnerSubscription sub = subscriptionRepository.findById(subscriptionId)
				.orElseThrow(() -> new AuthException("Subscription not found"));
		sub.setSubscriptionStatus(status);
		return toAdminView(subscriptionRepository.save(sub));
	}

	@Transactional(readOnly = true)
	public PgSubscriptionAnalyticsResponse analytics() {
		PgSubscriptionAnalyticsResponse a = new PgSubscriptionAnalyticsResponse();
		LocalDateTime now = LocalDateTime.now();
		a.setActiveSubscriptions(subscriptionRepository.findAll().stream()
				.filter(s -> s.getSubscriptionStatus() == AgentSubscriptionStatus.ACTIVE
						&& s.getExpiryDate() != null && s.getExpiryDate().isAfter(now))
				.count());
		a.setExpiredSubscriptions(subscriptionRepository.countBySubscriptionStatus(AgentSubscriptionStatus.EXPIRED));
		a.setSuccessfulPayments(paymentRepository.countByPaymentStatus(SubscriptionPaymentStatus.SUCCESS));
		a.setPendingPayments(paymentRepository.countByPaymentStatus(SubscriptionPaymentStatus.PENDING));
		BigDecimal revenue = paymentRepository.findAll().stream()
				.filter(p -> p.getPaymentStatus() == SubscriptionPaymentStatus.SUCCESS)
				.map(PgSubscriptionPayment::getAmount)
				.reduce(BigDecimal.ZERO, BigDecimal::add);
		a.setTotalRevenue(revenue);
		a.setActivePlans(planRepository.findAllByStatusOrderBySortOrderAsc(SubscriptionPlanStatus.ACTIVE).size());
		a.setInactivePlans(planRepository.findAll().stream()
				.filter(p -> p.getStatus() != SubscriptionPlanStatus.ACTIVE).count());
		return a;
	}

	private PgSubscriptionPlan mapRequest(PgSubscriptionPlan plan, PgSubscriptionPlanRequest request) {
		plan.setPlanName(request.getPlanName().trim());
		plan.setPrice(request.getPrice());
		plan.setDurationDays(request.getDurationDays());
		plan.setMaxPgListings(request.getMaxPgListings());
		plan.setMaxRoomListings(request.getMaxRoomListings());
		plan.setFeaturedPgCount(request.getFeaturedPgCount());
		plan.setBedManagementAccess(request.isBedManagementAccess());
		plan.setTenantAnalyticsAccess(request.isTenantAnalyticsAccess());
		plan.setPremiumBadge(request.isPremiumBadge());
		plan.setPrioritySupport(request.isPrioritySupport());
		plan.setDescription(request.getDescription());
		plan.setRecommended(request.isRecommended());
		plan.setSortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0);
		return plan;
	}

	private PgOwnerSubscriptionAdminView toAdminView(PgOwnerSubscription sub) {
		PgOwnerSubscriptionAdminView v = new PgOwnerSubscriptionAdminView();
		v.setSubscriptionId(sub.getId());
		v.setPgOwnerId(sub.getPgOwnerId());
		v.setPlanId(sub.getPlanId());
		v.setStartDate(sub.getStartDate());
		v.setExpiryDate(sub.getExpiryDate());
		v.setPgUsed(sub.getPgUsed());
		v.setRemainingPg(sub.getRemainingPg());
		v.setSubscriptionStatus(sub.getSubscriptionStatus());
		v.setPaymentStatus(sub.getPaymentStatus());
		pgOwnerRepository.findById(sub.getPgOwnerId()).ifPresent(o -> {
			v.setPgOwnerName(o.getFullName());
			v.setPgOwnerEmail(o.getEmail());
			v.setPgOwnerCode(o.getPgOwnerCode());
		});
		planRepository.findById(sub.getPlanId()).ifPresent(p -> v.setPlanName(p.getPlanName()));
		return v;
	}

	public static class PgOwnerSubscriptionAdminView {
		private Long subscriptionId;
		private Long pgOwnerId;
		private String pgOwnerName;
		private String pgOwnerEmail;
		private String pgOwnerCode;
		private Long planId;
		private String planName;
		private LocalDateTime startDate;
		private LocalDateTime expiryDate;
		private Integer pgUsed;
		private Integer remainingPg;
		private AgentSubscriptionStatus subscriptionStatus;
		private SubscriptionPaymentStatus paymentStatus;

		public Long getSubscriptionId() {
			return subscriptionId;
		}

		public void setSubscriptionId(Long subscriptionId) {
			this.subscriptionId = subscriptionId;
		}

		public Long getPgOwnerId() {
			return pgOwnerId;
		}

		public void setPgOwnerId(Long pgOwnerId) {
			this.pgOwnerId = pgOwnerId;
		}

		public String getPgOwnerName() {
			return pgOwnerName;
		}

		public void setPgOwnerName(String pgOwnerName) {
			this.pgOwnerName = pgOwnerName;
		}

		public String getPgOwnerEmail() {
			return pgOwnerEmail;
		}

		public void setPgOwnerEmail(String pgOwnerEmail) {
			this.pgOwnerEmail = pgOwnerEmail;
		}

		public String getPgOwnerCode() {
			return pgOwnerCode;
		}

		public void setPgOwnerCode(String pgOwnerCode) {
			this.pgOwnerCode = pgOwnerCode;
		}

		public Long getPlanId() {
			return planId;
		}

		public void setPlanId(Long planId) {
			this.planId = planId;
		}

		public String getPlanName() {
			return planName;
		}

		public void setPlanName(String planName) {
			this.planName = planName;
		}

		public LocalDateTime getStartDate() {
			return startDate;
		}

		public void setStartDate(LocalDateTime startDate) {
			this.startDate = startDate;
		}

		public LocalDateTime getExpiryDate() {
			return expiryDate;
		}

		public void setExpiryDate(LocalDateTime expiryDate) {
			this.expiryDate = expiryDate;
		}

		public Integer getPgUsed() {
			return pgUsed;
		}

		public void setPgUsed(Integer pgUsed) {
			this.pgUsed = pgUsed;
		}

		public Integer getRemainingPg() {
			return remainingPg;
		}

		public void setRemainingPg(Integer remainingPg) {
			this.remainingPg = remainingPg;
		}

		public AgentSubscriptionStatus getSubscriptionStatus() {
			return subscriptionStatus;
		}

		public void setSubscriptionStatus(AgentSubscriptionStatus subscriptionStatus) {
			this.subscriptionStatus = subscriptionStatus;
		}

		public SubscriptionPaymentStatus getPaymentStatus() {
			return paymentStatus;
		}

		public void setPaymentStatus(SubscriptionPaymentStatus paymentStatus) {
			this.paymentStatus = paymentStatus;
		}
	}
}
