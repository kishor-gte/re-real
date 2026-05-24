<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="../includes/init.jsp" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Login | EstateVault</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${ctx}/css/admin.css">
    <link rel="stylesheet" href="${ctx}/css/admin-pages.css">
</head>
<body class="admin-body admin-bg-grid admin-page">
<%@ include file="../includes/admin-particles.jsp" %>
<nav class="admin-nav">
    <a class="admin-brand" href="${ctx}/"><span class="admin-brand-badge">Admin</span> EstateVault</a>
    <div class="admin-nav-actions">
        <a href="${ctx}/" class="admin-btn-ghost">Public Site</a>
        <a href="${ctx}/admin/register" class="admin-btn-gold">Register</a>
    </div>
</nav>
<div id="toast-container" class="toast-container"></div>
<div id="loader" class="loader-overlay"><div class="spinner"></div></div>

<main class="admin-split">
    <section class="admin-visual" aria-hidden="true">
        <div class="admin-visual-bg" style="background-image:url('https://images.unsplash.com/photo-1486406146926-c627a92ad1ab?w=1200&q=80');"></div>
        <div class="admin-visual-overlay"></div>
        <div class="admin-visual-scanline"></div>
        <div class="admin-visual-inner">
            <div>
                <span class="admin-visual-tag">Authorized Personnel</span>
                <h1>Admin <span>Command</span> Center</h1>
                <p class="admin-visual-desc">Secure access to EstateVault operations — manage users, properties, revenue, and platform governance.</p>
                <div class="admin-visual-features">
                    <span>&#128274; Session secured</span>
                    <span>&#128101; Role-based access</span>
                    <span>&#9889; Real-time dashboard</span>
                </div>
            </div>
            <div class="admin-visual-bottom">
                <div class="admin-stat-pills">
                    <div class="admin-stat-pill"><strong>24/7</strong><small>Monitoring</small></div>
                    <div class="admin-stat-pill"><strong>RBAC</strong><small>Enforced</small></div>
                </div>
                <div class="admin-float-badge">
                    <strong>Corporate HQ</strong>
                    <span>EstateVault Operations</span>
                </div>
            </div>
        </div>
    </section>

    <section class="admin-form-panel">
        <div class="admin-glass admin-glass-neon admin-card-animate">
            <div class="admin-form-header">
                <div class="admin-icon-wrap" aria-hidden="true">&#128272;</div>
                <h2>Admin Sign In</h2>
                <p>Official email and password required</p>
            </div>
            <form id="adminLoginForm" class="admin-form-grid" novalidate>
                <div class="admin-form-group">
                    <input class="admin-form-control form-control" id="officialEmail" name="officialEmail" type="email" placeholder=" " required autocomplete="email">
                    <label class="admin-floating-label" for="officialEmail">Official Email</label>
                </div>
                <div class="admin-form-group admin-pwd-wrap">
                    <input class="admin-form-control form-control" id="password" name="password" type="password" placeholder=" " required autocomplete="current-password">
                    <label class="admin-floating-label" for="password">Password</label>
                    <button type="button" class="admin-pwd-toggle pwd-toggle" id="togglePwd" aria-label="Show password">&#128065;</button>
                </div>
                <div class="d-flex justify-content-between align-items-center flex-wrap gap-2">
                    <div class="admin-form-check mb-0">
                        <input type="checkbox" id="rememberMe" name="rememberMe">
                        <label for="rememberMe">Remember Me</label>
                    </div>
                    <a href="${ctx}/admin/forgot-password" class="admin-link small">Forgot Password?</a>
                </div>
                <button type="submit" class="admin-btn-primary w-100">Sign In to Dashboard</button>
            </form>
            <p class="text-center mt-3 mb-0" style="color:var(--admin-muted);">
                Need an admin account?
                <a href="${ctx}/admin/register" class="admin-link">Register here</a>
            </p>
            <p class="text-center mt-2 mb-0">
                <a href="${ctx}/user/login" class="small" style="color:var(--admin-muted);text-decoration:none;">&#8592; User portal login</a>
            </p>
        </div>
    </section>
</main>
<script src="${ctx}/js/auth.js"></script>
<script src="${ctx}/js/admin.js"></script>
<script>
AdminAPI.base = '${ctx}';
initPwdToggle('togglePwd', 'password');
(function () {
  const p = new URLSearchParams(location.search);
  if (p.get('logout') === 'success') UI.toast('Logged out successfully');
  if (p.get('reset') === 'success') UI.toast('Password reset successful. Please sign in.');
})();
document.getElementById('adminLoginForm').addEventListener('submit', async function (e) {
  e.preventDefault();
  const email = document.getElementById('officialEmail').value.trim();
  const password = document.getElementById('password').value;
  if (!V.email(email)) { UI.toast('Enter a valid official email', 'error'); return; }
  UI.showLoader();
  try {
    await AdminAPI.login({
      officialEmail: email,
      password: password,
      rememberMe: document.getElementById('rememberMe').checked
    });
    UI.toast('Admin login successful');
    const ctx = '${ctx}';
    const params = new URLSearchParams(location.search);
    const target = params.get('redirect') || (ctx + '/admin/dashboard');
    location.href = target;
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
