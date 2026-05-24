<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="pgoUri" value="${pageContext.request.requestURI}"/>
<aside class="pgo-sidebar" aria-label="PG Owner navigation">
    <div class="pgo-sidebar-brand"><span>EstateVault</span><strong>PG Owner Portal</strong></div>
    <ul class="pgo-sidebar-nav">
        <li><a href="${ctx}/pg-owner/dashboard" class="${fn:contains(pgoUri, '/pg-owner/dashboard') ? 'active' : ''}">&#128202; Dashboard</a></li>
        <li><a href="${ctx}/pg-owner/properties/add" class="${fn:contains(pgoUri, '/properties/add') ? 'active' : ''}">&#10133; Add PG</a></li>
        <li><a href="${ctx}/pg-owner/properties" class="${fn:contains(pgoUri, '/pg-owner/properties') && !fn:contains(pgoUri, '/properties/add') ? 'active' : ''}">&#127968; Manage PG</a></li>
        <li><a href="${ctx}/pg-owner/bookings" class="${fn:contains(pgoUri, '/pg-owner/bookings') ? 'active' : ''}" id="pgoBookingsNavLink">&#128179; PG Bookings <span id="pgoPendingBadge" class="pgo-nav-badge" style="display:none;">0</span></a></li>
        <li><a href="${ctx}/pg-owner/tenants" class="${fn:contains(pgoUri, '/pg-owner/tenants') ? 'active' : ''}">&#128101; Tenants</a></li>
        <li><a href="${ctx}/pg-owner/subscription/plans" class="${fn:contains(pgoUri, '/subscription/plans') ? 'active' : ''}">&#128179; Subscription Plans</a></li>
        <li><a href="${ctx}/pg-owner/subscription" class="${fn:contains(pgoUri, '/pg-owner/subscription') && !fn:contains(pgoUri, '/subscription/plans') && !fn:contains(pgoUri, '/subscription/payment') ? 'active' : ''}">&#9989; Active Subscription</a></li>
        <li><a href="${ctx}/pg-owner/subscription#payments" class="${fn:contains(pgoUri, '/subscription') ? '' : ''}">&#128196; Payment History</a></li>
        <li><a href="${ctx}/pg-owner/notifications" class="${fn:contains(pgoUri, '/pg-owner/notifications') ? 'active' : ''}">&#128276; Notifications</a></li>
        <li><a href="${ctx}/pg-owner/settings" class="${fn:contains(pgoUri, '/pg-owner/settings') ? 'active' : ''}">&#9881; Settings</a></li>
    </ul>
    <div class="pgo-sidebar-footer">
        <a href="${ctx}/pg-owner/logout" class="pgo-btn-outline w-100">Logout</a>
    </div>
    <script>
    (function () {
      var badge = document.getElementById('pgoPendingBadge');
      if (!badge) return;
      fetch('${ctx}/api/pg-owner/bookings/pending-count', { credentials: 'same-origin' })
        .then(function (r) { return r.json(); })
        .then(function (res) {
          var count = (res.data && res.data.pendingCount) || 0;
          if (count > 0) {
            badge.textContent = String(count);
            badge.style.display = 'inline-flex';
          }
        })
        .catch(function () {});
    })();
    </script>
</aside>
