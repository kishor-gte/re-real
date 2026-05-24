<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="../includes/init.jsp" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Explore PG | EstateVault</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${ctx}/css/auth.css">
    <link rel="stylesheet" href="${ctx}/css/home.css">
    <link rel="stylesheet" href="${ctx}/css/user-properties.css">
    <link rel="stylesheet" href="${ctx}/css/user-pgs.css">
</head>
<body class="auth-body auth-bg-orbs explore-pgs-page">
<%@ include file="../includes/auth-particles.jsp" %>

<nav class="auth-nav dashboard-nav">
    <a class="brand" href="${ctx}/">EstateVault</a>
    <div class="dashboard-nav-actions">
        <c:if test="${loggedIn}">
            <span class="nav-user">Signed in as <strong>${userName}</strong></span>
            <a href="${ctx}/user/dashboard" class="btn-outline">Dashboard</a>
        </c:if>
        <c:if test="${!loggedIn}">
            <a href="${ctx}/user/login" class="btn-outline">User Login</a>
        </c:if>
        <a href="${ctx}/" class="btn-outline">Home</a>
        <c:if test="${loggedIn}">
            <a href="#" id="logoutLink" class="btn-outline">Logout</a>
        </c:if>
    </div>
</nav>

<main class="explore-main">
    <header class="explore-hero">
        <span class="explore-tag">Verified PG listings</span>
        <h1>Explore <span>PG</span></h1>
        <p>Browse active PG accommodations from verified owners — filter by your city and location.</p>
    </header>

    <div class="explore-filters">
        <div class="explore-filter-group">
            <span class="explore-filter-label">City</span>
            <select id="pgCityFilter" class="pg-location-select" aria-label="Filter by city">
                <option value="ALL">All cities</option>
            </select>
        </div>
        <div class="explore-filter-group">
            <span class="explore-filter-label">State</span>
            <input type="text" id="pgStateFilter" class="pg-location-input" placeholder="Optional state filter"
                value="${userState}" aria-label="Filter by state">
        </div>
        <div class="explore-filter-group explore-filter-group--actions">
            <button type="button" id="pgApplyFilters" class="btn-gold">Apply filters</button>
            <c:if test="${not empty userCity}">
                <button type="button" id="pgUseMyCity" class="btn-outline" data-city="${userCity}">My city (${userCity})</button>
            </c:if>
        </div>
    </div>

    <p class="explore-results-count" id="pgResultsCount" aria-live="polite"></p>

    <div id="pgExploreLoader" class="explore-loader" style="display:none;">
        <div class="spinner"></div>
    </div>

    <div id="pgExploreEmpty" class="explore-empty" style="display:none;">
        <p>No active PG listings match your location.</p>
        <c:if test="${loggedIn}">
            <a href="${ctx}/user/dashboard" class="btn-gold">Back to dashboard</a>
        </c:if>
    </div>

    <div id="pgExploreContent"></div>
</main>

<div id="toast-container" class="toast-container"></div>

<script src="${ctx}/js/auth.js"></script>
<script src="${ctx}/js/pgs-browse.js"></script>
<script>
PgsBrowse.init('${ctx}', {
  loggedIn: ${loggedIn},
  defaultCity: '${userCity}',
  defaultState: '${userState}'
});
</script>
<%@ include file="../includes/portal-scripts.jsp" %>
</body>
</html>
