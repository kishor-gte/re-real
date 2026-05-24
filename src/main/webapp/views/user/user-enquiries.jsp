<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="../includes/init.jsp" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Enquiries | EstateVault</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${ctx}/css/auth.css">
    <link rel="stylesheet" href="${ctx}/css/dashboard.css">
    <link rel="stylesheet" href="${ctx}/css/user-portal.css">
    <link rel="stylesheet" href="${ctx}/css/user-properties.css">
    <link rel="stylesheet" href="${ctx}/css/rtc-chat.css?v=4">
</head>
<body class="auth-body auth-bg-orbs user-portal-page">
<%@ include file="../includes/auth-particles.jsp" %>
<%@ include file="../includes/user-portal-topnav.jsp" %>
<div class="user-portal-wrap">
    <%@ include file="../includes/user-sidebar.jsp" %>
    <main class="user-portal-main">
        <h1 class="dashboard-section-title">My <span>Enquiries</span></h1>
        <p style="color:var(--muted);margin-bottom:1.25rem;">Track enquiry status and read agent replies here.</p>
        <div id="enquiriesList" class="my-enquiries-list"></div>
    </main>
</div>
<div id="toast-container" class="toast-container"></div>
<script src="${ctx}/js/auth.js"></script>
<script src="${ctx}/js/user-portal.js"></script>
<%@ include file="../includes/rtc-shell.jsp" %>
<script>
EstateRTC.init({ ctx: '${ctx}', mode: 'user', api: AuthAPI });
(function () {
  const ctx = '${ctx}';
  UserPortal.initSidebar();
  document.getElementById('sidebarLogout')?.addEventListener('click', function (e) {
    e.preventDefault();
    AuthAPI.logout().finally(function () { window.location.href = ctx + '/user/logout'; });
  });
  AuthAPI.request(ctx + '/api/user/enquiries').then(function (data) {
    const list = data.data || [];
    const el = document.getElementById('enquiriesList');
    if (!list.length) {
      el.innerHTML = '<p class="enquiry-empty">No enquiries yet. <a href="' + ctx + '/user/properties">Explore properties</a></p>';
      return;
    }
    el.innerHTML = list.map(function (e) {
      let reply = e.agentReply ? '<div class="enquiry-reply"><strong>Agent reply</strong><p>' + esc(e.agentReply) + '</p></div>' : '';
      let rtc = (typeof EstateRTC !== 'undefined') ? EstateRTC.cardActionsHtml({
        enquiryId: e.id,
        peerId: e.agentId,
        peerName: e.agentName,
        propertyTitle: e.propertyTitle
      }) : '';
      return '<article class="enquiry-card enquiry-card--' + (e.status || '').toLowerCase() + '">' +
        '<div class="enquiry-card-head"><h4>' + esc(e.propertyTitle) + '</h4>' +
        '<span class="enquiry-status enquiry-status--' + (e.status || '').toLowerCase() + '">' + esc(e.statusLabel) + '</span></div>' +
        '<p class="enquiry-meta">' + esc(e.propertyCode) + ' · Agent: ' + esc(e.agentName) + '</p>' +
        '<p class="enquiry-user-msg"><strong>Your message:</strong> ' + esc(e.message) + '</p>' + reply + rtc + '</article>';
    }).join('');
    if (typeof EstateRTC !== 'undefined') {
      EstateRTC.bindCardActions(el);
    }
  }).catch(function (err) {
    document.getElementById('enquiriesList').innerHTML = '<p class="enquiry-empty">' + esc(err.message) + '</p>';
  });
  function esc(s) { return String(s || '').replace(/&/g,'&amp;').replace(/</g,'&lt;'); }
})();
</script>
<%@ include file="../includes/portal-scripts.jsp" %>
</body>
</html>
