package com.realestate.main.service.booking;

import java.math.BigDecimal;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.realestate.main.entity.PgBooking;
import com.realestate.main.entity.PgBookingOccupant;
import com.realestate.main.entity.PgOwner;
import com.realestate.main.entity.PgProperty;
import com.realestate.main.entity.PgRoom;
import com.realestate.main.entity.User;
import com.realestate.main.repository.PgBookingOccupantRepository;
import com.realestate.main.repository.PgOwnerRepository;
import com.realestate.main.repository.PgPropertyRepository;
import com.realestate.main.repository.PgRoomRepository;
import com.realestate.main.repository.UserRepository;
import com.realestate.main.service.EmailService;

@Service
public class PgBookingNotificationService {

	private final EmailService emailService;
	private final UserRepository userRepository;
	private final PgOwnerRepository pgOwnerRepository;
	private final PgPropertyRepository pgPropertyRepository;
	private final PgRoomRepository pgRoomRepository;
	private final PgBookingOccupantRepository occupantRepository;

	public PgBookingNotificationService(EmailService emailService, UserRepository userRepository,
			PgOwnerRepository pgOwnerRepository, PgPropertyRepository pgPropertyRepository,
			PgRoomRepository pgRoomRepository, PgBookingOccupantRepository occupantRepository) {
		this.emailService = emailService;
		this.userRepository = userRepository;
		this.pgOwnerRepository = pgOwnerRepository;
		this.pgPropertyRepository = pgPropertyRepository;
		this.pgRoomRepository = pgRoomRepository;
		this.occupantRepository = occupantRepository;
	}

	@Async
	public void sendFullyPaidNotifications(PgBooking booking) {
		User user = userRepository.findById(booking.getUserId()).orElse(null);
		PgOwner owner = pgOwnerRepository.findById(booking.getPgOwnerId()).orElse(null);
		PgProperty property = pgPropertyRepository.findById(booking.getPgPropertyId()).orElse(null);
		PgRoom room = pgRoomRepository.findById(booking.getRoomId()).orElse(null);
		if (user == null || owner == null || property == null) {
			return;
		}
		String sharing = room != null && room.getSharingType() != null
				? PgOwnerPgBookingService.formatSharingLabel(room.getSharingType())
				: "—";
		String guests = occupantRepository.findByPgBookingIdOrderByOccupantIndexAsc(booking.getId()).stream()
				.map(PgBookingOccupant::getFullName)
				.collect(Collectors.joining(", "));
		emailService.sendPgBookingFullyPaidUserEmail(user, booking, property, sharing, guests);
		emailService.sendPgBookingFullyPaidOwnerEmail(owner, user, booking, property, sharing, guests);
	}
}
