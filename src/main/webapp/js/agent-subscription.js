/**
 * Agent subscription plans, checkout, overview
 */
(function (global) {
  'use strict';

  const AgentSubscription = {
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
        const res = await AuthAPI.request(this.ctx + '/api/agent/subscription/overview');
        this.overview = res.data;
        this.renderOverview();
      } catch (e) {
        UI.toast(e.message || 'Could not load subscription', 'error');
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
        const rows = (o.paymentHistory || []).map(function (p) {
          return '<tr><td>' + esc(p.invoiceNumber || '—') + '</td><td>' + esc(p.planName) + '</td><td>₹' + esc(p.amount) + '</td><td><span class="status-pill ' + (p.paymentStatus === 'SUCCESS' ? 'active' : 'pending') + '">' + esc(p.paymentStatus) + '</span></td><td>' + esc(formatDate(p.paymentDate)) + '</td></tr>';
        }).join('');
        hist.innerHTML = rows || '<tr><td colspan="5" class="text-muted">No payments yet</td></tr>';
      }
    },

    async loadPlansPage() {
      const limit = new URLSearchParams(window.location.search).get('limit');
      if (limit === '1') {
        const banner = document.getElementById('limitBanner');
        if (banner) banner.style.display = 'block';
      }
      try {
        const res = await AuthAPI.request(this.ctx + '/api/agent/subscription/overview');
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
            if (msg) {
              msg.textContent = this.overview.upgradeBlockedMessage
                || (this.overview.postingStatus && this.overview.postingStatus.message)
                || 'Your current plan still has available listing slots.';
            }
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
        UI.toast(e.message, 'error');
      }
    },

    renderPlanCards(plans) {
      const grid = document.getElementById('plansGrid');
      if (!grid) return;
      if (!plans.length) {
        grid.innerHTML = '<p class="text-muted">No upgrade plans available right now. Check back when you reach your current plan limit.</p>';
        return;
      }
      grid.innerHTML = plans.map(function (plan) {
        const unlimited = plan.unlimitedProperties || plan.maxProperties < 0;
        const listings = unlimited ? 'Unlimited listings' : plan.maxProperties + ' property listings';
        return '<div class="sub-plan-card' + (plan.recommended ? ' recommended' : '') + '">' +
          (plan.recommended ? '<span class="sub-plan-badge">Recommended</span>' : '') +
          '<h3>' + esc(plan.planName) + '</h3>' +
          '<div class="sub-plan-price">₹' + esc(plan.price) + '<small>/' + esc(plan.durationDays) + ' days</small></div>' +
          '<ul class="sub-plan-features">' +
          '<li>' + listings + '</li>' +
          '<li>' + esc(plan.featuredListings) + ' featured slots</li>' +
          '<li>' + (plan.leadsLimit < 0 ? 'Unlimited' : plan.leadsLimit) + ' leads</li>' +
          (plan.analyticsAccess ? '<li>Analytics dashboard</li>' : '') +
          (plan.premiumBadge ? '<li>Premium agent badge</li>' : '') +
          '</ul>' +
          '<button type="button" class="agent-btn-gold w-100" data-plan-id="' + plan.id + '">Upgrade Now</button>' +
          '</div>';
      }).join('');

      grid.querySelectorAll('[data-plan-id]').forEach(function (btn) {
        btn.addEventListener('click', function () {
          AgentSubscription.startCheckout(btn.getAttribute('data-plan-id'));
        });
      });
    },

    renderCompare(plans) {
      const tbody = document.getElementById('compareBody');
      if (!tbody || !plans.length) return;
      const rows = [
        ['Listings', function (p) { return p.unlimitedProperties ? 'Unlimited' : p.maxProperties; }],
        ['Price', function (p) { return '₹' + p.price; }],
        ['Featured', function (p) { return p.featuredListings; }],
        ['Leads', function (p) { return p.leadsLimit < 0 ? 'Unlimited' : p.leadsLimit; }],
        ['Analytics', function (p) { return p.analyticsAccess ? 'Yes' : '—'; }]
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
      UI.showLoader();
      try {
        const res = await AuthAPI.request(this.ctx + '/api/agent/subscription/checkout/' + planId, { method: 'POST' });
        const checkout = res.data;
        if (checkout.demoMode) {
          await AuthAPI.request(this.ctx + '/api/agent/subscription/payment/demo/' + checkout.subscriptionId, { method: 'POST' });
          UI.toast('Subscription activated (demo mode)');
          window.location.assign(this.ctx + '/agent/subscription?success=1');
          return;
        }
        sessionStorage.setItem('subCheckout', JSON.stringify(checkout));
        window.location.assign(this.ctx + '/agent/subscription/payment?subscriptionId=' + checkout.subscriptionId);
      } catch (e) {
        UI.toast(e.message, 'error');
      } finally {
        UI.hideLoader();
      }
    },

    initPaymentPage() {
      const raw = sessionStorage.getItem('subCheckout');
      const params = new URLSearchParams(window.location.search);
      const subId = params.get('subscriptionId');
      let checkout = null;
      try { checkout = raw ? JSON.parse(raw) : null; } catch (e) { /* ignore */ }
      if (!checkout || String(checkout.subscriptionId) !== String(subId)) {
        UI.toast('Invalid checkout session', 'error');
        window.location.assign(this.ctx + '/agent/subscription/plans');
        return;
      }
      setText('payPlanName', checkout.planName);
      setText('payAmount', '₹' + checkout.amount);
      document.getElementById('payNowBtn')?.addEventListener('click', function () {
        AgentSubscription.openRazorpay(checkout);
      });
    },

    openRazorpay(checkout) {
      if (typeof Razorpay === 'undefined') {
        UI.toast('Payment SDK not loaded', 'error');
        return;
      }
      const self = this;
      const options = {
        key: checkout.razorpayKeyId,
        amount: Math.round(parseFloat(checkout.amount) * 100),
        currency: 'INR',
        name: 'EstateVault',
        description: checkout.planName + ' Subscription',
        order_id: checkout.razorpayOrderId,
        handler: function (response) {
          self.verifyPayment(checkout.subscriptionId, response);
        },
        theme: { color: '#c9a227' }
      };
      const rzp = new Razorpay(options);
      rzp.on('payment.failed', function () {
        UI.toast('Payment failed', 'error');
      });
      rzp.open();
    },

    async verifyPayment(subscriptionId, razorpayResponse) {
      UI.showLoader();
      try {
        await AuthAPI.request(this.ctx + '/api/agent/subscription/payment/verify', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            subscriptionId: subscriptionId,
            razorpayOrderId: razorpayResponse.razorpay_order_id,
            razorpayPaymentId: razorpayResponse.razorpay_payment_id,
            razorpaySignature: razorpayResponse.razorpay_signature
          })
        });
        sessionStorage.removeItem('subCheckout');
        UI.toast('Subscription activated!');
        window.location.assign(this.ctx + '/agent/subscription?success=1');
      } catch (e) {
        UI.toast(e.message, 'error');
      } finally {
        UI.hideLoader();
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
    } catch (e) {
      return iso;
    }
  }

  function esc(s) {
    if (s == null) return '';
    return String(s).replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
  }

  global.AgentSubscription = AgentSubscription;
})(typeof window !== 'undefined' ? window : this);
