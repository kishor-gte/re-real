package com.realestate.main.config;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.realestate.main.entity.PgOwner;
import com.realestate.main.entity.PgOwnerSubscription;
import com.realestate.main.entity.PgSubscriptionPlan;
import com.realestate.main.entity.enums.AgentSubscriptionStatus;
import com.realestate.main.repository.PgOwnerRepository;
import com.realestate.main.repository.PgOwnerSubscriptionRepository;
import com.realestate.main.repository.PgSubscriptionPlanRepository;
import com.realestate.main.service.pgsubscription.PgSubscriptionNotificationService;

@Component
public class PgSubscriptionExpiryScheduler {

	private static final Logger log = LoggerFactory.getLogger(PgSubscriptionExpiryScheduler.class);

	private final PgOwnerSubscriptionRepository subscriptionRepository;
	private final PgSubscriptionPlanRepository planRepository;
	private final PgOwnerRepository pgOwnerRepository;
	private final PgSubscriptionNotificationService notificationService;

	public PgSubscriptionExpiryScheduler(PgOwnerSubscriptionRepository subscriptionRepository,
			PgSubscriptionPlanRepository planRepository, PgOwnerRepository pgOwnerRepository,
			PgSubscriptionNotificationService notificationService) {
		this.subscriptionRepository = subscriptionRepository;
		this.planRepository = planRepository;
		this.pgOwnerRepository = pgOwnerRepository;
		this.notificationService = notificationService;
	}

	@Scheduled(cron = "0 15 2 * * *")
	@Transactional
	public void processExpiry() {
		LocalDateTime now = LocalDateTime.now();
		List<PgOwnerSubscription> expired = subscriptionRepository.findExpiringOrExpired(
				AgentSubscriptionStatus.ACTIVE, now);
		for (PgOwnerSubscription sub : expired) {
			sub.setSubscriptionStatus(AgentSubscriptionStatus.EXPIRED);
			subscriptionRepository.save(sub);
			String planName = planRepository.findById(sub.getPlanId()).map(PgSubscriptionPlan::getPlanName)
					.orElse("Subscription");
			notificationService.notifySubscriptionExpired(sub.getPgOwnerId(), planName);
			log.info("Expired PG subscription {} for owner {}", sub.getId(), sub.getPgOwnerId());
		}
	}

	@Scheduled(cron = "0 45 9 * * *")
	@Transactional(readOnly = true)
	public void remindExpiringSoon() {
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime inThreeDays = now.plusDays(3);
		subscriptionRepository.findAll().stream()
				.filter(s -> s.getSubscriptionStatus() == AgentSubscriptionStatus.ACTIVE)
				.filter(s -> s.getExpiryDate() != null && s.getExpiryDate().isAfter(now)
						&& !s.getExpiryDate().isAfter(inThreeDays))
				.forEach(sub -> {
					PgOwner owner = pgOwnerRepository.findById(sub.getPgOwnerId()).orElse(null);
					if (owner == null) {
						return;
					}
					String planName = planRepository.findById(sub.getPlanId()).map(PgSubscriptionPlan::getPlanName)
							.orElse("Plan");
					int days = (int) ChronoUnit.DAYS.between(now.toLocalDate(), sub.getExpiryDate().toLocalDate());
					notificationService.notifySubscriptionExpiringSoon(owner, planName, Math.max(1, days));
				});
	}
}
