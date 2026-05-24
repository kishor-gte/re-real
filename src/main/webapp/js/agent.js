/**
 * EstateVault Agent API & validators.
 * Load auth.js first for shared UI, V helpers, initPwdToggle, initPwdStrength, initOtpBoxes.
 */
(function (global) {
  'use strict';

  if (typeof V !== 'undefined') {
    V.agentPwd = function (v) {
      return /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/.test(v);
    };
    V.rera = function (v) {
      return /^[A-Za-z0-9][A-Za-z0-9\s\-/]{7,39}$/.test(String(v || '').trim());
    };
    V.pincode = function (v) {
      return /^\d{6}$/.test(String(v || '').trim());
    };
    V.agency = function (v) {
      return String(v || '').trim().length >= 3;
    };
  }

  function agentPasswordStrength(pwd) {
    let s = 0;
    if (pwd.length >= 8) s++;
    if (/[A-Z]/.test(pwd)) s++;
    if (/[a-z]/.test(pwd)) s++;
    if (/\d/.test(pwd)) s++;
    if (/[@$!%*?&]/.test(pwd)) s++;
    return s;
  }

  function initAgentPwdStrength(inputId, barSel) {
    const input = document.getElementById(inputId);
    const bar = document.querySelector(barSel + ' span');
    if (!input || !bar) return;
    input.addEventListener('input', function () {
      const s = agentPasswordStrength(input.value);
      const w = ['0%', '20%', '40%', '60%', '80%', '100%'];
      const c = ['#ef4444', '#f97316', '#eab308', '#84cc16', '#22c55e'];
      bar.style.width = w[s];
      bar.style.background = c[Math.max(0, s - 1)] || '#ef4444';
    });
  }

  const AgentAPI = {
    base: '',
    async request(url, options) {
      options = options || {};
      const headers = Object.assign({}, options.headers || {});
      const fullUrl = url.startsWith('http') ? url : (AgentAPI.base || '') + url;
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
      return this.request('/api/agent/auth/check-email?email=' + encodeURIComponent(email));
    },
    checkMobile(mobile) {
      return this.request('/api/agent/auth/check-mobile?mobile=' + encodeURIComponent(mobile));
    },
    checkRera(reraNumber) {
      return this.request('/api/agent/auth/check-rera?reraNumber=' + encodeURIComponent(reraNumber));
    },
    checkReferral(referralCode) {
      return this.request('/api/agent/auth/check-referral?referralCode=' + encodeURIComponent(referralCode));
    },
    register(formData) {
      return this.request('/api/agent/auth/register', { method: 'POST', body: formData });
    },
    verifyOtp(body) {
      return this.request('/api/agent/auth/verify-otp', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body)
      });
    },
    resendOtp(email) {
      return this.request('/api/agent/auth/resend-otp', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email: email })
      });
    },
    login(body) {
      return this.request('/api/agent/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body)
      });
    },
    logout() {
      return this.request('/api/agent/auth/logout', { method: 'POST' });
    },
    forgotPassword(body) {
      return this.request('/api/agent/auth/forgot-password', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body)
      });
    },
    resetPassword(body) {
      return this.request('/api/agent/auth/reset-password', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body)
      });
    }
  };

  global.AgentAPI = AgentAPI;
  global.initAgentPwdStrength = initAgentPwdStrength;
  global.agentPasswordStrength = agentPasswordStrength;
})(typeof window !== 'undefined' ? window : this);
