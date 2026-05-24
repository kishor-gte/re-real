package com.realestate.main.dto.pgbooking;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.realestate.main.entity.enums.PgSharingType;

public class PgBookingRoomOptionResponse {

	private Long roomId;
	private String roomNumber;
	private Integer floorNumber;
	private String sharingType;
	private Integer totalBeds;
	private Integer availableBeds;
	private BigDecimal rentPerBed;
	private List<Integer> availableBedNumbers = new ArrayList<>();

	public Long getRoomId() {
		return roomId;
	}

	public void setRoomId(Long roomId) {
		this.roomId = roomId;
	}

	public String getRoomNumber() {
		return roomNumber;
	}

	public void setRoomNumber(String roomNumber) {
		this.roomNumber = roomNumber;
	}

	public Integer getFloorNumber() {
		return floorNumber;
	}

	public void setFloorNumber(Integer floorNumber) {
		this.floorNumber = floorNumber;
	}

	public String getSharingType() {
		return sharingType;
	}

	public void setSharingType(String sharingType) {
		this.sharingType = sharingType;
	}

	public void setSharingTypeEnum(PgSharingType type) {
		this.sharingType = type != null ? type.name() : null;
	}

	public Integer getTotalBeds() {
		return totalBeds;
	}

	public void setTotalBeds(Integer totalBeds) {
		this.totalBeds = totalBeds;
	}

	public Integer getAvailableBeds() {
		return availableBeds;
	}

	public void setAvailableBeds(Integer availableBeds) {
		this.availableBeds = availableBeds;
	}

	public BigDecimal getRentPerBed() {
		return rentPerBed;
	}

	public void setRentPerBed(BigDecimal rentPerBed) {
		this.rentPerBed = rentPerBed;
	}

	public List<Integer> getAvailableBedNumbers() {
		return availableBedNumbers;
	}

	public void setAvailableBedNumbers(List<Integer> availableBedNumbers) {
		this.availableBedNumbers = availableBedNumbers;
	}
}
