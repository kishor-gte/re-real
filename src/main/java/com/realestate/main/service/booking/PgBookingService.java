package com.realestate.main.service.booking;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.realestate.main.config.BookingProperties;
import com.realestate.main.config.RazorpayProperties;
import com.realestate.main.dto.booking.RazorpayOrderResponse;
import com.realestate.main.dto.pgbooking.PgBookingCheckoutPreviewResponse;
import com.realestate.main.dto.pgbooking.PgBookingConfirmationResponse;
import com.realestate.main.dto.pgbooking.PgBookingDraftRequest;
import com.realestate.main.dto.pgbooking.PgBookingRoomOptionResponse;
import com.realestate.main.dto.pgbooking.PgOccupantRequest;
import com.realestate.main.dto.pgbooking.PgOccupantSummaryDto;
import com.realestate.main.dto.pgbooking.PgPaymentVerifyRequest;
import com.realestate.main.dto.pgbooking.UserPgBookingListItemResponse;
import com.realestate.main.dto.response.PublicPgCardResponse;
import com.realestate.main.entity.PgBooking;
import com.realestate.main.entity.PgBookingOccupant;
import com.realestate.main.entity.PgMonthlyRentDue;
import com.realestate.main.entity.PgFloor;
import com.realestate.main.entity.PgPaymentTransaction;
import com.realestate.main.entity.PgProperty;
import com.realestate.main.entity.PgRoom;
import com.realestate.main.entity.PgRoomSharing;
import com.realestate.main.entity.User;
import com.realestate.main.entity.enums.BookingStatus;
import com.realestate.main.entity.enums.PaymentMethod;
import com.realestate.main.entity.enums.PaymentTransactionStatus;
import com.realestate.main.entity.enums.PgOwnerApprovalStatus;
import com.realestate.main.entity.enums.PgPropertyStatus;
import com.realestate.main.entity.enums.PgRoomStatus;
import com.realestate.main.entity.enums.PgStayStatus;
import com.realestate.main.exception.AuthException;
import com.realestate.main.repository.PgBookingOccupantRepository;
import com.realestate.main.repository.PgBookingRepository;
import com.realestate.main.repository.PgOwnerRepository;
import com.realestate.main.repository.PgFloorRepository;
import com.realestate.main.repository.PgPaymentTransactionRepository;
import com.realestate.main.repository.PgPropertyRepository;
import com.realestate.main.repository.PgRoomRepository;
import com.realestate.main.repository.PgRoomSharingRepository;
import com.realestate.main.service.PublicPgBrowseService;
import com.realestate.main.service.UserService;

@Service
public class PgBookingService {

	private final PgBookingRepository pgBookingRepository;
	private final PgBookingOccupantRepository occupantRepository;
	private final PgPaymentTransactionRepository pgPaymentTransactionRepository;
	private final PgPropertyRepository pgPropertyRepository;
	private final PgRoomRepository pgRoomRepository;
	private final PgFloorRepository pgFloorRepository;
	private final PgRoomSharingRepository pgRoomSharingRepository;
	private final PublicPgBrowseService publicPgBrowseService;
	private final UserService userService;
	private final RazorpayPaymentService razorpayPaymentService;
	private final BookingProperties bookingProperties;
	private final RazorpayProperties razorpayProperties;
	private final PgBookingNotificationService pgBookingNotificationService;
	private final PgMonthlyRentService pgMonthlyRentService;
	private final PgOwnerRepository pgOwnerRepository;

	public PgBookingService(PgBookingRepository pgBookingRepository, PgBookingOccupantRepository occupantRepository,
			PgPaymentTransactionRepository pgPaymentTransactionRepository, PgPropertyRepository pgPropertyRepository,
			PgRoomRepository pgRoomRepository, PgFloorRepository pgFloorRepository,
			PgRoomSharingRepository pgRoomSharingRepository, PublicPgBrowseService publicPgBrowseService,
			UserService userService, RazorpayPaymentService razorpayPaymentService,
			BookingProperties bookingProperties, RazorpayProperties razorpayProperties,
			PgBookingNotificationService pgBookingNotificationService,
			@Lazy PgMonthlyRentService pgMonthlyRentService, PgOwnerRepository pgOwnerRepository) {
		this.pgBookingRepository = pgBookingRepository;
		this.occupantRepository = occupantRepository;
		this.pgPaymentTransactionRepository = pgPaymentTransactionRepository;
		this.pgPropertyRepository = pgPropertyRepository;
		this.pgRoomRepository = pgRoomRepository;
		this.pgFloorRepository = pgFloorRepository;
		this.pgRoomSharingRepository = pgRoomSharingRepository;
		this.publicPgBrowseService = publicPgBrowseService;
		this.userService = userService;
		this.razorpayPaymentService = razorpayPaymentService;
		this.bookingProperties = bookingProperties;
		this.razorpayProperties = razorpayProperties;
		this.pgBookingNotificationService = pgBookingNotificationService;
		this.pgMonthlyRentService = pgMonthlyRentService;
		this.pgOwnerRepository = pgOwnerRepository;
	}

