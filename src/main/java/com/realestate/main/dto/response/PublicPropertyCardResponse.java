package com.realestate.main.dto.response;

import java.math.BigDecimal;

import com.realestate.main.entity.enums.AgentSpecialization;
import com.realestate.main.entity.enums.ListingType;

public class PublicPropertyCardResponse {

	private Long id;
	private String propertyCode;
	private String title;
	private ListingType listingType;
	private AgentSpecialization category;
	private String categoryLabel;
	private String propertySubType;
	private BigDecimal price;
	private boolean priceNegotiable;
	private Double areaSqFt;
	private Double plotAreaSqFt;
	private String bhk;
	private String locality;
	private String city;
	private String state;
	private String primaryImageUrl;
	private String specsSummary;
	private String amenities;

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

	public String getCategoryLabel() {
		return categoryLabel;
	}

	public void setCategoryLabel(String categoryLabel) {
		this.categoryLabel = categoryLabel;
	}

	public String getPropertySubType() {
		return propertySubType;
	}

	public void setPropertySubType(String propertySubType) {
		this.propertySubType = propertySubType;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public boolean isPriceNegotiable() {
		return priceNegotiable;
	}

	public void setPriceNegotiable(boolean priceNegotiable) {
		this.priceNegotiable = priceNegotiable;
	}

	public Double getAreaSqFt() {
		return areaSqFt;
	}

	public void setAreaSqFt(Double areaSqFt) {
		this.areaSqFt = areaSqFt;
	}

	public Double getPlotAreaSqFt() {
		return plotAreaSqFt;
	}

	public void setPlotAreaSqFt(Double plotAreaSqFt) {
		this.plotAreaSqFt = plotAreaSqFt;
	}

	public String getBhk() {
		return bhk;
	}

	public void setBhk(String bhk) {
		this.bhk = bhk;
	}

	public String getLocality() {
		return locality;
	}

	public void setLocality(String locality) {
		this.locality = locality;
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

	public String getPrimaryImageUrl() {
		return primaryImageUrl;
	}

	public void setPrimaryImageUrl(String primaryImageUrl) {
		this.primaryImageUrl = primaryImageUrl;
	}

	public String getSpecsSummary() {
		return specsSummary;
	}

	public void setSpecsSummary(String specsSummary) {
		this.specsSummary = specsSummary;
	}

	public String getAmenities() {
		return amenities;
	}

	public void setAmenities(String amenities) {
		this.amenities = amenities;
	}
}
