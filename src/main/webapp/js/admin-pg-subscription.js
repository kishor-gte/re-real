(function (global) {
  'use strict';

  const AdminPgSubscription = {
    ctx: '',

    init(ctx, page) {
      this.ctx = (ctx || '').replace(/\/$/, '');
      if (page === 'plans') this.loadPlans();
      if (page === 'owners') this.loadOwnerSubs(false);
      if (page === 'active') this.loadOwnerSubs(true);
      if (page === 'payments') this.loadPayments();
      if (page === 'analytics') this.loadAnalytics();
      this.bindPlanForm();
    },

    api(path, options) {
      return fetch(this.ctx + path, Object.assign({ credentials: 'same-origin' }, options || {}))
        .then(function (r) {
          return r.json().then(function (d) {
            if (!r.ok || d.success === false) throw new Error(d.message || 'Request failed');
            return d;
          });
        });
    },

    loadPlans() {
      const self = this;
      this.api('/api/admin/pg-subscriptions/plans').then(function (res) {
        const tbody = document.getElementById('adminPlansBody');
        if (!tbody) return;
        tbody.innerHTML = (res.data || []).map(function (p) {
          return '<tr><td>' + esc(p.planName) + '</td><td>' + esc(p.planCode) + '</td><td>₹' + esc(p.price) + '</td><td>' + esc(p.maxPgListings) + '</td><td>' + esc(p.maxRoomListings) + '</td><td><span class="status-pill ' + (p.status === 'ACTIVE' ? 'active' : 'inactive') + '">' + esc(p.status) + '</span></td>' +
            '<td><button class="btn btn-sm btn-outline-light" data-edit="' + p.id + '">Edit</button> ' +
            (p.status === 'ACTIVE' ? '<button class="btn btn-sm btn-outline-warning" data-off="' + p.id + '">Disable</button>' :
              '<button class="btn btn-sm btn-outline-success" data-on="' + p.id + '">Activate</button>') + '</td></tr>';
        }).join('');
        tbody.querySelectorAll('[data-edit]').forEach(function (b) {
          b.addEventListener('click', function () {
            self.openEdit(res.data.find(function (x) { return String(x.id) === b.getAttribute('data-edit'); }));
          });
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
      this.api('/api/admin/pg-subscriptions/plans/' + id + '/status?status=' + status, { method: 'PATCH' })
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
      document.getElementById('planMaxPg').value = plan.maxPgListings;
      document.getElementById('planMaxRooms').value = plan.maxRoomListings;
      document.getElementById('planFeatured').value = plan.featuredPgCount;
      document.getElementById('planDesc').value = plan.description || '';
      document.getElementById('planRecommended').checked = plan.recommended;
      document.getElementById('planAnalytics').checked = plan.tenantAnalyticsAccess;
      document.getElementById('planBedMgmt').checked = plan.bedManagementAccess;
      document.getElementById('planPriority').checked = plan.prioritySupport;
      document.getElementById('planPremium').checked = plan.premiumBadge;
      document.getElementById('planFormTitle').textContent = 'Edit Plan';
      document.getElementById('planModal')?.classList.add('open');
    },

    bindPlanForm() {
      const self = this;
      document.getElementById('newPlanBtn')?.addEventListener('click', function () {
        document.getElementById('planForm').reset();
        document.getElementById('planId').value = '';
        document.getElementById('planFormTitle').textContent = 'Create PG Plan';
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
        maxPgListings: parseInt(document.getElementById('planMaxPg').value, 10),
        maxRoomListings: parseInt(document.getElementById('planMaxRooms').value, 10),
        featuredPgCount: parseInt(document.getElementById('planFeatured').value, 10) || 0,
        description: document.getElementById('planDesc').value,
        recommended: document.getElementById('planRecommended').checked,
        tenantAnalyticsAccess: document.getElementById('planAnalytics').checked,
        bedManagementAccess: document.getElementById('planBedMgmt').checked,
        prioritySupport: document.getElementById('planPriority').checked,
        premiumBadge: document.getElementById('planPremium').checked,
        sortOrder: 0
      };
      const url = id ? '/api/admin/pg-subscriptions/plans/' + id : '/api/admin/pg-subscriptions/plans';
      const method = id ? 'PUT' : 'POST';
      this.api(url, { method: method, headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(body) })
        .then(function () {
          document.getElementById('planModal')?.classList.remove('open');
          self.loadPlans();
        })
        .catch(function (e) { alert(e.message); });
    },

    loadOwnerSubs(activeOnly) {
      const path = activeOnly ? '/api/admin/pg-subscriptions/active' : '/api/admin/pg-subscriptions/owner-subscriptions';
      this.api(path).then(function (res) {
        const tbody = document.getElementById('adminSubsBody');
        if (!tbody) return;
        tbody.innerHTML = (res.data || []).map(function (s) {
          return '<tr><td>' + esc(s.pgOwnerName) + '</td><td>' + esc(s.pgOwnerCode) + '</td><td>' + esc(s.planName) + '</td><td>' + esc(s.pgUsed) + '/' + esc(s.remainingPg) + '</td><td>' + esc(s.subscriptionStatus) + '</td><td>' + esc(s.expiryDate) + '</td></tr>';
        }).join('') || '<tr><td colspan="6">No records</td></tr>';
      }).catch(function (e) { alert(e.message); });
    },

    loadPayments() {
      this.api('/api/admin/pg-subscriptions/payments').then(function (res) {
        const tbody = document.getElementById('adminPaymentsBody');
        if (!tbody) return;
        tbody.innerHTML = (res.data || []).map(function (p) {
          return '<tr><td>' + esc(p.invoiceNumber) + '</td><td>' + esc(p.planName) + '</td><td>₹' + esc(p.amount) + '</td><td>' + esc(p.paymentStatus) + '</td><td>' + esc(p.paymentDate) + '</td></tr>';
        }).join('') || '<tr><td colspan="5">No payments</td></tr>';
      }).catch(function (e) { alert(e.message); });
    },

    loadAnalytics() {
      this.api('/api/admin/pg-subscriptions/analytics').then(function (res) {
        const a = res.data || {};
        setText('pgAnActive', a.activeSubscriptions);
        setText('pgAnExpired', a.expiredSubscriptions);
        setText('pgAnRevenue', a.totalRevenue != null ? '₹' + a.totalRevenue : '—');
        setText('pgAnPayments', a.successfulPayments);
        setText('pgAnPlans', a.activePlans);
      }).catch(function (e) { alert(e.message); });
    }
  };

  function setText(id, val) {
    const el = document.getElementById(id);
    if (el) el.textContent = val != null ? val : '—';
  }

  function esc(s) {
    if (s == null) return '';
    return String(s).replace(/&/g, '&amp;').replace(/</g, '&lt;');
  }

  global.AdminPgSubscription = AdminPgSubscription;
})(typeof window !== 'undefined' ? window : this);
