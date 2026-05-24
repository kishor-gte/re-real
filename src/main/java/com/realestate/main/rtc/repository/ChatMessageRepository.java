package com.realestate.main.rtc.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.realestate.main.rtc.entity.ChatMessage;
import com.realestate.main.rtc.enums.ChatMessageStatus;
import com.realestate.main.rtc.enums.RtcParticipantType;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

	List<ChatMessage> findByRoomIdAndDeletedFalseOrderBySentAtAsc(Long roomId);

	long countByRoomIdAndSenderTypeNotAndMessageStatusNotAndDeletedFalse(Long roomId, RtcParticipantType senderType,
			ChatMessageStatus status);

	@Modifying
	@Query("UPDATE ChatMessage m SET m.messageStatus = :status, m.seenAt = CURRENT_TIMESTAMP "
			+ "WHERE m.roomId = :roomId AND m.senderType <> :readerType AND m.deleted = false")
	int markSeenInRoom(@Param("roomId") Long roomId, @Param("readerType") RtcParticipantType readerType,
			@Param("status") ChatMessageStatus status);

	@Query("SELECT COUNT(m) FROM ChatMessage m JOIN ChatRoom r ON m.roomId = r.id "
			+ "WHERE r.agentId = :participantId AND m.senderType <> :selfType AND m.messageStatus <> :seen AND m.deleted = false")
	long countUnreadForAgent(@Param("participantId") Long agentId, @Param("selfType") RtcParticipantType selfType,
			@Param("seen") ChatMessageStatus seen);

	@Query("SELECT COUNT(m) FROM ChatMessage m JOIN ChatRoom r ON m.roomId = r.id "
			+ "WHERE r.userId = :participantId AND m.senderType <> :selfType AND m.messageStatus <> :seen AND m.deleted = false")
	long countUnreadForUser(@Param("participantId") Long userId, @Param("selfType") RtcParticipantType selfType,
			@Param("seen") ChatMessageStatus seen);

	@Query("SELECT r.id, r.enquiryId, r.bookingId, COUNT(m) FROM ChatRoom r JOIN ChatMessage m ON m.roomId = r.id "
			+ "WHERE r.agentId = :participantId AND m.senderType <> :selfType AND m.messageStatus <> :seen AND m.deleted = false "
			+ "GROUP BY r.id, r.enquiryId, r.bookingId")
	java.util.List<Object[]> unreadGroupedForAgent(@Param("participantId") Long agentId,
			@Param("selfType") RtcParticipantType selfType, @Param("seen") ChatMessageStatus seen);

	@Query("SELECT r.id, r.enquiryId, r.bookingId, r.pgBookingId, COUNT(m) FROM ChatRoom r JOIN ChatMessage m ON m.roomId = r.id "
			+ "WHERE r.userId = :participantId AND m.senderType <> :selfType AND m.messageStatus <> :seen AND m.deleted = false "
			+ "GROUP BY r.id, r.enquiryId, r.bookingId, r.pgBookingId")
	java.util.List<Object[]> unreadGroupedForUser(@Param("participantId") Long userId,
			@Param("selfType") RtcParticipantType selfType, @Param("seen") ChatMessageStatus seen);

	@Query("SELECT r.id, r.enquiryId, r.bookingId, r.pgBookingId, COUNT(m) FROM ChatRoom r JOIN ChatMessage m ON m.roomId = r.id "
			+ "WHERE r.pgOwnerId = :participantId AND m.senderType <> :selfType AND m.messageStatus <> :seen AND m.deleted = false "
			+ "GROUP BY r.id, r.enquiryId, r.bookingId, r.pgBookingId")
	java.util.List<Object[]> unreadGroupedForPgOwner(@Param("participantId") Long pgOwnerId,
			@Param("selfType") RtcParticipantType selfType, @Param("seen") ChatMessageStatus seen);
}
