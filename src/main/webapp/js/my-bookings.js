(function (global) {
  'use strict';

  const MyBookings = {
    ctx: '',
    activeTab: 'property',
    propertyBookings: [],
    pgBookings: [],

    init(ctx) {
      this.ctx = (ctx || '').replace(/\/$/, '');
      this.bindTabs();
      this.load();
    },

    bindTabs() {
      document.querySelectorAll('[data-bookings-tab]').forEach(function (btn) {
        btn.addEventListener('click', function () {
          MyBookings.switchTab(btn.getAttribute('data-bookings-tab'));
        });
      });
    },

    switchTab(tab) {
      this.activeTab = tab === 'pg' ? 'pg' : 'property';
      document.querySelectorAll('[data-bookings-tab]').forEach(function (btn) {
        const active = btn.getAttribute('data-bookings-tab') === MyBookings.activeTab;
        btn.classList.toggle('active', active);
        btn.setAttribute('aria-selected', active ? 'true' : 'false');
      });
      this.render();
    },

    async load() {
      const listEl = document.getElementById('bookingsList');
      const loader = document.getElementById('bookingsLoader');
      if (loader) loader.style.display = 'flex';
      try {
        const [propertyRes, pgRes] = await Promise.all([
          AuthAPI.request(this.ctx + '/api/user/bookings'),
          AuthAPI.request(this.ctx + '/api/user/pg-bookings')
        ]);
        this.propertyBookings = propertyRes.data || [];
        this.pgBookings = pgRes.data || [];
      } catch (err) {
        if (listEl) {
          listEl.innerHTML = '<div class="glass-card"><p>' + esc(err.message) + '</p></div>';
        }
      } finally {
        if (loader) loader.style.display = 'none';
        this.render();
      }
    },

    render() {
      const listEl = document.getElementById('bookingsList');
      if (!listEl) return;
      if (this.activeTab === 'pg') {
        listEl.innerHTML = this.renderPgList();
        this.bindPgActions(listEl);
        return;
      }
      const list = this.propertyBookings;
      if (!list.length) {
        listEl.innerHTML = '<div class="glass-card"><p>No property bookings yet. <a href="' + esc(this.ctx) +
          '/user/properties">Explore properties</a> and tap <strong>Book Property</strong>.</p></div>';
        return;
      }
      listEl.innerHTML = list.map(function (b) { return MyBookings.propertyCardHtml(b); }).join('');
      if (global.EstateRTC) {
        EstateRTC.bindCardActions(listEl);
      }
    },

    bindPgActions(root) {
      root.querySelectorAll('[data-pg-vacate]').forEach(function (btn) {
        btn.addEventListener('click', function () {
          MyBookings.requestVacate(btn.getAttribute('data-pg-vacate'));
        });
      });
    },

    async requestVacate(bookingId) {
      const days = 30;
      const reason = window.prompt(
        'To leave this PG you must give ' + days + ' days prior notice.\n\nOptional reason for leaving:'
      );
      if (reason === null) return;
      try {
        const res = await AuthAPI.request(this.ctx + '/api/user/pg-bookings/' + bookingId + '/request-vacate', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ reason: reason.trim() || null })
        });
        alert(res.message || (res.data && res.data.message) || 'Vacate notice submitted.');
        this.load();
      } catch (err) {
        alert(err.message || 'Could not submit vacate notice');
      }
    },

    renderPgList() {
      const list = this.pgBookings;
      if (!list.length) {
        return '<div class="glass-card"><p>No PG bookings yet. <a href="' + esc(this.ctx) +
          '/user/pgs">Explore PG</a> and book a room.</p></div>';
      }
      return list.map(function (b) { return MyBookings.pgCardHtml(b); }).join('');
    },

    propertyCardHtml(b) {
      const img = b.propertyImageUrl
        ? (this.ctx + (b.propertyImageUrl.charAt(0) === '/' ? b.propertyImageUrl : '/' + b.propertyImageUrl))
        : 'https://images.unsplash.com/photo-1600596542815-ffad4c1539a9?w=400&q=80';
      const statusClass = (b.status || '').toLowerCase().replace('_', '-');
      const paidBadge = b.paymentComplete
        ? '<span class="booking-paid-badge">&#10003; Fully paid</span>'
        : (num(b.remainingAmount) > 0 && b.status !== 'PENDING'
          ? '<span class="booking-pending-badge">Balance due</span>'
          : '<span class="booking-pending-badge">Payment pending</span>');
      let actions = '';
      if (b.canPayRemaining && num(b.remainingAmount) > 0) {
        actions += '<a href="' + esc(this.ctx) + '/user/booking/payment?bookingId=' + b.bookingId +
          '" class="btn-gold btn-sm">Pay remaining ' + formatInr(b.remainingAmount) + '</a>';
      } else if (b.paymentComplete) {
        actions += '<a href="' + esc(this.ctx) + '/user/booking/success?bookingId=' + b.bookingId +
          '" class="btn-gold btn-sm">View receipt</a>';
        if (b.invoiceDownloadUrl) {
          actions += '<a href="' + esc(this.ctx + b.invoiceDownloadUrl) + '" class="btn-outline btn-sm" download>Invoice</a>';
        }
      } else if (b.status === 'PENDING') {
        actions += '<a href="' + esc(this.ctx) + '/user/booking/payment?propertyId=' + b.propertyId +
          '" class="btn-gold btn-sm">Complete payment</a>';
      } else if (num(b.remainingAmount) > 0) {
        actions += '<a href="' + esc(this.ctx) + '/user/booking/payment?bookingId=' + b.bookingId +
          '" class="btn-gold btn-sm">Pay balance</a>';
      }
      return '<article class="glass-card my-booking-card my-booking-card--' + esc(statusClass) + '">' +
        '<div class="my-booking-card-inner">' +
        '<img src="' + escAttr(img) + '" alt="" class="my-booking-thumb">' +
        '<div class="my-booking-body">' +
        '<div class="my-booking-head"><h3>' + esc(b.propertyTitle || 'Property') + '</h3>' + paidBadge + '</div>' +
        '<p class="my-booking-meta">' + esc(b.bookingCode) + ' · ' + esc(b.propertyCode || '') + '</p>' +
        '<p class="my-booking-loc">' + esc(b.propertyLocation || '') + '</p>' +
        '<p class="my-booking-status"><strong>Status:</strong> ' + esc(b.statusLabel || b.status) + '</p>' +
        '<div class="my-booking-amounts">' +
        '<span>Total: <strong>' + formatInr(b.totalAmount) + '</strong></span>' +
        '<span>Paid: <strong>' + formatInr(b.paidAmount) + '</strong></span>' +
        (num(b.remainingAmount) > 0 ? '<span>Due: <strong>' + formatInr(b.remainingAmount) + '</strong></span>' : '') +
        '</div>' +
        (b.transactionId ? '<p class="my-booking-txn">Txn: ' + esc(b.transactionId) + '</p>' : '') +
        '<div class="my-booking-actions">' + actions + '</div>' +
        (global.EstateRTC ? EstateRTC.cardActionsHtml({
          bookingId: b.bookingId,
          peerId: b.agentId,
          peerName: b.agentName || 'Agent',
          propertyTitle: b.propertyTitle
        }) : '') +
        '</div></div></article>';
    },

    pgCardHtml(b) {
      const img = b.pgImageUrl
        ? (this.ctx + (b.pgImageUrl.charAt(0) === '/' ? b.pgImageUrl : '/' + b.pgImageUrl))
        : 'https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?w=400&q=80';
      const ownerBadge = ownerApprovalBadge(b);
      const paidBadge = b.paymentComplete
        ? '<span class="booking-paid-badge">&#10003; Fully paid</span>'
        : (num(b.remainingAmount) > 0 && b.ownerApproved
          ? '<span class="booking-pending-badge">Balance due</span>'
          : '');
      let actions = '';
      if (b.canPayRemaining && num(b.remainingAmount) > 0) {
        actions += '<a href="' + esc(this.ctx) + '/user/pg-booking/payment?bookingId=' + b.bookingId +
          '&balance=1" class="btn-gold btn-sm">Pay balance + deposit ' + formatInr(b.remainingAmount) + '</a>';
      } else if (b.canPayMonthlyRent && b.currentRentDueId) {
        actions += '<a href="' + esc(this.ctx) + '/user/pg-booking/payment?rentDueId=' + b.currentRentDueId +
          '" class="btn-gold btn-sm">Pay monthly rent ' + formatInr(b.currentRentDueAmount) + '</a>';
      } else if (b.paymentComplete) {
        actions += '<a href="' + esc(this.ctx) + '/user/pg-booking/success?bookingId=' + b.bookingId +
          '" class="btn-gold btn-sm">View confirmation</a>';
      } else if (b.paymentReceived) {
        actions += '<a href="' + esc(this.ctx) + '/user/pg-booking/success?bookingId=' + b.bookingId +
          '" class="btn-outline btn-sm">View details</a>';
      } else if (b.status === 'PENDING') {
        actions += '<a href="' + esc(this.ctx) + '/user/pg-booking/payment?bookingId=' + b.bookingId +
          '" class="btn-gold btn-sm">Complete payment</a>';
      }
      if (b.canRequestVacate) {
        actions += '<button type="button" class="btn-outline btn-sm" data-pg-vacate="' + b.bookingId +
          '">Request to leave (30-day notice)</button>';
      }
      const staySection = pgStaySectionHtml(b);
      return '<article class="glass-card my-booking-card my-booking-card--pg my-booking-card--' +
        esc((b.ownerApprovalStatus || 'pending').toLowerCase()) + '">' +
        '<div class="my-booking-card-inner">' +
        '<img src="' + escAttr(img) + '" alt="" class="my-booking-thumb">' +
        '<div class="my-booking-body">' +
        '<div class="my-booking-head"><h3>' + esc(b.pgName || 'PG') + '</h3>' + ownerBadge + paidBadge + '</div>' +
        '<p class="my-booking-meta">' + esc(b.bookingCode || '') + ' · ' + esc(b.pgCode || '') + '</p>' +
        '<p class="my-booking-loc">' + esc(b.pgLocation || '') + '</p>' +
        '<p class="my-booking-status"><strong>PG owner:</strong> ' + esc(b.ownerApprovalLabel || '—') + '</p>' +
        staySection +
        '<div class="my-booking-pg-details">' +
        '<span>Sharing: <strong>' + esc(b.sharingLabel || b.sharingType || '—') + '</strong></span>' +
        '<span>Beds: <strong>' + (b.bedCount || 0) + '</strong> (#' + esc(b.bedNumbers || '—') + ')</span>' +
        '<span>Room: <strong>' + esc(b.roomNumber || '—') + '</strong></span>' +
        (num(b.securityDepositAmount) > 0 ? '<span>Deposit: <strong>' + formatInr(b.securityDepositAmount) + '</strong></span>' : '') +
        '</div>' +
        '<div class="my-booking-amounts">' +
        '<span>Paid: <strong>' + formatInr(b.paidAmount) + '</strong></span>' +
        '<span>Total: <strong>' + formatInr(b.totalAmount) + '</strong></span>' +
        (num(b.remainingAmount) > 0 ? '<span>Due: <strong>' + formatInr(b.remainingAmount) + '</strong></span>' : '') +
        (b.activeStay && num(b.monthlyRentAmount) > 0 ? '<span>Monthly rent: <strong>' + formatInr(b.monthlyRentAmount) + '</strong></span>' : '') +
        '</div>' +
        (b.transactionId ? '<p class="my-booking-txn">Txn: ' + esc(b.transactionId) + '</p>' : '') +
        '<div class="my-booking-actions">' + actions + '</div>' +
        (b.canContactOwner && global.EstateRTC ? EstateRTC.cardActionsHtml({
          pgBookingId: b.bookingId,
          peerId: b.pgOwnerId,
          peerName: b.pgOwnerName || 'PG Owner',
          propertyTitle: b.pgName || 'PG'
        }) : '') +
        '</div></div></article>';
    }
  };

  function pgStaySectionHtml(b) {
    if (!b.moveInDate && !b.activeStay && b.stayStatus !== 'VACATED') {
      if (b.paymentComplete && b.ownerApproved) {
        return '<div class="pg-stay-info pg-stay-info--pending"><p>Your PG stay will begin once move-in is confirmed.</p></div>';
      }
      return '';
    }
    let html = '<div class="pg-stay-info">';
    html += '<p class="pg-stay-status"><strong>Stay:</strong> ' + esc(b.stayStatusLabel || b.stayStatus || '—') + '</p>';
    if (b.moveInDate) {
      html += '<p><strong>Joined:</strong> ' + formatDate(b.moveInDate) + '</p>';
    }
    if (b.canPayMonthlyRent && b.currentRentDuePeriod) {
      const overdue = b.currentRentDueStatus === 'OVERDUE';
      html += '<p class="' + (overdue ? 'pg-rent-overdue' : 'pg-rent-due') + '"><strong>Monthly rent due:</strong> ' +
        formatInr(b.currentRentDueAmount) + ' for ' + esc(b.currentRentDuePeriod) +
        ' (due ' + formatDate(b.currentRentDueDate) + ')' +
        (overdue ? ' — <strong>Overdue</strong>' : '') + '</p>';
    } else if (b.upcomingRentDueDate || b.nextRentDueDate) {
      html += '<p><strong>Next rent due:</strong> ' + formatDate(b.upcomingRentDueDate || b.nextRentDueDate) + '</p>';
    }
    if (b.stayStatus === 'NOTICE_PERIOD' && b.plannedVacateDate) {
      html += '<p class="pg-notice-info"><strong>Vacate by:</strong> ' + formatDate(b.plannedVacateDate) +
        ' (' + (b.noticePeriodDays || 30) + '-day notice submitted)</p>';
    }
    if (b.canRequestVacate) {
      html += '<p class="pg-notice-policy">To leave this PG, you must inform the owner at least ' +
        (b.noticePeriodDays || 30) + ' days in advance.</p>';
    }
    if (b.stayStatus === 'VACATED') {
      html += '<p class="pg-vacated-info">You have vacated this PG.</p>';
    }
    html += '</div>';
    return html;
  }

  function ownerApprovalBadge(b) {
    if (b.ownerApproved) {
      return '<span class="pg-owner-badge pg-owner-badge--approved">&#10003; Owner accepted</span>';
    }
    if (b.ownerRejected) {
      return '<span class="pg-owner-badge pg-owner-badge--rejected">&#10007; Owner rejected</span>';
    }
    if (b.awaitingOwnerApproval) {
      return '<span class="pg-owner-badge pg-owner-badge--pending">&#9203; Awaiting owner approval</span>';
    }
    if (!b.paymentReceived) {
      return '<span class="booking-pending-badge">Payment pending</span>';
    }
    return '<span class="pg-owner-badge pg-owner-badge--pending">Awaiting owner</span>';
  }

  function num(v) { return parseFloat(v) || 0; }

  function formatInr(amount) {
    const n = num(amount);
    return '₹ ' + n.toLocaleString('en-IN', { maximumFractionDigits: 0 });
  }

  function formatDate(d) {
    if (!d) return '—';
    try {
      return new Date(String(d) + 'T00:00:00').toLocaleDateString('en-IN', { day: 'numeric', month: 'short', year: 'numeric' });
    } catch (e) {
      return String(d);
    }
  }

  function esc(s) {
    if (!s) return '';
    return String(s).replace(/&/g, '&amp;').replace(/</g, '&lt;');
  }

  function escAttr(s) { return esc(s).replace(/"/g, '&quot;'); }

  global.MyBookings = MyBookings;
})(typeof window !== 'undefined' ? window : this);
