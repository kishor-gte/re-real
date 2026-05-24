package com.realestate.main.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.realestate.main.dto.request.PgPropertyCreateRequest;
import com.realestate.main.dto.request.PgPropertyCreateRequest.PgFloorRequest;
import com.realestate.main.dto.request.PgPropertyCreateRequest.PgImageMetaRequest;
import com.realestate.main.dto.request.PgPropertyCreateRequest.PgRoomRequest;
import com.realestate.main.dto.request.PgPropertyCreateRequest.PgRulesPolicyRequest;
import com.realestate.main.dto.request.PgPropertyCreateRequest.PgSharingPriceRequest;
import com.realestate.main.dto.response.PgPropertyListItemResponse;
import com.realestate.main.dto.response.PgPropertyResponse;
import com.realestate.main.dto.response.PgPropertyResponse.PgFloorResponse;
import com.realestate.main.dto.response.PgPropertyResponse.PgImageResponse;
import com.realestate.main.dto.response.PgPropertyResponse.PgRoomResponse;
import com.realestate.main.dto.response.PgPropertyResponse.PgRulesPolicyResponse;
import com.realestate.main.dto.response.PgPropertyResponse.PgSharingPriceResponse;
import com.realestate.main.entity.PgFloor;
import com.realestate.main.entity.PgOwner;
import com.realestate.main.entity.PgProperty;
import com.realestate.main.entity.PgPropertyAmenity;
import com.realestate.main.entity.PgPropertyImage;
import com.realestate.main.entity.PgRoom;
import com.realestate.main.entity.PgRoomSharing;
import com.realestate.main.entity.PgRulesPolicy;
import com.realestate.main.entity.enums.PgImageCategory;
import com.realestate.main.entity.enums.PgPropertyStatus;
import com.realestate.main.entity.enums.PgRoomStatus;
import com.realestate.main.entity.enums.PgSharingType;
import com.realestate.main.exception.AuthException;
import com.realestate.main.repository.PgFloorRepository;
import com.realestate.main.repository.PgPropertyAmenityRepository;
import com.realestate.main.repository.PgPropertyImageRepository;
import com.realestate.main.repository.PgPropertyRepository;
import com.realestate.main.repository.PgRoomRepository;
import com.realestate.main.repository.PgRoomSharingRepository;
import com.realestate.main.repository.PgRulesPolicyRepository;
import com.realestate.main.service.pgsubscription.PgOwnerPostingLimitService;
import com.realestate.main.util.PgAmenityCatalog;

@Service
public class PgPropertyService {

	private final PgPropertyRepository pgPropertyRepository;
	private final PgFloorRepository pgFloorRepository;
	private final PgRoomRepository pgRoomRepository;
	private final PgRoomSharingRepository pgRoomSharingRepository;
	private final PgPropertyAmenityRepository pgPropertyAmenityRepository;
	private final PgPropertyImageRepository pgPropertyImageRepository;
	private final PgRulesPolicyRepository pgRulesPolicyRepository;
	private final FileStorageService fileStorageService;
	private final PgOwnerPostingLimitService postingLimitService;

	public PgPropertyService(PgPropertyRepository pgPropertyRepository, PgFloorRepository pgFloorRepository,
			PgRoomRepository pgRoomRepository, PgRoomSharingRepository pgRoomSharingRepository,
			PgPropertyAmenityRepository pgPropertyAmenityRepository,
			PgPropertyImageRepository pgPropertyImageRepository, PgRulesPolicyRepository pgRulesPolicyRepository,
			FileStorageService fileStorageService, PgOwnerPostingLimitService postingLimitService) {
		this.pgPropertyRepository = pgPropertyRepository;
		this.pgFloorRepository = pgFloorRepository;
		this.pgRoomRepository = pgRoomRepository;
		this.pgRoomSharingRepository = pgRoomSharingRepository;
		this.pgPropertyAmenityRepository = pgPropertyAmenityRepository;
		this.pgPropertyImageRepository = pgPropertyImageRepository;
		this.pgRulesPolicyRepository = pgRulesPolicyRepository;
		this.fileStorageService = fileStorageService;
		this.postingLimitService = postingLimitService;
	}

