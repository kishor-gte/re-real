/**
 * EstateVault PG Owner API & validators.
 */
(function (global) {
  'use strict';

  if (typeof V !== 'undefined') {
    V.pgoPwd = function (v) {
      return /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/.test(v);
    };
    V.pgName = function (v) {
      return String(v || '').trim().length >= 3;
    };
  }

  function pgoPasswordStrength(pwd) {
    let s = 0;
    if (pwd.length >= 8) s++;
    if (/[A-Z]/.test(pwd)) s++;
    if (/[a-z]/.test(pwd)) s++;
    if (/\d/.test(pwd)) s++;
    if (/[@$!%*?&]/.test(pwd)) s++;
    return s;
  }

  function initPgoPwdStrength(inputId, barSel) {
    const input = document.getElementById(inputId);
    const bar = document.querySelector(barSel + ' span');
    if (!input || !bar) return;
    input.addEventListener('input', function () {
      const s = pgoPasswordStrength(input.value);
      const w = ['0%', '20%', '40%', '60%', '80%', '100%'];
      const c = ['#ef4444', '#f97316', '#eab308', '#84cc16', '#22c55e'];
      bar.style.width = w[s];
      bar.style.background = c[Math.max(0, s - 1)] || '#ef4444';
    });
  }

  const PgOwnerAPI = {
    base: '',
    async request(url, options) {
      options = options || {};
      const headers = Object.assign({}, options.headers || {});
      const fullUrl = url.startsWith('http') ? url : (PgOwnerAPI.base || '') + url;
      const res = await fetch(fullUrl, Object.assign({ credentials: 'same-origin' }, options, { headers }));
      const data = await res.json().catch(function () { return {}; });
      if (!res.ok || data.success === false) {
        const err = new Error(data.message || 'Request failed');
        err.data = data.data;
        err.status = res.status;
        throw err;
      }
      return data;
    },
    checkEmail(email) {
      return this.request('/api/pg-owner/auth/check-email?email=' + encodeURIComponent(email));
    },
    checkMobile(mobile) {
      return this.request('/api/pg-owner/auth/check-mobile?mobile=' + encodeURIComponent(mobile));
    },
    checkReferral(referralCode) {
      return this.request('/api/pg-owner/auth/check-referral?referralCode=' + encodeURIComponent(referralCode));
    },
    register(formData) {
      return this.request('/api/pg-owner/auth/register', { method: 'POST', body: formData });
    },
    verifyOtp(body) {
      return this.request('/api/pg-owner/auth/verify-otp', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body)
      });
    },
    resendOtp(email) {
      return this.request('/api/pg-owner/auth/resend-otp', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email: email })
      });
    },
    login(body) {
      return this.request('/api/pg-owner/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body)
      });
    },
    logout() {
      return this.request('/api/pg-owner/auth/logout', { method: 'POST' });
    },
    forgotPassword(body) {
      return this.request('/api/pg-owner/auth/forgot-password', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body)
      });
    },
    resetPassword(body) {
      return this.request('/api/pg-owner/auth/reset-password', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body)
      });
    }
  };

  global.PgOwnerAPI = PgOwnerAPI;
  global.initPgoPwdStrength = initPgoPwdStrength;
  global.pgoPasswordStrength = pgoPasswordStrength;
})(typeof window !== 'undefined' ? window : this);
