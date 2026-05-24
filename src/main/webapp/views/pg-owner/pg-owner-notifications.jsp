<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="../includes/init.jsp" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Notifications | PG Owner</title>
    <link rel="stylesheet" href="${ctx}/css/pg-owner.css">
    <link rel="stylesheet" href="${ctx}/css/pg-owner-pages.css">
</head>
<body class="pgo-body pgo-bg-grid">
<div class="pgo-dashboard-wrap">
    <%@ include file="../includes/pg-owner-sidebar.jsp" %>
    <div class="pgo-main">
        <header class="pgo-topbar"><h2 class="pgo-topbar-title">Notifications</h2></header>
        <div class="pgo-content">
            <div class="pgo-panel" id="pgNotifList"><p>Loading…</p></div>
        </div>
    </div>
</div>
<script src="${ctx}/js/auth.js"></script>
<script>
(function () {
  var ctx = '${ctx}';
  AuthAPI.request(ctx + '/api/pg-owner/subscription/notifications').then(function (res) {
    var list = document.getElementById('pgNotifList');
    var items = res.data || [];
    if (!items.length) { list.innerHTML = '<p>No notifications yet.</p>'; return; }
    list.innerHTML = items.map(function (n) {
      return '<div class="pgo-notif-item' + (n.read ? '' : ' unread') + '"><strong>' + esc(n.title) + '</strong><p>' + esc(n.message) + '</p><small>' + esc(n.createdAt) + '</small></div>';
    }).join('');
  }).catch(function (e) { document.getElementById('pgNotifList').innerHTML = '<p>' + e.message + '</p>'; });
  function esc(s) { return String(s || '').replace(/&/g,'&amp;').replace(/</g,'&lt;'); }
})();
</script>
<%@ include file="../includes/portal-scripts.jsp" %>
</body>
</html>
