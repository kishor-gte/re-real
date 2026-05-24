/**
 * Agent — user property bookings (view, print, grid cards).
 */
(function (global) {
  'use strict';

  const AgentBookings = {
    ctx: '',
    bookingsById: {},
    currentViewId: null,

    init(ctx) {
      this.ctx = (ctx || '').replace(/\/$/, '');
      this.bindModal();
      this.bindListActions();
      this.load();
    },

    bindModal() {
      const modal = document.getElementById('bkViewModal');
      if (!modal) return;
      modal.querySelectorAll('[data-bk-close]').forEach(function (el) {
        el.addEventListener('click', function () { AgentBookings.closeModal(); });
      });
      document.addEventListener('keydown', function (e) {
        if (e.key === 'Escape') AgentBookings.closeModal();
      });
      document.getElementById('bkModalPrintBtn')?.addEventListener('click', function () {
        if (AgentBookings.currentViewId != null) {
          AgentBookings.printBooking(AgentBookings.currentViewId);
        }
      });
    },

    bindListActions() {
      const listEl = document.getElementById('agentBookingsList');
      if (!listEl || listEl.dataset.actionsBound === 'true') return;
      listEl.dataset.actionsBound = 'true';
      listEl.addEventListener('click', function (e) {
        const viewBtn = e.target.closest('[data-bk-view]');
        const printBtn = e.target.closest('[data-bk-print]');
        if (viewBtn) {
          e.preventDefault();
          AgentBookings.viewBooking(viewBtn.getAttribute('data-bk-view'));
        } else if (printBtn) {
          e.preventDefault();
          AgentBookings.printBooking(printBtn.getAttribute('data-bk-print'));
        }
      });
    },

    async load() {
      const listEl = document.getElementById('agentBookingsList');
      const empty = document.getElementById('agentBookingsEmpty');
      if (!listEl) return;
      UI.showLoader();
      try {
        const data = await AgentAPI.request(this.ctx + '/api/agent/bookings');
        const list = sortByDateDesc(data.data || []);
        this.bookingsById = {};
        list.forEach(function (b) {
          if (b.bookingId != null) AgentBookings.bookingsById[String(b.bookingId)] = b;
        });
        this.updateKpis(list);
        if (!list.length) {
          listEl.innerHTML = '';
          if (empty) empty.style.display = 'block';
          return;
        }
        if (empty) empty.style.display = 'none';
        listEl.innerHTML = list.map(function (b) { return AgentBookings.cardHtml(b); }).join('');
        if (global.EstateRTC) {
          EstateRTC.bindCardActions(listEl);
        }
      } catch (err) {
        this.bookingsById = {};
        this.updateKpis([]);
        UI.toast(err.message || 'Could not load bookings', 'error');
      } finally {
        UI.hideLoader();
      }
    },

    async viewBooking(bookingId) {
      UI.showLoader();
      let b = this.bookingsById[String(bookingId)];
      try {
        if (!b) {
          const res = await AgentAPI.request(this.ctx + '/api/agent/bookings/' + bookingId);
          b = res.data;
          if (b && b.bookingId != null) this.bookingsById[String(b.bookingId)] = b;
        }
        if (!b) {
          UI.toast('Booking not found', 'error');
          return;
        }
        this.currentViewId = String(b.bookingId);
        this.openModal(b);
      } catch (err) {
        UI.toast(err.message || 'Could not load booking', 'error');
      } finally {
        UI.hideLoader();
      }
    },

    openModal(b) {
      const modal = document.getElementById('bkViewModal');
      const body = document.getElementById('bkModalBody');
      const title = document.getElementById('bkModalTitle');
      if (!modal || !body) return;
      if (title) {
        title.textContent = b.bookingCode || 'Booking details';
      }
      body.innerHTML = this.detailHtml(b, true);
      modal.hidden = false;
      modal.classList.add('is-open');
      document.body.style.overflow = 'hidden';
    },

    closeModal() {
      const modal = document.getElementById('bkViewModal');
      if (!modal) return;
      modal.hidden = true;
      modal.classList.remove('is-open');
      document.body.style.overflow = '';
      this.currentViewId = null;
    },

    printBooking(bookingId) {
      const b = this.bookingsById[String(bookingId)];
      if (!b) {
        UI.toast('Booking not found', 'error');
        return;
      }
      const html = this.printDocumentHtml(b);
      const win = window.open('', '_blank', 'width=900,height=700');
      if (!win) {
        UI.toast('Allow pop-ups to print', 'error');
        return;
      }
      win.document.open();
      win.document.write(html);
      win.document.close();
      win.focus();
      win.onload = function () {
        win.print();
      };
      setTimeout(function () {
        try { win.print(); } catch (e) { /* ignore */ }
      }, 400);
    },

    printDocumentHtml(b) {
      const printedAt = new Date().toLocaleString('en-IN');
      const emiRows = (b.emiMonths && num(b.emiMonths) > 0)
        ? printRow('EMI tenure', b.emiMonths + ' months') + printRow('Monthly EMI', formatInr(b.monthlyEmi))
        : '';
      return '<!DOCTYPE html><html><head><meta charset="UTF-8"><title>Booking ' + esc(b.bookingCode) + '</title>' +
        '<style>' +
        'body{font-family:Segoe UI,Arial,sans-serif;margin:24px;color:#111;line-height:1.45;}' +
        'h1{font-size:1.35rem;margin:0 0 4px;color:#0f172a;}' +
        '.sub{color:#64748b;font-size:0.9rem;margin-bottom:20px;}' +
        'section{border:1px solid #e2e8f0;border-radius:8px;padding:12px 14px;margin-bottom:14px;}' +
        'section h2{font-size:0.75rem;text-transform:uppercase;letter-spacing:0.06em;color:#64748b;margin:0 0 10px;}' +
        '.row{display:flex;justify-content:space-between;gap:12px;padding:5px 0;border-bottom:1px solid #f1f5f9;font-size:0.9rem;}' +
        '.row:last-child{border-bottom:none;} .row.total{border-top:2px solid #c9a227;margin-top:6px;padding-top:8px;font-weight:700;}' +
        '.status{display:inline-block;padding:4px 10px;border-radius:4px;font-size:0.8rem;font-weight:600;background:#f1f5f9;}' +
        '@media print{body{margin:12px;}}' +
        '</style></head><body>' +
        '<h1>EstateVault — Property Booking</h1>' +
        '<p class="sub">Booking ID: <strong>' + esc(b.bookingCode) + '</strong> · Printed: ' + esc(printedAt) + '</p>' +
        '<p><span class="status">' + esc(b.statusLabel || b.status) + '</span></p>' +
        '<section><h2>Property</h2>' +
        printRow('Title', b.propertyTitle) + printRow('Code', b.propertyCode) + printRow('Location', b.propertyLocation) +
        '</section>' +
        '<section><h2>Buyer</h2>' +
        printRow('Name', b.userName) + printRow('Email', b.userEmail) + printRow('Mobile', b.userMobile) +
        '</section>' +
        '<section><h2>Payment</h2>' +
        printRow('Plan', b.paymentPlanLabel) + printRow('Method', b.paymentMethodLabel) +
        printRow('Status', b.paymentReceived ? 'Received' : 'Pending') +
        printRow('Transaction ID', b.transactionId) + printRow('Booked on', formatDate(b.bookingDate)) +
        emiRows +
        '</section>' +
        '<section><h2>Amounts</h2>' +
        printRow('Base price', formatInr(b.basePrice)) + printRow('GST', formatInr(b.gstAmount)) +
        printRow('Registration', formatInr(b.registrationCharges)) + printRow('Booking charges', formatInr(b.bookingCharges)) +
        (num(b.discountAmount) > 0 ? printRow('Discount', '− ' + formatInr(b.discountAmount)) : '') +
        printRow('Total', formatInr(b.totalAmount), true) + printRow('Checkout paid', formatInr(b.payableNow)) +
        printRow('Paid so far', formatInr(b.paidAmount)) + printRow('Balance due', formatInr(b.remainingAmount)) +
        '</section>' +
        '<p class="sub" style="margin-top:24px;">Agent copy · EstateVault Real Estate Platform</p>' +
        '</body></html>';
    },

    detailHtml(b, forModal) {
      const ctx = this.ctx;
      const imgUrl = b.propertyImageUrl
        ? (ctx + (String(b.propertyImageUrl).charAt(0) === '/' ? b.propertyImageUrl : '/' + b.propertyImageUrl))
        : '';
      const imgBlock = imgUrl && forModal
        ? '<div class="bk-detail__img"><img src="' + escAttr(imgUrl) + '" alt=""></div>'
        : '';

      const emiBlock = (b.emiMonths && num(b.emiMonths) > 0)
        ? detailRow('EMI tenure', b.emiMonths + ' months') +
          detailRow('Monthly EMI', formatInr(b.monthlyEmi))
        : '';

      return imgBlock +
        '<div class="bk-detail__grid">' +
        '<section class="bk-detail__section"><h3>Property</h3>' +
        detailRow('Title', b.propertyTitle) +
        detailRow('Code', b.propertyCode) +
        detailRow('Location', b.propertyLocation) +
        '</section>' +
        '<section class="bk-detail__section"><h3>Buyer</h3>' +
        detailRow('Name', b.userName) +
        detailRow('Email', b.userEmail) +
        detailRow('Mobile', b.userMobile) +
        '</section>' +
        '</div>' +
        '<section class="bk-detail__section"><h3>Payment</h3>' +
        detailRow('Plan', b.paymentPlanLabel) +
        detailRow('Method', b.paymentMethodLabel) +
        detailRow('Payment status', b.paymentReceived ? 'Received' : 'Pending') +
        detailRow('Transaction ID', b.transactionId || '—') +
        detailRow('Booked on', formatDate(b.bookingDate)) +
        emiBlock +
        '</section>' +
        '<section class="bk-detail__section"><h3>Amounts</h3>' +
        detailRow('Base price', formatInr(b.basePrice)) +
        detailRow('GST', formatInr(b.gstAmount)) +
        detailRow('Registration', formatInr(b.registrationCharges)) +
        detailRow('Booking charges', formatInr(b.bookingCharges)) +
        (num(b.discountAmount) > 0 ? detailRow('Discount', '− ' + formatInr(b.discountAmount)) : '') +
        detailRow('Total', formatInr(b.totalAmount), true) +
        detailRow('Checkout paid', formatInr(b.payableNow)) +
        detailRow('Paid so far', formatInr(b.paidAmount)) +
        detailRow('Balance due', formatInr(b.remainingAmount)) +
        '</section>';
    },

    cardHtml(b) {
      const ctx = this.ctx;
      const id = b.bookingId;
      const statusKey = (b.status || 'pending').toLowerCase().replace(/_/g, '-');
      const imgUrl = b.propertyImageUrl
        ? (ctx + (String(b.propertyImageUrl).charAt(0) === '/' ? b.propertyImageUrl : '/' + b.propertyImageUrl))
        : '';
      const thumb = imgUrl
        ? '<div class="bk-card__thumb-wrap"><img src="' + escAttr(imgUrl) + '" alt="" class="bk-card__thumb" width="36" height="36" loading="lazy"></div>'
        : '<div class="bk-card__thumb-wrap"><span class="bk-card__thumb-icon" aria-hidden="true">&#127968;</span></div>';

      const breakdown = breakdownHtml(b);

      return '<article class="bk-card bk-card--' + esc(statusKey) + '">' +
        '<header class="bk-card__header">' + thumb +
        '<div class="bk-card__head-text">' +
        '<div class="bk-card__head-row">' +
        '<h3 class="bk-card__title">' + esc(b.propertyTitle || 'Property') + '</h3>' +
        '<span class="bk-pill bk-pill--' + esc(statusKey) + '">' + esc(b.statusLabel || b.status) + '</span>' +
        '</div>' +
        '<p class="bk-card__code">' + esc(b.bookingCode) + (b.propertyCode ? ' · ' + esc(b.propertyCode) : '') + '</p>' +
        '<p class="bk-card__loc">' + esc(b.propertyLocation || '—') + '</p>' +
        '</div></header>' +

        '<section class="bk-section bk-section--buyer">' +
        '<p class="bk-section__label">Buyer</p>' +
        '<p class="bk-buyer-name">' + esc(b.userName || '—') + '</p>' +
        '<p class="bk-buyer-meta">' + esc(b.userEmail || '') +
        (b.userMobile ? '<br>' + esc(b.userMobile) : '') + '</p>' +
        '</section>' +

        '<section class="bk-section">' +
        '<p class="bk-section__label">Payment</p>' +
        '<div class="bk-tags">' +
        '<span class="bk-tag">' + esc(b.paymentPlanLabel || '—') + '</span>' +
        '<span class="bk-tag">' + esc(b.paymentMethodLabel || '—') + '</span>' +
        (b.paymentReceived
          ? '<span class="bk-tag bk-tag--ok">Received</span>'
          : '<span class="bk-tag bk-tag--warn">Pending</span>') +
        '</div></section>' +

        '<div class="bk-metrics">' +
        metricBox('Total', b.totalAmount, '') +
        metricBox('Paid', b.paidAmount, 'bk-metric--paid') +
        metricBox(num(b.remainingAmount) > 0 ? 'Due' : 'Balance', b.remainingAmount, 'bk-metric--due') +
        '</div>' +

        '<footer class="bk-footer">' +
        '<span>Booked: <time>' + formatDate(b.bookingDate) + '</time></span>' +
        (b.transactionId ? '<span>Txn: <code title="' + escAttr(b.transactionId) + '">' +
          esc(shortTxn(b.transactionId)) + '</code></span>' : '') +
        '</footer>' +

        '<div class="bk-card__actions">' +
        '<button type="button" class="bk-btn bk-btn--view" data-bk-view="' + escAttr(String(id)) + '">View details</button>' +
        '<button type="button" class="bk-btn bk-btn--print" data-bk-print="' + escAttr(String(id)) + '">Print</button>' +
        '</div>' +
        (global.EstateRTC ? EstateRTC.cardActionsHtml({
          bookingId: id,
          peerId: b.userId,
          peerName: b.userName,
          propertyTitle: b.propertyTitle
        }) : '') +

        '<details class="bk-details"><summary>Price breakdown</summary>' +
        '<div class="bk-details__body">' + breakdown + '</div></details>' +
        '</article>';
    },

    updateKpis(list) {
      let pending = 0;
      let collected = 0;
      let due = 0;
      list.forEach(function (b) {
        const status = (b.status || '').toUpperCase();
        if (status === 'PENDING') pending += 1;
        if (status !== 'PENDING' && status !== 'CANCELLED') {
          collected += num(b.paidAmount);
          due += num(b.remainingAmount);
        }
      });
      const total = list.length;
      setText('abKpiTotal', String(total));
      setText('abKpiPending', String(pending));
      setText('abKpiDone', String(total - pending));
      setText('abKpiCollected', formatInr(collected));
      setText('abKpiDue', formatInr(due));
      const countEl = document.getElementById('abBookingCount');
      if (countEl) countEl.textContent = String(total);
    }
  };

  function detailRow(label, value, isTotal) {
    const v = value != null && value !== '' ? value : '—';
    return '<div class="bk-detail-row' + (isTotal ? ' bk-detail-row--total' : '') + '">' +
      '<span class="bk-detail-row__label">' + esc(label) + '</span>' +
      '<span class="bk-detail-row__val">' + esc(String(v)) + '</span></div>';
  }

  function printRow(label, value, isTotal) {
    const v = value != null && value !== '' ? value : '—';
    return '<div class="row' + (isTotal ? ' total' : '') + '"><span>' + esc(label) +
      '</span><span>' + esc(String(v)) + '</span></div>';
  }

  function breakdownHtml(b) {
    let html = '';
    html += breakdownRow('Base price', b.basePrice);
    html += breakdownRow('GST', b.gstAmount);
    html += breakdownRow('Registration', b.registrationCharges);
    html += breakdownRow('Booking charges', b.bookingCharges);
    if (num(b.discountAmount) > 0) html += breakdownRow('Discount', b.discountAmount, true);
    html += breakdownRow('Total', b.totalAmount, false, true);
    html += breakdownRow('Checkout paid', b.payableNow);
    return html;
  }

  function breakdownRow(label, amount, isDiscount, isTotal) {
    const n = num(amount);
    if (!isTotal && n === 0 && !isDiscount) return '';
    const cls = isTotal ? ' bk-breakdown-row--total' : (isDiscount ? ' bk-breakdown-row--disc' : '');
    const prefix = isDiscount ? '− ' : '';
    return '<div class="bk-breakdown-row' + cls + '"><dt>' + esc(label) + '</dt><dd>' +
      prefix + formatInr(amount) + '</dd></div>';
  }

  function metricBox(label, amount, extra) {
    return '<div class="bk-metric ' + (extra || '') + '">' +
      '<span class="bk-metric__label">' + esc(label) + '</span>' +
      '<strong class="bk-metric__val">' + formatInr(amount) + '</strong></div>';
  }

  function sortByDateDesc(list) {
    return list.slice().sort(function (a, b) {
      return dateMs(b.bookingDate) - dateMs(a.bookingDate);
    });
  }

  function dateMs(iso) {
    if (!iso) return 0;
    const t = new Date(iso).getTime();
    return isNaN(t) ? 0 : t;
  }

  function setText(id, text) {
    const el = document.getElementById(id);
    if (el) el.textContent = text;
  }

  function shortTxn(id) {
    const s = String(id || '');
    if (s.length <= 20) return s;
    return s.slice(0, 8) + '…' + s.slice(-6);
  }

  function num(v) { return parseFloat(v) || 0; }

  function formatInr(amount) {
    const n = num(amount);
    if (n >= 10000000) return '₹' + (n / 10000000).toFixed(2) + ' Cr';
    if (n >= 100000) return '₹' + (n / 100000).toFixed(2) + ' L';
    return '₹' + n.toLocaleString('en-IN', { maximumFractionDigits: 0 });
  }

  function formatDate(iso) {
    if (!iso) return '—';
    try {
      const d = new Date(iso);
      if (isNaN(d.getTime())) return '—';
      return d.toLocaleString('en-IN', {
        day: '2-digit', month: 'short', year: 'numeric',
        hour: '2-digit', minute: '2-digit'
      });
    } catch (e) {
      return '—';
    }
  }

  function esc(s) {
    if (!s) return '';
    return String(s).replace(/&/g, '&amp;').replace(/</g, '&lt;');
  }

  function escAttr(s) { return esc(s).replace(/"/g, '&quot;'); }

  global.AgentBookings = AgentBookings;
})(typeof window !== 'undefined' ? window : this);
