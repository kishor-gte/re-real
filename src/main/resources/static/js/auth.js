const TokenStore = {
  get() {
    return sessionStorage.getItem('accessToken') || localStorage.getItem('accessToken');
  },
  set(token, remember) {
    sessionStorage.setItem('accessToken', token);
    if (remember) localStorage.setItem('accessToken', token);
    else localStorage.removeItem('accessToken');
  },
  clear() {
    sessionStorage.removeItem('accessToken');
    localStorage.removeItem('accessToken');
  }
};

const AuthAPI = {
  authHeaders(extra = {}) {
    const headers = { ...extra };
    const token = TokenStore.get();
    if (token) headers['Authorization'] = 'Bearer ' + token;
    return headers;
  },
  async request(url, options = {}) {
    const headers = this.authHeaders(options.headers || {});
    const res = await fetch(url, { credentials: 'same-origin', ...options, headers });
    const data = await res.json().catch(() => ({}));
    if (!res.ok) {
      const err = new Error(data.message || 'Request failed');
      err.data = data.data;
      err.status = res.status;
      throw err;
    }
    return data;
  },
  checkEmail(email) {
    return this.request(`/api/auth/check-email?email=${encodeURIComponent(email)}`);
  },
  checkMobile(mobile) {
    return this.request(`/api/auth/check-mobile?mobile=${encodeURIComponent(mobile)}`);
  },
  register(formData) {
    return this.request('/api/auth/register', { method: 'POST', body: formData });
  },
  verifyOtp(body) {
    return this.request('/api/auth/verify-otp', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(body)
    });
  },
  resendOtp(email) {
    return this.request('/api/auth/resend-otp', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email })
    });
  },
  login(body) {
    return this.request('/api/auth/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(body)
    });
  },
  logout() {
    return this.request('/api/auth/logout', { method: 'POST' });
  },
  forgotPassword(body) {
    return this.request('/api/auth/forgot-password', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(body)
    });
  },
  validateResetToken(token) {
    return this.request(`/api/auth/validate-reset-token?token=${encodeURIComponent(token)}`);
  },
  resetPassword(body) {
    return this.request('/api/auth/reset-password', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(body)
    });
  }
};

const UI = {
  showLoader() { document.getElementById('loader')?.classList.add('active'); },
  hideLoader() { document.getElementById('loader')?.classList.remove('active'); },
  toast(msg, type = 'success') {
    const c = document.getElementById('toast-container');
    if (!c) return alert(msg);
    const el = document.createElement('div');
    el.className = `toast-msg ${type}`;
    el.textContent = msg;
    c.appendChild(el);
    setTimeout(() => el.remove(), 4000);
  },
  setField(input, valid, msg) {
    if (!input) return;
    if (valid === null || valid === undefined) {
      input.classList.remove('valid', 'invalid');
    } else {
      input.classList.toggle('valid', valid === true);
      input.classList.toggle('invalid', valid === false);
    }
    const err = input.closest('.form-group')?.querySelector('.field-error');
    if (err) err.textContent = msg || '';
  }
};

const V = {
  name: v => /^[A-Za-z\s]{3,50}$/.test(v.trim()),
  email: v => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(v),
  mobile: v => /^\d{10}$/.test(v),
  pwd: v => /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/.test(v),
  pincode: v => !v || /^\d{6}$/.test(v),
  age18(dob) {
    if (!dob) return false;
    const d = new Date(dob + 'T00:00:00');
    const t = new Date();
    t.setHours(0, 0, 0, 0);
    let age = t.getFullYear() - d.getFullYear();
    const m = t.getMonth() - d.getMonth();
    if (m < 0 || (m === 0 && t.getDate() < d.getDate())) age--;
    return age >= 18;
  },
  /** Date of birth must be today or earlier (no tomorrow/future). */
  notFutureDate(dob) {
    if (!dob) return false;
    const d = new Date(dob + 'T00:00:00');
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    return d.getTime() <= today.getTime();
  },
  passwordsMatch(pwd, confirm) {
    return pwd.length > 0 && pwd === confirm;
  }
};

/** Set max date on DOB inputs to today (blocks tomorrow and future in the picker). */
function setDobMaxToday(inputId) {
  const el = document.getElementById(inputId);
  if (!el) return;
  const today = new Date();
  const y = today.getFullYear();
  const m = String(today.getMonth() + 1).padStart(2, '0');
  const d = String(today.getDate()).padStart(2, '0');
  el.setAttribute('max', `${y}-${m}-${d}`);
}

