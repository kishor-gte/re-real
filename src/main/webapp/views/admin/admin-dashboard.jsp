<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ include file="../includes/init.jsp" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Dashboard | EstateVault</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${ctx}/css/admin.css">
    <link rel="stylesheet" href="${ctx}/css/admin-pages.css">
</head>
<body class="admin-body admin-bg-grid">
<%@ include file="../includes/admin-particles.jsp" %>

<div class="admin-dashboard-wrap">
    <%@ include file="../includes/admin-sidebar.jsp" %>

    <div class="admin-main">
        <header class="admin-topbar">
            <h2 class="admin-topbar-title">Operations <span>Overview</span></h2>
            <div class="admin-profile-dropdown">
                <button type="button" class="admin-profile-trigger" id="profileDropdownBtn" aria-expanded="false" aria-haspopup="true">
                    <span class="admin-profile-avatar" aria-hidden="true">${fn:substring(adminName, 0, 1)}</span>
                    <span>
                        <strong style="display:block;font-size:0.9rem;">${adminName}</strong>
                        <small style="color:var(--admin-muted);font-size:0.75rem;">${adminRole}</small>
                    </span>
                </button>
                <div class="admin-profile-menu" id="profileMenu" role="menu">
                    <p class="px-2 py-1 mb-2 small" style="color:var(--admin-muted);">${adminEmail}</p>
                    <a href="#" role="menuitem">My Profile</a>
                    <a href="#" role="menuitem">Security</a>
                    <a href="${ctx}/admin/logout" role="menuitem" id="topbarLogout">Sign Out</a>
                </div>
            </div>
        </header>

        <div class="admin-content">
            <section class="admin-welcome-banner">
                <h1>Welcome back, <em>${adminName}</em></h1>
                <p>Manage EstateVault platform metrics, users, and operations from your command center.</p>
                <div class="admin-meta-pills">
                    <span>&#128231; ${adminEmail}</span>
                    <span>&#128188; ${adminRole}</span>
                    <c:if test="${not empty lastLogin}">
                        <span>&#128336; Last login: ${lastLogin}</span>
                    </c:if>
                </div>
            </section>

            <div class="admin-stat-grid">
                <div class="admin-stat-card">
                    <div class="admin-stat-label">Total Users</div>
                    <p class="admin-stat-value neon" data-count="${stats.totalUsers}">0</p>
                </div>
                <div class="admin-stat-card">
                    <div class="admin-stat-label">Buyers</div>
                    <p class="admin-stat-value" data-count="${stats.totalBuyers}">0</p>
                </div>
                <div class="admin-stat-card">
                    <div class="admin-stat-label">Sellers</div>
                    <p class="admin-stat-value" data-count="${stats.totalSellers}">0</p>
                </div>
                <div class="admin-stat-card">
                    <div class="admin-stat-label">Properties</div>
                    <p class="admin-stat-value gold" data-count="${stats.totalProperties}">0</p>
                </div>
                <div class="admin-stat-card">
                    <div class="admin-stat-label">Service Providers</div>
                    <p class="admin-stat-value" data-count="${stats.totalServiceProviders}">0</p>
                </div>
                <div class="admin-stat-card">
                    <div class="admin-stat-label">Pending Verifications</div>
                    <p class="admin-stat-value" data-count="${stats.pendingVerifications}">0</p>
                </div>
                <div class="admin-stat-card">
                    <div class="admin-stat-label">Agents Pending Approval</div>
                    <p class="admin-stat-value gold" data-count="${stats.pendingAgentApprovals}">0</p>
                    <a href="${ctx}/admin/agents?status=PENDING" class="admin-link small">Review applications &rarr;</a>
                </div>
                <div class="admin-stat-card">
                    <div class="admin-stat-label">Active Agents</div>
                    <p class="admin-stat-value neon" data-count="${stats.activeAgents}">0</p>
                </div>
                <div class="admin-stat-card">
                    <div class="admin-stat-label">PG Owners Pending Approval</div>
                    <p class="admin-stat-value gold" data-count="${stats.pendingPgOwnerApprovals}">0</p>
                    <a href="${ctx}/admin/pg-owners?status=PENDING" class="admin-link small">Review PG owners &rarr;</a>
                </div>
                <div class="admin-stat-card">
                    <div class="admin-stat-label">Active PG Owners</div>
                    <p class="admin-stat-value neon" data-count="${stats.activePgOwners}">0</p>
                </div>
                <div class="admin-stat-card">
                    <div class="admin-stat-label">Total PG Owners</div>
                    <p class="admin-stat-value" data-count="${stats.totalPgOwners}">0</p>
                </div>
                <div class="admin-stat-card">
                    <div class="admin-stat-label">Open Complaints</div>
                    <p class="admin-stat-value" data-count="${stats.openComplaints}">0</p>
                </div>
                <div class="admin-stat-card">
                    <div class="admin-stat-label">Active Subscriptions</div>
                    <p class="admin-stat-value neon" data-count="${stats.activeSubscriptions}">0</p>
                </div>
                <div class="admin-stat-card">
                    <div class="admin-stat-label">Revenue Total</div>
                    <p class="admin-stat-value gold" id="revenueStat" data-revenue="${stats.revenueTotal}">&#8377;0</p>
                </div>
            </div>

            <div class="admin-charts-row mb-3">
                <div class="admin-panel mb-0">
                    <h3>User Growth</h3>
                    <div class="admin-chart-placeholder" id="chartUsers">Chart placeholder — integrate Chart.js</div>
                </div>
                <div class="admin-panel mb-0">
                    <h3>Revenue Overview</h3>
                    <div class="admin-chart-placeholder" id="chartRevenue">Chart placeholder — integrate Chart.js</div>
                </div>
            </div>

            <div class="admin-grid-2">
                <div class="admin-panel">
                    <h3>Recent Activity</h3>
                    <ul class="admin-activity-list">
                        <li class="admin-activity-item">
                            <span class="admin-activity-dot" aria-hidden="true"></span>
                            <div>
                                <p><strong>Admin session active</strong> — ${adminName} signed in to the dashboard.</p>
                                <time>Just now</time>
                            </div>
                        </li>
                        <li class="admin-activity-item">
                            <span class="admin-activity-dot" aria-hidden="true"></span>
                            <div>
                                <p><strong>${stats.pendingVerifications} users</strong> awaiting email verification.</p>
                                <time>Platform queue</time>
                            </div>
                        </li>
                        <li class="admin-activity-item">
                            <span class="admin-activity-dot" aria-hidden="true"></span>
                            <div>
                                <p><strong>${stats.totalUsers} registered users</strong> on EstateVault.</p>
                                <time>Live count</time>
                            </div>
                        </li>
                        <li class="admin-activity-item">
                            <span class="admin-activity-dot" aria-hidden="true"></span>
                            <div>
                                <p><strong>Properties module</strong> — listing management coming soon.</p>
                                <time>Roadmap</time>
                            </div>
                        </li>
                    </ul>
                </div>

                <div class="admin-panel">
                    <h3>Quick Actions</h3>
                    <div class="d-grid gap-2">
                        <a href="${ctx}/admin/agents?status=PENDING" class="admin-btn-outline">Review pending agents</a>
                        <a href="${ctx}/admin/users" class="admin-btn-outline">Browse all users</a>
                        <a href="${ctx}/admin/properties" class="admin-btn-outline">Browse all properties</a>
                        <a href="${ctx}/admin/pg-owners?status=PENDING" class="admin-btn-outline">Review pending PG owners</a>
                        <a href="${ctx}/" class="admin-btn-primary">View public site</a>
                        <a href="${ctx}/admin/logout" class="admin-btn-ghost" id="footerLogout">Sign out securely</a>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<script src="${ctx}/js/auth.js"></script>
