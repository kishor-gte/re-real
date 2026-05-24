/**
 * Property book / detail page — structured listing information.
 */
(function (global) {
  'use strict';

  const PropertyBook = {
    ctx: '',
    propertyId: null,
    loggedIn: false,
    property: null,

    init(ctx, options) {
      this.ctx = (ctx || '').replace(/\/$/, '');
      this.propertyId = options && options.propertyId;
      this.loggedIn = !!(options && options.loggedIn);
      this.bindEnquiryUi();
      this.loadProperty();
    },

    bindEnquiryUi() {
      const self = this;
      const modal = document.getElementById('enquiryModal');
      const closeBtn = document.getElementById('enquiryModalClose');
      const cancelBtn = document.getElementById('enquiryModalCancel');
      const form = document.getElementById('enquiryForm');

      function closeModal() {
        if (modal) modal.classList.remove('active');
        if (form) form.reset();
      }

      closeBtn && closeBtn.addEventListener('click', closeModal);
      cancelBtn && cancelBtn.addEventListener('click', closeModal);
      modal && modal.addEventListener('click', function (e) {
        if (e.target === modal) closeModal();
      });

      form && form.addEventListener('submit', function (e) {
        e.preventDefault();
        self.submitEnquiry();
      });

      document.addEventListener('click', function (e) {
        const btn = e.target.closest('[data-book-enquire]');
        if (!btn) return;
        e.preventDefault();
        self.openEnquiryModal();
      });
    },

    openEnquiryModal() {
      if (!this.property) return;
      if (!this.loggedIn) {
        const returnUrl = encodeURIComponent(this.ctx + '/user/properties/' + this.propertyId + '/book');
        window.location.href = this.ctx + '/user/login?redirect=' + returnUrl;
        return;
      }
      const titleEl = document.getElementById('enquiryPropertyTitle');
      const idEl = document.getElementById('enquiryPropertyId');
      if (titleEl) titleEl.textContent = this.property.title || 'Property';
      if (idEl) idEl.value = this.property.id;
      document.getElementById('enquiryModal')?.classList.add('active');
    },

    async submitEnquiry() {
      const messageEl = document.getElementById('enquiryMessage');
      const message = (messageEl && messageEl.value || '').trim();
      if (!this.property || !this.property.id) return;
      if (message.length < 10) {
        toast('Please enter at least 10 characters', 'error');
        return;
      }
      try {
        const data = await AuthAPI.request(this.ctx + '/api/user/enquiries', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ propertyId: Number(this.property.id), message: message })
        });
        document.getElementById('enquiryModal')?.classList.remove('active');
        document.getElementById('enquiryForm')?.reset();
        toast(data.message || 'Enquiry sent', 'success');
      } catch (err) {
        toast(err.message || 'Could not submit enquiry', 'error');
      }
    },

    async loadProperty() {
      const loader = document.getElementById('propertyBookLoader');
      const errEl = document.getElementById('propertyBookError');
      const content = document.getElementById('propertyBookContent');

      if (!this.propertyId) {
        this.showError('Invalid property.');
        return;
      }

      try {
        const res = await fetch(this.ctx + '/api/public/properties/' + this.propertyId, { credentials: 'same-origin' });
        const data = await res.json().catch(function () { return {}; });
        if (!res.ok || data.success === false) {
          throw new Error(data.message || 'Property not found');
        }
        this.property = normalizeProperty(data.data);
        if (content) {
          content.innerHTML = this.renderPage(this.property);
          content.style.display = 'block';
        }
        document.title = (this.property.title || 'Property') + ' | EstateVault';
      } catch (err) {
        this.showError(err.message || 'Could not load property');
      } finally {
        if (loader) loader.style.display = 'none';
      }
    },

    showError(msg) {
      const errEl = document.getElementById('propertyBookError');
      const loader = document.getElementById('propertyBookLoader');
      if (loader) loader.style.display = 'none';
      if (errEl) {
        errEl.style.display = 'block';
        errEl.innerHTML = '<p>' + escapeHtml(msg) + '</p>' +
          '<a href="' + escapeHtml(this.ctx) + '/user/properties" class="btn-gold">Back to Explore</a>';
      }
    },

    bookingPaymentUrl(propertyId) {
      const target = this.ctx + '/user/booking/payment?propertyId=' + propertyId;
      if (!this.loggedIn) {
        return this.ctx + '/user/login?redirect=' + encodeURIComponent(target);
      }
      return target;
    },

    renderPage(p) {
      const listing = listingTypeName(p.listingType);
      const tag = listing === 'RENT' ? 'rent' : 'sale';
      const tagLabel = p.listingTypeLabel || (listing === 'RENT' ? 'For Rent' : 'For Sale');
      const price = formatPrice(p.price, listing);
      const heroImg = imageUrl(this.ctx, p.primaryImageUrl);
      const gallery = (p.galleryUrls || []).map(function (u) {
        return '<img src="' + escapeAttr(imageUrl(PropertyBook.ctx, u)) + '" alt="" loading="lazy">';
      }).join('');

      const sections = [
        this.sectionOverview(p),
        this.sectionLocation(p),
        this.sectionArea(p),
        this.sectionResidential(p),
        this.sectionPlotLand(p),
        this.sectionCommercial(p),
        this.sectionProject(p),
        this.sectionAmenities(p)
      ].filter(Boolean).join('');

      return '<article class="property-book">' +
        '<header class="property-book-hero">' +
        '<div class="property-book-hero-media">' +
        '<img src="' + escapeAttr(heroImg) + '" alt="' + escapeAttr(p.title || '') + '">' +
        (gallery ? '<div class="property-book-gallery">' + gallery + '</div>' : '') +
        '</div>' +
        '<div class="property-book-hero-body">' +
        '<span class="property-tag ' + tag + '">' + escapeHtml(tagLabel) + '</span>' +
        '<span class="property-book-code">' + escapeHtml(p.propertyCode || '') + '</span>' +
        '<h1>' + escapeHtml(p.title) + '</h1>' +
        '<p class="property-book-loc">&#128205; ' + escapeHtml(fullLocation(p)) + '</p>' +
        '<p class="property-book-price">' + price +
        (p.priceNegotiable ? ' <span class="property-book-negotiable">Negotiable</span>' : '') + '</p>' +
        '<div class="property-book-tags">' +
        '<span class="property-book-tag">' + escapeHtml(p.categoryLabel || '') + '</span>' +
        (p.propertySubType ? '<span class="property-book-tag">' + escapeHtml(p.propertySubType) + '</span>' : '') +
        '</div>' +
        (p.agentName ? '<p class="property-book-agent">Listed by <strong>' + escapeHtml(p.agentName) + '</strong></p>' : '') +
        '<div class="property-book-actions">' +
        '<a href="' + escapeAttr(this.bookingPaymentUrl(p.id)) + '" class="btn-book-now"><span class="btn-book-now-shine"></span>Book Property</a>' +
        '<button type="button" class="btn-gold" data-book-enquire>Enquire now</button>' +
        '<a href="' + escapeAttr(this.ctx) + '/user/properties" class="btn-outline">Explore more</a>' +
        '</div></div></header>' +
        '<div class="property-book-sections">' + sections + '</div>' +
        '</article>';
    },

    sectionOverview(p) {
      const rowsHtml = joinRows([
        row('Listing type', p.listingTypeLabel || listingTypeName(p.listingType)),
        row('Category', p.categoryLabel),
        row('Property type', p.propertySubType),
        row('Listing code', p.propertyCode),
        row('Views', p.viewCount != null ? String(p.viewCount) : null)
      ]);
      const desc = p.description ? '<div class="property-book-description"><h3>Description</h3><p>' +
        escapeHtml(p.description).replace(/\n/g, '<br>') + '</p></div>' : '';
      return section('Overview', rowsHtml, desc);
    },

    sectionLocation(p) {
      const rowsHtml = joinRows([
        row('Address', p.addressLine),
        row('Locality', p.locality),
        row('City', p.city),
        row('State', p.state),
        row('PIN code', p.pincode)
      ]);
      if (!rowsHtml) {
        return '';
      }
      return section('Location', rowsHtml);
    },

    sectionArea(p) {
      const rowsHtml = joinRows([
        row('Carpet / built-up area', formatSqFt(p.areaSqFt)),
        row('Super built-up area', formatSqFt(p.superBuiltUpSqFt)),
        row('Plot area', formatSqFt(p.plotAreaSqFt)),
        row('Plot length', formatFt(p.plotLengthFt)),
        row('Plot width', formatFt(p.plotWidthFt)),
        row('Facing', p.facing)
      ]);
      if (!rowsHtml) return '';
      return section('Area & dimensions', rowsHtml);
    },

    sectionResidential(p) {
      const rowsHtml = joinRows([
        row('BHK', p.bhk),
        row('Bathrooms', p.bathrooms),
        row('Balconies', p.balconies),
        row('Floor', formatFloor(p.floorNumber, p.totalFloors)),
        row('Furnishing', p.furnishingLabel),
        row('Property age', p.propertyAgeYears != null ? p.propertyAgeYears + ' years' : null),
        row('Parking slots', p.parkingSlots)
      ]);
      if (!rowsHtml) return '';
      return section('Home details', rowsHtml);
    },

    sectionPlotLand(p) {
      const rowsHtml = joinRows([
        row('Land use', p.landUseLabel),
        row('Boundary wall', formatBool(p.boundaryWall)),
        row('Corner plot', formatBool(p.cornerPlot))
      ]);
      if (!rowsHtml) return '';
      return section('Plot & land', rowsHtml);
    },

    sectionCommercial(p) {
      const rowsHtml = joinRows([
        row('Commercial type', p.commercialTypeLabel),
        row('Seating capacity', p.seatsCapacity)
      ]);
      if (!rowsHtml) return '';
      return section('Commercial', rowsHtml);
    },

    sectionProject(p) {
      const rowsHtml = joinRows([
        row('Project name', p.projectName),
        row('Builder', p.builderName),
        row('Construction status', p.constructionStatusLabel),
        row('Possession date', formatDate(p.possessionDate)),
        row('Total units', p.totalUnits),
        row('Unit configuration', p.unitConfiguration),
        row('Private pool', formatBool(p.privatePool)),
        row('Private garden', formatBool(p.privateGarden))
      ]);
      if (!rowsHtml) return '';
      return section('Project & builder', rowsHtml);
    },

    sectionAmenities(p) {
      if (!p.amenities || !String(p.amenities).trim()) return '';
      const items = String(p.amenities).split(/[,;|]/).map(function (s) { return s.trim(); }).filter(Boolean);
      const list = items.length
        ? '<ul class="property-book-amenity-list">' + items.map(function (i) {
          return '<li>' + escapeHtml(i) + '</li>';
        }).join('') + '</ul>'
        : '<p>' + escapeHtml(p.amenities) + '</p>';
      return '<section class="property-book-section"><h2>Amenities</h2>' + list + '</section>';
    }
  };

  function section(title, rowsHtml, extra) {
    const html = rowsHtml || '';
    if (!html && !extra) {
      return '';
    }
    const grid = html ? '<dl class="property-book-grid">' + html + '</dl>' : '';
    return '<section class="property-book-section"><h2>' + escapeHtml(title) + '</h2>' +
      grid + (extra || '') + '</section>';
  }

  function joinRows(rows) {
    if (!rows || !rows.length) {
      return '';
    }
    return rows.filter(function (r) { return r; }).join('');
  }

  function row(label, value) {
    if (value == null || value === '' || value === undefined) {
      return '';
    }
    return '<div class="property-book-row"><dt>' + escapeHtml(label) + '</dt><dd>' + escapeHtml(String(value)) + '</dd></div>';
  }

  function normalizeProperty(p) {
    if (!p) {
      return p;
    }
    const out = Object.assign({}, p);
    if (out.galleryUrls == null) {
      out.galleryUrls = [];
    } else if (typeof out.galleryUrls === 'string') {
      out.galleryUrls = out.galleryUrls.split(',').map(function (s) { return s.trim(); }).filter(Boolean);
    } else if (!Array.isArray(out.galleryUrls)) {
      out.galleryUrls = [];
    }
    if (!out.listingTypeLabel && out.listingType) {
      const lt = listingTypeName(out.listingType);
      out.listingTypeLabel = lt === 'RENT' ? 'For Rent' : lt === 'SALE' ? 'For Sale' : lt;
    }
    return out;
  }

  function formatDate(v) {
    if (v == null || v === '') {
      return null;
    }
    if (Array.isArray(v) && v.length >= 3) {
      const y = v[0];
      const m = String(v[1]).padStart(2, '0');
      const d = String(v[2]).padStart(2, '0');
      return y + '-' + m + '-' + d;
    }
    if (typeof v === 'string') {
      return v.split('T')[0];
    }
    return String(v);
  }

  function fullLocation(p) {
    return [p.locality, p.city, p.state].filter(Boolean).join(', ');
  }

  function formatSqFt(n) {
    if (n == null || n <= 0) return null;
    return Math.round(n) + ' sq.ft';
  }

  function formatFt(n) {
    if (n == null || n <= 0) return null;
    return n + ' ft';
  }

  function formatFloor(floor, total) {
    if (floor == null && total == null) return null;
    if (floor != null && total != null) return floor + ' of ' + total;
    if (floor != null) return String(floor);
    return total + ' floors';
  }

  function formatBool(v) {
    if (v == null) return null;
    return v ? 'Yes' : 'No';
  }

  function listingTypeName(listingType) {
    if (!listingType) return '';
    if (typeof listingType === 'string') return listingType;
    return listingType.name || String(listingType);
  }

  function formatPrice(amount, listingType) {
    const n = parseFloat(amount);
    if (isNaN(n)) return '\u20B9 —';
    const suffix = listingType === 'RENT' ? '/mo' : '';
    if (n >= 10000000) return '\u20B9 ' + (n / 10000000).toFixed(2) + ' Cr' + suffix;
    if (n >= 100000) return '\u20B9 ' + (n / 100000).toFixed(2) + ' L' + suffix;
    return '\u20B9 ' + n.toLocaleString('en-IN') + suffix;
  }

  function imageUrl(ctx, path) {
    if (!path) {
      return 'https://images.unsplash.com/photo-1600596542815-ffad4c1539a9?w=1200&q=80';
    }
    return ctx + (path.charAt(0) === '/' ? path : '/' + path);
  }

  function escapeHtml(s) {
    if (!s) return '';
    return String(s).replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
  }

  function escapeAttr(s) {
    return escapeHtml(s).replace(/"/g, '&quot;');
  }

  function toast(msg, type) {
    if (typeof UI !== 'undefined' && UI.toast) {
      UI.toast(msg, type);
      return;
    }
    alert(msg);
  }

  global.PropertyBook = PropertyBook;
})(typeof window !== 'undefined' ? window : this);
