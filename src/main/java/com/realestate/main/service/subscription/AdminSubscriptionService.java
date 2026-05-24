package com.realestate.main.service.subscription;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.realestate.main.dto.subscription.SubscriptionAnalyticsResponse;
import com.realestate.main.dto.subscription.SubscriptionPaymentDto;
import com.realestate.main.dto.subscription.SubscriptionPlanDto;
import com.realestate.main.dto.subscription.SubscriptionPlanRequest;
import com.realestate.main.entity.Agent;
import com.realestate.main.entity.AgentSubscription;
import com.realestate.main.entity.SubscriptionPayment;
import com.realestate.main.entity.SubscriptionPlan;
import com.realestate.main.entity.enums.AgentSubscriptionStatus;
import com.realestate.main.entity.enums.SubscriptionPaymentStatus;
import com.realestate.main.entity.enums.SubscriptionPlanStatus;
import com.realestate.main.exception.AuthException;
import com.realestate.main.repository.AgentRepository;
import com.realestate.main.repository.AgentSubscriptionRepository;
import com.realestate.main.repository.SubscriptionPaymentRepository;
import com.realestate.main.repository.SubscriptionPlanRepository;

@Service
public class AdminSubscriptionService {

	private final SubscriptionPlanRepository planRepository;
	private final AgentSubscriptionRepository subscriptionRepository;
	private final SubscriptionPaymentRepository paymentRepository;
	private final AgentRepository agentRepository;

	public AdminSubscriptionService(SubscriptionPlanRepository planRepository,
			AgentSubscriptionRepository subscriptionRepository,
			SubscriptionPaymentRepository paymentRepository, AgentRepository agentRepository) {
		this.planRepository = planRepository;
		this.subscriptionRepository = subscriptionRepository;
		this.paymentRepository = paymentRepository;
		this.agentRepository = agentRepository;
	}

	@Transactional(readOnly = true)
	public List<SubscriptionPlanDto> listAllPlans() {
		return planRepository.findAllByOrderBySortOrderAsc().stream().map(SubscriptionPlanDto::from)
				.collect(Collectors.toList());
	}

	@Transactional
	public SubscriptionPlanDto createPlan(SubscriptionPlanRequest request) {
		if (planRepository.findByPlanCode(request.getPlanCode().trim().toUpperCase()).isPresent()) {
			throw new AuthException("Plan code already exists");
		}
		SubscriptionPlan plan = mapRequest(new SubscriptionPlan(), request);
		plan.setPlanCode(request.getPlanCode().trim().toUpperCase());
		plan.setStatus(SubscriptionPlanStatus.ACTIVE);
		return SubscriptionPlanDto.from(planRepository.save(plan));
	}

	@Transactional
	public SubscriptionPlanDto updatePlan(Long planId, SubscriptionPlanRequest request) {
		SubscriptionPlan plan = planRepository.findById(planId)
				.orElseThrow(() -> new AuthException("Plan not found"));
		mapRequest(plan, request);
		return SubscriptionPlanDto.from(planRepository.save(plan));
	}

	@Transactional
	public SubscriptionPlanDto setPlanStatus(Long planId, SubscriptionPlanStatus status) {
		SubscriptionPlan plan = planRepository.findById(planId)
				.orElseThrow(() -> new AuthException("Plan not found"));
		plan.setStatus(status);
		return SubscriptionPlanDto.from(planRepository.save(plan));
	}

