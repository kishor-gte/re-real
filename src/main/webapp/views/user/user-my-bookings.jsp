<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="../includes/init.jsp" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Bookings | EstateVault</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${ctx}/css/auth.css">
    <link rel="stylesheet" href="${ctx}/css/user-portal.css">
    <link rel="stylesheet" href="${ctx}/css/booking-payment.css">
</head>
<body class="auth-body auth-bg-orbs user-portal-page">
<%@ include file="../includes/auth-particles.jsp" %>
<%@ include file="../includes/user-portal-topnav.jsp" %>
<div class="user-portal-wrap">
    <%@ include file="../includes/user-sidebar.jsp" %>
    <main class="user-portal-main">
        <h1 class="dashboard-section-title">My <span>Bookings</span></h1>
        <p style="color:var(--muted);margin-bottom:1rem;">Track property and PG bookings, payments, and owner approval status.</p>
        <div class="my-bookings-tabs" role="tablist" aria-label="Booking type">
            <button type="button" class="my-bookings-tab active" data-bookings-tab="property" role="tab" aria-selected="true">Property Bookings</button>
            <button type="button" class="my-bookings-tab" data-bookings-tab="pg" role="tab" aria-selected="false">PG Bookings</button>
        </div>
        <div id="bookingsLoader" class="booking-loader"><div class="payment-loader-ring"></div></div>
        <div id="bookingsList" class="my-bookings-list"></div>
    </main>
</div>
<div id="toast-container" class="toast-container"></div>
<script src="${ctx}/js/auth.js"></script>
<script src="${ctx}/js/user-portal.js"></script>
<script src="${ctx}/js/my-bookings.js"></script>
<%@ include file="../includes/rtc-shell.jsp" %>
<script>
EstateRTC.init({ ctx: '${ctx}', mode: 'user', api: AuthAPI });
UserPortal.initSidebar();
MyBookings.init('${ctx}');
document.getElementById('sidebarLogout')?.addEventListener('click', function (e) {
  e.preventDefault();
  AuthAPI.logout().finally(function () { window.location.href = '${ctx}/user/logout'; });
});
</script>
<%@ include file="../includes/portal-scripts.jsp" %>
</body>
</html>
