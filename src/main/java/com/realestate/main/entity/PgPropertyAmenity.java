package com.realestate.main.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "pg_amenities")
public class PgPropertyAmenity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "pg_id", nullable = false)
	private Long pgPropertyId;

	@Column(name = "amenity_code", nullable = false, length = 64)
	private String amenityCode;

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

	public String getAmenityCode() {
		return amenityCode;
	}

	public void setAmenityCode(String amenityCode) {
		this.amenityCode = amenityCode;
	}
}
