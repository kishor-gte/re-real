<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="../includes/init.jsp" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Reset Agent Password | EstateVault</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${ctx}/css/agent.css">
    <link rel="stylesheet" href="${ctx}/css/agent-pages.css">
</head>
<body class="agent-body agent-bg-grid agent-page">
<%@ include file="../includes/agent-particles.jsp" %>
<nav class="agent-nav">
    <a class="agent-brand" href="${ctx}/"><span class="agent-brand-badge">Agent</span> EstateVault</a>
    <a href="${ctx}/agent/login" class="agent-btn-outline">Login</a>
</nav>
<div id="toast-container" class="toast-container"></div>
<div id="loader" class="loader-overlay"><div class="spinner"></div></div>
<main class="agent-split">
    <section class="agent-form-panel">
        <div class="agent-glass agent-glass-neon agent-card-animate">
            <c:choose>
                <c:when test="${tokenValid}">
                    <div class="agent-form-header">
                        <h2>Set New Password</h2>
                    </div>
                    <form id="agentResetForm" class="agent-form-grid" novalidate>
                        <input type="hidden" id="token" value="${resetToken}">
                        <div class="agent-form-group agent-pwd-wrap">
                            <input class="agent-form-control form-control" id="password" type="password" placeholder=" " required>
                            <label class="agent-floating-label" for="password">New Password</label>
                            <button type="button" class="agent-pwd-toggle pwd-toggle" id="togglePwd">&#128065;</button>
                        </div>
                        <div class="agent-strength-bar strength-bar" id="strReset"><span></span></div>
                        <div class="agent-form-group agent-pwd-wrap">
                            <input class="agent-form-control form-control" id="confirmPassword" type="password" placeholder=" " required>
                            <label class="agent-floating-label" for="confirmPassword">Confirm Password</label>
                        </div>
                        <p id="matchHint" class="small mb-0" style="color:var(--agent-muted);">Passwords must match.</p>
                        <button type="submit" class="agent-btn-gold w-100" id="saveBtn" disabled>Save Password</button>
                    </form>
                </c:when>
                <c:otherwise>
                    <div class="agent-form-header text-center">
                        <h2>Invalid Reset Link</h2>
                        <p style="color:var(--agent-muted);">Link expired or already used.</p>
                    </div>
                    <a href="${ctx}/agent/forgot-password" class="agent-btn-gold w-100 d-inline-block text-center" style="text-decoration:none;">Request New Link</a>
                </c:otherwise>
            </c:choose>
            <p class="text-center mt-3"><a href="${ctx}/agent/login" class="agent-link">Back to Login</a></p>
        </div>
    </section>
</main>
<script src="${ctx}/js/auth.js"></script>
<script src="${ctx}/js/agent.js"></script>
<script>AgentAPI.base = '${ctx}';</script>
<c:if test="${tokenValid}">
<script>
initPwdToggle('togglePwd', 'password');
initAgentPwdStrength('password', '#strReset');
const pwd = document.getElementById('password');
const confirmPwd = document.getElementById('confirmPassword');
const saveBtn = document.getElementById('saveBtn');
function updateMatch() {
  const match = pwd.value === confirmPwd.value;
  const strong = V.agentPwd(pwd.value);
  saveBtn.disabled = !(match && strong);
  document.getElementById('matchHint').textContent = match && strong ? 'Ready to save.' : 'Match passwords and meet requirements.';
}
pwd.addEventListener('input', updateMatch);
confirmPwd.addEventListener('input', updateMatch);
document.getElementById('agentResetForm').addEventListener('submit', async function (e) {
  e.preventDefault();
  UI.showLoader();
  try {
    await AgentAPI.resetPassword({ token: document.getElementById('token').value, password: pwd.value, confirmPassword: confirmPwd.value });
    UI.toast('Password updated');
    location.href = '${ctx}/agent/login?reset=success';
  } catch (err) { UI.toast(err.message, 'error'); }
  finally { UI.hideLoader(); }
});
</script>
</c:if>
<%@ include file="../includes/portal-scripts.jsp" %>
</body>
</html>