	@Transactional(readOnly = true)
	public List<AgentSubscriptionAdminView> listAgentSubscriptions() {
		return subscriptionRepository.findAll().stream().map(this::toAdminView).collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public List<AgentSubscriptionAdminView> listActiveSubscriptions() {
		LocalDateTime now = LocalDateTime.now();
		return subscriptionRepository.findAll().stream()
				.filter(s -> s.getSubscriptionStatus() == AgentSubscriptionStatus.ACTIVE && s.getExpiryDate() != null
						&& s.getExpiryDate().isAfter(now))
				.map(this::toAdminView)
				.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public List<SubscriptionPaymentDto> listPayments() {
		return paymentRepository.findAllByOrderByCreatedAtDesc().stream().map(p -> {
			String planName = planRepository.findById(p.getPlanId()).map(SubscriptionPlan::getPlanName).orElse("—");
			return SubscriptionPaymentDto.from(p, planName);
		}).collect(Collectors.toList());
	}

	@Transactional
	public SubscriptionPaymentDto setPaymentStatus(Long paymentId, SubscriptionPaymentStatus status) {
		SubscriptionPayment payment = paymentRepository.findById(paymentId)
				.orElseThrow(() -> new AuthException("Payment not found"));
		payment.setPaymentStatus(status);
		if (status == SubscriptionPaymentStatus.SUCCESS && payment.getPaymentDate() == null) {
			payment.setPaymentDate(LocalDateTime.now());
		}
		paymentRepository.save(payment);

		if (status == SubscriptionPaymentStatus.SUCCESS) {
			AgentSubscription sub = subscriptionRepository.findById(payment.getSubscriptionId()).orElse(null);
			if (sub != null && sub.getSubscriptionStatus() != AgentSubscriptionStatus.ACTIVE) {
				SubscriptionPlan plan = planRepository.findById(sub.getPlanId()).orElse(null);
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
		String planName = planRepository.findById(payment.getPlanId()).map(SubscriptionPlan::getPlanName).orElse("—");
		return SubscriptionPaymentDto.from(payment, planName);
	}

	@Transactional
	public AgentSubscriptionAdminView setSubscriptionStatus(Long subscriptionId, AgentSubscriptionStatus status) {
		AgentSubscription sub = subscriptionRepository.findById(subscriptionId)
				.orElseThrow(() -> new AuthException("Subscription not found"));
		sub.setSubscriptionStatus(status);
		return toAdminView(subscriptionRepository.save(sub));
	}

	@Transactional(readOnly = true)
	public SubscriptionAnalyticsResponse analytics() {
		SubscriptionAnalyticsResponse a = new SubscriptionAnalyticsResponse();
		LocalDateTime now = LocalDateTime.now();
		a.setActiveSubscriptions(subscriptionRepository.findAll().stream()
				.filter(s -> s.getSubscriptionStatus() == AgentSubscriptionStatus.ACTIVE
						&& s.getExpiryDate() != null && s.getExpiryDate().isAfter(now))
				.count());
		a.setExpiredSubscriptions(
				subscriptionRepository.countBySubscriptionStatus(AgentSubscriptionStatus.EXPIRED));
		a.setSuccessfulPayments(paymentRepository.countByPaymentStatus(SubscriptionPaymentStatus.SUCCESS));
		a.setPendingPayments(paymentRepository.countByPaymentStatus(SubscriptionPaymentStatus.PENDING));
		BigDecimal revenue = paymentRepository.findAll().stream()
				.filter(p -> p.getPaymentStatus() == SubscriptionPaymentStatus.SUCCESS)
				.map(SubscriptionPayment::getAmount)
				.reduce(BigDecimal.ZERO, BigDecimal::add);
		a.setTotalRevenue(revenue);
		a.setActivePlans(planRepository.findAllByStatusOrderBySortOrderAsc(SubscriptionPlanStatus.ACTIVE).size());
		a.setInactivePlans(planRepository.findAll().stream()
				.filter(p -> p.getStatus() != SubscriptionPlanStatus.ACTIVE).count());
		return a;
	}

	private SubscriptionPlan mapRequest(SubscriptionPlan plan, SubscriptionPlanRequest request) {
		plan.setPlanName(request.getPlanName().trim());
		plan.setPrice(request.getPrice());
		plan.setDurationDays(request.getDurationDays());
		plan.setMaxProperties(request.getMaxProperties());
		plan.setFeaturedListings(request.getFeaturedListings());
		plan.setPropertyImagesLimit(request.getPropertyImagesLimit());
		plan.setPremiumBadge(request.isPremiumBadge());
		plan.setLeadsLimit(request.getLeadsLimit());
		plan.setWhatsappLeadAccess(request.isWhatsappLeadAccess());
		plan.setAnalyticsAccess(request.isAnalyticsAccess());
		plan.setDescription(request.getDescription());
		plan.setRecommended(request.isRecommended());
		plan.setSortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0);
		return plan;
	}

	private AgentSubscriptionAdminView toAdminView(AgentSubscription sub) {
		AgentSubscriptionAdminView v = new AgentSubscriptionAdminView();
		v.setSubscriptionId(sub.getId());
		v.setAgentId(sub.getAgentId());
		v.setPlanId(sub.getPlanId());
		v.setStartDate(sub.getStartDate());
		v.setExpiryDate(sub.getExpiryDate());
		v.setPropertiesUsed(sub.getPropertiesUsed());
		v.setRemainingProperties(sub.getRemainingProperties());
		v.setSubscriptionStatus(sub.getSubscriptionStatus());
		v.setPaymentStatus(sub.getPaymentStatus());
		agentRepository.findById(sub.getAgentId()).ifPresent(a -> {
			v.setAgentName(a.getFullName());
			v.setAgentEmail(a.getEmail());
			v.setAgentCode(a.getAgentCode());
		});
		planRepository.findById(sub.getPlanId()).ifPresent(p -> v.setPlanName(p.getPlanName()));
		return v;
	}

	public static class AgentSubscriptionAdminView {
		private Long subscriptionId;
		private Long agentId;
		private String agentName;
		private String agentEmail;
		private String agentCode;
		private Long planId;
		private String planName;
		private LocalDateTime startDate;
		private LocalDateTime expiryDate;
		private Integer propertiesUsed;
		private Integer remainingProperties;
		private AgentSubscriptionStatus subscriptionStatus;
		private SubscriptionPaymentStatus paymentStatus;

		public Long getSubscriptionId() {
			return subscriptionId;
		}

		public void setSubscriptionId(Long subscriptionId) {
			this.subscriptionId = subscriptionId;
		}

		public Long getAgentId() {
			return agentId;
		}

		public void setAgentId(Long agentId) {
			this.agentId = agentId;
		}

		public String getAgentName() {
			return agentName;
		}

		public void setAgentName(String agentName) {
			this.agentName = agentName;
		}

		public String getAgentEmail() {
			return agentEmail;
		}

		public void setAgentEmail(String agentEmail) {
			this.agentEmail = agentEmail;
		}

		public String getAgentCode() {
			return agentCode;
		}

		public void setAgentCode(String agentCode) {
			this.agentCode = agentCode;
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

		public Integer getPropertiesUsed() {
			return propertiesUsed;
		}

		public void setPropertiesUsed(Integer propertiesUsed) {
			this.propertiesUsed = propertiesUsed;
		}

		public Integer getRemainingProperties() {
			return remainingProperties;
		}

		public void setRemainingProperties(Integer remainingProperties) {
			this.remainingProperties = remainingProperties;
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
