package com.realestate.main.config;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.realestate.main.entity.Agent;
import com.realestate.main.entity.AgentSubscription;
import com.realestate.main.entity.SubscriptionPlan;
import com.realestate.main.entity.enums.AgentSubscriptionStatus;
import com.realestate.main.repository.AgentRepository;
import com.realestate.main.repository.AgentSubscriptionRepository;
import com.realestate.main.repository.SubscriptionPlanRepository;
import com.realestate.main.service.subscription.SubscriptionNotificationService;

@Component
public class SubscriptionExpiryScheduler {

	private static final Logger log = LoggerFactory.getLogger(SubscriptionExpiryScheduler.class);

	private final AgentSubscriptionRepository subscriptionRepository;
	private final SubscriptionPlanRepository planRepository;
	private final AgentRepository agentRepository;
	private final SubscriptionNotificationService notificationService;

	public SubscriptionExpiryScheduler(AgentSubscriptionRepository subscriptionRepository,
			SubscriptionPlanRepository planRepository, AgentRepository agentRepository,
			SubscriptionNotificationService notificationService) {
		this.subscriptionRepository = subscriptionRepository;
		this.planRepository = planRepository;
		this.agentRepository = agentRepository;
		this.notificationService = notificationService;
	}

	@Scheduled(cron = "0 0 2 * * *")
	@Transactional
	public void processExpiry() {
		LocalDateTime now = LocalDateTime.now();
		List<AgentSubscription> expired = subscriptionRepository.findExpiringOrExpired(
				AgentSubscriptionStatus.ACTIVE, now);
		for (AgentSubscription sub : expired) {
			sub.setSubscriptionStatus(AgentSubscriptionStatus.EXPIRED);
			subscriptionRepository.save(sub);
			String planName = planRepository.findById(sub.getPlanId()).map(SubscriptionPlan::getPlanName)
					.orElse("Subscription");
			notificationService.notifySubscriptionExpired(sub.getAgentId(), planName);
			log.info("Expired subscription {} for agent {}", sub.getId(), sub.getAgentId());
		}
	}

	@Scheduled(cron = "0 30 9 * * *")
	@Transactional(readOnly = true)
	public void remindExpiringSoon() {
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime inThreeDays = now.plusDays(3);
		subscriptionRepository.findAll().stream()
				.filter(s -> s.getSubscriptionStatus() == AgentSubscriptionStatus.ACTIVE)
				.filter(s -> s.getExpiryDate() != null && s.getExpiryDate().isAfter(now)
						&& !s.getExpiryDate().isAfter(inThreeDays))
				.forEach(sub -> {
					Agent agent = agentRepository.findById(sub.getAgentId()).orElse(null);
					if (agent == null) {
						return;
					}
					String planName = planRepository.findById(sub.getPlanId()).map(SubscriptionPlan::getPlanName)
							.orElse("Plan");
					int days = (int) ChronoUnit.DAYS.between(now.toLocalDate(), sub.getExpiryDate().toLocalDate());
					notificationService.notifySubscriptionExpiringSoon(agent, planName, Math.max(1, days));
				});
	}
}
