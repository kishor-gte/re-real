package com.realestate.main.service.booking;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.realestate.main.dto.pgbooking.PgOccupantSummaryDto;
import com.realestate.main.dto.pgbooking.PgOwnerBookingListItemResponse;
import com.realestate.main.dto.pgbooking.PgOwnerBookingRejectRequest;
import com.realestate.main.entity.PgBooking;
import com.realestate.main.entity.PgBookingOccupant;
import com.realestate.main.entity.PgProperty;
import com.realestate.main.entity.PgRoom;
import com.realestate.main.entity.User;
import com.realestate.main.entity.enums.BookingStatus;
import com.realestate.main.entity.enums.PgOwnerApprovalStatus;
import com.realestate.main.entity.enums.PgSharingType;
import com.realestate.main.exception.AuthException;
import com.realestate.main.repository.PgBookingOccupantRepository;
import com.realestate.main.repository.PgBookingRepository;
import com.realestate.main.repository.PgPropertyRepository;
import com.realestate.main.repository.PgRoomRepository;
import com.realestate.main.repository.UserRepository;

@Service
public class PgOwnerPgBookingService {

	private final PgBookingRepository pgBookingRepository;
	private final PgBookingOccupantRepository occupantRepository;
	private final PgPropertyRepository pgPropertyRepository;
	private final PgRoomRepository pgRoomRepository;
	private final UserRepository userRepository;
	private final PgBookingService pgBookingService;
	private final PgMonthlyRentService pgMonthlyRentService;

	public PgOwnerPgBookingService(PgBookingRepository pgBookingRepository,
			PgBookingOccupantRepository occupantRepository, PgPropertyRepository pgPropertyRepository,
			PgRoomRepository pgRoomRepository, UserRepository userRepository, PgBookingService pgBookingService,
			@Lazy PgMonthlyRentService pgMonthlyRentService) {
		this.pgBookingRepository = pgBookingRepository;
		this.occupantRepository = occupantRepository;
		this.pgPropertyRepository = pgPropertyRepository;
		this.pgRoomRepository = pgRoomRepository;
		this.userRepository = userRepository;
		this.pgBookingService = pgBookingService;
		this.pgMonthlyRentService = pgMonthlyRentService;
	}

