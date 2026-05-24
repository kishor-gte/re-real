<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="../includes/init.jsp" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Forgot Password | EstateVault</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${ctx}/css/auth.css">
    <link rel="stylesheet" href="${ctx}/css/auth-pages.css">
</head>
<body class="auth-body auth-bg-orbs auth-page">
<%@ include file="../includes/auth-particles.jsp" %>
<nav class="auth-nav">
    <a class="brand" href="${ctx}/">EstateVault</a>
    <a href="${ctx}/user/login" class="btn-outline">Back to Login</a>
</nav>
<div id="toast-container" class="toast-container"></div>
<div id="loader" class="loader-overlay"><div class="spinner"></div></div>

<main class="auth-split">
    <section class="auth-visual" aria-hidden="true">
        <div class="auth-visual-bg" style="background-image:url('https://images.unsplash.com/photo-1600585154340-be6161a56a0c?w=1200&q=80');"></div>
        <div class="auth-visual-overlay"></div>
        <div class="auth-visual-inner">
            <div>
                <span class="auth-visual-tag">Account Recovery</span>
                <h1>Reset Your <span>Password</span></h1>
                <p class="auth-visual-desc">We verify your email and phone together so only you can recover your account.</p>
                <div class="auth-visual-features">
                    <span>&#128231; Email verification</span>
                    <span>&#128241; Phone match</span>
                    <span>&#9200; Time-limited link</span>
                </div>
            </div>
            <div class="auth-float-badge">
                <strong>Secure reset</strong>
                <span>One-time token link</span>
            </div>
        </div>
    </section>

    <section class="auth-form-panel">
        <div class="glass-card auth-card-animate">
            <div class="auth-form-header">
                <div class="auth-icon-wrap" aria-hidden="true">&#128273;</div>
                <h2>Forgot Password</h2>
                <p>Enter registered email and phone number</p>
            </div>
            <form id="forgotForm" class="form-grid" novalidate>
                <div class="form-group">
                    <input class="form-control" id="email" name="email" type="email" placeholder=" " required autocomplete="email">
                    <label class="floating-label" for="email">Registered Email</label>
                </div>
                <div class="form-group">
                    <input class="form-control" id="mobile" name="mobile" type="tel" placeholder=" " required
                           maxlength="10" pattern="\d{10}" inputmode="numeric" autocomplete="tel">
                    <label class="floating-label" for="mobile">Registered Phone (10 digits)</label>
                </div>
                <button type="submit" class="btn-primary-gold w-100" id="submitBtn">Send Reset Link</button>
            </form>
            <div id="resetLinkBox" class="mt-3 p-3 rounded dev-otp-banner" style="display:none;">
                <p class="mb-2 small"><strong>SMTP unavailable — use this link:</strong></p>
                <a id="resetLinkAnchor" href="#" class="small" style="color:#93c5fd;word-break:break-all;"></a>
            </div>
            <p class="text-center mt-3 mb-0 text-muted"><span style="color:white;">Remember password? </span><a href="${ctx}/user/login" style="color:var(--gold);text-decoration:none">Sign In</a></p>
        </div>
    </section>
</main>
<script src="${ctx}/js/auth.js"></script>
<script>
document.getElementById('mobile').addEventListener('input', function () {
  this.value = this.value.replace(/\D/g, '').slice(0, 10);
});
document.getElementById('forgotForm').addEventListener('submit', async e => {
  e.preventDefault();
  const email = document.getElementById('email').value.trim();
  const mobile = document.getElementById('mobile').value.trim();
  if (!V.email(email)) { UI.toast('Enter a valid email', 'error'); return; }
  if (!V.mobile(mobile)) { UI.toast('Enter a valid 10-digit phone number', 'error'); return; }
  UI.showLoader();
  document.getElementById('resetLinkBox').style.display = 'none';
  try {
    const res = await AuthAPI.forgotPassword({ email, mobile, clientOrigin: window.location.origin });
    UI.toast(res.message || 'Reset link sent');
    if (res.data && res.data.resetUrl) {
      const box = document.getElementById('resetLinkBox');
      const a = document.getElementById('resetLinkAnchor');
      a.href = res.data.resetUrl;
      a.textContent = res.data.resetUrl;
      box.style.display = 'block';
    } else {
      setTimeout(() => { window.location.href = '${ctx}/user/login?reset=sent'; }, 2500);
    }
  } catch (err) {
    UI.toast(err.message || 'Verification failed', 'error');
  } finally {
    UI.hideLoader();
  }
});
</script>
<%@ include file="../includes/portal-scripts.jsp" %>
</body>
</html>
