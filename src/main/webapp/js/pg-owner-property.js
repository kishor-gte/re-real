(function (global) {
  'use strict';

  var AMENITIES = [
    { code: 'wifi', label: 'WiFi', group: 'Basic' },
    { code: 'ro_water', label: 'RO Water', group: 'Basic' },
    { code: 'power_backup', label: 'Power Backup', group: 'Basic' },
    { code: 'laundry', label: 'Laundry', group: 'Basic' },
    { code: 'housekeeping', label: 'Housekeeping', group: 'Basic' },
    { code: 'attached_bathroom', label: 'Attached Bathroom', group: 'Basic' },
    { code: 'hot_water', label: 'Hot Water', group: 'Basic' },
    { code: 'study_table', label: 'Study Table', group: 'Basic' },
    { code: 'wardrobe', label: 'Wardrobe', group: 'Basic' },
    { code: 'bed_mattress', label: 'Bed & Mattress', group: 'Basic' },
    { code: 'breakfast', label: 'Breakfast', group: 'Food' },
    { code: 'lunch', label: 'Lunch', group: 'Food' },
    { code: 'dinner', label: 'Dinner', group: 'Food' },
    { code: 'refrigerator', label: 'Refrigerator', group: 'Food' },
    { code: 'microwave', label: 'Microwave', group: 'Food' },
    { code: 'common_kitchen', label: 'Common Kitchen', group: 'Food' },
    { code: 'cctv', label: 'CCTV', group: 'Security' },
    { code: 'security_guard', label: 'Security Guard', group: 'Security' },
    { code: 'biometric_entry', label: 'Biometric Entry', group: 'Security' },
    { code: 'gated_security', label: 'Gated Security', group: 'Security' },
    { code: 'tv', label: 'TV', group: 'Entertainment' },
    { code: 'indoor_games', label: 'Indoor Games', group: 'Entertainment' },
    { code: 'gym', label: 'Gym', group: 'Entertainment' },
    { code: 'common_lounge', label: 'Common Lounge', group: 'Entertainment' },
    { code: 'parking', label: 'Parking', group: 'Transport' },
    { code: 'bike_parking', label: 'Bike Parking', group: 'Transport' },
    { code: 'near_metro', label: 'Near Metro', group: 'Transport' },
    { code: 'college_nearby', label: 'College Nearby', group: 'Transport' },
    { code: 'it_park_nearby', label: 'IT Park Nearby', group: 'Transport' }
  ];

  var SHARING_BED_MAP = { SINGLE: 1, DOUBLE: 2, TRIPLE: 3, FOUR: 4, FIVE: 5 };

  var SHARING_TYPES = [
    { value: 'SINGLE', label: 'Single Sharing' },
    { value: 'DOUBLE', label: 'Double Sharing' },
    { value: 'TRIPLE', label: 'Triple Sharing' },
    { value: 'FOUR', label: 'Four Sharing' },
    { value: 'FIVE', label: 'Five Sharing' }
  ];

  var STEP_LABELS = ['Basic Info', 'Building', 'Floors & Rooms', 'Pricing', 'Amenities', 'Rules & Rent', 'Images'];

  function apiBase(ctx) {
    return (ctx || '') + '/api/pg-owner/properties';
  }

  function toast(msg, isError) {
    var el = document.getElementById('pgpToast');
    if (!el) {
      alert(msg);
      return;
    }
    el.textContent = msg;
    el.className = 'alert mt-3 ' + (isError ? 'alert-danger' : 'alert-success');
    el.classList.remove('d-none');
    setTimeout(function () { el.classList.add('d-none'); }, 5000);
  }

  function val(id) {
    var el = document.getElementById(id);
    return el ? el.value.trim() : '';
  }

  function checked(id) {
    var el = document.getElementById(id);
    return el ? el.checked : false;
  }

  function numVal(id) {
    var n = parseFloat(val(id));
    return isNaN(n) ? null : n;
  }

  function intVal(id) {
    var n = parseInt(val(id), 10);
    return isNaN(n) ? null : n;
  }

  var PgOwnerPropertyAPI = {
    list: function (ctx) {
      return fetch(apiBase(ctx)).then(function (r) { return r.json(); });
    },
    get: function (ctx, id) {
      return fetch(apiBase(ctx) + '/' + id).then(function (r) { return r.json(); });
    },
    create: function (ctx, formData) {
      return fetch(apiBase(ctx), { method: 'POST', body: formData }).then(function (r) { return r.json(); });
    },
    update: function (ctx, id, formData) {
      return fetch(apiBase(ctx) + '/' + id, { method: 'PUT', body: formData }).then(function (r) { return r.json(); });
    },
    publish: function (ctx, id) {
      return fetch(apiBase(ctx) + '/' + id + '/publish', { method: 'POST' }).then(function (r) { return r.json(); });
    },
    toggleStatus: function (ctx, id) {
      return fetch(apiBase(ctx) + '/' + id + '/status', { method: 'PATCH' }).then(function (r) { return r.json(); });
    },
    setActive: function (ctx, id, active) {
      return fetch(apiBase(ctx) + '/' + id + '/status?active=' + (active ? 'true' : 'false'), { method: 'PATCH' })
        .then(function (r) { return r.json(); });
    },
    remove: function (ctx, id) {
      return fetch(apiBase(ctx) + '/' + id, { method: 'DELETE' }).then(function (r) { return r.json(); });
    }
  };

  function statusLabel(status) {
    var s = (status || 'DRAFT').toUpperCase();
    if (s === 'PUBLISHED') return 'Active';
    if (s === 'INACTIVE') return 'Inactive';
    return 'Draft';
  }

  function statusPillClass(status) {
    var s = (status || 'DRAFT').toUpperCase();
    if (s === 'PUBLISHED') return 'is-active';
    if (s === 'INACTIVE') return 'is-inactive';
    return 'is-draft';
  }

  function renderManageCard(ctx, p) {
    var cover = p.coverImage ? ' style="background-image:url(' + ctx + p.coverImage + ')"' : '';
    var badge = 'pgp-badge-' + (p.status || 'draft').toLowerCase();
    var status = (p.status || 'DRAFT').toUpperCase();
    var toggleBtn = '';
    if (status === 'PUBLISHED') {
      toggleBtn = '<button type="button" class="btn btn-sm btn-outline-light pgp-toggle-active is-deactivate" data-pgp-toggle="' + p.id + '" data-active="false">Set Inactive</button>';
    } else if (status === 'INACTIVE') {
      toggleBtn = '<button type="button" class="btn btn-sm btn-outline-light pgp-toggle-active is-activate" data-pgp-toggle="' + p.id + '" data-active="true">Set Active</button>';
    } else {
      toggleBtn = '<button type="button" class="btn btn-sm btn-outline-warning" data-pgp-publish="' + p.id + '">Publish</button>';
    }
    return '<article class="pgp-manage-card" data-pgp-id="' + p.id + '">' +
      '<div class="pgp-manage-cover"' + cover + '></div>' +
      '<div class="pgp-manage-body">' +
      '<div class="pgp-status-row">' +
      '<span class="pgp-badge ' + badge + '">' + (p.status || 'DRAFT') + '</span>' +
      '<span class="pgp-active-pill ' + statusPillClass(status) + '">' + statusLabel(status) + '</span>' +
      '</div>' +
      '<h3 class="h6 mt-2 mb-1">' + escapeHtml(p.pgName) + '</h3>' +
      '<p class="small mb-2" style="color:var(--pgo-muted)">' + escapeHtml(p.city || '') + ', ' + escapeHtml(p.state || '') + '</p>' +
      '<p class="small mb-3">Beds: ' + (p.availableBeds || 0) + ' · Rent from ₹' + (p.monthlyRent || '—') + '</p>' +
      '<div class="d-flex flex-wrap gap-2 mb-2">' + toggleBtn + '</div>' +
      '<div class="d-flex flex-wrap gap-2">' +
      '<a href="' + ctx + '/pg-owner/properties/' + p.id + '" class="btn btn-sm btn-outline-light">View</a>' +
      '<a href="' + ctx + '/pg-owner/properties/' + p.id + '/edit" class="btn btn-sm btn-primary">Edit</a>' +
      '<a href="' + ctx + '/pg-owner/properties/' + p.id + '/gallery" class="btn btn-sm btn-outline-warning">Gallery</a>' +
      '</div></div></article>';
  }

  function loadManageGrid(ctx) {
    var grid = document.getElementById('pgpManageGrid');
    var empty = document.getElementById('pgpManageEmpty');
    if (!grid) return Promise.resolve();
    grid.innerHTML = '<p class="small" style="color:var(--pgo-muted)">Loading…</p>';
    if (empty) empty.classList.add('d-none');
    return PgOwnerPropertyAPI.list(ctx).then(function (res) {
      if (!res.success || !res.data || !res.data.length) {
        grid.innerHTML = '';
        if (empty) empty.classList.remove('d-none');
        return;
      }
      grid.innerHTML = res.data.map(function (p) { return renderManageCard(ctx, p); }).join('');
    }).catch(function () {
      grid.innerHTML = '';
      toast('Could not load PG properties', true);
    });
  }

  function initManagePage(ctx) {
    var grid = document.getElementById('pgpManageGrid');
    if (!grid) return;
    loadManageGrid(ctx);
    grid.addEventListener('click', function (e) {
      var toggleBtn = e.target.closest('[data-pgp-toggle]');
      if (toggleBtn) {
        e.preventDefault();
        var id = toggleBtn.getAttribute('data-pgp-toggle');
        var active = toggleBtn.getAttribute('data-active') === 'true';
        toggleBtn.disabled = true;
        PgOwnerPropertyAPI.setActive(ctx, id, active).then(function (res) {
          if (!res.success) throw new Error(res.message || 'Could not update status');
          toast(active ? 'PG is now active and visible to users' : 'PG is now inactive', false);
          loadManageGrid(ctx);
        }).catch(function (err) {
          toast(err.message || 'Could not update status', true);
          toggleBtn.disabled = false;
        });
        return;
      }
      var publishBtn = e.target.closest('[data-pgp-publish]');
      if (publishBtn) {
        e.preventDefault();
        var pubId = publishBtn.getAttribute('data-pgp-publish');
        publishBtn.disabled = true;
        PgOwnerPropertyAPI.publish(ctx, pubId).then(function (res) {
          if (!res.success) throw new Error(res.message || 'Could not publish');
          toast('PG published and active', false);
          loadManageGrid(ctx);
        }).catch(function (err) {
          toast(err.message || 'Could not publish PG', true);
          publishBtn.disabled = false;
        });
      }
    });
  }

  function escapeHtml(s) {
    return String(s || '').replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
  }

  function initWizard(ctx, options) {
    options = options || {};
    var editMode = !!options.editMode;
    var propertyId = options.propertyId;
    var currentStep = 0;
    var floors = [];
    var sharingPrices = [];
    var images = [];
    var maxImages = 30;
    var isSubmitting = false;
    var maxMb = 5;
    var floorsManualOverride = false;

    function setFieldVal(id, v) {
      var el = document.getElementById(id);
      if (el) el.value = v != null && v !== '' ? v : '';
    }

    function bedsForSharing(sharingType) {
      return SHARING_BED_MAP[sharingType] || 2;
    }

    function distributeRoomsAcrossFloors(totalFloors, totalRooms) {
      var f = Math.max(0, parseInt(totalFloors, 10) || 0);
      var r = Math.max(0, parseInt(totalRooms, 10) || 0);
      if (f < 1 || r < 1) return [];
      var base = Math.floor(r / f);
      var rem = r % f;
      var counts = [];
      for (var i = 0; i < f; i++) {
        counts.push(base + (i < rem ? 1 : 0));
      }
      return counts;
    }

    function roomNumberFor(floorNum, roomIndex) {
      var seq = roomIndex + 1;
      return String(floorNum) + (seq < 10 ? '0' + seq : String(seq));
    }

    function createDefaultRoom(floorNum, roomIndex) {
      var sharing = 'DOUBLE';
      var beds = bedsForSharing(sharing);
      return {
        roomNumber: roomNumberFor(floorNum, roomIndex),
        roomType: 'STANDARD',
        totalBeds: beds,
        occupiedBeds: 0,
        availableBeds: beds,
        sharingType: sharing,
        roomStatus: 'AVAILABLE'
      };
    }

    function updateRoomsPerFloorHint() {
      var hint = document.getElementById('pgpRoomsPerFloorHint');
      if (!hint) return;
      var totalF = parseInt(val('totalFloors'), 10) || 0;
      var totalR = parseInt(val('totalRooms'), 10) || 0;
      if (totalF < 1 || totalR < 1) {
        hint.textContent = 'Enter total floors and rooms — they will be distributed automatically per floor on the next step.';
        return;
      }
      var dist = distributeRoomsAcrossFloors(totalF, totalR);
      var parts = dist.map(function (c, i) { return 'Floor ' + (i + 1) + ': ' + c + ' room(s)'; });
      var beds = dist.reduce(function (s, c) { return s + c * bedsForSharing('DOUBLE'); }, 0);
      hint.textContent = parts.join(' · ') + ' · ~' + beds + ' beds (2 per room by default, editable next step)';
    }

    function updateFloorSummary() {
      var el = document.getElementById('pgpFloorSummary');
      if (!el) return;
      if (!floors.length) {
        el.textContent = '';
        return;
      }
      var totalRooms = 0;
      var totalBeds = 0;
      floors.forEach(function (f) {
        totalRooms += (f.rooms || []).length;
        (f.rooms || []).forEach(function (r) { totalBeds += r.totalBeds || 0; });
      });
      el.textContent = floors.length + ' floor(s) · ' + totalRooms + ' room(s) · ' + totalBeds + ' bed(s) total';
    }

    function updateCapacityFromFloors() {
      if (floors.length) syncFloorsFromDom();
      var capacity = 0;
      var available = 0;
      floors.forEach(function (floor) {
        (floor.rooms || []).forEach(function (room) {
          capacity += room.totalBeds || 0;
          available += room.availableBeds != null ? room.availableBeds : (room.totalBeds || 0);
        });
      });
      setFieldVal('totalCapacity', capacity);
      setFieldVal('availableBeds', available);
    }

    function generateFloorsFromTotals(force) {
      var totalF = parseInt(val('totalFloors'), 10) || 0;
      var totalR = parseInt(val('totalRooms'), 10) || 0;
      if (totalF < 1 || totalR < 1) {
        if (force) toast('Enter total floors (min 1) and total rooms (min 1) in Building Details', true);
        return false;
      }
      if (floorsManualOverride && !force) {
        updateFloorSummary();
        return false;
      }
      var distribution = distributeRoomsAcrossFloors(totalF, totalR);
      floors = [];
      for (var fi = 0; fi < distribution.length; fi++) {
        var floorNum = fi + 1;
        var roomCount = distribution[fi];
        var rooms = [];
        for (var ri = 0; ri < roomCount; ri++) {
          rooms.push(createDefaultRoom(floorNum, ri));
        }
        floors.push({
          floorNumber: floorNum,
          totalRooms: roomCount,
          floorDescription: 'Floor ' + floorNum + ' — ' + roomCount + ' room(s)',
          rooms: rooms
        });
      }
      floorsManualOverride = false;
      renderFloors();
      updateCapacityFromFloors();
      updateFloorSummary();
      return true;
    }

    function bindBuildingTotalsListeners() {
      ['totalFloors', 'totalRooms'].forEach(function (id) {
        var el = document.getElementById(id);
        if (!el) return;
        el.addEventListener('input', function () {
          if (!floorsManualOverride) {
            floors = [];
          }
          updateRoomsPerFloorHint();
        });
      });
    }

    renderProgress();
    renderAmenities();
    sharingPrices = [];
    renderSharingRows();
    bindWizardNav();
    bindBuildingTotalsListeners();
    bindImageUpload();
    document.getElementById('pgpAddFloorBtn').addEventListener('click', function () {
      floorsManualOverride = true;
      addFloor();
    });
    document.getElementById('pgpAutoGenerateBtn').addEventListener('click', function () {
      floorsManualOverride = false;
      generateFloorsFromTotals(true);
    });
    document.getElementById('pgpAddSharingBtn').addEventListener('click', addSharingRow);
    updateRoomsPerFloorHint();

    if (editMode && propertyId) {
      loadForEdit(propertyId);
    }

    function renderProgress() {
      var stepsEl = document.getElementById('pgpSteps');
      if (!stepsEl) return;
      stepsEl.innerHTML = STEP_LABELS.map(function (label, i) {
        return '<div class="pgp-progress-step' + (i === 0 ? ' active' : '') + '" data-step="' + i + '">' + label + '</div>';
      }).join('');
      updateProgressBar();
    }

    function updateProgressBar() {
      var fill = document.getElementById('pgpProgressFill');
      if (fill) fill.style.width = ((currentStep + 1) / STEP_LABELS.length * 100) + '%';
      document.querySelectorAll('.pgp-progress-step').forEach(function (el, i) {
        el.classList.remove('active', 'done');
        if (i === currentStep) el.classList.add('active');
        else if (i < currentStep) el.classList.add('done');
      });
      document.querySelectorAll('.pgp-step-panel').forEach(function (panel, i) {
        panel.classList.toggle('active', i === currentStep);
      });
    }

    function bindWizardNav() {
      document.getElementById('pgpPrevBtn').addEventListener('click', function () {
        if (currentStep > 0) { currentStep--; updateProgressBar(); }
      });
      document.getElementById('pgpNextBtn').addEventListener('click', function () {
        if (!validateStep(currentStep)) return;
        if (currentStep === 1) {
          var tf = parseInt(val('totalFloors'), 10) || 0;
          var tr = parseInt(val('totalRooms'), 10) || 0;
          if (tf >= 1 && tr >= 1 && (!floors.length || !floorsManualOverride)) {
            generateFloorsFromTotals(false);
          }
        }
        if (currentStep < STEP_LABELS.length - 1) { currentStep++; updateProgressBar(); }
      });
      document.getElementById('pgpDraftBtn').addEventListener('click', function () { submit(false); });
      document.getElementById('pgpPublishBtn').addEventListener('click', function () { submit(true); });
      document.getElementById('pgpPreviewBtn').addEventListener('click', showPreview);
    }

    function validateStep(step) {
      clearErrors();
      if (step === 0) {
        if (val('pgName').length < 3) return showFieldError('pgName', 'PG name is required');
        if (!val('pgType')) return showFieldError('pgType', 'Select PG type');
        if (!val('genderAllowed')) return showFieldError('genderAllowed', 'Select gender allowed');
      }
      if (step === 1) {
        var tf = parseInt(val('totalFloors'), 10) || 0;
        var tr = parseInt(val('totalRooms'), 10) || 0;
        if (tf < 1) return showFieldError('totalFloors', 'Enter total floors (minimum 1)');
        if (tr < 1) return showFieldError('totalRooms', 'Enter total rooms (minimum 1)');
      }
      return true;
    }

    function validateForSubmit(publish) {
      clearErrors();
      if (val('pgName').length < 3) return showFieldError('pgName', 'PG name is required');
      if (!val('pgType')) return showFieldError('pgType', 'Select PG type');
      if (!val('genderAllowed')) return showFieldError('genderAllowed', 'Select gender allowed');
      if (publish) {
        if (!val('address')) return showFieldError('address', 'Address is required to publish');
        if (!val('city')) return showFieldError('city', 'City is required to publish');
        if (!val('state')) return showFieldError('state', 'State is required to publish');
        if (!/^\d{6}$/.test(val('pincode'))) return showFieldError('pincode', 'Enter valid pincode to publish');
        if (images.length < 3) {
          currentStep = 6;
          updateProgressBar();
          toast('Upload at least 3 images before publishing', true);
          return false;
        }
      }
      return true;
    }

    function showFieldError(id, msg) {
      var err = document.getElementById(id + 'Error');
      if (err) { err.textContent = msg; err.classList.remove('d-none'); }
      toast(msg, true);
      return false;
    }

    function clearErrors() {
      document.querySelectorAll('.pgp-field-error').forEach(function (e) {
        e.textContent = '';
        e.classList.add('d-none');
      });
    }

    function renderAmenities() {
      var wrap = document.getElementById('pgpAmenitiesGrid');
      if (!wrap) return;
      wrap.innerHTML = AMENITIES.map(function (a) {
        return '<label class="pgp-amenity-item"><input type="checkbox" name="amenity" value="' + a.code + '"> ' + a.label + '</label>';
      }).join('');
    }

    function renderSharingDefaults() {
      sharingPrices = SHARING_TYPES.map(function (s) { return { sharingType: s.value, monthlyRent: '' }; });
      renderSharingRows();
    }

    function renderSharingRows() {
      var wrap = document.getElementById('pgpSharingList');
      if (!wrap) return;
      wrap.innerHTML = sharingPrices.map(function (row, idx) {
        var opts = SHARING_TYPES.map(function (s) {
          return '<option value="' + s.value + '"' + (row.sharingType === s.value ? ' selected' : '') + '>' + s.label + '</option>';
        }).join('');
        return '<div class="pgp-sharing-row" data-idx="' + idx + '">' +
          '<select class="form-select form-select-sm pgp-sharing-type">' + opts + '</select>' +
          '<input type="number" class="form-control form-control-sm pgp-sharing-rent" placeholder="Monthly rent ₹" value="' + (row.monthlyRent || '') + '">' +
          '<button type="button" class="pgp-btn-icon pgp-remove-sharing" title="Remove">✕</button></div>';
      }).join('');
      wrap.querySelectorAll('.pgp-sharing-type').forEach(function (sel) {
        sel.addEventListener('change', syncSharingFromDom);
      });
      wrap.querySelectorAll('.pgp-sharing-rent').forEach(function (inp) {
        inp.addEventListener('input', syncSharingFromDom);
      });
      wrap.querySelectorAll('.pgp-remove-sharing').forEach(function (btn) {
        btn.addEventListener('click', function () {
          var idx = parseInt(btn.closest('.pgp-sharing-row').dataset.idx, 10);
          sharingPrices.splice(idx, 1);
          renderSharingRows();
        });
      });
    }

    function syncSharingFromDom() {
      sharingPrices = [];
      document.querySelectorAll('#pgpSharingList .pgp-sharing-row').forEach(function (row) {
        sharingPrices.push({
          sharingType: row.querySelector('.pgp-sharing-type').value,
          monthlyRent: row.querySelector('.pgp-sharing-rent').value
        });
      });
    }

    function addSharingRow() {
      syncSharingFromDom();
      sharingPrices.push({ sharingType: 'SINGLE', monthlyRent: '' });
      renderSharingRows();
    }

    function addFloor() {
      floors.push({ floorNumber: floors.length + 1, totalRooms: 0, floorDescription: '', rooms: [] });
      renderFloors();
    }

    function renderFloors() {
      var wrap = document.getElementById('pgpFloorsWrap');
      if (!wrap) return;
      wrap.innerHTML = floors.map(function (floor, fi) {
        var roomsHtml = (floor.rooms || []).map(function (room, ri) {
          return renderRoomCard(fi, ri, room);
        }).join('');
        return '<div class="pgp-floor-card" data-floor="' + fi + '">' +
          '<div class="pgp-floor-head"><h4>Floor ' + (floor.floorNumber || fi + 1) + '</h4>' +
          '<button type="button" class="pgp-btn-icon pgp-remove-floor" data-fi="' + fi + '">Remove</button></div>' +
          '<div class="row g-2 mb-2">' +
          '<div class="col-md-3"><label class="form-label small">Floor #</label><input type="number" class="form-control form-control-sm pgp-floor-num" value="' + (floor.floorNumber || fi + 1) + '"></div>' +
          '<div class="col-md-3"><label class="form-label small">Total Rooms</label><input type="number" class="form-control form-control-sm pgp-floor-rooms" value="' + (floor.totalRooms || 0) + '"></div>' +
          '<div class="col-md-6"><label class="form-label small">Description</label><input type="text" class="form-control form-control-sm pgp-floor-desc" value="' + escapeHtml(floor.floorDescription || '') + '"></div></div>' +
          '<div class="pgp-rooms-wrap">' + roomsHtml + '</div>' +
          '<button type="button" class="pgp-btn-add pgp-add-room" data-fi="' + fi + '">+ Add Room</button></div>';
      }).join('');

      wrap.querySelectorAll('.pgp-remove-floor').forEach(function (btn) {
        btn.addEventListener('click', function () {
          floorsManualOverride = true;
          floors.splice(parseInt(btn.dataset.fi, 10), 1);
          renderFloors();
          updateCapacityFromFloors();
        });
      });
      wrap.querySelectorAll('.pgp-add-room').forEach(function (btn) {
        btn.addEventListener('click', function () {
          floorsManualOverride = true;
          var fi = parseInt(btn.dataset.fi, 10);
          floors[fi].rooms = floors[fi].rooms || [];
          var floorNum = floors[fi].floorNumber || fi + 1;
          floors[fi].rooms.push(createDefaultRoom(floorNum, floors[fi].rooms.length));
          renderFloors();
          updateCapacityFromFloors();
        });
      });
      wrap.querySelectorAll('.pgp-remove-room').forEach(function (btn) {
        btn.addEventListener('click', function () {
          floorsManualOverride = true;
          var parts = btn.dataset.key.split(':');
          floors[parseInt(parts[0], 10)].rooms.splice(parseInt(parts[1], 10), 1);
          renderFloors();
          updateCapacityFromFloors();
        });
      });
      wrap.querySelectorAll('.pgp-room-sharing').forEach(function (sel) {
        sel.addEventListener('change', function () {
          floorsManualOverride = true;
          var fi = parseInt(sel.dataset.fi, 10);
          var ri = parseInt(sel.dataset.ri, 10);
          var beds = bedsForSharing(sel.value);
          floors[fi].rooms[ri].sharingType = sel.value;
          floors[fi].rooms[ri].totalBeds = beds;
          floors[fi].rooms[ri].availableBeds = beds;
          var card = sel.closest('.pgp-room-card');
          if (card) {
            var bedsInp = card.querySelector('.pgp-room-beds');
            var availInp = card.querySelector('.pgp-room-available');
            if (bedsInp) bedsInp.value = beds;
            if (availInp) availInp.value = beds;
          }
          updateCapacityFromFloors();
          updateFloorSummary();
        });
      });
      wrap.querySelectorAll('.pgp-room-beds, .pgp-room-available').forEach(function (inp) {
        inp.addEventListener('input', function () {
          floorsManualOverride = true;
          updateCapacityFromFloors();
          updateFloorSummary();
        });
      });
      syncFloorInputs();
      updateFloorSummary();
    }

    function renderRoomCard(fi, ri, room) {
      return '<div class="pgp-room-card">' +
        '<div class="pgp-room-head"><h5>Room</h5><button type="button" class="pgp-btn-icon pgp-remove-room" data-key="' + fi + ':' + ri + '">Remove</button></div>' +
        '<div class="row g-2">' +
        '<div class="col-md-2"><input class="form-control form-control-sm pgp-room-num" placeholder="Room #" value="' + escapeHtml(room.roomNumber || '') + '" data-fi="' + fi + '" data-ri="' + ri + '"></div>' +
        '<div class="col-md-3"><select class="form-select form-select-sm pgp-room-type" data-fi="' + fi + '" data-ri="' + ri + '">' + roomTypeOptions(room.roomType) + '</select></div>' +
        '<div class="col-md-2"><input class="form-control form-control-sm pgp-room-beds" type="number" placeholder="Beds" value="' + (room.totalBeds || 1) + '" data-fi="' + fi + '" data-ri="' + ri + '"></div>' +
        '<div class="col-md-2"><input class="form-control form-control-sm pgp-room-available" type="number" placeholder="Available" value="' + (room.availableBeds != null ? room.availableBeds : 1) + '" data-fi="' + fi + '" data-ri="' + ri + '"></div>' +
        '<div class="col-md-3"><select class="form-select form-select-sm pgp-room-sharing" data-fi="' + fi + '" data-ri="' + ri + '">' + sharingOptions(room.sharingType) + '</select></div>' +
        '</div></div>';
    }

    function roomTypeOptions(selected) {
      var types = ['DELUXE', 'STANDARD', 'PREMIUM', 'LUXURY', 'DORMITORY'];
      return types.map(function (t) {
        return '<option value="' + t + '"' + (selected === t ? ' selected' : '') + '>' + t.replace('_', ' ') + '</option>';
      }).join('');
    }

    function sharingOptions(selected) {
      return SHARING_TYPES.map(function (s) {
        return '<option value="' + s.value + '"' + (selected === s.value ? ' selected' : '') + '>' + s.label + '</option>';
      }).join('');
    }

    function syncFloorInputs() {
      document.querySelectorAll('.pgp-floor-num').forEach(function (inp) {
        inp.addEventListener('change', function () {
          floors[inp.closest('.pgp-floor-card').dataset.floor].floorNumber = parseInt(inp.value, 10) || 0;
        });
      });
    }

    function syncFloorsFromDom() {
      document.querySelectorAll('.pgp-floor-card').forEach(function (card, fi) {
        floors[fi].floorNumber = parseInt(card.querySelector('.pgp-floor-num').value, 10) || fi + 1;
        floors[fi].totalRooms = parseInt(card.querySelector('.pgp-floor-rooms').value, 10) || 0;
        floors[fi].floorDescription = card.querySelector('.pgp-floor-desc').value;
        card.querySelectorAll('.pgp-room-card').forEach(function (roomEl, ri) {
          floors[fi].rooms[ri] = floors[fi].rooms[ri] || {};
          floors[fi].rooms[ri].roomNumber = roomEl.querySelector('.pgp-room-num').value;
          floors[fi].rooms[ri].roomType = roomEl.querySelector('.pgp-room-type').value;
          floors[fi].rooms[ri].totalBeds = parseInt(roomEl.querySelector('.pgp-room-beds').value, 10) || 0;
          floors[fi].rooms[ri].availableBeds = parseInt(roomEl.querySelector('.pgp-room-available').value, 10) || 0;
          floors[fi].rooms[ri].sharingType = roomEl.querySelector('.pgp-room-sharing').value;
          floors[fi].rooms[ri].roomStatus = 'AVAILABLE';
        });
      });
    }

    function bindImageUpload() {
      var drop = document.getElementById('pgpDropzone');
      var input = document.getElementById('pgpImageInput');
      if (!drop || !input) return;
      drop.addEventListener('click', function () { input.click(); });
      drop.addEventListener('dragover', function (e) { e.preventDefault(); drop.classList.add('dragover'); });
      drop.addEventListener('dragleave', function () { drop.classList.remove('dragover'); });
      drop.addEventListener('drop', function (e) {
        e.preventDefault();
        drop.classList.remove('dragover');
        handleFiles(e.dataTransfer.files);
      });
      input.addEventListener('change', function () { handleFiles(input.files); input.value = ''; });
    }

    function handleFiles(fileList) {
      Array.from(fileList || []).forEach(function (file) {
        if (images.length >= maxImages) return;
        if (!/^image\/(jpeg|jpg|png)$/i.test(file.type)) {
          toast('Only JPG and PNG images allowed', true);
          return;
        }
        if (file.size > maxMb * 1024 * 1024) {
          toast('Each image must be under ' + maxMb + 'MB', true);
          return;
        }
        var reader = new FileReader();
        reader.onload = function (ev) {
          images.push({ file: file, preview: ev.target.result, imageType: 'BUILDING' });
          renderImageGrid();
        };
        reader.readAsDataURL(file);
      });
    }

    function renderImageGrid() {
      var grid = document.getElementById('pgpImageGrid');
      if (!grid) return;
      grid.innerHTML = images.map(function (img, i) {
        return '<div class="pgp-image-thumb"><img src="' + img.preview + '" alt="">' +
          '<button type="button" class="pgp-remove-img" data-i="' + i + '">✕</button>' +
          '<select class="pgp-img-type" data-i="' + i + '">' +
          ['BUILDING', 'ROOM', 'BATHROOM', 'DINING', 'AMENITY', 'FLOOR'].map(function (t) {
            return '<option value="' + t + '"' + (img.imageType === t ? ' selected' : '') + '>' + t + '</option>';
          }).join('') + '</select></div>';
      }).join('');
      grid.querySelectorAll('.pgp-remove-img').forEach(function (btn) {
        btn.addEventListener('click', function () {
          images.splice(parseInt(btn.dataset.i, 10), 1);
          renderImageGrid();
        });
      });
      grid.querySelectorAll('.pgp-img-type').forEach(function (sel) {
        sel.addEventListener('change', function () {
          images[parseInt(sel.dataset.i, 10)].imageType = sel.value;
        });
      });
      var cnt = document.getElementById('pgpImageCount');
      if (cnt) cnt.textContent = images.length + ' / ' + maxImages + ' images';
    }

    function collectPayload(publish) {
      syncSharingFromDom();
      syncFloorsFromDom();
      var amenities = [];
      document.querySelectorAll('#pgpAmenitiesGrid input:checked').forEach(function (cb) {
        if (amenities.indexOf(cb.value) === -1) {
          amenities.push(cb.value);
        }
      });
      return {
        pgName: val('pgName'),
        pgType: val('pgType'),
        genderAllowed: val('genderAllowed'),
        description: val('description'),
        address: val('address'),
        landmark: val('landmark'),
        city: val('city'),
        state: val('state'),
        pincode: val('pincode'),
        mapLocation: val('mapLocation'),
        nearbyPlaces: val('nearbyPlaces'),
        totalFloors: intVal('totalFloors'),
        totalRooms: intVal('totalRooms'),
        totalCapacity: intVal('totalCapacity'),
        liftAvailable: checked('liftAvailable'),
        parkingAvailable: checked('parkingAvailable'),
        cctvSecurity: checked('cctvSecurity'),
        biometricEntry: checked('biometricEntry'),
        fireSafety: checked('fireSafety'),
        availableFrom: val('availableFrom') || null,
        immediateAvailability: checked('immediateAvailability'),
        availableBeds: intVal('availableBeds'),
        monthlyRent: numVal('monthlyRent'),
        securityDeposit: numVal('securityDeposit'),
        maintenanceCharges: numVal('maintenanceCharges'),
        electricityIncluded: checked('electricityIncluded'),
        waterIncluded: checked('waterIncluded'),
        bookingAmount: numVal('bookingAmount'),
        floors: floors,
        sharingPrices: (function () {
          var seen = {};
          return sharingPrices.filter(function (s) { return s.monthlyRent; }).map(function (s) {
            return { sharingType: s.sharingType, monthlyRent: parseFloat(s.monthlyRent) };
          }).filter(function (s) {
            if (seen[s.sharingType]) return false;
            seen[s.sharingType] = true;
            return true;
          });
        })(),
        amenities: amenities,
        rules: {
          noSmoking: checked('ruleNoSmoking'),
          noAlcohol: checked('ruleNoAlcohol'),
          visitorsAllowed: checked('ruleVisitors'),
          petsAllowed: checked('rulePets'),
          curfewEnabled: checked('ruleCurfew'),
          idProofMandatory: checked('ruleIdProof'),
          curfewTiming: val('curfewTiming'),
          noticePeriodDays: intVal('noticePeriodDays'),
          securityDepositAmount: numVal('rulesSecurityDeposit')
        },
        publish: !!publish,
        featured: checked('featured'),
        imageMeta: images.map(function (img) { return { imageType: img.imageType }; })
      };
    }

    function submit(publish) {
      if (isSubmitting) {
        return;
      }
      if (!floors.length) {
        generateFloorsFromTotals(true);
      } else {
        updateCapacityFromFloors();
      }
      if (!validateForSubmit(publish)) {
        currentStep = 0;
        updateProgressBar();
        return;
      }
      var payload = collectPayload(publish);
      var fd = new FormData();
      fd.append('payload', JSON.stringify(payload));
      images.forEach(function (img) { fd.append('images', img.file); });

      var draftBtn = document.getElementById('pgpDraftBtn');
      var publishBtn = document.getElementById('pgpPublishBtn');
      isSubmitting = true;
      if (draftBtn) draftBtn.disabled = true;
      if (publishBtn) publishBtn.disabled = true;

      var req = editMode && propertyId
        ? PgOwnerPropertyAPI.update(ctx, propertyId, fd)
        : PgOwnerPropertyAPI.create(ctx, fd);

      req.then(function (res) {
        isSubmitting = false;
        if (draftBtn) draftBtn.disabled = false;
        if (publishBtn) publishBtn.disabled = false;
        if (!res.success) {
          toast(res.message || 'Save failed', true);
          return;
        }
        toast(res.message || 'Saved');
        var targetId = res.data.id;
        setTimeout(function () {
          if (!publish && !editMode) {
            window.location.href = ctx + '/pg-owner/properties/' + targetId + '/edit';
          } else {
            window.location.href = ctx + '/pg-owner/properties/' + targetId;
          }
        }, 800);
      }).catch(function () {
        isSubmitting = false;
        if (draftBtn) draftBtn.disabled = false;
        if (publishBtn) publishBtn.disabled = false;
        toast('Network error. Please try again.', true);
      });
    }

    function showPreview() {
      var data = collectPayload(false);
      var body = document.getElementById('pgpPreviewBody');
      if (!body) return;
      body.innerHTML = '<h4>' + escapeHtml(data.pgName) + '</h4><p>' + escapeHtml(data.city) + ', ' + escapeHtml(data.state) + '</p>' +
        '<p>Floors: ' + (data.floors || []).length + ' · Amenities: ' + (data.amenities || []).length + '</p>';
      new bootstrap.Modal(document.getElementById('pgpPreviewModal')).show();
    }

    function loadForEdit(id) {
      PgOwnerPropertyAPI.get(ctx, id).then(function (res) {
        if (!res.success || !res.data) return;
        var d = res.data;
        setVal('pgName', d.pgName);
        setVal('pgType', d.pgType);
        setVal('genderAllowed', d.genderAllowed);
        setVal('description', d.description);
        setVal('address', d.address);
        setVal('landmark', d.landmark);
        setVal('city', d.city);
        setVal('state', d.state);
        setVal('pincode', d.pincode);
        setVal('mapLocation', d.mapLocation);
        setVal('nearbyPlaces', d.nearbyPlaces);
        setVal('totalFloors', d.totalFloors);
        setVal('totalRooms', d.totalRooms);
        setVal('totalCapacity', d.totalCapacity);
        setVal('availableBeds', d.availableBeds);
        setChecked('liftAvailable', d.liftAvailable);
        setChecked('parkingAvailable', d.parkingAvailable);
        setChecked('cctvSecurity', d.cctvSecurity);
        setChecked('biometricEntry', d.biometricEntry);
        setChecked('fireSafety', d.fireSafety);
        if (d.availableFrom) setVal('availableFrom', d.availableFrom);
        setChecked('immediateAvailability', d.immediateAvailability);
        setVal('monthlyRent', d.monthlyRent);
        setVal('securityDeposit', d.securityDeposit);
        setVal('maintenanceCharges', d.maintenanceCharges);
        setChecked('electricityIncluded', d.electricityIncluded);
        setChecked('waterIncluded', d.waterIncluded);
        setVal('bookingAmount', d.bookingAmount);
        setChecked('featured', d.featured);
        if (d.amenities) {
          d.amenities.forEach(function (code) {
            var cb = document.querySelector('#pgpAmenitiesGrid input[value="' + code + '"]');
            if (cb) cb.checked = true;
          });
        }
        if (d.rules) {
          setChecked('ruleNoSmoking', d.rules.noSmoking);
          setChecked('ruleNoAlcohol', d.rules.noAlcohol);
          setChecked('ruleVisitors', d.rules.visitorsAllowed);
          setChecked('rulePets', d.rules.petsAllowed);
          setChecked('ruleCurfew', d.rules.curfewEnabled);
          setChecked('ruleIdProof', d.rules.idProofMandatory);
          setVal('curfewTiming', d.rules.curfewTiming);
          setVal('noticePeriodDays', d.rules.noticePeriodDays);
          setVal('rulesSecurityDeposit', d.rules.securityDepositAmount);
        }
        floors = (d.floors || []).map(function (f) {
          return {
            floorNumber: f.floorNumber,
            totalRooms: f.totalRooms,
            floorDescription: f.floorDescription,
            rooms: (f.rooms || []).map(function (r) {
              return {
                roomNumber: r.roomNumber,
                roomType: r.roomType,
                totalBeds: r.totalBeds,
                availableBeds: r.availableBeds,
                sharingType: r.sharingType,
                roomStatus: r.roomStatus
              };
            })
          };
        });
        floorsManualOverride = true;
        if (!floors.length) {
          generateFloorsFromTotals(true);
        } else {
          renderFloors();
          updateCapacityFromFloors();
          updateFloorSummary();
          updateRoomsPerFloorHint();
        }
        sharingPrices = (d.sharingPrices || []).map(function (s) {
          return { sharingType: s.sharingType, monthlyRent: s.monthlyRent };
        });
        if (!sharingPrices.length) renderSharingRows();
        else renderSharingRows();
      });
    }

    function setVal(id, v) {
      var el = document.getElementById(id);
      if (el && v != null) el.value = v;
    }

    function setChecked(id, v) {
      var el = document.getElementById(id);
      if (el) el.checked = !!v;
    }
  }

  function initDetails(ctx, propertyId) {
    PgOwnerPropertyAPI.get(ctx, propertyId).then(function (res) {
      if (!res.success || !res.data) return;
      var d = res.data;
      var root = document.getElementById('pgpDetailsRoot');
      if (!root) return;
      root.innerHTML =
        '<div class="pgp-glass-card"><h2 class="h4">' + escapeHtml(d.pgName) + '</h2>' +
        '<p style="color:var(--pgo-muted)">' + escapeHtml(d.address) + ', ' + escapeHtml(d.city) + '</p>' +
        '<span class="pgp-badge pgp-badge-' + (d.status || 'draft').toLowerCase() + '">' + d.status + '</span></div>' +
        '<div class="pgp-detail-grid mt-3">' +
        detailBlock('PG Type', d.pgType) + detailBlock('Gender', d.genderAllowed) +
        detailBlock('Floors', d.totalFloors) + detailBlock('Rooms', d.totalRooms) +
        detailBlock('Capacity', d.totalCapacity) + detailBlock('Available Beds', d.availableBeds) +
        detailBlock('Monthly Rent', d.monthlyRent ? '₹' + d.monthlyRent : '—') +
        detailBlock('Security Deposit', d.securityDeposit ? '₹' + d.securityDeposit : '—') +
        '</div>' +
        '<div class="pgp-glass-card mt-3"><h3 class="pgp-section-title">Description</h3><p>' + escapeHtml(d.description || '—') + '</p></div>' +
        '<div class="pgp-glass-card"><h3 class="pgp-section-title">Amenities</h3><p>' + (d.amenities || []).join(', ') + '</p></div>';
    });
  }

  function detailBlock(label, value) {
    return '<div class="pgp-glass-card"><div class="small" style="color:var(--pgo-muted)">' + label + '</div><strong>' + escapeHtml(value) + '</strong></div>';
  }

  function initGallery(ctx, propertyId) {
    PgOwnerPropertyAPI.get(ctx, propertyId).then(function (res) {
      if (!res.success || !res.data || !res.data.images || !res.data.images.length) return;
      var imgs = res.data.images;
      var main = document.getElementById('pgpGalleryMain');
      var thumbs = document.getElementById('pgpGalleryThumbs');
      if (!main || !thumbs) return;
      function show(i) {
        main.innerHTML = '<img src="' + ctx + imgs[i].imagePath + '" alt="PG photo">';
        thumbs.querySelectorAll('img').forEach(function (t, idx) {
          t.classList.toggle('active', idx === i);
        });
      }
      thumbs.innerHTML = imgs.map(function (img, i) {
        return '<img src="' + ctx + img.imagePath + '" data-i="' + i + '" alt=""' + (i === 0 ? ' class="active"' : '') + '>';
      }).join('');
      show(0);
      thumbs.querySelectorAll('img').forEach(function (t) {
        t.addEventListener('click', function () { show(parseInt(t.dataset.i, 10)); });
      });
    });
  }

  global.PgOwnerPropertyAPI = PgOwnerPropertyAPI;
  global.PgPropertyUI = {
    initManagePage: initManagePage,
    initWizard: initWizard,
    initDetails: initDetails,
    initGallery: initGallery
  };
})(typeof window !== 'undefined' ? window : this);