<script src="${ctx}/js/admin.js"></script>
<script>
(function () {
  document.getElementById('profileDropdownBtn')?.addEventListener('click', function () {
    document.getElementById('profileMenu')?.classList.toggle('open');
    this.setAttribute('aria-expanded', document.getElementById('profileMenu')?.classList.contains('open'));
  });
  document.addEventListener('click', function (e) {
    const dd = document.querySelector('.admin-profile-dropdown');
    if (dd && !dd.contains(e.target)) {
      document.getElementById('profileMenu')?.classList.remove('open');
    }
  });

  function animateCount(el, target, duration) {
    duration = duration || 1200;
    const start = performance.now();
    const isFloat = String(target).indexOf('.') >= 0;
    target = parseFloat(target) || 0;
    function frame(now) {
      const t = Math.min(1, (now - start) / duration);
      const eased = 1 - Math.pow(1 - t, 3);
      const val = Math.round(target * eased);
      el.textContent = isFloat ? val.toLocaleString('en-IN') : val.toLocaleString('en-IN');
      if (t < 1) requestAnimationFrame(frame);
      else el.textContent = (isFloat ? target : Math.round(target)).toLocaleString('en-IN');
    }
    requestAnimationFrame(frame);
  }

  document.querySelectorAll('[data-count]').forEach(function (el) {
    animateCount(el, el.getAttribute('data-count'));
  });

  const revEl = document.getElementById('revenueStat');
  if (revEl) {
    const target = parseFloat(revEl.getAttribute('data-revenue')) || 0;
    const start = performance.now();
    const duration = 1400;
    function frame(now) {
      const t = Math.min(1, (now - start) / duration);
      const eased = 1 - Math.pow(1 - t, 3);
      const val = target * eased;
      revEl.textContent = '\u20B9' + val.toLocaleString('en-IN', { minimumFractionDigits: 0, maximumFractionDigits: 2 });
      if (t < 1) requestAnimationFrame(frame);
    }
    requestAnimationFrame(frame);
  }
})();
</script>
<%@ include file="../includes/portal-scripts.jsp" %>
</body>
</html>
