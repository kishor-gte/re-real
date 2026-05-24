<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="../includes/init.jsp" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Agent Register | EstateVault</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${ctx}/css/agent.css">
    <link rel="stylesheet" href="${ctx}/css/agent-pages.css">
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
</head>
<body class="agent-body agent-bg-grid agent-page">
<%@ include file="../includes/agent-particles.jsp" %>
<nav class="agent-nav">
    <a class="agent-brand" href="${ctx}/"><span class="agent-brand-badge">Agent</span> EstateVault</a>
    <a href="${ctx}/agent/login" class="agent-btn-outline">Login</a>
</nav>
<div id="toast-container" class="toast-container"></div>
<div id="loader" class="loader-overlay"><div class="spinner"></div></div>

<main class="agent-split wide-form">
    <section class="agent-visual" aria-hidden="true">
        <div class="agent-visual-bg" style="background-image:url('https://images.unsplash.com/photo-1600596542815-ffad4c1539a9?w=1200&q=80');"></div>
        <div class="agent-visual-overlay"></div>
        <div class="agent-visual-inner">
            <div>
                <span class="agent-visual-tag">Broker Onboarding</span>
                <h1>Join The <span>Elite</span> Network</h1>
                <p class="agent-visual-desc">Register as a licensed real estate agent. OTP verification activates your broker account and Agent ID.</p>
                <div class="agent-visual-features">
                    <span>&#128231; Email OTP</span>
                    <span>&#127963; RERA verified</span>
                    <span>&#127873; Referral rewards</span>
                </div>
            </div>
        </div>
    </section>
    <section class="agent-form-panel">
        <div class="agent-glass agent-glass-neon agent-card-animate">
            <div class="agent-form-header">
                <div class="agent-icon-wrap">&#127970;</div>
                <h2>Agent Registration</h2>
                <p>Complete all fields — verify with OTP after submit</p>
            </div>
            <form id="agentRegisterForm" enctype="multipart/form-data" class="agent-form-grid" novalidate>
                <div class="agent-form-group">
                    <input class="agent-form-control form-control" id="fullName" name="fullName" placeholder=" " required>
                    <label class="agent-floating-label" for="fullName">Full Name *</label>
                    <div class="agent-field-error field-error"></div>
                </div>
                <div class="agent-form-row-2">
                    <div class="agent-form-group">
                        <input class="agent-form-control form-control" id="email" name="email" type="email" placeholder=" " required>
                        <label class="agent-floating-label" for="email">Email Address *</label>
                        <div class="agent-field-error field-error"></div>
                    </div>
                    <div class="agent-form-group">
                        <input class="agent-form-control form-control" id="mobile" name="mobile" maxlength="10" placeholder=" " required>
                        <label class="agent-floating-label" for="mobile">Mobile (10 digits) *</label>
                        <div class="agent-field-error field-error"></div>
                    </div>
                </div>
                <div class="agent-form-row-2">
                    <div class="agent-form-group">
                        <input class="agent-form-control form-control" id="agencyName" name="agencyName" placeholder=" " required>
                        <label class="agent-floating-label" for="agencyName">Agency Name *</label>
                        <div class="agent-field-error field-error"></div>
                    </div>
                    <div class="agent-form-group">
                        <input class="agent-form-control form-control" id="reraNumber" name="reraNumber" placeholder=" " required maxlength="40">
                        <label class="agent-floating-label" for="reraNumber">RERA Registration No. *</label>
                        <div class="agent-field-error field-error"></div>
                    </div>
                </div>
                <div class="agent-form-row-2">
                    <div class="agent-form-group">
                        <input class="agent-form-control form-control" id="experience" name="experience" type="number" min="0" max="60" placeholder=" " required>
                        <label class="agent-floating-label" for="experience">Experience (Years) *</label>
                    </div>
                    <div class="agent-form-group">
                        <select class="agent-form-select form-select" id="specialization" name="specialization" required>
                            <option value="" disabled selected></option>
                            <option value="RESIDENTIAL">Residential Properties</option>
                            <option value="COMMERCIAL">Commercial Properties</option>
                            <option value="LUXURY_VILLAS">Luxury Villas</option>
                            <option value="APARTMENTS">Apartments</option>
                            <option value="PLOTS_LANDS">Plots &amp; Lands</option>
                            <option value="RENTAL">Rental Properties</option>
                            <option value="CONSTRUCTION">Construction Projects</option>
                        </select>
                        <label class="agent-floating-label" for="specialization">Specialization *</label>
                    </div>
                </div>
                <div class="agent-form-group">
                    <input class="agent-form-control form-control" id="officeAddress" name="officeAddress" placeholder=" " required>
                    <label class="agent-floating-label" for="officeAddress">Office Address *</label>
                </div>
                <div class="agent-form-row-2">
                    <div class="agent-form-group">
                        <input class="agent-form-control form-control" id="city" name="city" placeholder=" " required>
                        <label class="agent-floating-label" for="city">City *</label>
                    </div>
                    <div class="agent-form-group">
                        <input class="agent-form-control form-control" id="state" name="state" placeholder=" " required>
                        <label class="agent-floating-label" for="state">State *</label>
                    </div>
                </div>
                <div class="agent-form-group" style="max-width:200px;">
                    <input class="agent-form-control form-control" id="pincode" name="pincode" maxlength="6" placeholder=" " required>
                    <label class="agent-floating-label" for="pincode">Pincode *</label>
                    <div class="agent-field-error field-error"></div>
                </div>
                <div class="agent-form-row-2">
                    <div class="agent-form-group agent-pwd-wrap">
                        <input class="agent-form-control form-control" id="password" name="password" type="password" placeholder=" " required>
                        <label class="agent-floating-label" for="password">Password (8+ chars) *</label>
                        <button type="button" class="agent-pwd-toggle pwd-toggle" id="togglePwd1">&#128065;</button>
                        <div class="agent-strength-bar strength-bar" id="strAgent"><span></span></div>
                        <div class="agent-field-error field-error"></div>
                    </div>
                    <div class="agent-form-group agent-pwd-wrap">
                        <input class="agent-form-control form-control" id="confirmPassword" name="confirmPassword" type="password" placeholder=" " required>
                        <label class="agent-floating-label" for="confirmPassword">Confirm Password *</label>
                        <button type="button" class="agent-pwd-toggle pwd-toggle" id="togglePwd2">&#128065;</button>
                        <div class="agent-field-error field-error"></div>
                    </div>
                </div>
                <div class="agent-form-row-2">
                    <div class="agent-form-group">
                        <label class="small" style="color:var(--agent-muted);">Profile Photo</label>
                        <input type="file" class="agent-form-control form-control mt-1" id="profilePhoto" name="profilePhoto" accept="image/*">
                        <img id="profilePreview" class="agent-profile-preview profile-preview" alt="Preview">
                    </div>
                    <div class="agent-form-group">
                        <label class="small" style="color:var(--agent-muted);">Agency Logo (optional)</label>
                        <input type="file" class="agent-form-control form-control mt-1" id="agencyLogo" name="agencyLogo" accept="image/*">
                        <img id="logoPreview" class="agent-profile-preview profile-preview" alt="Logo preview">
                    </div>
                </div>
                <div class="agent-form-group">
                    <label class="small" style="color:var(--agent-muted);">Government ID Proof *</label>
                    <input type="file" class="agent-form-control form-control mt-1" id="governmentId" name="governmentId"
                           accept=".pdf,.doc,.docx,.ppt,.pptx,.xls,.xlsx,.txt,.rtf,.odt,.ods,.odp,image/*,application/pdf,application/msword,application/vnd.openxmlformats-officedocument.wordprocessingml.document,application/vnd.ms-powerpoint,application/vnd.openxmlformats-officedocument.presentationml.presentation,application/vnd.ms-excel,application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                           required>
                    <p class="small mt-1 mb-0" style="color:var(--agent-muted);">
                        PDF, Word, Excel, PowerPoint, images — max 20 MB
                    </p>
                    <p id="govtFileName" class="small mt-1 mb-0" style="color:var(--agent-gold);display:none;"></p>
                </div>
                <div class="agent-form-group">
                    <input class="agent-form-control form-control" id="referralCode" name="referralCode" placeholder=" " maxlength="12">
                    <label class="agent-floating-label" for="referralCode">Referral Code (optional)</label>
                    <div class="agent-field-error field-error"></div>
                </div>
                <div class="agent-form-check">
                    <input type="checkbox" id="termsAccepted" name="termsAccepted" value="true">
                    <label for="termsAccepted">I accept Terms &amp; Conditions and confirm RERA credentials are accurate *</label>
                </div>
                <button type="submit" class="agent-btn-gold w-100">Register &amp; Send OTP</button>
            </form>
            <p class="text-center mt-3 mb-0" style="color:var(--agent-muted);">
                Already registered? <a href="${ctx}/agent/login" class="agent-link">Agent login</a>
            </p>
        </div>
    </section>
