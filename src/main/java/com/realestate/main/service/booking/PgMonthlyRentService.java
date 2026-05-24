package com.realestate.main.service.booking;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.realestate.main.config.RazorpayProperties;
import com.realestate.main.dto.booking.RazorpayOrderResponse;
import com.realestate.main.dto.pgbooking.PgMonthlyRentDueResponse;
import com.realestate.main.dto.pgbooking.PgRentPaymentPreviewResponse;
import com.realestate.main.dto.pgbooking.PgRentPaymentVerifyRequest;
import com.realestate.main.dto.pgbooking.PgVacateNoticeRequest;
import com.realestate.main.dto.pgbooking.PgVacateNoticeResponse;
import com.realestate.main.entity.PgBooking;
import com.realestate.main.entity.PgMonthlyRentDue;
import com.realestate.main.entity.PgProperty;
import com.realestate.main.entity.User;
import com.realestate.main.entity.enums.BookingStatus;
import com.realestate.main.entity.enums.PgOwnerApprovalStatus;
import com.realestate.main.entity.enums.PgRentDueStatus;
import com.realestate.main.entity.enums.PgStayStatus;
import com.realestate.main.exception.AuthException;
import com.realestate.main.repository.PgBookingRepository;
import com.realestate.main.repository.PgMonthlyRentDueRepository;
import com.realestate.main.repository.PgPropertyRepository;
import com.realestate.main.service.UserService;

@Service
public class PgMonthlyRentService {

	public static final int NOTICE_PERIOD_DAYS = 30;

