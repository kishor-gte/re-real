package com.realestate.main.rtc.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.realestate.main.entity.PgBooking;
import com.realestate.main.entity.PgProperty;
import com.realestate.main.entity.Property;
import com.realestate.main.entity.PropertyBooking;
import com.realestate.main.entity.PropertyEnquiry;
import com.realestate.main.entity.enums.BookingStatus;
import com.realestate.main.entity.enums.PgOwnerApprovalStatus;
import com.realestate.main.exception.AuthException;
import com.realestate.main.repository.PgBookingRepository;
import com.realestate.main.repository.PgPropertyRepository;
import com.realestate.main.repository.PropertyBookingRepository;
import com.realestate.main.repository.PropertyEnquiryRepository;
import com.realestate.main.repository.PropertyRepository;
import com.realestate.main.rtc.dto.RtcPrincipal;
import com.realestate.main.rtc.enums.RtcParticipantType;

@Service
public class CommunicationAccessService {

	private final PropertyEnquiryRepository enquiryRepository;
	private final PropertyBookingRepository bookingRepository;
	private final PropertyRepository propertyRepository;
	private final PgBookingRepository pgBookingRepository;
	private final PgPropertyRepository pgPropertyRepository;

	public CommunicationAccessService(PropertyEnquiryRepository enquiryRepository,
			PropertyBookingRepository bookingRepository, PropertyRepository propertyRepository,
			PgBookingRepository pgBookingRepository, PgPropertyRepository pgPropertyRepository) {
		this.enquiryRepository = enquiryRepository;
		this.bookingRepository = bookingRepository;
		this.propertyRepository = propertyRepository;
		this.pgBookingRepository = pgBookingRepository;
		this.pgPropertyRepository = pgPropertyRepository;
	}

	public EnquiryContext requireEnquiryAccess(Long enquiryId, RtcPrincipal principal) {
		PropertyEnquiry enquiry = enquiryRepository.findById(enquiryId)
				.orElseThrow(() -> new AuthException("Enquiry not found"));
		assertAgentUserParticipant(enquiry.getAgentId(), enquiry.getUserId(), principal);
		Property property = propertyRepository.findById(enquiry.getPropertyId())
				.orElseThrow(() -> new AuthException("Property not found"));
		return new EnquiryContext(enquiry, property.getTitle());
	}

	public BookingContext requireBookingAccess(Long bookingId, RtcPrincipal principal) {
		PropertyBooking booking = bookingRepository.findById(bookingId)
				.orElseThrow(() -> new AuthException("Booking not found"));
		assertAgentUserParticipant(booking.getAgentId(), booking.getUserId(), principal);
		Property property = propertyRepository.findById(booking.getPropertyId())
				.orElseThrow(() -> new AuthException("Property not found"));
		return new BookingContext(booking, property.getTitle());
	}

	public PgBookingContext requirePgBookingAccess(Long pgBookingId, RtcPrincipal principal) {
		PgBooking booking = pgBookingRepository.findById(pgBookingId)
				.orElseThrow(() -> new AuthException("PG booking not found"));
		if (resolvePgOwnerApproval(booking) != PgOwnerApprovalStatus.APPROVED) {
			throw new AuthException("Chat and voice calls are available after the PG owner accepts the booking");
		}
		assertPgOwnerUserParticipant(booking.getPgOwnerId(), booking.getUserId(), principal);
		PgProperty property = pgPropertyRepository.findById(booking.getPgPropertyId())
				.orElseThrow(() -> new AuthException("PG property not found"));
		String title = property.getPgName() != null ? property.getPgName() : "PG";
		return new PgBookingContext(booking, title);
	}

	private void assertAgentUserParticipant(Long agentId, Long userId, RtcPrincipal principal) {
		if (principal.getType() == RtcParticipantType.AGENT && !agentId.equals(principal.getId())) {
			throw new AuthException("You cannot access this conversation");
		}
		if (principal.getType() == RtcParticipantType.USER && !userId.equals(principal.getId())) {
			throw new AuthException("You cannot access this conversation");
		}
		if (principal.getType() == RtcParticipantType.PG_OWNER) {
			throw new AuthException("You cannot access this conversation");
		}
	}

	private void assertPgOwnerUserParticipant(Long pgOwnerId, Long userId, RtcPrincipal principal) {
		if (principal.getType() == RtcParticipantType.PG_OWNER && !pgOwnerId.equals(principal.getId())) {
			throw new AuthException("You cannot access this conversation");
		}
		if (principal.getType() == RtcParticipantType.USER && !userId.equals(principal.getId())) {
			throw new AuthException("You cannot access this conversation");
		}
		if (principal.getType() == RtcParticipantType.AGENT) {
			throw new AuthException("You cannot access this conversation");
		}
	}

	public record EnquiryContext(PropertyEnquiry enquiry, String propertyTitle) {
	}

	public record BookingContext(PropertyBooking booking, String propertyTitle) {
	}

	public record PgBookingContext(PgBooking booking, String propertyTitle) {
	}

	private static PgOwnerApprovalStatus resolvePgOwnerApproval(PgBooking booking) {
		if (booking.getOwnerApprovalStatus() != null) {
			return booking.getOwnerApprovalStatus();
		}
		if (booking.getPaidAmount() != null && booking.getPaidAmount().compareTo(BigDecimal.ZERO) > 0
				&& booking.getBookingStatus() != BookingStatus.CANCELLED) {
			return PgOwnerApprovalStatus.PENDING;
		}
		return PgOwnerApprovalStatus.APPROVED;
	}
}
