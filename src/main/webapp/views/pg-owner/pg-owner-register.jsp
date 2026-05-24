<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="../includes/init.jsp" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>PG Owner Register | EstateVault</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${ctx}/css/pg-owner.css">
    <link rel="stylesheet" href="${ctx}/css/pg-owner-pages.css">
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
</head>
<body class="pgo-body pgo-bg-grid pgo-page">
<%@ include file="../includes/pg-owner-particles.jsp" %>
<nav class="pgo-nav">
    <a class="pgo-brand" href="${ctx}/"><span class="pgo-brand-badge">PG Owner</span> EstateVault</a>
    <a href="${ctx}/pg-owner/login" class="pgo-btn-outline">Login</a>
</nav>
<div id="toast-container" class="toast-container"></div>
<div id="loader" class="loader-overlay"><div class="spinner"></div></div>

<main class="pgo-split wide-form">
    <section class="pgo-visual" aria-hidden="true">
        <div class="pgo-visual-bg" style="background-image:url('https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?w=1200&q=80');"></div>
        <div class="pgo-visual-overlay"></div>
        <div class="pgo-visual-inner">
            <div>
                <span class="pgo-visual-tag">PG &amp; Hostel Partners</span>
                <h1>List Your <span>PG</span> Professionally</h1>
                <p class="pgo-visual-desc">Register as a verified PG owner. OTP verification and admin approval keep the platform trusted.</p>
                <div class="pgo-visual-features">
                    <span>&#128231; Email OTP</span>
                    <span>&#128274; Admin verified</span>
                    <span>&#128100; Owner profile</span>
                </div>
            </div>
        </div>
    </section>
    <section class="pgo-form-panel">
        <div class="pgo-glass pgo-glass-neon pgo-card-animate">
            <div class="pgo-form-header">
                <div class="pgo-icon-wrap">&#127968;</div>
                <h2>PG Owner Registration</h2>
                <p>Complete your owner profile — verify with OTP after submit</p>
            </div>
            <form id="pgoRegisterForm" enctype="multipart/form-data" class="pgo-form-grid" novalidate>
                <div class="pgo-form-group">
                    <input class="pgo-form-control form-control" id="fullName" name="fullName" placeholder=" " required>
                    <label class="pgo-floating-label" for="fullName">Full Name *</label>
                    <div class="pgo-field-error field-error"></div>
                </div>
                <div class="pgo-form-row-2">
                    <div class="pgo-form-group">
                        <input class="pgo-form-control form-control" id="email" name="email" type="email" placeholder=" " required>
                        <label class="pgo-floating-label" for="email">Email Address *</label>
                        <div class="pgo-field-error field-error"></div>
                    </div>
                    <div class="pgo-form-group">
                        <input class="pgo-form-control form-control" id="mobile" name="mobile" maxlength="10" placeholder=" " required>
                        <label class="pgo-floating-label" for="mobile">Mobile (10 digits) *</label>
                        <div class="pgo-field-error field-error"></div>
                    </div>
                </div>
                <div class="pgo-form-row-2">
                    <div class="pgo-form-group">
                        <input class="pgo-form-control form-control" id="businessRegistrationNumber" name="businessRegistrationNumber" placeholder=" " maxlength="50">
                        <label class="pgo-floating-label" for="businessRegistrationNumber">Business Reg. No. (optional)</label>
                    </div>
                    <div class="pgo-form-group">
                        <input class="pgo-form-control form-control" id="experience" name="experience" type="number" min="0" max="60" placeholder=" " required>
                        <label class="pgo-floating-label" for="experience">Experience (Years) *</label>
                    </div>
                </div>
                <div class="pgo-form-row-2">
                    <div class="pgo-form-group">
                        <input class="pgo-form-control form-control" id="city" name="city" placeholder=" " required>
                        <label class="pgo-floating-label" for="city">City *</label>
                    </div>
                    <div class="pgo-form-group">
                        <input class="pgo-form-control form-control" id="state" name="state" placeholder=" " required>
                        <label class="pgo-floating-label" for="state">State *</label>
                    </div>
                </div>
                <div class="pgo-form-group" style="max-width:200px;">
                    <input class="pgo-form-control form-control" id="pincode" name="pincode" maxlength="6" placeholder=" " required>
                    <label class="pgo-floating-label" for="pincode">Pincode *</label>
                    <div class="pgo-field-error field-error"></div>
                </div>
                <div class="pgo-form-row-2">
                    <div class="pgo-form-group pgo-pwd-wrap">
                        <input class="pgo-form-control form-control" id="password" name="password" type="password" placeholder=" " required>
                        <label class="pgo-floating-label" for="password">Password (8+ chars) *</label>
                        <button type="button" class="pgo-pwd-toggle pwd-toggle" id="togglePwd1">&#128065;</button>
                        <div class="pgo-strength-bar strength-bar" id="strPgo"><span></span></div>
                        <div class="pgo-field-error field-error"></div>
                    </div>
                    <div class="pgo-form-group pgo-pwd-wrap">
                        <input class="pgo-form-control form-control" id="confirmPassword" name="confirmPassword" type="password" placeholder=" " required>
                        <label class="pgo-floating-label" for="confirmPassword">Confirm Password *</label>
                        <button type="button" class="pgo-pwd-toggle pwd-toggle" id="togglePwd2">&#128065;</button>
                        <div class="pgo-field-error field-error"></div>
                    </div>
                </div>
                <div class="pgo-form-group">
                    <label class="small" style="color:var(--pgo-muted);">Profile Photo (optional)</label>
                    <input type="file" class="pgo-form-control form-control mt-1" id="profilePhoto" name="profilePhoto" accept="image/*">
                    <img id="profilePreview" class="pgo-profile-preview profile-preview" alt="Preview">
                </div>
                <div class="pgo-form-group">
                    <label class="small" style="color:var(--pgo-muted);">Government ID Proof *</label>
                    <input type="file" class="pgo-form-control form-control mt-1" id="governmentId" name="governmentId"
                           accept=".pdf,.doc,.docx,image/*,application/pdf" required>
                    <p class="small mt-1 mb-0" style="color:var(--pgo-muted);">PDF or images — max 20 MB</p>
                </div>
                <div class="pgo-form-group">
                    <input class="pgo-form-control form-control" id="referralCodeUsed" name="referralCodeUsed" placeholder=" " maxlength="12">
                    <label class="pgo-floating-label" for="referralCodeUsed">Referral Code (optional)</label>
                    <div class="pgo-field-error field-error"></div>
                </div>
                <div class="pgo-form-check">
                    <input type="checkbox" id="termsAccepted" name="termsAccepted" value="true">
                    <label for="termsAccepted">I accept Terms &amp; Conditions and confirm details are accurate *</label>
                </div>
                <button type="submit" class="pgo-btn-gold w-100">Register &amp; Send OTP</button>
            </form>
            <p class="text-center mt-3 mb-0" style="color:var(--pgo-muted);">
                Already registered? <a href="${ctx}/pg-owner/login" class="pgo-link">PG Owner login</a>
            </p>
        </div>
    </section>
