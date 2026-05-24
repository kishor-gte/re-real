package com.realestate.main.dto.response;

import java.math.BigDecimal;
import java.time.Instant;

import com.realestate.main.entity.enums.GenderAllowed;
import com.realestate.main.entity.enums.PgPropertyStatus;
import com.realestate.main.entity.enums.PgType;

public class PgPropertyListItemResponse {

	private Long id;
	private String pgCode;
	private String pgName;
	private PgType pgType;
	private GenderAllowed genderAllowed;
	private String city;
	private String state;
	private Integer totalRooms;
	private Integer availableBeds;
	private BigDecimal monthlyRent;
	private PgPropertyStatus status;
	private Boolean featured;
	private String coverImage;
	private Instant createdAt;

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

	public GenderAllowed getGenderAllowed() {
		return genderAllowed;
	}

	public void setGenderAllowed(GenderAllowed genderAllowed) {
		this.genderAllowed = genderAllowed;
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

	public PgPropertyStatus getStatus() {
		return status;
	}

	public void setStatus(PgPropertyStatus status) {
		this.status = status;
	}

	public Boolean getFeatured() {
		return featured;
	}

	public void setFeatured(Boolean featured) {
		this.featured = featured;
	}

	public String getCoverImage() {
		return coverImage;
	}

	public void setCoverImage(String coverImage) {
		this.coverImage = coverImage;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}
}
