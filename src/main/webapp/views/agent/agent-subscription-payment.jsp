<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="../includes/init.jsp" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Complete Payment | Agent Portal</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${ctx}/css/agent.css">
    <link rel="stylesheet" href="${ctx}/css/agent-pages.css">
    <link rel="stylesheet" href="${ctx}/css/subscription.css">
    <script src="https://checkout.razorpay.com/v1/checkout.js"></script>
</head>
<body class="agent-body agent-bg-grid">
<%@ include file="../includes/agent-particles.jsp" %>
<div class="agent-dashboard-wrap">
    <%@ include file="../includes/agent-sidebar.jsp" %>
    <div class="agent-main">
        <header class="agent-topbar"><h2 class="agent-topbar-title">Secure <span>Payment</span></h2></header>
        <div class="agent-content">
            <div class="agent-panel" style="max-width:480px;margin:auto;">
                <h3 id="payPlanName">—</h3>
                <p class="agent-stat-value gold" id="payAmount">—</p>
                <p style="color:var(--agent-muted);font-size:0.9rem;">Pay via Razorpay. Your subscription activates instantly after successful payment.</p>
                <button type="button" class="agent-btn-gold w-100 mt-3" id="payNowBtn">Proceed to Pay</button>
                <a href="${ctx}/agent/subscription/plans" class="agent-btn-ghost w-100 mt-2 d-block text-center">Back to plans</a>
            </div>
        </div>
    </div>
</div>
<script src="${ctx}/js/auth.js"></script>
<script src="${ctx}/js/agent.js"></script>
<script src="${ctx}/js/agent-subscription.js"></script>
<script>AgentSubscription.init('${ctx}', 'payment');</script>
<%@ include file="../includes/portal-scripts.jsp" %>
</body>
</html>
