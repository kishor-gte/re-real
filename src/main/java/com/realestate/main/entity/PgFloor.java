package com.realestate.main.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "pg_floors")
public class PgFloor {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "floor_id")
	private Long id;

	@Column(name = "pg_id", nullable = false)
	private Long pgPropertyId;

	@Column(name = "floor_number", nullable = false)
	private Integer floorNumber;

	@Column(name = "total_rooms")
	private Integer totalRooms;

	@Column(name = "floor_description", columnDefinition = "TEXT")
	private String floorDescription;

	@Column(name = "sort_order")
	private Integer sortOrder = 0;

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

	public Integer getFloorNumber() {
		return floorNumber;
	}

	public void setFloorNumber(Integer floorNumber) {
		this.floorNumber = floorNumber;
	}

	public Integer getTotalRooms() {
		return totalRooms;
	}

	public void setTotalRooms(Integer totalRooms) {
		this.totalRooms = totalRooms;
	}

	public String getFloorDescription() {
		return floorDescription;
	}

	public void setFloorDescription(String floorDescription) {
		this.floorDescription = floorDescription;
	}

	public Integer getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(Integer sortOrder) {
		this.sortOrder = sortOrder;
	}
}
