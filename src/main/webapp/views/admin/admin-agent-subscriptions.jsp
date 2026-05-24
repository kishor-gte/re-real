<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="../includes/init.jsp" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Manage Agent Plans | Admin</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${ctx}/css/admin.css">
    <link rel="stylesheet" href="${ctx}/css/subscription.css">
</head>
<body class="admin-body admin-bg-grid">
<div class="admin-dashboard-wrap">
    <%@ include file="../includes/admin-sidebar.jsp" %>
    <div class="admin-main">
        <header class="admin-topbar"><h2 class="admin-topbar-title">Agent <span>Subscriptions</span></h2></header>
        <div class="admin-content">
            <div class="admin-panel table-responsive">
                <table class="admin-sub-table table table-dark">
                    <thead><tr><th>Agent</th><th>Plan</th><th>Status</th><th>Expiry</th><th>Usage</th><th>Set status</th></tr></thead>
                    <tbody id="agentSubsBody"><tr><td colspan="6">Loading…</td></tr></tbody>
                </table>
            </div>
        </div>
    </div>
</div>
<script src="${ctx}/js/admin-subscription.js"></script>
<script>AdminSubscription.init('${ctx}', 'agents');</script>
<%@ include file="../includes/portal-scripts.jsp" %>
</body>
</html>
