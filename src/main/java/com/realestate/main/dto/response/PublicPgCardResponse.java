package com.realestate.main.dto.response;

import java.math.BigDecimal;

import com.realestate.main.entity.enums.GenderAllowed;
import com.realestate.main.entity.enums.PgType;

public class PublicPgCardResponse {

	private Long id;
	private String pgCode;
	private String pgName;
	private PgType pgType;
	private String pgTypeLabel;
	private GenderAllowed genderAllowed;
	private String genderLabel;
	private String city;
	private String state;
	private String address;
	private String landmark;
	private Integer totalRooms;
	private Integer availableBeds;
	private BigDecimal monthlyRent;
	private String coverImageUrl;
	private String specsSummary;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPgCode() {
		return pgCode;
	}

	public void setPgCode(String pgCode) {
		this.pgCode = pgCode;
	}

	public String getPgName() {
		return pgName;
	}

	public void setPgName(String pgName) {
		this.pgName = pgName;
	}

	public PgType getPgType() {
		return pgType;
	}

	public void setPgType(PgType pgType) {
		this.pgType = pgType;
	}

	public String getPgTypeLabel() {
		return pgTypeLabel;
	}

	public void setPgTypeLabel(String pgTypeLabel) {
		this.pgTypeLabel = pgTypeLabel;
	}

	public GenderAllowed getGenderAllowed() {
		return genderAllowed;
	}

	public void setGenderAllowed(GenderAllowed genderAllowed) {
		this.genderAllowed = genderAllowed;
	}

	public String getGenderLabel() {
		return genderLabel;
	}

	public void setGenderLabel(String genderLabel) {
		this.genderLabel = genderLabel;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getLandmark() {
		return landmark;
	}

	public void setLandmark(String landmark) {
		this.landmark = landmark;
	}

	public Integer getTotalRooms() {
		return totalRooms;
	}

	public void setTotalRooms(Integer totalRooms) {
		this.totalRooms = totalRooms;
	}

	public Integer getAvailableBeds() {
		return availableBeds;
	}

	public void setAvailableBeds(Integer availableBeds) {
		this.availableBeds = availableBeds;
	}

	public BigDecimal getMonthlyRent() {
		return monthlyRent;
	}

	public void setMonthlyRent(BigDecimal monthlyRent) {
		this.monthlyRent = monthlyRent;
	}

	public String getCoverImageUrl() {
		return coverImageUrl;
	}

	public void setCoverImageUrl(String coverImageUrl) {
		this.coverImageUrl = coverImageUrl;
	}

	public String getSpecsSummary() {
		return specsSummary;
	}

	public void setSpecsSummary(String specsSummary) {
		this.specsSummary = specsSummary;
	}
}
