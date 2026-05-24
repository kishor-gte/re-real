<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="../includes/init.jsp" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Verify Admin OTP | EstateVault</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${ctx}/css/admin.css">
    <link rel="stylesheet" href="${ctx}/css/admin-pages.css">
</head>
<body class="admin-body admin-bg-grid admin-page admin-otp-page">
<%@ include file="../includes/admin-particles.jsp" %>
<nav class="admin-nav">
    <a class="admin-brand" href="${ctx}/"><span class="admin-brand-badge">Admin</span> EstateVault</a>
    <a href="${ctx}/admin/login" class="admin-btn-outline">Login</a>
</nav>
<div id="toast-container" class="toast-container"></div>
<div id="loader" class="loader-overlay"><div class="spinner"></div></div>

<main class="admin-split">
    <section class="admin-visual" aria-hidden="true">
        <div class="admin-visual-bg" style="background-image:url('https://images.unsplash.com/photo-1556761175-5973dc0fb32a?w=1200&q=80');"></div>
        <div class="admin-visual-overlay"></div>
        <div class="admin-visual-scanline"></div>
        <div class="admin-visual-inner">
            <div>
                <span class="admin-visual-tag">Identity Verification</span>
                <h1>Verify <span>Official</span> Email</h1>
                <p class="admin-visual-desc">Enter the 6-digit OTP sent to your official email to activate your admin account.</p>
                <div class="admin-visual-features">
                    <span>&#9203; 5 min expiry</span>
                    <span>&#128260; Resend after 30s</span>
                    <span>&#128274; Max 5 attempts</span>
                </div>
            </div>
            <div class="admin-float-badge">
                <strong>Activation required</strong>
                <span>Then sign in at admin login</span>
            </div>
        </div>
    </section>

    <section class="admin-form-panel">
        <div class="admin-glass admin-glass-neon admin-card-animate text-center">
            <div class="admin-otp-ring" aria-hidden="true">&#9993;</div>
            <div class="admin-form-header text-center" style="margin-bottom:1rem;">
                <h2 style="margin:0;">Verify Admin OTP</h2>
                <p class="mt-2 mb-0">Code sent to <strong id="emailDisplay" style="color:var(--admin-neon);"></strong></p>
            </div>
            <div id="devOtpBanner" class="admin-dev-otp-banner dev-otp-banner text-start mt-2" style="display:none;">
                <strong>SMTP blocked on this network.</strong> Use this OTP to verify:
                <div id="devOtpCode" style="font-size:28px;font-weight:bold;letter-spacing:6px;color:var(--admin-gold);margin-top:8px;"></div>
            </div>
            <p class="small" style="color:var(--admin-muted);">Expires in <span id="adminCountdown">5:00</span></p>

            <form id="adminOtpForm" class="mt-3">
                <input type="hidden" id="officialEmail">
                <input type="hidden" id="otp">
                <div class="admin-otp-inputs otp-inputs" id="adminOtpBox">
                    <input maxlength="1" inputmode="numeric" autocomplete="one-time-code" aria-label="digit 1">
                    <input maxlength="1" inputmode="numeric" autocomplete="one-time-code" aria-label="digit 2">
                    <input maxlength="1" inputmode="numeric" autocomplete="one-time-code" aria-label="digit 3">
                    <input maxlength="1" inputmode="numeric" autocomplete="one-time-code" aria-label="digit 4">
                    <input maxlength="1" inputmode="numeric" autocomplete="one-time-code" aria-label="digit 5">
                    <input maxlength="1" inputmode="numeric" autocomplete="one-time-code" aria-label="digit 6">
                </div>
                <button type="submit" class="admin-btn-gold w-100 mt-3">Verify &amp; Activate</button>
            </form>
            <button id="resendBtn" class="admin-btn-outline w-100 mt-2" disabled>Resend OTP (30s)</button>
            <p class="small mt-3 mb-2" style="color:var(--admin-muted);">Max 5 attempts</p>
            <a href="${ctx}/admin/register" class="admin-link small">&#8592; Back to registration</a>
        </div>
    </section>
</main>
<script src="${ctx}/js/auth.js"></script>
<script src="${ctx}/js/admin.js"></script>
<script>
AdminAPI.base = '${ctx}';
const ctx = '${ctx}';
const params = new URLSearchParams(location.search);
const emailVal = (params.get('email') || sessionStorage.getItem('pendingAdminEmail') || '').trim().toLowerCase();
document.getElementById('officialEmail').value = emailVal;
document.getElementById('emailDisplay').textContent = emailVal;

const devOtp = sessionStorage.getItem('devAdminOtp');
if (devOtp) {
  document.getElementById('devOtpBanner').style.display = 'block';
  document.getElementById('devOtpCode').textContent = devOtp;
}

let verifying = false;
let verifySucceeded = false;

async function submitAdminOtp() {
  if (verifySucceeded || verifying) return;
  const otp = document.getElementById('otp').value.replace(/\D/g, '');
  if (otp.length !== 6) return;
  if (!emailVal) {
    UI.toast('Email missing. Please register again.', 'error');
    return;
  }
  verifying = true;
  UI.showLoader();
  try {
    await AdminAPI.verifyOtp({ officialEmail: emailVal, otp: otp });
    verifySucceeded = true;
    sessionStorage.removeItem('pendingAdminEmail');
    sessionStorage.removeItem('devAdminOtp');
    UI.toast('Admin account activated! You can login now.');
    setTimeout(function () { location.href = ctx + '/admin/login'; }, 1200);
  } catch (e) {
    UI.toast(e.message, 'error');
    verifying = false;
  } finally {
    UI.hideLoader();
  }
}

initOtpBoxes('adminOtpBox', 'otp', function () { submitAdminOtp(); });

let sec = 300, resendWait = 30;
const countdownEl = document.getElementById('adminCountdown');
const resendBtn = document.getElementById('resendBtn');

const timer = setInterval(function () {
  sec--;
  const m = Math.floor(sec / 60), s = sec % 60;
  countdownEl.textContent = m + ':' + String(s).padStart(2, '0');
  if (sec <= 0) { countdownEl.textContent = 'Expired'; clearInterval(timer); }
}, 1000);

const resendTimer = setInterval(function () {
  resendWait--;
  resendBtn.textContent = resendWait > 0 ? 'Resend OTP (' + resendWait + 's)' : 'Resend OTP';
  if (resendWait <= 0) { resendBtn.disabled = false; clearInterval(resendTimer); }
}, 1000);

resendBtn.addEventListener('click', async function () {
  UI.showLoader();
  try {
    const r = await AdminAPI.resendOtp(emailVal);
    if (r.data && r.data.devOtp) {
      sessionStorage.setItem('devAdminOtp', r.data.devOtp);
      document.getElementById('devOtpBanner').style.display = 'block';
      document.getElementById('devOtpCode').textContent = r.data.devOtp;
    }
    UI.toast(r.message || 'OTP resent');
    resendBtn.disabled = true;
    resendWait = 30;
    sec = 300;
  } catch (e) {
    UI.toast(e.message, 'error');
  } finally {
    UI.hideLoader();
  }
});

document.getElementById('adminOtpForm').addEventListener('submit', function (e) {
  e.preventDefault();
  submitAdminOtp();
});
</script>
<%@ include file="../includes/portal-scripts.jsp" %>
</body>
</html>
