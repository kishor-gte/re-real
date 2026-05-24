<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="../includes/init.jsp" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Subscription Payments | Admin</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${ctx}/css/admin.css">
    <link rel="stylesheet" href="${ctx}/css/subscription.css">
</head>
<body class="admin-body admin-bg-grid">
<div class="admin-dashboard-wrap">
    <%@ include file="../includes/admin-sidebar.jsp" %>
    <div class="admin-main">
        <header class="admin-topbar"><h2 class="admin-topbar-title">Subscription <span>Payments</span></h2></header>
        <div class="admin-content">
            <p class="text-muted small">Activate or deactivate payment records. Setting SUCCESS activates the linked subscription.</p>
            <div class="admin-panel table-responsive">
                <table class="admin-sub-table table table-dark">
                    <thead><tr><th>Invoice</th><th>Plan</th><th>Amount</th><th>Status</th><th>Date</th><th>Admin action</th></tr></thead>
                    <tbody id="paymentsBody"><tr><td colspan="6">Loading…</td></tr></tbody>
                </table>
            </div>
        </div>
    </div>
</div>
<script src="${ctx}/js/admin-subscription.js"></script>
<script>AdminSubscription.init('${ctx}', 'payments');</script>
<%@ include file="../includes/portal-scripts.jsp" %>
</body>
</html>