	@Transactional(rollbackFor = Exception.class)
	public PgPropertyResponse createProperty(PgOwner owner, PgPropertyCreateRequest request,
			List<MultipartFile> images, List<PgImageMetaRequest> imageMeta) {
		postingLimitService.assertCanPost(owner.getId());
		validateCreateRequest(owner.getId(), null, request, images, request.isPublish());
		PgProperty property = mapProperty(new PgProperty(), owner, request);
		property.setPgCode(generatePgCode());
		property.setStatus(request.isPublish() ? PgPropertyStatus.PUBLISHED : PgPropertyStatus.DRAFT);
		property = pgPropertyRepository.save(property);
		clearNestedData(property.getId(), true);
		boolean hasImages = images != null && images.stream().anyMatch(f -> f != null && !f.isEmpty());
		saveNestedData(property, request, owner.getId(), images, imageMeta, hasImages);
		postingLimitService.syncUsageAfterPgCreated(owner.getId());
		return getProperty(owner.getId(), property.getId());
	}

	@Transactional(rollbackFor = Exception.class)
	public PgPropertyResponse updateProperty(PgOwner owner, Long propertyId, PgPropertyCreateRequest request,
			List<MultipartFile> images, List<PgImageMetaRequest> imageMeta) {
		PgProperty property = requireOwnedProperty(owner.getId(), propertyId);
		boolean hasNewImages = images != null && images.stream().anyMatch(f -> f != null && !f.isEmpty());
		validateCreateRequest(owner.getId(), propertyId, request, images, hasNewImages);
		mapProperty(property, owner, request);
		if (request.isPublish()) {
			property.setStatus(PgPropertyStatus.PUBLISHED);
		} else if (property.getStatus() != PgPropertyStatus.INACTIVE) {
			property.setStatus(PgPropertyStatus.DRAFT);
		}
		pgPropertyRepository.save(property);

		List<PgPropertyImage> existingImages = hasNewImages ? List.of()
				: pgPropertyImageRepository.findByPgPropertyIdOrderBySortOrderAscUploadedAtAsc(propertyId);
		clearNestedData(property.getId(), hasNewImages);
		saveNestedData(property, request, owner.getId(), images, imageMeta, hasNewImages);
		if (!hasNewImages) {
			for (PgPropertyImage img : existingImages) {
				PgPropertyImage copy = new PgPropertyImage();
				copy.setPgPropertyId(property.getId());
				copy.setImagePath(img.getImagePath());
				copy.setImageType(img.getImageType());
				copy.setFloorId(img.getFloorId());
				copy.setRoomId(img.getRoomId());
				copy.setSortOrder(img.getSortOrder());
				pgPropertyImageRepository.save(copy);
			}
		}
		return getProperty(owner.getId(), property.getId());
	}

