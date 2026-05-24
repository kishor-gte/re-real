<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="../includes/init.jsp" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Register | EstateVault</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${ctx}/css/auth.css">
    <link rel="stylesheet" href="${ctx}/css/auth-pages.css">
</head>
<body class="auth-body auth-bg-orbs auth-page">
<%@ include file="../includes/auth-particles.jsp" %>
<nav class="auth-nav">
    <a class="brand" href="${ctx}/">EstateVault</a>
    <a href="${ctx}/user/login" class="btn-outline">Login</a>
</nav>
<div id="toast-container" class="toast-container"></div>
<div id="loader" class="loader-overlay"><div class="spinner"></div></div>

<main class="auth-split wide-form">
    <section class="auth-visual" aria-hidden="true">
        <div class="auth-visual-bg" style="background-image:url('https://images.unsplash.com/photo-1560518883-ce09059eeffa?w=1200&q=80');"></div>
        <div class="auth-visual-overlay"></div>
        <div class="auth-visual-inner">
            <div>
                <span class="auth-visual-tag">Join EstateVault</span>
                <h1>Start Your <span>Property</span> Journey</h1>
                <p class="auth-visual-desc">Register as a buyer or seller, verify with OTP, and unlock premium listings across India.</p>
                <div class="auth-visual-features">
                    <span>&#127968; Buy &amp; rent</span>
                    <span>&#128200; Sell &amp; invest</span>
                    <span>&#128274; OTP verified</span>
                </div>
            </div>
            <div class="auth-visual-bottom">
                <div class="auth-stat-pills">
                    <div class="auth-stat-pill"><strong>3 Steps</strong><small>Quick signup</small></div>
                    <div class="auth-stat-pill"><strong>Free</strong><small>Registration</small></div>
                </div>
                <div class="auth-float-badge">
                    <strong>Buyer or Seller</strong>
                    <span>Choose your role</span>
                </div>
            </div>
        </div>
    </section>

    <section class="auth-form-panel">
    <div class="glass-card auth-card-animate">
        <div class="auth-form-header">
            <div class="auth-icon-wrap" aria-hidden="true">&#128100;</div>
            <h2>Create Account</h2>
            <p>Join as Buyer or Seller — verify with OTP</p>
        </div>
        <div class="step-indicator enhanced">
            <div class="step-dot active"></div><div class="step-dot"></div><div class="step-dot"></div>
        </div>

        <form id="registerForm" enctype="multipart/form-data" novalidate>
            <!-- Step 1 -->
            <div class="form-step active" data-step="1">
                <div class="form-grid">
                    <div class="form-group">
                        <input class="form-control" id="fullName" name="fullName" placeholder=" " required>
                        <label class="floating-label" for="fullName">Full Name *</label>
                        <div class="field-error"></div>
                    </div>
                    <div class="form-group">
                        <input class="form-control" id="email" name="email" type="email" placeholder=" " required>
                        <label class="floating-label" for="email">Email Address *</label>
                        <div class="field-error"></div>
                    </div>
                    <div class="form-group">
                        <input class="form-control" id="mobile" name="mobile" maxlength="10" placeholder=" " required>
                        <label class="floating-label" for="mobile">Mobile Number *</label>
                        <div class="field-error"></div>
                    </div>
                    <div class="form-row-2">
                        <div class="form-group">
                            <select class="form-select" id="gender" name="gender" required>
                                <option value="" disabled selected></option>
                                <option value="MALE">Male</option>
                                <option value="FEMALE">Female</option>
                                <option value="OTHER">Other</option>
                            </select>
                            <label class="floating-label" for="gender">Gender *</label>
                        </div>
                        <div class="form-group">
                            <input class="form-control" id="dateOfBirth" name="dateOfBirth" type="date" placeholder=" " required>
                            <label class="floating-label" for="dateOfBirth">Date of Birth *</label>
                            <div class="field-error"></div>
                        </div>
                    </div>
                </div>
                <button type="button" class="btn-primary-gold w-100 mt-3" data-next>Next</button>
            </div>

            <!-- Step 2 -->
            <div class="form-step" data-step="2">
                <div class="form-grid">
                    <div class="form-group pwd-wrap">
                        <input class="form-control" id="password" name="password" type="password" placeholder=" " required>
                        <label class="floating-label" for="password">Password *</label>
                        <button type="button" class="pwd-toggle" id="togglePwd1">&#128065;</button>
                        <div class="strength-bar" id="str1"><span></span></div>
                        <div class="field-error"></div>
                    </div>
                    <div class="form-group pwd-wrap">
                        <input class="form-control" id="confirmPassword" name="confirmPassword" type="password" placeholder=" " required>
                        <label class="floating-label" for="confirmPassword">Confirm Password *</label>
                        <button type="button" class="pwd-toggle" id="togglePwd2">&#128065;</button>
                        <div class="field-error"></div>
                    </div>
                    <div>
                        <label class="text-muted small">Role *</label>
                        <div class="role-cards mt-1">
                            <label class="role-card active"><input type="radio" name="role" value="BUYER" checked> Buyer</label>
                            <label class="role-card"><input type="radio" name="role" value="SELLER"> Seller</label>
                        </div>
                    </div>
                </div>
                <div class="d-flex gap-2 mt-3">
                    <button type="button" class="btn-outline flex-fill" data-prev>Back</button>
                    <button type="button" class="btn-primary-gold flex-fill" data-next>Next</button>
                </div>
            </div>

            <!-- Step 3 -->
            <div class="form-step" data-step="3">
                <div class="form-grid">
                    <div class="form-group">
                        <input class="form-control" id="address" name="address" placeholder=" ">
                        <label class="floating-label" for="address">Address</label>
                    </div>
                    <div class="form-row-2">
                        <div class="form-group">
                            <input class="form-control" id="city" name="city" placeholder=" ">
                            <label class="floating-label" for="city">City</label>
                        </div>
                        <div class="form-group">
                            <input class="form-control" id="state" name="state" placeholder=" ">
                            <label class="floating-label" for="state">State</label>
                        </div>
                    </div>
                    <div class="form-group">
                        <input class="form-control" id="pincode" name="pincode" maxlength="6" placeholder=" ">
                        <label class="floating-label" for="pincode">Pincode</label>
                        <div class="field-error"></div>
                    </div>
                    <div class="form-group">
                        <input class="form-control" id="referralCode" name="referralCode" placeholder=" ">
                        <label class="floating-label" for="referralCode">Referral Code (Optional)</label>
                    </div>
                    <div class="form-group">
                        <label class="text-muted small">Profile Image (Optional)</label>
                        <input type="file" class="form-control mt-1" id="profileImage" name="profileImage" accept="image/*">
                        <img id="profilePreview" class="profile-preview" alt="preview">
                    </div>
                    <div class="form-check">
                        <input class="form-check-input" type="checkbox" id="termsAccepted" name="termsAccepted" value="true">
                        <label class="form-check-label" for="termsAccepted">I agree to Terms &amp; Conditions *</label>
                    </div>
                </div>
                <div class="d-flex gap-2 mt-3">
                    <button type="button" class="btn-outline flex-fill" data-prev>Back</button>
                    <button type="submit" class="btn-primary-gold flex-fill">Register &amp; Send OTP</button>
                </div>
            </div>
        </form>
        <p class="text-center mt-3 text-muted mb-0"><span style="color:white;">Already registered? </span><a href="${ctx}/user/login" style="color:var(--gold);text-decoration:none">Login</a></p>
    </div>
    </section>
