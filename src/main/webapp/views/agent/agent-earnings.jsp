<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="../includes/init.jsp" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Earnings | Agent Portal</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${ctx}/css/agent.css">
    <link rel="stylesheet" href="${ctx}/css/agent-pages.css">
    <link rel="stylesheet" href="${ctx}/css/agent-portal.css">
</head>
<body class="agent-body agent-bg-grid">
<%@ include file="../includes/agent-particles.jsp" %>
<div class="agent-dashboard-wrap">
    <%@ include file="../includes/agent-sidebar.jsp" %>
    <div class="agent-main">
        <header class="agent-topbar">
            <h2 class="agent-topbar-title">Booking <span>Earnings</span></h2>
            <a href="${ctx}/agent/bookings" class="agent-btn-outline">User Bookings</a>
        </header>
        <div class="agent-content ap-layout">
            <div class="ap-kpi-grid">
                <div class="ap-kpi"><label>Collected</label><strong class="gold" id="erCollected">—</strong></div>
                <div class="ap-kpi"><label>Outstanding</label><strong id="erPending">—</strong></div>
                <div class="ap-kpi"><label>Booking value</label><strong id="erTotal">—</strong></div>
                <div class="ap-kpi"><label>Bookings</label><strong class="neon" id="erCount">—</strong></div>
            </div>
            <div class="ap-panel">
                <h3>Monthly collections</h3>
                <div id="erMonthlyChart" class="ap-bar-chart"></div>
            </div>
            <div class="ap-panel table-responsive">
                <h3>Earnings breakdown</h3>
                <table class="ap-table">
                    <thead><tr><th>Booking</th><th>Property</th><th>Buyer</th><th>Status</th><th>Total</th><th>Paid</th><th>Due</th><th>Date</th></tr></thead>
                    <tbody id="erTableBody"><tr><td colspan="8">Loading…</td></tr></tbody>
                </table>
            </div>
        </div>
    </div>
</div>
<script src="${ctx}/js/auth.js"></script>
<script src="${ctx}/js/agent.js"></script>
<script src="${ctx}/js/agent-portal.js"></script>
<script>AgentPortal.init('${ctx}', 'earnings');</script>
<%@ include file="../includes/portal-scripts.jsp" %>
</body>
</html>