	@Transactional(readOnly = true)
	public List<PgPropertyListItemResponse> listProperties(Long ownerId) {
		return pgPropertyRepository.findByOwnerIdOrderByCreatedAtDesc(ownerId).stream()
				.map(this::toListItem)
				.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public PgPropertyResponse getProperty(Long ownerId, Long propertyId) {
		requireOwnedProperty(ownerId, propertyId);
		return getPropertyById(propertyId);
	}

	@Transactional(readOnly = true)
	public PgPropertyResponse getPublishedProperty(Long propertyId) {
		PgProperty property = pgPropertyRepository.findById(propertyId)
				.filter(p -> p.getStatus() == PgPropertyStatus.PUBLISHED)
				.orElseThrow(() -> new AuthException("PG not found or no longer available"));
		return toDetailResponse(property);
	}

	private PgPropertyResponse getPropertyById(Long propertyId) {
		PgProperty property = pgPropertyRepository.findById(propertyId)
				.orElseThrow(() -> new AuthException("PG property not found"));
		return toDetailResponse(property);
	}

	@Transactional
	public PgPropertyResponse publishProperty(Long ownerId, Long propertyId) {
		PgProperty property = requireOwnedProperty(ownerId, propertyId);
		long imageCount = pgPropertyImageRepository.countByPgPropertyId(propertyId);
		if (imageCount < 3) {
			throw new AuthException("Upload at least 3 images before publishing");
		}
		property.setStatus(PgPropertyStatus.PUBLISHED);
		pgPropertyRepository.save(property);
		return getProperty(ownerId, propertyId);
	}

	@Transactional
	public void deleteProperty(Long ownerId, Long propertyId) {
		PgProperty property = requireOwnedProperty(ownerId, propertyId);
		clearNestedData(property.getId(), true);
		pgPropertyRepository.delete(property);
	}

	@Transactional
	public PgPropertyResponse toggleStatus(Long ownerId, Long propertyId) {
		PgProperty property = requireOwnedProperty(ownerId, propertyId);
		if (property.getStatus() == PgPropertyStatus.PUBLISHED) {
			property.setStatus(PgPropertyStatus.INACTIVE);
		} else if (property.getStatus() == PgPropertyStatus.INACTIVE) {
			property.setStatus(PgPropertyStatus.PUBLISHED);
		} else {
			throw new AuthException("Publish the PG property before toggling availability");
		}
		pgPropertyRepository.save(property);
		return getProperty(ownerId, propertyId);
	}

	@Transactional
	public PgPropertyResponse setPropertyActive(Long ownerId, Long propertyId, boolean active) {
		PgProperty property = requireOwnedProperty(ownerId, propertyId);
		if (active) {
			if (property.getStatus() == PgPropertyStatus.DRAFT) {
				throw new AuthException("Publish the PG property before making it active");
			}
			property.setStatus(PgPropertyStatus.PUBLISHED);
		} else {
			if (property.getStatus() == PgPropertyStatus.DRAFT) {
				throw new AuthException("Draft PG properties are not visible to users");
			}
			property.setStatus(PgPropertyStatus.INACTIVE);
		}
		pgPropertyRepository.save(property);
		return getProperty(ownerId, propertyId);
	}

	private void validateCreateRequest(Long ownerId, Long propertyId, PgPropertyCreateRequest request,
			List<MultipartFile> images, boolean requireImages) {
		boolean draft = !request.isPublish();
		if (request.getPgName() == null || request.getPgName().trim().length() < 3) {
			throw new AuthException("PG name is required (minimum 3 characters)");
		}
		if (request.getPgType() == null) {
			throw new AuthException("PG type is required");
		}
		if (request.getGenderAllowed() == null) {
			throw new AuthException("Gender allowed is required");
		}
		if (!draft) {
			if (request.getAddress() == null || request.getAddress().trim().isEmpty()) {
				throw new AuthException("Address is required");
			}
			if (request.getCity() == null || request.getCity().trim().isEmpty()) {
				throw new AuthException("City is required");
			}
			if (request.getState() == null || request.getState().trim().isEmpty()) {
				throw new AuthException("State is required");
			}
			if (request.getPincode() == null || !request.getPincode().matches("\\d{6}")) {
				throw new AuthException("Valid 6-digit pincode is required");
			}
		}
		boolean duplicateName = propertyId == null
				? pgPropertyRepository.existsByOwnerIdAndPgNameIgnoreCase(ownerId, request.getPgName().trim())
				: pgPropertyRepository.existsByOwnerIdAndPgNameIgnoreCaseAndIdNot(ownerId, request.getPgName().trim(),
						propertyId);
		if (duplicateName) {
			throw new AuthException("You already have a PG property with this name. Open Manage PGs to edit it.");
		}
		if (requireImages) {
			fileStorageService.validatePgPropertyImageBatch(images, true);
		} else if (images != null && !images.isEmpty()) {
			fileStorageService.validatePgPropertyImageBatch(images, false);
		}
	}

	private PgProperty mapProperty(PgProperty property, PgOwner owner, PgPropertyCreateRequest request) {
		property.setOwnerId(owner.getId());
		property.setPgName(request.getPgName().trim());
		property.setPgType(request.getPgType());
		property.setGenderAllowed(request.getGenderAllowed());
		property.setDescription(trimToNull(request.getDescription()));
		property.setOwnerName(firstNonBlank(request.getOwnerName(), owner.getFullName()));
		property.setMobile(owner.getMobile());
		property.setEmail(owner.getEmail());
		property.setAddress(firstNonBlank(request.getAddress(),
				owner.getOfficeAddress() != null ? owner.getOfficeAddress() : "Address to be updated"));
		property.setLandmark(trimToNull(request.getLandmark()));
		property.setCity(firstNonBlank(request.getCity(), owner.getCity()));
		property.setState(firstNonBlank(request.getState(), owner.getState()));
		property.setPincode(firstNonBlank(request.getPincode(), owner.getPincode()));
		property.setMapLocation(trimToNull(request.getMapLocation()));
		property.setNearbyPlaces(trimToNull(request.getNearbyPlaces()));
		property.setTotalFloors(safeInt(request.getTotalFloors()));
		property.setTotalRooms(safeInt(request.getTotalRooms()));
		property.setTotalCapacity(safeInt(request.getTotalCapacity()));
		property.setAvailableBeds(safeInt(request.getAvailableBeds()));
		property.setLiftAvailable(bool(request.getLiftAvailable()));
		property.setParkingAvailable(bool(request.getParkingAvailable()));
		property.setCctvSecurity(bool(request.getCctvSecurity()));
		property.setBiometricEntry(bool(request.getBiometricEntry()));
		property.setFireSafety(bool(request.getFireSafety()));
		property.setAvailableFrom(request.getAvailableFrom());
		property.setImmediateAvailability(bool(request.getImmediateAvailability()));
		property.setMonthlyRent(request.getMonthlyRent());
		property.setSecurityDeposit(request.getSecurityDeposit());
		property.setMaintenanceCharges(request.getMaintenanceCharges());
		property.setElectricityIncluded(bool(request.getElectricityIncluded()));
		property.setWaterIncluded(bool(request.getWaterIncluded()));
		property.setBookingAmount(request.getBookingAmount());
		property.setFeatured(request.isFeatured());
		return property;
	}

	private void saveNestedData(PgProperty property, PgPropertyCreateRequest request, Long ownerId,
			List<MultipartFile> images, List<PgImageMetaRequest> imageMeta, boolean saveImages) {
		List<PgFloor> savedFloors = new ArrayList<>();
		Map<String, PgRoom> roomKeyMap = new HashMap<>();
		int floorSort = 0;
		for (PgFloorRequest floorReq : dedupeFloors(request.getFloors())) {
			PgFloor floor = new PgFloor();
			floor.setPgPropertyId(property.getId());
			floor.setFloorNumber(safeInt(floorReq.getFloorNumber()));
			floor.setTotalRooms(safeInt(floorReq.getTotalRooms()));
			floor.setFloorDescription(trimToNull(floorReq.getFloorDescription()));
			floor.setSortOrder(floorSort++);
			floor = pgFloorRepository.save(floor);
			savedFloors.add(floor);
			int roomIndex = 0;
			for (PgRoomRequest roomReq : floorReq.getRooms()) {
				PgRoom room = new PgRoom();
				room.setFloorId(floor.getId());
				room.setPgPropertyId(property.getId());
				room.setRoomNumber(roomReq.getRoomNumber() != null ? roomReq.getRoomNumber().trim() : "Room");
				room.setRoomType(roomReq.getRoomType());
				room.setRoomSize(trimToNull(roomReq.getRoomSize()));
				room.setAttachedBathroom(bool(roomReq.getAttachedBathroom()));
				room.setBalconyAvailable(bool(roomReq.getBalconyAvailable()));
				room.setAcAvailable(bool(roomReq.getAcAvailable()));
				room.setFurnishing(roomReq.getFurnishing());
				room.setSharingType(roomReq.getSharingType());
				int totalBeds = safeInt(roomReq.getTotalBeds());
				int occupied = safeInt(roomReq.getOccupiedBeds());
				int available = roomReq.getAvailableBeds() != null ? roomReq.getAvailableBeds()
						: Math.max(0, totalBeds - occupied);
				room.setTotalBeds(totalBeds);
				room.setOccupiedBeds(occupied);
				room.setAvailableBeds(available);
				room.setRoomPrice(roomReq.getRoomPrice());
				room.setRoomStatus(roomReq.getRoomStatus() != null ? roomReq.getRoomStatus() : PgRoomStatus.AVAILABLE);
				room = pgRoomRepository.save(room);
				roomKeyMap.put(floorSort - 1 + ":" + roomIndex, room);
				roomIndex++;
			}
		}

		for (PgSharingPriceRequest priceReq : dedupeSharingPrices(request.getSharingPrices())) {
			if (priceReq.getSharingType() == null || priceReq.getMonthlyRent() == null) {
				continue;
			}
			PgRoomSharing sharing = new PgRoomSharing();
			sharing.setPgPropertyId(property.getId());
			sharing.setSharingType(priceReq.getSharingType());
			sharing.setMonthlyRent(priceReq.getMonthlyRent());
			pgRoomSharingRepository.save(sharing);
		}

		for (String amenity : dedupeAmenities(request.getAmenities())) {
			PgPropertyAmenity entity = new PgPropertyAmenity();
			entity.setPgPropertyId(property.getId());
			entity.setAmenityCode(amenity);
			pgPropertyAmenityRepository.save(entity);
		}

		PgRulesPolicyRequest rulesReq = request.getRules();
		pgRulesPolicyRepository.deleteByPgPropertyId(property.getId());
		if (rulesReq != null) {
			PgRulesPolicy rules = new PgRulesPolicy();
			rules.setPgPropertyId(property.getId());
			rules.setNoSmoking(bool(rulesReq.getNoSmoking()));
			rules.setNoAlcohol(bool(rulesReq.getNoAlcohol()));
			rules.setVisitorsAllowed(bool(rulesReq.getVisitorsAllowed()));
			rules.setPetsAllowed(bool(rulesReq.getPetsAllowed()));
			rules.setCurfewEnabled(bool(rulesReq.getCurfewEnabled()));
			rules.setIdProofMandatory(bool(rulesReq.getIdProofMandatory()));
			rules.setCurfewTiming(trimToNull(rulesReq.getCurfewTiming()));
			rules.setNoticePeriodDays(rulesReq.getNoticePeriodDays());
			rules.setSecurityDepositAmount(rulesReq.getSecurityDepositAmount());
			pgRulesPolicyRepository.save(rules);
		}

		if (saveImages && images != null) {
			int sort = 0;
			for (int i = 0; i < images.size(); i++) {
				MultipartFile file = images.get(i);
				if (file == null || file.isEmpty()) {
					continue;
				}
				String path = fileStorageService.storePgPropertyImage(file, ownerId, property.getId());
				PgPropertyImage img = new PgPropertyImage();
				img.setPgPropertyId(property.getId());
				img.setImagePath(path);
				img.setSortOrder(sort++);
				PgImageMetaRequest meta = imageMeta != null && i < imageMeta.size() ? imageMeta.get(i) : null;
				if (meta != null) {
					img.setImageType(meta.getImageType() != null ? meta.getImageType() : PgImageCategory.BUILDING);
					if (meta.getFloorIndex() != null && meta.getRoomIndex() != null) {
						PgRoom room = roomKeyMap.get(meta.getFloorIndex() + ":" + meta.getRoomIndex());
						if (room != null) {
							img.setRoomId(room.getId());
							img.setFloorId(room.getFloorId());
						}
					} else if (meta.getFloorIndex() != null && meta.getFloorIndex() >= 0
							&& meta.getFloorIndex() < savedFloors.size()) {
						img.setFloorId(savedFloors.get(meta.getFloorIndex()).getId());
					}
				} else {
					img.setImageType(PgImageCategory.BUILDING);
				}
				pgPropertyImageRepository.save(img);
			}
		}

		recalculateAggregates(property);
	}

	private void recalculateAggregates(PgProperty property) {
		List<PgFloor> floors = pgFloorRepository.findByPgPropertyIdOrderBySortOrderAscFloorNumberAsc(property.getId());
		List<PgRoom> rooms = pgRoomRepository.findByPgPropertyIdOrderByFloorIdAscRoomNumberAsc(property.getId());
		if (property.getTotalFloors() == null || property.getTotalFloors() == 0) {
			property.setTotalFloors(floors.size());
		}
		if (property.getTotalRooms() == null || property.getTotalRooms() == 0) {
			property.setTotalRooms(rooms.size());
		}
		int capacity = rooms.stream().mapToInt(r -> safeInt(r.getTotalBeds())).sum();
		int available = rooms.stream().mapToInt(r -> safeInt(r.getAvailableBeds())).sum();
		if (property.getTotalCapacity() == null || property.getTotalCapacity() == 0) {
			property.setTotalCapacity(capacity);
		}
		if (property.getAvailableBeds() == null) {
			property.setAvailableBeds(available);
		}
		if (property.getMonthlyRent() == null) {
			pgRoomSharingRepository.findByPgPropertyId(property.getId()).stream()
					.map(PgRoomSharing::getMonthlyRent)
					.filter(r -> r != null)
					.min(BigDecimal::compareTo)
					.ifPresent(property::setMonthlyRent);
		}
		pgPropertyRepository.save(property);
	}

	private void clearNestedData(Long propertyId, boolean includeImages) {
		if (includeImages) {
			pgPropertyImageRepository.deleteByPgPropertyId(propertyId);
		}
		pgPropertyAmenityRepository.deleteByPgPropertyId(propertyId);
		pgRoomSharingRepository.deleteByPgPropertyId(propertyId);
		pgRoomRepository.deleteByPgPropertyId(propertyId);
		pgFloorRepository.deleteByPgPropertyId(propertyId);
		pgRulesPolicyRepository.deleteByPgPropertyId(propertyId);
	}

	private PgProperty requireOwnedProperty(Long ownerId, Long propertyId) {
		return pgPropertyRepository.findByIdAndOwnerId(propertyId, ownerId)
				.orElseThrow(() -> new AuthException("PG property not found"));
	}

	private String generatePgCode() {
		return "PG-" + java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 12)
				.toUpperCase(java.util.Locale.ROOT);
	}

