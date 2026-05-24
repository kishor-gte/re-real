<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="../includes/init.jsp" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Forgot Password | EstateVault</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${ctx}/css/admin.css">
    <link rel="stylesheet" href="${ctx}/css/admin-pages.css">
</head>
<body class="admin-body admin-bg-grid admin-page">
<%@ include file="../includes/admin-particles.jsp" %>
<nav class="admin-nav">
    <a class="admin-brand" href="${ctx}/"><span class="admin-brand-badge">Admin</span> EstateVault</a>
    <a href="${ctx}/admin/login" class="admin-btn-outline">Back to Login</a>
</nav>
<div id="toast-container" class="toast-container"></div>
<div id="loader" class="loader-overlay"><div class="spinner"></div></div>

<main class="admin-split">
    <section class="admin-visual" aria-hidden="true">
        <div class="admin-visual-bg" style="background-image:url('https://images.unsplash.com/photo-1454165804606-c3d57bc86b40?w=1200&q=80');"></div>
        <div class="admin-visual-overlay"></div>
        <div class="admin-visual-scanline"></div>
        <div class="admin-visual-inner">
            <div>
                <span class="admin-visual-tag">Account Recovery</span>
                <h1>Reset <span>Admin</span> Access</h1>
                <p class="admin-visual-desc">Verify your official email and registered mobile to receive a secure, time-limited password reset link.</p>
                <div class="admin-visual-features">
                    <span>&#128231; Official email</span>
                    <span>&#128241; Mobile match</span>
                    <span>&#9200; One-time token</span>
                </div>
            </div>
            <div class="admin-float-badge">
                <strong>Verified admins only</strong>
                <span>Email must be activated</span>
            </div>
        </div>
    </section>

    <section class="admin-form-panel">
        <div class="admin-glass admin-glass-neon admin-card-animate">
            <div class="admin-form-header">
                <div class="admin-icon-wrap" aria-hidden="true">&#128273;</div>
                <h2>Forgot Admin Password</h2>
                <p>Enter registered official email and mobile</p>
            </div>
            <form id="adminForgotForm" class="admin-form-grid" novalidate>
                <div class="admin-form-group">
                    <input class="admin-form-control form-control" id="officialEmail" name="officialEmail" type="email" placeholder=" " required autocomplete="email">
                    <label class="admin-floating-label" for="officialEmail">Official Email</label>
                </div>
                <div class="admin-form-group">
                    <input class="admin-form-control form-control" id="mobile" name="mobile" type="tel" placeholder=" " required maxlength="10" inputmode="numeric" autocomplete="tel">
                    <label class="admin-floating-label" for="mobile">Registered Mobile (10 digits)</label>
                </div>
                <button type="submit" class="admin-btn-gold w-100" id="submitBtn">Send Reset Link</button>
            </form>
            <div id="resetLinkBox" class="mt-3 admin-dev-otp-banner" style="display:none;">
                <p class="mb-2 small"><strong>SMTP unavailable — use this link:</strong></p>
                <a id="resetLinkAnchor" href="#" class="admin-link small" style="word-break:break-all;"></a>
            </div>
            <p class="text-center mt-3 mb-0" style="color:var(--admin-muted);">
                Remember password?
                <a href="${ctx}/admin/login" class="admin-link">Admin Sign In</a>
            </p>
        </div>
    </section>
</main>
<script src="${ctx}/js/auth.js"></script>
<script src="${ctx}/js/admin.js"></script>
<script>
AdminAPI.base = '${ctx}';
document.getElementById('mobile').addEventListener('input', function () {
  this.value = this.value.replace(/\D/g, '').slice(0, 10);
});

document.getElementById('adminForgotForm').addEventListener('submit', async function (e) {
  e.preventDefault();
  const officialEmail = document.getElementById('officialEmail').value.trim();
  const mobile = document.getElementById('mobile').value.trim();
  if (!V.email(officialEmail)) { UI.toast('Enter a valid official email', 'error'); return; }
  if (!V.mobile(mobile)) { UI.toast('Enter a valid 10-digit mobile', 'error'); return; }
  UI.showLoader();
  document.getElementById('resetLinkBox').style.display = 'none';
  try {
    const res = await AdminAPI.forgotPassword({
      officialEmail: officialEmail,
      mobile: mobile,
      clientOrigin: window.location.origin
    });
    UI.toast(res.message || 'Reset link sent if account verified');
    if (res.data && res.data.resetUrl) {
      const box = document.getElementById('resetLinkBox');
      const a = document.getElementById('resetLinkAnchor');
      a.href = res.data.resetUrl;
      a.textContent = res.data.resetUrl;
      box.style.display = 'block';
    } else {
      setTimeout(function () {
        window.location.href = '${ctx}/admin/login?reset=sent';
      }, 2500);
    }
  } catch (err) {
    UI.toast(err.message || 'Verification failed', 'error');
  } finally {
    UI.hideLoader();
  }
});
</script>
<%@ include file="../includes/portal-scripts.jsp" %>
</body>
</html>
