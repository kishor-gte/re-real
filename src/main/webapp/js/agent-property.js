/**
 * Agent property listing — add form & my properties list.
 */
(function (global) {
  'use strict';

  const AgentProperty = {
    _listCtx: '',
    _submitInProgress: false,

    initAddForm(ctx) {
      const form = document.getElementById('addPropertyForm');
      if (!form || form.dataset.propertyFormBound === 'true') return;
      form.dataset.propertyFormBound = 'true';

      const spec = (document.getElementById('specializationKey') || {}).value || '';
      this.applySpecializationFields(spec);
      this.initSelectFields();

      if (spec === 'RENTAL') {
        const lt = document.getElementById('listingType');
        if (lt) lt.value = 'RENT';
      }

      form.addEventListener('submit', function (e) {
        e.preventDefault();
        AgentProperty.submitAdd(ctx, form, spec);
      });
    },

    initSelectFields() {
      document.querySelectorAll('.agent-form-group--select .agent-form-select').forEach(function (sel) {
        if (sel.dataset.selectUiInit === 'true') return;
        sel.dataset.selectUiInit = 'true';
        function update() {
          if (sel.value) {
            sel.classList.add('has-value');
          } else {
            sel.classList.remove('has-value');
          }
        }
        sel.addEventListener('change', update);
        update();
      });
    },

    applySpecializationFields(spec) {
      const form = document.getElementById('addPropertyForm');
      if (form) {
        form.querySelectorAll('[data-spec-required]').forEach(function (el) {
          el.removeAttribute('required');
          el.removeAttribute('data-spec-required');
        });
      }

      document.querySelectorAll('.spec-field, .spec-block').forEach(function (el) {
        const allowed = (el.getAttribute('data-spec') || '').split(/\s+/);
        const show = allowed.indexOf(spec) >= 0;
        el.style.display = show ? '' : 'none';
        el.querySelectorAll('input, select, textarea').forEach(function (input) {
          if (!show) {
            input.removeAttribute('required');
            input.disabled = true;
          } else {
            input.disabled = false;
          }
        });
      });

      if (['RESIDENTIAL', 'APARTMENTS', 'RENTAL'].indexOf(spec) >= 0) {
        this.requireField('areaSqFt');
        this.requireField('bhk');
      }
      if (spec === 'LUXURY_VILLAS') {
        this.requireField('areaSqFt');
        this.requireField('bhk');
      }
      if (spec === 'COMMERCIAL') {
        this.requireField('areaSqFt');
        this.requireField('commercialType');
      }
      if (spec === 'PLOTS_LANDS') {
        this.requireField('plotAreaSqFt');
        this.requireField('landUse');
      }
      if (spec === 'CONSTRUCTION') {
        this.requireField('projectName');
        this.requireField('builderName');
        this.requireField('constructionStatus');
      }
    },

    requireField(id) {
      const el = document.getElementById(id);
      if (el && !el.disabled) {
        el.setAttribute('required', 'required');
        el.setAttribute('data-spec-required', 'true');
      }
    },

    collectAmenities() {
      const hidden = document.getElementById('amenities');
      if (!hidden) return;
      const selected = [];
      document.querySelectorAll('.amenity-cb:checked').forEach(function (cb) {
        selected.push(cb.value);
      });
      hidden.value = selected.join(', ');
    },

    async submitAdd(ctx, form, spec) {
      if (this._submitInProgress) return;
      if (!this.validateForm(spec, true)) return;

      this._submitInProgress = true;
      this.setSubmitBusy(form, true);
      this.collectAmenities();
      const fd = this.buildFormData(form);

      UI.showLoader();
      try {
        const res = await fetch(ctx + '/api/agent/properties', {
          method: 'POST',
          body: fd,
          credentials: 'same-origin'
        });
        const data = await res.json().catch(function () { return {}; });
        if (!res.ok || data.success === false) {
          const msg = data.message || 'Could not save property';
          if (msg.toLowerCase().indexOf('free') >= 0 || msg.toLowerCase().indexOf('subscription') >= 0 || msg.toLowerCase().indexOf('limit') >= 0) {
            AgentProperty._submitInProgress = false;
            AgentProperty.setSubmitBusy(form, false);
            AgentProperty.showPostingLimitModal(ctx, msg);
            return;
          }
          throw new Error(msg);
        }
        UI.toast(data.message || 'Property listed!');
        window.setTimeout(function () {
          window.location.assign(ctx + '/agent/properties');
        }, 600);
      } catch (err) {
        this._submitInProgress = false;
        this.setSubmitBusy(form, false);
        UI.toast(err.message || 'Failed to add property', 'error');
        if (err.data && typeof err.data === 'object') {
          Object.values(err.data).forEach(function (m) { if (m) UI.toast(m, 'error'); });
        }
      } finally {
        UI.hideLoader();
      }
    },

    initEditForm(ctx) {
      const form = document.getElementById('addPropertyForm');
      const propertyId = (document.getElementById('propertyId') || {}).value;
      if (!form || !propertyId || form.dataset.propertyFormBound === 'true') return;

      const spec = (document.getElementById('specializationKey') || {}).value || '';
      const base = ctx.replace(/\/$/, '');

      UI.showLoader();
      fetch(base + '/api/agent/properties/' + propertyId, { credentials: 'same-origin' })
        .then(function (res) { return res.json().then(function (d) { return { res: res, data: d }; }); })
        .then(function (out) {
          if (!out.res.ok || out.data.success === false) {
            throw new Error(out.data.message || 'Could not load property');
          }
          form.dataset.propertyFormBound = 'true';
          AgentProperty.populateForm(out.data.data, spec);
          AgentProperty.applySpecializationFields(spec);
          AgentProperty.initSelectFields();
          form.addEventListener('submit', function (e) {
            e.preventDefault();
            AgentProperty.submitEdit(base, form, spec, propertyId);
          });
        })
        .catch(function (err) {
          UI.toast(err.message, 'error');
          window.setTimeout(function () { window.location.assign(base + '/agent/properties'); }, 800);
        })
        .finally(function () { UI.hideLoader(); });
    },

    populateForm(p, spec) {
      if (!p) return;
      setVal('title', p.title);
      setVal('listingType', p.listingType);
      setVal('propertySubType', p.propertySubType);
      setVal('price', p.price);
      setCheck('priceNegotiable', p.priceNegotiable !== false);
      setVal('description', p.description || '');
      setVal('areaSqFt', p.areaSqFt);
      setVal('plotAreaSqFt', p.plotAreaSqFt);
      setVal('superBuiltUpSqFt', p.superBuiltUpSqFt);
      setVal('bhk', p.bhk);
      setVal('furnishing', p.furnishing);
      setVal('bathrooms', p.bathrooms);
      setVal('balconies', p.balconies);
      setVal('floorNumber', p.floorNumber);
      setVal('totalFloors', p.totalFloors);
      setVal('propertyAgeYears', p.propertyAgeYears);
      setVal('parkingSlots', p.parkingSlots);
      setVal('commercialType', p.commercialType);
      setVal('seatsCapacity', p.seatsCapacity);
      setVal('plotLengthFt', p.plotLengthFt);
      setVal('plotWidthFt', p.plotWidthFt);
      setVal('facing', p.facing);
      setVal('landUse', p.landUse);
      setCheck('boundaryWall', p.boundaryWall === true);
      setCheck('cornerPlot', p.cornerPlot === true);
      setVal('projectName', p.projectName);
      setVal('builderName', p.builderName);
      setVal('constructionStatus', p.constructionStatus);
      setVal('possessionDate', p.possessionDate);
      setVal('totalUnits', p.totalUnits);
      setVal('unitConfiguration', p.unitConfiguration);
      setCheck('privatePool', p.privatePool === true);
      setCheck('privateGarden', p.privateGarden === true);
      setVal('addressLine', p.addressLine);
      setVal('locality', p.locality);
      setVal('city', p.city);
      setVal('state', p.state);
      setVal('pincode', p.pincode);

      if (p.amenities) {
        const selected = String(p.amenities).split(',').map(function (s) { return s.trim(); });
        document.querySelectorAll('.amenity-cb').forEach(function (cb) {
          cb.checked = selected.indexOf(cb.value) >= 0;
        });
      }

      if (p.primaryImageUrl) {
        const wrap = document.getElementById('currentPrimaryPreview');
        const img = document.getElementById('currentPrimaryImg');
        if (wrap && img) {
          const ctx = (typeof AgentAPI !== 'undefined' && AgentAPI.base) ? AgentAPI.base : '';
          img.src = baseUrl(ctx.replace(/\/$/, ''), p.primaryImageUrl);
          wrap.style.display = 'block';
        }
      }
    },

    async submitEdit(ctx, form, spec, propertyId) {
      if (this._submitInProgress) return;
      if (!this.validateForm(spec, false)) return;

      this._submitInProgress = true;
      this.setSubmitBusy(form, true);
      this.collectAmenities();
      const fd = this.buildFormData(form);
      UI.showLoader();
      try {
        const res = await fetch(ctx + '/api/agent/properties/' + propertyId, {
          method: 'PUT',
          body: fd,
          credentials: 'same-origin'
        });
        const data = await res.json().catch(function () { return {}; });
        if (!res.ok || data.success === false) {
          throw new Error(data.message || 'Could not update property');
        }
        UI.toast(data.message || 'Property updated');
        window.setTimeout(function () { window.location.assign(ctx + '/agent/properties'); }, 600);
      } catch (err) {
        this._submitInProgress = false;
        this.setSubmitBusy(form, false);
        UI.toast(err.message || 'Update failed', 'error');
      } finally {
        UI.hideLoader();
      }
    },

    setSubmitBusy(form, busy) {
      const btn = document.getElementById('submitPropertyBtn');
      if (!btn) return;
      if (!btn.dataset.defaultLabel) {
        btn.dataset.defaultLabel = btn.textContent.trim();
      }
      btn.disabled = !!busy;
      btn.setAttribute('aria-busy', busy ? 'true' : 'false');
      btn.textContent = busy ? 'Saving…' : btn.dataset.defaultLabel;
    },

    validateForm(spec, requirePrimary) {
      const title = document.getElementById('title').value.trim();
      const price = document.getElementById('price').value;
      const subType = document.getElementById('propertySubType').value;
      const pincode = document.getElementById('pincode').value.trim();
      if (title.length < 5) { UI.toast('Title must be at least 5 characters', 'error'); return false; }
      if (!subType) { UI.toast('Select property category', 'error'); return false; }
      if (!price || parseFloat(price) <= 0) { UI.toast('Enter a valid price', 'error'); return false; }
      if (!/^\d{6}$/.test(pincode)) { UI.toast('Pincode must be 6 digits', 'error'); return false; }
      const primaryEl = document.getElementById('primaryImage');
      if (requirePrimary && primaryEl && !primaryEl.disabled && !primaryEl.files[0]) {
        UI.toast('Primary image is required', 'error');
        return false;
      }

      if (['RESIDENTIAL', 'APARTMENTS', 'RENTAL', 'LUXURY_VILLAS'].indexOf(spec) >= 0) {
        if (!this.positiveNumber('areaSqFt')) {
          UI.toast('Built-up / carpet area (sq.ft) is required', 'error');
          return false;
        }
        const bhk = document.getElementById('bhk');
        if (bhk && !bhk.disabled && !bhk.value) {
          UI.toast('Select BHK configuration', 'error');
          return false;
        }
      }
      if (spec === 'COMMERCIAL') {
        if (!this.positiveNumber('areaSqFt')) {
          UI.toast('Carpet / built-up area (sq.ft) is required', 'error');
          return false;
        }
        const ct = document.getElementById('commercialType');
        if (ct && !ct.value) { UI.toast('Commercial property type is required', 'error'); return false; }
      }
      if (spec === 'PLOTS_LANDS') {
        if (!this.positiveNumber('plotAreaSqFt')) {
          UI.toast('Plot area (sq.ft) is required', 'error');
          return false;
        }
        const land = document.getElementById('landUse');
        if (land && !land.value) { UI.toast('Land use type is required', 'error'); return false; }
      }
      if (spec === 'CONSTRUCTION') {
        if (!document.getElementById('projectName').value.trim()) {
          UI.toast('Project name is required', 'error');
          return false;
        }
        if (!document.getElementById('builderName').value.trim()) {
          UI.toast('Builder name is required', 'error');
          return false;
        }
        const cs = document.getElementById('constructionStatus');
        if (cs && !cs.value) { UI.toast('Construction status is required', 'error'); return false; }
      }
      return true;
    },

    positiveNumber(id) {
      const el = document.getElementById(id);
      if (!el || el.disabled) return false;
      const n = parseFloat(el.value);
      return !isNaN(n) && n > 0;
    },

    buildFormData(form) {
      const fd = new FormData();
      const elements = form.querySelectorAll('input, select, textarea');
      elements.forEach(function (el) {
        if (!el.name || el.disabled) return;
        if (el.type === 'file') {
          if (el.files && el.files.length) {
            if (el.multiple) {
              for (let i = 0; i < el.files.length; i++) {
                fd.append(el.name, el.files[i]);
              }
            } else {
              fd.append(el.name, el.files[0]);
            }
          }
          return;
        }
        if (el.type === 'checkbox') {
          if (el.checked) fd.append(el.name, el.value || 'true');
          return;
        }
        if (el.type === 'radio' && !el.checked) return;
        fd.append(el.name, el.value);
      });
      const negotiable = document.getElementById('priceNegotiable');
      if (negotiable && !negotiable.disabled) {
        fd.set('priceNegotiable', negotiable.checked ? 'true' : 'false');
      }
      return fd;
    },

    async initList(ctx) {
      const grid = document.getElementById('propertiesGrid');
      const empty = document.getElementById('propertiesEmpty');
      if (!grid) return;

      const base = ctx.replace(/\/$/, '');
      this._listCtx = base;

      grid.addEventListener('click', function (e) {
        const btn = e.target.closest('[data-action]');
        if (!btn) return;
        const id = btn.getAttribute('data-id');
        const action = btn.getAttribute('data-action');
        if (!id) return;
        if (action === 'edit') {
          window.location.assign(base + '/agent/properties/' + id + '/edit');
        } else if (action === 'delete') {
          AgentProperty.confirmDelete(base, id);
        } else if (action === 'toggle-status') {
          AgentProperty.toggleStatus(base, id, btn);
        }
      });

      await this.reloadList();
    },

    async reloadList() {
      const grid = document.getElementById('propertiesGrid');
      const empty = document.getElementById('propertiesEmpty');
      const base = this._listCtx;

      UI.showLoader();
      try {
        const res = await fetch(base + '/api/agent/properties', { credentials: 'same-origin' });
        const data = await res.json().catch(function () { return {}; });
        if (!res.ok || data.success === false) {
          throw new Error(data.message || 'Could not load properties');
        }
        const list = data.data || [];
        if (!list.length) {
          grid.innerHTML = '';
          empty.style.display = 'block';
          return;
        }
        empty.style.display = 'none';
        grid.innerHTML = list.map(function (p) { return AgentProperty.cardHtml(base, p); }).join('');
      } catch (err) {
        UI.toast(err.message, 'error');
        grid.innerHTML = '';
        empty.style.display = 'block';
      } finally {
        UI.hideLoader();
      }
    },

    async confirmDelete(ctx, id) {
      const ok = typeof Swal !== 'undefined'
        ? (await Swal.fire({
          title: 'Delete property?',
          text: 'This cannot be undone.',
          icon: 'warning',
          showCancelButton: true,
          confirmButtonColor: '#ef4444',
          confirmButtonText: 'Delete'
        })).isConfirmed
        : window.confirm('Delete this property?');
      if (!ok) return;

      UI.showLoader();
      try {
        const res = await fetch(ctx + '/api/agent/properties/' + id, {
          method: 'DELETE',
          credentials: 'same-origin'
        });
        const data = await res.json().catch(function () { return {}; });
        if (!res.ok || data.success === false) {
          throw new Error(data.message || 'Delete failed');
        }
        UI.toast(data.message || 'Property deleted');
        await this.reloadList();
      } catch (err) {
        UI.toast(err.message, 'error');
      } finally {
        UI.hideLoader();
      }
    },

    async toggleStatus(ctx, id, btn) {
      UI.showLoader();
      try {
        const res = await fetch(ctx + '/api/agent/properties/' + id + '/status', {
          method: 'PATCH',
          credentials: 'same-origin'
        });
        const data = await res.json().catch(function () { return {}; });
        if (!res.ok || data.success === false) {
          throw new Error(data.message || 'Could not update status');
        }
        UI.toast(data.message || 'Status updated');
        await this.reloadList();
      } catch (err) {
        UI.toast(err.message, 'error');
      } finally {
        UI.hideLoader();
      }
    },

    cardHtml(ctx, p) {
      const tag = p.listingType === 'RENT' ? 'rent' : 'sale';
      const tagLabel = p.listingType === 'RENT' ? 'For Rent' : 'For Sale';
      const price = formatPrice(p.price, p.listingType);
      const specs = buildSpecs(p);
      const imgPath = p.primaryImageUrl || '';
      const img = imgPath
        ? (imgPath.startsWith('http') ? imgPath : baseUrl(ctx, imgPath))
        : 'https://images.unsplash.com/photo-1600596542815-ffad4c1539a9?w=800&q=80';
      const loc = escapeHtml((p.locality || '') + ', ' + (p.city || ''));
      const amenitiesHtml = buildAmenityPills(p.amenities);
      const isActive = p.status === 'ACTIVE';
      const statusClass = isActive ? 'active' : 'inactive';
      const statusLabel = isActive ? 'Active' : 'Inactive';
      const toggleLabel = isActive ? 'Mark inactive' : 'Mark active';
      const toggleHint = isActive
        ? 'Hide from listings (rented/sold pause)'
        : 'Available for rent/sale again';

      return '<article class="agent-property-card' + (isActive ? '' : ' agent-property-card--inactive') + '">' +
        '<div class="agent-property-card-img">' +
        '<img src="' + escapeAttr(img) + '" alt="' + escapeAttr(p.title || 'Property') + '" loading="lazy">' +
        '<span class="agent-property-tag ' + tag + '">' + tagLabel + '</span>' +
        '<span class="agent-property-status ' + statusClass + '">' + statusLabel + '</span></div>' +
        '<div class="agent-property-card-body">' +
        '<h4>' + escapeHtml(p.title) + '</h4>' +
        '<p class="agent-property-loc">&#128205; ' + loc + '</p>' +
        '<p class="agent-property-price">' + price + '</p>' +
        '<p class="agent-property-specs">' + specs + '</p>' +
        amenitiesHtml +
        '<div class="agent-property-meta">' +
        '<span class="agent-property-badge code">' + escapeHtml(p.propertyCode || '') + '</span>' +
        '<span class="agent-property-badge">' + escapeHtml(p.propertySubType || '') + '</span>' +
        '</div>' +
        '<div class="agent-property-actions">' +
        '<button type="button" class="agent-prop-btn agent-prop-btn--edit" data-action="edit" data-id="' + p.id + '">Edit</button>' +
        '<button type="button" class="agent-prop-btn agent-prop-btn--toggle" data-action="toggle-status" data-id="' + p.id + '" title="' + escapeAttr(toggleHint) + '">' + toggleLabel + '</button>' +
        '<button type="button" class="agent-prop-btn agent-prop-btn--delete" data-action="delete" data-id="' + p.id + '">Delete</button>' +
        '</div></div></article>';
    }
  };

  function setVal(id, val) {
    const el = document.getElementById(id);
    if (!el || val === null || val === undefined) return;
    el.value = val;
    if (el.tagName === 'SELECT') el.classList.add('has-value');
  }

  function setCheck(id, checked) {
    const el = document.getElementById(id);
    if (el) el.checked = !!checked;
  }

  function baseUrl(ctx, path) {
    if (!path) return '';
    if (path.charAt(0) === '/') return ctx + path;
    return ctx + '/' + path;
  }

  function formatPrice(amount, listingType) {
    const n = parseFloat(amount);
    if (isNaN(n)) return '\u20B9 —';
    const suffix = listingType === 'RENT' ? '/mo' : '';
    if (n >= 10000000) return '\u20B9 ' + (n / 10000000).toFixed(2) + ' Cr' + suffix;
    if (n >= 100000) return '\u20B9 ' + (n / 100000).toFixed(2) + ' L' + suffix;
    return '\u20B9 ' + n.toLocaleString('en-IN') + suffix;
  }

  function buildSpecs(p) {
    const parts = [];
    if (p.bhk) parts.push(escapeHtml(p.bhk));
    if (p.areaSqFt) parts.push(Math.round(p.areaSqFt) + ' sq.ft');
    if (p.plotAreaSqFt) parts.push(Math.round(p.plotAreaSqFt) + ' plot sq.ft');
    if (p.commercialType) parts.push(escapeHtml(String(p.commercialType).replace(/_/g, ' ')));
    if (p.projectName) parts.push(escapeHtml(p.projectName));
    if (!parts.length && p.propertySubType) parts.push(escapeHtml(p.propertySubType));
    return parts.join(' \u2022 ');
  }

  function buildAmenityPills(amenitiesStr) {
    if (!amenitiesStr || !String(amenitiesStr).trim()) return '';
    const items = String(amenitiesStr).split(',').map(function (s) { return s.trim(); }).filter(Boolean);
    if (!items.length) return '';
    const pills = items.slice(0, 5).map(function (a) {
      return '<span class="agent-property-amenity-pill">' + escapeHtml(a) + '</span>';
    }).join('');
    const more = items.length > 5 ? '<span class="agent-property-amenity-pill">+' + (items.length - 5) + '</span>' : '';
    return '<div class="agent-property-amenities">' + pills + more + '</div>';
  }

  function escapeHtml(s) {
    if (!s) return '';
    return String(s).replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;');
  }

  function escapeAttr(s) {
    return escapeHtml(s).replace(/'/g, '&#39;');
  }

  AgentProperty.showPostingLimitModal = function (ctx, message) {
    const plansUrl = (ctx || '').replace(/\/$/, '') + '/agent/subscription/plans';
    const text = message || 'You have used your 2 free property listings. Upgrade your subscription to continue posting.';
    if (typeof Swal !== 'undefined') {
      Swal.fire({
        icon: 'warning',
        title: 'Free Property Posting Limit Reached',
        html: '<p>' + escapeHtml(text) + '</p>',
        confirmButtonText: 'View Subscription Plans',
        confirmButtonColor: '#c9a227',
        showCancelButton: true,
        cancelButtonText: 'Close'
      }).then(function (r) {
        if (r.isConfirmed) window.location.assign(plansUrl);
      });
      return;
    }
    if (window.confirm(text + '\n\nOpen subscription plans?')) {
      window.location.assign(plansUrl);
    }
  };

  global.AgentProperty = AgentProperty;
})(typeof window !== 'undefined' ? window : this);
