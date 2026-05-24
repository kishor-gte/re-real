/**
 * PG booking payment — Razorpay checkout.
 */
(function (global) {
  'use strict';

  const PgBookingPayment = {
    ctx: '',
    bookingId: null,
    rentDueId: null,
    balancePaymentMode: false,
    rentPaymentMode: false,
    preview: null,
    timerInterval: null,

    init(ctx, options) {
      this.ctx = (ctx || '').replace(/\/$/, '');
      this.bookingId = options && options.bookingId;
      this.rentDueId = options && options.rentDueId;
      this.balancePaymentMode = !!(options && options.balancePaymentMode);
      this.rentPaymentMode = !!(options && options.rentPaymentMode);
      document.getElementById('pgProceedPayBtn')?.addEventListener('click', () => this.proceedPayment());
      if (this.rentPaymentMode && this.rentDueId) {
        this.loadRentPreview();
        return;
      }
      if (!this.bookingId) {
        this.showError('Invalid booking.');
        return;
      }
      this.loadPreview();
    },

    async loadRentPreview() {
      const loader = document.getElementById('pgPayLoader');
      const checkout = document.getElementById('pgPayCheckout');
      if (loader) loader.style.display = 'flex';
      try {
        const data = await AuthAPI.request(this.ctx + '/api/user/pg-bookings/rent-dues/' + this.rentDueId + '/checkout-preview');
        this.preview = data.data;
        this.renderRentSummary();
        this.renderRentPrice();
        this.showRazorpayBanner(this.preview.razorpayConfigured);
        if (checkout) checkout.style.display = 'grid';
        const timer = document.getElementById('reservationTimer');
        if (timer) timer.style.display = 'none';
        const header = document.querySelector('.booking-payment-header h1');
        if (header) header.innerHTML = 'PG <span>Monthly Rent</span> Payment';
        const sub = document.querySelector('.booking-sub');
        if (sub) sub.textContent = 'Pay monthly rent to continue your PG stay';
        const occ = document.getElementById('pgOccupantsSection');
        if (occ) occ.style.display = 'none';
      } catch (err) {
        this.showError(err.message || 'Could not load rent due');
      } finally {
        if (loader) loader.style.display = 'none';
      }
    },

    renderRentSummary() {
      const p = this.preview;
      document.getElementById('pgSummarySection').innerHTML =
        '<h2>' + esc(p.pgName || 'PG') + '</h2>' +
        '<p class="prop-meta">' + esc(p.bookingCode || '') + ' · Room ' + esc(p.roomNumber || '') + '</p>' +
        '<p class="prop-loc">' + esc(p.pgLocation || '') + '</p>' +
        '<p><strong>Rent period:</strong> ' + esc(p.periodLabel || '') + '</p>' +
        '<p><strong>Due date:</strong> ' + esc(formatDate(p.dueDate)) + '</p>';
    },

    renderRentPrice() {
      const p = this.preview;
      let html = '<h3>Monthly rent</h3><table class="price-table">';
      html += row('Rent period', p.periodLabel);
      html += row('Due date', formatDate(p.dueDate));
      html += row('Monthly rent', p.monthlyRentAmount);
      html += '<tr class="price-total-row"><td><strong>Pay now</strong></td><td><strong>' + formatInr(p.amount) + '</strong></td></tr>';
      html += '</table>';
      document.getElementById('pgPriceSection').innerHTML = html;
    },

    async loadPreview() {
      const loader = document.getElementById('pgPayLoader');
      const checkout = document.getElementById('pgPayCheckout');
      const errEl = document.getElementById('pgPayError');
      if (loader) loader.style.display = 'flex';
      try {
        const previewUrl = this.balancePaymentMode
          ? this.ctx + '/api/user/pg-bookings/' + this.bookingId + '/balance-preview'
          : this.ctx + '/api/user/pg-bookings/' + this.bookingId + '/checkout-preview';
        const data = await AuthAPI.request(previewUrl);
        this.preview = data.data;
        this.balancePaymentMode = !!this.preview.balancePayment;
        this.renderSummary();
        this.renderOccupants();
        this.renderPrice();
        this.showRazorpayBanner(this.preview.razorpayConfigured);
        if (checkout) checkout.style.display = 'grid';
        if (!this.balancePaymentMode) {
          this.startTimer(this.preview.reservationMinutes || 30);
        } else {
          const timer = document.getElementById('reservationTimer');
          if (timer) timer.style.display = 'none';
          const header = document.querySelector('.booking-payment-header h1');
          if (header) header.innerHTML = 'PG <span>Balance</span> Payment';
          const sub = document.querySelector('.booking-sub');
          if (sub) sub.textContent = 'Pay remaining rent and security deposit after PG owner approval';
        }
      } catch (err) {
        this.showError(err.message || 'Could not load booking');
      } finally {
        if (loader) loader.style.display = 'none';
      }
    },

    renderSummary() {
      const p = this.preview;
      const pg = p.pg || {};
      const img = pg.coverImageUrl ? this.ctx + (pg.coverImageUrl.charAt(0) === '/' ? pg.coverImageUrl : '/' + pg.coverImageUrl) : '';
      document.getElementById('pgSummarySection').innerHTML =
        (img ? '<img src="' + escAttr(img) + '" alt="" class="pg-pay-thumb">' : '') +
        '<h2>' + esc(pg.pgName || 'PG') + '</h2>' +
        '<p class="prop-meta">' + esc(pg.pgCode || '') + ' · Room ' + esc(p.roomNumber) + '</p>' +
        '<p class="prop-loc">' + esc([pg.city, pg.state].filter(Boolean).join(', ')) + '</p>' +
        '<p><strong>Beds:</strong> ' + (p.bedCount || 0) + ' (#' + esc(p.bedNumbers || '') + ')</p>' +
        '<p><strong>Booking ref:</strong> ' + esc(p.bookingCode) + '</p>';
    },

    renderOccupants() {
      const list = (this.preview.occupants || []).map(function (o) {
        return '<li><strong>' + esc(o.fullName) + '</strong> · Bed ' + (o.bedNumber || '—') +
          ' · ' + esc(o.mobile) + (o.email ? ' · ' + esc(o.email) : '') + '</li>';
      }).join('');
      document.getElementById('pgOccupantsSection').innerHTML =
        '<h3>Guest details</h3><ul class="property-book-amenity-list">' + (list || '<li>—</li>') + '</ul>';
    },

    renderPrice() {
      const p = this.preview;
      let html = '<h3>Payment summary</h3><table class="price-table">';
      if (p.balancePayment) {
        html += row('Already paid', p.paidAmount);
        html += row('Sharing', p.sharingLabel || '—');
        html += row('Security deposit', p.securityDepositAmount);
        html += row('Total booking', p.totalAmount);
        html += '<tr class="price-total-row"><td><strong>Pay now (balance + deposit)</strong></td><td><strong>' + formatInr(p.payableNow) + '</strong></td></tr>';
      } else {
        html += row('Rent per bed', p.rentPerBed);
        html += row('Beds booked', p.bedCount);
        html += row('Monthly rent', num(p.rentPerBed) * (p.bedCount || 1));
        html += row('Security deposit', p.securityDepositAmount);
        html += row('Total booking', p.totalAmount);
        html += '<tr class="price-total-row"><td><strong>Pay now (advance)</strong></td><td><strong>' + formatInr(p.payableNow) + '</strong></td></tr>';
        html += row('Due after owner approval', p.remainingAmount);
      }
      html += '</table>';
      document.getElementById('pgPriceSection').innerHTML = html;
    },

    async proceedPayment() {
      this.setLoading(true);
      try {
        let orderRes;
        if (this.rentPaymentMode && this.rentDueId) {
          orderRes = await AuthAPI.request(this.ctx + '/api/user/pg-bookings/rent-dues/' + this.rentDueId + '/pay', { method: 'POST' });
        } else {
          orderRes = await AuthAPI.request(this.ctx + '/api/user/pg-bookings/' + this.bookingId + '/pay', { method: 'POST' });
        }
        await this.openRazorpay(orderRes.data);
      } catch (err) {
        toast(err.message || 'Payment could not start', 'error');
      } finally {
        this.setLoading(false);
      }
    },

    showRazorpayBanner(configured) {
      const el = document.getElementById('razorpayModeBanner');
      if (!el) return;
      el.style.display = 'block';
      if (configured) {
        el.className = 'razorpay-mode-banner razorpay-mode-banner--live';
        el.innerHTML = '<strong>Razorpay Test Mode</strong> — Use test card <code>4111 1111 1111 1111</code>, any future expiry, any CVV.';
      } else {
        el.className = 'razorpay-mode-banner razorpay-mode-banner--demo';
        el.innerHTML = '<strong>Demo mode</strong> — Configure Razorpay keys in application.properties for live checkout.';
      }
    },

    openRazorpay(order) {
      const self = this;
      return new Promise(function (resolve, reject) {
        if (order.demoMode) {
          if (!confirm('Demo mode: simulate successful payment?')) {
            reject(new Error('Payment cancelled'));
            return;
          }
          self.verifyDemo(order).then(resolve).catch(reject);
          return;
        }
        if (typeof Razorpay === 'undefined') {
          reject(new Error('Razorpay script failed to load'));
          return;
        }
        const rzp = new Razorpay({
          key: order.razorpayKeyId,
          currency: order.currency || 'INR',
          name: 'EstateVault',
          description: 'PG booking ' + order.bookingCode,
          order_id: order.razorpayOrderId,
          prefill: { name: order.userName, email: order.userEmail, contact: order.userMobile },
          theme: { color: '#D4AF37' },
          handler: function (response) {
            self.verifyPayment(order, response, false).then(resolve).catch(reject);
          },
          modal: { ondismiss: function () { reject(new Error('Payment cancelled')); } }
        });
        rzp.open();
      });
    },

    verifyDemo(order) {
      return this.verifyPayment(order, {
        razorpay_order_id: order.razorpayOrderId,
        razorpay_payment_id: 'demo_pg_' + Date.now(),
        razorpay_signature: 'demo'
      }, true);
    },

    async verifyPayment(order, response, demoMode) {
      let res;
      if (this.rentPaymentMode && this.rentDueId) {
        res = await AuthAPI.request(this.ctx + '/api/user/pg-bookings/rent-payment/verify', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            rentDueId: this.rentDueId,
            razorpayOrderId: response.razorpay_order_id || order.razorpayOrderId,
            razorpayPaymentId: response.razorpay_payment_id,
            razorpaySignature: response.razorpay_signature,
            demoMode: !!demoMode
          })
        });
        const successUrl = this.ctx + '/user/bookings';
        document.getElementById('successModalText').textContent =
          'Monthly rent for ' + (res.data.periodLabel || '') + ' paid successfully.';
        document.getElementById('successModalBtn').href = successUrl;
        document.getElementById('successModalBtn').textContent = 'Back to my bookings';
        document.getElementById('paymentSuccessModal').style.display = 'flex';
        setTimeout(function () { window.location.href = successUrl; }, 2200);
        return res.data;
      }
      res = await AuthAPI.request(this.ctx + '/api/user/pg-bookings/payment/verify', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          bookingId: order.bookingId,
          razorpayOrderId: response.razorpay_order_id || order.razorpayOrderId,
          razorpayPaymentId: response.razorpay_payment_id,
          razorpaySignature: response.razorpay_signature,
          demoMode: !!demoMode
        })
      });
      const successUrl = this.ctx + '/user/pg-booking/success?bookingId=' + order.bookingId;
      const fullyPaid = res.data && res.data.status === 'FULLY_PAID';
      document.getElementById('successModalText').textContent = fullyPaid
        ? 'PG booking ' + (res.data.bookingCode || '') + ' fully paid. Confirmation emails sent.'
        : 'PG booking ' + (res.data.bookingCode || '') + ' — advance paid: ' + formatInr(res.data.paidAmount);
      document.getElementById('successModalBtn').href = successUrl;
      document.getElementById('paymentSuccessModal').style.display = 'flex';
      setTimeout(function () { window.location.href = successUrl; }, 2200);
      return res.data;
    },

    showError(msg) {
      const errEl = document.getElementById('pgPayError');
      if (errEl) {
        errEl.style.display = 'block';
        errEl.innerHTML = '<p>' + esc(msg) + '</p><a href="' + esc(this.ctx) + '/user/pgs" class="btn-gold">Back</a>';
      }
    },

    setLoading(on) {
      const btn = document.getElementById('pgProceedPayBtn');
      if (!btn) return;
      btn.disabled = on;
      btn.querySelector('.btn-proceed-text').style.display = on ? 'none' : 'inline';
      btn.querySelector('.btn-proceed-loader').style.display = on ? 'inline-block' : 'none';
    },

    startTimer(minutes) {
      const wrap = document.getElementById('reservationTimer');
      const display = document.getElementById('timerDisplay');
      if (!wrap || !display) return;
      wrap.style.display = 'flex';
      let seconds = minutes * 60;
      if (this.timerInterval) clearInterval(this.timerInterval);
      this.timerInterval = setInterval(function () {
        if (seconds <= 0) { display.textContent = 'Expired'; return; }
        seconds--;
        display.textContent = String(Math.floor(seconds / 60)).padStart(2, '0') + ':' + String(seconds % 60).padStart(2, '0');
      }, 1000);
    }
  };

  function row(label, val) {
    if (val == null || val === '') return '';
    var isMoney = label.indexOf('bed') === -1 && label.indexOf('Sharing') === -1;
    var display = isMoney && !isNaN(parseFloat(val)) ? formatInr(val) : esc(String(val));
    return '<tr><td>' + esc(label) + '</td><td>' + display + '</td></tr>';
  }

  function num(v) { return parseFloat(v) || 0; }

  function formatInr(amount) {
    var n = parseFloat(amount);
    if (isNaN(n)) return '₹ —';
    return '₹ ' + n.toLocaleString('en-IN', { maximumFractionDigits: 0 });
  }

  function formatDate(d) {
    if (!d) return '—';
    try {
      return new Date(d + 'T00:00:00').toLocaleDateString('en-IN', { day: 'numeric', month: 'short', year: 'numeric' });
    } catch (e) {
      return String(d);
    }
  }

  function esc(s) { return String(s || '').replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;'); }
  function escAttr(s) { return esc(s).replace(/"/g, '&quot;'); }
  function toast(msg, type) { if (typeof UI !== 'undefined' && UI.toast) UI.toast(msg, type); else alert(msg); }

  global.PgBookingPayment = PgBookingPayment;
})(typeof window !== 'undefined' ? window : this);
