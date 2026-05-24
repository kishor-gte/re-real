/**
 * Agent portal: analytics, earnings, referrals, messages, notifications
 */
(function (global) {
  'use strict';

  const AgentPortal = {
    ctx: '',

    init(ctx, page) {
      this.ctx = (ctx || '').replace(/\/$/, '');
      if (page === 'analytics') this.loadAnalytics();
      if (page === 'earnings') this.loadEarnings();
      if (page === 'referrals') this.loadReferrals();
      if (page === 'messages') this.loadMessages();
      if (page === 'notifications') this.initNotifications();
    },

    async loadAnalytics() {
      try {
        const res = await AuthAPI.request(this.ctx + '/api/agent/portal/analytics');
        const d = res.data;
        setText('anProps', d.totalProperties);
        setText('anViews', d.totalViews);
        setText('anLeads', d.totalLeads);
        setText('anBookings', d.totalBookings);
        setText('anCollected', d.totalCollected);
        setText('anPending', d.totalPending);
        setText('anConv', d.leadToBookingRate + '%');

        renderBarChart('anMonthlyChart', d.monthlyLeads || [], 'count');
        renderStatusList('anLeadsStatus', d.leadsByStatus || []);
        renderStatusList('anBookingsStatus', d.bookingsByStatus || []);

        const tbody = document.getElementById('anTopProps');
        if (tbody) {
          tbody.innerHTML = (d.topPropertiesByViews || []).map(function (p) {
            return '<tr><td>' + esc(p.title) + '</td><td>' + esc(p.propertyCode) + '</td><td>' + p.views + '</td><td>' + p.enquiries + '</td></tr>';
          }).join('') || '<tr><td colspan="4">No properties yet</td></tr>';
        }
      } catch (e) {
        UI.toast(e.message || 'Failed to load analytics', 'error');
      }
    },

    async loadEarnings() {
      try {
        const res = await AuthAPI.request(this.ctx + '/api/agent/portal/earnings');
        const d = res.data;
        setText('erCollected', formatMoney(d.totalCollected));
        setText('erPending', formatMoney(d.totalPending));
        setText('erTotal', formatMoney(d.totalBookingValue));
        setText('erCount', d.bookingCount);

        renderBarChartMoney('erMonthlyChart', d.monthlyBreakdown || []);

        const tbody = document.getElementById('erTableBody');
        if (tbody) {
          tbody.innerHTML = (d.rows || []).map(function (r) {
            return '<tr><td>' + esc(r.bookingCode) + '</td><td>' + esc(r.propertyTitle) + '</td><td>' + esc(r.buyerName) + '</td><td>' + esc(r.statusLabel) + '</td><td>₹' + esc(r.totalAmount) + '</td><td class="gold">₹' + esc(r.paidAmount) + '</td><td>₹' + esc(r.pendingAmount) + '</td><td>' + esc(r.bookingDate) + '</td></tr>';
          }).join('') || '<tr><td colspan="8">No earnings from bookings yet</td></tr>';
        }
      } catch (e) {
        UI.toast(e.message || 'Failed to load earnings', 'error');
      }
    },

    async loadReferrals() {
      try {
        const res = await AuthAPI.request(this.ctx + '/api/agent/portal/referrals');
        const d = res.data;
        setText('refCode', d.referralCode);
        setText('refTotal', d.totalReferrals);
        setText('refEarned', formatMoney(d.paidEarnings));
        setText('refPending', formatMoney(d.pendingEarnings));
        setText('refAll', formatMoney(d.totalEarnings));

        const tbody = document.getElementById('refTableBody');
        if (tbody) {
          tbody.innerHTML = (d.referrals || []).map(function (r) {
            return '<tr><td>' + esc(r.referredAgentName) + '<br><small class="text-muted">' + esc(r.referredAgentEmail) + '</small></td><td>' + esc(r.referredAgentCode) + '</td><td>' + esc(r.status) + '</td><td>₹' + esc(r.commissionAmount) + '</td><td><span class="status-pill ' + (r.commissionStatus === 'CREDITED' ? 'active' : 'pending') + '">' + esc(r.commissionStatus) + '</span></td><td>' + esc(r.registeredAt) + '</td></tr>';
          }).join('') || '<tr><td colspan="6">Share your referral code to invite agents</td></tr>';
        }
      } catch (e) {
        UI.toast(e.message || 'Failed to load referrals', 'error');
      }
    },

    async loadMessages() {
      try {
        const api = (typeof AgentAPI !== 'undefined') ? AgentAPI : AuthAPI;
        const res = await api.request(this.ctx + '/api/agent/enquiries');
        const list = res.data || [];
        setText('msgCount', list.length);
        const el = document.getElementById('msgList');
        if (!el) return;
        el.innerHTML = list.map(function (m) {
          const unread = m.status === 'PENDING';
          let rtc = '';
          if (global.EstateRTC && m.id && m.userId) {
            rtc = EstateRTC.cardActionsHtml({
              enquiryId: m.id,
              peerId: m.userId,
              peerName: m.userName,
              propertyTitle: m.propertyTitle
            });
          }
          return '<article class="ap-msg-card' + (unread ? ' unread' : '') + '">' +
            '<div class="d-flex justify-content-between"><span class="ap-msg-from">' + esc(m.userName) + '</span><span class="ap-notif-meta">' + esc(m.status) + ' · ' + esc(formatDate(m.createdAt)) + '</span></div>' +
            '<p class="mb-1 small" style="color:var(--agent-gold);">' + esc(m.propertyTitle || 'Property') + '</p>' +
            '<p class="ap-msg-preview">' + esc(m.message) + '</p>' +
            '<div class="small text-muted">' + esc(m.userEmail) + (m.userMobile ? ' · ' + esc(m.userMobile) : '') + '</div>' +
            (m.agentReply ? '<p class="mt-2 small"><strong>Your reply:</strong> ' + esc(m.agentReply) + '</p>' : '') +
            rtc +
            '<a href="' + esc(AgentPortal.ctx) + '/agent/enquiries" class="agent-btn-outline btn-sm mt-2">Manage in Leads</a>' +
            '</article>';
        }).join('') || '<p class="text-muted">No buyer messages yet.</p>';
        if (global.EstateRTC) {
          EstateRTC.bindCardActions(el);
        }
      } catch (e) {
        UI.toast(e.message || 'Failed to load messages', 'error');
      }
    },

    initNotifications() {
      const self = this;
      let category = 'ALL';
      document.querySelectorAll('[data-notif-filter]').forEach(function (btn) {
        btn.addEventListener('click', function () {
          document.querySelectorAll('[data-notif-filter]').forEach(function (b) { b.classList.remove('active'); });
          btn.classList.add('active');
          category = btn.getAttribute('data-notif-filter');
          self.loadNotifications(category);
        });
      });
      this.loadNotifications(category);
    },

    async loadNotifications(category) {
      try {
        const url = this.ctx + '/api/agent/portal/notifications?category=' + encodeURIComponent(category || 'ALL');
        const res = await AuthAPI.request(url);
        const d = res.data;
        setText('notifUnread', d.unreadCount);
        setText('notifTotal', d.totalCount);

        const el = document.getElementById('notifList');
        if (!el) return;
        el.innerHTML = (d.notifications || []).map(function (n) {
          const catClass = (n.category || '').toLowerCase();
          const details = (n.details || []).map(function (line) {
            return '<div><dt>' + esc(line.label) + '</dt><dd>' + esc(line.value) + '</dd></div>';
          }).join('');
          return '<article class="ap-notif-card' + (n.unread ? ' unread' : '') + '">' +
            '<div class="ap-notif-head">' +
            '<div><span class="ap-cat-pill ' + catClass + '">' + esc(n.category) + '</span><h4 class="ap-notif-title">' + esc(n.title) + '</h4></div>' +
            '<span class="ap-notif-meta">' + esc(n.createdAt) + '</span></div>' +
            '<p class="ap-notif-msg">' + esc(n.message) + '</p>' +
            '<dl class="ap-notif-details">' + details + '</dl>' +
            (n.linkUrl ? '<a href="' + esc(AgentPortal.ctx + n.linkUrl) + '" class="agent-btn-outline btn-sm mt-2">View details</a>' : '') +
            '</article>';
        }).join('') || '<p class="text-muted">No notifications yet.</p>';
      } catch (e) {
        UI.toast(e.message || 'Failed to load notifications', 'error');
      }
    }
  };

  function renderBarChart(containerId, items, valueKey) {
    const el = document.getElementById(containerId);
    if (!el || !items.length) {
      if (el) el.innerHTML = '<p class="text-muted small">No data</p>';
      return;
    }
    const max = Math.max.apply(null, items.map(function (i) { return i[valueKey] || 0; })) || 1;
    el.innerHTML = items.map(function (item) {
      const v = item[valueKey] || 0;
      const h = Math.max(4, Math.round((v / max) * 100));
      return '<div class="ap-bar-col"><div class="ap-bar" style="height:' + h + '%"></div><span class="ap-bar-label">' + esc(item.month) + '</span><span class="ap-bar-label">' + v + '</span></div>';
    }).join('');
  }

  function renderBarChartMoney(containerId, items) {
    const el = document.getElementById(containerId);
    if (!el || !items.length) {
      if (el) el.innerHTML = '<p class="text-muted small">No data</p>';
      return;
    }
    const max = Math.max.apply(null, items.map(function (i) {
      return parseFloat(i.collected) || 0;
    })) || 1;
    el.innerHTML = items.map(function (item) {
      const v = parseFloat(item.collected) || 0;
      const h = Math.max(4, Math.round((v / max) * 100));
      return '<div class="ap-bar-col"><div class="ap-bar" style="height:' + h + '%"></div><span class="ap-bar-label">' + esc(item.month) + '</span><span class="ap-bar-label">₹' + Math.round(v) + '</span></div>';
    }).join('');
  }

  function renderStatusList(containerId, items) {
    const el = document.getElementById(containerId);
    if (!el) return;
    el.innerHTML = items.map(function (s) {
      return '<div class="d-flex justify-content-between py-1 border-bottom border-secondary border-opacity-25"><span>' + esc(s.label) + '</span><strong>' + s.count + '</strong></div>';
    }).join('') || '<p class="text-muted small">—</p>';
  }

  function setText(id, val) {
    const el = document.getElementById(id);
    if (el) el.textContent = val != null ? val : '—';
  }

  function formatMoney(v) {
    if (v == null) return '₹0';
    return '₹' + v;
  }

  function formatDate(iso) {
    if (!iso) return '—';
    try {
      return new Date(iso).toLocaleString('en-IN', { day: '2-digit', month: 'short', year: 'numeric', hour: '2-digit', minute: '2-digit' });
    } catch (e) {
      return iso;
    }
  }

  function esc(s) {
    if (s == null) return '';
    return String(s).replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
  }

  global.AgentPortal = AgentPortal;
})(typeof window !== 'undefined' ? window : this);
