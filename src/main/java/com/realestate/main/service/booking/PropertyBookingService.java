package com.realestate.main.service.booking;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.realestate.main.config.BookingProperties;
import com.realestate.main.config.RazorpayProperties;
import com.realestate.main.dto.booking.BalancePaymentPreviewResponse;
import com.realestate.main.dto.booking.BookingCheckoutPreviewResponse;
import com.realestate.main.dto.booking.BookingConfirmationResponse;
import com.realestate.main.dto.booking.BookingDraftRequest;
import com.realestate.main.dto.booking.EmiPlanOptionDto;
import com.realestate.main.dto.booking.EmiScheduleItemDto;
import com.realestate.main.dto.booking.PaymentVerifyRequest;
import com.realestate.main.dto.booking.PriceBreakdownDto;
import com.realestate.main.dto.booking.RazorpayOrderResponse;
import com.realestate.main.dto.booking.UserBookingListItemResponse;
import com.realestate.main.entity.Agent;
import com.realestate.main.entity.BookingInvoice;
import com.realestate.main.entity.EmiPayment;
import com.realestate.main.entity.InstallmentPlan;
import com.realestate.main.entity.PaymentTransaction;
import com.realestate.main.entity.Property;
import com.realestate.main.entity.PropertyBooking;
import com.realestate.main.entity.User;
import com.realestate.main.entity.enums.BookingStatus;
import com.realestate.main.entity.enums.InstallmentStatus;
import com.realestate.main.entity.enums.ListingType;
import com.realestate.main.entity.enums.PaymentPlanType;
import com.realestate.main.entity.enums.PaymentTransactionStatus;
import com.realestate.main.entity.enums.PropertyStatus;
import com.realestate.main.exception.AuthException;
import com.realestate.main.repository.AgentRepository;
import com.realestate.main.repository.BookingInvoiceRepository;
import com.realestate.main.repository.EmiPaymentRepository;
import com.realestate.main.repository.InstallmentPlanRepository;
import com.realestate.main.repository.PaymentTransactionRepository;
import com.realestate.main.repository.PropertyBookingRepository;
import com.realestate.main.repository.PropertyRepository;
import com.realestate.main.service.PublicPropertyBrowseService;
import com.realestate.main.service.UserService;

@Service
public class PropertyBookingService {

	private static final List<BookingStatus> ACTIVE_BOOKING_STATUSES = Arrays.asList(BookingStatus.CONFIRMED,
			BookingStatus.PARTIALLY_PAID, BookingStatus.FULLY_PAID, BookingStatus.EMI_ACTIVE);

	private static final List<BookingStatus> BLOCKING_USER_PROPERTY_STATUSES = Arrays.asList(BookingStatus.CONFIRMED,
			BookingStatus.PARTIALLY_PAID, BookingStatus.FULLY_PAID, BookingStatus.EMI_ACTIVE);

	private final PropertyBookingRepository bookingRepository;
	private final PropertyRepository propertyRepository;
	private final AgentRepository agentRepository;
	private final PaymentTransactionRepository transactionRepository;
	private final InstallmentPlanRepository installmentPlanRepository;
	private final EmiPaymentRepository emiPaymentRepository;
	private final UserService userService;
	private final PublicPropertyBrowseService propertyBrowseService;
	private final BookingPricingService pricingService;
	private final EmiCalculationService emiCalculationService;
	private final RazorpayPaymentService razorpayPaymentService;
	private final BookingInvoiceService invoiceService;
	private final BookingNotificationService notificationService;
	private final BookingInvoiceRepository invoiceRepository;
	private final BookingProperties bookingProperties;
	private final RazorpayProperties razorpayProperties;

