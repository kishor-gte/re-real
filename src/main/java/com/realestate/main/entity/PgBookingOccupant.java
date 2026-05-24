package com.realestate.main.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "pg_booking_occupants")
public class PgBookingOccupant {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "occupant_id")
	private Long id;

	@Column(name = "pg_booking_id", nullable = false)
	private Long pgBookingId;

	@Column(name = "occupant_index", nullable = false)
	private Integer occupantIndex;

	@Column(name = "bed_number")
	private Integer bedNumber;

	@Column(name = "full_name", nullable = false, length = 120)
	private String fullName;

	@Column(length = 20)
	private String mobile;

	@Column(length = 120)
	private String email;

	@Column(name = "created_at", nullable = false)
	private java.time.LocalDateTime createdAt;

	@PrePersist
	void onCreate() {
		createdAt = java.time.LocalDateTime.now();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getPgBookingId() {
		return pgBookingId;
	}

	public void setPgBookingId(Long pgBookingId) {
		this.pgBookingId = pgBookingId;
	}

	public Integer getOccupantIndex() {
		return occupantIndex;
	}

	public void setOccupantIndex(Integer occupantIndex) {
		this.occupantIndex = occupantIndex;
	}

	public Integer getBedNumber() {
		return bedNumber;
	}

	public void setBedNumber(Integer bedNumber) {
		this.bedNumber = bedNumber;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public java.time.LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(java.time.LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
}
