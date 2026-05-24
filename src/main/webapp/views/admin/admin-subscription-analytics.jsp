<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="../includes/init.jsp" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Subscription Analytics | Admin</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${ctx}/css/admin.css">
    <link rel="stylesheet" href="${ctx}/css/subscription.css">
</head>
<body class="admin-body admin-bg-grid">
<div class="admin-dashboard-wrap">
    <%@ include file="../includes/admin-sidebar.jsp" %>
    <div class="admin-main">
        <header class="admin-topbar"><h2 class="admin-topbar-title">Subscription <span>Analytics</span></h2></header>
        <div class="admin-content">
            <div class="sub-kpi-grid">
                <div class="sub-kpi"><label>Active subscriptions</label><strong id="anActiveSubs">—</strong></div>
                <div class="sub-kpi"><label>Total revenue</label><strong id="anRevenue">—</strong></div>
                <div class="sub-kpi"><label>Successful payments</label><strong id="anSuccessPay">—</strong></div>
                <div class="sub-kpi"><label>Pending payments</label><strong id="anPendingPay">—</strong></div>
                <div class="sub-kpi"><label>Active plans</label><strong id="anActivePlans">—</strong></div>
            </div>
        </div>
    </div>
</div>
<script src="${ctx}/js/admin-subscription.js"></script>
<script>AdminSubscription.init('${ctx}', 'analytics');</script>
<%@ include file="../includes/portal-scripts.jsp" %>
</body>
</html>
