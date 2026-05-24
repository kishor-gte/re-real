package com.realestate.main.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.realestate.main.entity.enums.GenderAllowed;
import com.realestate.main.entity.enums.PgFurnishingType;
import com.realestate.main.entity.enums.PgImageCategory;
import com.realestate.main.entity.enums.PgRoomStatus;
import com.realestate.main.entity.enums.PgRoomType;
import com.realestate.main.entity.enums.PgSharingType;
import com.realestate.main.entity.enums.PgType;

public class PgPropertyCreateRequest {

	private String pgName;
	private PgType pgType;
	private GenderAllowed genderAllowed;
	private String description;
	private String ownerName;
	private String mobile;
	private String email;
	private String address;
	private String landmark;
	private String city;
	private String state;
	private String pincode;
	private String mapLocation;
	private String nearbyPlaces;

	private Integer totalFloors;
	private Integer totalRooms;
	private Integer totalCapacity;
	private Boolean liftAvailable;
	private Boolean parkingAvailable;
	private Boolean cctvSecurity;
	private Boolean biometricEntry;
	private Boolean fireSafety;

	private LocalDate availableFrom;
	private Boolean immediateAvailability;
	private Integer availableBeds;

	private BigDecimal monthlyRent;
	private BigDecimal securityDeposit;
	private BigDecimal maintenanceCharges;
	private Boolean electricityIncluded;
	private Boolean waterIncluded;
	private BigDecimal bookingAmount;

	private List<PgFloorRequest> floors = new ArrayList<>();
	private List<PgSharingPriceRequest> sharingPrices = new ArrayList<>();
	private List<String> amenities = new ArrayList<>();
	private PgRulesPolicyRequest rules;

	private boolean publish;
	private boolean featured;

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

	public List<PgFloorRequest> getFloors() {
		return floors;
	}

	public void setFloors(List<PgFloorRequest> floors) {
		this.floors = floors != null ? floors : new ArrayList<>();
	}

	public List<PgSharingPriceRequest> getSharingPrices() {
		return sharingPrices;
	}

	public void setSharingPrices(List<PgSharingPriceRequest> sharingPrices) {
		this.sharingPrices = sharingPrices != null ? sharingPrices : new ArrayList<>();
	}

	public List<String> getAmenities() {
		return amenities;
	}

	public void setAmenities(List<String> amenities) {
		this.amenities = amenities != null ? amenities : new ArrayList<>();
	}

	public PgRulesPolicyRequest getRules() {
		return rules;
	}

	public void setRules(PgRulesPolicyRequest rules) {
		this.rules = rules;
	}

	public boolean isPublish() {
		return publish;
	}

	public void setPublish(boolean publish) {
		this.publish = publish;
	}

	public boolean isFeatured() {
		return featured;
	}

	public void setFeatured(boolean featured) {
		this.featured = featured;
	}

	public static class PgFloorRequest {
		private Integer floorNumber;
		private Integer totalRooms;
		private String floorDescription;
		private List<PgRoomRequest> rooms = new ArrayList<>();

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

		public List<PgRoomRequest> getRooms() {
			return rooms;
		}

		public void setRooms(List<PgRoomRequest> rooms) {
			this.rooms = rooms != null ? rooms : new ArrayList<>();
		}
	}

	public static class PgRoomRequest {
		private String roomNumber;
		private PgRoomType roomType;
		private String roomSize;
		private Boolean attachedBathroom;
		private Boolean balconyAvailable;
		private Boolean acAvailable;
		private PgFurnishingType furnishing;
		private PgSharingType sharingType;
		private Integer totalBeds;
		private Integer occupiedBeds;
		private Integer availableBeds;
		private BigDecimal roomPrice;
		private PgRoomStatus roomStatus;

		public String getRoomNumber() {
			return roomNumber;
		}

		public void setRoomNumber(String roomNumber) {
			this.roomNumber = roomNumber;
		}

		public PgRoomType getRoomType() {
			return roomType;
		}

		public void setRoomType(PgRoomType roomType) {
			this.roomType = roomType;
		}

		public String getRoomSize() {
			return roomSize;
		}

		public void setRoomSize(String roomSize) {
			this.roomSize = roomSize;
		}

		public Boolean getAttachedBathroom() {
			return attachedBathroom;
		}

		public void setAttachedBathroom(Boolean attachedBathroom) {
			this.attachedBathroom = attachedBathroom;
		}

		public Boolean getBalconyAvailable() {
			return balconyAvailable;
		}

		public void setBalconyAvailable(Boolean balconyAvailable) {
			this.balconyAvailable = balconyAvailable;
		}

		public Boolean getAcAvailable() {
			return acAvailable;
		}

