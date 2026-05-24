package com.realestate.main.entity;

import java.math.BigDecimal;

import com.realestate.main.entity.enums.PgFurnishingType;
import com.realestate.main.entity.enums.PgRoomStatus;
import com.realestate.main.entity.enums.PgRoomType;
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
@Table(name = "pg_rooms")
public class PgRoom {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "room_id")
	private Long id;

	@Column(name = "floor_id", nullable = false)
	private Long floorId;

	@Column(name = "pg_id", nullable = false)
	private Long pgPropertyId;

	@Column(name = "room_number", nullable = false, length = 32)
	private String roomNumber;

	@Enumerated(EnumType.STRING)
	@Column(name = "room_type", length = 32)
	private PgRoomType roomType;

	@Column(name = "room_size", length = 32)
	private String roomSize;

	@Column(name = "attached_bathroom")
	private Boolean attachedBathroom = false;

	@Column(name = "balcony_available")
	private Boolean balconyAvailable = false;

	@Column(name = "ac_available")
	private Boolean acAvailable = false;

	@Enumerated(EnumType.STRING)
	@Column(length = 32)
	private PgFurnishingType furnishing;

	@Enumerated(EnumType.STRING)
	@Column(name = "sharing_type", length = 16)
	private PgSharingType sharingType;

	@Column(name = "total_beds")
	private Integer totalBeds;

	@Column(name = "occupied_beds")
	private Integer occupiedBeds = 0;

	@Column(name = "available_beds")
	private Integer availableBeds;

	@Column(name = "room_price", precision = 12, scale = 2)
	private BigDecimal roomPrice;

	@Enumerated(EnumType.STRING)
	@Column(name = "room_status", length = 32)
	private PgRoomStatus roomStatus = PgRoomStatus.AVAILABLE;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getFloorId() {
		return floorId;
	}

	public void setFloorId(Long floorId) {
		this.floorId = floorId;
	}

	public Long getPgPropertyId() {
		return pgPropertyId;
	}

	public void setPgPropertyId(Long pgPropertyId) {
		this.pgPropertyId = pgPropertyId;
	}

	public String getRoomNumber() {
		return roomNumber;
	}

	public void setRoomNumber(String roomNumber) {
		this.roomNumber = roomNumber;
	}

	public PgRoomType getRoomType() {
		return roomType;
	}

	public void setRoomType(PgRoomType roomType) {
		this.roomType = roomType;
	}

	public String getRoomSize() {
		return roomSize;
	}

	public void setRoomSize(String roomSize) {
		this.roomSize = roomSize;
	}

	public Boolean getAttachedBathroom() {
		return attachedBathroom;
	}

	public void setAttachedBathroom(Boolean attachedBathroom) {
		this.attachedBathroom = attachedBathroom;
	}

	public Boolean getBalconyAvailable() {
		return balconyAvailable;
	}

	public void setBalconyAvailable(Boolean balconyAvailable) {
		this.balconyAvailable = balconyAvailable;
	}

	public Boolean getAcAvailable() {
		return acAvailable;
	}

	public void setAcAvailable(Boolean acAvailable) {
		this.acAvailable = acAvailable;
	}

	public PgFurnishingType getFurnishing() {
		return furnishing;
	}

	public void setFurnishing(PgFurnishingType furnishing) {
		this.furnishing = furnishing;
	}

	public PgSharingType getSharingType() {
		return sharingType;
	}

	public void setSharingType(PgSharingType sharingType) {
		this.sharingType = sharingType;
	}

	public Integer getTotalBeds() {
		return totalBeds;
	}

	public void setTotalBeds(Integer totalBeds) {
		this.totalBeds = totalBeds;
	}

	public Integer getOccupiedBeds() {
		return occupiedBeds;
	}

	public void setOccupiedBeds(Integer occupiedBeds) {
		this.occupiedBeds = occupiedBeds;
	}

	public Integer getAvailableBeds() {
		return availableBeds;
	}

	public void setAvailableBeds(Integer availableBeds) {
		this.availableBeds = availableBeds;
	}

	public BigDecimal getRoomPrice() {
		return roomPrice;
	}

	public void setRoomPrice(BigDecimal roomPrice) {
		this.roomPrice = roomPrice;
	}

	public PgRoomStatus getRoomStatus() {
		return roomStatus;
	}

	public void setRoomStatus(PgRoomStatus roomStatus) {
		this.roomStatus = roomStatus;
	}
}
