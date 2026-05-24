<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="../includes/init.jsp" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Subscription | Agent Portal</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${ctx}/css/agent.css">
    <link rel="stylesheet" href="${ctx}/css/agent-pages.css">
    <link rel="stylesheet" href="${ctx}/css/subscription.css">
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
</head>
<body class="agent-body agent-bg-grid">
<%@ include file="../includes/agent-particles.jsp" %>
<div class="agent-dashboard-wrap">
    <%@ include file="../includes/agent-sidebar.jsp" %>
    <div class="agent-main">
        <header class="agent-topbar">
            <h2 class="agent-topbar-title">My <span>Subscription</span></h2>
            <a href="${ctx}/agent/subscription/plans" class="agent-btn-gold">Upgrade Plan</a>
        </header>
        <div class="agent-content">
            <c:if test="${param.success == '1'}">
                <div class="alert alert-success">Subscription activated successfully.</div>
            </c:if>
            <div class="sub-hero">
                <h1>Premium agent listings</h1>
                <p>Manage your plan, posting limits, and payment history.</p>
            </div>
            <div class="sub-kpi-grid">
                <div class="sub-kpi"><label>Current Plan</label><strong id="subPlanName">—</strong></div>
                <div class="sub-kpi"><label>Status</label><strong id="subStatus">—</strong></div>
                <div class="sub-kpi"><label>Properties Posted</label><strong id="subPosted">${stats.totalProperties}</strong></div>
                <div class="sub-kpi"><label>Remaining Posts</label><strong id="subRemaining">—</strong></div>
                <div class="sub-kpi"><label>Free Posts Left</label><strong id="subFreeRemaining">${stats.remainingFreePosts}</strong></div>
                <div class="sub-kpi"><label>Expires</label><strong id="subExpiry">${stats.subscriptionExpiry}</strong></div>
            </div>
            <div class="agent-panel">
                <h3>Payment history</h3>
                <div class="table-responsive">
                    <table class="table table-dark table-borderless admin-sub-table">
                        <thead><tr><th>Invoice</th><th>Plan</th><th>Amount</th><th>Status</th><th>Date</th></tr></thead>
                        <tbody id="paymentHistoryBody"><tr><td colspan="5">Loading…</td></tr></tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>
<script src="${ctx}/js/auth.js"></script>
<script src="${ctx}/js/agent.js"></script>
<script src="${ctx}/js/agent-subscription.js"></script>
<script>AgentSubscription.init('${ctx}', 'overview');</script>
<%@ include file="../includes/portal-scripts.jsp" %>
</body>
</html>