	public PropertyBookingService(PropertyBookingRepository bookingRepository, PropertyRepository propertyRepository,
			AgentRepository agentRepository, PaymentTransactionRepository transactionRepository,
			InstallmentPlanRepository installmentPlanRepository, EmiPaymentRepository emiPaymentRepository,
			UserService userService, PublicPropertyBrowseService propertyBrowseService,
			BookingPricingService pricingService, EmiCalculationService emiCalculationService,
			RazorpayPaymentService razorpayPaymentService, BookingInvoiceService invoiceService,
			BookingNotificationService notificationService, BookingInvoiceRepository invoiceRepository,
			BookingProperties bookingProperties, RazorpayProperties razorpayProperties) {
		this.bookingRepository = bookingRepository;
		this.propertyRepository = propertyRepository;
		this.agentRepository = agentRepository;
		this.transactionRepository = transactionRepository;
		this.installmentPlanRepository = installmentPlanRepository;
		this.emiPaymentRepository = emiPaymentRepository;
		this.userService = userService;
		this.propertyBrowseService = propertyBrowseService;
		this.pricingService = pricingService;
		this.emiCalculationService = emiCalculationService;
		this.razorpayPaymentService = razorpayPaymentService;
		this.invoiceService = invoiceService;
		this.notificationService = notificationService;
		this.invoiceRepository = invoiceRepository;
		this.bookingProperties = bookingProperties;
		this.razorpayProperties = razorpayProperties;
	}

	@Transactional(readOnly = true)
	public BookingCheckoutPreviewResponse checkoutPreview(Long propertyId, String couponCode) {
		if (!bookingProperties.isEnabled()) {
			throw new AuthException("Property booking is temporarily unavailable");
		}
		Property property = requireActiveProperty(propertyId);
		boolean available = isPropertyAvailable(propertyId);
		BookingCheckoutPreviewResponse response = new BookingCheckoutPreviewResponse();
		response.setProperty(propertyBrowseService.getActiveDetail(propertyId));
		response.setAvailable(available);
		response.setAvailabilityMessage(available ? "Available for booking"
				: "This property already has an active booking");
		PriceBreakdownDto breakdown = pricingService.calculate(property, PaymentPlanType.FULL, null, couponCode);
		response.setPriceBreakdown(breakdown);
		response.setEmiPlans(emiCalculationService.buildAllPlans(breakdown.getTotalAmount()));
		response.setReservationMinutes(bookingProperties.getReservationMinutes());
		response.setRazorpayConfigured(razorpayPaymentService.isConfigured());
		return response;
	}

	@Transactional
	public PropertyBooking createDraft(Long userId, BookingDraftRequest request) {
		Property property = requireActiveProperty(request.getPropertyId());
		if (!isPropertyAvailable(property.getId())) {
			throw new AuthException("Property is not available for booking");
		}
		cancelExpiredPendingForUserProperty(userId, property.getId());

		if (bookingRepository.existsByUserIdAndPropertyIdAndBookingStatusIn(userId, property.getId(),
				BLOCKING_USER_PROPERTY_STATUSES)) {
			throw new AuthException("You already have a confirmed booking for this property");
		}

		PaymentPlanType plan = request.getPaymentPlanType();
		if (plan == PaymentPlanType.EMI && (request.getEmiMonths() == null || request.getEmiMonths() <= 0)) {
			throw new AuthException("Select an EMI tenure");
		}
		PriceBreakdownDto price = pricingService.calculate(property, plan, request.getEmiMonths(),
				request.getCouponCode());

		PropertyBooking booking = bookingRepository
				.findTopByUserIdAndPropertyIdAndBookingStatusOrderByCreatedAtDesc(userId, property.getId(),
						BookingStatus.PENDING)
				.orElse(new PropertyBooking());

		if (booking.getId() == null) {
			booking.setBookingCode(generateBookingCode());
			booking.setUserId(userId);
			booking.setPropertyId(property.getId());
			booking.setAgentId(property.getAgentId());
			booking.setBookingDate(LocalDateTime.now());
			booking.setBookingStatus(BookingStatus.PENDING);
			booking.setPaidAmount(BigDecimal.ZERO);
		}

		booking.setPaymentType(plan);
		booking.setPaymentMethod(request.getPaymentMethod());
		booking.setBasePrice(price.getBasePrice());
		booking.setGstAmount(price.getGstAmount());
		booking.setRegistrationCharges(price.getRegistrationCharges());
		booking.setBookingCharges(price.getBookingCharges());
		booking.setDiscountAmount(price.getDiscountAmount());
		booking.setTotalAmount(price.getTotalAmount());
		booking.setPayableNow(price.getPayableNow());
		booking.setRemainingAmount(price.getTotalAmount());
		booking.setEmiMonths(request.getEmiMonths());
		booking.setCouponCode(request.getCouponCode());
		booking.setRazorpayOrderId(null);
		booking.setReservationExpiresAt(LocalDateTime.now().plusMinutes(bookingProperties.getReservationMinutes()));
		return bookingRepository.save(booking);
	}

