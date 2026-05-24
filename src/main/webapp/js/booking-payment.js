/**
 * EstateVault — property booking & Razorpay checkout
 */
(function (global) {
  'use strict';

  const BookingPayment = {
    ctx: '',
    propertyId: null,
    bookingId: null,
    balanceMode: false,
    preview: null,
    balancePreview: null,
    selectedEmiMonths: 6,
    draftBookingId: null,
    timerInterval: null,

    init(ctx, options) {
      this.ctx = (ctx || '').replace(/\/$/, '');
      options = options || {};
      this.propertyId = options.propertyId;
      this.bookingId = options.bookingId;
      this.balanceMode = !!options.balanceMode;
      this.bindUi();
      if (this.balanceMode && this.bookingId) {
        this.loadBalancePreview();
      } else if (this.propertyId) {
        this.loadPreview();
      } else {
        const errEl = document.getElementById('bookingError');
        if (errEl) {
          errEl.style.display = 'block';
          errEl.innerHTML = '<p>Invalid checkout.</p><a href="' + esc(this.ctx) + '/user/bookings" class="btn-gold">My bookings</a>';
        }
      }
    },

    bindUi() {
      const self = this;
      document.querySelectorAll('input[name="paymentPlan"]').forEach(function (radio) {
        radio.addEventListener('change', function () {
          self.onPlanChange();
        });
      });
      document.querySelectorAll('input[name="payMethod"]').forEach(function (radio) {
        radio.addEventListener('change', function () {
          document.querySelectorAll('.pay-method').forEach(function (l) { l.classList.remove('active'); });
          radio.closest('.pay-method')?.classList.add('active');
        });
      });
      document.getElementById('applyCoupon')?.addEventListener('click', function () {
        self.loadPreview(document.getElementById('couponCode')?.value);
      });
      document.getElementById('proceedPayBtn')?.addEventListener('click', function () {
        self.proceedPayment();
      });
    },

    async loadBalancePreview() {
      const loader = document.getElementById('bookingLoader');
      const checkout = document.getElementById('bookingCheckout');
      const errEl = document.getElementById('bookingError');
      if (loader) loader.style.display = 'flex';
      if (checkout) checkout.style.display = 'none';
      if (errEl) errEl.style.display = 'none';

      try {
        const data = await AuthAPI.request(this.ctx + '/api/user/bookings/' + this.bookingId + '/balance-preview');
        this.balancePreview = data.data;
        this.draftBookingId = this.bookingId;
        this.renderProperty(this.balancePreview.property);
        this.renderBalancePrice(this.balancePreview);
        this.showRazorpayBanner(this.balancePreview.razorpayConfigured);
        document.getElementById('bookingPlanSection')?.style.setProperty('display', 'none');
        document.getElementById('bookingCouponSection')?.style.setProperty('display', 'none');
        document.getElementById('reservationTimer')?.style.setProperty('display', 'none');
        const btnText = document.querySelector('#proceedPayBtn .btn-proceed-text');
        if (btnText) {
          btnText.textContent = 'Pay remaining ' + formatInr(this.balancePreview.remainingAmount);
        }
        if (checkout) checkout.style.display = 'grid';
        this.setProgressStep(3);
      } catch (err) {
        if (errEl) {
          errEl.style.display = 'block';
          errEl.innerHTML = '<p>' + esc(err.message) + '</p><a href="' + esc(this.ctx) + '/user/bookings" class="btn-gold">Back to bookings</a>';
        }
      } finally {
        if (loader) loader.style.display = 'none';
      }
    },

    renderBalancePrice(data) {
      const el = document.getElementById('priceSection');
      if (!el) return;
      el.innerHTML = '<h3>Outstanding balance</h3>' +
        '<p class="balance-booking-ref">' + esc(data.bookingCode) + ' · ' + esc(data.statusLabel || '') + '</p>' +
        '<table class="price-table">' +
        row('Booking total', data.totalAmount) +
        row('Already paid', data.paidAmount) +
        '<tr class="price-total-row"><td><strong>Due now</strong></td><td><strong>' + formatInr(data.remainingAmount) + '</strong></td></tr>' +
        '</table>';
    },

    async loadPreview(couponCode) {
      const loader = document.getElementById('bookingLoader');
      const checkout = document.getElementById('bookingCheckout');
      const errEl = document.getElementById('bookingError');
      if (loader) loader.style.display = 'flex';
      if (checkout) checkout.style.display = 'none';
      if (errEl) errEl.style.display = 'none';

      try {
        let url = this.ctx + '/api/user/bookings/checkout-preview?propertyId=' + this.propertyId;
        if (couponCode) url += '&couponCode=' + encodeURIComponent(couponCode);
        const data = await AuthAPI.request(url);
        this.preview = data.data;
        if (!this.preview.available) {
          throw new Error(this.preview.availabilityMessage || 'Property not available');
        }
        this.renderProperty(this.preview.property);
        this.renderPrice(this.preview.priceBreakdown);
        this.renderEmiPlans(this.preview.emiPlans);
        this.onPlanChange();
        this.showRazorpayBanner(this.preview.razorpayConfigured);
        if (checkout) checkout.style.display = 'grid';
        this.startReservationTimer(this.preview.reservationMinutes || 30);
      } catch (err) {
        if (errEl) {
          errEl.style.display = 'block';
          errEl.innerHTML = '<p>' + esc(err.message) + '</p><a href="' + esc(this.ctx) + '/user/properties" class="btn-gold">Back to listings</a>';
        }
      } finally {
        if (loader) loader.style.display = 'none';
      }
    },

    onPlanChange() {
      const plan = document.querySelector('input[name="paymentPlan"]:checked')?.value || 'FULL';
      document.querySelectorAll('.plan-option').forEach(function (l) {
        l.classList.toggle('active', l.querySelector('input')?.checked);
      });
      const emiSec = document.getElementById('emiPlansSection');
      if (emiSec) emiSec.style.display = plan === 'EMI' ? 'block' : 'none';
      this.updatePayableDisplay(plan);
      this.setProgressStep(plan === 'EMI' ? 2 : 2);
    },

    updatePayableDisplay(plan) {
      if (!this.preview) return;
      const total = num(this.preview.priceBreakdown.totalAmount);
      let payable = total;
      if (plan === 'BOOKING_ADVANCE') {
        payable = total * 0.10;
      } else if (plan === 'EMI') {
        const planObj = (this.preview.emiPlans || []).find(function (p) { return p.months === BookingPayment.selectedEmiMonths; });
        payable = planObj ? num(planObj.payableNow) : total * 0.1058;
      }
      const el = document.getElementById('payableNowHighlight');
      if (el) el.textContent = formatInr(payable);
    },

    renderProperty(p) {
      if (!p) return;
      const img = imageUrl(this.ctx, p.primaryImageUrl);
      const gallery = (p.galleryUrls || []).map(function (u, i) {
        return '<button type="button" class="slider-thumb' + (i === 0 ? ' active' : '') + '" data-src="' + escAttr(imageUrl(BookingPayment.ctx, u)) + '"><img src="' + escAttr(imageUrl(BookingPayment.ctx, u)) + '" alt=""></button>';
      }).join('');
      document.getElementById('propertySection').innerHTML =
        '<div class="property-slider">' +
        '<img id="sliderMain" src="' + escAttr(img) + '" alt="' + escAttr(p.title) + '">' +
        (gallery ? '<div class="slider-thumbs">' + gallery + '</div>' : '') +
        '</div>' +
        '<h2>' + esc(p.title) + '</h2>' +
        '<p class="prop-meta"><span>' + esc(p.propertyCode) + '</span> · ' + esc(p.categoryLabel) + ' · ' + esc(p.propertySubType || '') + '</p>' +
        '<p class="prop-loc">&#128205; ' + esc([p.locality, p.city, p.state].filter(Boolean).join(', ')) + '</p>' +
        (p.agentName ? '<p class="prop-agent">Agent: <strong>' + esc(p.agentName) + '</strong></p>' : '') +
        '<p class="prop-avail"><span class="avail-dot"></span> Available for booking</p>' +
        (p.description ? '<p class="prop-desc">' + esc(p.description).substring(0, 280) + (p.description.length > 280 ? '…' : '') + '</p>' : '') +
        (p.amenities ? '<p class="prop-amenities"><strong>Amenities:</strong> ' + esc(p.amenities) + '</p>' : '');
      document.querySelectorAll('.slider-thumb').forEach(function (btn) {
        btn.addEventListener('click', function () {
          document.getElementById('sliderMain').src = btn.getAttribute('data-src');
          document.querySelectorAll('.slider-thumb').forEach(function (b) { b.classList.remove('active'); });
          btn.classList.add('active');
        });
      });
    },

    renderPrice(price) {
      if (!price) return;
      document.getElementById('priceSection').innerHTML =
        '<h3>Price details</h3>' +
        '<table class="price-table">' +
        row('Property price', price.basePrice) +
        row('GST / Tax', price.gstAmount) +
        row('Registration charges', price.registrationCharges) +
        row('Booking charges', price.bookingCharges) +
        (num(price.discountAmount) > 0 ? row('Discount', '-' + formatInr(price.discountAmount)) : '') +
        '</table>' +
        '<div class="price-total-row"><span>Total amount</span><strong>' + formatInr(price.totalAmount) + '</strong></div>' +
        '<div class="price-pay-now-row"><span>Pay now</span><strong id="payableNowHighlight">' + formatInr(price.payableNow) + '</strong></div>';
    },

    renderEmiPlans(plans) {
      const grid = document.getElementById('emiPlansGrid');
      if (!grid || !plans) return;
      const self = this;
      grid.innerHTML = plans.map(function (plan) {
        const active = plan.months === self.selectedEmiMonths ? ' active' : '';
        return '<button type="button" class="emi-plan-card' + active + '" data-months="' + plan.months + '">' +
          '<span class="emi-months">' + plan.months + ' mo</span>' +
          '<span class="emi-amount">' + formatInr(plan.monthlyEmi) + '/mo</span>' +
          '<span class="emi-down">Down ' + formatInr(plan.downPayment) + '</span></button>';
      }).join('');
      grid.querySelectorAll('.emi-plan-card').forEach(function (btn) {
        btn.addEventListener('click', function () {
          self.selectedEmiMonths = parseInt(btn.getAttribute('data-months'), 10);
          grid.querySelectorAll('.emi-plan-card').forEach(function (b) { b.classList.remove('active'); });
          btn.classList.add('active');
          self.showEmiBreakdown(plans.find(function (p) { return p.months === self.selectedEmiMonths; }));
          self.updatePayableDisplay('EMI');
        });
      });
      if (plans.length) {
        this.showEmiBreakdown(plans.find(function (p) { return p.months === this.selectedEmiMonths; }.bind(this)) || plans[1]);
      }
    },

    showEmiBreakdown(plan) {
      const el = document.getElementById('emiBreakdown');
      if (!el || !plan) return;
      let table = '<table class="emi-table"><thead><tr><th>#</th><th>Due date</th><th>Amount</th></tr></thead><tbody>';
      (plan.schedule || []).forEach(function (s) {
        table += '<tr><td>' + s.installmentNumber + '</td><td>' + formatDate(s.dueDate) + '</td><td>' + formatInr(s.amount) + '</td></tr>';
      });
      table += '</tbody></table>';
      el.innerHTML = '<p><strong>Down payment:</strong> ' + formatInr(plan.downPayment) +
        ' · <strong>Principal:</strong> ' + formatInr(plan.principalAmount) +
        ' · <strong>Interest:</strong> ' + formatInr(plan.totalInterest) + '</p>' + table;
    },

    async proceedPayment() {
      if (this.balanceMode) {
        return this.proceedBalancePayment();
      }
      const plan = document.querySelector('input[name="paymentPlan"]:checked')?.value || 'FULL';
      const method = document.querySelector('input[name="payMethod"]:checked')?.value || 'UPI';
      const coupon = document.getElementById('couponCode')?.value?.trim() || null;
      this.setPayLoading(true);
      this.setProgressStep(3);

      try {
        const draftBody = {
          propertyId: Number(this.propertyId),
          paymentPlanType: plan,
          paymentMethod: method,
          emiMonths: plan === 'EMI' ? this.selectedEmiMonths : null,
          couponCode: coupon
        };
        const draftRes = await AuthAPI.request(this.ctx + '/api/user/bookings/draft', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(draftBody)
        });
        this.draftBookingId = draftRes.data.id;
        await this.startPayOrder(this.draftBookingId);
      } catch (err) {
        toast(err.message || 'Payment could not start', 'error');
      } finally {
        this.setPayLoading(false);
      }
    },

    async proceedBalancePayment() {
      this.setPayLoading(true);
      this.setProgressStep(3);
      try {
        await this.startPayOrder(this.bookingId);
      } catch (err) {
        toast(err.message || 'Payment could not start', 'error');
      } finally {
        this.setPayLoading(false);
      }
    },

    async startPayOrder(bookingId) {
      const orderRes = await AuthAPI.request(this.ctx + '/api/user/bookings/' + bookingId + '/pay', {
        method: 'POST'
      });
      const order = orderRes.data;
      await this.openRazorpay(order);
    },

    showRazorpayBanner(configured) {
      const el = document.getElementById('razorpayModeBanner');
      if (!el) return;
      if (configured) {
        el.style.display = 'block';
        el.className = 'razorpay-mode-banner razorpay-mode-banner--live';
        el.innerHTML = '<strong>Razorpay Test Mode</strong> — Use test card <code>4111 1111 1111 1111</code>, any future expiry, any CVV. OTP: any 4–10 digits.';
      } else {
        el.style.display = 'block';
        el.className = 'razorpay-mode-banner razorpay-mode-banner--demo';
        el.innerHTML = '<strong>Demo mode</strong> — Add <code>app.razorpay.key-id</code> and <code>app.razorpay.key-secret</code> in application.properties for real test checkout.';
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
          reject(new Error('Razorpay checkout script failed to load. Check your internet connection.'));
          return;
        }
        if (!order.razorpayKeyId || !order.razorpayOrderId) {
          reject(new Error('Payment gateway not ready. Please refresh and try again.'));
          return;
        }
        const options = {
          key: order.razorpayKeyId,
          currency: order.currency || 'INR',
          name: 'EstateVault',
          description: 'Property booking ' + order.bookingCode,
          order_id: order.razorpayOrderId,
          prefill: {
            name: order.userName || '',
            email: order.userEmail || '',
            contact: order.userMobile || ''
          },
          theme: { color: '#D4AF37' },
          handler: function (response) {
            self.verifyPayment(order, response, false).then(resolve).catch(reject);
          },
          modal: {
            ondismiss: function () {
              reject(new Error('Payment cancelled'));
            }
          }
        };
        const rzp = new Razorpay(options);
        rzp.on('payment.failed', function (resp) {
          const msg = (resp.error && resp.error.description) ? resp.error.description : 'Payment failed';
          reject(new Error(msg));
        });
        rzp.open();
      });
    },

    async verifyDemo(order) {
      return this.verifyPayment(order, {
        razorpay_order_id: order.razorpayOrderId,
        razorpay_payment_id: 'demo_' + Date.now(),
        razorpay_signature: 'demo'
      }, true);
    },

    async verifyPayment(order, response, demoMode) {
      const body = {
        bookingId: order.bookingId,
        razorpayOrderId: response.razorpay_order_id || order.razorpayOrderId,
        razorpayPaymentId: response.razorpay_payment_id,
        razorpaySignature: response.razorpay_signature,
        demoMode: !!demoMode
      };
      const res = await AuthAPI.request(this.ctx + '/api/user/bookings/payment/verify', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body)
      });
      this.setProgressStep(4);
      this.showSuccess(res.data);
      return res.data;
    },

    showSuccess(data) {
      const modal = document.getElementById('paymentSuccessModal');
      const text = document.getElementById('successModalText');
      const btn = document.getElementById('successModalBtn');
      const successUrl = this.ctx + '/user/booking/success?bookingId=' + data.bookingId;
      if (text) {
        text.textContent = 'Payment successful! Booking ' + (data.bookingCode || '') +
          ' is confirmed. Amount paid: ₹' + num(data.paidAmount).toLocaleString('en-IN') +
          '. A confirmation email has been sent to you.';
      }
      if (btn) {
        btn.href = successUrl;
      }
      if (modal) modal.style.display = 'flex';
      toast('Payment completed successfully', 'success');
      setTimeout(function () {
        window.location.href = successUrl;
      }, 2500);
    },

    setPayLoading(loading) {
      const btn = document.getElementById('proceedPayBtn');
      if (!btn) return;
      btn.disabled = loading;
      btn.querySelector('.btn-proceed-text').style.display = loading ? 'none' : 'inline';
      btn.querySelector('.btn-proceed-loader').style.display = loading ? 'inline-block' : 'none';
    },

    setProgressStep(step) {
      document.querySelectorAll('.booking-progress-step').forEach(function (el) {
        const s = parseInt(el.getAttribute('data-step'), 10);
        el.classList.toggle('active', s <= step);
        el.classList.toggle('done', s < step);
      });
    },

    startReservationTimer(minutes) {
      const wrap = document.getElementById('reservationTimer');
      const display = document.getElementById('timerDisplay');
      if (!wrap || !display) return;
      wrap.style.display = 'flex';
      let seconds = minutes * 60;
      const self = this;
      if (this.timerInterval) clearInterval(this.timerInterval);
      this.timerInterval = setInterval(function () {
        if (seconds <= 0) {
          clearInterval(self.timerInterval);
          display.textContent = 'Expired';
          return;
        }
        seconds--;
        const m = Math.floor(seconds / 60);
        const s = seconds % 60;
        display.textContent = String(m).padStart(2, '0') + ':' + String(s).padStart(2, '0');
      }, 1000);
    }
  };

  function row(label, amount) {
    return '<tr><td>' + esc(label) + '</td><td>' + formatInr(amount) + '</td></tr>';
  }

  function num(v) { return parseFloat(v) || 0; }

  function formatInr(amount) {
    const n = num(amount);
    if (n >= 10000000) return '₹ ' + (n / 10000000).toFixed(2) + ' Cr';
    if (n >= 100000) return '₹ ' + (n / 100000).toFixed(2) + ' L';
    return '₹ ' + n.toLocaleString('en-IN', { maximumFractionDigits: 0 });
  }

  function formatDate(d) {
    if (!d) return '—';
    if (Array.isArray(d)) return d[0] + '-' + String(d[1]).padStart(2, '0') + '-' + String(d[2]).padStart(2, '0');
    return String(d).split('T')[0];
  }

  function imageUrl(ctx, path) {
    if (!path) return 'https://images.unsplash.com/photo-1600596542815-ffad4c1539a9?w=800&q=80';
    return ctx + (path.charAt(0) === '/' ? path : '/' + path);
  }

  function esc(s) {
    if (!s) return '';
    return String(s).replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
  }

  function escAttr(s) { return esc(s).replace(/"/g, '&quot;'); }

  function toast(msg, type) {
    if (typeof UI !== 'undefined' && UI.toast) UI.toast(msg, type);
    else alert(msg);
  }

  global.BookingPayment = BookingPayment;
})(typeof window !== 'undefined' ? window : this);
