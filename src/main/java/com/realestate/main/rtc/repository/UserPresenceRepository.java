package com.realestate.main.rtc.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.realestate.main.rtc.entity.UserPresence;
import com.realestate.main.rtc.entity.UserPresenceId;
import com.realestate.main.rtc.enums.RtcParticipantType;

public interface UserPresenceRepository extends JpaRepository<UserPresence, UserPresenceId> {

	Optional<UserPresence> findByParticipantTypeAndParticipantId(RtcParticipantType type, Long id);
}
