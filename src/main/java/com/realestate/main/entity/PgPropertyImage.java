package com.realestate.main.entity;

import java.time.Instant;

import com.realestate.main.entity.enums.PgImageCategory;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "pg_images")
public class PgPropertyImage {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "image_id")
	private Long id;

	@Column(name = "pg_id", nullable = false)
	private Long pgPropertyId;

	@Column(name = "floor_id")
	private Long floorId;

	@Column(name = "room_id")
	private Long roomId;

	@Column(name = "image_path", nullable = false, length = 500)
	private String imagePath;

	@Enumerated(EnumType.STRING)
	@Column(name = "image_type", length = 32)
	private PgImageCategory imageType = PgImageCategory.BUILDING;

	@Column(name = "sort_order")
	private Integer sortOrder = 0;

	@Column(name = "uploaded_at", nullable = false, updatable = false)
	private Instant uploadedAt;

	@PrePersist
	void onCreate() {
		uploadedAt = Instant.now();
	}

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

	public Long getFloorId() {
		return floorId;
	}

	public void setFloorId(Long floorId) {
		this.floorId = floorId;
	}

	public Long getRoomId() {
		return roomId;
	}

	public void setRoomId(Long roomId) {
		this.roomId = roomId;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public PgImageCategory getImageType() {
		return imageType;
	}

	public void setImageType(PgImageCategory imageType) {
		this.imageType = imageType;
	}

	public Integer getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(Integer sortOrder) {
		this.sortOrder = sortOrder;
	}

	public Instant getUploadedAt() {
		return uploadedAt;
	}
}
