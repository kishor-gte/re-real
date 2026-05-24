/**
 * User PG browse — filter by city/state, group by location.
 */
(function (global) {
  'use strict';

  const PgsBrowse = {
    ctx: '',
    city: 'ALL',
    state: '',
    loggedIn: false,

    init(ctx, options) {
      this.ctx = (ctx || '').replace(/\/$/, '');
      options = options || {};
      this.loggedIn = !!options.loggedIn;
      if (options.defaultCity) {
        this.city = options.defaultCity;
      }
      if (options.defaultState) {
        this.state = options.defaultState;
      }
      this.bindFilters();
      this.loadCities().then(() => this.loadListings());
      const logout = document.getElementById('logoutLink');
      if (logout && typeof AuthAPI !== 'undefined') {
        logout.addEventListener('click', function (e) {
          e.preventDefault();
          AuthAPI.logout();
        });
      }
    },

    bindFilters() {
      const self = this;
      document.getElementById('pgApplyFilters')?.addEventListener('click', function () {
        const cityEl = document.getElementById('pgCityFilter');
        const stateEl = document.getElementById('pgStateFilter');
        self.city = cityEl ? cityEl.value : 'ALL';
        self.state = stateEl ? stateEl.value.trim() : '';
        self.loadListings();
      });

      document.getElementById('pgUseMyCity')?.addEventListener('click', function () {
        const city = this.getAttribute('data-city') || '';
        const cityEl = document.getElementById('pgCityFilter');
        if (cityEl && city) {
          cityEl.value = city;
          self.city = city;
          self.loadListings();
        }
      });

      document.getElementById('pgCityFilter')?.addEventListener('change', function () {
        self.city = this.value || 'ALL';
      });
    },

    async loadCities() {
      const select = document.getElementById('pgCityFilter');
      if (!select) return;
      try {
        const res = await fetch(this.ctx + '/api/public/pgs/cities', { credentials: 'same-origin' });
        const data = await res.json().catch(function () { return {}; });
        const cities = (data.data || []).filter(Boolean);
        cities.forEach(function (city) {
          const opt = document.createElement('option');
          opt.value = city;
          opt.textContent = city;
          select.appendChild(opt);
        });
        if (this.city && this.city !== 'ALL') {
          select.value = this.city;
          if (select.value !== this.city) {
            const opt = document.createElement('option');
            opt.value = this.city;
            opt.textContent = this.city;
            select.appendChild(opt);
            select.value = this.city;
          }
        }
      } catch (ignore) {
        // cities optional
      }
    },

    async loadListings() {
      const content = document.getElementById('pgExploreContent');
      const empty = document.getElementById('pgExploreEmpty');
      const loader = document.getElementById('pgExploreLoader');
      const countEl = document.getElementById('pgResultsCount');
      if (!content) return;

      let url = this.ctx + '/api/public/pgs?';
      if (this.city && this.city !== 'ALL') {
        url += 'city=' + encodeURIComponent(this.city) + '&';
      }
      if (this.state) {
        url += 'state=' + encodeURIComponent(this.state);
      }

      if (loader) loader.style.display = 'flex';
      content.innerHTML = '';
      if (empty) empty.style.display = 'none';

      try {
        const res = await fetch(url, { credentials: 'same-origin' });
        const data = await res.json().catch(function () { return {}; });
        if (!res.ok || data.success === false) {
          throw new Error(data.message || 'Could not load PG listings');
        }
        const list = data.data || [];
        if (countEl) {
          countEl.textContent = list.length + ' PG listing' + (list.length === 1 ? '' : 's') + ' found';
        }
        if (!list.length) {
          if (empty) empty.style.display = 'block';
          return;
        }
        content.innerHTML = this.renderGrouped(list);
      } catch (err) {
        if (countEl) countEl.textContent = '';
        if (empty) {
          empty.style.display = 'block';
          const p = empty.querySelector('p');
          if (p) p.textContent = err.message || 'Could not load PG listings';
        }
      } finally {
        if (loader) loader.style.display = 'none';
      }
    },

    renderGrouped(list) {
      const self = this;
      const grouped = new Map();
      list.forEach(function (p) {
        const key = (p.city || 'Unknown') + ', ' + (p.state || '');
        if (!grouped.has(key)) grouped.set(key, []);
        grouped.get(key).push(p);
      });

      let html = '';
      grouped.forEach(function (items, location) {
        html += '<section class="pg-location-section">' +
          '<h2 class="pg-location-heading">' + escapeHtml(location) + ' <span>(' + items.length + ')</span></h2>' +
          '<div class="properties-grid pg-grid">' +
          items.map(function (p) { return self.cardHtml(p); }).join('') +
          '</div></section>';
      });
      return html;
    },

    cardHtml(p) {
      const price = p.monthlyRent != null ? '\u20B9' + Number(p.monthlyRent).toLocaleString('en-IN') + '/mo' : 'Price on request';
      const img = p.coverImageUrl
        ? (this.ctx + (p.coverImageUrl.charAt(0) === '/' ? p.coverImageUrl : '/' + p.coverImageUrl))
        : 'https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?w=800&q=80';
      const loc = escapeHtml((p.landmark ? p.landmark + ', ' : '') + (p.city || ''));
      const specs = escapeHtml(p.specsSummary || p.pgTypeLabel || '');
      const gender = escapeHtml(p.genderLabel || '');
      const bookUrl = this.ctx + '/user/pgs/' + p.id + '/book';

      return '<article class="property-card explore-property-card pg-card">' +
        '<div class="property-img">' +
        '<img src="' + escapeAttr(img) + '" alt="' + escapeAttr(p.pgName || 'PG') + '" loading="lazy">' +
        '<span class="property-tag rent pg-tag">' + escapeHtml(p.pgTypeLabel || 'PG') + '</span>' +
        '</div>' +
        '<div class="property-body">' +
        '<h3>' + escapeHtml(p.pgName) + '</h3>' +
        '<p class="property-loc">&#128205; ' + loc + '</p>' +
        '<div class="property-meta">' +
        '<span class="property-price">' + price + '</span>' +
        '<span class="property-specs">' + specs + '</span></div>' +
        (gender ? '<p class="pg-gender-label">' + gender + '</p>' : '') +
        '<span class="pg-beds-badge">' + (p.availableBeds || 0) + ' beds free</span>' +
        '<span class="explore-listing-code">' + escapeHtml(p.pgCode || '') + '</span>' +
        '<div class="property-card-actions">' +
        '<a href="' + escapeAttr(bookUrl) + '" class="btn-book">Book</a>' +
        '</div></div></article>';
    }
  };

  function escapeHtml(s) {
    return String(s || '').replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
  }

  function escapeAttr(s) {
    return String(s || '').replace(/"/g, '&quot;');
  }

  global.PgsBrowse = PgsBrowse;
})(typeof window !== 'undefined' ? window : this);
