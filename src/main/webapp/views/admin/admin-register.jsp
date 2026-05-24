<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="../includes/init.jsp" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Register | EstateVault</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${ctx}/css/admin.css">
    <link rel="stylesheet" href="${ctx}/css/admin-pages.css">
</head>
<body class="admin-body admin-bg-grid admin-page">
<%@ include file="../includes/admin-particles.jsp" %>
<nav class="admin-nav">
    <a class="admin-brand" href="${ctx}/"><span class="admin-brand-badge">Admin</span> EstateVault</a>
    <a href="${ctx}/admin/login" class="admin-btn-outline">Login</a>
</nav>
<div id="toast-container" class="toast-container"></div>
<div id="loader" class="loader-overlay"><div class="spinner"></div></div>

<main class="admin-split wide-form">
    <section class="admin-visual" aria-hidden="true">
        <div class="admin-visual-bg" style="background-image:url('https://images.unsplash.com/photo-1497366216548-37526070297c?w=1200&q=80');"></div>
        <div class="admin-visual-overlay"></div>
        <div class="admin-visual-scanline"></div>
        <div class="admin-visual-inner">
            <div>
                <span class="admin-visual-tag">Staff Onboarding</span>
                <h1>Join The <span>Admin</span> Team</h1>
                <p class="admin-visual-desc">Create the platform administrator account. Official email OTP verification is required before first login.</p>
                <div class="admin-visual-features">
                    <span>&#128231; Official email OTP</span>
                    <span>&#128274; 10+ char password</span>
                    <span>&#128737; Single admin account</span>
                </div>
            </div>
            <div class="admin-visual-bottom">
                <div class="admin-stat-pill"><strong>1 Admin</strong><small>Full platform control</small></div>
            </div>
        </div>
    </section>

    <section class="admin-form-panel">
        <div class="admin-glass admin-glass-neon admin-card-animate">
            <div class="admin-form-header">
                <div class="admin-icon-wrap" aria-hidden="true">&#128188;</div>
                <h2>Admin Registration</h2>
                <p>Complete all fields — verify with OTP after submit</p>
            </div>

            <form id="adminRegisterForm" enctype="multipart/form-data" class="admin-form-grid" novalidate>
                <div class="admin-form-group">
                    <input class="admin-form-control form-control" id="fullName" name="fullName" placeholder=" " required>
                    <label class="admin-floating-label" for="fullName">Full Name *</label>
                    <div class="admin-field-error field-error"></div>
                </div>
                <div class="admin-form-row-2">
                    <div class="admin-form-group">
                        <input class="admin-form-control form-control" id="officialEmail" name="officialEmail" type="email" placeholder=" " required>
                        <label class="admin-floating-label" for="officialEmail">Official Email *</label>
                        <div class="admin-field-error field-error"></div>
                    </div>
                    <div class="admin-form-group">
                        <input class="admin-form-control form-control" id="mobile" name="mobile" maxlength="10" placeholder=" " required>
                        <label class="admin-floating-label" for="mobile">Mobile (10 digits) *</label>
                        <div class="admin-field-error field-error"></div>
                    </div>
                </div>
                <div class="admin-form-row-2">
                    <div class="admin-form-group admin-pwd-wrap">
                        <input class="admin-form-control form-control" id="password" name="password" type="password" placeholder=" " required>
                        <label class="admin-floating-label" for="password">Password (10+ chars) *</label>
                        <button type="button" class="admin-pwd-toggle pwd-toggle" id="togglePwd1">&#128065;</button>
                        <div class="admin-strength-bar strength-bar" id="strAdmin"><span></span></div>
                        <div class="admin-field-error field-error"></div>
                    </div>
                    <div class="admin-form-group admin-pwd-wrap">
                        <input class="admin-form-control form-control" id="confirmPassword" name="confirmPassword" type="password" placeholder=" " required>
                        <label class="admin-floating-label" for="confirmPassword">Confirm Password *</label>
                        <button type="button" class="admin-pwd-toggle pwd-toggle" id="togglePwd2">&#128065;</button>
                        <div class="admin-field-error field-error"></div>
                    </div>
                </div>
                <div class="admin-form-row-2">
                    <div class="admin-form-group">
                        <select class="admin-form-select form-select" id="securityQuestion" name="securityQuestion" required>
                            <option value="" disabled selected></option>
                            <option value="What was the name of your first school?">What was the name of your first school?</option>
                            <option value="What city were you born in?">What city were you born in?</option>
                            <option value="What is your mother's maiden name?">What is your mother's maiden name?</option>
                            <option value="What was your first pet's name?">What was your first pet's name?</option>
                            <option value="What was the model of your first car?">What was the model of your first car?</option>
                        </select>
                        <label class="admin-floating-label" for="securityQuestion">Security Question *</label>
                    </div>
                    <div class="admin-form-group">
                        <input class="admin-form-control form-control" id="securityAnswer" name="securityAnswer" placeholder=" " required>
                        <label class="admin-floating-label" for="securityAnswer">Security Answer *</label>
                        <div class="admin-field-error field-error"></div>
                    </div>
                </div>
                <div class="admin-form-group">
                    <label class="small" style="color:var(--admin-muted);">Profile Image (optional)</label>
                    <input type="file" class="admin-form-control form-control mt-1" id="profileImage" name="profileImage" accept="image/*">
                    <img id="profilePreview" class="admin-profile-preview profile-preview" alt="Preview">
                </div>
                <div class="admin-form-check">
                    <input type="checkbox" id="termsAccepted" name="termsAccepted" value="true">
                    <label for="termsAccepted">I accept the Admin Terms &amp; Conditions and authorize background verification *</label>
                </div>
                <button type="submit" class="admin-btn-gold w-100">Register &amp; Send OTP</button>
            </form>
            <p class="text-center mt-3 mb-0" style="color:var(--admin-muted);">
                Already registered?
                <a href="${ctx}/admin/login" class="admin-link">Admin login</a>
            </p>
        </div>
    </section>
