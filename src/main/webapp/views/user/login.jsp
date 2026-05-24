<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="../includes/init.jsp" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login | EstateVault</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${ctx}/css/auth.css">
    <link rel="stylesheet" href="${ctx}/css/auth-pages.css">
</head>
<body class="auth-body auth-bg-orbs auth-page">
<%@ include file="../includes/auth-particles.jsp" %>
<nav class="auth-nav">
    <a class="brand" href="${ctx}/">EstateVault</a>
    <a href="${ctx}/user/register" class="btn-primary-gold">Register</a>
</nav>
<div id="toast-container" class="toast-container"></div>
<div id="loader" class="loader-overlay"><div class="spinner"></div></div>

<main class="auth-split">
    <section class="auth-visual" aria-hidden="true">
        <div class="auth-visual-bg" style="background-image:url('https://images.unsplash.com/photo-1600596542815-ffad4c1539a9?w=1200&q=80');"></div>
        <div class="auth-visual-overlay"></div>
        <div class="auth-visual-inner">
            <div>
                <span class="auth-visual-tag">Premium Real Estate</span>
                <h1>Welcome <span>Back</span> Home</h1>
                <p class="auth-visual-desc">Sign in to access verified listings, your dashboard, and personalized property recommendations.</p>
                <div class="auth-visual-features">
                    <span>&#10003; Secure JWT login</span>
                    <span>&#10003; Verified accounts</span>
                    <span>&#10003; Buyer &amp; Seller roles</span>
                </div>
            </div>
            <div class="auth-visual-bottom">
                <div class="auth-stat-pills">
                    <div class="auth-stat-pill"><strong>2,400+</strong><small>Properties</small></div>
                    <div class="auth-stat-pill"><strong>98%</strong><small>Satisfaction</small></div>
                </div>
                <div class="auth-float-badge">
                    <strong>EstateVault</strong>
                    <span>Trusted since 2010</span>
                </div>
            </div>
        </div>
    </section>

    <section class="auth-form-panel">
        <div class="glass-card auth-card-animate">
            <div class="auth-form-header">
                <div class="auth-icon-wrap" aria-hidden="true">&#128274;</div>
                <h2>Sign In</h2>
                <p>Enter your credentials to continue</p>
            </div>
            <form id="loginForm" class="form-grid" novalidate>
                <div class="form-group">
                    <input class="form-control" id="email" name="email" type="email" placeholder=" " required autocomplete="email">
                    <label class="floating-label" for="email">Email Address</label>
                </div>
                <div class="form-group pwd-wrap">
                    <input class="form-control" id="password" name="password" type="password" placeholder=" " required autocomplete="current-password">
                    <label class="floating-label" for="password">Password</label>
                    <button type="button" class="pwd-toggle" id="togglePwd" aria-label="Show password">&#128065;</button>
                </div>
                <div class="d-flex justify-content-between align-items-center flex-wrap gap-2">
                    <div class="form-check mb-0">
                        <input class="form-check-input" type="checkbox" id="rememberMe" name="rememberMe">
                        <label class="form-check-label" for="rememberMe">Remember Me</label>
                    </div>
                    <a href="${ctx}/user/forgot-password" class="small" style="color:var(--gold);text-decoration:none;">Forgot Password?</a>
                </div>
                <button type="submit" class="btn-primary-gold w-100">Sign In</button>
            </form>
            <p class="text-center mt-3 mb-0 text-muted"><span style="color:white;">New user? </span><a href="${ctx}/user/register" style="color:var(--gold);text-decoration:none">Create account</a></p>
            <p class="text-center mt-2 mb-0"><a href="${ctx}/" class="small text-muted">&#8592; <span style="color:white;text-decoration:none">Back to Home</span></a></p>
        </div>
    </section>
</main>
<script src="${ctx}/js/auth.js"></script>
<script>
initPwdToggle('togglePwd','password');
(function () {
  const p = new URLSearchParams(location.search);
  if (p.get('reset') === 'success') UI.toast('Password reset successful. Please sign in.');
  if (p.get('reset') === 'sent') UI.toast('If verified, a reset link was sent to your email.');
  if (p.get('logout') === 'success') UI.toast('Logged out successfully');
})();
document.getElementById('loginForm').addEventListener('submit', async e => {
  e.preventDefault();
  UI.showLoader();
  try {
    const remember = document.getElementById('rememberMe').checked;
    const res = await AuthAPI.login({
      email: document.getElementById('email').value.trim(),
      password: document.getElementById('password').value,
      rememberMe: remember
    });
    if (res.data && res.data.accessToken) {
      TokenStore.set(res.data.accessToken, remember);
    }
    UI.toast('Login successful');
    const ctx = '${ctx}';
    const params = new URLSearchParams(location.search);
    const customRedirect = params.get('redirect');
    let target = customRedirect || (res.data && res.data.redirectUrl) || (ctx + '/user/dashboard');
    if (!customRedirect && target.indexOf('/user/dashboard') !== -1) {
      target += (target.indexOf('?') === -1 ? '?' : '&') + 'welcome=1';
    }
    location.href = target;
  } catch(err) { UI.toast(err.message || 'Invalid email or password', 'error'); }
  finally { UI.hideLoader(); }
});
</script>
<%@ include file="../includes/portal-scripts.jsp" %>
</body>
</html>
