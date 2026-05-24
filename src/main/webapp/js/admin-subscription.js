/**
 * Admin subscription management
 */
(function (global) {
  'use strict';

  const AdminSubscription = {
    ctx: '',

    init(ctx, page) {
      this.ctx = (ctx || '').replace(/\/$/, '');
      if (page === 'plans') this.loadPlans();
      if (page === 'agents') this.loadAgentSubs(false);
      if (page === 'active') this.loadAgentSubs(true);
      if (page === 'payments') this.loadPayments();
      if (page === 'analytics') this.loadAnalytics();
      this.bindPlanForm();
    },

    api(path, options) {
      return fetch(this.ctx + path, Object.assign({ credentials: 'same-origin' }, options || {}))
        .then(function (r) { return r.json().then(function (d) { if (!r.ok || d.success === false) throw new Error(d.message || 'Request failed'); return d; }); });
    },

    loadPlans() {
      const self = this;
      this.api('/api/admin/subscriptions/plans').then(function (res) {
        const tbody = document.getElementById('adminPlansBody');
        if (!tbody) return;
        tbody.innerHTML = (res.data || []).map(function (p) {
          return '<tr><td>' + esc(p.planName) + '</td><td>' + esc(p.planCode) + '</td><td>₹' + esc(p.price) + '</td><td>' + esc(p.maxProperties) + '</td><td><span class="status-pill ' + (p.status === 'ACTIVE' ? 'active' : 'inactive') + '">' + esc(p.status) + '</span></td>' +
            '<td><button class="btn btn-sm btn-outline-light" data-edit="' + p.id + '">Edit</button> ' +
            (p.status === 'ACTIVE' ? '<button class="btn btn-sm btn-outline-warning" data-off="' + p.id + '">Disable</button>' :
              '<button class="btn btn-sm btn-outline-success" data-on="' + p.id + '">Activate</button>') + '</td></tr>';
        }).join('');
        tbody.querySelectorAll('[data-edit]').forEach(function (b) {
          b.addEventListener('click', function () { self.openEdit(res.data.find(function (x) { return String(x.id) === b.getAttribute('data-edit'); })); });
        });
        tbody.querySelectorAll('[data-off]').forEach(function (b) {
          b.addEventListener('click', function () { self.setPlanStatus(b.getAttribute('data-off'), 'INACTIVE'); });
        });
        tbody.querySelectorAll('[data-on]').forEach(function (b) {
          b.addEventListener('click', function () { self.setPlanStatus(b.getAttribute('data-on'), 'ACTIVE'); });
        });
      }).catch(function (e) { alert(e.message); });
    },

    setPlanStatus(id, status) {
      const self = this;
      this.api('/api/admin/subscriptions/plans/' + id + '/status?status=' + status, { method: 'PATCH' })
        .then(function () { self.loadPlans(); })
        .catch(function (e) { alert(e.message); });
    },

    openEdit(plan) {
      if (!plan) return;
      document.getElementById('planId').value = plan.id;
      document.getElementById('planName').value = plan.planName;
      document.getElementById('planCode').value = plan.planCode;
      document.getElementById('planPrice').value = plan.price;
      document.getElementById('planDuration').value = plan.durationDays;
      document.getElementById('planMaxProps').value = plan.maxProperties;
      document.getElementById('planFeatured').value = plan.featuredListings;
      document.getElementById('planLeads').value = plan.leadsLimit;
      document.getElementById('planDesc').value = plan.description || '';
      document.getElementById('planRecommended').checked = plan.recommended;
      document.getElementById('planAnalytics').checked = plan.analyticsAccess;
      document.getElementById('planFormTitle').textContent = 'Edit Plan';
      document.getElementById('planModal')?.classList.add('open');
    },

    bindPlanForm() {
      const self = this;
      document.getElementById('newPlanBtn')?.addEventListener('click', function () {
        document.getElementById('planForm').reset();
        document.getElementById('planId').value = '';
        document.getElementById('planFormTitle').textContent = 'Create Plan';
        document.getElementById('planModal')?.classList.add('open');
      });
      document.getElementById('closePlanModal')?.addEventListener('click', function () {
        document.getElementById('planModal')?.classList.remove('open');
      });
      document.getElementById('planForm')?.addEventListener('submit', function (e) {
        e.preventDefault();
        self.savePlan();
      });
    },

    savePlan() {
      const self = this;
      const id = document.getElementById('planId').value;
      const body = {
        planName: document.getElementById('planName').value,
        planCode: document.getElementById('planCode').value,
        price: parseFloat(document.getElementById('planPrice').value),
        durationDays: parseInt(document.getElementById('planDuration').value, 10),
        maxProperties: parseInt(document.getElementById('planMaxProps').value, 10),
        featuredListings: parseInt(document.getElementById('planFeatured').value, 10) || 0,
        leadsLimit: parseInt(document.getElementById('planLeads').value, 10) || 0,
        description: document.getElementById('planDesc').value,
        recommended: document.getElementById('planRecommended').checked,
        analyticsAccess: document.getElementById('planAnalytics').checked,
        propertyImagesLimit: 10,
        premiumBadge: false,
        whatsappLeadAccess: false,
        sortOrder: 0
      };
      const method = id ? 'PUT' : 'POST';
      const url = id ? '/api/admin/subscriptions/plans/' + id : '/api/admin/subscriptions/plans';
      this.api(url, { method: method, headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(body) })
        .then(function () {
          document.getElementById('planModal')?.classList.remove('open');
          self.loadPlans();
        })
        .catch(function (e) { alert(e.message); });
    },

    loadAgentSubs(activeOnly) {
      const path = activeOnly ? '/api/admin/subscriptions/active' : '/api/admin/subscriptions/agent-subscriptions';
      this.api(path).then(function (res) {
        const tbody = document.getElementById('agentSubsBody');
        if (!tbody) return;
        tbody.innerHTML = (res.data || []).map(function (s) {
          return '<tr><td>' + esc(s.agentName) + '<br><small>' + esc(s.agentEmail) + '</small></td><td>' + esc(s.planName) + '</td><td>' + esc(s.subscriptionStatus) + '</td><td>' + esc(s.expiryDate) + '</td><td>' + esc(s.propertiesUsed) + ' / ' + esc(s.remainingProperties) + '</td>' +
            '<td><select data-sub-status="' + s.subscriptionId + '" class="form-select form-select-sm">' +
            ['ACTIVE', 'EXPIRED', 'CANCELLED', 'PENDING'].map(function (st) {
              return '<option value="' + st + '"' + (s.subscriptionStatus === st ? ' selected' : '') + '>' + st + '</option>';
            }).join('') + '</select></td></tr>';
        }).join('');
        tbody.querySelectorAll('[data-sub-status]').forEach(function (sel) {
          sel.addEventListener('change', function () {
            AdminSubscription.api('/api/admin/subscriptions/agent-subscriptions/' + sel.getAttribute('data-sub-status') + '/status?status=' + sel.value, { method: 'PATCH' })
              .catch(function (e) { alert(e.message); });
          });
        });
      });
    },

    loadPayments() {
      const self = this;
      this.api('/api/admin/subscriptions/payments').then(function (res) {
        const tbody = document.getElementById('paymentsBody');
        if (!tbody) return;
        tbody.innerHTML = (res.data || []).map(function (p) {
          return '<tr><td>' + esc(p.invoiceNumber || p.id) + '</td><td>' + esc(p.planName) + '</td><td>₹' + esc(p.amount) + '</td><td>' + esc(p.paymentStatus) + '</td><td>' + esc(p.paymentDate) + '</td>' +
            '<td><select data-pay="' + p.id + '" class="form-select form-select-sm">' +
            ['SUCCESS', 'PENDING', 'FAILED', 'REFUNDED'].map(function (st) {
              return '<option value="' + st + '"' + (p.paymentStatus === st ? ' selected' : '') + '>' + st + '</option>';
            }).join('') + '</select></td></tr>';
        }).join('');
        tbody.querySelectorAll('[data-pay]').forEach(function (sel) {
          sel.addEventListener('change', function () {
            self.api('/api/admin/subscriptions/payments/' + sel.getAttribute('data-pay') + '/status?status=' + sel.value, { method: 'PATCH' })
              .then(function () { UI.toast && UI.toast('Payment updated'); })
              .catch(function (e) { alert(e.message); });
          });
        });
      });
    },

    loadAnalytics() {
      this.api('/api/admin/subscriptions/analytics').then(function (res) {
        const a = res.data || {};
        setText('anActiveSubs', a.activeSubscriptions);
        setText('anRevenue', '₹' + (a.totalRevenue || 0));
        setText('anSuccessPay', a.successfulPayments);
        setText('anPendingPay', a.pendingPayments);
        setText('anActivePlans', a.activePlans);
      });
    }
  };

  function setText(id, v) { const el = document.getElementById(id); if (el) el.textContent = v != null ? v : '—'; }
  function esc(s) { if (s == null) return ''; return String(s).replace(/&/g, '&amp;').replace(/</g, '&lt;'); }

  global.AdminSubscription = AdminSubscription;
})(typeof window !== 'undefined' ? window : this);
