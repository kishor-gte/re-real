package com.realestate.main.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.realestate.main.entity.enums.AgentSpecialization;
import com.realestate.main.entity.enums.CommercialType;
import com.realestate.main.entity.enums.ConstructionStatus;
import com.realestate.main.entity.enums.FurnishingStatus;
import com.realestate.main.entity.enums.LandUseType;
import com.realestate.main.entity.enums.ListingType;
import com.realestate.main.entity.enums.PropertyStatus;

public class PropertyResponse {

	private Long id;
	private String propertyCode;
	private String title;
	private String description;
	private ListingType listingType;
	private AgentSpecialization category;
	private String propertySubType;
	private PropertyStatus status;
	private BigDecimal price;
	private boolean priceNegotiable;
	private Double areaSqFt;
	private Double plotAreaSqFt;
	private String addressLine;
	private String locality;
	private String city;
	private String state;
	private String pincode;
	private String bhk;
	private Integer bathrooms;
	private Integer balconies;
	private Integer floorNumber;
	private Integer totalFloors;
	private FurnishingStatus furnishing;
	private Integer propertyAgeYears;
	private Integer parkingSlots;
	private CommercialType commercialType;
	private Integer seatsCapacity;
	private Double plotLengthFt;
	private Double plotWidthFt;
	private String facing;
	private LandUseType landUse;
	private Boolean boundaryWall;
	private Boolean cornerPlot;
	private String projectName;
	private String builderName;
	private ConstructionStatus constructionStatus;
	private LocalDate possessionDate;
	private Integer totalUnits;
	private String unitConfiguration;
	private Boolean privatePool;
	private Boolean privateGarden;
	private String amenities;
	private String primaryImageUrl;
	private List<String> galleryUrls;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

	public String getPropertySubType() {
		return propertySubType;
	}

	public void setPropertySubType(String propertySubType) {
		this.propertySubType = propertySubType;
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

	public String getAddressLine() {
		return addressLine;
	}

	public void setAddressLine(String addressLine) {
		this.addressLine = addressLine;
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

	public String getPincode() {
		return pincode;
	}

	public void setPincode(String pincode) {
		this.pincode = pincode;
	}

	public String getBhk() {
		return bhk;
	}

	public void setBhk(String bhk) {
		this.bhk = bhk;
	}

	public Integer getBathrooms() {
		return bathrooms;
	}

	public void setBathrooms(Integer bathrooms) {
		this.bathrooms = bathrooms;
	}

	public Integer getBalconies() {
		return balconies;
	}

	public void setBalconies(Integer balconies) {
		this.balconies = balconies;
	}

	public Integer getFloorNumber() {
		return floorNumber;
	}

	public void setFloorNumber(Integer floorNumber) {
		this.floorNumber = floorNumber;
	}

	public Integer getTotalFloors() {
		return totalFloors;
	}

	public void setTotalFloors(Integer totalFloors) {
		this.totalFloors = totalFloors;
	}

	public FurnishingStatus getFurnishing() {
		return furnishing;
	}

	public void setFurnishing(FurnishingStatus furnishing) {
		this.furnishing = furnishing;
	}

	public Integer getPropertyAgeYears() {
		return propertyAgeYears;
	}

	public void setPropertyAgeYears(Integer propertyAgeYears) {
		this.propertyAgeYears = propertyAgeYears;
	}

	public Integer getParkingSlots() {
		return parkingSlots;
	}

	public void setParkingSlots(Integer parkingSlots) {
		this.parkingSlots = parkingSlots;
	}

	public CommercialType getCommercialType() {
		return commercialType;
	}

	public void setCommercialType(CommercialType commercialType) {
		this.commercialType = commercialType;
	}

	public Integer getSeatsCapacity() {
		return seatsCapacity;
	}

	public void setSeatsCapacity(Integer seatsCapacity) {
		this.seatsCapacity = seatsCapacity;
	}

	public Double getPlotLengthFt() {
		return plotLengthFt;
	}

	public void setPlotLengthFt(Double plotLengthFt) {
		this.plotLengthFt = plotLengthFt;
	}

	public Double getPlotWidthFt() {
		return plotWidthFt;
	}

	public void setPlotWidthFt(Double plotWidthFt) {
		this.plotWidthFt = plotWidthFt;
	}

	public String getFacing() {
		return facing;
	}

	public void setFacing(String facing) {
		this.facing = facing;
	}

	public LandUseType getLandUse() {
		return landUse;
	}

	public void setLandUse(LandUseType landUse) {
		this.landUse = landUse;
	}

	public Boolean getBoundaryWall() {
		return boundaryWall;
	}

	public void setBoundaryWall(Boolean boundaryWall) {
		this.boundaryWall = boundaryWall;
	}

	public Boolean getCornerPlot() {
		return cornerPlot;
	}

	public void setCornerPlot(Boolean cornerPlot) {
		this.cornerPlot = cornerPlot;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getBuilderName() {
		return builderName;
	}

	public void setBuilderName(String builderName) {
		this.builderName = builderName;
	}

	public ConstructionStatus getConstructionStatus() {
		return constructionStatus;
	}

	public void setConstructionStatus(ConstructionStatus constructionStatus) {
		this.constructionStatus = constructionStatus;
	}

	public LocalDate getPossessionDate() {
		return possessionDate;
	}

	public void setPossessionDate(LocalDate possessionDate) {
		this.possessionDate = possessionDate;
	}

	public Integer getTotalUnits() {
		return totalUnits;
	}

	public void setTotalUnits(Integer totalUnits) {
		this.totalUnits = totalUnits;
	}

	public String getUnitConfiguration() {
		return unitConfiguration;
	}

	public void setUnitConfiguration(String unitConfiguration) {
		this.unitConfiguration = unitConfiguration;
	}

	public Boolean getPrivatePool() {
		return privatePool;
	}

	public void setPrivatePool(Boolean privatePool) {
		this.privatePool = privatePool;
	}

	public Boolean getPrivateGarden() {
		return privateGarden;
	}

	public void setPrivateGarden(Boolean privateGarden) {
		this.privateGarden = privateGarden;
	}

	public String getAmenities() {
		return amenities;
	}

	public void setAmenities(String amenities) {
		this.amenities = amenities;
	}

	public String getPrimaryImageUrl() {
		return primaryImageUrl;
	}

	public void setPrimaryImageUrl(String primaryImageUrl) {
		this.primaryImageUrl = primaryImageUrl;
	}

	public List<String> getGalleryUrls() {
		return galleryUrls;
	}

	public void setGalleryUrls(List<String> galleryUrls) {
		this.galleryUrls = galleryUrls;
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
