package com.realestate.main.service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.realestate.main.dto.response.AgentNotificationItemResponse;
import com.realestate.main.dto.response.AgentNotificationItemResponse.DetailLine;
import com.realestate.main.dto.response.AgentNotificationsSummaryResponse;
import com.realestate.main.entity.Property;
import com.realestate.main.entity.PropertyBooking;
import com.realestate.main.entity.PropertyEnquiry;
import com.realestate.main.entity.SubscriptionNotification;
import com.realestate.main.entity.SubscriptionPayment;
import com.realestate.main.entity.User;
import com.realestate.main.entity.BookingNotification;
import com.realestate.main.entity.enums.BookingStatus;
import com.realestate.main.entity.enums.EnquiryStatus;
import com.realestate.main.entity.enums.NotificationRecipientType;
import com.realestate.main.entity.enums.SubscriptionPaymentStatus;
import com.realestate.main.repository.BookingNotificationRepository;
import com.realestate.main.repository.PropertyBookingRepository;
import com.realestate.main.repository.PropertyEnquiryRepository;
import com.realestate.main.repository.PropertyRepository;
import com.realestate.main.repository.SubscriptionNotificationRepository;
import com.realestate.main.repository.SubscriptionPaymentRepository;
import com.realestate.main.repository.UserRepository;

@Service
public class AgentNotificationFeedService {

	private static final DateTimeFormatter DT = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");

	private final PropertyEnquiryRepository enquiryRepository;
	private final PropertyBookingRepository bookingRepository;
	private final PropertyRepository propertyRepository;
	private final UserRepository userRepository;
	private final SubscriptionNotificationRepository subscriptionNotificationRepository;
	private final SubscriptionPaymentRepository subscriptionPaymentRepository;
	private final BookingNotificationRepository bookingNotificationRepository;

	public AgentNotificationFeedService(PropertyEnquiryRepository enquiryRepository,
			PropertyBookingRepository bookingRepository, PropertyRepository propertyRepository,
			UserRepository userRepository,
			SubscriptionNotificationRepository subscriptionNotificationRepository,
			SubscriptionPaymentRepository subscriptionPaymentRepository,
			BookingNotificationRepository bookingNotificationRepository) {
		this.enquiryRepository = enquiryRepository;
		this.bookingRepository = bookingRepository;
		this.propertyRepository = propertyRepository;
		this.userRepository = userRepository;
		this.subscriptionNotificationRepository = subscriptionNotificationRepository;
		this.subscriptionPaymentRepository = subscriptionPaymentRepository;
		this.bookingNotificationRepository = bookingNotificationRepository;
	}

	@Transactional(readOnly = true)
	public AgentNotificationsSummaryResponse loadFeed(Long agentId, String categoryFilter) {
		List<AgentNotificationItemResponse> all = new ArrayList<>();
		all.addAll(fromEnquiries(agentId));
		all.addAll(fromBookings(agentId));
		all.addAll(fromSubscription(agentId));
		all.addAll(fromSubscriptionPayments(agentId));
		all.addAll(fromBookingEmails(agentId));

		all.sort(Comparator.comparing(AgentNotificationItemResponse::getCreatedAt).reversed());

		if (categoryFilter != null && !categoryFilter.isBlank() && !"ALL".equalsIgnoreCase(categoryFilter)) {
			String cat = categoryFilter.toUpperCase();
			all = all.stream().filter(n -> cat.equals(n.getCategory())).collect(Collectors.toList());
		}

		AgentNotificationsSummaryResponse summary = new AgentNotificationsSummaryResponse();
		summary.setNotifications(all);
		summary.setTotalCount(all.size());
		summary.setUnreadCount(all.stream().filter(AgentNotificationItemResponse::isUnread).count());
		return summary;
	}

