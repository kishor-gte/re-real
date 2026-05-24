<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="../includes/init.jsp" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>PG Subscription Plans | Admin</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${ctx}/css/admin.css">
    <link rel="stylesheet" href="${ctx}/css/subscription.css">
    <style>.plan-modal{display:none;position:fixed;inset:0;background:rgba(0,0,0,.7);z-index:1000;align-items:center;justify-content:center}.plan-modal.open{display:flex}.plan-modal-box{background:#1e293b;padding:1.5rem;border-radius:12px;max-width:560px;width:95%;max-height:90vh;overflow:auto}</style>
</head>
<body class="admin-body admin-bg-grid">
<%@ include file="../includes/admin-particles.jsp" %>
<div class="admin-dashboard-wrap">
    <%@ include file="../includes/admin-sidebar.jsp" %>
    <div class="admin-main">
        <header class="admin-topbar">
            <h2 class="admin-topbar-title">PG Subscription <span>Plans</span></h2>
            <button type="button" class="admin-btn-gold" id="newPlanBtn">+ Create Plan</button>
        </header>
        <div class="admin-content">
            <div class="admin-panel">
                <table class="admin-sub-table table table-dark">
                    <thead><tr><th>Name</th><th>Code</th><th>Price</th><th>Max PG</th><th>Max Rooms</th><th>Status</th><th></th></tr></thead>
                    <tbody id="adminPlansBody"><tr><td colspan="7">Loading…</td></tr></tbody>
                </table>
            </div>
        </div>
    </div>
</div>
<div id="planModal" class="plan-modal">
    <div class="plan-modal-box">
        <h3 id="planFormTitle">Create PG Plan</h3>
        <form id="planForm">
            <input type="hidden" id="planId">
            <div class="mb-2"><label class="form-label">Plan Name</label><input class="form-control" id="planName" required></div>
            <div class="mb-2"><label class="form-label">Plan Code</label><input class="form-control" id="planCode" required></div>
            <div class="row">
                <div class="col-6 mb-2"><label class="form-label">Price (₹)</label><input type="number" step="0.01" class="form-control" id="planPrice" required></div>
                <div class="col-6 mb-2"><label class="form-label">Duration (days)</label><input type="number" class="form-control" id="planDuration" required></div>
            </div>
            <div class="row">
                <div class="col-6 mb-2"><label class="form-label">Max PG (-1 = unlimited)</label><input type="number" class="form-control" id="planMaxPg" required></div>
                <div class="col-6 mb-2"><label class="form-label">Max rooms (-1 = unlimited)</label><input type="number" class="form-control" id="planMaxRooms" required></div>
            </div>
            <div class="mb-2"><label class="form-label">Featured PG count</label><input type="number" class="form-control" id="planFeatured" value="0"></div>
            <div class="mb-2"><label class="form-label">Description</label><textarea class="form-control" id="planDesc" rows="2"></textarea></div>
            <div class="form-check mb-1"><input type="checkbox" class="form-check-input" id="planRecommended"><label class="form-check-label">Recommended</label></div>
            <div class="form-check mb-1"><input type="checkbox" class="form-check-input" id="planAnalytics"><label class="form-check-label">Tenant analytics</label></div>
            <div class="form-check mb-1"><input type="checkbox" class="form-check-input" id="planBedMgmt"><label class="form-check-label">Bed management</label></div>
            <div class="form-check mb-1"><input type="checkbox" class="form-check-input" id="planPremium"><label class="form-check-label">Premium badge</label></div>
            <div class="form-check mb-3"><input type="checkbox" class="form-check-input" id="planPriority"><label class="form-check-label">Priority support</label></div>
            <button type="submit" class="admin-btn-gold">Save</button>
            <button type="button" class="admin-btn-outline ms-2" id="closePlanModal">Cancel</button>
        </form>
    </div>
</div>
<script src="${ctx}/js/auth.js"></script>
<script src="${ctx}/js/admin-pg-subscription.js"></script>
<script>AdminPgSubscription.init('${ctx}', 'plans');</script>
<%@ include file="../includes/portal-scripts.jsp" %>
</body>
</html>
