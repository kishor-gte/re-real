package com.realestate.main.rtc.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.realestate.main.rtc.entity.VoiceCallHistory;
import com.realestate.main.rtc.enums.RtcParticipantType;
import com.realestate.main.rtc.enums.VoiceCallStatus;

public interface VoiceCallHistoryRepository extends JpaRepository<VoiceCallHistory, Long> {

	Optional<VoiceCallHistory> findBySessionId(String sessionId);

	List<VoiceCallHistory> findByRoomIdOrderByCreatedAtDesc(Long roomId);

	List<VoiceCallHistory> findTop50ByCallerTypeAndCallerIdOrReceiverTypeAndReceiverIdOrderByCreatedAtDesc(
			RtcParticipantType callerType, Long callerId, RtcParticipantType receiverType, Long receiverId);

	boolean existsByReceiverTypeAndReceiverIdAndCallStatus(RtcParticipantType receiverType, Long receiverId,
			VoiceCallStatus status);
}
