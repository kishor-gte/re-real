/**
 * Public / user property browse — filters, enquiry modal, my enquiries.
 */
(function (global) {
  'use strict';

  const PropertiesBrowse = {
    ctx: '',
    category: 'ALL',
    listingType: 'ALL',
    loggedIn: false,
    pendingProperty: null,

    init(ctx, options) {
      this.ctx = (ctx || '').replace(/\/$/, '');
      this.loggedIn = !!(options && options.loggedIn);
      this.bindFilters();
      this.bindEnquiryUi();
      this.loadListings();
      if (this.loggedIn) {
        this.loadMyEnquiries();
      }
      const logout = document.getElementById('logoutLink');
      if (logout && typeof AuthAPI !== 'undefined') {
        logout.addEventListener('click', function (e) {
          e.preventDefault();
          AuthAPI.logout();
        });
      }
    },

    bindEnquiryUi() {
      const self = this;
      const grid = document.getElementById('propertiesGrid');
      if (grid) {
        grid.addEventListener('click', function (e) {
          const btn = e.target.closest('[data-enquire-id]');
          if (!btn) return;
          e.preventDefault();
          self.openEnquiryModal({
            id: btn.getAttribute('data-enquire-id'),
            title: btn.getAttribute('data-enquire-title') || 'Property'
          });
        });
      }

      const modal = document.getElementById('enquiryModal');
      const closeBtn = document.getElementById('enquiryModalClose');
      const cancelBtn = document.getElementById('enquiryModalCancel');
      const form = document.getElementById('enquiryForm');

      function closeModal() {
        if (modal) modal.classList.remove('active');
        self.pendingProperty = null;
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
    },

    openEnquiryModal(property) {
      if (!this.loggedIn) {
        const returnUrl = encodeURIComponent(this.ctx + '/user/properties');
        window.location.href = this.ctx + '/user/login?redirect=' + returnUrl;
        return;
      }
      this.pendingProperty = property;
      const titleEl = document.getElementById('enquiryPropertyTitle');
      const idEl = document.getElementById('enquiryPropertyId');
      if (titleEl) titleEl.textContent = property.title;
      if (idEl) idEl.value = property.id;
      document.getElementById('enquiryModal')?.classList.add('active');
    },

    async submitEnquiry() {
      const messageEl = document.getElementById('enquiryMessage');
      const message = (messageEl && messageEl.value || '').trim();
      const propertyId = this.pendingProperty && this.pendingProperty.id;
      if (!propertyId) return;
      if (message.length < 10) {
        toast('Please enter at least 10 characters', 'error');
        return;
      }
      try {
        const data = await AuthAPI.request(this.ctx + '/api/user/enquiries', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ propertyId: Number(propertyId), message: message })
        });
        document.getElementById('enquiryModal')?.classList.remove('active');
        document.getElementById('enquiryForm')?.reset();
        this.pendingProperty = null;
        toast(data.message || 'Enquiry sent', 'success');
        this.loadMyEnquiries();
      } catch (err) {
        toast(err.message || 'Could not submit enquiry', 'error');
      }
    },

    async loadMyEnquiries() {
      const listEl = document.getElementById('myEnquiriesList');
      const section = document.getElementById('myEnquiriesSection');
      if (!listEl || !this.loggedIn) return;
      if (section) section.style.display = 'block';
      listEl.innerHTML = '<p class="enquiry-loading">Loading your enquiries…</p>';
      try {
        const data = await AuthAPI.request(this.ctx + '/api/user/enquiries');
        const list = data.data || [];
        if (!list.length) {
          listEl.innerHTML = '<p class="enquiry-empty">No enquiries yet. Use Enquire on a property to contact an agent.</p>';
          return;
        }
        listEl.innerHTML = list.map(function (e) { return PropertiesBrowse.enquiryCardHtml(e); }).join('');
      } catch (err) {
        listEl.innerHTML = '<p class="enquiry-empty">' + escapeHtml(err.message || 'Could not load enquiries') + '</p>';
      }
    },

    enquiryCardHtml(e) {
      const status = e.status || '';
      const statusClass = status.toLowerCase();
      let replyBlock = '';
      if (status === 'REPLIED' && e.agentReply) {
        replyBlock = '<div class="enquiry-reply"><strong>Agent reply</strong><p>' + escapeHtml(e.agentReply) + '</p></div>';
      } else if (status === 'ACCEPTED') {
        replyBlock = '<p class="enquiry-hint">The agent accepted your enquiry and will reply soon.</p>';
      } else if (status === 'PENDING') {
        replyBlock = '<p class="enquiry-hint">Waiting for the agent to accept your enquiry.</p>';
      } else if (status === 'DECLINED') {
        replyBlock = '<p class="enquiry-hint enquiry-hint--muted">This enquiry was declined by the agent.</p>';
      }
      return '<article class="enquiry-card enquiry-card--' + statusClass + '">' +
        '<div class="enquiry-card-head">' +
        '<h4>' + escapeHtml(e.propertyTitle || 'Property') + '</h4>' +
        '<span class="enquiry-status enquiry-status--' + statusClass + '">' + escapeHtml(e.statusLabel || status) + '</span></div>' +
        '<p class="enquiry-meta">' + escapeHtml(e.propertyCode || '') +
        (e.agentName ? ' · Agent: ' + escapeHtml(e.agentName) : '') + '</p>' +
        '<p class="enquiry-user-msg"><strong>Your message:</strong> ' + escapeHtml(e.message) + '</p>' +
        replyBlock +
        '</article>';
    },

    bindFilters() {
      const self = this;
      document.querySelectorAll('#listingTypeFilters .explore-toggle').forEach(function (btn) {
        btn.addEventListener('click', function () {
          document.querySelectorAll('#listingTypeFilters .explore-toggle').forEach(function (b) {
            b.classList.remove('active');
            b.setAttribute('aria-pressed', 'false');
          });
          btn.classList.add('active');
          btn.setAttribute('aria-pressed', 'true');
          self.listingType = btn.getAttribute('data-listing') || 'ALL';
          self.loadListings();
        });
      });

      document.querySelectorAll('#categoryFilters .explore-chip').forEach(function (btn) {
        btn.addEventListener('click', function () {
          document.querySelectorAll('#categoryFilters .explore-chip').forEach(function (b) {
            b.classList.remove('active');
            b.setAttribute('aria-pressed', 'false');
          });
          btn.classList.add('active');
          btn.setAttribute('aria-pressed', 'true');
          self.category = btn.getAttribute('data-category') || 'ALL';
          self.loadListings();
        });
      });
    },

    async loadListings() {
      const grid = document.getElementById('propertiesGrid');
      const empty = document.getElementById('exploreEmpty');
      const loader = document.getElementById('exploreLoader');
      const countEl = document.getElementById('resultsCount');

      if (!grid) return;

      let url = this.ctx + '/api/public/properties?';
      if (this.category && this.category !== 'ALL') {
        url += 'category=' + encodeURIComponent(this.category) + '&';
      }
      if (this.listingType && this.listingType !== 'ALL') {
        url += 'listingType=' + encodeURIComponent(this.listingType);
      }

      if (loader) loader.style.display = 'flex';
      grid.innerHTML = '';
      if (empty) empty.style.display = 'none';

      try {
        const res = await fetch(url, { credentials: 'same-origin' });
        const data = await res.json().catch(function () { return {}; });
        if (!res.ok || data.success === false) {
          throw new Error(data.message || 'Could not load properties');
        }
        const list = data.data || [];
        if (countEl) {
          countEl.textContent = list.length + ' propert' + (list.length === 1 ? 'y' : 'ies') + ' found';
        }
        if (!list.length) {
          if (empty) empty.style.display = 'block';
          return;
        }
        grid.innerHTML = list.map(function (p) { return PropertiesBrowse.cardHtml(p); }).join('');
      } catch (err) {
        if (countEl) countEl.textContent = '';
        if (empty) {
          empty.style.display = 'block';
          empty.querySelector('p').textContent = err.message || 'Could not load listings';
        }
      } finally {
        if (loader) loader.style.display = 'none';
      }
    },

    cardHtml(p) {
      const listing = listingTypeName(p.listingType);
      const tag = listing === 'RENT' ? 'rent' : 'sale';
      const tagLabel = listing === 'RENT' ? 'For Rent' : 'For Sale';
      const price = formatPrice(p.price, listing);
      const img = p.primaryImageUrl
        ? (this.ctx + (p.primaryImageUrl.charAt(0) === '/' ? p.primaryImageUrl : '/' + p.primaryImageUrl))
        : 'https://images.unsplash.com/photo-1600596542815-ffad4c1539a9?w=800&q=80';
      const loc = escapeHtml((p.locality || '') + ', ' + (p.city || ''));
      const specs = escapeHtml(p.specsSummary || p.propertySubType || '');
      const cat = escapeHtml(p.categoryLabel || '');

      return '<article class="property-card explore-property-card">' +
        '<div class="property-img">' +
        '<img src="' + escapeAttr(img) + '" alt="' + escapeAttr(p.title || '') + '" loading="lazy">' +
        '<span class="property-tag ' + tag + '">' + tagLabel + '</span>' +
        '<span class="explore-cat-badge">' + cat + '</span></div>' +
        '<div class="property-body">' +
        '<h3>' + escapeHtml(p.title) + '</h3>' +
        '<p class="property-loc">&#128205; ' + loc + '</p>' +
        '<div class="property-meta">' +
        '<span class="property-price">' + price + '</span>' +
        '<span class="property-specs">' + specs + '</span></div>' +
        (p.priceNegotiable ? '<span class="explore-negotiable">Negotiable</span>' : '') +
        '<span class="explore-listing-code">' + escapeHtml(p.propertyCode || '') + '</span>' +
        '<div class="property-card-actions">' +
        '<a href="' + escapeAttr(this.ctx + '/user/properties/' + p.id + '/book') + '" class="btn-book">Book</a>' +
        '<button type="button" class="btn-enquire" data-enquire-id="' + escapeAttr(String(p.id)) + '" data-enquire-title="' + escapeAttr(p.title || '') + '">Enquire</button>' +
        '</div></div></article>';
    }
  };

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

  global.PropertiesBrowse = PropertiesBrowse;
})(typeof window !== 'undefined' ? window : this);
