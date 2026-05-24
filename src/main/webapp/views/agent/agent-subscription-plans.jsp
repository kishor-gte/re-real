<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="../includes/init.jsp" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Subscription Plans | Agent Portal</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${ctx}/css/agent.css">
    <link rel="stylesheet" href="${ctx}/css/agent-pages.css">
    <link rel="stylesheet" href="${ctx}/css/subscription.css">
</head>
<body class="agent-body agent-bg-grid">
<%@ include file="../includes/agent-particles.jsp" %>
<div class="agent-dashboard-wrap">
    <%@ include file="../includes/agent-sidebar.jsp" %>
    <div class="agent-main">
        <header class="agent-topbar">
            <h2 class="agent-topbar-title">Upgrade <span>Plan</span></h2>
            <a href="${ctx}/agent/subscription" class="agent-btn-outline">My Subscription</a>
        </header>
        <div class="agent-content">
            <div id="limitBanner" class="sub-banner-limit" style="display:none;">
                <strong>Free Property Posting Limit Reached</strong>
                <p class="mb-0 mt-1">You have used your 2 free property listings. Choose a plan below to continue posting.</p>
            </div>
            <div id="upgradeBlockedPanel" class="sub-hero" style="display:none; border-color:rgba(212,175,55,0.45);">
                <h1>Current plan in use</h1>
                <p id="upgradeBlockedMessage" class="mb-2"></p>
                <p class="mb-0 small" style="color:#94a3b8;">Upgrade options appear when you have used all listing slots on your current plan, or when your plan expires.</p>
                <a href="${ctx}/agent/subscription" class="agent-btn-gold mt-3 d-inline-block">View my subscription</a>
                <a href="${ctx}/agent/properties/add" class="agent-btn-outline mt-3 ms-2 d-inline-block">Add property</a>
            </div>
            <div id="plansPageHero" class="sub-hero">
                <h1>Choose your growth plan</h1>
                <p>Unlock more listings, featured visibility, and premium leads — like top property portals.</p>
            </div>
            <div id="plansGrid" class="sub-plans-grid"><p class="text-muted">Loading plans…</p></div>
            <div id="compareSection" class="agent-panel mt-4">
                <h3>Compare plans</h3>
                <div class="table-responsive">
                    <table class="sub-compare-table">
                        <thead id="compareHead"></thead>
                        <tbody id="compareBody"></tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>
<script src="${ctx}/js/auth.js"></script>
<script src="${ctx}/js/agent.js"></script>
<script src="${ctx}/js/agent-subscription.js"></script>
<script>AgentSubscription.init('${ctx}', 'plans');</script>
<%@ include file="../includes/portal-scripts.jsp" %>
</body>
</html>
