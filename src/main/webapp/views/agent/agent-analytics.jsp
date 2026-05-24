<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="../includes/init.jsp" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Analytics | Agent Portal</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${ctx}/css/agent.css">
    <link rel="stylesheet" href="${ctx}/css/agent-pages.css">
    <link rel="stylesheet" href="${ctx}/css/agent-portal.css">
    <link rel="stylesheet" href="${ctx}/css/subscription.css">
</head>
<body class="agent-body agent-bg-grid">
<%@ include file="../includes/agent-particles.jsp" %>
<div class="agent-dashboard-wrap">
    <%@ include file="../includes/agent-sidebar.jsp" %>
    <div class="agent-main">
        <header class="agent-topbar"><h2 class="agent-topbar-title">Portfolio <span>Analytics</span></h2></header>
        <div class="agent-content ap-layout">
            <div class="ap-kpi-grid">
                <div class="ap-kpi"><label>Properties</label><strong id="anProps">—</strong></div>
                <div class="ap-kpi"><label>Total views</label><strong class="neon" id="anViews">—</strong></div>
                <div class="ap-kpi"><label>Leads</label><strong id="anLeads">—</strong></div>
                <div class="ap-kpi"><label>Bookings</label><strong id="anBookings">—</strong></div>
                <div class="ap-kpi"><label>Collected</label><strong class="gold" id="anCollected">—</strong></div>
                <div class="ap-kpi"><label>Outstanding</label><strong id="anPending">—</strong></div>
                <div class="ap-kpi"><label>Lead → booking</label><strong id="anConv">—</strong></div>
            </div>
            <div class="row g-3">
                <div class="col-lg-6">
                    <div class="ap-panel">
                        <h3>Leads (last 6 months)</h3>
                        <div id="anMonthlyChart" class="ap-bar-chart"></div>
                    </div>
                </div>
                <div class="col-lg-3">
                    <div class="ap-panel"><h3>Leads by status</h3><div id="anLeadsStatus"></div></div>
                </div>
                <div class="col-lg-3">
                    <div class="ap-panel"><h3>Bookings by status</h3><div id="anBookingsStatus"></div></div>
                </div>
            </div>
            <div class="ap-panel">
                <h3>Top properties by views</h3>
                <table class="ap-table"><thead><tr><th>Title</th><th>Code</th><th>Views</th><th>Enquiries</th></tr></thead>
                <tbody id="anTopProps"><tr><td colspan="4">Loading…</td></tr></tbody></table>
            </div>
        </div>
    </div>
</div>
<script src="${ctx}/js/auth.js"></script>
<script src="${ctx}/js/agent.js"></script>
<script src="${ctx}/js/agent-portal.js"></script>
<script>AgentPortal.init('${ctx}', 'analytics');</script>
<%@ include file="../includes/portal-scripts.jsp" %>
</body>
</html>
