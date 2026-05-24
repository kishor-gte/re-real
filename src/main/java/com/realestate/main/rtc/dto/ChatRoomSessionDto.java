package com.realestate.main.rtc.dto;

import java.util.ArrayList;
import java.util.List;

import com.realestate.main.rtc.enums.PresenceStatus;
import com.realestate.main.rtc.enums.RtcParticipantType;

public class ChatRoomSessionDto {

	private Long roomId;
	private String roomCode;
	private Long enquiryId;
	private Long bookingId;
	private Long pgBookingId;
	private String propertyTitle;
	private RtcParticipantType peerType;
	private Long peerId;
	private String peerName;
	private PresenceStatus peerPresence;
	private RtcPrincipal me;
	private List<ChatMessageDto> messages = new ArrayList<>();
	private List<String> stunServers = new ArrayList<>();

	public Long getRoomId() {
		return roomId;
	}

	public void setRoomId(Long roomId) {
		this.roomId = roomId;
	}

	public String getRoomCode() {
		return roomCode;
	}

	public void setRoomCode(String roomCode) {
		this.roomCode = roomCode;
	}

	public Long getEnquiryId() {
		return enquiryId;
	}

	public void setEnquiryId(Long enquiryId) {
		this.enquiryId = enquiryId;
	}

	public Long getBookingId() {
		return bookingId;
	}

	public void setBookingId(Long bookingId) {
		this.bookingId = bookingId;
	}

	public Long getPgBookingId() {
		return pgBookingId;
	}

	public void setPgBookingId(Long pgBookingId) {
		this.pgBookingId = pgBookingId;
	}

	public String getPropertyTitle() {
		return propertyTitle;
	}

	public void setPropertyTitle(String propertyTitle) {
		this.propertyTitle = propertyTitle;
	}

	public RtcParticipantType getPeerType() {
		return peerType;
	}

	public void setPeerType(RtcParticipantType peerType) {
		this.peerType = peerType;
	}

	public Long getPeerId() {
		return peerId;
	}

	public void setPeerId(Long peerId) {
		this.peerId = peerId;
	}

	public String getPeerName() {
		return peerName;
	}

	public void setPeerName(String peerName) {
		this.peerName = peerName;
	}

	public PresenceStatus getPeerPresence() {
		return peerPresence;
	}

	public void setPeerPresence(PresenceStatus peerPresence) {
		this.peerPresence = peerPresence;
	}

	public RtcPrincipal getMe() {
		return me;
	}

	public void setMe(RtcPrincipal me) {
		this.me = me;
	}

	public List<ChatMessageDto> getMessages() {
		return messages;
	}

	public void setMessages(List<ChatMessageDto> messages) {
		this.messages = messages;
	}

	public List<String> getStunServers() {
		return stunServers;
	}

	public void setStunServers(List<String> stunServers) {
		this.stunServers = stunServers;
	}
}
