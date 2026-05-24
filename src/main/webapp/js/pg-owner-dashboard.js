(function (global) {
  'use strict';

  const PgOwnerDashboard = {
    ctx: '',

    init(ctx) {
      this.ctx = (ctx || '').replace(/\/$/, '');
      this.loadStats();
    },

    async loadStats() {
      try {
        const res = await AuthAPI.request(this.ctx + '/api/pg-owner/subscription/dashboard-stats');
        const s = res.data;
        if (!s) return;
        setText('dashTotalPg', s.totalPgListings);
        setText('dashFreeLeft', s.remainingFreeListings);
        setText('dashActivePlan', s.activeSubscriptionPlan || 'Free');
        setText('dashExpiry', s.subscriptionExpiry ? formatDate(s.subscriptionExpiry) : '—');
        setText('dashTotalBeds', s.totalBedsAvailable);
        setText('dashOccupiedBeds', s.occupiedBeds);
        setText('dashEarnings', s.monthlyEarnings != null ? '₹' + Number(s.monthlyEarnings).toLocaleString('en-IN') : '—');
        setText('dashInquiries', s.pgInquiries);
        const upgradeBtn = document.getElementById('dashUpgradeBtn');
        if (upgradeBtn) {
          upgradeBtn.style.display = s.showUpgradeButton ? 'inline-block' : 'none';
        }
        if (!s.canPostPg) {
          const banner = document.getElementById('dashLimitBanner');
          if (banner) banner.style.display = 'block';
        }
      } catch (e) {
        console.warn('Dashboard stats:', e.message);
      }
    }
  };

  function setText(id, val) {
    const el = document.getElementById(id);
    if (el) el.textContent = val != null ? val : '—';
  }

  function formatDate(iso) {
    try {
      return new Date(iso).toLocaleDateString('en-IN', { day: 'numeric', month: 'short', year: 'numeric' });
    } catch (e) { return '—'; }
  }

  global.PgOwnerDashboard = PgOwnerDashboard;
})(typeof window !== 'undefined' ? window : this);
