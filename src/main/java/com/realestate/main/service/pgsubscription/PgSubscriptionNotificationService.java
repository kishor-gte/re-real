package com.realestate.main.service.pgsubscription;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.realestate.main.entity.PgOwner;
import com.realestate.main.entity.PgSubscriptionNotification;
import com.realestate.main.entity.PgSubscriptionPlan;
import com.realestate.main.repository.PgOwnerRepository;
import com.realestate.main.repository.PgSubscriptionNotificationRepository;
import com.realestate.main.service.EmailService;

@Service
public class PgSubscriptionNotificationService {

	private final PgSubscriptionNotificationRepository notificationRepository;
	private final PgOwnerRepository pgOwnerRepository;
	private final EmailService emailService;

	public PgSubscriptionNotificationService(PgSubscriptionNotificationRepository notificationRepository,
			PgOwnerRepository pgOwnerRepository, EmailService emailService) {
		this.notificationRepository = notificationRepository;
		this.pgOwnerRepository = pgOwnerRepository;
		this.emailService = emailService;
	}

	@Transactional
	public void notifySubscriptionActivated(Long pgOwnerId, Long subscriptionId, PgSubscriptionPlan plan) {
		save(pgOwnerId, "SUBSCRIPTION_ACTIVATED", "Subscription activated",
				"Your " + plan.getPlanName() + " plan is now active.");
		pgOwnerRepository.findById(pgOwnerId).ifPresent(owner -> emailService
				.sendPgSubscriptionActivatedEmail(owner.getEmail(), owner.getFullName(), plan.getPlanName()));
	}

	@Transactional
	public void notifyPaymentSuccess(Long pgOwnerId, Long subscriptionId, String planName, String invoiceNumber) {
		save(pgOwnerId, "PAYMENT_SUCCESS", "Payment successful",
				"Payment for " + planName + " was successful. Invoice: " + invoiceNumber);
		pgOwnerRepository.findById(pgOwnerId).ifPresent(owner -> emailService
				.sendPgSubscriptionPaymentEmail(owner.getEmail(), owner.getFullName(), planName, invoiceNumber));
	}

	@Transactional
	public void notifySubscriptionExpiringSoon(PgOwner owner, String planName, int daysLeft) {
		save(owner.getId(), "SUBSCRIPTION_EXPIRING", "Subscription expiring soon",
				"Your " + planName + " plan expires in " + daysLeft + " day(s).");
		emailService.sendPgSubscriptionExpiringEmail(owner.getEmail(), owner.getFullName(), planName, daysLeft);
	}

	@Transactional
	public void notifySubscriptionExpired(Long pgOwnerId, String planName) {
		save(pgOwnerId, "SUBSCRIPTION_EXPIRED", "Subscription expired",
				"Your " + planName + " subscription has expired. Upgrade to continue adding PGs.");
		pgOwnerRepository.findById(pgOwnerId).ifPresent(owner -> emailService
				.sendPgSubscriptionExpiredEmail(owner.getEmail(), owner.getFullName(), planName));
	}

	@Transactional
	public void notifyListingLimitReached(Long pgOwnerId) {
		save(pgOwnerId, "LISTING_LIMIT", "Free PG listing limit reached",
				"You have used your free PG listing. Upgrade to add more properties.");
	}

	private void save(Long pgOwnerId, String type, String title, String message) {
		PgSubscriptionNotification n = new PgSubscriptionNotification();
		n.setPgOwnerId(pgOwnerId);
		n.setNotificationType(type);
		n.setTitle(title);
		n.setMessage(message);
		notificationRepository.save(n);
	}
}
