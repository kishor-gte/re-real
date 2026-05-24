/**
 * PG Owner — user PG bookings (approve / reject).
 */
(function (global) {
  'use strict';

  var D = 'div';

  const PgOwnerBookings = {
    ctx: '',

    init(ctx) {
      this.ctx = (ctx || '').replace(/\/$/, '');
      this.bindActions();
      this.load();
    },

    bindActions() {
      const list = document.getElementById('pgoBookingsList');
      if (!list || list.dataset.bound === 'true') return;
      list.dataset.bound = 'true';
      list.addEventListener('click', function (e) {
        const approveBtn = e.target.closest('[data-pgo-approve]');
        const rejectBtn = e.target.closest('[data-pgo-reject]');
        if (approveBtn) {
          e.preventDefault();
          PgOwnerBookings.approve(approveBtn.getAttribute('data-pgo-approve'));
        } else if (rejectBtn) {
          e.preventDefault();
          PgOwnerBookings.reject(rejectBtn.getAttribute('data-pgo-reject'));
        }
      });
    },

    async load() {
      const loader = document.getElementById('pgoBookingsLoader');
      const empty = document.getElementById('pgoBookingsEmpty');
      const list = document.getElementById('pgoBookingsList');
      if (loader) loader.style.display = 'flex';
      try {
        const res = await PgOwnerAPI.request(this.ctx + '/api/pg-owner/bookings');
        const items = res.data || [];
        if (!items.length) {
          if (list) list.innerHTML = '';
          if (empty) empty.style.display = 'block';
          return;
        }
        if (empty) empty.style.display = 'none';
        if (list) {
          list.innerHTML = items.map(function (b) { return PgOwnerBookings.cardHtml(b); }).join('');
          if (global.EstateRTC) {
            EstateRTC.bindCardActions(list);
          }
        }
      } catch (err) {
        if (list) {
          list.innerHTML = '<' + D + ' class="pgo-panel"><p style="color:#f87171;">' + esc(err.message || 'Could not load bookings') + '</p></' + D + '>';
        }
      } finally {
        if (loader) loader.style.display = 'none';
        if (global.PgOwnerSidebarBadge) PgOwnerSidebarBadge.refresh(this.ctx);
      }
    },

    cardHtml(b) {
      const approvalClass = approvalBadgeClass(b.ownerApprovalStatus);
      const occupants = (b.occupants || []).map(function (o) {
        return '<li>' + esc(o.fullName) + ' · Bed ' + (o.bedNumber || '—') + ' · ' + esc(o.mobile) + '</li>';
      }).join('') || '<li>—</li>';

      const actions = b.awaitingOwnerAction
        ? '<' + D + ' class="pgo-booking-actions">' +
          '<button type="button" class="btn btn-success btn-sm" data-pgo-approve="' + b.bookingId + '">Allow booking</button>' +
          '<button type="button" class="btn btn-outline-danger btn-sm" data-pgo-reject="' + b.bookingId + '">Reject</button>' +
          '</' + D + '>'
        : '';

      const details = '<' + D + ' class="pgo-booking-details">' +
        detailRow('Amount paid', formatInr(b.paidAmount)) +
        detailRow('Sharing type', esc(b.sharingLabel || b.sharingType || '—')) +
        detailRow('Beds requested', String(b.bedCount || 0) + ' bed(s)') +
        detailRow('Bed numbers', '#' + esc(b.bedNumbers || '—')) +
        detailRow('Rent per bed', formatInr(b.rentPerBed) + '/mo') +
        detailRow('Security deposit', formatInr(b.securityDepositAmount)) +
        detailRow('Total booking', formatInr(b.totalAmount)) +
        (num(b.remainingAmount) > 0 ? detailRow('Balance due', formatInr(b.remainingAmount)) : '') +
        detailRow('Payment status', b.paymentComplete ? 'Fully paid' : esc(b.bookingStatusLabel || '')) +
        '</' + D + '>';

      return '<article class="pgo-booking-card pgo-panel pgo-booking-card--' + esc((b.ownerApprovalStatus || 'pending').toLowerCase()) + '">' +
        '<' + D + ' class="pgo-booking-head">' +
        '<' + D + '><strong>' + esc(b.pgName || 'PG') + '</strong><span class="pgo-booking-code">' + esc(b.bookingCode || '') + '</span></' + D + '>' +
        '<span class="pgo-booking-badge ' + approvalClass + '">' + esc(b.ownerApprovalLabel || '') + '</span>' +
        '</' + D + '>' +
        '<p class="pgo-booking-meta">Room <strong>' + esc(b.roomNumber || '') + '</strong> · Guest: <strong>' + esc(b.userName || 'User') + '</strong> · ' + esc(b.userMobile || '') + '</p>' +
        details +
        '<' + D + ' class="pgo-booking-guests-wrap"><strong>Guest details</strong><ul class="pgo-booking-guests">' + occupants + '</ul></' + D + '>' +
        actions +
        (b.ownerApprovalStatus === 'APPROVED' && global.EstateRTC ? EstateRTC.cardActionsHtml({
          pgBookingId: b.bookingId,
          peerId: b.userId,
          peerName: b.userName || 'Guest',
          propertyTitle: b.pgName || 'PG'
        }) : '') +
        '</article>';
    },

    async approve(bookingId) {
      if (!confirm('Allow this user to stay in the booked room/beds?')) return;
      try {
        await PgOwnerAPI.request(this.ctx + '/api/pg-owner/bookings/' + bookingId + '/approve', { method: 'PATCH' });
        await this.load();
      } catch (err) {
        alert(err.message || 'Could not approve booking');
      }
    },

    async reject(bookingId) {
      const reason = prompt('Optional reason for rejection (shown internally):') || '';
      if (!confirm('Reject this booking? Beds will be released for other guests.')) return;
      try {
        await PgOwnerAPI.request(this.ctx + '/api/pg-owner/bookings/' + bookingId + '/reject', {
          method: 'PATCH',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ reason: reason })
        });
        await this.load();
      } catch (err) {
        alert(err.message || 'Could not reject booking');
      }
    }
  };

  const PgOwnerSidebarBadge = {
    async init(ctx) {
      this.ctx = (ctx || '').replace(/\/$/, '');
      await this.refresh(this.ctx);
    },

    async refresh(ctx) {
      const badge = document.getElementById('pgoPendingBadge');
      if (!badge) return;
      try {
        const res = await PgOwnerAPI.request((ctx || this.ctx) + '/api/pg-owner/bookings/pending-count');
        const count = (res.data && res.data.pendingCount) || 0;
        if (count > 0) {
          badge.textContent = String(count);
          badge.style.display = 'inline-flex';
        } else {
          badge.style.display = 'none';
        }
      } catch (e) {
        badge.style.display = 'none';
      }
    }
  };

  function detailRow(label, value) {
    return '<' + D + ' class="pgo-booking-detail-row"><span>' + label + '</span><strong>' + value + '</strong></' + D + '>';
  }

  function approvalBadgeClass(status) {
    if (status === 'APPROVED') return 'pgo-booking-badge--approved';
    if (status === 'REJECTED') return 'pgo-booking-badge--rejected';
    return 'pgo-booking-badge--pending';
  }

  function num(v) { return parseFloat(v) || 0; }

  function formatInr(amount) {
    var n = parseFloat(amount);
    if (isNaN(n)) return '₹ —';
    return '₹ ' + n.toLocaleString('en-IN', { maximumFractionDigits: 0 });
  }

  function esc(s) {
    return String(s || '').replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
  }

  global.PgOwnerBookings = PgOwnerBookings;
  global.PgOwnerSidebarBadge = PgOwnerSidebarBadge;
})(typeof window !== 'undefined' ? window : this);