	@Transactional(readOnly = true)
	public List<PgBookingRoomOptionResponse> listBookableRooms(Long pgPropertyId) {
		PgProperty property = requirePublishedPg(pgPropertyId);
		Map<Long, Integer> floorNumbers = pgFloorRepository.findByPgPropertyIdOrderBySortOrderAscFloorNumberAsc(
				property.getId()).stream()
				.collect(Collectors.toMap(PgFloor::getId, PgFloor::getFloorNumber, (a, b) -> a));

		return pgRoomRepository.findByPgPropertyIdOrderByFloorIdAscRoomNumberAsc(property.getId()).stream()
				.filter(this::isRoomBookable)
				.map(room -> toRoomOption(room, floorNumbers.get(room.getFloorId()), property))
				.collect(Collectors.toList());
	}

	@Transactional
	public PgBooking createDraft(Long userId, PgBookingDraftRequest request) {
		if (!bookingProperties.isEnabled()) {
			throw new AuthException("PG booking is temporarily unavailable");
		}
		PgProperty property = requirePublishedPg(request.getPgPropertyId());
		PgRoom room = pgRoomRepository.findById(request.getRoomId())
				.filter(r -> Objects.equals(r.getPgPropertyId(), property.getId()))
				.orElseThrow(() -> new AuthException("Room not found"));

		validateDraftRequest(request, room);

		BigDecimal rentPerBed = resolveRentPerBed(room, property);
		int bedCount = request.getBedCount();
		BigDecimal monthlyRentTotal = rentPerBed.multiply(BigDecimal.valueOf(bedCount)).setScale(2, RoundingMode.HALF_UP);
		BigDecimal securityDepositTotal = resolveSecurityDepositTotal(property, bedCount);
		BigDecimal totalAmount = monthlyRentTotal.add(securityDepositTotal);
		BigDecimal payableNow = resolvePayableNow(property, monthlyRentTotal, bedCount);
		BigDecimal remainingAmount = totalAmount.subtract(payableNow).max(BigDecimal.ZERO);

		String bedNumbersStr = request.getBedNumbers().stream().sorted().map(String::valueOf)
				.collect(Collectors.joining(","));

		PgBooking booking = pgBookingRepository
				.findTopByUserIdAndPgPropertyIdAndBookingStatusOrderByCreatedAtDesc(userId, property.getId(),
						BookingStatus.PENDING)
				.orElse(new PgBooking());

		if (booking.getId() != null) {
			occupantRepository.deleteByPgBookingId(booking.getId());
		} else {
			booking.setBookingCode(generateBookingCode());
			booking.setUserId(userId);
			booking.setPgPropertyId(property.getId());
			booking.setPgOwnerId(property.getOwnerId());
			booking.setBookingDate(LocalDateTime.now());
			booking.setBookingStatus(BookingStatus.PENDING);
			booking.setPaidAmount(BigDecimal.ZERO);
		}

		booking.setRoomId(room.getId());
		booking.setRoomNumber(room.getRoomNumber());
		booking.setBedCount(bedCount);
		booking.setBedNumbers(bedNumbersStr);
		booking.setPaymentMethod(request.getPaymentMethod() != null ? request.getPaymentMethod() : PaymentMethod.UPI);
		booking.setRentPerBed(rentPerBed);
		booking.setSecurityDepositAmount(securityDepositTotal);
		booking.setTotalAmount(totalAmount);
		booking.setPayableNow(payableNow);
		booking.setRemainingAmount(remainingAmount);
		booking.setRazorpayOrderId(null);
		booking.setReservationExpiresAt(LocalDateTime.now().plusMinutes(bookingProperties.getReservationMinutes()));
		booking = pgBookingRepository.save(booking);

		saveOccupants(booking.getId(), request);
		return booking;
	}

