package com.realestate.main.dto.pgbooking;

import java.util.ArrayList;
import java.util.List;

import com.realestate.main.entity.enums.PaymentMethod;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class PgBookingDraftRequest {

	@NotNull
	private Long pgPropertyId;

	@NotNull
	private Long roomId;

	@NotNull
	@Min(1)
	private Integer bedCount;

	@NotEmpty
	private List<Integer> bedNumbers = new ArrayList<>();

	@NotEmpty
	@Valid
	private List<PgOccupantRequest> occupants = new ArrayList<>();

	private PaymentMethod paymentMethod;

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

	public Integer getBedCount() {
		return bedCount;
	}

	public void setBedCount(Integer bedCount) {
		this.bedCount = bedCount;
	}

	public List<Integer> getBedNumbers() {
		return bedNumbers;
	}

	public void setBedNumbers(List<Integer> bedNumbers) {
		this.bedNumbers = bedNumbers;
	}

	public List<PgOccupantRequest> getOccupants() {
		return occupants;
	}

	public void setOccupants(List<PgOccupantRequest> occupants) {
		this.occupants = occupants;
	}

	public PaymentMethod getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(PaymentMethod paymentMethod) {
		this.paymentMethod = paymentMethod;
	}
}
