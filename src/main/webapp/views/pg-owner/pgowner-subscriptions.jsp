<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="../includes/init.jsp" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My PG Subscription | EstateVault</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${ctx}/css/pg-owner.css">
    <link rel="stylesheet" href="${ctx}/css/pg-owner-pages.css">
    <link rel="stylesheet" href="${ctx}/css/subscription.css">
</head>
<body class="pgo-body pgo-bg-grid">
<%@ include file="../includes/pg-owner-particles.jsp" %>
<div class="pgo-dashboard-wrap">
    <%@ include file="../includes/pg-owner-sidebar.jsp" %>
    <div class="pgo-main">
        <header class="pgo-topbar">
            <h2 class="pgo-topbar-title">My <span>Subscription</span></h2>
            <a href="${ctx}/pg-owner/subscription/plans" class="pgo-btn-gold">Upgrade Plan</a>
        </header>
        <div class="pgo-content">
            <c:if test="${param.success == '1'}">
                <div class="alert alert-success">Subscription activated successfully.</div>
            </c:if>
            <div class="sub-hero">
                <h1>Premium PG management</h1>
                <p>Manage your plan, PG posting limits, and payment history.</p>
            </div>
            <div class="sub-kpi-grid">
                <div class="sub-kpi"><label>Current Plan</label><strong id="subPlanName">—</strong></div>
                <div class="sub-kpi"><label>Status</label><strong id="subStatus">—</strong></div>
                <div class="sub-kpi"><label>PG Listings</label><strong id="subPosted">—</strong></div>
                <div class="sub-kpi"><label>Remaining Slots</label><strong id="subRemaining">—</strong></div>
                <div class="sub-kpi"><label>Free PG Left</label><strong id="subFreeRemaining">—</strong></div>
                <div class="sub-kpi"><label>Expires</label><strong id="subExpiry">—</strong></div>
            </div>
            <div class="pgo-panel" id="payments">
                <h3>Payment history</h3>
                <div class="table-responsive">
                    <table class="table table-dark table-borderless">
                        <thead><tr><th>Invoice</th><th>Plan</th><th>Amount</th><th>Status</th><th>Date</th></tr></thead>
                        <tbody id="paymentHistoryBody"><tr><td colspan="5">Loading…</td></tr></tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>
<script src="${ctx}/js/auth.js"></script>
<script src="${ctx}/js/pg-owner-subscription.js"></script>
<script>PgOwnerSubscription.init('${ctx}', 'overview');</script>
<%@ include file="../includes/portal-scripts.jsp" %>
</body>
</html>
