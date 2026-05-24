<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="balanceMode" value="${balancePaymentMode == true}"/>
<%@ include file="../includes/init.jsp" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Secure Booking &amp; Payment | EstateVault</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${ctx}/css/auth.css">
    <link rel="stylesheet" href="${ctx}/css/user-portal.css">
    <link rel="stylesheet" href="${ctx}/css/booking-payment.css">
    <script src="https://checkout.razorpay.com/v1/checkout.js"></script>
</head>
<body class="auth-body auth-bg-orbs booking-payment-page user-portal-page">
<%@ include file="../includes/auth-particles.jsp" %>
<%@ include file="../includes/user-portal-topnav.jsp" %>
<div class="user-portal-wrap booking-payment-wrap">
    <%@ include file="../includes/user-sidebar.jsp" %>
    <main class="user-portal-main booking-payment-main">
        <header class="booking-payment-header">
            <c:choose>
                <c:when test="${balanceMode}">
                    <a href="${ctx}/user/bookings" class="booking-back">&#8592; My bookings</a>
                    <h1>Pay <span>remaining balance</span></h1>
                    <p class="booking-sub">Complete the outstanding amount on your booking</p>
                </c:when>
                <c:otherwise>
                    <a href="${ctx}/user/properties/${propertyId}/book" class="booking-back">&#8592; Property details</a>
                    <h1>Secure <span>Booking</span> &amp; Payment</h1>
                    <p class="booking-sub">Complete your reservation with encrypted payment</p>
                </c:otherwise>
            </c:choose>
            <div id="razorpayModeBanner" class="razorpay-mode-banner" style="display:none;"></div>
            <div id="reservationTimer" class="reservation-timer" style="display:none;">
                <span class="timer-pulse"></span>
                <span>Reservation holds for <strong id="timerDisplay">30:00</strong></span>
            </div>
        </header>

        <div id="bookingLoader" class="booking-loader">
            <div class="payment-loader-ring"></div>
            <p>Preparing checkout…</p>
        </div>

        <div id="bookingError" class="booking-error glass-card" style="display:none;"></div>

        <div id="bookingCheckout" class="booking-checkout-grid" style="display:none;">
            <div class="booking-col-left">
                <section class="glass-card booking-property-card" id="propertySection"></section>
                <section class="glass-card booking-progress-card">
                    <h3>Booking progress</h3>
                    <div class="booking-progress-track">
                        <div class="booking-progress-step active" data-step="1"><span>1</span> Details</div>
                        <div class="booking-progress-step" data-step="2"><span>2</span> Plan</div>
                        <div class="booking-progress-step" data-step="3"><span>3</span> Pay</div>
                        <div class="booking-progress-step" data-step="4"><span>4</span> Done</div>
                    </div>
                </section>
            </div>

            <div class="booking-col-right">
                <section class="glass-card booking-price-card" id="priceSection"></section>

                <section class="glass-card booking-plan-card" id="bookingPlanSection">
                    <h3>Payment plan</h3>
                    <div class="plan-options" id="planOptions">
                        <label class="plan-option active"><input type="radio" name="paymentPlan" value="FULL" checked> Full payment</label>
                        <label class="plan-option"><input type="radio" name="paymentPlan" value="BOOKING_ADVANCE"> Booking amount only (10%)</label>
                        <label class="plan-option"><input type="radio" name="paymentPlan" value="EMI"> Pay via EMI</label>
                    </div>
                    <div id="emiPlansSection" class="emi-plans-section" style="display:none;">
                        <h4>Select EMI tenure</h4>
                        <div id="emiPlansGrid" class="emi-plans-grid"></div>
                        <div id="emiBreakdown" class="emi-breakdown"></div>
                    </div>
                </section>

                <section class="glass-card booking-method-card">
                    <h3>Payment method</h3>
                    <div class="payment-methods" id="paymentMethods">
                        <label class="pay-method active"><input type="radio" name="payMethod" value="UPI" checked> UPI</label>
                        <label class="pay-method"><input type="radio" name="payMethod" value="CREDIT_CARD"> Credit Card</label>
                        <label class="pay-method"><input type="radio" name="payMethod" value="DEBIT_CARD"> Debit Card</label>
                        <label class="pay-method"><input type="radio" name="payMethod" value="NET_BANKING"> Net Banking</label>
                        <label class="pay-method"><input type="radio" name="payMethod" value="WALLET"> Wallets</label>
                        <label class="pay-method"><input type="radio" name="payMethod" value="EMI"> EMI</label>
                        <label class="pay-method"><input type="radio" name="payMethod" value="BANK_TRANSFER"> Bank Transfer</label>
                        <label class="pay-method"><input type="radio" name="payMethod" value="CASH_TOKEN"> Cash token</label>
                    </div>
                </section>

                <section class="glass-card booking-coupon-card" id="bookingCouponSection">
                    <h3>Discount coupon</h3>
                    <div class="coupon-row">
                        <input type="text" id="couponCode" placeholder="Try ESTATE10 or LUXURY5" maxlength="40">
                        <button type="button" class="btn-outline-sm" id="applyCoupon">Apply</button>
                    </div>
                </section>

                <button type="button" id="proceedPayBtn" class="btn-proceed-pay">
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
        <p id="successModalText">Your booking is confirmed.</p>
        <a href="#" id="successModalBtn" class="btn-gold">View confirmation</a>
    </div>
</div>

<div id="toast-container" class="toast-container"></div>

<script src="${ctx}/js/auth.js"></script>
<script src="${ctx}/js/user-portal.js"></script>
<script src="${ctx}/js/booking-payment.js"></script>
<script>
BookingPayment.init('${ctx}', {
  propertyId: ${not empty propertyId ? propertyId : 'null'},
  bookingId: ${not empty bookingId ? bookingId : 'null'},
  balanceMode: ${balanceMode}
});
UserPortal.initSidebar();
</script>
<%@ include file="../includes/portal-scripts.jsp" %>
</body>
</html>
