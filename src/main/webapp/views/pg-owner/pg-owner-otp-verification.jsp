<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="../includes/init.jsp" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Verify PG Owner OTP | EstateVault</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${ctx}/css/pg-owner.css">
    <link rel="stylesheet" href="${ctx}/css/pg-owner-pages.css">
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
</head>
<body class="pgo-body pgo-bg-grid pgo-page pgo-otp-page">
<%@ include file="../includes/pg-owner-particles.jsp" %>
<nav class="pgo-nav">
    <a class="pgo-brand" href="${ctx}/"><span class="pgo-brand-badge">PG Owner</span> EstateVault</a>
    <a href="${ctx}/pg-owner/login" class="pgo-btn-outline">Login</a>
</nav>
<div id="toast-container" class="toast-container"></div>
<div id="loader" class="loader-overlay"><div class="spinner"></div></div>
<main class="pgo-split">
    <section class="pgo-visual" aria-hidden="true">
        <div class="pgo-visual-bg" style="background-image:url('https://images.unsplash.com/photo-1560520031-0aee6d09c04d?w=1200&q=80');"></div>
        <div class="pgo-visual-overlay"></div>
        <div class="pgo-visual-inner">
            <div>
                <span class="pgo-visual-tag">Email Verification</span>
                <h1>Activate <span>PG owner</span> Account</h1>
                <p class="pgo-visual-desc">Enter the 6-digit OTP sent to your registered email.</p>
            </div>
        </div>
    </section>
    <section class="pgo-form-panel">
        <div class="pgo-glass pgo-glass-neon pgo-card-animate text-center">
            <div class="pgo-otp-ring">&#9993;</div>
            <h2>Verify OTP</h2>
            <p class="mt-2">Code sent to <strong id="emailDisplay" style="color:var(--pgo-gold);"></strong></p>
            <p class="small mt-2 mb-0" style="color:var(--pgo-muted);">Check your inbox and spam folder. The code expires in <span id="agentCountdown">5:00</span>.</p>
            <form id="agentOtpForm" class="mt-3">
                <input type="hidden" id="email">
                <input type="hidden" id="otp">
                <div class="pgo-otp-inputs otp-inputs" id="agentOtpBox">
                    <input maxlength="1" inputmode="numeric" aria-label="digit 1">
                    <input maxlength="1" inputmode="numeric" aria-label="digit 2">
                    <input maxlength="1" inputmode="numeric" aria-label="digit 3">
                    <input maxlength="1" inputmode="numeric" aria-label="digit 4">
                    <input maxlength="1" inputmode="numeric" aria-label="digit 5">
                    <input maxlength="1" inputmode="numeric" aria-label="digit 6">
                </div>
                <button type="submit" class="pgo-btn-gold w-100 mt-3">Verify &amp; Activate</button>
            </form>
            <button id="resendBtn" class="pgo-btn-outline w-100 mt-2" disabled>Resend OTP (30s)</button>
            <a href="${ctx}/pg-owner/register" class="pgo-link small d-inline-block mt-3">&#8592; Back to registration</a>
        </div>
    </section>
</main>
<script src="${ctx}/js/auth.js"></script>
<script src="${ctx}/js/pg-owner.js"></script>
<script>
PgOwnerAPI.base = '${ctx}';
const ctx = '${ctx}';
const emailVal = (new URLSearchParams(location.search).get('email') || sessionStorage.getItem('pendingPgOwnerEmail') || '').trim().toLowerCase();
document.getElementById('email').value = emailVal;
document.getElementById('emailDisplay').textContent = emailVal;
sessionStorage.removeItem('devPgOwnerOtp');
sessionStorage.removeItem('pgOwnerOtpEmailSent');

let verifying = false, verifySucceeded = false;

function readOtpFromBoxes() {
  const box = document.getElementById('agentOtpBox');
  if (!box) return '';
  return [...box.querySelectorAll('input')].map(function (i) {
    return (i.value || '').replace(/\D/g, '');
  }).join('').slice(0, 6);
}

async function submitAgentOtp() {
  if (verifySucceeded || verifying) return;
  const otp = readOtpFromBoxes() || (document.getElementById('otp').value || '').replace(/\D/g, '');
  document.getElementById('otp').value = otp;
  if (otp.length !== 6 || !emailVal) {
    UI.toast('Enter the complete 6-digit OTP', 'error');
    return;
  }
  verifying = true;
  UI.showLoader();
  try {
    await PgOwnerAPI.verifyOtp({ email: emailVal, otp: otp });
    verifySucceeded = true;
    sessionStorage.removeItem('pendingPgOwnerEmail');
    UI.toast('Email verified! Please sign in — admin approval is required before access.');
    window.setTimeout(function () {
      window.location.assign(ctx + '/pg-owner/login?registered=1');
    }, 600);
  } catch (e) {
    UI.toast(e.message, 'error');
    verifying = false;
    document.getElementById('agentOtpBox')?._resetOtpAutoSubmit?.();
  } finally {
    UI.hideLoader();
  }
}
initOtpBoxes('agentOtpBox', 'otp', function () { submitAgentOtp(); });
let sec = 300, resendWait = 30;
const countdownEl = document.getElementById('agentCountdown');
const resendBtn = document.getElementById('resendBtn');
const timer = setInterval(function () {
  sec--;
  countdownEl.textContent = Math.floor(sec / 60) + ':' + String(sec % 60).padStart(2, '0');
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
    const r = await PgOwnerAPI.resendOtp(emailVal);
    UI.toast(r.message || 'OTP resent to your email.', 'success');
    resendBtn.disabled = true;
    resendWait = 30;
    sec = 300;
  } catch (e) { UI.toast(e.message, 'error'); }
  finally { UI.hideLoader(); }
});
document.getElementById('agentOtpForm').addEventListener('submit', function (e) { e.preventDefault(); submitAgentOtp(); });
</script>
<%@ include file="../includes/portal-scripts.jsp" %>
</body>
</html>