</main>
<script src="${ctx}/js/auth.js"></script>
<script src="${ctx}/js/pg-owner.js"></script>
<script>
PgOwnerAPI.base = '${ctx}';
initPwdToggle('togglePwd1', 'password');
initPwdToggle('togglePwd2', 'confirmPassword');
initPgoPwdStrength('password', '#strPgo');

document.getElementById('profilePhoto')?.addEventListener('change', function () {
  const f = this.files[0];
  const img = document.getElementById('profilePreview');
  if (f && img) { img.src = URL.createObjectURL(f); img.style.display = 'block'; }
});

document.getElementById('mobile')?.addEventListener('input', function () {
  this.value = this.value.replace(/\D/g, '').slice(0, 10);
});
document.getElementById('pincode')?.addEventListener('input', function () {
  this.value = this.value.replace(/\D/g, '').slice(0, 6);
});

let emailTimer, mobileTimer, refTimer;
document.getElementById('email')?.addEventListener('input', function () {
  clearTimeout(emailTimer);
  emailTimer = setTimeout(async function () {
    if (!V.email(this.value)) return UI.setField(this, false, 'Invalid email format');
    try {
      const r = await PgOwnerAPI.checkEmail(this.value.trim());
      UI.setField(this, r.data, r.data ? '' : 'Email already registered');
    } catch (e) {}
  }.bind(this), 500);
});
document.getElementById('mobile')?.addEventListener('input', function () {
  clearTimeout(mobileTimer);
  mobileTimer = setTimeout(async function () {
    if (!V.mobile(this.value)) return UI.setField(this, false, 'Enter valid 10-digit mobile');
    try {
      const r = await PgOwnerAPI.checkMobile(this.value);
      UI.setField(this, r.data, r.data ? '' : 'Mobile already registered');
    } catch (e) {}
  }.bind(this), 500);
});
document.getElementById('referralCodeUsed')?.addEventListener('input', function () {
  clearTimeout(refTimer);
  if (!this.value.trim()) { UI.setField(this, null, ''); return; }
  refTimer = setTimeout(async function () {
    try {
      const r = await PgOwnerAPI.checkReferral(this.value.trim());
      UI.setField(this, r.data, r.data ? '' : 'Invalid referral code');
    } catch (e) {}
  }.bind(this), 500);
});

