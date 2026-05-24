package com.realestate.main.service.subscription;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.realestate.main.entity.Agent;
import com.realestate.main.entity.SubscriptionNotification;
import com.realestate.main.entity.SubscriptionPlan;
import com.realestate.main.repository.AgentRepository;
import com.realestate.main.repository.SubscriptionNotificationRepository;
import com.realestate.main.service.EmailService;

@Service
public class SubscriptionNotificationService {

	private static final Logger log = LoggerFactory.getLogger(SubscriptionNotificationService.class);

	private final SubscriptionNotificationRepository notificationRepository;
	private final AgentRepository agentRepository;
	private final EmailService emailService;

	public SubscriptionNotificationService(SubscriptionNotificationRepository notificationRepository,
			AgentRepository agentRepository, EmailService emailService) {
		this.notificationRepository = notificationRepository;
		this.agentRepository = agentRepository;
		this.emailService = emailService;
	}

	@Async
	@Transactional
	public void notifySubscriptionActivated(Long agentId, Long subscriptionId, SubscriptionPlan plan) {
		String message = "Your " + plan.getPlanName() + " subscription is now active.";
		save(agentId, subscriptionId, "SUBSCRIPTION_ACTIVATED", "EMAIL", message);
		save(agentId, subscriptionId, "SUBSCRIPTION_ACTIVATED", "DASHBOARD", message);
		agentRepository.findById(agentId).ifPresent(agent -> {
			try {
				emailService.sendSubscriptionActivatedEmail(agent.getEmail(), agent.getFullName(), plan.getPlanName());
			} catch (Exception e) {
				log.warn("Subscription activation email failed for agent {}", agentId, e);
			}
		});
	}

	@Async
	@Transactional
	public void notifyPaymentSuccess(Long agentId, Long subscriptionId, String planName, String invoiceNumber) {
		String message = "Payment received for " + planName + ". Invoice: " + invoiceNumber;
		save(agentId, subscriptionId, "PAYMENT_SUCCESS", "EMAIL", message);
		save(agentId, subscriptionId, "PAYMENT_SUCCESS", "DASHBOARD", message);
		agentRepository.findById(agentId).ifPresent(agent -> {
			try {
				emailService.sendSubscriptionPaymentEmail(agent.getEmail(), agent.getFullName(), planName,
						invoiceNumber);
			} catch (Exception e) {
				log.warn("Payment email failed for agent {}", agentId, e);
			}
		});
	}

	@Async
	@Transactional
	public void notifyPropertyLimitReached(Long agentId) {
		String message = "You have reached your free property posting limit. Upgrade to continue listing.";
		save(agentId, null, "PROPERTY_LIMIT_REACHED", "DASHBOARD", message);
	}

	@Async
	@Transactional
	public void notifySubscriptionExpiringSoon(Agent agent, String planName, int daysLeft) {
		String message = "Your " + planName + " subscription expires in " + daysLeft + " day(s).";
		save(agent.getId(), null, "SUBSCRIPTION_EXPIRING", "EMAIL", message);
		save(agent.getId(), null, "SUBSCRIPTION_EXPIRING", "DASHBOARD", message);
		try {
			emailService.sendSubscriptionExpiringEmail(agent.getEmail(), agent.getFullName(), planName, daysLeft);
		} catch (Exception e) {
			log.warn("Expiry reminder email failed", e);
		}
	}

	@Async
	@Transactional
	public void notifySubscriptionExpired(Long agentId, String planName) {
		String message = "Your " + planName + " subscription has expired. You are back on the free plan (2 listings).";
		save(agentId, null, "SUBSCRIPTION_EXPIRED", "DASHBOARD", message);
		agentRepository.findById(agentId).ifPresent(agent -> {
			try {
				emailService.sendSubscriptionExpiredEmail(agent.getEmail(), agent.getFullName(), planName);
			} catch (Exception e) {
				log.warn("Expiry email failed", e);
			}
		});
	}

	private void save(Long agentId, Long subscriptionId, String type, String channel, String message) {
		SubscriptionNotification n = new SubscriptionNotification();
		n.setAgentId(agentId);
		n.setSubscriptionId(subscriptionId);
		n.setNotificationType(type);
		n.setChannel(channel);
		n.setStatus("SENT");
		n.setMessage(message);
		notificationRepository.save(n);
	}
}
