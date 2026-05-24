<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="../includes/init.jsp" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard | EstateVault</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${ctx}/css/auth.css">
    <link rel="stylesheet" href="${ctx}/css/auth-pages.css">
    <link rel="stylesheet" href="${ctx}/css/dashboard.css">
    <link rel="stylesheet" href="${ctx}/css/user-portal.css">
</head>
<body class="auth-body auth-bg-orbs auth-page dashboard-page user-portal-page">
<%@ include file="../includes/auth-particles.jsp" %>
<%@ include file="../includes/user-portal-topnav.jsp" %>
<div class="user-portal-wrap">
<%@ include file="../includes/user-sidebar.jsp" %>
<main class="user-portal-main auth-container wide" style="max-width:none;">

    <c:if test="${showWelcome}">
        <div class="dashboard-alert dashboard-alert-success" id="welcomeAlert" role="status">
            <span class="dashboard-alert-icon" aria-hidden="true">&#10003;</span>
            <div>
                <strong>Login successful!</strong>
                <p class="mb-0 mt-1" style="font-size:0.92rem;opacity:0.95;">
                    Welcome to EstateVault, <strong>${firstName}</strong>. Your session is active and secure.
                </p>
            </div>
            <button type="button" class="dashboard-alert-close" id="dismissWelcome" aria-label="Dismiss">&times;</button>
        </div>
    </c:if>

    <section class="dashboard-welcome" aria-labelledby="welcomeHeading">
        <span class="dashboard-welcome-badge">Your Dashboard</span>
        <h1 id="welcomeHeading">Welcome back, <span>${firstName}</span>!</h1>
        <p class="dashboard-welcome-sub">
            Hello <strong>${userName}</strong> — manage your EstateVault account, explore properties,
            and track your real estate journey from one place.
        </p>
        <div class="dashboard-welcome-meta">
            <span>&#128231; ${userEmail}</span>
            <span>&#128100; ${userRole}</span>
            <c:if test="${verified}">
                <span>&#10003; Email verified</span>
            </c:if>
        </div>
    </section>

    <div class="dashboard-profile">
        <div class="dashboard-avatar" aria-hidden="true">${userInitial}</div>
        <div class="dashboard-profile-info">
            <h2>${userName}</h2>
            <p>${userEmail}</p>
            <span class="dashboard-role-pill ${userRoleCode eq 'SELLER' ? 'seller' : ''}">${userRole}</span>
        </div>
    </div>

    <div class="dashboard-stats">
        <div class="dashboard-stat-card">
            <div class="dashboard-stat-label">Account Status</div>
            <p class="dashboard-stat-value ${accountStatusCode eq 'ACTIVE' ? 'status-active' : 'status-pending'}">${accountStatus}</p>
        </div>
        <div class="dashboard-stat-card">
            <div class="dashboard-stat-label">Member Since</div>
            <p class="dashboard-stat-value">${memberSince}</p>
        </div>
        <div class="dashboard-stat-card">
            <div class="dashboard-stat-label">Location</div>
            <p class="dashboard-stat-value">${userCity}</p>
        </div>
        <div class="dashboard-stat-card">
            <div class="dashboard-stat-label">Referral Code</div>
            <p class="dashboard-stat-value">${referralCode}</p>
        </div>
    </div>

    <h3 class="dashboard-section-title">Quick Actions</h3>
    <div class="dashboard-actions">
        <a href="${ctx}/user/properties" class="dashboard-action-card">
            <span class="dashboard-action-icon" aria-hidden="true">&#127968;</span>
            <h3>Explore Properties</h3>
            <p>Browse agent listings by type — sale &amp; rent</p>
        </a>
        <a href="${ctx}/user/pgs" class="dashboard-action-card">
            <span class="dashboard-action-icon" aria-hidden="true">&#127976;</span>
            <h3>Explore PG</h3>
            <p>Find active PG accommodations near ${userCity}</p>
        </a>
        <a href="${ctx}/user/enquiries" class="dashboard-action-card">
            <span class="dashboard-action-icon" aria-hidden="true">&#128172;</span>
            <h3>My Enquiries</h3>
            <p>Track agent replies on your enquiries</p>
        </a>
        <a href="${ctx}/user/bookings" class="dashboard-action-card">
            <span class="dashboard-action-icon" aria-hidden="true">&#128179;</span>
            <h3>My Bookings</h3>
            <p>View payments, receipts &amp; booking status</p>
        </a>
        <div class="dashboard-action-card dashboard-action-disabled" title="Coming soon">
            <span class="dashboard-action-icon" aria-hidden="true">&#9881;</span>
            <h3>Edit Profile</h3>
            <p>Update your details (coming soon)</p>
        </div>
        <a href="${ctx}/#properties" class="dashboard-action-card">
            <span class="dashboard-action-icon" aria-hidden="true">&#10084;</span>
            <h3>Saved Listings</h3>
            <p>View properties you have shortlisted</p>
        </a>
        <a href="mailto:support@estatevault.com" class="dashboard-action-card">
            <span class="dashboard-action-icon" aria-hidden="true">&#128172;</span>
            <h3>Contact Support</h3>
            <p>Get help from our team anytime</p>
        </a>
    </div>

    <div class="dashboard-grid-2">
        <div class="dashboard-panel">
            <h3 class="dashboard-section-title">Account Details</h3>
            <ul class="dashboard-detail-list">
                <li>
                    <span class="label">Full name</span>
                    <span class="value">${userName}</span>
                </li>
                <li>
                    <span class="label">Email</span>
                    <span class="value">${userEmail}</span>
                </li>
                <li>
                    <span class="label">Mobile</span>
                    <span class="value">${userMobile}</span>
                </li>
                <li>
                    <span class="label">Role</span>
                    <span class="value">${userRole}</span>
                </li>
                <li>
                    <span class="label">City</span>
                    <span class="value">${userCity}</span>
                </li>
                <li>
                    <span class="label">State</span>
                    <span class="value">${userState}</span>
                </li>
            </ul>
        </div>

        <div class="dashboard-panel">
            <h3 class="dashboard-section-title">Recent Activity</h3>
            <div class="dashboard-activity-item">
                <span class="dashboard-activity-dot" aria-hidden="true"></span>
                <div>
                    <p><strong>Successful login</strong> — You signed in to EstateVault.</p>
                    <time>Just now</time>
                </div>
            </div>
            <c:if test="${verified}">
                <div class="dashboard-activity-item">
                    <span class="dashboard-activity-dot" aria-hidden="true"></span>
                    <div>
                        <p><strong>Account verified</strong> — Your email OTP verification is complete.</p>
                        <time>Completed</time>
                    </div>
                </div>
            </c:if>
            <div class="dashboard-activity-item">
                <span class="dashboard-activity-dot" aria-hidden="true"></span>
                <div>
                    <p><strong>Member since ${memberSince}</strong> — Thank you for joining EstateVault.</p>
                    <time>${memberSince}</time>
                </div>
            </div>
        </div>
    </div>

    <div class="dashboard-panel">
        <h3 class="dashboard-section-title">Security &amp; Tips</h3>
        <ul class="dashboard-detail-list mb-0">
            <li>
                <span class="label">Session</span>
                <span class="value" style="color:var(--success);">Active (JWT)</span>
            </li>
            <li>
                <span class="label">Recommendation</span>
                <span class="value">Log out on shared devices after use</span>
            </li>
        </ul>
        <div class="dashboard-footer-actions">
            <a href="${ctx}/" class="btn-primary-gold">Back to Home</a>
            <a href="#" id="logoutLinkFooter" class="btn-outline">Sign Out</a>
        </div>
    </div>

</main>
</div>

<script src="${ctx}/js/auth.js"></script>
<script src="${ctx}/js/user-portal.js"></script>
<script>
(function () {
  const ctx = '${ctx}';
  UserPortal.initSidebar();

  async function doLogout(e) {
    if (e) e.preventDefault();
    try {
      await AuthAPI.logout();
    } catch (err) { /* still redirect */ }
    TokenStore.clear();
    window.location.href = ctx + '/user/logout';
  }

  document.getElementById('sidebarLogout')?.addEventListener('click', doLogout);
  document.getElementById('logoutLinkFooter')?.addEventListener('click', doLogout);

  document.getElementById('dismissWelcome')?.addEventListener('click', function () {
    const el = document.getElementById('welcomeAlert');
    if (el) el.remove();
    try {
      const url = new URL(window.location.href);
      url.searchParams.delete('welcome');
      window.history.replaceState({}, '', url.pathname + url.search);
    } catch (ignore) {}
  });
})();
</script>
<%@ include file="../includes/portal-scripts.jsp" %>
</body>
</html>
