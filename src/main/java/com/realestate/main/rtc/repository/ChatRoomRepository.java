package com.realestate.main.rtc.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.realestate.main.rtc.entity.ChatRoom;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

	Optional<ChatRoom> findByEnquiryId(Long enquiryId);

	Optional<ChatRoom> findByBookingId(Long bookingId);

	Optional<ChatRoom> findByPgBookingId(Long pgBookingId);

	Optional<ChatRoom> findByRoomCode(String roomCode);
}
