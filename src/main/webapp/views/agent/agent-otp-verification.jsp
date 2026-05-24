<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="../includes/init.jsp" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Verify Agent OTP | EstateVault</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${ctx}/css/agent.css">
    <link rel="stylesheet" href="${ctx}/css/agent-pages.css">
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
</head>
<body class="agent-body agent-bg-grid agent-page agent-otp-page">
<%@ include file="../includes/agent-particles.jsp" %>
<nav class="agent-nav">
    <a class="agent-brand" href="${ctx}/"><span class="agent-brand-badge">Agent</span> EstateVault</a>
    <a href="${ctx}/agent/login" class="agent-btn-outline">Login</a>
</nav>
<div id="toast-container" class="toast-container"></div>
<div id="loader" class="loader-overlay"><div class="spinner"></div></div>
<main class="agent-split">
    <section class="agent-visual" aria-hidden="true">
        <div class="agent-visual-bg" style="background-image:url('https://images.unsplash.com/photo-1560520031-0aee6d09c04d?w=1200&q=80');"></div>
        <div class="agent-visual-overlay"></div>
        <div class="agent-visual-inner">
            <div>
                <span class="agent-visual-tag">Email Verification</span>
                <h1>Activate <span>Broker</span> Account</h1>
                <p class="agent-visual-desc">Enter the 6-digit OTP sent to your registered email.</p>
            </div>
        </div>
    </section>
    <section class="agent-form-panel">
        <div class="agent-glass agent-glass-neon agent-card-animate text-center">
            <div class="agent-otp-ring">&#9993;</div>
            <h2>Verify OTP</h2>
            <p class="mt-2">Code sent to <strong id="emailDisplay" style="color:var(--agent-gold);"></strong></p>
            <div id="devOtpBanner" class="agent-dev-otp-banner dev-otp-banner text-start mt-2" style="display:none;">
                <strong>SMTP blocked.</strong> Use this OTP:
                <div id="devOtpCode" style="font-size:28px;font-weight:bold;letter-spacing:6px;color:var(--agent-gold);margin-top:8px;"></div>
            </div>
            <p class="small" style="color:var(--agent-muted);">Expires in <span id="agentCountdown">5:00</span></p>
            <form id="agentOtpForm" class="mt-3">
                <input type="hidden" id="email">
                <input type="hidden" id="otp">
                <div class="agent-otp-inputs otp-inputs" id="agentOtpBox">
                    <input maxlength="1" inputmode="numeric" aria-label="digit 1">
                    <input maxlength="1" inputmode="numeric" aria-label="digit 2">
                    <input maxlength="1" inputmode="numeric" aria-label="digit 3">
                    <input maxlength="1" inputmode="numeric" aria-label="digit 4">
                    <input maxlength="1" inputmode="numeric" aria-label="digit 5">
                    <input maxlength="1" inputmode="numeric" aria-label="digit 6">
                </div>
                <button type="submit" class="agent-btn-gold w-100 mt-3">Verify &amp; Activate</button>
            </form>
            <button id="resendBtn" class="agent-btn-outline w-100 mt-2" disabled>Resend OTP (30s)</button>
            <a href="${ctx}/agent/register" class="agent-link small d-inline-block mt-3">&#8592; Back to registration</a>
        </div>
    </section>
</main>
<script src="${ctx}/js/auth.js"></script>
<script src="${ctx}/js/agent.js"></script>
<script>
AgentAPI.base = '${ctx}';
const ctx = '${ctx}';
const emailVal = (new URLSearchParams(location.search).get('email') || sessionStorage.getItem('pendingAgentEmail') || '').trim().toLowerCase();
document.getElementById('email').value = emailVal;
document.getElementById('emailDisplay').textContent = emailVal;
const devOtp = sessionStorage.getItem('devAgentOtp');
const emailSentFlag = sessionStorage.getItem('agentOtpEmailSent');
if (devOtp) {
  document.getElementById('devOtpBanner').style.display = 'block';
  document.getElementById('devOtpCode').textContent = devOtp;
} else if (emailSentFlag === 'false') {
  document.getElementById('devOtpBanner').style.display = 'block';
  document.getElementById('devOtpCode').textContent = '—';
  document.querySelector('#devOtpBanner p').textContent =
    'We could not deliver email. Use Resend OTP or check the server log for delivery errors.';
}
let verifying = false, verifySucceeded = false;
async function submitAgentOtp() {
  if (verifySucceeded || verifying) return;
  const otp = document.getElementById('otp').value.replace(/\D/g, '');
  if (otp.length !== 6 || !emailVal) return;
  verifying = true;
  UI.showLoader();
  try {
    await AgentAPI.verifyOtp({ email: emailVal, otp: otp });
    verifySucceeded = true;
    sessionStorage.removeItem('pendingAgentEmail');
    sessionStorage.removeItem('devAgentOtp');
    UI.toast('Application submitted! Awaiting admin approval…');
    window.setTimeout(function () {
      window.location.assign(ctx + '/agent/pending-approval?email=' + encodeURIComponent(emailVal));
    }, 500);
  } catch (e) {
    UI.toast(e.message, 'error');
    verifying = false;
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
    const r = await AgentAPI.resendOtp(emailVal);
    const sent = r.data && r.data.emailSent !== false;
    sessionStorage.setItem('agentOtpEmailSent', sent ? 'true' : 'false');
    if (r.data && r.data.devOtp) {
      sessionStorage.setItem('devAgentOtp', r.data.devOtp);
      document.getElementById('devOtpBanner').style.display = 'block';
      document.getElementById('devOtpCode').textContent = r.data.devOtp;
    }
    UI.toast(r.message || (sent ? 'OTP resent to your email.' : 'OTP resent — shown on this page.'), sent ? 'success' : 'warning');
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
