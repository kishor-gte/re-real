<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="../includes/init.jsp" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Agent Forgot Password | EstateVault</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${ctx}/css/agent.css">
    <link rel="stylesheet" href="${ctx}/css/agent-pages.css">
</head>
<body class="agent-body agent-bg-grid agent-page">
<%@ include file="../includes/agent-particles.jsp" %>
<nav class="agent-nav">
    <a class="agent-brand" href="${ctx}/"><span class="agent-brand-badge">Agent</span> EstateVault</a>
    <a href="${ctx}/agent/login" class="agent-btn-outline">Back to Login</a>
</nav>
<div id="toast-container" class="toast-container"></div>
<div id="loader" class="loader-overlay"><div class="spinner"></div></div>
<main class="agent-split">
    <section class="agent-visual" aria-hidden="true">
        <div class="agent-visual-bg" style="background-image:url('https://images.unsplash.com/photo-1454165804606-c3d57bc86b40?w=1200&q=80');"></div>
        <div class="agent-visual-overlay"></div>
        <div class="agent-visual-inner">
            <div>
                <span class="agent-visual-tag">Account Recovery</span>
                <h1>Reset <span>Agent</span> Password</h1>
                <p class="agent-visual-desc">Verify email and mobile to receive a secure reset link.</p>
            </div>
        </div>
    </section>
    <section class="agent-form-panel">
        <div class="agent-glass agent-glass-neon agent-card-animate">
            <div class="agent-form-header">
                <div class="agent-icon-wrap">&#128273;</div>
                <h2>Forgot Password</h2>
                <p>Registered email and mobile required</p>
            </div>
            <form id="agentForgotForm" class="agent-form-grid" novalidate>
                <div class="agent-form-group">
                    <input class="agent-form-control form-control" id="email" type="email" placeholder=" " required>
                    <label class="agent-floating-label" for="email">Email Address</label>
                </div>
                <div class="agent-form-group">
                    <input class="agent-form-control form-control" id="mobile" maxlength="10" placeholder=" " required>
                    <label class="agent-floating-label" for="mobile">Registered Mobile</label>
                </div>
                <button type="submit" class="agent-btn-gold w-100">Send Reset Link</button>
            </form>
            <div id="resetLinkBox" class="mt-3 agent-dev-otp-banner" style="display:none;">
                <p class="mb-2 small"><strong>SMTP unavailable — use this link:</strong></p>
                <a id="resetLinkAnchor" href="#" class="agent-link small" style="word-break:break-all;"></a>
            </div>
            <p class="text-center mt-3 mb-0" style="color:var(--agent-muted);">
                <a href="${ctx}/agent/login" class="agent-link">Agent Sign In</a>
            </p>
        </div>
    </section>
</main>
<script src="${ctx}/js/auth.js"></script>
<script src="${ctx}/js/agent.js"></script>
<script>
AgentAPI.base = '${ctx}';
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
    const res = await AgentAPI.forgotPassword({ email: email, mobile: mobile, clientOrigin: window.location.origin });
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
