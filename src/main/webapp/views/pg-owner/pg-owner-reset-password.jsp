<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="../includes/init.jsp" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Reset PG Owner Password | EstateVault</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${ctx}/css/pg-owner.css">
    <link rel="stylesheet" href="${ctx}/css/pg-owner-pages.css">
</head>
<body class="pgo-body pgo-bg-grid pgo-page">
<%@ include file="../includes/pg-owner-particles.jsp" %>
<nav class="pgo-nav">
    <a class="pgo-brand" href="${ctx}/"><span class="pgo-brand-badge">PG Owner</span> EstateVault</a>
    <a href="${ctx}/pg-owner/login" class="pgo-btn-outline">Login</a>
</nav>
<div id="toast-container" class="toast-container"></div>
<div id="loader" class="loader-overlay"><div class="spinner"></div></div>
<main class="pgo-split">
    <section class="pgo-form-panel">
        <div class="pgo-glass pgo-glass-neon pgo-card-animate">
            <c:choose>
                <c:when test="${tokenValid}">
                    <div class="pgo-form-header">
                        <h2>Set New Password</h2>
                    </div>
                    <form id="agentResetForm" class="pgo-form-grid" novalidate>
                        <input type="hidden" id="token" value="${resetToken}">
                        <div class="pgo-form-group pgo-pwd-wrap">
                            <input class="pgo-form-control form-control" id="password" type="password" placeholder=" " required>
                            <label class="pgo-floating-label" for="password">New Password</label>
                            <button type="button" class="pgo-pwd-toggle pwd-toggle" id="togglePwd">&#128065;</button>
                        </div>
                        <div class="pgo-strength-bar strength-bar" id="strReset"><span></span></div>
                        <div class="pgo-form-group pgo-pwd-wrap">
                            <input class="pgo-form-control form-control" id="confirmPassword" type="password" placeholder=" " required>
                            <label class="pgo-floating-label" for="confirmPassword">Confirm Password</label>
                        </div>
                        <p id="matchHint" class="small mb-0" style="color:var(--pgo-muted);">Passwords must match.</p>
                        <button type="submit" class="pgo-btn-gold w-100" id="saveBtn" disabled>Save Password</button>
                    </form>
                </c:when>
                <c:otherwise>
                    <div class="pgo-form-header text-center">
                        <h2>Invalid Reset Link</h2>
                        <p style="color:var(--pgo-muted);">Link expired or already used.</p>
                    </div>
                    <a href="${ctx}/pg-owner/forgot-password" class="pgo-btn-gold w-100 d-inline-block text-center" style="text-decoration:none;">Request New Link</a>
                </c:otherwise>
            </c:choose>
            <p class="text-center mt-3"><a href="${ctx}/pg-owner/login" class="pgo-link">Back to Login</a></p>
        </div>
    </section>
</main>
<script src="${ctx}/js/auth.js"></script>
<script src="${ctx}/js/pg-owner.js"></script>
<script>PgOwnerAPI.base = '${ctx}';</script>
<c:if test="${tokenValid}">
<script>
initPwdToggle('togglePwd', 'password');
initPgoPwdStrength('password', '#strReset');
const pwd = document.getElementById('password');
const confirmPwd = document.getElementById('confirmPassword');
const saveBtn = document.getElementById('saveBtn');
function updateMatch() {
  const match = pwd.value === confirmPwd.value;
  const strong = V.pgoPwd(pwd.value);
  saveBtn.disabled = !(match && strong);
  document.getElementById('matchHint').textContent = match && strong ? 'Ready to save.' : 'Match passwords and meet requirements.';
}
pwd.addEventListener('input', updateMatch);
confirmPwd.addEventListener('input', updateMatch);
document.getElementById('agentResetForm').addEventListener('submit', async function (e) {
  e.preventDefault();
  UI.showLoader();
  try {
    await PgOwnerAPI.resetPassword({ token: document.getElementById('token').value, password: pwd.value, confirmPassword: confirmPwd.value });
    UI.toast('Password updated');
    location.href = '${ctx}/pg-owner/login?reset=success';
  } catch (err) { UI.toast(err.message, 'error'); }
  finally { UI.hideLoader(); }
});
</script>
</c:if>
<%@ include file="../includes/portal-scripts.jsp" %>
</body>
</html>

