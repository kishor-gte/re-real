<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="adminUri" value="${pageContext.request.requestURI}"/>
<c:set var="agentPending" value="${pendingAgentCount != null ? pendingAgentCount : (stats != null ? stats.pendingAgentApprovals : 0)}"/>
<c:set var="pgOwnerPending" value="${pendingPgOwnerCount != null ? pendingPgOwnerCount : (stats != null ? stats.pendingPgOwnerApprovals : 0)}"/>
<aside class="admin-sidebar" aria-label="Admin navigation">
    <div class="admin-sidebar-brand">
        <span>EstateVault</span>
        <strong>Admin Portal</strong>
    </div>
    <ul class="admin-sidebar-nav">
        <li><a href="${ctx}/admin/dashboard" class="${fn:contains(adminUri, '/admin/dashboard') ? 'active' : ''}">&#128202; Dashboard</a></li>
        <li><a href="${ctx}/admin/agents?status=PENDING" class="${fn:contains(adminUri, '/admin/agents') ? 'active' : ''}">&#128188; Agents<c:if test="${agentPending > 0}"><span class="badge bg-warning text-dark ms-1">${agentPending}</span></c:if></a></li>
        <li><a href="${ctx}/admin/pg-owners?status=PENDING" class="${fn:contains(adminUri, '/admin/pg-owners') ? 'active' : ''}">&#127976; PG Owner<c:if test="${pgOwnerPending > 0}"><span class="badge bg-warning text-dark ms-1">${pgOwnerPending}</span></c:if></a></li>
        <li><a href="${ctx}/admin/users" class="${fn:contains(adminUri, '/admin/users') ? 'active' : ''}">&#128101; Users</a></li>
        <li><a href="${ctx}/admin/properties" class="${fn:contains(adminUri, '/admin/properties') ? 'active' : ''}">&#127968; Properties</a></li>
        <li><a href="#">&#128736; Service Providers</a></li>
        <li><a href="${ctx}/admin/subscriptions/plans" class="${fn:contains(adminUri, '/subscriptions/plans') ? 'active' : ''}">&#128179; Subscription Plans</a></li>
        <li><a href="${ctx}/admin/subscriptions/agents" class="${fn:contains(adminUri, '/subscriptions/agents') ? 'active' : ''}">&#128101; Agent Plans</a></li>
        <li><a href="${ctx}/admin/subscriptions/active" class="${fn:contains(adminUri, '/subscriptions/active') ? 'active' : ''}">&#9989; Active Subscriptions</a></li>
        <li><a href="${ctx}/admin/subscriptions/payments" class="${fn:contains(adminUri, '/subscriptions/payments') ? 'active' : ''}">&#128179; Payments</a></li>
        <li><a href="${ctx}/admin/subscriptions/analytics" class="${fn:contains(adminUri, '/subscriptions/analytics') && !fn:contains(adminUri, '/pg-subscriptions') ? 'active' : ''}">&#128200; Agent Analytics</a></li>
        <li class="admin-sidebar-divider" style="margin:0.5rem 0;border-top:1px solid rgba(255,255,255,0.08);"></li>
        <li><a href="${ctx}/admin/pg-subscriptions/plans" class="${fn:contains(adminUri, '/pg-subscriptions/plans') ? 'active' : ''}">&#127976; PG Subscription Plans</a></li>
        <li><a href="${ctx}/admin/pg-subscriptions/owners" class="${fn:contains(adminUri, '/pg-subscriptions/owners') ? 'active' : ''}">&#128101; Manage PG Plans</a></li>
        <li><a href="${ctx}/admin/pg-subscriptions/active" class="${fn:contains(adminUri, '/pg-subscriptions/active') ? 'active' : ''}">&#9989; Active PG Subscriptions</a></li>
        <li><a href="${ctx}/admin/pg-subscriptions/payments" class="${fn:contains(adminUri, '/pg-subscriptions/payments') ? 'active' : ''}">&#128179; PG Subscription Payments</a></li>
        <li><a href="${ctx}/admin/pg-subscriptions/analytics" class="${fn:contains(adminUri, '/pg-subscriptions/analytics') ? 'active' : ''}">&#128200; PG Analytics</a></li>
        <li><a href="#">&#128200; Reports</a></li>
        <li><a href="#">&#9881; Settings</a></li>
    </ul>
    <div class="admin-sidebar-footer">
        <a href="${ctx}/admin/logout" class="admin-btn-outline w-100" id="sidebarLogout">Logout</a>
    </div>
</aside>
