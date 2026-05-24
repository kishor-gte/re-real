<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="../includes/init.jsp" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>PG Owner Forgot Password | EstateVault</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${ctx}/css/pg-owner.css">
    <link rel="stylesheet" href="${ctx}/css/pg-owner-pages.css">
</head>
<body class="pgo-body pgo-bg-grid pgo-page">
<%@ include file="../includes/pg-owner-particles.jsp" %>
<nav class="pgo-nav">
    <a class="pgo-brand" href="${ctx}/"><span class="pgo-brand-badge">PG Owner</span> EstateVault</a>
    <a href="${ctx}/pg-owner/login" class="pgo-btn-outline">Back to Login</a>
</nav>
<div id="toast-container" class="toast-container"></div>
<div id="loader" class="loader-overlay"><div class="spinner"></div></div>
<main class="pgo-split">
    <section class="pgo-visual" aria-hidden="true">
        <div class="pgo-visual-bg" style="background-image:url('https://images.unsplash.com/photo-1454165804606-c3d57bc86b40?w=1200&q=80');"></div>
        <div class="pgo-visual-overlay"></div>
        <div class="pgo-visual-inner">
            <div>
                <span class="pgo-visual-tag">Account Recovery</span>
                <h1>Reset <span>PG Owner</span> Password</h1>
                <p class="pgo-visual-desc">Verify email and mobile to receive a secure reset link.</p>
            </div>
        </div>
    </section>
    <section class="pgo-form-panel">
        <div class="pgo-glass pgo-glass-neon pgo-card-animate">
            <div class="pgo-form-header">
                <div class="pgo-icon-wrap">&#128273;</div>
                <h2>Forgot Password</h2>
                <p>Registered email and mobile required</p>
            </div>
            <form id="agentForgotForm" class="pgo-form-grid" novalidate>
                <div class="pgo-form-group">
                    <input class="pgo-form-control form-control" id="email" type="email" placeholder=" " required>
                    <label class="pgo-floating-label" for="email">Email Address</label>
                </div>
                <div class="pgo-form-group">
                    <input class="pgo-form-control form-control" id="mobile" maxlength="10" placeholder=" " required>
                    <label class="pgo-floating-label" for="mobile">Registered Mobile</label>
                </div>
                <button type="submit" class="pgo-btn-gold w-100">Send Reset Link</button>
            </form>
            <div id="resetLinkBox" class="mt-3 pgo-dev-otp-banner" style="display:none;">
                <p class="mb-2 small"><strong>SMTP unavailable — use this link:</strong></p>
                <a id="resetLinkAnchor" href="#" class="pgo-link small" style="word-break:break-all;"></a>
            </div>
            <p class="text-center mt-3 mb-0" style="color:var(--pgo-muted);">
                <a href="${ctx}/pg-owner/login" class="pgo-link">PG Owner Sign In</a>
            </p>
        </div>
    </section>
</main>
<script src="${ctx}/js/auth.js"></script>
<script src="${ctx}/js/pg-owner.js"></script>
<script>
PgOwnerAPI.base = '${ctx}';
document.getElementById('mobile').addEventListener('input', function () {
  this.value = this.value.replace(/\D/g, '').slice(0, 10);
});
document.getElementById('agentForgotForm').addEventListener('submit', async function (e) {
  e.preventDefault();
  const email = document.getElementById('email').value.trim();
  const mobile = document.getElementById('mobile').value.trim();
  if (!V.email(email) || !V.mobile(mobile)) { UI.toast('Check email and mobile', 'error'); return; }
  UI.showLoader();
  try {
    const res = await PgOwnerAPI.forgotPassword({ email: email, mobile: mobile, clientOrigin: window.location.origin });
    UI.toast(res.message || 'Reset link sent');
    if (res.data && res.data.resetUrl) {
      const box = document.getElementById('resetLinkBox');
      const a = document.getElementById('resetLinkAnchor');
      a.href = res.data.resetUrl;
      a.textContent = res.data.resetUrl;
      box.style.display = 'block';
    }
  } catch (err) { UI.toast(err.message, 'error'); }
  finally { UI.hideLoader(); }
});
</script>
<%@ include file="../includes/portal-scripts.jsp" %>
</body>
</html>