	@Transactional(readOnly = true)
	public List<PgOwnerBookingListItemResponse> listForOwner(Long pgOwnerId) {
		return pgBookingRepository.findByPgOwnerIdOrderByCreatedAtDesc(pgOwnerId).stream()
				.filter(this::isVisibleToOwner)
				.map(this::toListItem)
				.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public long countPendingApprovals(Long pgOwnerId) {
		return pgBookingRepository.countPendingOwnerApprovals(pgOwnerId);
	}

	@Transactional
	public PgOwnerBookingListItemResponse approve(Long pgOwnerId, Long bookingId) {
		PgBooking booking = requireOwnerBooking(pgOwnerId, bookingId);
		requireAwaitingApproval(booking);
		booking.setOwnerApprovalStatus(PgOwnerApprovalStatus.APPROVED);
		booking.setOwnerRespondedAt(LocalDateTime.now());
		booking.setOwnerRejectionReason(null);
		pgBookingRepository.save(booking);
		pgMonthlyRentService.activateStayIfReady(booking);
		return toListItem(booking);
	}

	@Transactional
	public PgOwnerBookingListItemResponse reject(Long pgOwnerId, Long bookingId, PgOwnerBookingRejectRequest request) {
		PgBooking booking = requireOwnerBooking(pgOwnerId, bookingId);
		requireAwaitingApproval(booking);
		booking.setOwnerApprovalStatus(PgOwnerApprovalStatus.REJECTED);
		booking.setOwnerRespondedAt(LocalDateTime.now());
		booking.setOwnerRejectionReason(request != null && request.getReason() != null
				? request.getReason().trim()
				: null);
		booking.setBookingStatus(BookingStatus.CANCELLED);
		pgBookingRepository.save(booking);
		pgBookingService.releaseBedOccupancy(booking);
		return toListItem(booking);
	}

	private boolean isVisibleToOwner(PgBooking booking) {
		return booking.getPaidAmount() != null && booking.getPaidAmount().compareTo(BigDecimal.ZERO) > 0;
	}

	private void requireAwaitingApproval(PgBooking booking) {
		PgOwnerApprovalStatus status = PgBookingService.resolveOwnerApprovalStatus(booking);
		if (status != PgOwnerApprovalStatus.PENDING) {
			throw new AuthException("This booking has already been reviewed");
		}
		if (booking.getBookingStatus() == BookingStatus.CANCELLED) {
			throw new AuthException("Booking is cancelled");
		}
	}

	private PgBooking requireOwnerBooking(Long pgOwnerId, Long bookingId) {
		PgBooking booking = pgBookingRepository.findByIdAndPgOwnerId(bookingId, pgOwnerId)
				.orElseThrow(() -> new AuthException("Booking not found"));
		if (!isVisibleToOwner(booking)) {
			throw new AuthException("Booking is not available for review");
		}
		return booking;
	}

	private PgOwnerBookingListItemResponse toListItem(PgBooking booking) {
		PgProperty property = pgPropertyRepository.findById(booking.getPgPropertyId()).orElse(null);
		PgRoom room = pgRoomRepository.findById(booking.getRoomId()).orElse(null);
		User user = userRepository.findById(booking.getUserId()).orElse(null);
		PgOwnerApprovalStatus approval = PgBookingService.resolveOwnerApprovalStatus(booking);

		PgOwnerBookingListItemResponse item = new PgOwnerBookingListItemResponse();
		item.setBookingId(booking.getId());
		item.setBookingCode(booking.getBookingCode());
		item.setBookingStatus(booking.getBookingStatus());
		item.setBookingStatusLabel(formatBookingStatusLabel(booking.getBookingStatus()));
		item.setOwnerApprovalStatus(approval);
		item.setOwnerApprovalLabel(PgBookingService.formatOwnerApprovalLabel(approval));
		item.setPgPropertyId(booking.getPgPropertyId());
		if (property != null) {
			item.setPgName(property.getPgName());
			item.setPgCode(property.getPgCode());
		}
		item.setRoomNumber(booking.getRoomNumber());
		item.setBedCount(booking.getBedCount());
		item.setBedNumbers(booking.getBedNumbers());
		if (room != null && room.getSharingType() != null) {
			item.setSharingType(room.getSharingType().name());
			item.setSharingLabel(formatSharingLabel(room.getSharingType()));
		}
		item.setRentPerBed(booking.getRentPerBed());
		item.setSecurityDepositAmount(booking.getSecurityDepositAmount());
		item.setPayableNow(booking.getPayableNow());
		item.setTotalAmount(booking.getTotalAmount());
		item.setPaidAmount(booking.getPaidAmount());
		item.setRemainingAmount(booking.getRemainingAmount());
		item.setPaymentComplete(booking.getBookingStatus() == BookingStatus.FULLY_PAID);
		item.setBookingDate(booking.getBookingDate());
		item.setUserId(booking.getUserId());
		if (user != null) {
			item.setUserName(user.getFullName());
			item.setUserEmail(user.getEmail());
			item.setUserMobile(user.getMobile());
		}
		item.setAwaitingOwnerAction(approval == PgOwnerApprovalStatus.PENDING
				&& booking.getBookingStatus() != BookingStatus.CANCELLED);
		item.setOccupants(loadOccupants(booking.getId()));
		return item;
	}

	private List<PgOccupantSummaryDto> loadOccupants(Long bookingId) {
		return occupantRepository.findByPgBookingIdOrderByOccupantIndexAsc(bookingId).stream().map(this::toOccupantDto)
				.collect(Collectors.toList());
	}

	private PgOccupantSummaryDto toOccupantDto(PgBookingOccupant occupant) {
		PgOccupantSummaryDto dto = new PgOccupantSummaryDto();
		dto.setFullName(occupant.getFullName());
		dto.setMobile(occupant.getMobile());
		dto.setEmail(occupant.getEmail());
		dto.setBedNumber(occupant.getBedNumber());
		return dto;
	}

	private static String formatBookingStatusLabel(BookingStatus status) {
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

	static String formatSharingLabel(PgSharingType type) {
		if (type == null) {
			return "—";
		}
		return switch (type) {
		case SINGLE -> "Single sharing";
		case DOUBLE -> "Double sharing";
		case TRIPLE -> "Triple sharing";
		case FOUR -> "4-sharing";
		case FIVE -> "5-sharing";
		};
	}
}