</main>
<script src="${ctx}/js/auth.js"></script>
<script src="${ctx}/js/admin.js"></script>
<script>
AdminAPI.base = '${ctx}';
initPwdToggle('togglePwd1', 'password');
initPwdToggle('togglePwd2', 'confirmPassword');
initAdminPwdStrength('password', '#strAdmin');

const profileInput = document.getElementById('profileImage');
const preview = document.getElementById('profilePreview');
profileInput?.addEventListener('change', function () {
  const f = profileInput.files[0];
  if (f) {
    preview.src = URL.createObjectURL(f);
    preview.style.display = 'block';
  }
});

document.getElementById('mobile')?.addEventListener('input', function () {
  this.value = this.value.replace(/\D/g, '').slice(0, 10);
});

let emailTimer, mobileTimer;

document.getElementById('officialEmail')?.addEventListener('input', function () {
  clearTimeout(emailTimer);
  emailTimer = setTimeout(async function () {
    if (!V.email(this.value)) return UI.setField(this, false, 'Invalid email format');
    try {
      const r = await AdminAPI.checkEmail(this.value.trim());
      UI.setField(this, r.data, r.data ? '' : 'Official email already registered');
    } catch (e) {}
  }.bind(this), 500);
});

document.getElementById('mobile')?.addEventListener('input', function () {
  clearTimeout(mobileTimer);
  mobileTimer = setTimeout(async function () {
    if (!V.mobile(this.value)) return UI.setField(this, false, 'Enter valid 10-digit mobile');
    try {
      const r = await AdminAPI.checkMobile(this.value);
      UI.setField(this, r.data, r.data ? '' : 'Mobile already registered');
    } catch (e) {}
  }.bind(this), 500);
});

function checkAdminPasswordMatch() {
  const pwdEl = document.getElementById('password');
  const confirmEl = document.getElementById('confirmPassword');
  if (!confirmEl.value) { UI.setField(confirmEl, null, ''); return; }
  if (V.passwordsMatch(pwdEl.value, confirmEl.value)) {
    UI.setField(confirmEl, true, '');
  } else {
    UI.setField(confirmEl, false, 'Passwords do not match');
  }
}
document.getElementById('password')?.addEventListener('input', checkAdminPasswordMatch);
document.getElementById('confirmPassword')?.addEventListener('input', checkAdminPasswordMatch);

document.getElementById('adminRegisterForm').addEventListener('submit', async function (e) {
  e.preventDefault();
  const pwd = document.getElementById('password').value;
  const cpwd = document.getElementById('confirmPassword').value;
  const nameEl = document.getElementById('fullName');
  const emailEl = document.getElementById('officialEmail');
  const mobileEl = document.getElementById('mobile');
  if (!V.name(nameEl.value)) {
    UI.setField(nameEl, false, 'Name: letters only, 3–50 characters');
    UI.toast('Please fix full name', 'error');
    return;
  }
  if (!V.email(emailEl.value) || emailEl.classList.contains('invalid')) {
    UI.toast('Check official email', 'error');
    return;
  }
  if (!V.mobile(mobileEl.value) || mobileEl.classList.contains('invalid')) {
    UI.toast('Check mobile number', 'error');
    return;
  }
  if (!V.adminPwd(pwd)) {
    UI.toast('Password must be 10+ chars with upper, lower, digit & special', 'error');
    return;
  }
  if (pwd !== cpwd) {
    UI.toast('Passwords do not match', 'error');
    return;
  }
  if (!document.getElementById('securityQuestion').value || !document.getElementById('securityAnswer').value.trim()) {
    UI.toast('Security question and answer required', 'error');
    return;
  }
  if (!document.getElementById('termsAccepted').checked) {
    UI.toast('Please accept Terms & Conditions', 'error');
    return;
  }

  const fd = new FormData(e.target);
  fd.set('termsAccepted', document.getElementById('termsAccepted').checked ? 'true' : 'false');

  UI.showLoader();
  try {
    const r = await AdminAPI.register(fd);
    const email = emailEl.value.trim().toLowerCase();
    sessionStorage.setItem('pendingAdminEmail', email);
    if (r.data && r.data.devOtp) {
      sessionStorage.setItem('devAdminOtp', r.data.devOtp);
    } else {
      sessionStorage.removeItem('devAdminOtp');
    }
    UI.toast(r.message || 'Verify OTP to activate account');
    location.href = '${ctx}/admin/otp-verification?email=' + encodeURIComponent(email);
  } catch (err) {
    UI.toast(err.message, 'error');
    if (err.data) Object.values(err.data).forEach(function (m) { UI.toast(m, 'error'); });
  } finally {
    UI.hideLoader();
  }
});
</script>
<%@ include file="../includes/portal-scripts.jsp" %>
</body>
</html>
