package com.realestate.main.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.realestate.main.entity.enums.AgentSpecialization;
import com.realestate.main.entity.enums.CommercialType;
import com.realestate.main.entity.enums.ConstructionStatus;
import com.realestate.main.entity.enums.FurnishingStatus;
import com.realestate.main.entity.enums.LandUseType;
import com.realestate.main.entity.enums.ListingType;
import com.realestate.main.entity.enums.PropertyStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "properties")
public class Property {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "property_code", nullable = false, unique = true, length = 20)
	private String propertyCode;

	@Column(name = "agent_id", nullable = false)
	private Long agentId;

	@Column(nullable = false, length = 150)
	private String title;

	@Column(columnDefinition = "TEXT")
	private String description;

	@Enumerated(EnumType.STRING)
	@Column(name = "listing_type", nullable = false, length = 10)
	private ListingType listingType;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 30)
	private AgentSpecialization category;

	@Column(name = "property_sub_type", nullable = false, length = 60)
	private String propertySubType;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private PropertyStatus status = PropertyStatus.ACTIVE;

	@Column(nullable = false, precision = 15, scale = 2)
	private BigDecimal price;

	@Column(name = "price_negotiable", nullable = false)
	private boolean priceNegotiable = true;

	@Column(name = "area_sq_ft")
	private Double areaSqFt;

	@Column(name = "plot_area_sq_ft")
	private Double plotAreaSqFt;

	@Column(name = "super_built_up_sq_ft")
	private Double superBuiltUpSqFt;

	@Column(name = "address_line", nullable = false, length = 255)
	private String addressLine;

	@Column(nullable = false, length = 100)
	private String locality;

	@Column(nullable = false, length = 80)
	private String city;

	@Column(nullable = false, length = 80)
	private String state;

	@Column(nullable = false, length = 6)
	private String pincode;

	@Column(length = 10)
	private String bhk;

	@Column
	private Integer bathrooms;

	@Column
	private Integer balconies;

	@Column(name = "floor_number")
	private Integer floorNumber;

	@Column(name = "total_floors")
	private Integer totalFloors;

	@Enumerated(EnumType.STRING)
	@Column(length = 20)
	private FurnishingStatus furnishing;

	@Column(name = "property_age_years")
	private Integer propertyAgeYears;

	@Column(name = "parking_slots")
	private Integer parkingSlots;

	@Enumerated(EnumType.STRING)
	@Column(name = "commercial_type", length = 30)
	private CommercialType commercialType;

	@Column(name = "seats_capacity")
	private Integer seatsCapacity;

	@Column(name = "plot_length_ft")
	private Double plotLengthFt;

	@Column(name = "plot_width_ft")
	private Double plotWidthFt;

	@Column(length = 20)
	private String facing;

	@Enumerated(EnumType.STRING)
	@Column(name = "land_use", length = 30)
	private LandUseType landUse;

	@Column(name = "boundary_wall")
	private Boolean boundaryWall;

	@Column(name = "corner_plot")
	private Boolean cornerPlot;

	@Column(name = "project_name", length = 120)
	private String projectName;

	@Column(name = "builder_name", length = 120)
	private String builderName;

	@Enumerated(EnumType.STRING)
	@Column(name = "construction_status", length = 30)
	private ConstructionStatus constructionStatus;

	@Column(name = "possession_date")
	private LocalDate possessionDate;

	@Column(name = "total_units")
	private Integer totalUnits;

	@Column(name = "unit_configuration", length = 255)
	private String unitConfiguration;

	@Column(name = "private_pool")
	private Boolean privatePool;

	@Column(name = "private_garden")
	private Boolean privateGarden;

	@Column(length = 500)
	private String amenities;

	@Column(name = "primary_image_url", nullable = false, length = 500)
	private String primaryImageUrl;

	@Column(name = "gallery_urls", length = 2000)
	private String galleryUrls;

	@Column(name = "view_count", nullable = false)
	private int viewCount;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

	@PrePersist
	void onCreate() {
		LocalDateTime now = LocalDateTime.now();
		createdAt = now;
		updatedAt = now;
		if (status == null) {
			status = PropertyStatus.ACTIVE;
		}
	}

	@PreUpdate
	void onUpdate() {
		updatedAt = LocalDateTime.now();
	}

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

	public Long getAgentId() {
		return agentId;
	}

	public void setAgentId(Long agentId) {
		this.agentId = agentId;
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

	public Double getSuperBuiltUpSqFt() {
		return superBuiltUpSqFt;
	}

	public void setSuperBuiltUpSqFt(Double superBuiltUpSqFt) {
		this.superBuiltUpSqFt = superBuiltUpSqFt;
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

	public String getGalleryUrls() {
		return galleryUrls;
	}

	public void setGalleryUrls(String galleryUrls) {
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

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}
}