	private static final DateTimeFormatter PERIOD_FMT = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH);

	private final PgBookingRepository pgBookingRepository;
	private final PgMonthlyRentDueRepository rentDueRepository;
	private final PgPropertyRepository pgPropertyRepository;
	private final PgBookingService pgBookingService;
	private final RazorpayPaymentService razorpayPaymentService;
	private final RazorpayProperties razorpayProperties;
	private final UserService userService;

	public PgMonthlyRentService(PgBookingRepository pgBookingRepository, PgMonthlyRentDueRepository rentDueRepository,
			PgPropertyRepository pgPropertyRepository, @Lazy PgBookingService pgBookingService,
			RazorpayPaymentService razorpayPaymentService, RazorpayProperties razorpayProperties,
			UserService userService) {
		this.pgBookingRepository = pgBookingRepository;
		this.rentDueRepository = rentDueRepository;
		this.pgPropertyRepository = pgPropertyRepository;
		this.pgBookingService = pgBookingService;
		this.razorpayPaymentService = razorpayPaymentService;
		this.razorpayProperties = razorpayProperties;
		this.userService = userService;
	}

	@Transactional
	public void activateStayIfReady(PgBooking booking) {
		if (booking.getBookingStatus() != BookingStatus.FULLY_PAID) {
			return;
		}
		if (PgBookingService.resolveOwnerApprovalStatus(booking) != PgOwnerApprovalStatus.APPROVED) {
			return;
		}
		if (booking.getMoveInDate() != null) {
			return;
		}
		LocalDate today = LocalDate.now();
		booking.setMoveInDate(today);
		booking.setStayStatus(PgStayStatus.ACTIVE);
		booking.setNextRentDueDate(today.plusMonths(1));
		pgBookingRepository.save(booking);
	}

	@Transactional
	public PgBooking refreshStayState(PgBooking booking) {
		if (booking.getStayStatus() == PgStayStatus.VACATED) {
			return booking;
		}
		if (booking.getMoveInDate() == null && booking.getBookingStatus() == BookingStatus.FULLY_PAID
				&& PgBookingService.resolveOwnerApprovalStatus(booking) == PgOwnerApprovalStatus.APPROVED) {
			activateStayIfReady(booking);
			booking = pgBookingRepository.findById(booking.getId()).orElse(booking);
		}
		if (booking.getStayStatus() == PgStayStatus.NOTICE_PERIOD && booking.getPlannedVacateDate() != null
				&& !LocalDate.now().isBefore(booking.getPlannedVacateDate())) {
			booking.setStayStatus(PgStayStatus.VACATED);
			pgBookingRepository.save(booking);
			pgBookingService.releaseBedOccupancy(booking);
			return booking;
		}
		if (booking.getMoveInDate() != null && isStaying(booking)) {
			syncRentDues(booking);
		}
		return pgBookingRepository.findById(booking.getId()).orElse(booking);
	}

	@Transactional(readOnly = true)
	public PgRentPaymentPreviewResponse rentCheckoutPreview(Long userId, Long rentDueId) {
		PgMonthlyRentDue due = requirePayableDue(userId, rentDueId);
		PgBooking booking = pgBookingRepository.findById(due.getPgBookingId()).orElseThrow();
		PgProperty property = pgPropertyRepository.findById(booking.getPgPropertyId()).orElse(null);

		PgRentPaymentPreviewResponse preview = new PgRentPaymentPreviewResponse();
		preview.setRentDueId(due.getId());
		preview.setBookingId(booking.getId());
		preview.setBookingCode(booking.getBookingCode());
		if (property != null) {
			preview.setPgName(property.getPgName());
			preview.setPgLocation(formatLocation(property));
		}
		preview.setRoomNumber(booking.getRoomNumber());
		preview.setPeriodLabel(due.getPeriodLabel());
		preview.setDueDate(due.getDueDate());
		preview.setAmount(due.getAmount());
		preview.setMonthlyRentAmount(monthlyRentAmount(booking));
		preview.setRazorpayConfigured(razorpayPaymentService.isConfigured());
		return preview;
	}

	@Transactional(readOnly = true)
	public List<PgMonthlyRentDueResponse> listRentDues(Long userId, Long bookingId) {
		PgBooking booking = requireActiveStayBooking(userId, bookingId);
		return rentDueRepository.findByPgBookingIdOrderByDueDateAsc(booking.getId()).stream()
				.map(this::toDueResponse)
				.collect(Collectors.toList());
	}

	@Transactional
	public PgVacateNoticeResult requestVacate(Long userId, Long bookingId, PgVacateNoticeRequest request) {
		PgBooking booking = requireActiveStayBooking(userId, bookingId);
		if (booking.getStayStatus() == PgStayStatus.NOTICE_PERIOD) {
			throw new AuthException("Vacate notice already submitted");
		}
		if (booking.getStayStatus() == PgStayStatus.VACATED) {
			throw new AuthException("You have already vacated this PG");
		}
		LocalDate vacateDate = LocalDate.now().plusDays(NOTICE_PERIOD_DAYS);
		booking.setNoticeRequestedAt(LocalDateTime.now());
		booking.setPlannedVacateDate(vacateDate);
		booking.setStayStatus(PgStayStatus.NOTICE_PERIOD);
		pgBookingRepository.save(booking);
		return new PgVacateNoticeResult(vacateDate, NOTICE_PERIOD_DAYS,
				request != null ? request.getReason() : null);
	}

	@Transactional
	public RazorpayOrderResponse createRentPaymentOrder(Long userId, Long rentDueId) {
		PgMonthlyRentDue due = rentDueRepository.findById(rentDueId)
				.orElseThrow(() -> new AuthException("Rent due not found"));
		PgBooking booking = pgBookingRepository.findByIdAndUserId(due.getPgBookingId(), userId)
				.orElseThrow(() -> new AuthException("Unauthorized"));
		if (!isStaying(booking)) {
			throw new AuthException("PG stay is not active");
		}
		if (due.getDueStatus() == PgRentDueStatus.PAID) {
			throw new AuthException("This month's rent is already paid");
		}
		User user = userService.getById(userId);
		String orderId = razorpayPaymentService.createOrder(due.getAmount(),
				booking.getBookingCode() + "-RENT-" + due.getDueDate());
		due.setRazorpayOrderId(orderId);
		rentDueRepository.save(due);

		RazorpayOrderResponse response = new RazorpayOrderResponse();
		response.setBookingId(booking.getId());
		response.setBookingCode(booking.getBookingCode());
		response.setRazorpayOrderId(orderId);
		response.setRazorpayKeyId(razorpayPaymentService.getKeyId());
		response.setAmount(due.getAmount());
		response.setAmountPaise(RazorpayPaymentService.toPaise(due.getAmount()));
		response.setCurrency(razorpayProperties.getCurrency());
		response.setDemoMode(!razorpayPaymentService.isConfigured());
		response.setUserName(user.getFullName());
		response.setUserEmail(user.getEmail());
		response.setUserMobile(formatMobile(user.getMobile()));
		response.setBalancePayment(false);
		return response;
	}

	@Transactional
	public PgMonthlyRentDueResponse verifyRentPayment(Long userId, PgRentPaymentVerifyRequest request) {
		PgMonthlyRentDue due = rentDueRepository.findById(request.getRentDueId())
				.orElseThrow(() -> new AuthException("Rent due not found"));
		pgBookingRepository.findByIdAndUserId(due.getPgBookingId(), userId)
				.orElseThrow(() -> new AuthException("Unauthorized"));

		if (due.getDueStatus() == PgRentDueStatus.PAID) {
			return toDueResponse(due);
		}
		if (!Objects.equals(request.getRazorpayOrderId(), due.getRazorpayOrderId())) {
			throw new AuthException("Order mismatch");
		}

		boolean razorpayLive = razorpayPaymentService.isConfigured();
		if (razorpayLive) {
			if (request.getRazorpayPaymentId() == null || request.getRazorpaySignature() == null) {
				throw new AuthException("Payment details incomplete");
			}
			razorpayPaymentService.verifySignature(request.getRazorpayOrderId(), request.getRazorpayPaymentId(),
					request.getRazorpaySignature());
		}

		due.setDueStatus(PgRentDueStatus.PAID);
		due.setPaidAt(LocalDateTime.now());
		due.setTransactionCode("PGRENT-" + UUID.randomUUID().toString().substring(0, 10).toUpperCase());
		rentDueRepository.save(due);
		return toDueResponse(due);
	}

	public BigDecimal monthlyRentAmount(PgBooking booking) {
		int beds = booking.getBedCount() != null ? booking.getBedCount() : 1;
		BigDecimal rent = booking.getRentPerBed() != null ? booking.getRentPerBed() : BigDecimal.ZERO;
		return rent.multiply(BigDecimal.valueOf(beds)).setScale(2, RoundingMode.HALF_UP);
	}

	public PgMonthlyRentDue findNextPayableDue(PgBooking booking) {
		return rentDueRepository
				.findFirstByPgBookingIdAndDueStatusInOrderByDueDateAsc(booking.getId(),
						List.of(PgRentDueStatus.PENDING, PgRentDueStatus.OVERDUE))
				.orElse(null);
	}

	void syncRentDues(PgBooking booking) {
		LocalDate today = LocalDate.now();
		LocalDate cursor = booking.getNextRentDueDate();
		if (cursor == null && booking.getMoveInDate() != null) {
			cursor = booking.getMoveInDate().plusMonths(1);
		}
		if (cursor == null) {
			return;
		}
		while (cursor != null && !cursor.isAfter(today)) {
			if (booking.getPlannedVacateDate() != null && cursor.isAfter(booking.getPlannedVacateDate())) {
				break;
			}
			createDueIfMissing(booking, cursor);
			cursor = cursor.plusMonths(1);
		}
		booking.setNextRentDueDate(cursor);
		pgBookingRepository.save(booking);
		markOverdue(booking.getId());
	}

	private void createDueIfMissing(PgBooking booking, LocalDate dueDate) {
		if (rentDueRepository.findByPgBookingIdAndDueDate(booking.getId(), dueDate).isPresent()) {
			return;
		}
		PgMonthlyRentDue due = new PgMonthlyRentDue();
		due.setPgBookingId(booking.getId());
		due.setDueDate(dueDate);
		due.setPeriodLabel(dueDate.format(PERIOD_FMT));
		due.setAmount(monthlyRentAmount(booking));
		due.setDueStatus(PgRentDueStatus.PENDING);
		rentDueRepository.save(due);
	}

	private void markOverdue(Long bookingId) {
		LocalDate today = LocalDate.now();
		for (PgMonthlyRentDue due : rentDueRepository.findByPgBookingIdOrderByDueDateAsc(bookingId)) {
			if (due.getDueStatus() == PgRentDueStatus.PENDING && due.getDueDate().isBefore(today)) {
				due.setDueStatus(PgRentDueStatus.OVERDUE);
				rentDueRepository.save(due);
			}
		}
	}

	public PgVacateNoticeResponse toVacateResponse(PgVacateNoticeResult result) {
		PgVacateNoticeResponse response = new PgVacateNoticeResponse();
		response.setPlannedVacateDate(result.getPlannedVacateDate());
		response.setNoticePeriodDays(result.getNoticePeriodDays());
		response.setMessage("Your vacate notice is recorded. You must vacate on or before "
				+ result.getPlannedVacateDate() + " (" + result.getNoticePeriodDays() + "-day notice period).");
		return response;
	}

	private PgMonthlyRentDue requirePayableDue(Long userId, Long rentDueId) {
		PgMonthlyRentDue due = rentDueRepository.findById(rentDueId)
				.orElseThrow(() -> new AuthException("Rent due not found"));
		PgBooking booking = pgBookingRepository.findByIdAndUserId(due.getPgBookingId(), userId)
				.orElseThrow(() -> new AuthException("Unauthorized"));
		if (!isStaying(booking)) {
			throw new AuthException("PG stay is not active");
		}
		if (due.getDueStatus() == PgRentDueStatus.PAID) {
			throw new AuthException("This month's rent is already paid");
		}
		return due;
	}

	private static String formatLocation(PgProperty property) {
		String city = property.getCity();
		String state = property.getState();
		if (city != null && state != null) {
			return city + ", " + state;
		}
		return city != null ? city : (state != null ? state : "");
	}

	private PgBooking requireActiveStayBooking(Long userId, Long bookingId) {
		PgBooking booking = pgBookingRepository.findByIdAndUserId(bookingId, userId)
				.orElseThrow(() -> new AuthException("Booking not found"));
		if (booking.getMoveInDate() == null) {
			throw new AuthException("PG stay has not started yet");
		}
		return refreshStayState(booking);
	}

	static boolean isStaying(PgBooking booking) {
		return booking.getStayStatus() == PgStayStatus.ACTIVE
				|| booking.getStayStatus() == PgStayStatus.NOTICE_PERIOD;
	}

	static String formatStayStatusLabel(PgStayStatus status) {
		if (status == null) {
			return "Not started";
		}
		return switch (status) {
		case ACTIVE -> "Active stay";
		case NOTICE_PERIOD -> "Notice period (leaving soon)";
		case VACATED -> "Vacated";
		};
	}

	private PgMonthlyRentDueResponse toDueResponse(PgMonthlyRentDue due) {
		PgMonthlyRentDueResponse r = new PgMonthlyRentDueResponse();
		r.setRentDueId(due.getId());
		r.setBookingId(due.getPgBookingId());
		r.setDueDate(due.getDueDate());
		r.setPeriodLabel(due.getPeriodLabel());
		r.setAmount(due.getAmount());
		r.setDueStatus(due.getDueStatus());
		r.setDueStatusLabel(formatDueStatus(due.getDueStatus()));
		r.setOverdue(due.getDueStatus() == PgRentDueStatus.OVERDUE);
		return r;
	}

	private static String formatDueStatus(PgRentDueStatus status) {
		if (status == null) {
			return "Unknown";
		}
		return switch (status) {
		case PENDING -> "Due";
		case PAID -> "Paid";
		case OVERDUE -> "Overdue";
		};
	}

	private static String formatMobile(String mobile) {
		if (mobile == null) {
			return "";
		}
		String digits = mobile.replaceAll("\\D", "");
		if (digits.length() == 10) {
			return "+91" + digits;
		}
		return digits.startsWith("+") ? digits : "+" + digits;
	}

	public static class PgVacateNoticeResult {
		private final LocalDate plannedVacateDate;
		private final int noticePeriodDays;
		private final String reason;

		public PgVacateNoticeResult(LocalDate plannedVacateDate, int noticePeriodDays, String reason) {
			this.plannedVacateDate = plannedVacateDate;
			this.noticePeriodDays = noticePeriodDays;
			this.reason = reason;
		}

		public LocalDate getPlannedVacateDate() {
			return plannedVacateDate;
		}

		public int getNoticePeriodDays() {
			return noticePeriodDays;
		}

		public String getReason() {
			return reason;
		}
	}
}