	private List<PgFloorRequest> dedupeFloors(List<PgFloorRequest> floors) {
		if (floors == null || floors.isEmpty()) {
			return List.of();
		}
		Map<Integer, PgFloorRequest> byNumber = new LinkedHashMap<>();
		int auto = 1;
		for (PgFloorRequest floor : floors) {
			if (floor == null) {
				continue;
			}
			int key = floor.getFloorNumber() != null ? floor.getFloorNumber() : auto++;
			byNumber.putIfAbsent(key, floor);
		}
		return new ArrayList<>(byNumber.values());
	}

	private List<PgSharingPriceRequest> dedupeSharingPrices(List<PgSharingPriceRequest> prices) {
		if (prices == null || prices.isEmpty()) {
			return List.of();
		}
		Map<PgSharingType, PgSharingPriceRequest> byType = new LinkedHashMap<>();
		for (PgSharingPriceRequest price : prices) {
			if (price == null || price.getSharingType() == null || price.getMonthlyRent() == null) {
				continue;
			}
			byType.put(price.getSharingType(), price);
		}
		return new ArrayList<>(byType.values());
	}

	private List<String> dedupeAmenities(List<String> amenities) {
		if (amenities == null || amenities.isEmpty()) {
			return List.of();
		}
		return amenities.stream()
				.filter(code -> code != null && PgAmenityCatalog.ALLOWED.contains(code))
				.distinct()
				.collect(Collectors.toList());
	}