function checkPwdMatch() {
  const pwd = document.getElementById('password').value;
  const c = document.getElementById('confirmPassword');
  if (!c.value) { UI.setField(c, null, ''); return; }
  UI.setField(c, pwd === c.value, pwd === c.value ? '' : 'Passwords do not match');
}
document.getElementById('password')?.addEventListener('input', checkPwdMatch);
document.getElementById('confirmPassword')?.addEventListener('input', checkPwdMatch);

document.getElementById('pgoRegisterForm').addEventListener('submit', async function (e) {
  e.preventDefault();
  const nameEl = document.getElementById('fullName');
  const emailEl = document.getElementById('email');
  const mobileEl = document.getElementById('mobile');
  const pwd = document.getElementById('password').value;
  const cpwd = document.getElementById('confirmPassword').value;

  if (!V.name(nameEl.value)) { UI.toast('Full name: letters only, 3+ characters', 'error'); return; }
  if (!V.email(emailEl.value) || emailEl.classList.contains('invalid')) { UI.toast('Check email', 'error'); return; }
  if (!V.mobile(mobileEl.value) || mobileEl.classList.contains('invalid')) { UI.toast('Check mobile', 'error'); return; }
  if (!V.pincode(document.getElementById('pincode').value)) { UI.toast('Pincode must be 6 digits', 'error'); return; }
  if (!V.pgoPwd(pwd)) { UI.toast('Password: 8+ chars, upper, lower, digit & special', 'error'); return; }
  if (pwd !== cpwd) { UI.toast('Passwords do not match', 'error'); return; }
  if (!document.getElementById('governmentId').files[0]) { UI.toast('Government ID is required', 'error'); return; }
  if (!document.getElementById('termsAccepted').checked) { UI.toast('Accept Terms & Conditions', 'error'); return; }

  const fd = new FormData(e.target);
  fd.set('termsAccepted', document.getElementById('termsAccepted').checked ? 'true' : 'false');
  const ctx = '${ctx}';
  const email = emailEl.value.trim().toLowerCase();

  UI.showLoader();
  try {
    const r = await PgOwnerAPI.register(fd);
    sessionStorage.setItem('pendingPgOwnerEmail', email);
    UI.toast(r.message || 'OTP sent to your email. Check your inbox and spam folder.', 'success');
    window.location.assign(ctx + '/pg-owner/otp-verification?email=' + encodeURIComponent(email));
  } catch (err) {
    UI.toast(err.message || 'Registration failed', 'error');
  } finally {
    UI.hideLoader();
  }
});
</script>
<%@ include file="../includes/portal-scripts.jsp" %>
</body>
</html>