</main>
<script src="${ctx}/js/auth.js"></script>
<script>
setDobMaxToday('dateOfBirth');
initRoleCards(); initPwdStrength('password','#str1');
initPwdToggle('togglePwd1','password'); initPwdToggle('togglePwd2','confirmPassword');

function validateRegisterStep1() {
  const nameEl = document.getElementById('fullName');
  const emailEl = document.getElementById('email');
  const mobileEl = document.getElementById('mobile');
  const genderEl = document.getElementById('gender');
  const dobEl = document.getElementById('dateOfBirth');
  let ok = true;

  if (!V.name(nameEl.value)) {
    UI.setField(nameEl, false, 'Full Name is required (letters only, min 3 characters)');
    ok = false;
  } else UI.setField(nameEl, true, '');

  if (!V.email(emailEl.value)) {
    UI.setField(emailEl, false, 'Invalid email format');
    ok = false;
  } else if (emailEl.classList.contains('invalid')) {
    ok = false;
  } else UI.setField(emailEl, true, '');

  if (!V.mobile(mobileEl.value)) {
    UI.setField(mobileEl, false, 'Enter valid 10-digit mobile number');
    ok = false;
  } else if (mobileEl.classList.contains('invalid')) {
    ok = false;
  } else UI.setField(mobileEl, true, '');

  if (!genderEl.value) {
    UI.toast('Please select gender', 'error');
    ok = false;
  }

  const dob = dobEl.value;
  if (!dob) {
    UI.setField(dobEl, false, 'Date of birth is required');
    ok = false;
  } else if (!V.notFutureDate(dob)) {
    UI.setField(dobEl, false, 'Date of birth cannot be in the future');
    UI.toast('Date of birth cannot be tomorrow or a future date', 'error');
    ok = false;
  } else if (!V.age18(dob)) {
    UI.setField(dobEl, false, 'You must be at least 18 years old');
    UI.toast('You must be at least 18 years old', 'error');
    ok = false;
  } else UI.setField(dobEl, true, '');

  if (!ok) UI.toast('Please fix the errors in step 1', 'error');
  return ok;
}