	private PgPropertyListItemResponse toListItem(PgProperty property) {
		PgPropertyListItemResponse item = new PgPropertyListItemResponse();
		item.setId(property.getId());
		item.setPgCode(property.getPgCode());
		item.setPgName(property.getPgName());
		item.setPgType(property.getPgType());
		item.setGenderAllowed(property.getGenderAllowed());
		item.setCity(property.getCity());
		item.setState(property.getState());
		item.setTotalRooms(property.getTotalRooms());
		item.setAvailableBeds(property.getAvailableBeds());
		item.setMonthlyRent(property.getMonthlyRent());
		item.setStatus(property.getStatus());
		item.setFeatured(property.getFeatured());
		item.setCreatedAt(property.getCreatedAt());
		pgPropertyImageRepository.findByPgPropertyIdOrderBySortOrderAscUploadedAtAsc(property.getId()).stream()
				.findFirst()
				.ifPresent(img -> item.setCoverImage(img.getImagePath()));
		return item;
	}

	private PgPropertyResponse toDetailResponse(PgProperty property) {
		PgPropertyResponse response = new PgPropertyResponse();
		response.setId(property.getId());
		response.setPgCode(property.getPgCode());
		response.setPgName(property.getPgName());
		response.setPgType(property.getPgType());
		response.setGenderAllowed(property.getGenderAllowed());
		response.setDescription(property.getDescription());
		response.setOwnerName(property.getOwnerName());
		response.setMobile(property.getMobile());
		response.setEmail(property.getEmail());
		response.setAddress(property.getAddress());
		response.setLandmark(property.getLandmark());
		response.setCity(property.getCity());
		response.setState(property.getState());
		response.setPincode(property.getPincode());
		response.setMapLocation(property.getMapLocation());
		response.setNearbyPlaces(property.getNearbyPlaces());
		response.setTotalFloors(property.getTotalFloors());
		response.setTotalRooms(property.getTotalRooms());
		response.setTotalCapacity(property.getTotalCapacity());
		response.setAvailableBeds(property.getAvailableBeds());
		response.setLiftAvailable(property.getLiftAvailable());
		response.setParkingAvailable(property.getParkingAvailable());
		response.setCctvSecurity(property.getCctvSecurity());
		response.setBiometricEntry(property.getBiometricEntry());
		response.setFireSafety(property.getFireSafety());
		response.setAvailableFrom(property.getAvailableFrom());
		response.setImmediateAvailability(property.getImmediateAvailability());
		response.setMonthlyRent(property.getMonthlyRent());
		response.setSecurityDeposit(property.getSecurityDeposit());
		response.setMaintenanceCharges(property.getMaintenanceCharges());
		response.setElectricityIncluded(property.getElectricityIncluded());
		response.setWaterIncluded(property.getWaterIncluded());
		response.setBookingAmount(property.getBookingAmount());
		response.setStatus(property.getStatus());
		response.setFeatured(property.getFeatured());
		response.setCreatedAt(property.getCreatedAt());
		response.setUpdatedAt(property.getUpdatedAt());

		List<PgFloor> floors = pgFloorRepository.findByPgPropertyIdOrderBySortOrderAscFloorNumberAsc(property.getId());
		List<PgRoom> rooms = pgRoomRepository.findByPgPropertyIdOrderByFloorIdAscRoomNumberAsc(property.getId());
		Map<Long, List<PgRoom>> roomsByFloor = rooms.stream().collect(Collectors.groupingBy(PgRoom::getFloorId));
		List<PgFloorResponse> floorResponses = new ArrayList<>();
		for (PgFloor floor : floors) {
			PgFloorResponse fr = new PgFloorResponse();
			fr.setId(floor.getId());
			fr.setFloorNumber(floor.getFloorNumber());
			fr.setTotalRooms(floor.getTotalRooms());
			fr.setFloorDescription(floor.getFloorDescription());
			List<PgRoomResponse> roomResponses = roomsByFloor.getOrDefault(floor.getId(), List.of()).stream()
					.map(this::toRoomResponse)
					.collect(Collectors.toList());
			fr.setRooms(roomResponses);
			floorResponses.add(fr);
		}
		response.setFloors(floorResponses);

		List<PgSharingPriceResponse> prices = pgRoomSharingRepository.findByPgPropertyId(property.getId()).stream()
				.map(s -> {
					PgSharingPriceResponse pr = new PgSharingPriceResponse();
					pr.setId(s.getId());
					pr.setSharingType(s.getSharingType());
					pr.setMonthlyRent(s.getMonthlyRent());
					return pr;
				})
				.collect(Collectors.toList());
		response.setSharingPrices(prices);

		response.setAmenities(pgPropertyAmenityRepository.findByPgPropertyId(property.getId()).stream()
				.map(PgPropertyAmenity::getAmenityCode)
				.collect(Collectors.toList()));

		pgRulesPolicyRepository.findByPgPropertyId(property.getId()).ifPresent(rules -> {
			PgRulesPolicyResponse rr = new PgRulesPolicyResponse();
			rr.setNoSmoking(rules.getNoSmoking());
			rr.setNoAlcohol(rules.getNoAlcohol());
			rr.setVisitorsAllowed(rules.getVisitorsAllowed());
			rr.setPetsAllowed(rules.getPetsAllowed());
			rr.setCurfewEnabled(rules.getCurfewEnabled());
			rr.setIdProofMandatory(rules.getIdProofMandatory());
			rr.setCurfewTiming(rules.getCurfewTiming());
			rr.setNoticePeriodDays(rules.getNoticePeriodDays());
			rr.setSecurityDepositAmount(rules.getSecurityDepositAmount());
			response.setRules(rr);
		});

		List<PgImageResponse> images = pgPropertyImageRepository
				.findByPgPropertyIdOrderBySortOrderAscUploadedAtAsc(property.getId()).stream()
				.map(img -> {
					PgImageResponse ir = new PgImageResponse();
					ir.setId(img.getId());
					ir.setImagePath(img.getImagePath());
					ir.setImageType(img.getImageType());
					ir.setFloorId(img.getFloorId());
					ir.setRoomId(img.getRoomId());
					ir.setSortOrder(img.getSortOrder());
					return ir;
				})
				.collect(Collectors.toList());
		response.setImages(images);
		return response;
	}

