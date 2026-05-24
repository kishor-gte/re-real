/**
 * PG book / detail page — full PG information for users.
 */
(function (global) {
  'use strict';

  const PgBook = {
    ctx: '',
    pgId: null,
    loggedIn: false,
    pg: null,

    init(ctx, options) {
      this.ctx = (ctx || '').replace(/\/$/, '');
      this.pgId = options && options.pgId;
      this.loggedIn = !!(options && options.loggedIn);
      this.loadPg();
    },

    async loadPg() {
      const loader = document.getElementById('pgBookLoader');
      const errEl = document.getElementById('pgBookError');
      const content = document.getElementById('pgBookContent');

      if (!this.pgId) {
        this.showError('Invalid PG listing.');
        return;
      }

      try {
        const res = await fetch(this.ctx + '/api/public/pgs/' + this.pgId, { credentials: 'same-origin' });
        const data = await res.json().catch(function () { return {}; });
        if (!res.ok || data.success === false) {
          throw new Error(data.message || 'PG not found');
        }
        this.pg = data.data;
        if (content) {
          content.innerHTML = this.renderPage(this.pg);
          content.style.display = 'block';
        }
        document.title = (this.pg.pgName || 'PG') + ' | EstateVault';
      } catch (err) {
        this.showError(err.message || 'Could not load PG details');
      } finally {
        if (loader) loader.style.display = 'none';
      }
    },

    showError(msg) {
      const errEl = document.getElementById('pgBookError');
      const loader = document.getElementById('pgBookLoader');
      if (loader) loader.style.display = 'none';
      if (errEl) {
        errEl.style.display = 'block';
        errEl.innerHTML = '<p>' + escapeHtml(msg) + '</p>' +
          '<a href="' + escapeHtml(this.ctx) + '/user/pgs" class="btn-gold">Back to Explore PG</a>';
      }
    },

    renderPage(p) {
      const heroImg = this.primaryImage(p);
      const gallery = (p.images || []).slice(1).map(function (img) {
        return '<img src="' + escapeAttr(imageUrl(PgBook.ctx, img.imagePath)) + '" alt="" loading="lazy">';
      }).join('');

      const sections = [
        this.sectionOverview(p),
        this.sectionLocation(p),
        this.sectionBuilding(p),
        this.sectionPricing(p),
        this.sectionSharingPrices(p),
        this.sectionAmenities(p),
        this.sectionRules(p),
        this.sectionRooms(p)
      ].filter(Boolean).join('');

      const contactBlock = (p.ownerName || p.mobile)
        ? '<p class="property-book-agent">Managed by <strong>' + escapeHtml(p.ownerName || 'PG Owner') + '</strong>' +
          (p.mobile ? ' · <a href="tel:' + escapeAttr(p.mobile) + '">' + escapeHtml(p.mobile) + '</a>' : '') + '</p>'
        : '';

      return '<article class="property-book pg-book">' +
        '<header class="property-book-hero">' +
        '<div class="property-book-hero-media">' +
        '<img src="' + escapeAttr(heroImg) + '" alt="' + escapeAttr(p.pgName || '') + '">' +
        (gallery ? '<div class="property-book-gallery">' + gallery + '</div>' : '') +
        '</div>' +
        '<div class="property-book-hero-body">' +
        '<span class="property-tag rent">' + escapeHtml(formatPgType(p.pgType)) + '</span>' +
        '<span class="property-book-code">' + escapeHtml(p.pgCode || '') + '</span>' +
        '<h1>' + escapeHtml(p.pgName) + '</h1>' +
        '<p class="property-book-loc">&#128205; ' + escapeHtml(fullLocation(p)) + '</p>' +
        '<p class="property-book-price">' + formatPrice(p.monthlyRent) + '/mo</p>' +
        '<div class="property-book-tags">' +
        '<span class="property-book-tag">' + escapeHtml(formatGender(p.genderAllowed)) + '</span>' +
        '<span class="property-book-tag">' + (p.availableBeds || 0) + ' beds available</span>' +
        '</div>' +
        contactBlock +
        '<div class="property-book-actions">' +
        '<a href="' + escapeAttr(this.ctx + '/user/pgs/' + this.pgId + '/booking') + '" class="btn-book-now"><span class="btn-book-now-shine"></span>Book this PG</a>' +
        '<a href="tel:' + escapeAttr(p.mobile || '') + '" class="btn-gold">Contact Owner</a>' +
        '<a href="' + escapeAttr(this.ctx) + '/user/pgs" class="btn-outline">Explore more PGs</a>' +
        '</div></div></header>' +
        '<div class="property-book-sections">' + sections + '</div>' +
        '<section class="pg-book-bottom-cta">' +
        '<h2>Ready to move in?</h2>' +
        '<p>Select your room, beds, and complete secure payment.</p>' +
        '<a href="' + escapeAttr(this.ctx + '/user/pgs/' + this.pgId + '/booking') + '" class="btn-book-now"><span class="btn-book-now-shine"></span>Book this PG</a>' +
        '</section></article>';
    },

    primaryImage(p) {
      const first = (p.images && p.images.length) ? p.images[0].imagePath : null;
      return imageUrl(this.ctx, first) ||
        'https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?w=1200&q=80';
    },

    sectionOverview(p) {
      const rowsHtml = joinRows([
        row('PG type', formatPgType(p.pgType)),
        row('Gender allowed', formatGender(p.genderAllowed)),
        row('PG code', p.pgCode),
        row('Total rooms', p.totalRooms),
        row('Total capacity', p.totalCapacity),
        row('Available beds', p.availableBeds),
        row('Available from', formatDate(p.availableFrom)),
        row('Immediate move-in', formatBool(p.immediateAvailability))
      ]);
      const desc = p.description
        ? '<div class="property-book-description"><h3>Description</h3><p>' +
          escapeHtml(p.description).replace(/\n/g, '<br>') + '</p></div>'
        : '';
      return section('Overview', rowsHtml, desc);
    },

    sectionLocation(p) {
      const rowsHtml = joinRows([
        row('Address', p.address),
        row('Landmark', p.landmark),
        row('City', p.city),
        row('State', p.state),
        row('PIN code', p.pincode),
        row('Nearby places', p.nearbyPlaces)
      ]);
      if (!rowsHtml) return '';
      return section('Location', rowsHtml);
    },

    sectionBuilding(p) {
      const rowsHtml = joinRows([
        row('Total floors', p.totalFloors),
        row('Lift', formatBool(p.liftAvailable)),
        row('Parking', formatBool(p.parkingAvailable)),
        row('CCTV security', formatBool(p.cctvSecurity)),
        row('Biometric entry', formatBool(p.biometricEntry)),
        row('Fire safety', formatBool(p.fireSafety))
      ]);
      if (!rowsHtml) return '';
      return section('Building & security', rowsHtml);
    },

    sectionPricing(p) {
      const rowsHtml = joinRows([
        row('Monthly rent (from)', formatPrice(p.monthlyRent)),
        row('Security deposit', formatPrice(p.securityDeposit)),
        row('Maintenance charges', formatPrice(p.maintenanceCharges)),
        row('Booking amount', formatPrice(p.bookingAmount)),
        row('Electricity included', formatBool(p.electricityIncluded)),
        row('Water included', formatBool(p.waterIncluded))
      ]);
      if (!rowsHtml) return '';
      return section('Pricing', rowsHtml);
    },

    sectionSharingPrices(p) {
      const prices = p.sharingPrices || [];
      if (!prices.length) return '';
      const list = '<ul class="property-book-amenity-list">' + prices.map(function (sp) {
        return '<li>' + escapeHtml(formatSharingType(sp.sharingType)) + ' — ' + formatPrice(sp.monthlyRent) + '/mo</li>';
      }).join('') + '</ul>';
      return '<section class="property-book-section"><h2>Sharing &amp; rent</h2>' + list + '</section>';
    },

    sectionAmenities(p) {
      const items = p.amenities || [];
      if (!items.length) return '';
      const list = '<ul class="property-book-amenity-list">' + items.map(function (code) {
        return '<li>' + escapeHtml(formatAmenity(code)) + '</li>';
      }).join('') + '</ul>';
      return '<section class="property-book-section"><h2>Amenities</h2>' + list + '</section>';
    },

    sectionRules(p) {
      const r = p.rules;
      if (!r) return '';
      const rowsHtml = joinRows([
        row('No smoking', formatBool(r.noSmoking)),
        row('No alcohol', formatBool(r.noAlcohol)),
        row('Visitors allowed', formatBool(r.visitorsAllowed)),
        row('Pets allowed', formatBool(r.petsAllowed)),
        row('Curfew', r.curfewEnabled ? ('Yes' + (r.curfewTiming ? ' — ' + r.curfewTiming : '')) : 'No'),
        row('ID proof mandatory', formatBool(r.idProofMandatory)),
        row('Notice period', r.noticePeriodDays != null ? r.noticePeriodDays + ' days' : null),
        row('Security deposit', formatPrice(r.securityDepositAmount))
      ]);
      if (!rowsHtml) return '';
      return section('Rules & policies', rowsHtml);
    },

    sectionRooms(p) {
      const floors = p.floors || [];
      if (!floors.length) return '';
      let html = '<section class="property-book-section"><h2>Rooms &amp; floors</h2>';
      floors.forEach(function (floor) {
        html += '<h3 class="pg-floor-title">Floor ' + escapeHtml(String(floor.floorNumber)) + '</h3>';
        const rooms = floor.rooms || [];
        if (!rooms.length) return;
        html += '<dl class="property-book-grid">';
        rooms.forEach(function (room) {
          const label = 'Room ' + (room.roomNumber || '—');
          const value = [
            formatSharingType(room.sharingType),
            (room.availableBeds != null ? room.availableBeds + ' bed(s) free' : null),
            room.roomType ? String(room.roomType).replace(/_/g, ' ') : null
          ].filter(Boolean).join(' · ');
          if (value) {
            html += '<div class="property-book-row"><dt>' + escapeHtml(label) + '</dt><dd>' + escapeHtml(value) + '</dd></div>';
          }
        });
        html += '</dl>';
      });
      html += '</section>';
      return html;
    }
  };

  function section(title, rowsHtml, extra) {
    const html = rowsHtml || '';
    if (!html && !extra) return '';
    const grid = html ? '<dl class="property-book-grid">' + html + '</dl>' : '';
    return '<section class="property-book-section"><h2>' + escapeHtml(title) + '</h2>' +
      grid + (extra || '') + '</section>';
  }

  function joinRows(rows) {
    return (rows || []).filter(function (r) { return r; }).join('');
  }

  function row(label, value) {
    if (value == null || value === '') return '';
    return '<div class="property-book-row"><dt>' + escapeHtml(label) + '</dt><dd>' + escapeHtml(String(value)) + '</dd></div>';
  }

  function fullLocation(p) {
    return [p.landmark, p.address, p.city, p.state, p.pincode].filter(Boolean).join(', ');
  }

  function imageUrl(ctx, path) {
    if (!path) return '';
    return ctx + (path.charAt(0) === '/' ? path : '/' + path);
  }

  function formatPgType(type) {
    if (!type) return 'PG';
    const name = typeof type === 'string' ? type : type.name || String(type);
    return name.replace(/_/g, ' ').replace(/\bPG\b/i, 'PG').replace(/\b\w/g, function (c) { return c.toUpperCase(); });
  }

  function formatGender(g) {
    if (!g) return '—';
    const name = typeof g === 'string' ? g : g.name || String(g);
    if (name === 'MALE') return 'Male only';
    if (name === 'FEMALE') return 'Female only';
    if (name === 'BOTH') return 'Male & Female';
    return name;
  }

  function formatSharingType(type) {
    if (!type) return 'Sharing';
    const name = typeof type === 'string' ? type : type.name || String(type);
    return name.replace(/_/g, ' ').toLowerCase().replace(/\b\w/g, function (c) { return c.toUpperCase(); });
  }

  function formatAmenity(code) {
    if (!code) return '';
    return String(code).replace(/_/g, ' ').toLowerCase().replace(/\b\w/g, function (c) { return c.toUpperCase(); });
  }

  function formatBool(v) {
    if (v == null) return null;
    return v ? 'Yes' : 'No';
  }

  function formatPrice(amount) {
    const n = parseFloat(amount);
    if (isNaN(n)) return '\u20B9 —';
    if (n >= 100000) return '\u20B9 ' + (n / 100000).toFixed(2) + ' L';
    return '\u20B9 ' + n.toLocaleString('en-IN');
  }

  function formatDate(v) {
    if (v == null || v === '') return null;
    if (Array.isArray(v) && v.length >= 3) {
      return v[0] + '-' + String(v[1]).padStart(2, '0') + '-' + String(v[2]).padStart(2, '0');
    }
    return String(v);
  }

  function escapeHtml(s) {
    if (!s) return '';
    return String(s).replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
  }

  function escapeAttr(s) {
    return escapeHtml(s).replace(/"/g, '&quot;');
  }

  global.PgBook = PgBook;
})(typeof window !== 'undefined' ? window : this);
