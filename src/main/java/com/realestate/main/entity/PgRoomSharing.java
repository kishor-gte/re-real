package com.realestate.main.entity;

import java.math.BigDecimal;

import com.realestate.main.entity.enums.PgSharingType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "pg_room_sharing")
public class PgRoomSharing {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "pg_id", nullable = false)
	private Long pgPropertyId;

	@Column(name = "room_id")
	private Long roomId;

	@Enumerated(EnumType.STRING)
	@Column(name = "sharing_type", nullable = false, length = 16)
	private PgSharingType sharingType;

	@Column(name = "monthly_rent", nullable = false, precision = 12, scale = 2)
	private BigDecimal monthlyRent;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getPgPropertyId() {
		return pgPropertyId;
	}

	public void setPgPropertyId(Long pgPropertyId) {
		this.pgPropertyId = pgPropertyId;
	}

	public Long getRoomId() {
		return roomId;
	}

	public void setRoomId(Long roomId) {
		this.roomId = roomId;
	}

	public PgSharingType getSharingType() {
		return sharingType;
	}

	public void setSharingType(PgSharingType sharingType) {
		this.sharingType = sharingType;
	}

	public BigDecimal getMonthlyRent() {
		return monthlyRent;
	}

	public void setMonthlyRent(BigDecimal monthlyRent) {
		this.monthlyRent = monthlyRent;
	}
}