	@Transactional(readOnly = true)
	public PgBookingCheckoutPreviewResponse checkoutPreview(Long userId, Long bookingId) {
		PgBooking booking = requireUserBooking(userId, bookingId);
		if (canPayRemaining(booking)) {
			throw new AuthException("Use balance payment preview for this booking");
		}
		if (booking.getBookingStatus() != BookingStatus.PENDING) {
			throw new AuthException("Booking is not awaiting payment");
		}
		return toCheckoutPreview(booking, false);
	}

	@Transactional(readOnly = true)
	public PgBookingCheckoutPreviewResponse balancePaymentPreview(Long userId, Long bookingId) {
		PgBooking booking = requireUserBooking(userId, bookingId);
		if (!canPayRemaining(booking)) {
			throw new AuthException("No outstanding balance on this booking");
		}
		return toCheckoutPreview(booking, true);
	}

	@Transactional
	public RazorpayOrderResponse createPaymentOrder(Long userId, Long bookingId) {
		PgBooking booking = requireUserBooking(userId, bookingId);
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
		pgBookingRepository.save(booking);

		PgPaymentTransaction tx = new PgPaymentTransaction();
		tx.setTransactionCode("PGTX-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase());
		tx.setPgBookingId(booking.getId());
		tx.setPaymentMethod(booking.getPaymentMethod() != null ? booking.getPaymentMethod() : PaymentMethod.UPI);
		tx.setAmount(chargeAmount);
		tx.setPaymentStatus(PaymentTransactionStatus.PENDING);
		tx.setRazorpayOrderId(razorpayOrderId);
		pgPaymentTransactionRepository.save(tx);

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
	public PgBookingConfirmationResponse verifyPayment(Long userId, PgPaymentVerifyRequest request) {
		PgBooking booking = requireUserBooking(userId, request.getBookingId());
		String requestOrderId = request.getRazorpayOrderId();
		if (requestOrderId == null || requestOrderId.isBlank()) {
			throw new AuthException("Razorpay order ID is missing");
		}
		if (!Objects.equals(requestOrderId, booking.getRazorpayOrderId())) {
			throw new AuthException("Order mismatch — payment tampering detected");
		}

		PgPaymentTransaction tx = pgPaymentTransactionRepository.findByRazorpayOrderId(requestOrderId)
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
			paymentId = "demo_pg_pay_" + UUID.randomUUID().toString().substring(0, 12);
		}

		if (booking.getPaidAmount() != null && booking.getPaidAmount().compareTo(BigDecimal.ZERO) > 0) {
			if (tx.getAmount().compareTo(booking.getRemainingAmount()) != 0) {
				throw new AuthException("Payment amount mismatch");
			}
		} else if (tx.getAmount().compareTo(booking.getPayableNow()) != 0) {
			throw new AuthException("Payment amount mismatch");
		}

		boolean wasPartiallyPaid = booking.getPaidAmount() != null
				&& booking.getPaidAmount().compareTo(BigDecimal.ZERO) > 0;

		tx.setPaymentStatus(PaymentTransactionStatus.SUCCESS);
		tx.setPaymentDate(LocalDateTime.now());
		tx.setRazorpayPaymentId(paymentId);
		tx.setGatewayResponse(razorpayLive ? "razorpay_verified" : "demo_verified");
		pgPaymentTransactionRepository.save(tx);

		booking.setPaidAmount(booking.getPaidAmount().add(tx.getAmount()));
		booking.setRemainingAmount(booking.getTotalAmount().subtract(booking.getPaidAmount()));
		booking.setTransactionId(tx.getTransactionCode());
		if (booking.getRemainingAmount().compareTo(BigDecimal.ZERO) <= 0) {
			booking.setBookingStatus(BookingStatus.FULLY_PAID);
		} else if (booking.getPaidAmount().compareTo(BigDecimal.ZERO) > 0) {
			booking.setBookingStatus(BookingStatus.PARTIALLY_PAID);
		} else {
			booking.setBookingStatus(BookingStatus.CONFIRMED);
		}
		if (!wasPartiallyPaid) {
			booking.setOwnerApprovalStatus(PgOwnerApprovalStatus.PENDING);
			applyBedOccupancy(booking);
		}
		pgBookingRepository.save(booking);

		if (booking.getBookingStatus() == BookingStatus.FULLY_PAID) {
			pgBookingNotificationService.sendFullyPaidNotifications(booking);
			pgMonthlyRentService.activateStayIfReady(booking);
		}

		return toConfirmation(booking);
	}

	@Transactional(readOnly = true)
	public PgBookingConfirmationResponse getConfirmation(Long userId, Long bookingId) {
		return toConfirmation(requireUserBooking(userId, bookingId));
	}

	@Transactional
	public List<UserPgBookingListItemResponse> listMyBookings(Long userId) {
		return pgBookingRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
				.filter(b -> (b.getPaidAmount() != null && b.getPaidAmount().compareTo(BigDecimal.ZERO) > 0)
						|| b.getBookingStatus() == BookingStatus.PENDING)
				.map(b -> toUserListItem(pgMonthlyRentService.refreshStayState(b)))
				.collect(Collectors.toList());
	}

	private UserPgBookingListItemResponse toUserListItem(PgBooking booking) {
		PgProperty property = pgPropertyRepository.findById(booking.getPgPropertyId()).orElse(null);
		PgRoom room = pgRoomRepository.findById(booking.getRoomId()).orElse(null);
		PgOwnerApprovalStatus approval = resolveOwnerApprovalStatus(booking);
		boolean paid = booking.getPaidAmount() != null && booking.getPaidAmount().compareTo(BigDecimal.ZERO) > 0;

		UserPgBookingListItemResponse item = new UserPgBookingListItemResponse();
		item.setBookingId(booking.getId());
		item.setBookingCode(booking.getBookingCode());
		item.setStatus(booking.getBookingStatus());
		item.setStatusLabel(formatStatusLabel(booking.getBookingStatus()));
		item.setOwnerApprovalStatus(approval);
		item.setOwnerApprovalLabel(formatOwnerApprovalLabel(approval));
		item.setPgPropertyId(booking.getPgPropertyId());
		if (property != null) {
			item.setPgName(property.getPgName());
			item.setPgCode(property.getPgCode());
			StringBuilder loc = new StringBuilder();
			if (property.getCity() != null && !property.getCity().isBlank()) {
				loc.append(property.getCity().trim());
			}
			if (property.getState() != null && !property.getState().isBlank()) {
				if (loc.length() > 0) {
					loc.append(", ");
				}
				loc.append(property.getState().trim());
			}
			item.setPgLocation(loc.toString());
		}
		try {
			item.setPgImageUrl(publicPgBrowseService.getPublishedCard(booking.getPgPropertyId()).getCoverImageUrl());
		} catch (Exception ignored) {
			item.setPgImageUrl(null);
		}
		item.setRoomNumber(booking.getRoomNumber());
		if (room != null && room.getSharingType() != null) {
			item.setSharingType(room.getSharingType().name());
			item.setSharingLabel(PgOwnerPgBookingService.formatSharingLabel(room.getSharingType()));
		}
		item.setBedCount(booking.getBedCount());
		item.setBedNumbers(booking.getBedNumbers());
		item.setRentPerBed(booking.getRentPerBed());
		item.setTotalAmount(booking.getTotalAmount());
		item.setPaidAmount(booking.getPaidAmount());
		item.setPayableNow(booking.getPayableNow());
		item.setRemainingAmount(booking.getRemainingAmount());
		item.setTransactionId(booking.getTransactionId());
		item.setBookingDate(booking.getBookingDate());
		item.setPaymentReceived(paid);
		item.setOwnerApproved(approval == PgOwnerApprovalStatus.APPROVED);
		item.setOwnerRejected(approval == PgOwnerApprovalStatus.REJECTED);
		item.setCanContactOwner(approval == PgOwnerApprovalStatus.APPROVED);
		item.setPgOwnerId(booking.getPgOwnerId());
		if (booking.getPgOwnerId() != null) {
			pgOwnerRepository.findById(booking.getPgOwnerId())
					.ifPresent(o -> item.setPgOwnerName(o.getFullName()));
		}
		item.setAwaitingOwnerApproval(paid && approval == PgOwnerApprovalStatus.PENDING
				&& booking.getBookingStatus() != BookingStatus.CANCELLED);
		boolean canPayRemaining = canPayRemaining(booking);
		item.setCanPayRemaining(canPayRemaining);
		item.setSecurityDepositAmount(booking.getSecurityDepositAmount());
		item.setPaymentComplete(booking.getBookingStatus() == BookingStatus.FULLY_PAID);
		populateStayFields(item, booking);
		return item;
	}

	private void populateStayFields(UserPgBookingListItemResponse item, PgBooking booking) {
		item.setNoticePeriodDays(PgMonthlyRentService.NOTICE_PERIOD_DAYS);
		item.setMonthlyRentAmount(pgMonthlyRentService.monthlyRentAmount(booking));
		if (booking.getMoveInDate() != null) {
			item.setActiveStay(PgMonthlyRentService.isStaying(booking));
			item.setMoveInDate(booking.getMoveInDate());
			item.setNextRentDueDate(booking.getNextRentDueDate());
			item.setStayStatus(booking.getStayStatus());
			item.setStayStatusLabel(PgMonthlyRentService.formatStayStatusLabel(booking.getStayStatus()));
			item.setNoticeRequestedAt(booking.getNoticeRequestedAt());
			item.setPlannedVacateDate(booking.getPlannedVacateDate());
			item.setCanRequestVacate(booking.getStayStatus() == PgStayStatus.ACTIVE
					&& booking.getBookingStatus() == BookingStatus.FULLY_PAID);
			PgMonthlyRentDue nextDue = pgMonthlyRentService.findNextPayableDue(booking);
			if (nextDue != null) {
				item.setCurrentRentDueId(nextDue.getId());
				item.setCurrentRentDueAmount(nextDue.getAmount());
				item.setCurrentRentDuePeriod(nextDue.getPeriodLabel());
				item.setCurrentRentDueDate(nextDue.getDueDate());
				item.setCurrentRentDueStatus(nextDue.getDueStatus() != null ? nextDue.getDueStatus().name() : null);
				item.setCanPayMonthlyRent(true);
			} else if (booking.getNextRentDueDate() != null && PgMonthlyRentService.isStaying(booking)) {
				item.setCanPayMonthlyRent(false);
				item.setUpcomingRentDueDate(booking.getNextRentDueDate());
			}
		}
	}

	boolean canPayRemaining(PgBooking booking) {
		if (booking.getBookingStatus() == BookingStatus.CANCELLED
				|| booking.getBookingStatus() == BookingStatus.FULLY_PAID) {
			return false;
		}
		if (resolveOwnerApprovalStatus(booking) != PgOwnerApprovalStatus.APPROVED) {
			return false;
		}
		return booking.getPaidAmount() != null && booking.getPaidAmount().compareTo(BigDecimal.ZERO) > 0
				&& booking.getRemainingAmount() != null
				&& booking.getRemainingAmount().compareTo(BigDecimal.ZERO) > 0;
	}

	private void validateDraftRequest(PgBookingDraftRequest request, PgRoom room) {
		if (!isRoomBookable(room)) {
			throw new AuthException("Selected room is not available");
		}
		int available = availableBedCount(room);
		if (request.getBedCount() == null || request.getBedCount() < 1) {
			throw new AuthException("Select at least one bed");
		}
		if (request.getBedCount() > available) {
			throw new AuthException("Only " + available + " bed(s) available in this room");
		}
		if (request.getBedNumbers() == null || request.getBedNumbers().size() != request.getBedCount()) {
			throw new AuthException("Select bed number(s) matching the bed count");
		}
		List<Integer> validBeds = computeAvailableBedNumbers(room);
		for (Integer bed : request.getBedNumbers()) {
			if (bed == null || !validBeds.contains(bed)) {
				throw new AuthException("Bed " + bed + " is not available in this room");
			}
		}
		if (request.getOccupants() == null || request.getOccupants().size() != request.getBedCount()) {
			throw new AuthException("Provide guest details for each bed you are booking");
		}
		for (PgOccupantRequest occ : request.getOccupants()) {
			if (occ.getFullName() == null || occ.getFullName().trim().length() < 2) {
				throw new AuthException("Guest name is required for each bed");
			}
			if (occ.getMobile() == null || occ.getMobile().replaceAll("\\D", "").length() < 10) {
				throw new AuthException("Valid mobile number is required for each guest");
			}
		}
	}

	private void saveOccupants(Long bookingId, PgBookingDraftRequest request) {
		List<Integer> beds = request.getBedNumbers().stream().sorted().collect(Collectors.toList());
		for (int i = 0; i < request.getOccupants().size(); i++) {
			PgOccupantRequest occ = request.getOccupants().get(i);
			PgBookingOccupant entity = new PgBookingOccupant();
			entity.setPgBookingId(bookingId);
			entity.setOccupantIndex(i);
			entity.setBedNumber(i < beds.size() ? beds.get(i) : occ.getBedNumber());
			entity.setFullName(occ.getFullName().trim());
			entity.setMobile(occ.getMobile().trim());
			entity.setEmail(occ.getEmail() != null ? occ.getEmail().trim() : null);
			occupantRepository.save(entity);
		}
	}

	private void applyBedOccupancy(PgBooking booking) {
		PgRoom room = pgRoomRepository.findById(booking.getRoomId()).orElseThrow();
		int occupied = room.getOccupiedBeds() != null ? room.getOccupiedBeds() : 0;
		int available = room.getAvailableBeds() != null ? room.getAvailableBeds() : Math.max(0,
				(room.getTotalBeds() != null ? room.getTotalBeds() : 0) - occupied);
		int newOccupied = occupied + booking.getBedCount();
		int newAvailable = Math.max(0, available - booking.getBedCount());
		room.setOccupiedBeds(newOccupied);
		room.setAvailableBeds(newAvailable);
		if (newAvailable <= 0) {
			room.setRoomStatus(PgRoomStatus.FULLY_OCCUPIED);
		}
		pgRoomRepository.save(room);

		PgProperty property = pgPropertyRepository.findById(booking.getPgPropertyId()).orElseThrow();
		int pgAvailable = property.getAvailableBeds() != null ? property.getAvailableBeds() : 0;
		property.setAvailableBeds(Math.max(0, pgAvailable - booking.getBedCount()));
		pgPropertyRepository.save(property);
	}

	void releaseBedOccupancy(PgBooking booking) {
		PgRoom room = pgRoomRepository.findById(booking.getRoomId()).orElseThrow();
		int occupied = room.getOccupiedBeds() != null ? room.getOccupiedBeds() : 0;
		int available = room.getAvailableBeds() != null ? room.getAvailableBeds() : 0;
		int release = booking.getBedCount() != null ? booking.getBedCount() : 0;
		room.setOccupiedBeds(Math.max(0, occupied - release));
		room.setAvailableBeds(available + release);
		if (room.getRoomStatus() == PgRoomStatus.FULLY_OCCUPIED && room.getAvailableBeds() > 0) {
			room.setRoomStatus(PgRoomStatus.AVAILABLE);
		}
		pgRoomRepository.save(room);

		PgProperty property = pgPropertyRepository.findById(booking.getPgPropertyId()).orElseThrow();
		int pgAvailable = property.getAvailableBeds() != null ? property.getAvailableBeds() : 0;
		property.setAvailableBeds(pgAvailable + release);
		pgPropertyRepository.save(property);
	}

	private PgBookingCheckoutPreviewResponse toCheckoutPreview(PgBooking booking, boolean balancePayment) {
		PgRoom room = pgRoomRepository.findById(booking.getRoomId()).orElse(null);
		PgBookingCheckoutPreviewResponse response = new PgBookingCheckoutPreviewResponse();
		response.setBookingId(booking.getId());
		response.setBookingCode(booking.getBookingCode());
		response.setPg(publicPgBrowseService.getPublishedCard(booking.getPgPropertyId()));
		response.setRoomNumber(booking.getRoomNumber());
		response.setBedCount(booking.getBedCount());
		response.setBedNumbers(booking.getBedNumbers());
		response.setRentPerBed(booking.getRentPerBed());
		response.setSecurityDepositAmount(booking.getSecurityDepositAmount());
		if (room != null && room.getSharingType() != null) {
			response.setSharingLabel(PgOwnerPgBookingService.formatSharingLabel(room.getSharingType()));
		}
		response.setTotalAmount(booking.getTotalAmount());
		response.setPayableNow(balancePayment ? booking.getRemainingAmount() : booking.getPayableNow());
		response.setPaidAmount(booking.getPaidAmount());
		response.setRemainingAmount(booking.getRemainingAmount());
		response.setBalancePayment(balancePayment);
		response.setOwnerApproved(resolveOwnerApprovalStatus(booking) == PgOwnerApprovalStatus.APPROVED);
		response.setReservationMinutes(bookingProperties.getReservationMinutes());
		response.setRazorpayConfigured(razorpayPaymentService.isConfigured());
		response.setOccupants(loadOccupantSummaries(booking.getId()));
		return response;
	}

	private PgBookingConfirmationResponse toConfirmation(PgBooking booking) {
		PgProperty property = pgPropertyRepository.findById(booking.getPgPropertyId()).orElse(null);
		PgBookingConfirmationResponse response = new PgBookingConfirmationResponse();
		response.setBookingId(booking.getId());
		response.setBookingCode(booking.getBookingCode());
		response.setStatus(booking.getBookingStatus());
		response.setStatusLabel(formatStatusLabel(booking.getBookingStatus()));
		PgOwnerApprovalStatus approval = resolveOwnerApprovalStatus(booking);
		response.setOwnerApprovalStatus(approval);
		response.setOwnerApprovalLabel(formatOwnerApprovalLabel(approval));
		if (property != null) {
			response.setPgName(property.getPgName());
			response.setPgCode(property.getPgCode());
		}
		response.setRoomNumber(booking.getRoomNumber());
		response.setBedCount(booking.getBedCount());
		response.setBedNumbers(booking.getBedNumbers());
		response.setTotalAmount(booking.getTotalAmount());
		response.setPaidAmount(booking.getPaidAmount());
		response.setRemainingAmount(booking.getRemainingAmount());
		response.setTransactionCode(booking.getTransactionId());
		response.setBookingDate(booking.getBookingDate());
		response.setOccupants(loadOccupantSummaries(booking.getId()));
		return response;
	}

	private List<PgOccupantSummaryDto> loadOccupantSummaries(Long bookingId) {
		return occupantRepository.findByPgBookingIdOrderByOccupantIndexAsc(bookingId).stream().map(o -> {
			PgOccupantSummaryDto dto = new PgOccupantSummaryDto();
			dto.setFullName(o.getFullName());
			dto.setMobile(o.getMobile());
			dto.setEmail(o.getEmail());
			dto.setBedNumber(o.getBedNumber());
			return dto;
		}).collect(Collectors.toList());
	}

	private PgBookingRoomOptionResponse toRoomOption(PgRoom room, Integer floorNumber, PgProperty property) {
		PgBookingRoomOptionResponse option = new PgBookingRoomOptionResponse();
		option.setRoomId(room.getId());
		option.setRoomNumber(room.getRoomNumber());
		option.setFloorNumber(floorNumber);
		option.setSharingTypeEnum(room.getSharingType());
		option.setTotalBeds(room.getTotalBeds());
		option.setAvailableBeds(availableBedCount(room));
		option.setRentPerBed(resolveRentPerBed(room, property));
		option.setAvailableBedNumbers(computeAvailableBedNumbers(room));
		return option;
	}

	private boolean isRoomBookable(PgRoom room) {
		if (room.getRoomStatus() == PgRoomStatus.FULLY_OCCUPIED
				|| room.getRoomStatus() == PgRoomStatus.UNDER_MAINTENANCE) {
			return false;
		}
		return availableBedCount(room) > 0;
	}

	private int availableBedCount(PgRoom room) {
		if (room.getAvailableBeds() != null && room.getAvailableBeds() > 0) {
			return room.getAvailableBeds();
		}
		int total = room.getTotalBeds() != null ? room.getTotalBeds() : 0;
		int occupied = room.getOccupiedBeds() != null ? room.getOccupiedBeds() : 0;
		return Math.max(0, total - occupied);
	}

	private List<Integer> computeAvailableBedNumbers(PgRoom room) {
		int total = room.getTotalBeds() != null && room.getTotalBeds() > 0 ? room.getTotalBeds() : availableBedCount(room);
		if (total < 1) {
			total = Math.max(1, availableBedCount(room));
		}
		int occupied = room.getOccupiedBeds() != null ? room.getOccupiedBeds() : 0;
		int available = availableBedCount(room);
		if (available <= 0) {
			return List.of();
		}
		return IntStream.rangeClosed(1, total).boxed()
				.sorted(Comparator.naturalOrder())
				.skip(Math.max(0, occupied))
				.limit(available)
				.collect(Collectors.toList());
	}

	private BigDecimal resolveRentPerBed(PgRoom room, PgProperty property) {
		if (room.getRoomPrice() != null && room.getRoomPrice().compareTo(BigDecimal.ZERO) > 0) {
			return room.getRoomPrice();
		}
		if (room.getSharingType() != null) {
			List<PgRoomSharing> sharing = pgRoomSharingRepository.findByPgPropertyId(property.getId());
			for (PgRoomSharing sp : sharing) {
				if (sp.getSharingType() == room.getSharingType() && sp.getMonthlyRent() != null
						&& sp.getMonthlyRent().compareTo(BigDecimal.ZERO) > 0) {
					return sp.getMonthlyRent();
				}
			}
		}
		if (property.getMonthlyRent() != null && property.getMonthlyRent().compareTo(BigDecimal.ZERO) > 0) {
			return property.getMonthlyRent();
		}
		return BigDecimal.valueOf(5000);
	}

	private BigDecimal resolvePayableNow(PgProperty property, BigDecimal monthlyRentTotal, int bedCount) {
		if (property.getBookingAmount() != null && property.getBookingAmount().compareTo(BigDecimal.ZERO) > 0) {
			return property.getBookingAmount().multiply(BigDecimal.valueOf(bedCount)).setScale(2, RoundingMode.HALF_UP);
		}
		return monthlyRentTotal.multiply(bookingProperties.getAdvancePercent())
				.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
	}

	private BigDecimal resolveSecurityDepositTotal(PgProperty property, int bedCount) {
		if (property.getSecurityDeposit() != null && property.getSecurityDeposit().compareTo(BigDecimal.ZERO) > 0) {
			return property.getSecurityDeposit().multiply(BigDecimal.valueOf(bedCount)).setScale(2,
					RoundingMode.HALF_UP);
		}
		return BigDecimal.ZERO;
	}

	private PgProperty requirePublishedPg(Long pgPropertyId) {
		return pgPropertyRepository.findById(pgPropertyId)
				.filter(p -> p.getStatus() == PgPropertyStatus.PUBLISHED)
				.orElseThrow(() -> new AuthException("PG not found or not available for booking"));
	}

	private PgBooking requireUserBooking(Long userId, Long bookingId) {
		return pgBookingRepository.findByIdAndUserId(bookingId, userId)
				.orElseThrow(() -> new AuthException("Booking not found"));
	}

	private static String generateBookingCode() {
		return "PGB-" + UUID.randomUUID().toString().substring(0, 10).toUpperCase();
	}

	private static String formatStatusLabel(BookingStatus status) {
		if (status == null) {
			return "Unknown";
		}
		return switch (status) {
		case PENDING -> "Awaiting payment";
		case CONFIRMED -> "Confirmed";
		case PARTIALLY_PAID -> "Partially paid";
		case FULLY_PAID -> "Fully paid";
		case EMI_ACTIVE -> "EMI active";
		case CANCELLED -> "Cancelled";
		};
	}

	static PgOwnerApprovalStatus resolveOwnerApprovalStatus(PgBooking booking) {
		if (booking.getOwnerApprovalStatus() != null) {
			return booking.getOwnerApprovalStatus();
		}
		if (booking.getPaidAmount() != null && booking.getPaidAmount().compareTo(BigDecimal.ZERO) > 0
				&& booking.getBookingStatus() != BookingStatus.CANCELLED) {
			return PgOwnerApprovalStatus.PENDING;
		}
		return PgOwnerApprovalStatus.APPROVED;
	}

	static String formatOwnerApprovalLabel(PgOwnerApprovalStatus status) {
		if (status == null) {
			return "Unknown";
		}
		return switch (status) {
		case PENDING -> "Awaiting your approval";
		case APPROVED -> "Approved by PG owner";
		case REJECTED -> "Rejected by PG owner";
		};
	}

	private static String formatMobileForRazorpay(String mobile) {
		if (mobile == null) {
			return "";
		}
		String digits = mobile.replaceAll("\\D", "");
		if (digits.length() == 10) {
			return "+91" + digits;
		}
		return digits.startsWith("+") ? digits : "+" + digits;
	}
}
