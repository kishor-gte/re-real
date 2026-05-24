<div class="pgp-step-panel active" data-step="0">
    <div class="pgp-glass-card">
        <h3 class="pgp-section-title">PG Basic Information</h3>
        <div class="row g-3">
            <div class="col-md-6">
                <label class="form-label">PG / Hostel Name *</label>
                <input type="text" class="form-control" id="pgName" maxlength="200">
                <div class="pgp-field-error d-none" id="pgNameError"></div>
            </div>
            <div class="col-md-3">
                <label class="form-label">PG Type *</label>
                <select class="form-select" id="pgType">
                    <option value="">Select</option>
                    <option value="BOYS_PG">Boys PG</option>
                    <option value="GIRLS_PG">Girls PG</option>
                    <option value="CO_LIVING">Co-Living Space</option>
                </select>
                <div class="pgp-field-error d-none" id="pgTypeError"></div>
            </div>
            <div class="col-md-3">
                <label class="form-label">Gender Allowed *</label>
                <select class="form-select" id="genderAllowed">
                    <option value="">Select</option>
                    <option value="MALE">Male</option>
                    <option value="FEMALE">Female</option>
                    <option value="BOTH">Both</option>
                </select>
                <div class="pgp-field-error d-none" id="genderAllowedError"></div>
            </div>
            <div class="col-12">
                <label class="form-label">Description</label>
                <textarea class="form-control" id="description" rows="3"></textarea>
            </div>
            <div class="col-12">
                <div class="pgp-glass-card py-2 px-3 mb-0" style="margin-bottom:0">
                    <p class="small mb-1" style="color:var(--pgo-muted)">Owner contact (from your account — reused for every PG listing)</p>
                    <p class="mb-0"><strong>${pgOwnerName}</strong> · ${pgOwnerEmail} · ${pgOwnerMobile}</p>
                </div>
            </div>
            <div class="col-12"><label class="form-label">Full Address *</label><textarea class="form-control" id="address" rows="2"></textarea><div class="pgp-field-error d-none" id="addressError"></div></div>
            <div class="col-md-4"><label class="form-label">Landmark</label><input class="form-control" id="landmark"></div>
            <div class="col-md-3"><label class="form-label">City *</label><input class="form-control" id="city"><div class="pgp-field-error d-none" id="cityError"></div></div>
            <div class="col-md-3"><label class="form-label">State *</label><input class="form-control" id="state"><div class="pgp-field-error d-none" id="stateError"></div></div>
            <div class="col-md-2"><label class="form-label">Pincode *</label><input class="form-control" id="pincode" maxlength="6"><div class="pgp-field-error d-none" id="pincodeError"></div></div>
            <div class="col-md-6"><label class="form-label">Google Map Location</label><input class="form-control" id="mapLocation" placeholder="Map URL or coordinates"></div>
            <div class="col-md-6"><label class="form-label">Nearby Colleges / Companies</label><input class="form-control" id="nearbyPlaces"></div>
        </div>
    </div>
</div>

<div class="pgp-step-panel" data-step="1">
    <div class="pgp-glass-card">
        <h3 class="pgp-section-title">Building Details</h3>
        <div class="row g-3">
            <div class="col-md-3"><label class="form-label">Total Floors</label><input type="number" class="form-control" id="totalFloors" min="1" placeholder="e.g. 3"></div>
            <div class="col-md-3"><label class="form-label">Total Rooms</label><input type="number" class="form-control" id="totalRooms" min="1" placeholder="e.g. 12"></div>
            <div class="col-md-3"><label class="form-label">Total Capacity (beds)</label><input type="number" class="form-control" id="totalCapacity" min="0" readonly title="Auto from rooms"></div>
            <div class="col-md-3"><label class="form-label">Available Beds</label><input type="number" class="form-control" id="availableBeds" min="0" readonly title="Auto from rooms"></div>
            <div class="col-12"><p class="small mb-0" id="pgpRoomsPerFloorHint" style="color:var(--pgo-neon-cyan)">Enter total floors and rooms — they will be distributed automatically per floor on the next step.</p></div>
            <div class="col-md-3 form-check"><input class="form-check-input" type="checkbox" id="liftAvailable"><label class="form-check-label" for="liftAvailable">Lift Available</label></div>
            <div class="col-md-3 form-check"><input class="form-check-input" type="checkbox" id="parkingAvailable"><label class="form-check-label" for="parkingAvailable">Parking</label></div>
            <div class="col-md-3 form-check"><input class="form-check-input" type="checkbox" id="cctvSecurity"><label class="form-check-label" for="cctvSecurity">CCTV</label></div>
            <div class="col-md-3 form-check"><input class="form-check-input" type="checkbox" id="biometricEntry"><label class="form-check-label" for="biometricEntry">Biometric Entry</label></div>
            <div class="col-md-3 form-check"><input class="form-check-input" type="checkbox" id="fireSafety"><label class="form-check-label" for="fireSafety">Fire Safety</label></div>
        </div>
    </div>
</div>

<div class="pgp-step-panel" data-step="2">
    <div class="pgp-glass-card">
        <h3 class="pgp-section-title">Floors &amp; Rooms</h3>
        <p class="small" style="color:var(--pgo-muted)">Floors and rooms are auto-created from building totals. Beds are set per sharing type (1–5 per room).</p>
        <div id="pgpFloorSummary" class="small mb-2" style="color:var(--pgo-gold)"></div>
        <div id="pgpFloorsWrap"></div>
        <div class="d-flex flex-wrap gap-2 mt-2">
            <button type="button" class="pgp-btn-add flex-grow-1" id="pgpAutoGenerateBtn">↻ Regenerate from building totals</button>
            <button type="button" class="pgp-btn-add flex-grow-1" id="pgpAddFloorBtn">+ Add Floor manually</button>
        </div>
    </div>
