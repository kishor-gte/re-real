package com.realestate.main.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.realestate.main.entity.enums.AgentSpecialization;
import com.realestate.main.entity.enums.ListingType;
import com.realestate.main.entity.enums.PropertyStatus;

public class AdminPropertyListItemResponse {

	private Long id;
	private String propertyCode;
	private String title;
	private ListingType listingType;
	private AgentSpecialization category;
	private PropertyStatus status;
	private BigDecimal price;
	private String city;
	private String locality;
	private int viewCount;
	private LocalDateTime createdAt;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPropertyCode() {
		return propertyCode;
	}

	public void setPropertyCode(String propertyCode) {
		this.propertyCode = propertyCode;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public ListingType getListingType() {
		return listingType;
	}

	public void setListingType(ListingType listingType) {
		this.listingType = listingType;
	}

	public AgentSpecialization getCategory() {
		return category;
	}

	public void setCategory(AgentSpecialization category) {
		this.category = category;
	}

	public PropertyStatus getStatus() {
		return status;
	}

	public void setStatus(PropertyStatus status) {
		this.status = status;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getLocality() {
		return locality;
	}

	public void setLocality(String locality) {
		this.locality = locality;
	}

	public int getViewCount() {
		return viewCount;
	}

	public void setViewCount(int viewCount) {
		this.viewCount = viewCount;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
}