	private List<AgentNotificationItemResponse> fromEnquiries(Long agentId) {
		List<AgentNotificationItemResponse> items = new ArrayList<>();
		for (PropertyEnquiry e : enquiryRepository.findByAgentIdOrderByCreatedAtDesc(agentId)) {
			Property p = propertyRepository.findById(e.getPropertyId()).orElse(null);
			AgentNotificationItemResponse n = new AgentNotificationItemResponse();
			n.setId("enquiry-" + e.getId());
			n.setCategory("LEAD");
			n.setType("PROPERTY_ENQUIRY");
			n.setTitle(enquiryTitle(e.getStatus()));
			n.setMessage(e.getMessage());
			n.setChannel("PORTAL");
			n.setStatus(e.getStatus().name());
			n.setLinkUrl("/agent/enquiries");
			n.setCreatedAt(format(e.getCreatedAt()));
			n.setUnread(e.getStatus() == EnquiryStatus.PENDING);
			List<DetailLine> details = new ArrayList<>();
			details.add(new DetailLine("Buyer", e.getUserName()));
			details.add(new DetailLine("Email", e.getUserEmail()));
			if (e.getUserMobile() != null && !e.getUserMobile().isBlank()) {
				details.add(new DetailLine("Mobile", e.getUserMobile()));
			}
			details.add(new DetailLine("Property", p != null ? p.getTitle() : "ID " + e.getPropertyId()));
			if (p != null) {
				details.add(new DetailLine("Property code", p.getPropertyCode()));
			}
			details.add(new DetailLine("Enquiry status", e.getStatus().name()));
			if (e.getAgentReply() != null && !e.getAgentReply().isBlank()) {
				details.add(new DetailLine("Your reply", e.getAgentReply()));
			}
			n.setDetails(details);
			items.add(n);
		}
		return items;
	}

	private List<AgentNotificationItemResponse> fromBookings(Long agentId) {
		List<AgentNotificationItemResponse> items = new ArrayList<>();
		for (PropertyBooking b : bookingRepository.findByAgentIdOrderByCreatedAtDesc(agentId)) {
			if (b.getBookingStatus() == BookingStatus.CANCELLED) {
				continue;
			}
			Property p = propertyRepository.findById(b.getPropertyId()).orElse(null);
			User user = userRepository.findById(b.getUserId()).orElse(null);

			AgentNotificationItemResponse n = new AgentNotificationItemResponse();
			n.setId("booking-" + b.getId());
			n.setCategory("BOOKING");
			n.setType("USER_BOOKING");
			n.setTitle("Booking " + b.getBookingCode() + " — " + b.getBookingStatus().name().replace('_', ' '));
			n.setMessage("Buyer booked your listing" + (p != null ? ": " + p.getTitle() : ""));
			n.setChannel("PORTAL");
			n.setStatus(b.getBookingStatus().name());
			n.setLinkUrl("/agent/bookings");
			n.setCreatedAt(format(b.getCreatedAt() != null ? b.getCreatedAt() : b.getBookingDate()));
			n.setUnread(b.getBookingStatus() == BookingStatus.PENDING
					|| b.getBookingStatus() == BookingStatus.CONFIRMED);

			List<DetailLine> details = new ArrayList<>();
			details.add(new DetailLine("Booking ID", b.getBookingCode()));
			details.add(new DetailLine("Buyer", user != null ? user.getFullName() : "—"));
			if (user != null) {
				details.add(new DetailLine("Buyer email", user.getEmail()));
			}
			details.add(new DetailLine("Property", p != null ? p.getTitle() : "—"));
			details.add(new DetailLine("Total amount", "₹" + b.getTotalAmount()));
			details.add(new DetailLine("Paid", "₹" + (b.getPaidAmount() != null ? b.getPaidAmount() : "0")));
			details.add(new DetailLine("Balance", "₹" + (b.getRemainingAmount() != null ? b.getRemainingAmount() : "0")));
			details.add(new DetailLine("Payment plan", b.getPaymentType() != null ? b.getPaymentType().name() : "—"));
			n.setDetails(details);
			items.add(n);
		}
		return items;
	}

	private List<AgentNotificationItemResponse> fromSubscription(Long agentId) {
		List<AgentNotificationItemResponse> items = new ArrayList<>();
		for (SubscriptionNotification sn : subscriptionNotificationRepository.findByAgentIdOrderByCreatedAtDesc(agentId)) {
			AgentNotificationItemResponse n = new AgentNotificationItemResponse();
			n.setId("sub-" + sn.getId());
			n.setCategory("SUBSCRIPTION");
			n.setType(sn.getNotificationType());
			n.setTitle(formatSubscriptionType(sn.getNotificationType()));
			n.setMessage(sn.getMessage());
			n.setChannel(sn.getChannel());
			n.setStatus(sn.getStatus());
			n.setLinkUrl("/agent/subscription");
			n.setCreatedAt(format(sn.getCreatedAt()));
			n.setUnread("SUBSCRIPTION_EXPIRING".equals(sn.getNotificationType())
					|| "PROPERTY_LIMIT_REACHED".equals(sn.getNotificationType()));
			n.getDetails().add(new DetailLine("Channel", sn.getChannel()));
			if (sn.getSubscriptionId() != null) {
				n.getDetails().add(new DetailLine("Subscription ID", String.valueOf(sn.getSubscriptionId())));
			}
			items.add(n);
		}
		return items;
	}

