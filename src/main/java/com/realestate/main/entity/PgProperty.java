package com.realestate.main.entity;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

import com.realestate.main.entity.enums.GenderAllowed;
import com.realestate.main.entity.enums.PgPropertyStatus;
import com.realestate.main.entity.enums.PgType;

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
@Table(name = "pg_properties")
public class PgProperty {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "pg_id")
	private Long id;

	@Column(name = "owner_id", nullable = false)
	private Long ownerId;

	@Column(name = "pg_code", nullable = false, unique = true, length = 32)
	private String pgCode;

	@Column(name = "pg_name", nullable = false, length = 200)
	private String pgName;

	@Enumerated(EnumType.STRING)
	@Column(name = "pg_type", nullable = false, length = 32)
	private PgType pgType;

	@Enumerated(EnumType.STRING)
	@Column(name = "gender_allowed", nullable = false, length = 16)
	private GenderAllowed genderAllowed;

	@Column(columnDefinition = "TEXT")
	private String description;

	@Column(name = "owner_name", length = 120)
	private String ownerName;

	@Column(length = 20)
	private String mobile;

	@Column(length = 120)
	private String email;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String address;

	@Column(length = 200)
	private String landmark;

	@Column(nullable = false, length = 80)
	private String city;

	@Column(nullable = false, length = 80)
	private String state;

	@Column(nullable = false, length = 10)
	private String pincode;

	@Column(name = "map_location", length = 500)
	private String mapLocation;

	@Column(name = "nearby_places", columnDefinition = "TEXT")
	private String nearbyPlaces;

	@Column(name = "total_floors")
	private Integer totalFloors;

	@Column(name = "total_rooms")
	private Integer totalRooms;

	@Column(name = "total_capacity")
	private Integer totalCapacity;

	@Column(name = "available_beds")
	private Integer availableBeds;

	@Column(name = "lift_available")
	private Boolean liftAvailable = false;

	@Column(name = "parking_available")
	private Boolean parkingAvailable = false;

	@Column(name = "cctv_security")
	private Boolean cctvSecurity = false;

	@Column(name = "biometric_entry")
	private Boolean biometricEntry = false;

	@Column(name = "fire_safety")
	private Boolean fireSafety = false;

	@Column(name = "available_from")
	private LocalDate availableFrom;

	@Column(name = "immediate_availability")
	private Boolean immediateAvailability = false;

	@Column(name = "monthly_rent", precision = 12, scale = 2)
	private BigDecimal monthlyRent;

	@Column(name = "security_deposit", precision = 12, scale = 2)
	private BigDecimal securityDeposit;

	@Column(name = "maintenance_charges", precision = 12, scale = 2)
	private BigDecimal maintenanceCharges;

	@Column(name = "electricity_included")
	private Boolean electricityIncluded = false;

	@Column(name = "water_included")
	private Boolean waterIncluded = false;

	@Column(name = "booking_amount", precision = 12, scale = 2)
	private BigDecimal bookingAmount;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 16)
	private PgPropertyStatus status = PgPropertyStatus.DRAFT;

	@Column(name = "featured")
	private Boolean featured = false;

	@Column(name = "created_at", nullable = false, updatable = false)
	private Instant createdAt;

	@Column(name = "updated_at")
	private Instant updatedAt;

	@PrePersist
	void onCreate() {
		Instant now = Instant.now();
		createdAt = now;
		updatedAt = now;
	}

	@PreUpdate
	void onUpdate() {
		updatedAt = Instant.now();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
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

	public String getMapLocation() {
		return mapLocation;
	}

	public void setMapLocation(String mapLocation) {
		this.mapLocation = mapLocation;
	}

	public String getNearbyPlaces() {
		return nearbyPlaces;
	}

	public void setNearbyPlaces(String nearbyPlaces) {
		this.nearbyPlaces = nearbyPlaces;
	}

	public Integer getTotalFloors() {
		return totalFloors;
	}

	public void setTotalFloors(Integer totalFloors) {
		this.totalFloors = totalFloors;
	}

	public Integer getTotalRooms() {
		return totalRooms;
	}

	public void setTotalRooms(Integer totalRooms) {
		this.totalRooms = totalRooms;
	}

	public Integer getTotalCapacity() {
		return totalCapacity;
	}

	public void setTotalCapacity(Integer totalCapacity) {
		this.totalCapacity = totalCapacity;
	}

	public Integer getAvailableBeds() {
		return availableBeds;
	}

	public void setAvailableBeds(Integer availableBeds) {
		this.availableBeds = availableBeds;
	}

	public Boolean getLiftAvailable() {
		return liftAvailable;
	}

	public void setLiftAvailable(Boolean liftAvailable) {
		this.liftAvailable = liftAvailable;
	}

	public Boolean getParkingAvailable() {
		return parkingAvailable;
	}

	public void setParkingAvailable(Boolean parkingAvailable) {
		this.parkingAvailable = parkingAvailable;
	}

	public Boolean getCctvSecurity() {
		return cctvSecurity;
	}

	public void setCctvSecurity(Boolean cctvSecurity) {
		this.cctvSecurity = cctvSecurity;
	}

	public Boolean getBiometricEntry() {
		return biometricEntry;
	}

	public void setBiometricEntry(Boolean biometricEntry) {
		this.biometricEntry = biometricEntry;
	}

	public Boolean getFireSafety() {
		return fireSafety;
	}

	public void setFireSafety(Boolean fireSafety) {
		this.fireSafety = fireSafety;
	}

	public LocalDate getAvailableFrom() {
		return availableFrom;
	}

	public void setAvailableFrom(LocalDate availableFrom) {
		this.availableFrom = availableFrom;
	}

	public Boolean getImmediateAvailability() {
		return immediateAvailability;
	}

	public void setImmediateAvailability(Boolean immediateAvailability) {
		this.immediateAvailability = immediateAvailability;
	}

	public BigDecimal getMonthlyRent() {
		return monthlyRent;
	}

	public void setMonthlyRent(BigDecimal monthlyRent) {
		this.monthlyRent = monthlyRent;
	}

	public BigDecimal getSecurityDeposit() {
		return securityDeposit;
	}

	public void setSecurityDeposit(BigDecimal securityDeposit) {
		this.securityDeposit = securityDeposit;
	}

	public BigDecimal getMaintenanceCharges() {
		return maintenanceCharges;
	}

	public void setMaintenanceCharges(BigDecimal maintenanceCharges) {
		this.maintenanceCharges = maintenanceCharges;
	}

	public Boolean getElectricityIncluded() {
		return electricityIncluded;
	}

	public void setElectricityIncluded(Boolean electricityIncluded) {
		this.electricityIncluded = electricityIncluded;
	}

	public Boolean getWaterIncluded() {
		return waterIncluded;
	}

	public void setWaterIncluded(Boolean waterIncluded) {
		this.waterIncluded = waterIncluded;
	}

	public BigDecimal getBookingAmount() {
		return bookingAmount;
	}

	public void setBookingAmount(BigDecimal bookingAmount) {
		this.bookingAmount = bookingAmount;
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

	public Instant getCreatedAt() {
		return createdAt;
	}

	public Instant getUpdatedAt() {
		return updatedAt;
	}
}
