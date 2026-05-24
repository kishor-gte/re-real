<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="../includes/init.jsp" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>PG Subscription Payment | EstateVault</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${ctx}/css/pg-owner.css">
    <link rel="stylesheet" href="${ctx}/css/subscription.css">
    <script src="https://checkout.razorpay.com/v1/checkout.js"></script>
</head>
<body class="pgo-body pgo-bg-grid">
<div class="pgo-dashboard-wrap">
    <%@ include file="../includes/pg-owner-sidebar.jsp" %>
    <div class="pgo-main">
        <div class="pgo-content" style="max-width:480px;margin:3rem auto;">
            <div class="pgo-panel text-center">
                <h2>Complete payment</h2>
                <p class="text-muted">Plan: <strong id="payPlanName">—</strong></p>
                <p class="display-6" id="payAmount">—</p>
                <button type="button" class="pgo-btn-gold w-100" id="payNowBtn">Pay with Razorpay</button>
                <a href="${ctx}/pg-owner/subscription/plans" class="d-block mt-3">Cancel</a>
            </div>
        </div>
    </div>
</div>
<script src="${ctx}/js/auth.js"></script>
<script src="${ctx}/js/pg-owner-subscription.js"></script>
<script>PgOwnerSubscription.init('${ctx}', 'payment');</script>
<%@ include file="../includes/portal-scripts.jsp" %>
</body>
</html>
