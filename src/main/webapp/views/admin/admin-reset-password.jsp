<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="../includes/init.jsp" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Reset Admin Password | EstateVault</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${ctx}/css/admin.css">
    <link rel="stylesheet" href="${ctx}/css/admin-pages.css">
</head>
<body class="admin-body admin-bg-grid admin-page">
<%@ include file="../includes/admin-particles.jsp" %>
<nav class="admin-nav">
    <a class="admin-brand" href="${ctx}/"><span class="admin-brand-badge">Admin</span> EstateVault</a>
    <a href="${ctx}/admin/login" class="admin-btn-outline">Login</a>
</nav>
<div id="toast-container" class="toast-container"></div>
<div id="loader" class="loader-overlay"><div class="spinner"></div></div>

<main class="admin-split">
    <section class="admin-visual" aria-hidden="true">
        <div class="admin-visual-bg" style="background-image:url('https://images.unsplash.com/photo-1600607687939-ce8a6c25118c?w=1200&q=80');"></div>
        <div class="admin-visual-overlay"></div>
        <div class="admin-visual-scanline"></div>
        <div class="admin-visual-inner">
            <div>
                <span class="admin-visual-tag">New Credentials</span>
                <h1>Strong <span>Admin</span> Password</h1>
                <p class="admin-visual-desc">Minimum 10 characters with uppercase, lowercase, number, and special character.</p>
                <div class="admin-visual-features">
                    <span>&#128274; BCrypt encrypted</span>
                    <span>&#10003; Match confirmation</span>
                    <span>&#9889; Instant unlock</span>
                </div>
            </div>
            <div class="admin-float-badge">
                <strong>One-time token</strong>
                <span>Link expires after use</span>
            </div>
        </div>
    </section>

    <section class="admin-form-panel">
        <div class="admin-glass admin-glass-neon admin-card-animate">
            <c:choose>
                <c:when test="${tokenValid}">
                    <div class="admin-form-header">
                        <div class="admin-icon-wrap" aria-hidden="true">&#128272;</div>
                        <h2>Set New Admin Password</h2>
                        <p>Choose a strong password and confirm below</p>
                    </div>
                    <form id="adminResetForm" class="admin-form-grid" novalidate>
                        <input type="hidden" id="token" value="${resetToken}">
                        <div class="admin-form-group admin-pwd-wrap">
                            <input class="admin-form-control form-control" id="password" name="password" type="password" placeholder=" " required autocomplete="new-password">
                            <label class="admin-floating-label" for="password">New Password</label>
                            <button type="button" class="admin-pwd-toggle pwd-toggle" id="togglePwd" aria-label="Show password">&#128065;</button>
                        </div>
                        <div class="admin-strength-bar strength-bar" id="strAdminReset"><span></span></div>
                        <div class="admin-form-group admin-pwd-wrap">
                            <input class="admin-form-control form-control" id="confirmPassword" name="confirmPassword" type="password" placeholder=" " required autocomplete="new-password">
                            <label class="admin-floating-label" for="confirmPassword">Confirm Password</label>
                            <button type="button" class="admin-pwd-toggle pwd-toggle" id="toggleConfirm" aria-label="Show password">&#128065;</button>
                        </div>
                        <p id="matchHint" class="small mb-0" style="color:var(--admin-muted);">Passwords must match to save.</p>
                        <button type="submit" class="admin-btn-gold w-100" id="saveBtn" disabled>Save Password</button>
                    </form>
                </c:when>
                <c:otherwise>
                    <div class="admin-form-header text-center">
                        <div class="admin-icon-wrap mx-auto" aria-hidden="true">&#9888;</div>
                        <h2>Invalid Reset Link</h2>
                        <p style="color:var(--admin-muted);">This link is missing, expired, or already used.</p>
                    </div>
                    <a href="${ctx}/admin/forgot-password" class="admin-btn-gold w-100 d-inline-block text-center" style="text-decoration:none;">Request New Link</a>
                </c:otherwise>
            </c:choose>
            <p class="text-center mt-3 mb-0"><a href="${ctx}/admin/login" class="admin-link">Back to Admin Login</a></p>
        </div>
    </section>
</main>
<script src="${ctx}/js/auth.js"></script>
<script src="${ctx}/js/admin.js"></script>
<script>AdminAPI.base = '${ctx}';</script>
<c:if test="${tokenValid}">
<script>
initPwdToggle('togglePwd', 'password');
initPwdToggle('toggleConfirm', 'confirmPassword');
initAdminPwdStrength('password', '#strAdminReset');

const pwd = document.getElementById('password');
const confirmPwd = document.getElementById('confirmPassword');
const saveBtn = document.getElementById('saveBtn');
const matchHint = document.getElementById('matchHint');

function updateMatchState() {
  const match = V.passwordsMatch(pwd.value, confirmPwd.value);
  const strong = V.adminPwd(pwd.value);
  saveBtn.disabled = !(match && strong);
  matchHint.textContent = match
    ? (strong ? 'Passwords match. Click Save to update.' : 'Password must be 10+ chars with upper, lower, digit & special.')
    : 'Passwords must match to save.';
  matchHint.style.color = match && strong ? 'var(--admin-success)' : 'var(--admin-muted)';
}

pwd.addEventListener('input', updateMatchState);
confirmPwd.addEventListener('input', updateMatchState);

document.getElementById('adminResetForm').addEventListener('submit', async function (e) {
  e.preventDefault();
  if (!V.adminPwd(pwd.value)) { UI.toast('Password does not meet requirements', 'error'); return; }
  if (!V.passwordsMatch(pwd.value, confirmPwd.value)) { UI.toast('Passwords do not match', 'error'); return; }
  UI.showLoader();
  try {
    await AdminAPI.resetPassword({
      token: document.getElementById('token').value,
      password: pwd.value,
      confirmPassword: confirmPwd.value
    });
    UI.toast('Password updated successfully');
    setTimeout(function () {
      window.location.href = '${ctx}/admin/login?reset=success';
    }, 1500);
  } catch (err) {
    UI.toast(err.message || 'Could not reset password', 'error');
  } finally {
    UI.hideLoader();
  }
});
</script>
</c:if>
<%@ include file="../includes/portal-scripts.jsp" %>
</body>
</html>