</main>
<script src="${ctx}/js/auth.js"></script>
<script src="${ctx}/js/agent.js"></script>
<script>
AgentAPI.base = '${ctx}';
initPwdToggle('togglePwd1', 'password');
initPwdToggle('togglePwd2', 'confirmPassword');
initAgentPwdStrength('password', '#strAgent');

function bindPreview(inputId, imgId) {
  document.getElementById(inputId)?.addEventListener('change', function () {
    const f = this.files[0];
    const img = document.getElementById(imgId);
    if (f && img) { img.src = URL.createObjectURL(f); img.style.display = 'block'; }
  });
}
bindPreview('profilePhoto', 'profilePreview');
bindPreview('agencyLogo', 'logoPreview');

const GOVT_ALLOWED_EXT = ['.pdf','.doc','.docx','.ppt','.pptx','.xls','.xlsx','.txt','.rtf','.odt','.ods','.odp','.jpg','.jpeg','.png','.gif','.webp','.bmp','.heic'];
const GOVT_MAX_MB = 20;

document.getElementById('governmentId')?.addEventListener('change', function () {
  const f = this.files[0];
  const hint = document.getElementById('govtFileName');
  if (!f) {
    hint.style.display = 'none';
    UI.setField(this, null, '');
    return;
  }
  const name = f.name || '';
  const dot = name.lastIndexOf('.');
  const ext = dot >= 0 ? name.substring(dot).toLowerCase() : '';
  const sizeMb = f.size / (1024 * 1024);
  hint.style.display = 'block';
  hint.textContent = 'Selected: ' + name + ' (' + sizeMb.toFixed(2) + ' MB)';
  if (!ext || GOVT_ALLOWED_EXT.indexOf(ext) < 0) {
    UI.setField(this, false, 'Allowed: PDF, Word, Excel, PowerPoint, images');
    return;
  }
  if (sizeMb > GOVT_MAX_MB) {
    UI.setField(this, false, 'File must be under ' + GOVT_MAX_MB + ' MB');
    return;
  }
  UI.setField(this, true, '');
});

