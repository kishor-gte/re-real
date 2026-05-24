<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="../includes/init.jsp" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>User Bookings | PG Owner Portal</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${ctx}/css/pg-owner.css">
    <link rel="stylesheet" href="${ctx}/css/pg-owner-pages.css">
    <link rel="stylesheet" href="${ctx}/css/pg-owner-bookings.css">
</head>
<body class="pgo-body pgo-bg-grid pgo-bookings-page">
<%@ include file="../includes/pg-owner-particles.jsp" %>
<div class="pgo-dashboard-wrap">
    <%@ include file="../includes/pg-owner-sidebar.jsp" %>
    <div class="pgo-main">
        <header class="pgo-topbar">
            <h2 class="pgo-topbar-title">User <span>Bookings</span></h2>
            <span class="pgo-bookings-sub">Review paid bookings and allow or reject guest stays</span>
        </header>
        <div class="pgo-content">
            <div id="pgoBookingsLoader" class="pgo-bookings-loader"><div class="spinner"></div><p>Loading bookings…</p></div>
            <div id="pgoBookingsEmpty" class="pgo-panel text-center" style="display:none;">
                <p style="color:var(--pgo-muted);margin:0;">No user bookings yet.</p>
                <p style="color:var(--pgo-muted);font-size:0.9rem;margin:0.5rem 0 0;">Bookings appear here after users pay for your PG rooms.</p>
            </div>
            <div id="pgoBookingsList" class="pgo-bookings-list"></div>
        </div>
    </div>
</div>
<script src="${ctx}/js/pg-owner.js"></script>
<script src="${ctx}/js/pg-owner-bookings.js"></script>
<%@ include file="../includes/rtc-shell.jsp" %>
<script>
PgOwnerAPI.base = '${ctx}';
EstateRTC.init({ ctx: '${ctx}', mode: 'pg-owner', api: PgOwnerAPI });
PgOwnerBookings.init('${ctx}');
PgOwnerSidebarBadge.init('${ctx}');
</script>
<%@ include file="../includes/portal-scripts.jsp" %>
</body>
</html>
