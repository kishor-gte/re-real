<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="agentUri" value="${pageContext.request.requestURI}"/>
<aside class="agent-sidebar" aria-label="Agent navigation">
    <div class="agent-sidebar-brand">
        <span>EstateVault</span>
        <strong>Agent Portal</strong>
    </div>
    <ul class="agent-sidebar-nav">
        <li><a href="${ctx}/agent/dashboard" class="${fn:contains(agentUri, '/agent/dashboard') ? 'active' : ''}">&#128202; Dashboard</a></li>
        <li><a href="${ctx}/agent/properties" class="${fn:contains(agentUri, '/agent/properties') && !fn:contains(agentUri, '/properties/add') ? 'active' : ''}">&#127968; My Properties</a></li>
        <li><a href="${ctx}/agent/properties/add" class="${fn:contains(agentUri, '/properties/add') ? 'active' : ''}">&#10133; Add Property</a></li>
        <li><a href="${ctx}/agent/enquiries" class="${fn:contains(agentUri, '/agent/enquiries') ? 'active' : ''}">&#128101; Leads &amp; Inquiries <span class="rtc-nav-badge rtc-hidden" data-rtc-nav-badge="enquiries" aria-hidden="true"></span></a></li>
        <li><a href="${ctx}/agent/bookings" class="${fn:contains(agentUri, '/agent/bookings') ? 'active' : ''}">&#128203; User Bookings <span class="rtc-nav-badge rtc-hidden" data-rtc-nav-badge="bookings" aria-hidden="true"></span></a></li>
        <li><a href="${ctx}/agent/subscription" class="${fn:contains(agentUri, '/agent/subscription') && !fn:contains(agentUri, '/subscription/plans') ? 'active' : ''}">&#128179; My Subscription</a></li>
        <li><a href="${ctx}/agent/subscription/plans" class="${fn:contains(agentUri, '/subscription/plans') ? 'active' : ''}">&#11014; Upgrade Plan</a></li>
        <li><a href="${ctx}/agent/analytics" class="${fn:contains(agentUri, '/agent/analytics') ? 'active' : ''}">&#128200; Analytics</a></li>
        <li><a href="${ctx}/agent/messages" class="${fn:contains(agentUri, '/agent/messages') ? 'active' : ''}">&#128172; Buyer Messages <span class="rtc-nav-badge rtc-hidden" data-rtc-nav-badge="messages" aria-hidden="true"></span></a></li>
        <li><a href="${ctx}/agent/earnings" class="${fn:contains(agentUri, '/agent/earnings') ? 'active' : ''}">&#128176; Earnings</a></li>
        <li><a href="${ctx}/agent/referrals" class="${fn:contains(agentUri, '/agent/referrals') ? 'active' : ''}">&#127873; Referral Earnings</a></li>
        <li><a href="${ctx}/agent/notifications" class="${fn:contains(agentUri, '/agent/notifications') ? 'active' : ''}">&#128276; Notifications</a></li>
    </ul>
    <div class="agent-sidebar-footer">
        <a href="${ctx}/agent/logout" class="agent-btn-outline w-100">Logout</a>
    </div>
</aside>
