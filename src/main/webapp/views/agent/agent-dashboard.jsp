<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ include file="../includes/init.jsp" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Agent Dashboard | EstateVault</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${ctx}/css/agent.css">
    <link rel="stylesheet" href="${ctx}/css/agent-pages.css">
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
</head>
<body class="agent-body agent-bg-grid">
<%@ include file="../includes/agent-particles.jsp" %>
<div class="agent-dashboard-wrap">
    <%@ include file="../includes/agent-sidebar.jsp" %>
    <div class="agent-main">
        <header class="agent-topbar">
            <h2 class="agent-topbar-title">Broker <span>Dashboard</span></h2>
            <div class="agent-profile-dropdown">
                <button type="button" class="agent-profile-trigger" id="profileDropdownBtn">
                    <c:choose>
                        <c:when test="${not empty profilePhoto}">
                            <img src="${ctx}${profilePhoto}" alt="" class="agent-profile-avatar-img" style="width:36px;height:36px;border-radius:50%;object-fit:cover;">
                        </c:when>
                        <c:otherwise>
                            <span class="agent-profile-avatar">${fn:substring(agentName, 0, 1)}</span>
                        </c:otherwise>
                    </c:choose>
                    <span>
                        <strong style="display:block;font-size:0.9rem;">${agentName}</strong>
                        <small style="color:var(--agent-muted);">${agentCode}</small>
                    </span>
                </button>
                <div class="agent-profile-menu" id="profileMenu">
                    <p class="px-2 py-1 mb-1 small" style="color:var(--agent-muted);">${agentEmail}</p>
                    <p class="px-2 mb-2 small" style="color:var(--agent-muted);">${agencyName} &middot; ${specialization}</p>
                    <p class="px-2 mb-2 small">Referral: <strong style="color:var(--agent-gold);">${referralCode}</strong></p>
                    <a href="#" role="menuitem">My Profile</a>
                    <a href="${ctx}/agent/logout" role="menuitem">Sign Out</a>
                </div>
            </div>
        </header>
        <div class="agent-content">
            <section class="agent-welcome-banner">
                <h1>Welcome, <em>${agentName}</em></h1>
                <p>Manage properties, leads, and grow your real estate portfolio.</p>
                <div class="agent-meta-pills">
                    <span>&#128231; ${agentEmail}</span>
                    <span>&#128188; ${agentCode}</span>
                    <span>&#127970; ${agencyName}</span>
                    <c:if test="${not empty lastLogin}">
                        <span>&#128336; Last login: ${lastLogin}</span>
                    </c:if>
                </div>
            </section>
            <div class="agent-stat-grid">
                <div class="agent-stat-card"><div class="agent-stat-label">Properties Posted</div><p class="agent-stat-value gold" data-count="${stats.totalProperties}">0</p></div>
                <div class="agent-stat-card"><div class="agent-stat-label">Leads &amp; Inquiries</div><p class="agent-stat-value neon" data-count="${stats.totalLeads}">0</p></div>
                <div class="agent-stat-card"><div class="agent-stat-label">Property Views</div><p class="agent-stat-value" data-count="${stats.propertyViews}">0</p></div>
                <div class="agent-stat-card"><div class="agent-stat-label">Unread Messages</div><p class="agent-stat-value" data-count="${stats.unreadMessages}">0</p></div>
                <div class="agent-stat-card"><div class="agent-stat-label">Remaining Posts</div><p class="agent-stat-value neon">${stats.remainingPosts < 0 ? '∞' : stats.remainingPosts}</p></div>
                <div class="agent-stat-card"><div class="agent-stat-label">Current Plan</div><p class="agent-stat-value" style="font-size:0.95rem;">${stats.currentPlanName}</p></div>
                <div class="agent-stat-card"><div class="agent-stat-label">Subscription</div><p class="agent-stat-value" style="font-size:0.9rem;">${stats.subscriptionStatus}</p></div>
                <div class="agent-stat-card"><div class="agent-stat-label">Expires</div><p class="agent-stat-value" style="font-size:0.85rem;">${stats.subscriptionExpiry}</p></div>
                <c:if test="${!stats.canPostProperty}">
                <a href="${ctx}/agent/subscription/plans" class="agent-stat-card agent-stat-card--link" style="border-color:rgba(212,175,55,0.5);">
                    <div class="agent-stat-label">Upgrade</div>
                    <p class="agent-stat-value gold" style="font-size:1rem;">View Plans</p>
                    <span class="agent-stat-link-hint">Limit reached &rarr;</span>
                </a>
                </c:if>
                <div class="agent-stat-card"><div class="agent-stat-label">Referrals</div><p class="agent-stat-value" data-count="${stats.referralCount}">0</p></div>
                <div class="agent-stat-card"><div class="agent-stat-label">Referral Earnings</div><p class="agent-stat-value gold">&#8377;${stats.referralEarnings}</p></div>
                <a href="${ctx}/agent/bookings" class="agent-stat-card agent-stat-card--link">
                    <div class="agent-stat-label">User Bookings</div>
                    <p class="agent-stat-value neon" data-count="${stats.totalBookings}">0</p>
                    <span class="agent-stat-link-hint">View all &rarr;</span>
                </a>
            </div>
            <div class="agent-grid-2">
                <div class="agent-panel">
                    <h3>Recent Activities</h3>
                    <ul class="agent-activity-list">
                        <li class="agent-activity-item">
                            <span class="agent-activity-dot"></span>
                            <div><p><strong>Dashboard session</strong> — ${agentName} signed in.</p><time>Just now</time></div>
                        </li>
                        <li class="agent-activity-item">
                            <span class="agent-activity-dot"></span>
                            <div><p><strong>Agent ID ${agentCode}</strong> — account active.</p><time>Verified broker</time></div>
                        </li>
                        <li class="agent-activity-item">
                            <span class="agent-activity-dot"></span>
                            <div><p><strong>${stats.referralCount} referral(s)</strong> registered with your code.</p><time>Referral program</time></div>
                        </li>
                    </ul>
                </div>
                <div class="agent-panel">
                    <h3>Quick Actions</h3>
                    <div class="d-grid gap-2">
                        <a href="${ctx}/agent/properties/add" class="agent-btn-gold">Add Property</a>
                        <a href="${ctx}/agent/enquiries" class="agent-btn-outline">View Leads</a>
                        <a href="${ctx}/agent/bookings" class="agent-btn-outline">User Bookings</a>
                        <a href="${ctx}/agent/subscription" class="agent-btn-outline">Subscription</a>
                        <a href="${ctx}/agent/analytics" class="agent-btn-outline">Analytics</a>
                        <a href="${ctx}/agent/earnings" class="agent-btn-outline">Earnings</a>
                        <a href="${ctx}/agent/messages" class="agent-btn-outline">Buyer Messages</a>
                        <a href="${ctx}/agent/notifications" class="agent-btn-outline">Notifications</a>
                        <a href="${ctx}/" class="agent-btn-ghost">Public Site</a>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<script src="${ctx}/js/auth.js"></script>
<script src="${ctx}/js/agent.js"></script>
<script>
document.getElementById('profileDropdownBtn')?.addEventListener('click', function () {
  document.getElementById('profileMenu')?.classList.toggle('open');
});
document.addEventListener('click', function (e) {
  const dd = document.querySelector('.agent-profile-dropdown');
  if (dd && !dd.contains(e.target)) document.getElementById('profileMenu')?.classList.remove('open');
});
document.querySelectorAll('[data-count]').forEach(function (el) {
  const target = parseInt(el.getAttribute('data-count'), 10) || 0;
  let n = 0;
  const step = Math.max(1, Math.ceil(target / 40));
  const t = setInterval(function () {
    n += step;
    if (n >= target) { n = target; clearInterval(t); }
    el.textContent = n;
  }, 30);
});
setTimeout(function () {
  if (typeof Swal !== 'undefined') {
    Swal.fire({ toast: true, position: 'top-end', icon: 'info', title: 'Session active', showConfirmButton: false, timer: 3000 });
  }
}, 800);
</script>
<%@ include file="../includes/portal-scripts.jsp" %>
</body>
</html>
