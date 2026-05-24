package com.realestate.main.service.booking;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.realestate.main.entity.Agent;
import com.realestate.main.entity.BookingNotification;
import com.realestate.main.entity.Property;
import com.realestate.main.entity.PropertyBooking;
import com.realestate.main.entity.User;
import com.realestate.main.entity.enums.BookingNotificationType;
import com.realestate.main.entity.enums.NotificationRecipientType;
import com.realestate.main.repository.BookingNotificationRepository;
import com.realestate.main.service.EmailService;

@Service
public class BookingNotificationService {

	private static final Logger log = LoggerFactory.getLogger(BookingNotificationService.class);

	private final BookingNotificationRepository notificationRepository;
	private final EmailService emailService;

	@Value("${app.name:EstateVault}")
	private String appName;

	@Value("${app.support-email:support@estatevault.com}")
	private String supportEmail;

	public BookingNotificationService(BookingNotificationRepository notificationRepository,
			EmailService emailService) {
		this.notificationRepository = notificationRepository;
		this.emailService = emailService;
	}

	@Async
	@Transactional
	public void sendBookingConfirmations(PropertyBooking booking, Property property, User user, Agent agent,
			String invoiceUrl) {
		String subject = appName + " — Booking Confirmed " + booking.getBookingCode();
		String body = buildBody(booking, property, user, invoiceUrl);
		send(NotificationRecipientType.USER, user.getEmail(), booking, subject, body);
		if (agent != null && agent.getEmail() != null) {
			send(NotificationRecipientType.AGENT, agent.getEmail(), booking, subject,
					"New property booking on your listing: " + property.getTitle());
		}
		send(NotificationRecipientType.ADMIN, supportEmail, booking, subject,
				"Booking " + booking.getBookingCode() + " confirmed for property " + property.getPropertyCode());
	}

	private void send(NotificationRecipientType type, String email, PropertyBooking booking, String subject,
			String body) {
		BookingNotification n = new BookingNotification();
		n.setBookingId(booking.getId());
		n.setRecipientType(type);
		n.setChannel(BookingNotificationType.EMAIL);
		n.setSubject(subject);
		n.setBody(body);
		n.setRecipientEmail(email);
		try {
			boolean sent = emailService.sendSimpleHtmlEmail(email, subject, wrapHtml(body));
			n.setStatus(sent ? "SENT" : "FAILED");
			n.setSentAt(LocalDateTime.now());
		} catch (Exception e) {
			log.warn("Booking notification failed for {}: {}", email, e.getMessage());
			n.setStatus("FAILED");
		}
		notificationRepository.save(n);
	}

	private String buildBody(PropertyBooking booking, Property property, User user, String invoiceUrl) {
		return "Hello " + user.getFullName() + ",\n\nYour booking for \"" + property.getTitle()
				+ "\" is confirmed.\n\nBooking ID: " + booking.getBookingCode() + "\nTotal: ₹" + booking.getTotalAmount()
				+ "\nPaid: ₹" + booking.getPaidAmount() + "\nRemaining: ₹" + booking.getRemainingAmount()
				+ "\nStatus: " + booking.getBookingStatus() + "\nDate: "
				+ booking.getBookingDate().format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm"))
				+ (invoiceUrl != null ? "\n\nDownload invoice: " + invoiceUrl : "");
	}

	private String wrapHtml(String text) {
		String html = text.replace("\n", "<br/>");
		return "<div style='font-family:Arial,sans-serif;color:#e2e8f0;background:#0f172a;padding:24px;'>"
				+ "<div style='max-width:560px;margin:0 auto;background:#1e293b;border-radius:12px;padding:24px;'>"
				+ html + "</div></div>";
	}
}
