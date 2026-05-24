<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="userUri" value="${pageContext.request.requestURI}"/>
<aside class="user-sidebar" aria-label="User navigation">
    <div class="user-sidebar-brand">
        <span>EstateVault</span>
        <strong>Buyer Portal</strong>
    </div>
    <div class="user-sidebar-profile">
        <div class="user-sidebar-avatar" aria-hidden="true">${userInitial}</div>
        <div>
            <strong>${userName}</strong>
            <span>Verified member</span>
        </div>
    </div>
    <ul class="user-sidebar-nav">
        <li><a href="${ctx}/user/dashboard" class="${fn:contains(userUri, '/user/dashboard') ? 'active' : ''}">&#128202; Dashboard</a></li>
        <li><a href="${ctx}/user/properties" class="${fn:contains(userUri, '/user/properties') ? 'active' : ''}">&#127968; Explore Properties</a></li>
        <li><a href="${ctx}/user/pgs" class="${fn:contains(userUri, '/user/pgs') ? 'active' : ''}">&#127976; Explore PG</a></li>
        <li><a href="${ctx}/user/enquiries" class="${fn:contains(userUri, '/user/enquiries') ? 'active' : ''}">&#128172; My Enquiries <span class="rtc-nav-badge rtc-hidden" data-rtc-nav-badge="enquiries" aria-hidden="true"></span></a></li>
        <li><a href="${ctx}/user/bookings" class="${fn:contains(userUri, '/user/bookings') or fn:contains(userUri, '/user/booking') ? 'active' : ''}">&#128179; My Bookings <span class="rtc-nav-badge rtc-hidden" data-rtc-nav-badge="bookings" aria-hidden="true"></span></a></li>
        <li><a href="${ctx}/" class="">&#127968; Home</a></li>
    </ul>
    <div class="user-sidebar-footer">
        <a href="#" id="sidebarLogout" class="btn-outline w-100">Logout</a>
    </div>
</aside>