	private PgRoomResponse toRoomResponse(PgRoom room) {
		PgRoomResponse rr = new PgRoomResponse();
		rr.setId(room.getId());
		rr.setFloorId(room.getFloorId());
		rr.setRoomNumber(room.getRoomNumber());
		rr.setRoomType(room.getRoomType());
		rr.setRoomSize(room.getRoomSize());
		rr.setAttachedBathroom(room.getAttachedBathroom());
		rr.setBalconyAvailable(room.getBalconyAvailable());
		rr.setAcAvailable(room.getAcAvailable());
		rr.setFurnishing(room.getFurnishing());
		rr.setSharingType(room.getSharingType());
		rr.setTotalBeds(room.getTotalBeds());
		rr.setOccupiedBeds(room.getOccupiedBeds());
		rr.setAvailableBeds(room.getAvailableBeds());
		rr.setRoomPrice(room.getRoomPrice());
		rr.setRoomStatus(room.getRoomStatus());
		return rr;
	}

	private static String trimToNull(String value) {
		if (value == null) {
			return null;
		}
		String trimmed = value.trim();
		return trimmed.isEmpty() ? null : trimmed;
	}

	private static String firstNonBlank(String a, String b) {
		if (a != null && !a.trim().isEmpty()) {
			return a.trim();
		}
		return b != null ? b.trim() : null;
	}

	private static boolean bool(Boolean value) {
		return Boolean.TRUE.equals(value);
	}

	private static int safeInt(Integer value) {
		return value != null ? value : 0;
	}
}