	private List<AgentNotificationItemResponse> fromSubscriptionPayments(Long agentId) {
		List<AgentNotificationItemResponse> items = new ArrayList<>();
		for (SubscriptionPayment pay : subscriptionPaymentRepository.findByAgentIdOrderByCreatedAtDesc(agentId)) {
			AgentNotificationItemResponse n = new AgentNotificationItemResponse();
			n.setId("pay-" + pay.getId());
			n.setCategory("PAYMENT");
			n.setType("SUBSCRIPTION_PAYMENT");
			n.setTitle("Subscription payment — " + pay.getPaymentStatus().name());
			n.setMessage("Payment of ₹" + pay.getAmount() + (pay.getInvoiceNumber() != null
					? " · Invoice " + pay.getInvoiceNumber() : ""));
			n.setChannel("RAZORPAY");
			n.setStatus(pay.getPaymentStatus().name());
			n.setLinkUrl("/agent/subscription");
			n.setCreatedAt(format(pay.getPaymentDate() != null ? pay.getPaymentDate() : pay.getCreatedAt()));
			n.setUnread(pay.getPaymentStatus() == SubscriptionPaymentStatus.PENDING);
			n.getDetails().add(new DetailLine("Amount", "₹" + pay.getAmount()));
			if (pay.getTransactionId() != null) {
				n.getDetails().add(new DetailLine("Transaction", pay.getTransactionId()));
			}
			if (pay.getInvoiceNumber() != null) {
				n.getDetails().add(new DetailLine("Invoice", pay.getInvoiceNumber()));
			}
			items.add(n);
		}
		return items;
	}

	private List<AgentNotificationItemResponse> fromBookingEmails(Long agentId) {
		List<PropertyBooking> bookings = bookingRepository.findByAgentIdOrderByCreatedAtDesc(agentId);
		if (bookings.isEmpty()) {
			return List.of();
		}
		Set<Long> bookingIds = bookings.stream().map(PropertyBooking::getId).collect(Collectors.toSet());
		Map<Long, PropertyBooking> bookingMap = bookings.stream()
				.collect(Collectors.toMap(PropertyBooking::getId, b -> b, (a, b) -> a));

		List<BookingNotification> emails = bookingNotificationRepository.findByBookingIdInAndRecipientType(
				bookingIds, NotificationRecipientType.AGENT);
		List<AgentNotificationItemResponse> items = new ArrayList<>();
		Set<String> seen = new HashSet<>();

		for (BookingNotification bn : emails) {
			String key = bn.getBookingId() + "-" + bn.getSubject();
			if (!seen.add(key)) {
				continue;
			}
			PropertyBooking b = bookingMap.get(bn.getBookingId());
			AgentNotificationItemResponse n = new AgentNotificationItemResponse();
			n.setId("bemail-" + bn.getId());
			n.setCategory("BOOKING");
			n.setType("BOOKING_EMAIL");
			n.setTitle(bn.getSubject() != null ? bn.getSubject() : "Booking email");
			n.setMessage(bn.getBody() != null ? truncate(bn.getBody(), 200) : "Email notification sent");
			n.setChannel(bn.getChannel() != null ? bn.getChannel().name() : "EMAIL");
			n.setStatus(bn.getStatus());
			n.setLinkUrl("/agent/bookings");
			n.setCreatedAt(format(bn.getCreatedAt()));
			n.setUnread("SENT".equals(bn.getStatus()));
			n.getDetails().add(new DetailLine("Recipient", bn.getRecipientEmail()));
			if (b != null) {
				n.getDetails().add(new DetailLine("Booking", b.getBookingCode()));
			}
			items.add(n);
		}
		return items;
	}

	private static String enquiryTitle(EnquiryStatus status) {
		return switch (status) {
		case PENDING -> "New lead inquiry";
		case ACCEPTED -> "Lead accepted";
		case DECLINED -> "Lead declined";
		case REPLIED -> "Reply sent to buyer";
		default -> "Lead update";
		};
	}

	private static String formatSubscriptionType(String type) {
		if (type == null) {
			return "Subscription update";
		}
		return type.replace('_', ' ').toLowerCase();
	}

	private static String format(java.time.LocalDateTime dt) {
		return dt != null ? dt.format(DT) : "";
	}

	private static String truncate(String s, int max) {
		if (s == null) {
			return "";
		}
		String t = s.replaceAll("<[^>]+>", " ").trim();
		return t.length() <= max ? t : t.substring(0, max) + "…";
	}
}