		public void setAcAvailable(Boolean acAvailable) {
			this.acAvailable = acAvailable;
		}

		public PgFurnishingType getFurnishing() {
			return furnishing;
		}

		public void setFurnishing(PgFurnishingType furnishing) {
			this.furnishing = furnishing;
		}

		public PgSharingType getSharingType() {
			return sharingType;
		}

		public void setSharingType(PgSharingType sharingType) {
			this.sharingType = sharingType;
		}

		public Integer getTotalBeds() {
			return totalBeds;
		}

		public void setTotalBeds(Integer totalBeds) {
			this.totalBeds = totalBeds;
		}

		public Integer getOccupiedBeds() {
			return occupiedBeds;
		}

		public void setOccupiedBeds(Integer occupiedBeds) {
			this.occupiedBeds = occupiedBeds;
		}

		public Integer getAvailableBeds() {
			return availableBeds;
		}

		public void setAvailableBeds(Integer availableBeds) {
			this.availableBeds = availableBeds;
		}

		public BigDecimal getRoomPrice() {
			return roomPrice;
		}

		public void setRoomPrice(BigDecimal roomPrice) {
			this.roomPrice = roomPrice;
		}

		public PgRoomStatus getRoomStatus() {
			return roomStatus;
		}

		public void setRoomStatus(PgRoomStatus roomStatus) {
			this.roomStatus = roomStatus;
		}
	}

	public static class PgSharingPriceRequest {
		private PgSharingType sharingType;
		private BigDecimal monthlyRent;

		public PgSharingType getSharingType() {
			return sharingType;
		}

		public void setSharingType(PgSharingType sharingType) {
			this.sharingType = sharingType;
		}

		public BigDecimal getMonthlyRent() {
			return monthlyRent;
		}

		public void setMonthlyRent(BigDecimal monthlyRent) {
			this.monthlyRent = monthlyRent;
		}
	}

	public static class PgRulesPolicyRequest {
		private Boolean noSmoking;
		private Boolean noAlcohol;
		private Boolean visitorsAllowed;
		private Boolean petsAllowed;
		private Boolean curfewEnabled;
		private Boolean idProofMandatory;
		private String curfewTiming;
		private Integer noticePeriodDays;
		private BigDecimal securityDepositAmount;

		public Boolean getNoSmoking() {
			return noSmoking;
		}

		public void setNoSmoking(Boolean noSmoking) {
			this.noSmoking = noSmoking;
		}

		public Boolean getNoAlcohol() {
			return noAlcohol;
		}

		public void setNoAlcohol(Boolean noAlcohol) {
			this.noAlcohol = noAlcohol;
		}

		public Boolean getVisitorsAllowed() {
			return visitorsAllowed;
		}

		public void setVisitorsAllowed(Boolean visitorsAllowed) {
			this.visitorsAllowed = visitorsAllowed;
		}

		public Boolean getPetsAllowed() {
			return petsAllowed;
		}

		public void setPetsAllowed(Boolean petsAllowed) {
			this.petsAllowed = petsAllowed;
		}

		public Boolean getCurfewEnabled() {
			return curfewEnabled;
		}

		public void setCurfewEnabled(Boolean curfewEnabled) {
			this.curfewEnabled = curfewEnabled;
		}

		public Boolean getIdProofMandatory() {
			return idProofMandatory;
		}

		public void setIdProofMandatory(Boolean idProofMandatory) {
			this.idProofMandatory = idProofMandatory;
		}

		public String getCurfewTiming() {
			return curfewTiming;
		}

		public void setCurfewTiming(String curfewTiming) {
			this.curfewTiming = curfewTiming;
		}

		public Integer getNoticePeriodDays() {
			return noticePeriodDays;
		}

		public void setNoticePeriodDays(Integer noticePeriodDays) {
			this.noticePeriodDays = noticePeriodDays;
		}

		public BigDecimal getSecurityDepositAmount() {
			return securityDepositAmount;
		}

		public void setSecurityDepositAmount(BigDecimal securityDepositAmount) {
			this.securityDepositAmount = securityDepositAmount;
		}
	}

	public static class PgImageMetaRequest {
		private PgImageCategory imageType;
		private Integer floorIndex;
		private Integer roomIndex;

		public PgImageCategory getImageType() {
			return imageType;
		}

		public void setImageType(PgImageCategory imageType) {
			this.imageType = imageType;
		}

		public Integer getFloorIndex() {
			return floorIndex;
		}

		public void setFloorIndex(Integer floorIndex) {
			this.floorIndex = floorIndex;
		}

		public Integer getRoomIndex() {
			return roomIndex;
		}

		public void setRoomIndex(Integer roomIndex) {
			this.roomIndex = roomIndex;
		}
	}
}