function validateRegisterStep2() {
  const pwdEl = document.getElementById('password');
  const confirmEl = document.getElementById('confirmPassword');
  const pwd = pwdEl.value;
  const confirm = confirmEl.value;
  let ok = true;

  if (!V.pwd(pwd)) {
    UI.setField(pwdEl, false, 'Password is too weak');
    ok = false;
  } else UI.setField(pwdEl, true, '');

  if (!confirm) {
    UI.setField(confirmEl, false, 'Confirm password is required');
    ok = false;
  } else if (!V.passwordsMatch(pwd, confirm)) {
    UI.setField(confirmEl, false, 'Passwords do not match');
    UI.toast('Passwords do not match', 'error');
    ok = false;
  } else UI.setField(confirmEl, true, '');

  if (!ok && V.passwordsMatch(pwd, confirm) === false && confirm) {
    /* toast already shown for mismatch */
  } else if (!ok) {
    UI.toast('Please fix password fields before continuing', 'error');
  }
  return ok;
}

function checkPasswordMatchLive() {
  const pwdEl = document.getElementById('password');
  const confirmEl = document.getElementById('confirmPassword');
  if (!pwdEl || !confirmEl) return;
  const confirm = confirmEl.value;
  if (!confirm) {
    UI.setField(confirmEl, null, '');
    return;
  }
  if (V.passwordsMatch(pwdEl.value, confirm)) {
    UI.setField(confirmEl, true, '');
  } else {
    UI.setField(confirmEl, false, 'Passwords do not match');
  }
}

document.getElementById('password')?.addEventListener('input', checkPasswordMatchLive);
document.getElementById('confirmPassword')?.addEventListener('input', checkPasswordMatchLive);

document.getElementById('dateOfBirth')?.addEventListener('change', function() {
  if (!this.value) return;
  if (!V.notFutureDate(this.value)) {
    UI.setField(this, false, 'Date of birth cannot be in the future');
    this.value = '';
  } else if (!V.age18(this.value)) {
    UI.setField(this, false, 'You must be at least 18 years old');
  } else {
    UI.setField(this, true, '');
  }
});

initMultiStep('registerForm', [validateRegisterStep1, validateRegisterStep2]);

const profileInput = document.getElementById('profileImage');
const preview = document.getElementById('profilePreview');
profileInput?.addEventListener('change', () => {
  const f = profileInput.files[0];
  if (f) { preview.src = URL.createObjectURL(f); preview.style.display = 'block'; }
});

let emailTimer, mobileTimer;
document.getElementById('email')?.addEventListener('input', function() {
  clearTimeout(emailTimer);
  emailTimer = setTimeout(async () => {
    if (!V.email(this.value)) return UI.setField(this, false, 'Invalid email format');
    try {
      const r = await AuthAPI.checkEmail(this.value);
      UI.setField(this, r.data, r.data ? '' : 'Email already exists');
    } catch(e) {}
  }, 500);
});

document.getElementById('mobile')?.addEventListener('input', function() {
  this.value = this.value.replace(/\D/g,'').slice(0,10);
  clearTimeout(mobileTimer);
  mobileTimer = setTimeout(async () => {
    if (!V.mobile(this.value)) return UI.setField(this, false, 'Enter valid 10-digit mobile number');
    try {
      const r = await AuthAPI.checkMobile(this.value);
      UI.setField(this, r.data, r.data ? '' : 'Phone number already registered');
    } catch(e) {}
  }, 500);
});

document.getElementById('registerForm').addEventListener('submit', async e => {
  e.preventDefault();
  const pwd = document.getElementById('password').value;
  const cpwd = document.getElementById('confirmPassword').value;
  if (!V.pwd(pwd)) { UI.toast('Password is too weak','error'); return; }
  if (pwd !== cpwd) { UI.toast('Passwords do not match','error'); return; }
  const dobVal = document.getElementById('dateOfBirth').value;
  if (!V.notFutureDate(dobVal)) { UI.toast('Date of birth cannot be in the future','error'); return; }
  if (!V.age18(dobVal)) { UI.toast('You must be 18+ years old','error'); return; }
  if (!document.getElementById('termsAccepted').checked) { UI.toast('Please accept Terms & Conditions','error'); return; }

  const fd = new FormData(e.target);
  if (!document.getElementById('termsAccepted').checked) fd.delete('termsAccepted');
  else fd.set('termsAccepted', 'true');

  UI.showLoader();
  try {
    const r = await AuthAPI.register(fd);
    sessionStorage.setItem('pendingEmail', document.getElementById('email').value);
    if (r.data?.devOtp) {
      sessionStorage.setItem('devOtp', r.data.devOtp);
    } else {
      sessionStorage.removeItem('devOtp');
    }
    UI.toast(r.message || 'Continue to verify OTP');
    location.href = '${ctx}/user/otp-verification?email=' + encodeURIComponent(document.getElementById('email').value);
  } catch(err) {
    UI.toast(err.message, 'error');
    if (err.data) Object.values(err.data).forEach(m => UI.toast(m, 'error'));
  } finally { UI.hideLoader(); }
});
</script>
<%@ include file="../includes/portal-scripts.jsp" %>
</body>
</html>
