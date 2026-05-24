<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="../includes/init.jsp" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Notifications | Agent Portal</title>
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
        <header class="agent-topbar"><h2 class="agent-topbar-title">All <span>Notifications</span></h2></header>
        <div class="agent-content ap-layout">
            <div class="ap-kpi-grid">
                <div class="ap-kpi"><label>Unread</label><strong class="gold" id="notifUnread">—</strong></div>
                <div class="ap-kpi"><label>Total alerts</label><strong id="notifTotal">—</strong></div>
            </div>
            <div class="ap-filter-bar">
                <button type="button" class="active" data-notif-filter="ALL">All</button>
                <button type="button" data-notif-filter="LEAD">Leads</button>
                <button type="button" data-notif-filter="BOOKING">Bookings</button>
                <button type="button" data-notif-filter="SUBSCRIPTION">Subscription</button>
                <button type="button" data-notif-filter="PAYMENT">Payments</button>
            </div>
            <div class="ap-panel">
                <p class="small text-muted mb-3">Leads, bookings, subscription events, payments, and email alerts — with full details.</p>
                <div id="notifList" class="ap-notif-list">Loading…</div>
            </div>
        </div>
    </div>
</div>
<script src="${ctx}/js/auth.js"></script>
<script src="${ctx}/js/agent.js"></script>
<script src="${ctx}/js/agent-portal.js"></script>
<script>AgentPortal.init('${ctx}', 'notifications');</script>
<%@ include file="../includes/portal-scripts.jsp" %>
</body>
</html>
