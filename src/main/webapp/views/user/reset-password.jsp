<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="../includes/init.jsp" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Reset Password | EstateVault</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${ctx}/css/auth.css">
    <link rel="stylesheet" href="${ctx}/css/auth-pages.css">
</head>
<body class="auth-body auth-bg-orbs auth-page">
<%@ include file="../includes/auth-particles.jsp" %>
<nav class="auth-nav">
    <a class="brand" href="${ctx}/">EstateVault</a>
    <a href="${ctx}/user/login" class="btn-outline">Login</a>
</nav>
<div id="toast-container" class="toast-container"></div>
<div id="loader" class="loader-overlay"><div class="spinner"></div></div>

<main class="auth-split">
    <section class="auth-visual" aria-hidden="true">
        <div class="auth-visual-bg" style="background-image:url('https://images.unsplash.com/photo-1600607687939-ce8a6c25118c?w=1200&q=80');"></div>
        <div class="auth-visual-overlay"></div>
        <div class="auth-visual-inner">
            <div>
                <span class="auth-visual-tag">New Credentials</span>
                <h1>Create A <span>Strong</span> Password</h1>
                <p class="auth-visual-desc">Use uppercase, lowercase, numbers, and special characters to keep your account secure.</p>
                <div class="auth-visual-features">
                    <span>&#128274; BCrypt encrypted</span>
                    <span>&#10003; Match confirmation</span>
                    <span>&#9889; Instant update</span>
                </div>
            </div>
            <div class="auth-float-badge">
                <strong>Secure save</strong>
                <span>One-time reset token</span>
            </div>
        </div>
    </section>

    <section class="auth-form-panel">
        <div class="glass-card auth-card-animate">
            <c:choose>
                <c:when test="${tokenValid}">
                    <div class="auth-form-header">
                        <div class="auth-icon-wrap" aria-hidden="true">&#128272;</div>
                        <h2>Set New Password</h2>
                        <p>Choose a strong password and confirm it below</p>
                    </div>
                    <form id="resetForm" class="form-grid" novalidate>
                        <input type="hidden" id="token" value="${resetToken}">
                        <div class="form-group pwd-wrap">
                            <input class="form-control" id="password" name="password" type="password" placeholder=" " required autocomplete="new-password">
                            <label class="floating-label" for="password">New Password</label>
                            <button type="button" class="pwd-toggle" id="togglePwd" aria-label="Show password">&#128065;</button>
                        </div>
                        <div class="strength-bar" id="strPwd"><span></span></div>
                        <div class="form-group pwd-wrap">
                            <input class="form-control" id="confirmPassword" name="confirmPassword" type="password" placeholder=" " required autocomplete="new-password">
                            <label class="floating-label" for="confirmPassword">Confirm Password</label>
                            <button type="button" class="pwd-toggle" id="toggleConfirm" aria-label="Show password">&#128065;</button>
                        </div>
                        <p id="matchHint" class="small mb-0" style="color:var(--muted);">Passwords must match to save.</p>
                        <button type="submit" class="btn-primary-gold w-100" id="saveBtn" disabled>Save Password</button>
                    </form>
                </c:when>
                <c:otherwise>
                    <div class="auth-form-header text-center">
                        <div class="auth-icon-wrap mx-auto" aria-hidden="true">&#9888;</div>
                        <h2>Invalid Reset Link</h2>
                        <p class="text-muted">This link is missing, expired, or already used.</p>
                    </div>
                    <a href="${ctx}/user/forgot-password" class="btn-primary-gold w-100 d-inline-block text-center" style="text-decoration:none;">Request New Link</a>
                </c:otherwise>
            </c:choose>
            <p class="text-center mt-3 mb-0"><a href="${ctx}/user/login" style="color:var(--gold);text-decoration:none">Back to Login</a></p>
        </div>
    </section>
</main>
<script src="${ctx}/js/auth.js"></script>
<c:if test="${tokenValid}">
<script>
initPwdToggle('togglePwd', 'password');
initPwdToggle('toggleConfirm', 'confirmPassword');
initPwdStrength('password', '#strPwd');

const pwd = document.getElementById('password');
const confirmPwd = document.getElementById('confirmPassword');
const saveBtn = document.getElementById('saveBtn');
const matchHint = document.getElementById('matchHint');

function updateMatchState() {
  const match = V.passwordsMatch(pwd.value, confirmPwd.value);
  const strong = V.pwd(pwd.value);
  saveBtn.disabled = !(match && strong);
  matchHint.textContent = match
    ? (strong ? 'Passwords match. Click Save to update.' : 'Password must meet strength rules.')
    : 'Passwords must match to save.';
  matchHint.style.color = match && strong ? 'var(--success)' : 'var(--muted)';
}

pwd.addEventListener('input', updateMatchState);
confirmPwd.addEventListener('input', updateMatchState);

document.getElementById('resetForm').addEventListener('submit', async e => {
  e.preventDefault();
  if (!V.pwd(pwd.value)) { UI.toast('Password is too weak', 'error'); return; }
  if (!V.passwordsMatch(pwd.value, confirmPwd.value)) { UI.toast('Passwords do not match', 'error'); return; }
  UI.showLoader();
  try {
    await AuthAPI.resetPassword({
      token: document.getElementById('token').value,
      password: pwd.value,
      confirmPassword: confirmPwd.value
    });
    UI.toast('Password updated successfully');
    setTimeout(() => { window.location.href = '${ctx}/user/login?reset=success'; }, 1500);
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
