<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="../includes/init.jsp" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>User Bookings | Agent Portal</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${ctx}/css/agent.css">
    <link rel="stylesheet" href="${ctx}/css/agent-pages.css">
    <link rel="stylesheet" href="${ctx}/css/agent-bookings.css?v=5">
    <link rel="stylesheet" href="${ctx}/css/rtc-chat.css?v=2">
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
</head>
<body class="agent-body agent-bg-grid agent-bookings-page">
<%@ include file="../includes/agent-particles.jsp" %>
<div class="agent-dashboard-wrap">
    <%@ include file="../includes/agent-sidebar.jsp" %>
    <div class="agent-main">
        <header class="agent-topbar">
            <h2 class="agent-topbar-title">User <span>Bookings</span></h2>
        </header>
        <div class="agent-content ab-layout">
            <div id="toast-container" class="toast-container"></div>
            <div id="loader" class="loader-overlay"><div class="spinner"></div></div>

            <section class="ab-panel ab-panel--overview">
                <header class="ab-panel-head">
                    <h3 class="ab-panel-title">Overview</h3>
                    <p class="ab-panel-desc">Summary of bookings on your listings</p>
                </header>
                <div class="ab-kpi-grid" id="abKpiRow">
                    <div class="ab-kpi-card"><span class="ab-kpi-label">Total bookings</span><strong id="abKpiTotal" class="ab-kpi-val neon">—</strong></div>
                    <div class="ab-kpi-card"><span class="ab-kpi-label">Pending payment</span><strong id="abKpiPending" class="ab-kpi-val">—</strong></div>
                    <div class="ab-kpi-card"><span class="ab-kpi-label">Completed</span><strong id="abKpiDone" class="ab-kpi-val gold">—</strong></div>
                    <div class="ab-kpi-card"><span class="ab-kpi-label">Collected</span><strong id="abKpiCollected" class="ab-kpi-val gold">—</strong></div>
                    <div class="ab-kpi-card"><span class="ab-kpi-label">Outstanding</span><strong id="abKpiDue" class="ab-kpi-val">—</strong></div>
                </div>
            </section>

            <section class="ab-panel ab-panel--list">
                <header class="ab-panel-head">
                    <h3 class="ab-panel-title">All bookings <span id="abBookingCount" class="ab-count-badge">0</span></h3>
                    <p class="ab-panel-desc">Newest first · buyer, payment &amp; balance in each box</p>
                </header>
                <div id="agentBookingsEmpty" class="ab-empty" style="display:none;">
                    <p>No user bookings yet.</p>
                    <span class="ab-empty-hint">Bookings appear when buyers checkout on your properties.</span>
                </div>
                <div id="agentBookingsList" class="ab-list"></div>
            </section>
        </div>
    </div>
</div>

<div id="bkViewModal" class="bk-modal" role="dialog" aria-modal="true" aria-labelledby="bkModalTitle" hidden>
    <div class="bk-modal__backdrop" data-bk-close></div>
    <div class="bk-modal__dialog">
        <header class="bk-modal__header">
            <h2 id="bkModalTitle" class="bk-modal__title">Booking details</h2>
            <button type="button" class="bk-modal__close" data-bk-close aria-label="Close">&times;</button>
        </header>
        <div id="bkModalBody" class="bk-modal__body"></div>
        <footer class="bk-modal__footer">
            <button type="button" class="agent-btn-outline" data-bk-close>Close</button>
            <button type="button" class="agent-btn-gold" id="bkModalPrintBtn">Print</button>
        </footer>
    </div>
</div>

<div id="bkPrintRoot" class="bk-print-root" aria-hidden="true"></div>

<script src="${ctx}/js/auth.js"></script>
<script src="${ctx}/js/agent.js"></script>
<script src="${ctx}/js/agent-bookings.js?v=6"></script>
<%@ include file="../includes/rtc-shell.jsp" %>
<script>
AgentAPI.base = '${ctx}';
EstateRTC.init({ ctx: '${ctx}', mode: 'agent', api: AgentAPI });
AgentBookings.init('${ctx}');
</script>
<%@ include file="../includes/portal-scripts.jsp" %>
</body>
</html>
