<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="../includes/init.jsp" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>PG Subscription Payments | Admin</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${ctx}/css/admin.css">
</head>
<body class="admin-body admin-bg-grid">
<div class="admin-dashboard-wrap">
    <%@ include file="../includes/admin-sidebar.jsp" %>
    <div class="admin-main">
        <header class="admin-topbar"><h2 class="admin-topbar-title">PG Subscription <span>Payments</span></h2></header>
        <div class="admin-content">
            <div class="admin-panel">
                <table class="table table-dark">
                    <thead><tr><th>Invoice</th><th>Plan</th><th>Amount</th><th>Status</th><th>Date</th></tr></thead>
                    <tbody id="adminPaymentsBody"><tr><td colspan="5">Loading…</td></tr></tbody>
                </table>
            </div>
        </div>
    </div>
</div>
<script src="${ctx}/js/admin-pg-subscription.js"></script>
<script>AdminPgSubscription.init('${ctx}', 'payments');</script>
<%@ include file="../includes/portal-scripts.jsp" %>
</body>
</html>
