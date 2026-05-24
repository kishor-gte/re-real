<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="../includes/init.jsp" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Booking Confirmed | EstateVault</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${ctx}/css/auth.css">
    <link rel="stylesheet" href="${ctx}/css/user-portal.css">
    <link rel="stylesheet" href="${ctx}/css/booking-payment.css">
</head>
<body class="auth-body auth-bg-orbs user-portal-page booking-success-page">
<%@ include file="../includes/auth-particles.jsp" %>
<%@ include file="../includes/user-portal-topnav.jsp" %>
<div class="user-portal-wrap">
    <%@ include file="../includes/user-sidebar.jsp" %>
    <main class="user-portal-main">
        <div id="successLoader" class="booking-loader"><div class="payment-loader-ring"></div></div>
        <div id="successContent" class="glass-card booking-success-card" style="display:none;"></div>
    </main>
</div>
<script src="${ctx}/js/auth.js"></script>
<script src="${ctx}/js/user-portal.js"></script>
<script>
(function () {
  const ctx = '${ctx}';
  const bookingId = <c:choose><c:when test="${bookingId != null}">${bookingId}</c:when><c:otherwise>null</c:otherwise></c:choose>;
  UserPortal.initSidebar();
  if (!bookingId) {
    document.getElementById('successContent').innerHTML = '<p>Missing booking reference.</p>';
    document.getElementById('successContent').style.display = 'block';
    document.getElementById('successLoader').style.display = 'none';
    return;
  }
  AuthAPI.request(ctx + '/api/user/bookings/' + bookingId + '/confirmation').then(function (res) {
    const d = res.data;
    let emiHtml = '';
    if (d.emiSchedule && d.emiSchedule.length) {
      emiHtml = '<h4>EMI schedule</h4><table class="emi-table"><thead><tr><th>#</th><th>Due</th><th>Amount</th></tr></thead><tbody>' +
        d.emiSchedule.map(function (e) {
          return '<tr><td>' + e.installmentNumber + '</td><td>' + (e.dueDate || '') + '</td><td>₹' + e.amount + '</td></tr>';
        }).join('') + '</tbody></table>';
    }
    document.getElementById('successContent').innerHTML =
      '<div class="success-checkmark large"></div>' +
      '<h1>Payment successful</h1>' +
      '<p class="success-lead">Your property booking is confirmed. Details are below and were sent to your email.</p>' +
      '<p class="success-code">' + (d.bookingCode || '') + '</p>' +
      '<p><strong>' + (d.propertyTitle || '') + '</strong> · ' + (d.propertyCode || '') + '</p>' +
      '<ul class="success-summary">' +
      '<li>Payment status: <strong class="text-success">' + (d.statusLabel || d.status || 'Confirmed') + '</strong></li>' +
      '<li>Total property value: <strong>₹' + Number(d.totalAmount).toLocaleString('en-IN') + '</strong></li>' +
      '<li>Amount paid now: <strong>₹' + Number(d.paidAmount).toLocaleString('en-IN') + '</strong></li>' +
      '<li>Remaining balance: <strong>₹' + Number(d.remainingAmount).toLocaleString('en-IN') + '</strong></li>' +
      '<li>Razorpay / Transaction ref: <strong>' + (d.transactionCode || '—') + '</strong></li>' +
      '</ul>' + emiHtml +
      '<div class="success-actions">' +
      (d.invoiceDownloadUrl ? '<a href="' + ctx + d.invoiceDownloadUrl + '" class="btn-gold" download>Download invoice PDF</a>' : '') +
      '<button type="button" class="btn-outline" onclick="window.print()">Print receipt</button>' +
      '<a href="' + ctx + '/user/bookings" class="btn-outline">All my bookings</a>' +
      '<a href="' + ctx + '/user/properties" class="btn-outline">Explore more</a>' +
      '</div>';
    document.getElementById('successContent').style.display = 'block';
  }).catch(function (err) {
    document.getElementById('successContent').innerHTML = '<p>' + err.message + '</p>';
    document.getElementById('successContent').style.display = 'block';
  }).finally(function () {
    document.getElementById('successLoader').style.display = 'none';
  });
})();
</script>
<%@ include file="../includes/portal-scripts.jsp" %>
</body>
</html>
