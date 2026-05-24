<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="../includes/init.jsp" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Verify OTP | EstateVault</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${ctx}/css/auth.css">
    <link rel="stylesheet" href="${ctx}/css/auth-pages.css">
</head>
<body class="auth-body auth-bg-orbs auth-page otp-page">
<%@ include file="../includes/auth-particles.jsp" %>
<nav class="auth-nav">
    <a class="brand" href="${ctx}/">EstateVault</a>
    <a href="${ctx}/user/login" class="btn-outline">Login</a>
</nav>
<div id="toast-container" class="toast-container"></div>
<div id="loader" class="loader-overlay"><div class="spinner"></div></div>

<main class="auth-split">
    <section class="auth-visual" aria-hidden="true">
        <div class="auth-visual-bg" style="background-image:url('https://images.unsplash.com/photo-1600585154526-990dced4db0d?w=1200&q=80');"></div>
        <div class="auth-visual-overlay"></div>
        <div class="auth-visual-inner">
            <div>
                <span class="auth-visual-tag">Almost There</span>
                <h1>Verify Your <span>Email</span></h1>
                <p class="auth-visual-desc">Enter the 6-digit code we sent to activate your EstateVault account securely.</p>
                <div class="auth-visual-features">
                    <span>&#9203; 5 min expiry</span>
                    <span>&#128260; Resend available</span>
                    <span>&#128274; Encrypted OTP</span>
                </div>
            </div>
            <div class="auth-float-badge">
                <strong>One last step</strong>
                <span>Then you can login</span>
            </div>
        </div>
    </section>

    <section class="auth-form-panel">
        <div class="glass-card auth-card-animate text-center">
            <div class="otp-icon-ring" aria-hidden="true">&#9993;</div>
            <div class="auth-form-header text-center" style="margin-bottom:1rem;">
                <h2 style="margin:0;">Verify OTP</h2>
                <p class="mt-2 mb-0">Code sent to <strong id="emailDisplay" style="color:var(--gold);"></strong></p>
            </div>
            <div id="devOtpBanner" class="alert dev-otp-banner text-start mt-2" style="display:none;">
                <strong>SMTP blocked on this network.</strong> Use this OTP to verify:
                <div id="devOtpCode" style="font-size:28px;font-weight:bold;letter-spacing:6px;color:var(--gold);margin-top:8px;"></div>
            </div>
            <p class="small text-muted">Expires in <span id="countdown">5:00</span></p>

            <form id="otpForm" class="mt-3">
                <input type="hidden" id="email">
                <input type="hidden" id="otp">
                <div class="otp-inputs" id="otpBox">
                    <input maxlength="1" inputmode="numeric" autocomplete="one-time-code" aria-label="digit 1">
                    <input maxlength="1" inputmode="numeric" autocomplete="one-time-code" aria-label="digit 2">
                    <input maxlength="1" inputmode="numeric" autocomplete="one-time-code" aria-label="digit 3">
                    <input maxlength="1" inputmode="numeric" autocomplete="one-time-code" aria-label="digit 4">
                    <input maxlength="1" inputmode="numeric" autocomplete="one-time-code" aria-label="digit 5">
                    <input maxlength="1" inputmode="numeric" autocomplete="one-time-code" aria-label="digit 6">
                </div>
                <button type="submit" class="btn-primary-gold w-100 mt-3">Verify OTP</button>
            </form>
            <button id="resendBtn" class="btn-outline w-100 mt-2" disabled>Resend OTP (30s)</button>
            <p class="small text-muted mt-3 mb-2">Max 5 attempts</p>
            <a href="${ctx}/user/register" class="small" style="color:var(--gold)">&#8592; Back to registration</a>
        </div>
    </section>
</main>
<script src="${ctx}/js/auth.js"></script>
<script>
const ctx = '${ctx}';
const params = new URLSearchParams(location.search);
const emailVal = params.get('email') || sessionStorage.getItem('pendingEmail') || '';
document.getElementById('email').value = emailVal;
document.getElementById('emailDisplay').textContent = emailVal;
const devOtp = sessionStorage.getItem('devOtp');
if (devOtp) {
  document.getElementById('devOtpBanner').style.display = 'block';
  document.getElementById('devOtpCode').textContent = devOtp;
}
let verifying = false;
let verifySucceeded = false;

async function submitOtp() {
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
    await AuthAPI.verifyOtp({ email: emailVal.trim().toLowerCase(), otp });
    verifySucceeded = true;
    sessionStorage.removeItem('pendingEmail');
    sessionStorage.removeItem('devOtp');
    UI.toast('Account activated! You can login now.');
    setTimeout(() => location.href = ctx + '/user/login', 1200);
  } catch (e) {
    UI.toast(e.message, 'error');
    verifying = false;
  } finally {
    UI.hideLoader();
  }
}

initOtpBoxes('otpBox', 'otp', () => submitOtp());

let sec = 300, resendWait = 30;
const countdownEl = document.getElementById('countdown');
const resendBtn = document.getElementById('resendBtn');

const timer = setInterval(() => {
  sec--;
  const m = Math.floor(sec/60), s = sec%60;
  countdownEl.textContent = m + ':' + String(s).padStart(2,'0');
  if (sec <= 0) { countdownEl.textContent = 'Expired'; clearInterval(timer); }
}, 1000);

const resendTimer = setInterval(() => {
  resendWait--;
  resendBtn.textContent = resendWait > 0 ? 'Resend OTP ('+resendWait+'s)' : 'Resend OTP';
  if (resendWait <= 0) { resendBtn.disabled = false; clearInterval(resendTimer); }
}, 1000);

resendBtn.addEventListener('click', async () => {
  UI.showLoader();
  try {
    const r = await AuthAPI.resendOtp(emailVal);
    if (r.data?.devOtp) {
      sessionStorage.setItem('devOtp', r.data.devOtp);
      document.getElementById('devOtpBanner').style.display = 'block';
      document.getElementById('devOtpCode').textContent = r.data.devOtp;
    }
    UI.toast(r.message || 'OTP resent');
    resendBtn.disabled = true; resendWait = 30; sec = 300;
  } catch(e) { UI.toast(e.message,'error'); }
  finally { UI.hideLoader(); }
});

document.getElementById('otpForm').addEventListener('submit', e => {
  e.preventDefault();
  submitOtp();
});
</script>
<%@ include file="../includes/portal-scripts.jsp" %>
</body>
</html>