function passwordStrength(pwd) {
  let s = 0;
  if (pwd.length >= 8) s++;
  if (/[A-Z]/.test(pwd)) s++;
  if (/[a-z]/.test(pwd)) s++;
  if (/\d/.test(pwd)) s++;
  if (/[@$!%*?&]/.test(pwd)) s++;
  return s;
}

function initPwdStrength(inputId, barSel) {
  const input = document.getElementById(inputId);
  const bar = document.querySelector(barSel + ' span');
  if (!input || !bar) return;
  input.addEventListener('input', () => {
    const s = passwordStrength(input.value);
    const w = ['0%','20%','40%','60%','80%','100%'];
    const c = ['#ef4444','#f97316','#eab308','#84cc16','#22c55e'];
    bar.style.width = w[s];
    bar.style.background = c[Math.max(0, s - 1)] || '#ef4444';
  });
}

function initPwdToggle(btnId, inputId) {
  document.getElementById(btnId)?.addEventListener('click', () => {
    const i = document.getElementById(inputId);
    if (!i) return;
    i.type = i.type === 'password' ? 'text' : 'password';
  });
}

function initOtpBoxes(containerId, hiddenId, onComplete) {
  const box = document.getElementById(containerId);
  const hidden = document.getElementById(hiddenId);
  if (!box || !hidden) return;
  const inputs = [...box.querySelectorAll('input')];
  let lastAutoSubmitted = '';

  const syncHidden = () => {
    hidden.value = inputs.map(i => i.value.replace(/\D/g, '')).join('').slice(0, 6);
    if (hidden.value.length === 6 && hidden.value !== lastAutoSubmitted && typeof onComplete === 'function') {
      lastAutoSubmitted = hidden.value;
      onComplete(hidden.value);
    }
  };

  const fillFromString = (raw, startIdx = 0) => {
    const digits = String(raw).replace(/\D/g, '').slice(0, 6);
    digits.split('').forEach((ch, i) => {
      const target = inputs[startIdx + i];
      if (target) target.value = ch;
    });
    syncHidden();
    const next = inputs[Math.min(startIdx + digits.length, inputs.length - 1)];
    next?.focus();
  };

  inputs.forEach((inp, idx) => {
    inp.addEventListener('input', () => {
      const v = inp.value.replace(/\D/g, '');
      if (v.length > 1) {
        fillFromString(v, idx);
        return;
      }
      inp.value = v.slice(0, 1);
      if (inp.value && idx < inputs.length - 1) inputs[idx + 1].focus();
      syncHidden();
    });
    inp.addEventListener('keydown', e => {
      if (e.key === 'Backspace' && !inp.value && idx > 0) inputs[idx - 1].focus();
    });
    inp.addEventListener('paste', e => {
      e.preventDefault();
      const text = (e.clipboardData || window.clipboardData).getData('text');
      fillFromString(text, 0);
    });
  });

  box.addEventListener('paste', e => {
    e.preventDefault();
    const text = (e.clipboardData || window.clipboardData).getData('text');
    fillFromString(text, 0);
  });
}

/**
 * Multi-step form. stepValidators[i] runs before leaving step i; return false to block Next.
 */
function initMultiStep(formId, stepValidators) {
  const form = document.getElementById(formId);
  if (!form) return;
  const steps = [...form.querySelectorAll('.form-step')];
  const dots = [...document.querySelectorAll('.step-dot')];
  const validators = stepValidators || [];
  let cur = 0;

  const show = n => {
    steps.forEach((s, i) => s.classList.toggle('active', i === n));
    dots.forEach((d, i) => d.classList.toggle('active', i <= n));
    cur = n;
  };

  form.querySelectorAll('[data-next]').forEach(btn => {
    btn.addEventListener('click', () => {
      const validate = validators[cur];
      if (typeof validate === 'function' && validate() === false) {
        return;
      }
      if (cur < steps.length - 1) show(cur + 1);
    });
  });
  form.querySelectorAll('[data-prev]').forEach(btn => {
    btn.addEventListener('click', () => {
      if (cur > 0) show(cur - 1);
    });
  });
  show(0);
}

function initRoleCards() {
  document.querySelectorAll('.role-card').forEach(card => {
    card.addEventListener('click', () => {
      document.querySelectorAll('.role-card').forEach(c => c.classList.remove('active'));
      card.classList.add('active');
      card.querySelector('input').checked = true;
    });
  });
}
