package com.realestate.main.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.realestate.main.dto.response.AgentBookingDetailResponse;
import com.realestate.main.entity.InstallmentPlan;
import com.realestate.main.entity.Property;
import com.realestate.main.entity.PropertyBooking;
import com.realestate.main.entity.User;
import com.realestate.main.entity.enums.BookingStatus;
import com.realestate.main.entity.enums.PaymentMethod;
import com.realestate.main.entity.enums.PaymentPlanType;
import com.realestate.main.repository.InstallmentPlanRepository;
import com.realestate.main.repository.PropertyBookingRepository;
import com.realestate.main.repository.PropertyRepository;
import com.realestate.main.exception.AuthException;
import com.realestate.main.repository.UserRepository;

@Service
public class AgentBookingService {

	private final PropertyBookingRepository bookingRepository;
	private final PropertyRepository propertyRepository;
	private final UserRepository userRepository;
	private final InstallmentPlanRepository installmentPlanRepository;

	public AgentBookingService(PropertyBookingRepository bookingRepository, PropertyRepository propertyRepository,
			UserRepository userRepository, InstallmentPlanRepository installmentPlanRepository) {
		this.bookingRepository = bookingRepository;
		this.propertyRepository = propertyRepository;
		this.userRepository = userRepository;
		this.installmentPlanRepository = installmentPlanRepository;
	}

	@Transactional(readOnly = true)
	public List<AgentBookingDetailResponse> listForAgent(Long agentId) {
		return bookingRepository.findByAgentIdOrderByBookingDateDesc(agentId).stream()
				.filter(b -> b.getBookingStatus() != BookingStatus.CANCELLED)
				.map(this::toDetail)
				.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public AgentBookingDetailResponse getForAgent(Long agentId, Long bookingId) {
		PropertyBooking booking = bookingRepository.findById(bookingId)
				.orElseThrow(() -> new AuthException("Booking not found"));
		if (!booking.getAgentId().equals(agentId)) {
			throw new AuthException("Unauthorized");
		}
		if (booking.getBookingStatus() == BookingStatus.CANCELLED) {
			throw new AuthException("Booking not available");
		}
		return toDetail(booking);
	}

	@Transactional(readOnly = true)
	public BigDecimal sumCollectedForAgent(Long agentId) {
		return bookingRepository.findByAgentIdOrderByCreatedAtDesc(agentId).stream()
				.filter(b -> b.getBookingStatus() != BookingStatus.CANCELLED
						&& b.getBookingStatus() != BookingStatus.PENDING)
				.map(PropertyBooking::getPaidAmount)
				.reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	@Transactional(readOnly = true)
	public BigDecimal sumPendingForAgent(Long agentId) {
		return bookingRepository.findByAgentIdOrderByCreatedAtDesc(agentId).stream()
				.filter(b -> b.getBookingStatus() != BookingStatus.CANCELLED
						&& b.getBookingStatus() != BookingStatus.PENDING)
				.map(PropertyBooking::getRemainingAmount)
				.reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	private AgentBookingDetailResponse toDetail(PropertyBooking booking) {
		Property property = propertyRepository.findById(booking.getPropertyId()).orElse(null);
		User user = userRepository.findById(booking.getUserId()).orElse(null);

		AgentBookingDetailResponse r = new AgentBookingDetailResponse();
		r.setBookingId(booking.getId());
		r.setBookingCode(booking.getBookingCode());
		r.setStatus(booking.getBookingStatus());
		r.setStatusLabel(formatStatusLabel(booking.getBookingStatus()));
		r.setPaymentPlanType(booking.getPaymentType());
		r.setPaymentPlanLabel(formatPaymentPlan(booking.getPaymentType()));
		r.setPropertyId(booking.getPropertyId());
		r.setBasePrice(booking.getBasePrice());
		r.setGstAmount(booking.getGstAmount());
		r.setRegistrationCharges(booking.getRegistrationCharges());
		r.setBookingCharges(booking.getBookingCharges());
		r.setDiscountAmount(booking.getDiscountAmount());
		r.setTotalAmount(booking.getTotalAmount());
		r.setPayableNow(booking.getPayableNow());
		r.setPaidAmount(booking.getPaidAmount());
		r.setRemainingAmount(booking.getRemainingAmount());
		r.setPaymentMethod(booking.getPaymentMethod());
		r.setPaymentMethodLabel(formatPaymentMethod(booking.getPaymentMethod()));
		r.setTransactionId(booking.getTransactionId());
		r.setEmiMonths(booking.getEmiMonths());
		r.setBookingDate(booking.getBookingDate());
		r.setPaymentReceived(booking.getBookingStatus() != BookingStatus.PENDING
				&& booking.getPaidAmount() != null
				&& booking.getPaidAmount().compareTo(BigDecimal.ZERO) > 0);

		if (property != null) {
			r.setPropertyTitle(property.getTitle());
			r.setPropertyCode(property.getPropertyCode());
			r.setPropertyImageUrl(property.getPrimaryImageUrl());
			StringBuilder loc = new StringBuilder();
			if (property.getLocality() != null && !property.getLocality().isBlank()) {
				loc.append(property.getLocality().trim());
			}
			if (property.getCity() != null && !property.getCity().isBlank()) {
				if (loc.length() > 0) {
					loc.append(", ");
				}
				loc.append(property.getCity().trim());
			}
			r.setPropertyLocation(loc.toString());
		}
		if (user != null) {
			r.setUserId(user.getId());
			r.setUserName(user.getFullName());
			r.setUserEmail(user.getEmail());
			r.setUserMobile(user.getMobile());
		}

		installmentPlanRepository.findByBookingId(booking.getId()).ifPresent(plan -> {
			r.setEmiMonths(plan.getEmiMonths());
			r.setMonthlyEmi(plan.getMonthlyAmount());
		});

		return r;
	}

	private static String formatStatusLabel(BookingStatus status) {
		if (status == null) {
			return "Unknown";
		}
		return switch (status) {
		case PENDING -> "Payment pending";
		case CONFIRMED -> "Confirmed — paid";
		case PARTIALLY_PAID -> "Partially paid";
		case FULLY_PAID -> "Fully paid";
		case EMI_ACTIVE -> "EMI active";
		case CANCELLED -> "Cancelled";
		};
	}

	private static String formatPaymentPlan(PaymentPlanType type) {
		if (type == null) {
			return "—";
		}
		return switch (type) {
		case FULL -> "Full payment";
		case BOOKING_ADVANCE -> "Booking advance (10%)";
		case EMI -> "EMI installment plan";
		};
	}

	private static String formatPaymentMethod(PaymentMethod method) {
		if (method == null) {
			return "—";
		}
		return switch (method) {
		case UPI -> "UPI";
		case CREDIT_CARD -> "Credit card";
		case DEBIT_CARD -> "Debit card";
		case NET_BANKING -> "Net banking";
		case WALLET -> "Wallet";
		case EMI -> "EMI";
		case BANK_TRANSFER -> "Bank transfer";
		case CASH_TOKEN -> "Cash token advance";
		};
	}
}
