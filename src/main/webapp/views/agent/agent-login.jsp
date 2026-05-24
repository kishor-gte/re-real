<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="../includes/init.jsp" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Agent Login | EstateVault</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${ctx}/css/agent.css">
    <link rel="stylesheet" href="${ctx}/css/agent-pages.css">
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
</head>
<body class="agent-body agent-bg-grid agent-page">
<%@ include file="../includes/agent-particles.jsp" %>
<nav class="agent-nav">
    <a class="agent-brand" href="${ctx}/"><span class="agent-brand-badge">Agent</span> EstateVault</a>
    <div class="agent-nav-actions">
        <a href="${ctx}/" class="agent-btn-ghost">Public Site</a>
        <a href="${ctx}/agent/register" class="agent-btn-gold">Register</a>
    </div>
</nav>
<div id="toast-container" class="toast-container"></div>
<div id="loader" class="loader-overlay"><div class="spinner"></div></div>

<main class="agent-split">
    <section class="agent-visual" aria-hidden="true">
        <div class="agent-visual-bg" style="background-image:url('https://images.unsplash.com/photo-1560518883-ce09059eeffa?w=1200&q=80');"></div>
        <div class="agent-visual-overlay"></div>
        <div class="agent-visual-scanline"></div>
        <div class="agent-visual-inner">
            <div>
                <span class="agent-visual-tag">Licensed Brokers</span>
                <h1>Agent <span>Portal</span></h1>
                <p class="agent-visual-desc">Manage listings, leads, and client relationships from your premium broker command center.</p>
                <div class="agent-visual-features">
                    <span>&#127968; Property management</span>
                    <span>&#128200; Analytics &amp; leads</span>
                    <span>&#128274; Secure session</span>
                </div>
            </div>
        </div>
    </section>
    <section class="agent-form-panel">
        <div class="agent-glass agent-glass-neon agent-card-animate">
            <div class="agent-form-header">
                <div class="agent-icon-wrap">&#128188;</div>
                <h2>Agent Sign In</h2>
                <p>Email and password required</p>
            </div>
            <form id="agentLoginForm" class="agent-form-grid" novalidate>
                <div class="agent-form-group">
                    <input class="agent-form-control form-control" id="email" name="email" type="email" placeholder=" " required autocomplete="email">
                    <label class="agent-floating-label" for="email">Email Address</label>
                </div>
                <div class="agent-form-group agent-pwd-wrap">
                    <input class="agent-form-control form-control" id="password" name="password" type="password" placeholder=" " required autocomplete="current-password">
                    <label class="agent-floating-label" for="password">Password</label>
                    <button type="button" class="agent-pwd-toggle pwd-toggle" id="togglePwd">&#128065;</button>
                </div>
                <div class="d-flex justify-content-between align-items-center flex-wrap gap-2">
                    <div class="agent-form-check mb-0">
                        <input type="checkbox" id="rememberMe" name="rememberMe">
                        <label for="rememberMe">Remember Me</label>
                    </div>
                    <a href="${ctx}/agent/forgot-password" class="agent-link small">Forgot Password?</a>
                </div>
                <button type="submit" class="agent-btn-gold w-100">Sign In to Dashboard</button>
            </form>
            <p class="text-center mt-3 mb-0" style="color:var(--agent-muted);">
                New agent? <a href="${ctx}/agent/register" class="agent-link">Register here</a>
            </p>
        </div>
    </section>
</main>
<script src="${ctx}/js/auth.js"></script>
<script src="${ctx}/js/agent.js"></script>
<script>
AgentAPI.base = '${ctx}';
initPwdToggle('togglePwd', 'password');
(function () {
  const p = new URLSearchParams(location.search);
  if (p.get('logout') === 'success') UI.toast('Logged out successfully');
  if (p.get('reset') === 'success') UI.toast('Password reset successful. Please sign in.');
  if (p.get('pending') === '1') UI.toast('Your application is pending admin approval. You will be notified by email.');
})();
document.getElementById('agentLoginForm').addEventListener('submit', async function (e) {
  e.preventDefault();
  const email = document.getElementById('email').value.trim();
  const password = document.getElementById('password').value;
  if (!V.email(email)) { UI.toast('Enter a valid email', 'error'); return; }
  UI.showLoader();
  try {
    await AgentAPI.login({ email: email, password: password, rememberMe: document.getElementById('rememberMe').checked });
    if (typeof Swal !== 'undefined') {
      await Swal.fire({ title: 'Welcome back', text: 'Redirecting to your dashboard…', icon: 'success', timer: 1500, showConfirmButton: false });
    }
    const ctx = '${ctx}';
    const params = new URLSearchParams(location.search);
    location.href = params.get('redirect') || (ctx + '/agent/dashboard');
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