</div>

<div class="pgp-step-panel" data-step="3">
    <div class="pgp-glass-card">
        <h3 class="pgp-section-title">Sharing &amp; Pricing</h3>
        <p class="small" style="color:var(--pgo-muted)">Set monthly rent for each sharing type.</p>
        <div id="pgpSharingList"></div>
        <button type="button" class="pgp-btn-add" id="pgpAddSharingBtn">+ Add Sharing Price</button>
    </div>
</div>

<div class="pgp-step-panel" data-step="4">
    <div class="pgp-glass-card">
        <h3 class="pgp-section-title">Amenities</h3>
        <div class="pgp-amenity-grid" id="pgpAmenitiesGrid"></div>
    </div>
</div>

<div class="pgp-step-panel" data-step="5">
    <div class="pgp-glass-card">
        <h3 class="pgp-section-title">Rules &amp; Rent / Payment</h3>
        <div class="row g-3 mb-3">
            <div class="col-md-4 form-check"><input class="form-check-input" type="checkbox" id="ruleNoSmoking"><label class="form-check-label" for="ruleNoSmoking">No Smoking</label></div>
            <div class="col-md-4 form-check"><input class="form-check-input" type="checkbox" id="ruleNoAlcohol"><label class="form-check-label" for="ruleNoAlcohol">No Alcohol</label></div>
            <div class="col-md-4 form-check"><input class="form-check-input" type="checkbox" id="ruleVisitors"><label class="form-check-label" for="ruleVisitors">Visitors Allowed</label></div>
            <div class="col-md-4 form-check"><input class="form-check-input" type="checkbox" id="rulePets"><label class="form-check-label" for="rulePets">Pets Allowed</label></div>
            <div class="col-md-4 form-check"><input class="form-check-input" type="checkbox" id="ruleCurfew"><label class="form-check-label" for="ruleCurfew">Curfew Timing</label></div>
            <div class="col-md-4 form-check"><input class="form-check-input" type="checkbox" id="ruleIdProof"><label class="form-check-label" for="ruleIdProof">ID Proof Mandatory</label></div>
            <div class="col-md-4"><label class="form-label">Curfew Time</label><input class="form-control" id="curfewTiming" placeholder="e.g. 10:00 PM"></div>
            <div class="col-md-4"><label class="form-label">Notice Period (days)</label><input type="number" class="form-control" id="noticePeriodDays"></div>
            <div class="col-md-4"><label class="form-label">Rules Security Deposit</label><input type="number" class="form-control" id="rulesSecurityDeposit"></div>
        </div>
        <hr style="border-color:var(--pgo-glass-border)">
        <div class="row g-3">
            <div class="col-md-3"><label class="form-label">Monthly Rent (from)</label><input type="number" class="form-control" id="monthlyRent"></div>
            <div class="col-md-3"><label class="form-label">Security Deposit</label><input type="number" class="form-control" id="securityDeposit"></div>
            <div class="col-md-3"><label class="form-label">Maintenance Charges</label><input type="number" class="form-control" id="maintenanceCharges"></div>
            <div class="col-md-3"><label class="form-label">Booking Amount</label><input type="number" class="form-control" id="bookingAmount"></div>
            <div class="col-md-3 form-check"><input class="form-check-input" type="checkbox" id="electricityIncluded"><label class="form-check-label" for="electricityIncluded">Electricity Included</label></div>
            <div class="col-md-3 form-check"><input class="form-check-input" type="checkbox" id="waterIncluded"><label class="form-check-label" for="waterIncluded">Water Included</label></div>
            <div class="col-md-3 form-check"><input class="form-check-input" type="checkbox" id="featured"><label class="form-check-label" for="featured">Featured PG</label></div>
        </div>
    </div>
</div>

<div class="pgp-step-panel" data-step="6">
    <div class="pgp-glass-card">
        <h3 class="pgp-section-title">Images &amp; Availability</h3>
        <div class="row g-3 mb-3">
            <div class="col-md-4"><label class="form-label">Available From</label><input type="date" class="form-control" id="availableFrom"></div>
            <div class="col-md-4 form-check mt-4"><input class="form-check-input" type="checkbox" id="immediateAvailability"><label class="form-check-label" for="immediateAvailability">Immediate Availability</label></div>
        </div>
        <div class="pgp-dropzone" id="pgpDropzone">
            <p class="mb-1">Drag &amp; drop images here or click to browse</p>
            <p class="small mb-0" style="color:var(--pgo-muted)">JPG/PNG · Max 5MB · Min 3 · Max 30 images</p>
            <p class="small mt-2" id="pgpImageCount">0 / 30 images</p>
        </div>
        <input type="file" id="pgpImageInput" accept="image/jpeg,image/png" multiple hidden>
        <div class="pgp-image-grid" id="pgpImageGrid"></div>
    </div>
</div>

<div class="pgp-wizard-actions">
    <button type="button" class="btn btn-outline-light" id="pgpPrevBtn">Previous</button>
    <div class="d-flex flex-wrap gap-2">
        <button type="button" class="btn btn-outline-warning" id="pgpPreviewBtn">Preview</button>
        <button type="button" class="btn btn-secondary" id="pgpDraftBtn">Save as Draft</button>
        <button type="button" class="btn btn-primary" id="pgpNextBtn">Next</button>
        <button type="button" class="btn btn-success" id="pgpPublishBtn">Publish PG</button>
    </div>
</div>

<div class="modal fade pgp-preview-modal" id="pgpPreviewModal" tabindex="-1">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header"><h5 class="modal-title">Preview</h5><button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button></div>
            <div class="modal-body" id="pgpPreviewBody"></div>
        </div>
    </div>
</div>
