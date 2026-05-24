(function (global) {
  'use strict';

  const PgOwnerSubscription = {
    ctx: '',
    overview: null,

    init(ctx, page) {
      this.ctx = (ctx || '').replace(/\/$/, '');
      if (page === 'overview') this.loadOverview();
      if (page === 'plans') this.loadPlansPage();
      if (page === 'payment') this.initPaymentPage();
    },

    async loadOverview() {
      try {
        const res = await AuthAPI.request(this.ctx + '/api/pg-owner/subscription/overview');
        this.overview = res.data;
        this.renderOverview();
      } catch (e) {
        if (typeof UI !== 'undefined' && UI.toast) UI.toast(e.message || 'Could not load subscription', 'error');
      }
    },

    renderOverview() {
      const o = this.overview;
      if (!o || !o.postingStatus) return;
      const ps = o.postingStatus;
      setText('subPlanName', o.activePlan ? o.activePlan.planName : 'Free Plan');
      setText('subStatus', o.subscriptionStatusLabel || 'Free');
      setText('subPosted', ps.totalPosted);
      setText('subRemaining', ps.unlimited ? 'Unlimited' : (ps.remainingPosts < 0 ? '—' : ps.remainingPosts));
      setText('subExpiry', o.expiryDate ? formatDate(o.expiryDate) : '—');
      setText('subFreeRemaining', Math.max(0, ps.freeLimit - ps.totalPosted));
      const hist = document.getElementById('paymentHistoryBody');
      if (hist) {
        hist.innerHTML = (o.paymentHistory || []).map(function (p) {
          return '<tr><td>' + esc(p.invoiceNumber || '—') + '</td><td>' + esc(p.planName) + '</td><td>₹' + esc(p.amount) + '</td><td><span class="status-pill ' + (p.paymentStatus === 'SUCCESS' ? 'active' : 'pending') + '">' + esc(p.paymentStatus) + '</span></td><td>' + esc(formatDate(p.paymentDate)) + '</td></tr>';
        }).join('') || '<tr><td colspan="5" class="text-muted">No payments yet</td></tr>';
      }
    },

    async loadPlansPage() {
      if (new URLSearchParams(window.location.search).get('limit') === '1') {
        const banner = document.getElementById('limitBanner');
        if (banner) banner.style.display = 'block';
      }
      try {
        const res = await AuthAPI.request(this.ctx + '/api/pg-owner/subscription/overview');
        this.overview = res.data;
        const canUpgrade = this.overview.canUpgradePlans !== false;
        const blockedPanel = document.getElementById('upgradeBlockedPanel');
        const hero = document.getElementById('plansPageHero');
        const compare = document.getElementById('compareSection');
        const grid = document.getElementById('plansGrid');
        if (!canUpgrade) {
          if (blockedPanel) {
            blockedPanel.style.display = 'block';
            const msg = document.getElementById('upgradeBlockedMessage');
            if (msg) msg.textContent = this.overview.upgradeBlockedMessage || (this.overview.postingStatus && this.overview.postingStatus.message) || '';
          }
          if (hero) hero.style.display = 'none';
          if (compare) compare.style.display = 'none';
          if (grid) grid.innerHTML = '';
          return;
        }
        if (blockedPanel) blockedPanel.style.display = 'none';
        if (hero) hero.style.display = '';
        if (compare) compare.style.display = '';
        this.renderPlanCards(this.overview.availablePlans || []);
        this.renderCompare(this.overview.availablePlans || []);
      } catch (e) {
        if (typeof UI !== 'undefined' && UI.toast) UI.toast(e.message, 'error');
      }
    },

    renderPlanCards(plans) {
      const grid = document.getElementById('plansGrid');
      if (!grid) return;
      if (!plans.length) {
        grid.innerHTML = '<p class="text-muted">No upgrade plans available. Reach your listing limit to see upgrade options.</p>';
        return;
      }
      grid.innerHTML = plans.map(function (plan) {
        const listings = plan.unlimitedPgListings || plan.maxPgListings < 0 ? 'Unlimited PG listings' : plan.maxPgListings + ' PG listings';
        const rooms = plan.unlimitedRooms || plan.maxRoomListings < 0 ? 'Unlimited rooms' : plan.maxRoomListings + ' rooms';
        return '<div class="sub-plan-card' + (plan.recommended ? ' recommended' : '') + '">' +
          (plan.recommended ? '<span class="sub-plan-badge">Recommended</span>' : '') +
          '<h3>' + esc(plan.planName) + '</h3>' +
          '<div class="sub-plan-price">₹' + esc(plan.price) + '<small>/' + esc(plan.durationDays) + ' days</small></div>' +
          '<ul class="sub-plan-features">' +
          '<li>' + listings + '</li><li>' + rooms + '</li>' +
          '<li>' + esc(plan.featuredPgCount) + ' featured PG slots</li>' +
          (plan.tenantAnalyticsAccess ? '<li>Analytics dashboard</li>' : '') +
          (plan.premiumBadge ? '<li>Premium owner badge</li>' : '') +
          (plan.prioritySupport ? '<li>Priority support</li>' : '') +
          '</ul>' +
          '<button type="button" class="pgo-btn-gold w-100" data-plan-id="' + plan.id + '">Upgrade Now</button></div>';
      }).join('');
      grid.querySelectorAll('[data-plan-id]').forEach(function (btn) {
        btn.addEventListener('click', function () {
          PgOwnerSubscription.startCheckout(btn.getAttribute('data-plan-id'));
        });
      });
    },

    renderCompare(plans) {
      const tbody = document.getElementById('compareBody');
      if (!tbody || !plans.length) return;
      const rows = [
        ['PG listings', function (p) { return p.unlimitedPgListings ? 'Unlimited' : p.maxPgListings; }],
        ['Rooms', function (p) { return p.unlimitedRooms ? 'Unlimited' : p.maxRoomListings; }],
        ['Price', function (p) { return '₹' + p.price; }],
        ['Analytics', function (p) { return p.tenantAnalyticsAccess ? 'Yes' : '—'; }]
      ];
      tbody.innerHTML = rows.map(function (row) {
        return '<tr><th>' + row[0] + '</th>' + plans.map(function (p) {
          return '<td>' + esc(String(row[1](p))) + '</td>';
        }).join('') + '</tr>';
      }).join('');
      const head = document.getElementById('compareHead');
      if (head) {
        head.innerHTML = '<tr><th>Feature</th>' + plans.map(function (p) {
          return '<th>' + esc(p.planName) + '</th>';
        }).join('') + '</tr>';
      }
    },

    async startCheckout(planId) {
      if (typeof UI !== 'undefined' && UI.showLoader) UI.showLoader();
      try {
        const res = await AuthAPI.request(this.ctx + '/api/pg-owner/subscription/checkout/' + planId, { method: 'POST' });
        const checkout = res.data;
        if (checkout.demoMode) {
          await AuthAPI.request(this.ctx + '/api/pg-owner/subscription/payment/demo/' + checkout.subscriptionId, { method: 'POST' });
          if (typeof UI !== 'undefined' && UI.toast) UI.toast('Subscription activated (demo mode)');
          window.location.assign(this.ctx + '/pg-owner/subscription?success=1');
          return;
        }
        sessionStorage.setItem('pgSubCheckout', JSON.stringify(checkout));
        window.location.assign(this.ctx + '/pg-owner/subscription/payment?subscriptionId=' + checkout.subscriptionId);
      } catch (e) {
        if (typeof UI !== 'undefined' && UI.toast) UI.toast(e.message, 'error');
      } finally {
        if (typeof UI !== 'undefined' && UI.hideLoader) UI.hideLoader();
      }
    },

    initPaymentPage() {
      const raw = sessionStorage.getItem('pgSubCheckout');
      const subId = new URLSearchParams(window.location.search).get('subscriptionId');
      let checkout = null;
      try { checkout = raw ? JSON.parse(raw) : null; } catch (e) { /* ignore */ }
      if (!checkout || String(checkout.subscriptionId) !== String(subId)) {
        window.location.assign(this.ctx + '/pg-owner/subscription/plans');
        return;
      }
      setText('payPlanName', checkout.planName);
      setText('payAmount', '₹' + checkout.amount);
      document.getElementById('payNowBtn')?.addEventListener('click', function () {
        PgOwnerSubscription.openRazorpay(checkout);
      });
    },

    openRazorpay(checkout) {
      if (typeof Razorpay === 'undefined') {
        if (typeof UI !== 'undefined' && UI.toast) UI.toast('Payment SDK not loaded', 'error');
        return;
      }
      const self = this;
      const rzp = new Razorpay({
        key: checkout.razorpayKeyId,
        amount: Math.round(parseFloat(checkout.amount) * 100),
        currency: 'INR',
        name: 'EstateVault',
        description: checkout.planName + ' PG Subscription',
        order_id: checkout.razorpayOrderId,
        handler: function (response) { self.verifyPayment(checkout.subscriptionId, response); },
        theme: { color: '#D4AF37' }
      });
      rzp.open();
    },

    async verifyPayment(subscriptionId, razorpayResponse) {
      if (typeof UI !== 'undefined' && UI.showLoader) UI.showLoader();
      try {
        await AuthAPI.request(this.ctx + '/api/pg-owner/subscription/payment/verify', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            subscriptionId: subscriptionId,
            razorpayOrderId: razorpayResponse.razorpay_order_id,
            razorpayPaymentId: razorpayResponse.razorpay_payment_id,
            razorpaySignature: razorpayResponse.razorpay_signature
          })
        });
        sessionStorage.removeItem('pgSubCheckout');
        window.location.assign(this.ctx + '/pg-owner/subscription?success=1');
      } catch (e) {
        if (typeof UI !== 'undefined' && UI.toast) UI.toast(e.message, 'error');
      } finally {
        if (typeof UI !== 'undefined' && UI.hideLoader) UI.hideLoader();
      }
    }
  };

  function setText(id, val) {
    const el = document.getElementById(id);
    if (el) el.textContent = val != null ? val : '—';
  }

  function formatDate(iso) {
    if (!iso) return '—';
    try {
      return new Date(iso).toLocaleDateString('en-IN', { day: '2-digit', month: 'short', year: 'numeric' });
    } catch (e) { return iso; }
  }

  function esc(s) {
    if (s == null) return '';
    return String(s).replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
  }

  global.PgOwnerSubscription = PgOwnerSubscription;
})(typeof window !== 'undefined' ? window : this);
