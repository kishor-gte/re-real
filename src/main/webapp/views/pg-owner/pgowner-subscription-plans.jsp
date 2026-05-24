<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="../includes/init.jsp" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>PG Subscription Plans | EstateVault</title>
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
            <h2 class="pgo-topbar-title">Upgrade <span>Plan</span></h2>
            <a href="${ctx}/pg-owner/subscription" class="pgo-btn-outline">My Subscription</a>
        </header>
        <div class="pgo-content">
            <div id="limitBanner" class="sub-banner-limit" style="display:none;">
                <strong>Free PG Listing Limit Reached</strong>
                <p class="mb-0 mt-1">You have already used your 1 free PG listing. Upgrade your subscription plan to continue adding more PG properties and unlock premium features.</p>
                <a href="${ctx}/pg-owner/subscription/plans" class="pgo-btn-gold btn-sm mt-2">View Subscription Plans</a>
            </div>
            <div id="upgradeBlockedPanel" class="sub-hero" style="display:none;">
                <h1>Current plan in use</h1>
                <p id="upgradeBlockedMessage" class="mb-2"></p>
                <a href="${ctx}/pg-owner/subscription" class="pgo-btn-gold mt-3 d-inline-block">View my subscription</a>
            </div>
            <div id="plansPageHero" class="sub-hero">
                <h1>Choose your PG growth plan</h1>
                <p>Unlock more PG listings, featured visibility, and tenant management — like top PG partner panels.</p>
            </div>
            <div id="plansGrid" class="sub-plans-grid"><p class="text-muted">Loading plans…</p></div>
            <div id="compareSection" class="pgo-panel mt-4">
                <h3>Compare plans</h3>
                <div class="table-responsive">
                    <table class="sub-compare-table">
                        <thead id="compareHead"></thead>
                        <tbody id="compareBody"></tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>
<div id="pgLimitModal" class="pg-limit-modal" style="display:none;" role="dialog" aria-modal="true">
    <div class="pg-limit-modal-dialog glass-card">
        <h2>Free PG Listing Limit Reached</h2>
        <p>You have already used your <strong>1 free PG listing</strong>.</p>
        <p>Upgrade your subscription plan to continue adding more PG properties and unlock premium features.</p>
        <a href="${ctx}/pg-owner/subscription/plans" class="pgo-btn-gold">View Subscription Plans</a>
        <button type="button" class="pgo-btn-outline mt-2" id="pgLimitModalClose">Close</button>
    </div>
</div>
<script src="${ctx}/js/auth.js"></script>
<script src="${ctx}/js/pg-owner-subscription.js"></script>
<script>
PgOwnerSubscription.init('${ctx}', 'plans');
if (new URLSearchParams(window.location.search).get('limit') === '1') {
  var b = document.getElementById('limitBanner');
  if (b) b.style.display = 'block';
  var m = document.getElementById('pgLimitModal');
  if (m) m.style.display = 'flex';
}
document.getElementById('pgLimitModalClose')?.addEventListener('click', function () {
  document.getElementById('pgLimitModal').style.display = 'none';
});
</script>
<%@ include file="../includes/portal-scripts.jsp" %>
</body>
</html>