	@Transactional(readOnly = true)
	public BalancePaymentPreviewResponse balancePaymentPreview(Long userId, Long bookingId) {
		PropertyBooking booking = requireUserBooking(userId, bookingId);
		if (!canPayRemaining(booking)) {
			throw new AuthException("No outstanding balance on this booking");
		}
		Property property = propertyRepository.findById(booking.getPropertyId()).orElseThrow();
		BalancePaymentPreviewResponse response = new BalancePaymentPreviewResponse();
		response.setBookingId(booking.getId());
		response.setBookingCode(booking.getBookingCode());
		response.setStatusLabel(formatStatusLabel(booking.getBookingStatus()));
		response.setTotalAmount(booking.getTotalAmount());
		response.setPaidAmount(booking.getPaidAmount());
		response.setRemainingAmount(booking.getRemainingAmount());
		response.setProperty(propertyBrowseService.toPublicCard(property));
		response.setRazorpayConfigured(razorpayPaymentService.isConfigured());
		return response;
	}

	@Transactional
	public RazorpayOrderResponse createPaymentOrder(Long userId, Long bookingId) {
		PropertyBooking booking = requireUserBooking(userId, bookingId);
		User user = userService.getById(userId);
		BigDecimal chargeAmount;
		boolean balancePayment = canPayRemaining(booking);

		if (booking.getBookingStatus() == BookingStatus.PENDING) {
			if (booking.getReservationExpiresAt() != null
					&& booking.getReservationExpiresAt().isBefore(LocalDateTime.now())) {
				throw new AuthException("Reservation expired. Please start booking again.");
			}
			chargeAmount = booking.getPayableNow();
		} else if (balancePayment) {
			chargeAmount = booking.getRemainingAmount();
			booking.setPayableNow(chargeAmount);
		} else {
			throw new AuthException("Booking is not in a payable state");
		}

		String razorpayOrderId = razorpayPaymentService.createOrder(chargeAmount, booking.getBookingCode());
		booking.setRazorpayOrderId(razorpayOrderId);
		bookingRepository.save(booking);

		PaymentTransaction tx = new PaymentTransaction();
		tx.setTransactionCode("TXN-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase());
		tx.setBookingId(booking.getId());
		tx.setPaymentMethod(booking.getPaymentMethod() != null ? booking.getPaymentMethod()
				: com.realestate.main.entity.enums.PaymentMethod.UPI);
		tx.setAmount(chargeAmount);
		tx.setPaymentStatus(PaymentTransactionStatus.PENDING);
		tx.setRazorpayOrderId(razorpayOrderId);
		transactionRepository.save(tx);

		RazorpayOrderResponse response = new RazorpayOrderResponse();
		response.setBookingId(booking.getId());
		response.setBookingCode(booking.getBookingCode());
		response.setRazorpayOrderId(razorpayOrderId);
		response.setRazorpayKeyId(razorpayPaymentService.getKeyId());
		response.setAmount(chargeAmount);
		response.setAmountPaise(RazorpayPaymentService.toPaise(chargeAmount));
		response.setCurrency(razorpayProperties.getCurrency());
		response.setDemoMode(!razorpayPaymentService.isConfigured());
		response.setUserName(user.getFullName());
		response.setUserEmail(user.getEmail());
		response.setUserMobile(formatMobileForRazorpay(user.getMobile()));
		response.setBalancePayment(balancePayment);
		return response;
	}

	@Transactional
	public BookingConfirmationResponse verifyPayment(Long userId, PaymentVerifyRequest request) {
		PropertyBooking booking = requireUserBooking(userId, request.getBookingId());
		String requestOrderId = request.getRazorpayOrderId();
		if (requestOrderId == null || requestOrderId.isBlank()) {
			throw new AuthException("Razorpay order ID is missing");
		}
		if (!Objects.equals(requestOrderId, booking.getRazorpayOrderId())) {
			throw new AuthException("Order mismatch — payment tampering detected");
		}
		PaymentTransaction tx = transactionRepository.findByRazorpayOrderId(requestOrderId)
				.orElseThrow(() -> new AuthException("Transaction not found"));
		if (tx.getPaymentStatus() == PaymentTransactionStatus.SUCCESS) {
			return toConfirmation(booking);
		}

		boolean razorpayLive = razorpayPaymentService.isConfigured();
		if (razorpayLive) {
			if (request.getRazorpayPaymentId() == null || request.getRazorpaySignature() == null) {
				throw new AuthException("Payment details incomplete");
			}
			razorpayPaymentService.verifySignature(requestOrderId, request.getRazorpayPaymentId(),
					request.getRazorpaySignature());
		}

		String paymentId = request.getRazorpayPaymentId();
		if (paymentId == null || paymentId.isBlank()) {
			if (razorpayLive) {
				throw new AuthException("Payment ID missing");
			}
			paymentId = "demo_pay_" + UUID.randomUUID().toString().substring(0, 12);
		}

		boolean firstPayment = booking.getPaidAmount().compareTo(BigDecimal.ZERO) == 0;
		if (firstPayment && booking.getBookingStatus() == BookingStatus.PENDING) {
			if (tx.getAmount().compareTo(booking.getPayableNow()) != 0) {
				throw new AuthException("Payment amount mismatch");
			}
		} else if (tx.getAmount().compareTo(booking.getRemainingAmount()) != 0) {
			throw new AuthException("Payment amount mismatch");
		}

		tx.setPaymentStatus(PaymentTransactionStatus.SUCCESS);
		tx.setPaymentDate(LocalDateTime.now());
		tx.setRazorpayPaymentId(paymentId);
		tx.setGatewayResponse(razorpayLive ? "razorpay_verified" : "demo_verified");
		transactionRepository.save(tx);

		booking.setPaidAmount(booking.getPaidAmount().add(tx.getAmount()));
		booking.setRemainingAmount(booking.getTotalAmount().subtract(booking.getPaidAmount()));
		booking.setTransactionId(tx.getTransactionCode());
		applyBookingStatusAfterPayment(booking);
		bookingRepository.save(booking);

		if (booking.getPaymentType() == PaymentPlanType.EMI && booking.getEmiMonths() != null
				&& firstPayment) {
			createEmiSchedule(booking);
		}

		Property property = propertyRepository.findById(booking.getPropertyId()).orElseThrow();
		markPropertyUnavailable(property);
		User user = userService.getById(userId);
		Agent agent = agentRepository.findById(booking.getAgentId()).orElse(null);
		BookingInvoice invoice;
		try {
			invoice = invoiceService.generate(booking, property, user);
		} catch (Exception e) {
			invoice = null;
		}
		String invoiceUrl = invoice != null ? invoice.getFilePath() : null;
		notificationService.sendBookingConfirmations(booking, property, user, agent, invoiceUrl);

		return toConfirmation(booking);
	}

	@Transactional(readOnly = true)
	public BookingConfirmationResponse getConfirmation(Long userId, Long bookingId) {
		return toConfirmation(requireUserBooking(userId, bookingId));
	}

	@Transactional(readOnly = true)
	public List<UserBookingListItemResponse> listMyBookings(Long userId) {
		return bookingRepository.findByUserIdAndBookingStatusNotOrderByCreatedAtDesc(userId, BookingStatus.CANCELLED)
				.stream()
				.map(this::toListItem)
				.collect(Collectors.toList());
	}

	private UserBookingListItemResponse toListItem(PropertyBooking booking) {
		Property property = propertyRepository.findById(booking.getPropertyId()).orElse(null);
		UserBookingListItemResponse item = new UserBookingListItemResponse();
		item.setBookingId(booking.getId());
		item.setBookingCode(booking.getBookingCode());
		item.setStatus(booking.getBookingStatus());
		item.setStatusLabel(formatStatusLabel(booking.getBookingStatus()));
		item.setPaymentPlanType(booking.getPaymentType());
		item.setPropertyId(booking.getPropertyId());
		item.setAgentId(booking.getAgentId());
		agentRepository.findById(booking.getAgentId()).ifPresent(a -> item.setAgentName(a.getFullName()));
		item.setTotalAmount(booking.getTotalAmount());
		item.setPaidAmount(booking.getPaidAmount());
		item.setRemainingAmount(booking.getRemainingAmount());
		item.setTransactionId(booking.getTransactionId());
		item.setBookingDate(booking.getBookingDate());
		boolean canPayRemaining = canPayRemaining(booking);
		item.setCanPayRemaining(canPayRemaining);
		item.setNextPayableAmount(canPayRemaining ? booking.getRemainingAmount() : BigDecimal.ZERO);
		item.setPaymentComplete(booking.getBookingStatus() != BookingStatus.PENDING
				&& booking.getBookingStatus() != BookingStatus.CANCELLED
				&& booking.getPaidAmount().compareTo(BigDecimal.ZERO) > 0
				&& booking.getRemainingAmount().compareTo(BigDecimal.ZERO) <= 0);
		if (property != null) {
			item.setPropertyTitle(property.getTitle());
			item.setPropertyCode(property.getPropertyCode());
			item.setPropertyImageUrl(property.getPrimaryImageUrl());
			item.setPropertyLocation(property.getLocality() + ", " + property.getCity());
		}
		invoiceRepository.findByBookingId(booking.getId()).ifPresent(inv -> item.setInvoiceDownloadUrl(inv.getFilePath()));
		return item;
	}

	private static String formatStatusLabel(BookingStatus status) {
		return switch (status) {
		case PENDING -> "Payment pending";
		case CONFIRMED -> "Confirmed — paid";
		case PARTIALLY_PAID -> "Partially paid";
		case FULLY_PAID -> "Fully paid";
		case EMI_ACTIVE -> "EMI active";
		case CANCELLED -> "Cancelled";
		};
	}

	private void cancelExpiredPendingForUserProperty(Long userId, Long propertyId) {
		bookingRepository.findTopByUserIdAndPropertyIdAndBookingStatusOrderByCreatedAtDesc(userId, propertyId,
				BookingStatus.PENDING).ifPresent(pending -> {
					if (pending.getReservationExpiresAt() != null
							&& pending.getReservationExpiresAt().isBefore(LocalDateTime.now())) {
						pending.setBookingStatus(BookingStatus.CANCELLED);
						bookingRepository.save(pending);
					}
				});
	}

	private void applyBookingStatusAfterPayment(PropertyBooking booking) {
		if (booking.getRemainingAmount().compareTo(BigDecimal.ZERO) <= 0) {
			booking.setBookingStatus(BookingStatus.FULLY_PAID);
		} else if (booking.getPaymentType() == PaymentPlanType.EMI) {
			booking.setBookingStatus(BookingStatus.EMI_ACTIVE);
		} else if (booking.getPaymentType() == PaymentPlanType.BOOKING_ADVANCE) {
			booking.setBookingStatus(BookingStatus.PARTIALLY_PAID);
		} else {
			booking.setBookingStatus(BookingStatus.CONFIRMED);
		}
	}

	private void createEmiSchedule(PropertyBooking booking) {
		EmiPlanOptionDto plan = emiCalculationService.buildPlan(booking.getTotalAmount(), booking.getEmiMonths());
		InstallmentPlan ip = new InstallmentPlan();
		ip.setBookingId(booking.getId());
		ip.setEmiMonths(plan.getMonths());
		ip.setMonthlyAmount(plan.getMonthlyEmi());
		ip.setDownPayment(plan.getDownPayment());
		ip.setPrincipalAmount(plan.getPrincipalAmount());
		ip.setInterestRate(bookingProperties.getEmiAnnualInterest());
		ip.setTotalInterest(plan.getTotalInterest());
		ip.setInstallmentStatus(BookingStatus.EMI_ACTIVE);
		ip = installmentPlanRepository.save(ip);

		for (EmiScheduleItemDto item : plan.getSchedule()) {
			EmiPayment emi = new EmiPayment();
			emi.setInstallmentPlanId(ip.getId());
			emi.setInstallmentNumber(item.getInstallmentNumber());
			emi.setDueDate(item.getDueDate());
			emi.setAmount(item.getAmount());
			emi.setInstallmentStatus(InstallmentStatus.SCHEDULED);
			emiPaymentRepository.save(emi);
		}
	}

	private BookingConfirmationResponse toConfirmation(PropertyBooking booking) {
		Property property = propertyRepository.findById(booking.getPropertyId()).orElse(null);
		BookingConfirmationResponse r = new BookingConfirmationResponse();
		r.setBookingId(booking.getId());
		r.setBookingCode(booking.getBookingCode());
		r.setStatus(booking.getBookingStatus());
		r.setStatusLabel(formatStatusLabel(booking.getBookingStatus()));
		r.setPaymentPlanType(booking.getPaymentType());
		r.setTotalAmount(booking.getTotalAmount());
		r.setPaidAmount(booking.getPaidAmount());
		r.setRemainingAmount(booking.getRemainingAmount());
		r.setTransactionCode(booking.getTransactionId());
		r.setBookingDate(booking.getBookingDate());
		if (property != null) {
			r.setPropertyTitle(property.getTitle());
			r.setPropertyCode(property.getPropertyCode());
		}
		installmentPlanRepository.findByBookingId(booking.getId()).ifPresent(plan -> {
			List<EmiScheduleItemDto> schedule = emiPaymentRepository
					.findByInstallmentPlanIdOrderByInstallmentNumberAsc(plan.getId()).stream().map(emi -> {
						EmiScheduleItemDto dto = new EmiScheduleItemDto();
						dto.setInstallmentNumber(emi.getInstallmentNumber());
						dto.setDueDate(emi.getDueDate());
						dto.setAmount(emi.getAmount());
						return dto;
					}).collect(Collectors.toList());
			r.setEmiSchedule(schedule);
		});
		invoiceRepository.findByBookingId(booking.getId()).ifPresent(inv -> {
			r.setInvoiceNumber(inv.getInvoiceNumber());
			r.setInvoiceDownloadUrl(inv.getFilePath());
		});
		return r;
	}

	private Property requireActiveProperty(Long propertyId) {
		return propertyRepository.findById(propertyId)
				.filter(p -> p.getStatus() == PropertyStatus.ACTIVE)
				.orElseThrow(() -> new AuthException("Property not found or not available"));
	}

	private PropertyBooking requireUserBooking(Long userId, Long bookingId) {
		return bookingRepository.findByIdAndUserId(bookingId, userId)
				.orElseThrow(() -> new AuthException("Booking not found"));
	}

	private boolean isPropertyAvailable(Long propertyId) {
		if (hasActivePendingReservation(propertyId)) {
			return false;
		}
		return !bookingRepository.existsByPropertyIdAndBookingStatusIn(propertyId, ACTIVE_BOOKING_STATUSES);
	}

	private boolean hasActivePendingReservation(Long propertyId) {
		return bookingRepository.findTopByPropertyIdAndBookingStatusOrderByCreatedAtDesc(propertyId,
				BookingStatus.PENDING).map(pending -> pending.getReservationExpiresAt() == null
						|| pending.getReservationExpiresAt().isAfter(LocalDateTime.now())).orElse(false);
	}

	private boolean canPayRemaining(PropertyBooking booking) {
		if (booking.getRemainingAmount() == null
				|| booking.getRemainingAmount().compareTo(BigDecimal.ZERO) <= 0) {
			return false;
		}
		BookingStatus status = booking.getBookingStatus();
		return status == BookingStatus.PARTIALLY_PAID || status == BookingStatus.EMI_ACTIVE
				|| status == BookingStatus.CONFIRMED;
	}

	private void markPropertyUnavailable(Property property) {
		if (property.getStatus() != PropertyStatus.ACTIVE) {
			return;
		}
		if (property.getListingType() == ListingType.RENT) {
			property.setStatus(PropertyStatus.RENTED);
		} else {
			property.setStatus(PropertyStatus.SOLD);
		}
		propertyRepository.save(property);
	}

	private static String generateBookingCode() {
		return "EVB" + LocalDate.now().toString().replace("-", "")
				+ UUID.randomUUID().toString().substring(0, 6).toUpperCase();
	}

	private static String formatMobileForRazorpay(String mobile) {
		if (mobile == null || mobile.isBlank()) {
			return "";
		}
		String digits = mobile.replaceAll("\\D", "");
		if (digits.length() > 10) {
			digits = digits.substring(digits.length() - 10);
		}
		return digits;
	}
}
