<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="../includes/init.jsp" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Referral Earnings | Agent Portal</title>
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
        <header class="agent-topbar"><h2 class="agent-topbar-title">Referral <span>Earnings</span></h2></header>
        <div class="agent-content ap-layout">
            <div class="sub-hero">
                <h1>Your referral program</h1>
                <p>Share code <strong style="color:#d4af37;" id="refCode">${referralCode}</strong> — earn commission when agents you refer go active.</p>
            </div>
            <div class="ap-kpi-grid">
                <div class="ap-kpi"><label>Total referrals</label><strong id="refTotal">—</strong></div>
                <div class="ap-kpi"><label>Credited</label><strong class="gold" id="refEarned">—</strong></div>
                <div class="ap-kpi"><label>Pending</label><strong id="refPending">—</strong></div>
                <div class="ap-kpi"><label>Potential total</label><strong id="refAll">—</strong></div>
            </div>
            <div class="ap-panel table-responsive">
                <h3>Referred agents</h3>
                <table class="ap-table">
                    <thead><tr><th>Agent</th><th>Code</th><th>Status</th><th>Commission</th><th>Payout</th><th>Registered</th></tr></thead>
                    <tbody id="refTableBody"><tr><td colspan="6">Loading…</td></tr></tbody>
                </table>
            </div>
        </div>
    </div>
</div>
<script src="${ctx}/js/auth.js"></script>
<script src="${ctx}/js/agent.js"></script>
<script src="${ctx}/js/agent-portal.js"></script>
<script>AgentPortal.init('${ctx}', 'referrals');</script>
<%@ include file="../includes/portal-scripts.jsp" %>
</body>
</html>