document.getElementById('mobile')?.addEventListener('input', function () {
  this.value = this.value.replace(/\D/g, '').slice(0, 10);
});
document.getElementById('pincode')?.addEventListener('input', function () {
  this.value = this.value.replace(/\D/g, '').slice(0, 6);
});

let emailTimer, mobileTimer, reraTimer, refTimer;
document.getElementById('email')?.addEventListener('input', function () {
  clearTimeout(emailTimer);
  emailTimer = setTimeout(async function () {
    if (!V.email(this.value)) return UI.setField(this, false, 'Invalid email format');
    try {
      const r = await AgentAPI.checkEmail(this.value.trim());
      UI.setField(this, r.data, r.data ? '' : 'Email already registered');
    } catch (e) {}
  }.bind(this), 500);
});
document.getElementById('mobile')?.addEventListener('input', function () {
  clearTimeout(mobileTimer);
  mobileTimer = setTimeout(async function () {
    if (!V.mobile(this.value)) return UI.setField(this, false, 'Enter valid 10-digit mobile');
    try {
      const r = await AgentAPI.checkMobile(this.value);
      UI.setField(this, r.data, r.data ? '' : 'Mobile already registered');
    } catch (e) {}
  }.bind(this), 500);
});
document.getElementById('reraNumber')?.addEventListener('input', function () {
  clearTimeout(reraTimer);
  reraTimer = setTimeout(async function () {
    if (!V.rera(this.value)) return UI.setField(this, false, 'Invalid RERA format (8–40 chars)');
    try {
      const r = await AgentAPI.checkRera(this.value.trim());
      UI.setField(this, r.data, r.data ? '' : 'RERA number already registered');
    } catch (e) {}
  }.bind(this), 500);
});
document.getElementById('referralCode')?.addEventListener('input', function () {
  clearTimeout(refTimer);
  if (!this.value.trim()) { UI.setField(this, null, ''); return; }
  refTimer = setTimeout(async function () {
    try {
      const r = await AgentAPI.checkReferral(this.value.trim());
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

document.getElementById('agentRegisterForm').addEventListener('submit', async function (e) {
  e.preventDefault();
  const nameEl = document.getElementById('fullName');
  const emailEl = document.getElementById('email');
  const mobileEl = document.getElementById('mobile');
  const agencyEl = document.getElementById('agencyName');
  const reraEl = document.getElementById('reraNumber');
  const pwd = document.getElementById('password').value;
  const cpwd = document.getElementById('confirmPassword').value;

  if (!V.name(nameEl.value)) { UI.toast('Full name: letters only, 3–50 chars', 'error'); return; }
  if (!V.email(emailEl.value) || emailEl.classList.contains('invalid')) { UI.toast('Check email', 'error'); return; }
  if (!V.mobile(mobileEl.value) || mobileEl.classList.contains('invalid')) { UI.toast('Check mobile', 'error'); return; }
  if (!V.agency(agencyEl.value)) { UI.toast('Agency name min 3 characters', 'error'); return; }
  if (!V.rera(reraEl.value) || reraEl.classList.contains('invalid')) { UI.toast('Check RERA number', 'error'); return; }
  if (!document.getElementById('specialization').value) { UI.toast('Select specialization', 'error'); return; }
  if (!V.pincode(document.getElementById('pincode').value)) { UI.toast('Pincode must be 6 digits', 'error'); return; }
  if (!V.agentPwd(pwd)) { UI.toast('Password: 8+ chars, upper, lower, digit & special', 'error'); return; }
  if (pwd !== cpwd) { UI.toast('Passwords do not match', 'error'); return; }
  const govtInput = document.getElementById('governmentId');
  const govtFile = govtInput.files[0];
  if (!govtFile) { UI.toast('Government ID is required', 'error'); return; }
  if (govtInput.classList.contains('invalid')) { UI.toast('Please choose a valid government ID file', 'error'); return; }
  if (!document.getElementById('termsAccepted').checked) { UI.toast('Accept Terms & Conditions', 'error'); return; }

  const fd = new FormData(e.target);
  fd.set('termsAccepted', document.getElementById('termsAccepted').checked ? 'true' : 'false');

  const ctx = '${ctx}';
  const email = emailEl.value.trim().toLowerCase();

  UI.showLoader();
  try {
    const r = await AgentAPI.register(fd);
    if (r.success === false) {
      throw new Error(r.message || 'Registration failed');
    }
    sessionStorage.setItem('pendingAgentEmail', email);
    const emailSent = r.data && r.data.emailSent !== false;
    if (r.data && r.data.devOtp) {
      sessionStorage.setItem('devAgentOtp', r.data.devOtp);
    } else {
      sessionStorage.removeItem('devAgentOtp');
    }
    sessionStorage.setItem('agentOtpEmailSent', emailSent ? 'true' : 'false');
    UI.toast(r.message || (emailSent
      ? 'OTP sent to your email. Check inbox and spam folder.'
      : 'Email could not be sent — your OTP is on the next screen.'), emailSent ? 'success' : 'warning');
    const otpUrl = ctx + '/agent/otp-verification?email=' + encodeURIComponent(email);
    window.setTimeout(function () {
      window.location.assign(otpUrl);
    }, 400);
  } catch (err) {
    UI.toast(err.message || 'Registration failed', 'error');
    if (err.data && typeof err.data === 'object') {
      Object.values(err.data).forEach(function (m) { if (m) UI.toast(m, 'error'); });
    }
  } finally {
    UI.hideLoader();
  }
});
</script>
<%@ include file="../includes/portal-scripts.jsp" %>
</body>
</html>
