<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="../includes/init.jsp" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>PG Booking Confirmed | EstateVault</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${ctx}/css/auth.css">
    <link rel="stylesheet" href="${ctx}/css/user-portal.css">
    <link rel="stylesheet" href="${ctx}/css/user-pgs.css">
</head>
<body class="auth-body auth-bg-orbs user-portal-page">
<%@ include file="../includes/auth-particles.jsp" %>
<%@ include file="../includes/user-portal-topnav.jsp" %>
<div class="user-portal-wrap">
<%@ include file="../includes/user-sidebar.jsp" %>
<main class="user-portal-main" style="max-width:720px;margin:0 auto;padding:2rem 1rem;">
    <div id="pgSuccessLoader" class="booking-loader"><div class="spinner"></div><p>Loading confirmation…</p></div>
    <div id="pgSuccessContent" class="glass-card" style="display:none;padding:1.5rem;"></div>
</main>
</div>
<script src="${ctx}/js/auth.js"></script>
<script src="${ctx}/js/user-portal.js"></script>
<script>
(function () {
  var ctx = '${ctx}';
  var bookingId = ${bookingId};
  UserPortal.initSidebar();
  AuthAPI.request(ctx + '/api/user/pg-bookings/' + bookingId + '/confirmation').then(function (res) {
    var d = res.data || {};
  var html = '<h1>' + (d.status === 'FULLY_PAID' ? 'PG booking fully paid' : 'Payment received') + '</h1>' +
      (d.status === 'FULLY_PAID'
        ? '<p class="pg-success-note">Your booking is complete. Confirmation emails have been sent to you and the PG owner.</p>'
        : '<p class="pg-success-note">Your booking request has been sent to the PG owner for approval. Pay the remaining balance after approval.</p>') +
      '<p><strong>' + (d.pgName || '') + '</strong> · Room ' + (d.roomNumber || '') + '</p>' +
      '<p>Booking: <strong>' + (d.bookingCode || '') + '</strong></p>' +
      '<p>Status: <strong>' + (d.ownerApprovalLabel || 'Awaiting PG owner approval') + '</strong></p>' +
      '<p>Beds: ' + (d.bedCount || 0) + ' (#' + (d.bedNumbers || '') + ')</p>' +
      '<p>Paid: <strong>₹' + Number(d.paidAmount || 0).toLocaleString('en-IN') + '</strong></p>' +
      '<ul>' + (d.occupants || []).map(function (o) {
        return '<li>' + o.fullName + ' · Bed ' + (o.bedNumber || '') + ' · ' + o.mobile + '</li>';
      }).join('') + '</ul>' +
      '<a href="' + ctx + '/user/pgs" class="btn-gold">Explore more PGs</a>';
    document.getElementById('pgSuccessContent').innerHTML = html;
    document.getElementById('pgSuccessContent').style.display = 'block';
    document.getElementById('pgSuccessLoader').style.display = 'none';
  }).catch(function () {
    document.getElementById('pgSuccessLoader').innerHTML = '<p>Could not load confirmation.</p>';
  });
})();
</script>
<%@ include file="../includes/portal-scripts.jsp" %>
</body>
</html>
