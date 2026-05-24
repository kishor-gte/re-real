<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<%@ include file="../includes/init.jsp" %>

<!DOCTYPE html>

<html lang="en">

<head>

    <meta charset="UTF-8">

    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>PG Owner Dashboard | EstateVault</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">

    <link rel="stylesheet" href="${ctx}/css/pg-owner.css">

    <link rel="stylesheet" href="${ctx}/css/pg-owner-pages.css">

</head>

<body class="pgo-body pgo-bg-grid">

<%@ include file="../includes/pg-owner-particles.jsp" %>

<div class="pgo-dashboard-wrap">

    <%@ include file="../includes/pg-owner-sidebar.jsp" %>

    <div class="pgo-main">

        <header class="pgo-topbar">

            <h2 class="pgo-topbar-title">PG Owner <span>Dashboard</span></h2>

            <div class="d-flex align-items-center gap-2">

                <c:if test="${not empty profilePhoto}">

                    <img src="${ctx}${profilePhoto}" alt="" style="width:40px;height:40px;border-radius:50%;object-fit:cover;border:2px solid var(--pgo-gold);">

                </c:if>

                <span><strong>${pgOwnerName}</strong><br><small style="color:var(--pgo-muted);">${pgOwnerCode}</small></span>

            </div>

        </header>

        <div class="pgo-content">

            <section class="pgo-welcome-banner">

                <h1>Welcome, <em>${pgOwnerName}</em></h1>

                <p>Manage your PG &amp; hostel listings from this portal.</p>

                <div class="pgo-meta-pills">

                    <span>&#128231; ${pgOwnerEmail}</span>

                    <c:if test="${not empty lastLogin}">

                        <span>&#128336; Last login: ${lastLogin}</span>

                    </c:if>

                </div>

            </section>

            <div id="dashLimitBanner" class="sub-banner-limit mb-3" style="display:none;">
                <strong>Free PG Listing Limit Reached</strong>
                <p class="mb-2 mt-1">You have used your 1 free PG listing. Upgrade to add more properties.</p>
                <a href="${ctx}/pg-owner/subscription/plans" class="pgo-btn-gold btn-sm">View Subscription Plans</a>
            </div>

            <div class="pgo-stat-grid pgo-stat-grid--dashboard">
                <div class="pgo-stat-card"><div class="pgo-stat-label">Total PG Listings</div><p class="pgo-stat-value neon" id="dashTotalPg">—</p></div>
                <div class="pgo-stat-card"><div class="pgo-stat-label">Remaining Free Listings</div><p class="pgo-stat-value" id="dashFreeLeft">—</p></div>
                <div class="pgo-stat-card"><div class="pgo-stat-label">Active Subscription</div><p class="pgo-stat-value" id="dashActivePlan">—</p></div>
                <div class="pgo-stat-card"><div class="pgo-stat-label">Subscription Expiry</div><p class="pgo-stat-value" id="dashExpiry">—</p></div>
                <div class="pgo-stat-card"><div class="pgo-stat-label">Total Beds Available</div><p class="pgo-stat-value" id="dashTotalBeds">—</p></div>
                <div class="pgo-stat-card"><div class="pgo-stat-label">Occupied Beds</div><p class="pgo-stat-value" id="dashOccupiedBeds">—</p></div>
                <div class="pgo-stat-card"><div class="pgo-stat-label">Monthly Earnings</div><p class="pgo-stat-value" id="dashEarnings">—</p></div>
                <div class="pgo-stat-card"><div class="pgo-stat-label">PG Inquiries</div><p class="pgo-stat-value" id="dashInquiries">—</p></div>
                <div class="pgo-stat-card pgo-stat-card--action">
                    <div class="pgo-stat-label">Upgrade</div>
                    <a href="${ctx}/pg-owner/subscription/plans" class="pgo-btn-gold btn-sm mt-2" id="dashUpgradeBtn">Upgrade Plan</a>
                </div>
            </div>

            <div class="pgo-panel mt-3">

                <h3 class="h5 mb-2">PG Property Management</h3>

                <p style="color:var(--pgo-muted);margin:0 0 1rem;">Add floors, rooms, sharing prices, amenities, and publish your PG listings.</p>

                <a href="${ctx}/pg-owner/properties" class="btn btn-outline-light btn-sm me-2">Manage PGs</a>

                <a href="${ctx}/pg-owner/properties/add" class="btn btn-primary btn-sm">Add Property</a>

            </div>

        </div>

    </div>

</div>

<script src="${ctx}/js/auth.js"></script>
<script src="${ctx}/js/pg-owner-dashboard.js"></script>
<script>PgOwnerDashboard.init('${ctx}');</script>
<%@ include file="../includes/portal-scripts.jsp" %>
</body>

</html>

