/**
 * EstateVault Admin API & validators.
 * Load auth.js first for shared UI, V helpers, initPwdToggle, initPwdStrength, initOtpBoxes.
 */
(function (global) {
  'use strict';

  if (typeof V !== 'undefined') {
    V.adminPwd = function (v) {
      return /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{10,}$/.test(v);
    };
  }

  function adminPasswordStrength(pwd) {
    let s = 0;
    if (pwd.length >= 10) s++;
    if (/[A-Z]/.test(pwd)) s++;
    if (/[a-z]/.test(pwd)) s++;
    if (/\d/.test(pwd)) s++;
    if (/[@$!%*?&]/.test(pwd)) s++;
    return s;
  }

  function initAdminPwdStrength(inputId, barSel) {
    const input = document.getElementById(inputId);
    const bar = document.querySelector(barSel + ' span');
    if (!input || !bar) return;
    input.addEventListener('input', function () {
      const s = adminPasswordStrength(input.value);
      const w = ['0%', '20%', '40%', '60%', '80%', '100%'];
      const c = ['#ef4444', '#f97316', '#eab308', '#84cc16', '#22c55e'];
      bar.style.width = w[s];
      bar.style.background = c[Math.max(0, s - 1)] || '#ef4444';
    });
  }

  const AdminAPI = {
    base: '',
    async request(url, options) {
      options = options || {};
      const headers = Object.assign({}, options.headers || {});
      const fullUrl = url.startsWith('http') ? url : (AdminAPI.base || '') + url;
      const res = await fetch(fullUrl, Object.assign({ credentials: 'same-origin' }, options, { headers }));
      const data = await res.json().catch(function () { return {}; });
      if (!res.ok) {
        const err = new Error(data.message || 'Request failed');
        err.data = data.data;
        err.status = res.status;
        throw err;
      }
      return data;
    },
    checkEmail(email) {
      return this.request('/api/admin/auth/check-email?email=' + encodeURIComponent(email));
    },
    checkMobile(mobile) {
      return this.request('/api/admin/auth/check-mobile?mobile=' + encodeURIComponent(mobile));
    },
    register(formData) {
      return this.request('/api/admin/auth/register', { method: 'POST', body: formData });
    },
    verifyOtp(body) {
      return this.request('/api/admin/auth/verify-otp', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body)
      });
    },
    resendOtp(officialEmail) {
      return this.request('/api/admin/auth/resend-otp', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ officialEmail: officialEmail })
      });
    },
    login(body) {
      return this.request('/api/admin/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body)
      });
    },
    logout() {
      return this.request('/api/admin/auth/logout', { method: 'POST' });
    },
    forgotPassword(body) {
      return this.request('/api/admin/auth/forgot-password', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body)
      });
    },
    resetPassword(body) {
      return this.request('/api/admin/auth/reset-password', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body)
      });
    }
  };

  const AdminAgentAPI = {
    base: '',
    async request(url, options) {
      options = options || {};
      const fullUrl = url.startsWith('http') ? url : (AdminAgentAPI.base || '') + url;
      const res = await fetch(fullUrl, Object.assign({ credentials: 'same-origin' }, options, {
        headers: Object.assign({ 'Content-Type': 'application/json' }, options.headers || {})
      }));
      const data = await res.json().catch(function () { return {}; });
      if (!res.ok || data.success === false) {
        const err = new Error(data.message || 'Request failed');
        err.data = data.data;
        throw err;
      }
      return data;
    },
    list(status) {
      return this.request('/api/admin/agents?status=' + encodeURIComponent(status || 'PENDING'));
    },
    pendingCount() {
      return this.request('/api/admin/agents/pending-count');
    },
    get(id) {
      return this.request('/api/admin/agents/' + id);
    },
    approve(id) {
      return this.request('/api/admin/agents/' + id + '/approve', { method: 'POST', body: '{}' });
    },
    reject(id, reason) {
      return this.request('/api/admin/agents/' + id + '/reject', {
        method: 'POST',
        body: JSON.stringify({ reason: reason })
      });
    },
    suspend(id) {
      return this.request('/api/admin/agents/' + id + '/suspend', { method: 'POST', body: '{}' });
    },
    reactivate(id) {
      return this.request('/api/admin/agents/' + id + '/reactivate', { method: 'POST', body: '{}' });
    }
  };

  global.AdminAPI = AdminAPI;
  global.AdminAgentAPI = AdminAgentAPI;
  global.initAdminPwdStrength = initAdminPwdStrength;
  global.adminPasswordStrength = adminPasswordStrength;
})(typeof window !== 'undefined' ? window : this);
