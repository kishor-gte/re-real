<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="../includes/init.jsp" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Book PG | EstateVault</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${ctx}/css/auth.css">
    <link rel="stylesheet" href="${ctx}/css/user-portal.css">
    <link rel="stylesheet" href="${ctx}/css/user-pgs.css">
    <link rel="stylesheet" href="${ctx}/css/booking-payment.css">
</head>
<body class="auth-body auth-bg-orbs pg-booking-form-page user-portal-page">
<%@ include file="../includes/auth-particles.jsp" %>
<%@ include file="../includes/user-portal-topnav.jsp" %>
<div class="user-portal-wrap">
<%@ include file="../includes/user-sidebar.jsp" %>
<main class="user-portal-main pg-booking-form-main">
    <header class="pg-booking-form-header">
        <a href="${ctx}/user/pgs/${pgId}/book" class="booking-back">&#8592; PG details</a>
        <h1>Book <span>PG</span></h1>
        <p class="booking-sub">Choose room, beds, and guest details</p>
    </header>

    <div id="pgBookingFormLoader" class="booking-loader"><div class="spinner"></div><p>Loading rooms…</p></div>
    <div id="pgBookingFormError" class="booking-error glass-card" style="display:none;"></div>

    <form id="pgBookingForm" class="pg-booking-form glass-card" style="display:none;">
        <input type="hidden" id="pgPropertyId" value="${pgId}">

        <div class="pg-form-group">
            <label for="roomSelect">Select room</label>
            <select id="roomSelect" required aria-label="Select room"></select>
            <p id="roomHint" class="pg-form-hint"></p>
        </div>

        <div class="pg-form-group">
            <label for="bedCount">How many beds?</label>
            <input type="number" id="bedCount" min="1" value="1" required>
        </div>

        <div class="pg-form-group">
            <label>Select bed number(s)</label>
            <div id="bedNumbersWrap" class="pg-bed-chips"></div>
            <p class="pg-form-hint">Pick the bed(s) you want in this room.</p>
        </div>

        <div id="occupantsSection">
            <h3 class="pg-form-section-title">Guest details</h3>
            <p class="pg-form-hint">Provide name and mobile for each person staying.</p>
            <div id="occupantsList"></div>
        </div>

        <div class="pg-form-actions">
            <button type="submit" class="btn-proceed-pay" id="confirmPgBookingBtn">
                <span class="btn-proceed-text">Confirm &amp; continue to payment</span>
            </button>
        </div>
    </form>
</main>
</div>

<div id="toast-container" class="toast-container"></div>
<script src="${ctx}/js/auth.js"></script>
<script src="${ctx}/js/user-portal.js"></script>
<script src="${ctx}/js/pg-booking-form.js"></script>
<script>
PgBookingForm.init('${ctx}', { pgId: ${pgId} });
UserPortal.initSidebar();
</script>
<%@ include file="../includes/portal-scripts.jsp" %>
</body>
</html>
