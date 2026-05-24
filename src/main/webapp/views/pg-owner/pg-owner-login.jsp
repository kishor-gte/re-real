<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="../includes/init.jsp" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>PG Owner Login | EstateVault</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${ctx}/css/pg-owner.css">
    <link rel="stylesheet" href="${ctx}/css/pg-owner-pages.css">
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
</head>
<body class="pgo-body pgo-bg-grid pgo-page">
<%@ include file="../includes/pg-owner-particles.jsp" %>
<nav class="pgo-nav">
    <a class="pgo-brand" href="${ctx}/"><span class="pgo-brand-badge">PG Owner</span> EstateVault</a>
    <div class="pgo-nav-actions">
        <a href="${ctx}/" class="pgo-btn-ghost">Public Site</a>
        <a href="${ctx}/pg-owner/register" class="pgo-btn-gold">Register</a>
    </div>
</nav>
<div id="toast-container" class="toast-container"></div>
<div id="loader" class="loader-overlay"><div class="spinner"></div></div>

<main class="pgo-split">
    <section class="pgo-visual" aria-hidden="true">
        <div class="pgo-visual-bg" style="background-image:url('https://images.unsplash.com/photo-1560518883-ce09059eeffa?w=1200&q=80');"></div>
        <div class="pgo-visual-overlay"></div>
        <div class="pgo-visual-scanline"></div>
        <div class="pgo-visual-inner">
            <div>
                <span class="pgo-visual-tag">PG & Hostel Owners</span>
                <h1>PG Owner <span>Portal</span></h1>
                <p class="pgo-visual-desc">Manage your PG or hostel listings, tenants, and bookings from your premium accommodation command center.</p>
                <div class="pgo-visual-features">
                    <span>&#127968; Property management</span>
                    <span>&#128200; Analytics &amp; leads</span>
                    <span>&#128274; Secure session</span>
                </div>
            </div>
        </div>
    </section>
    <section class="pgo-form-panel">
        <div class="pgo-glass pgo-glass-neon pgo-card-animate">
            <div class="pgo-form-header">
                <div class="pgo-icon-wrap">&#128188;</div>
                <h2>PG Owner Sign In</h2>
                <p>Email and password required</p>
            </div>
            <form id="pgoLoginForm" class="pgo-form-grid" novalidate>
                <div class="pgo-form-group">
                    <input class="pgo-form-control form-control" id="email" name="email" type="email" placeholder=" " required autocomplete="email">
                    <label class="pgo-floating-label" for="email">Email Address</label>
                </div>
                <div class="pgo-form-group pgo-pwd-wrap">
                    <input class="pgo-form-control form-control" id="password" name="password" type="password" placeholder=" " required autocomplete="current-password">
                    <label class="pgo-floating-label" for="password">Password</label>
                    <button type="button" class="pgo-pwd-toggle pwd-toggle" id="togglePwd">&#128065;</button>
                </div>
                <div class="d-flex justify-content-between align-items-center flex-wrap gap-2">
                    <div class="pgo-form-check mb-0">
                        <input type="checkbox" id="rememberMe" name="rememberMe">
                        <label for="rememberMe">Remember Me</label>
                    </div>
                    <a href="${ctx}/pg-owner/forgot-password" class="pgo-link small">Forgot Password?</a>
                </div>
                <button type="submit" class="pgo-btn-gold w-100">Sign In to Dashboard</button>
            </form>
            <p class="text-center mt-3 mb-0" style="color:var(--pgo-muted);">
                New PG owner? <a href="${ctx}/pg-owner/register" class="pgo-link">Register here</a>
            </p>
        </div>
    </section>
</main>
<script src="${ctx}/js/auth.js"></script>
<script src="${ctx}/js/pg-owner.js"></script>
<script>
PgOwnerAPI.base = '${ctx}';
initPwdToggle('togglePwd', 'password');
(function () {
  const p = new URLSearchParams(location.search);
  if (p.get('logout') === 'success') UI.toast('Logged out successfully');
  if (p.get('reset') === 'success') UI.toast('Password reset successful. Please sign in.');
  if (p.get('pending') === '1') UI.toast('Your application is pending admin approval. You will be notified by email.');
  if (p.get('registered') === '1') UI.toast('Registration complete. Sign in after admin approves your account.', 'success');
  <c:if test="${showPendingMessage}">UI.toast('Your application is pending admin approval. You will be notified by email.');</c:if>
})();
document.getElementById('pgoLoginForm').addEventListener('submit', async function (e) {
  e.preventDefault();
  const email = document.getElementById('email').value.trim();
  const password = document.getElementById('password').value;
  if (!V.email(email)) { UI.toast('Enter a valid email', 'error'); return; }
  UI.showLoader();
  try {
    await PgOwnerAPI.login({ email: email, password: password, rememberMe: document.getElementById('rememberMe').checked });
    if (typeof Swal !== 'undefined') {
      await Swal.fire({ title: 'Welcome back', text: 'Redirecting to your dashboard…', icon: 'success', timer: 1500, showConfirmButton: false });
    }
    const ctx = '${ctx}';
    const params = new URLSearchParams(location.search);
    location.href = params.get('redirect') || (ctx + '/pg-owner/dashboard');
  } catch (err) {
    UI.toast(err.message || 'Invalid email or password', 'error');
  } finally {
    UI.hideLoader();
  }
});
</script>
<%@ include file="../includes/portal-scripts.jsp" %>
</body>
</html>

