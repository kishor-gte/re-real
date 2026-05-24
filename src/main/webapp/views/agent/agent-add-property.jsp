<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ include file="../includes/init.jsp" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${editMode ? 'Edit Property' : 'Add Property'} | Agent Portal</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${ctx}/css/agent.css">
    <link rel="stylesheet" href="${ctx}/css/agent-pages.css">
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
</head>
<body class="agent-body agent-bg-grid">
<%@ include file="../includes/agent-particles.jsp" %>
<div class="agent-dashboard-wrap">
    <%@ include file="../includes/agent-sidebar.jsp" %>
    <div class="agent-main">
        <header class="agent-topbar">
            <h2 class="agent-topbar-title">${editMode ? 'Edit' : 'Add'} <span>Property</span></h2>
            <a href="${ctx}/agent/properties" class="agent-btn-outline">Back to list</a>
        </header>
        <div class="agent-content">
            <div class="agent-panel mb-3">
                <p class="mb-0 small" style="color:var(--agent-muted);">
                    Listing as <strong style="color:var(--agent-gold);">${specialization}</strong> specialist.
                    Fields below match your broker category.
                </p>
            </div>
            <div id="toast-container" class="toast-container"></div>
            <div id="loader" class="loader-overlay"><div class="spinner"></div></div>

            <form id="addPropertyForm" class="agent-form-grid" enctype="multipart/form-data" novalidate>
                <input type="hidden" id="specializationKey" value="${specializationKey}">
                <input type="hidden" id="propertyId" value="${propertyId != null ? propertyId : ''}">
                <input type="hidden" id="editMode" value="${editMode ? 'true' : 'false'}">

                <div class="agent-panel">
                    <h3>Basic details</h3>
                    <div class="agent-form-group">
                        <input class="agent-form-control form-control" id="title" name="title" placeholder=" " required maxlength="150">
                        <label class="agent-floating-label" for="title">Property title *</label>
                    </div>
                    <p class="agent-prop-section-title">Listing &amp; category</p>
                    <div class="agent-form-row-2">
                        <div class="agent-form-group agent-form-group--select">
                            <select class="agent-form-select form-select" id="listingType" name="listingType" required>
                                <option value="SALE">For Sale</option>
                                <option value="RENT">For Rent</option>
                            </select>
                            <label class="agent-floating-label" for="listingType">Listing type *</label>
                        </div>
                        <div class="agent-form-group agent-form-group--select">
                            <select class="agent-form-select form-select" id="propertySubType" name="propertySubType" required>
                                <option value="" disabled selected>Select category</option>
                                <c:forEach var="st" items="${propertySubTypes}">
                                    <option value="${st}">${st}</option>
                                </c:forEach>
                            </select>
                            <label class="agent-floating-label" for="propertySubType">Property category *</label>
                        </div>
                    </div>
                    <div class="agent-form-row-2">
                        <div class="agent-form-group">
                            <input class="agent-form-control form-control" id="price" name="price" type="number" min="1" step="0.01" placeholder=" " required>
                            <label class="agent-floating-label" for="price">Price (&#8377;) *</label>
                        </div>
                        <div class="agent-form-group d-flex align-items-center pt-3">
                            <div class="form-check">
                                <input class="form-check-input" type="checkbox" id="priceNegotiable" name="priceNegotiable" value="true" checked>
                                <label class="form-check-label" for="priceNegotiable" style="color:var(--agent-muted);">Price negotiable</label>
                            </div>
                        </div>
                    </div>
                    <div class="agent-form-group">
                        <textarea class="agent-form-control form-control" id="description" name="description" rows="4" placeholder=" " maxlength="5000"></textarea>
                        <label class="agent-floating-label" for="description">Description</label>
                    </div>
                </div>

                <div class="agent-panel spec-block" data-spec="RESIDENTIAL APARTMENTS RENTAL LUXURY_VILLAS COMMERCIAL">
                    <h3>Area &amp; layout</h3>
                    <p class="agent-prop-section-title spec-field" data-spec="RESIDENTIAL APARTMENTS RENTAL LUXURY_VILLAS">Bedrooms &amp; furnishing</p>
                    <div class="agent-form-row-2 spec-field" data-spec="RESIDENTIAL APARTMENTS RENTAL LUXURY_VILLAS">
                        <div class="agent-form-group agent-form-group--select">
                            <select class="agent-form-select form-select" id="bhk" name="bhk">
                                <option value="" disabled selected>Select BHK</option>
                                <option value="Studio">Studio</option>
                                <option value="1 BHK">1 BHK</option>
                                <option value="2 BHK">2 BHK</option>
                                <option value="3 BHK">3 BHK</option>
                                <option value="4 BHK">4 BHK</option>
                                <option value="5+ BHK">5+ BHK</option>
                            </select>
                            <label class="agent-floating-label" for="bhk">BHK configuration *</label>
                        </div>
                        <div class="agent-form-group agent-form-group--select">
                            <select class="agent-form-select form-select" id="furnishing" name="furnishing">
                                <option value="" disabled selected>Select</option>
                                <option value="UNFURNISHED">Unfurnished</option>
                                <option value="SEMI_FURNISHED">Semi-furnished</option>
                                <option value="FULLY_FURNISHED">Fully furnished</option>
                            </select>
                            <label class="agent-floating-label" for="furnishing">Furnishing</label>
                        </div>
                    </div>
                    <p class="agent-prop-section-title spec-field" data-spec="RESIDENTIAL APARTMENTS RENTAL LUXURY_VILLAS COMMERCIAL PLOTS_LANDS">Size &amp; dimensions</p>
                    <div class="agent-form-row-2">
                        <div class="agent-form-group spec-field" data-spec="RESIDENTIAL APARTMENTS RENTAL LUXURY_VILLAS COMMERCIAL">
                            <input class="agent-form-control form-control" id="areaSqFt" name="areaSqFt" type="number" min="1" step="1" placeholder=" ">
                            <label class="agent-floating-label" for="areaSqFt">Built-up / carpet area (sq.ft)</label>
                        </div>
                        <div class="agent-form-group spec-field" data-spec="COMMERCIAL">
                            <input class="agent-form-control form-control" id="superBuiltUpSqFt" name="superBuiltUpSqFt" type="number" min="1" step="1" placeholder=" ">
                            <label class="agent-floating-label" for="superBuiltUpSqFt">Super built-up (sq.ft)</label>
                        </div>
                    </div>
                    <p class="agent-prop-section-title spec-field" data-spec="RESIDENTIAL APARTMENTS RENTAL LUXURY_VILLAS">Additional details</p>
                    <div class="agent-form-row-2 spec-field" data-spec="RESIDENTIAL APARTMENTS RENTAL LUXURY_VILLAS">
                        <div class="agent-form-group">
                            <input class="agent-form-control form-control" id="bathrooms" name="bathrooms" type="number" min="0" max="20" placeholder=" ">
                            <label class="agent-floating-label" for="bathrooms">Bathrooms</label>
                        </div>
                        <div class="agent-form-group">
                            <input class="agent-form-control form-control" id="balconies" name="balconies" type="number" min="0" max="10" placeholder=" ">
                            <label class="agent-floating-label" for="balconies">Balconies</label>
                        </div>
                    </div>
                    <div class="agent-form-row-2 spec-field" data-spec="RESIDENTIAL APARTMENTS RENTAL LUXURY_VILLAS">
                        <div class="agent-form-group">
                            <input class="agent-form-control form-control" id="floorNumber" name="floorNumber" type="number" min="0" placeholder=" ">
                            <label class="agent-floating-label" for="floorNumber">Floor no.</label>
                        </div>
                        <div class="agent-form-group">
                            <input class="agent-form-control form-control" id="totalFloors" name="totalFloors" type="number" min="1" placeholder=" ">
                            <label class="agent-floating-label" for="totalFloors">Total floors</label>
                        </div>
                    </div>
                    <div class="agent-form-row-2 spec-field" data-spec="RESIDENTIAL APARTMENTS RENTAL LUXURY_VILLAS">
                        <div class="agent-form-group">
                            <input class="agent-form-control form-control" id="propertyAgeYears" name="propertyAgeYears" type="number" min="0" max="100" placeholder=" ">
                            <label class="agent-floating-label" for="propertyAgeYears">Property age (years)</label>
                        </div>
                        <div class="agent-form-group">
                            <input class="agent-form-control form-control" id="parkingSlots" name="parkingSlots" type="number" min="0" max="20" placeholder=" ">
                            <label class="agent-floating-label" for="parkingSlots">Parking slots</label>
                        </div>
                    </div>
                    <div class="agent-form-row-2 spec-field" data-spec="LUXURY_VILLAS">
                        <div class="agent-form-group d-flex align-items-center pt-3">
                            <div class="form-check me-3">
                                <input class="form-check-input" type="checkbox" id="privatePool" name="privatePool" value="true">
                                <label class="form-check-label" for="privatePool">Private pool</label>
                            </div>
                            <div class="form-check">
                                <input class="form-check-input" type="checkbox" id="privateGarden" name="privateGarden" value="true">
                                <label class="form-check-label" for="privateGarden">Private garden</label>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="agent-panel spec-field" data-spec="COMMERCIAL">
                    <h3>Commercial details</h3>
                    <div class="agent-form-row-2">
                        <div class="agent-form-group agent-form-group--select">
                            <select class="agent-form-select form-select" id="commercialType" name="commercialType">
                                <option value="" disabled selected>Select type</option>
                                <option value="OFFICE">Office</option>
                                <option value="RETAIL_SHOP">Retail shop</option>
                                <option value="SHOWROOM">Showroom</option>
                                <option value="WAREHOUSE">Warehouse</option>
                                <option value="CO_WORKING">Co-working</option>
                                <option value="INDUSTRIAL">Industrial</option>
                                <option value="OTHER">Other</option>
                            </select>
                            <label class="agent-floating-label" for="commercialType">Commercial type</label>
                        </div>
                        <div class="agent-form-group">
                            <input class="agent-form-control form-control" id="seatsCapacity" name="seatsCapacity" type="number" min="1" placeholder=" ">
                            <label class="agent-floating-label" for="seatsCapacity">Seats / capacity</label>
                        </div>
                    </div>
                </div>

                <div class="agent-panel spec-field" data-spec="PLOTS_LANDS LUXURY_VILLAS">
                    <h3>Plot size</h3>
                    <div class="agent-form-row-2">
                        <div class="agent-form-group">
                            <input class="agent-form-control form-control" id="plotAreaSqFt" name="plotAreaSqFt" type="number" min="1" step="1" placeholder=" ">
                            <label class="agent-floating-label" for="plotAreaSqFt">Plot area (sq.ft) *</label>
                        </div>
                    </div>
                </div>

                <div class="agent-panel spec-field" data-spec="PLOTS_LANDS">
                    <h3>Plot &amp; land details</h3>
                    <div class="agent-form-row-2">
                        <div class="agent-form-group agent-form-group--select">
                            <select class="agent-form-select form-select" id="landUse" name="landUse">
                                <option value="" disabled selected>Land use</option>
                                <option value="RESIDENTIAL_PLOT">Residential plot</option>
                                <option value="COMMERCIAL_PLOT">Commercial plot</option>
                                <option value="INDUSTRIAL_PLOT">Industrial plot</option>
                                <option value="AGRICULTURAL">Agricultural</option>
                                <option value="MIXED_USE">Mixed use</option>
                            </select>
                            <label class="agent-floating-label" for="landUse">Land use type</label>
                        </div>
                        <div class="agent-form-group">
                            <input class="agent-form-control form-control" id="facing" name="facing" placeholder=" " maxlength="20">
                            <label class="agent-floating-label" for="facing">Facing (e.g. East)</label>
                        </div>
                    </div>
                    <div class="agent-form-row-2">
                        <div class="agent-form-group">
                            <input class="agent-form-control form-control" id="plotLengthFt" name="plotLengthFt" type="number" min="1" step="0.1" placeholder=" ">
                            <label class="agent-floating-label" for="plotLengthFt">Length (ft)</label>
                        </div>
                        <div class="agent-form-group">
                            <input class="agent-form-control form-control" id="plotWidthFt" name="plotWidthFt" type="number" min="1" step="0.1" placeholder=" ">
                            <label class="agent-floating-label" for="plotWidthFt">Width (ft)</label>
                        </div>
                    </div>
                    <div class="d-flex gap-4 spec-field" data-spec="PLOTS_LANDS">
                        <div class="form-check">
                            <input class="form-check-input" type="checkbox" id="boundaryWall" name="boundaryWall" value="true">
                            <label class="form-check-label" for="boundaryWall">Boundary wall</label>
                        </div>
                        <div class="form-check">
                            <input class="form-check-input" type="checkbox" id="cornerPlot" name="cornerPlot" value="true">
                            <label class="form-check-label" for="cornerPlot">Corner plot</label>
                        </div>
                    </div>
                </div>

                <div class="agent-panel spec-field" data-spec="CONSTRUCTION">
                    <h3>Project details</h3>
                    <div class="agent-form-row-2">
                        <div class="agent-form-group">
                            <input class="agent-form-control form-control" id="projectName" name="projectName" placeholder=" " maxlength="120">
                            <label class="agent-floating-label" for="projectName">Project name</label>
                        </div>
                        <div class="agent-form-group">
                            <input class="agent-form-control form-control" id="builderName" name="builderName" placeholder=" " maxlength="120">
                            <label class="agent-floating-label" for="builderName">Builder name</label>
                        </div>
                    </div>
                    <div class="agent-form-row-2">
                        <div class="agent-form-group agent-form-group--select">
                            <select class="agent-form-select form-select" id="constructionStatus" name="constructionStatus">
                                <option value="" disabled selected>Status</option>
                                <option value="READY_TO_MOVE">Ready to move</option>
                                <option value="UNDER_CONSTRUCTION">Under construction</option>
                                <option value="NEW_LAUNCH">New launch</option>
                            </select>
                            <label class="agent-floating-label" for="constructionStatus">Construction status</label>
                        </div>
                        <div class="agent-form-group">
                            <input class="agent-form-control form-control" id="possessionDate" name="possessionDate" type="date" placeholder=" ">
                            <label class="agent-floating-label" for="possessionDate">Possession date</label>
                        </div>
                    </div>
                    <div class="agent-form-row-2">
                        <div class="agent-form-group">
                            <input class="agent-form-control form-control" id="totalUnits" name="totalUnits" type="number" min="1" placeholder=" ">
                            <label class="agent-floating-label" for="totalUnits">Total units</label>
                        </div>
                    </div>
                    <div class="agent-form-group">
                        <input class="agent-form-control form-control" id="unitConfiguration" name="unitConfiguration" placeholder=" " maxlength="255">
                        <label class="agent-floating-label" for="unitConfiguration">Unit types (e.g. 2/3 BHK)</label>
                    </div>
                </div>

                <div class="agent-panel">
                    <h3>Location</h3>
                    <div class="agent-form-group">
                        <input class="agent-form-control form-control" id="addressLine" name="addressLine" placeholder=" " required maxlength="255">
                        <label class="agent-floating-label" for="addressLine">Full address *</label>
                    </div>
                    <div class="agent-form-row-2">
                        <div class="agent-form-group">
                            <input class="agent-form-control form-control" id="locality" name="locality" placeholder=" " required maxlength="100">
                            <label class="agent-floating-label" for="locality">Locality / area *</label>
                        </div>
                        <div class="agent-form-group">
                            <input class="agent-form-control form-control" id="city" name="city" value="${defaultCity}" placeholder=" " required maxlength="80">
                            <label class="agent-floating-label" for="city">City *</label>
                        </div>
                    </div>
                    <div class="agent-form-row-2">
                        <div class="agent-form-group">
                            <input class="agent-form-control form-control" id="state" name="state" value="${defaultState}" placeholder=" " required maxlength="80">
                            <label class="agent-floating-label" for="state">State *</label>
                        </div>
                        <div class="agent-form-group">
                            <input class="agent-form-control form-control" id="pincode" name="pincode" maxlength="6" placeholder=" " required>
                            <label class="agent-floating-label" for="pincode">Pincode *</label>
                        </div>
                    </div>
                </div>

                <div class="agent-panel">
                    <h3>Amenities</h3>
                    <p class="small mb-3" style="color:var(--agent-muted);">Select all that apply</p>
                    <input type="hidden" name="amenities" id="amenities" value="">
                    <div class="agent-amenities-grid" id="amenitiesGrid">
                        <label class="agent-amenity-chip"><input type="checkbox" class="amenity-cb" value="Swimming Pool"> Swimming Pool</label>
                        <label class="agent-amenity-chip"><input type="checkbox" class="amenity-cb" value="Security"> Security</label>
                        <label class="agent-amenity-chip"><input type="checkbox" class="amenity-cb" value="Garden"> Garden</label>
                        <label class="agent-amenity-chip"><input type="checkbox" class="amenity-cb" value="Parking"> Parking</label>
                        <label class="agent-amenity-chip"><input type="checkbox" class="amenity-cb" value="Clubhouse"> Clubhouse</label>
                        <label class="agent-amenity-chip"><input type="checkbox" class="amenity-cb" value="Lift"> Lift</label>
                        <label class="agent-amenity-chip"><input type="checkbox" class="amenity-cb" value="Gym"> Gym</label>
                        <label class="agent-amenity-chip"><input type="checkbox" class="amenity-cb" value="Power Backup"> Power Backup</label>
                    </div>
                </div>

                <div class="agent-panel">
                    <h3>Photos</h3>
                    <div id="currentPrimaryPreview" class="mb-3" style="display:none;">
                        <label class="form-label" style="color:var(--agent-muted);">Current primary image</label>
                        <img id="currentPrimaryImg" src="" alt="Current" style="max-width:220px;border-radius:10px;border:1px solid var(--agent-glass-border);">
                    </div>
                    <div class="agent-form-group">
                        <label class="form-label" style="color:var(--agent-muted);" id="primaryImageLabel">Primary image (JPG, PNG, WebP — max 10 MB)</label>
                        <input class="form-control agent-form-control" type="file" id="primaryImage" name="primaryImage" accept="image/jpeg,image/png,image/webp,image/gif">
                    </div>
                    <div class="agent-form-group">
                        <label class="form-label" style="color:var(--agent-muted);">Gallery images (optional)</label>
                        <input class="form-control agent-form-control" type="file" id="galleryImages" name="galleryImages" accept="image/jpeg,image/png,image/webp,image/gif" multiple>
                    </div>
                </div>

                <div class="d-flex gap-2 flex-wrap">
                    <button type="submit" class="agent-btn-gold" id="submitPropertyBtn">${editMode ? 'Save changes' : 'Publish listing'}</button>
                    <a href="${ctx}/agent/properties" class="agent-btn-outline">Cancel</a>
                </div>
            </form>
        </div>
    </div>
</div>
<script src="${ctx}/js/auth.js"></script>
<script src="${ctx}/js/agent.js"></script>
<script src="${ctx}/js/agent-property.js"></script>
<script>
(function () {
  const ctx = '${ctx}';
  const editMode = document.getElementById('editMode') && document.getElementById('editMode').value === 'true';
  if (editMode) {
    AgentProperty.initEditForm(ctx);
  } else {
    AgentProperty.initAddForm(ctx);
    const primary = document.getElementById('primaryImage');
    if (primary) primary.setAttribute('required', 'required');
    const lbl = document.getElementById('primaryImageLabel');
    if (lbl) lbl.textContent = 'Primary image * (JPG, PNG, WebP — max 10 MB)';
  }
})();
</script>
<%@ include file="../includes/portal-scripts.jsp" %>
</body>
</html>
