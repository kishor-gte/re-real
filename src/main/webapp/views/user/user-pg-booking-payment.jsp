<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="../includes/init.jsp" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>PG Booking Payment | EstateVault</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${ctx}/css/auth.css">
    <link rel="stylesheet" href="${ctx}/css/user-portal.css">
    <link rel="stylesheet" href="${ctx}/css/booking-payment.css">
    <link rel="stylesheet" href="${ctx}/css/user-pgs.css">
    <script src="https://checkout.razorpay.com/v1/checkout.js"></script>
</head>
<body class="auth-body auth-bg-orbs user-portal-page booking-payment-page pg-booking-payment-page">
<%@ include file="../includes/auth-particles.jsp" %>
<%@ include file="../includes/user-portal-topnav.jsp" %>
<div class="user-portal-wrap booking-payment-wrap">
<%@ include file="../includes/user-sidebar.jsp" %>
<main class="user-portal-main booking-payment-main">
    <header class="booking-payment-header">
        <a href="${ctx}/user/pgs" class="booking-back">&#8592; Explore PG</a>
        <h1>PG <span>Booking</span> Payment</h1>
        <p class="booking-sub">Review your booking and pay securely with Razorpay</p>
        <div id="razorpayModeBanner" class="razorpay-mode-banner" style="display:none;"></div>
        <div id="reservationTimer" class="reservation-timer" style="display:none;">
            <span class="timer-pulse"></span>
            <span>Reservation holds for <strong id="timerDisplay">30:00</strong></span>
        </div>
    </header>

    <div id="pgPayLoader" class="booking-loader"><div class="payment-loader-ring"></div><p>Loading checkout…</p></div>
    <div id="pgPayError" class="booking-error glass-card" style="display:none;"></div>

    <div id="pgPayCheckout" class="booking-checkout-grid" style="display:none;">
        <div class="booking-col-left">
            <section class="glass-card booking-property-card" id="pgSummarySection"></section>
            <section class="glass-card" id="pgOccupantsSection"></section>
        </div>
        <div class="booking-col-right">
            <section class="glass-card booking-price-card" id="pgPriceSection"></section>
            <button type="button" id="pgProceedPayBtn" class="btn-proceed-pay">
                <span class="btn-proceed-text">Proceed to secure payment</span>
                <span class="btn-proceed-loader" style="display:none;"></span>
            </button>
            <p class="booking-secure-note">&#128274; Secured by Razorpay · 256-bit encryption</p>
        </div>
    </div>
</main>
</div>

<div id="paymentSuccessModal" class="payment-success-modal" style="display:none;">
    <div class="payment-success-dialog glass-card">
        <div class="success-checkmark"></div>
        <h2>Payment successful!</h2>
        <p id="successModalText">Your PG booking is confirmed.</p>
        <a href="#" id="successModalBtn" class="btn-gold">View confirmation</a>
    </div>
</div>

<script src="${ctx}/js/auth.js"></script>
<script src="${ctx}/js/user-portal.js"></script>
<script src="${ctx}/js/pg-booking-payment.js"></script>
<script>
PgBookingPayment.init('${ctx}', {
  bookingId: ${bookingId},
  rentDueId: ${rentDueId},
  balancePaymentMode: ${balancePaymentMode},
  rentPaymentMode: ${rentPaymentMode}
});
UserPortal.initSidebar();
</script>
<%@ include file="../includes/portal-scripts.jsp" %>
</body>
</html>
