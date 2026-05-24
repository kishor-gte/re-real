<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="../includes/init.jsp" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Explore Properties | EstateVault</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${ctx}/css/auth.css">
    <link rel="stylesheet" href="${ctx}/css/home.css">
    <link rel="stylesheet" href="${ctx}/css/user-properties.css">
</head>
<body class="auth-body auth-bg-orbs explore-properties-page">
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
        <span class="explore-tag">Verified agent listings</span>
        <h1>Explore <span>Properties</span></h1>
        <p>Browse active listings from licensed agents — filter by specialization and sale or rent.</p>
    </header>

    <div class="explore-filters">
        <div class="explore-filter-group">
            <span class="explore-filter-label">Listing</span>
            <div class="explore-toggle-group" id="listingTypeFilters" role="tablist">
                <button type="button" class="explore-toggle active" data-listing="ALL" aria-pressed="true">All</button>
                <button type="button" class="explore-toggle" data-listing="SALE" aria-pressed="false">For Sale</button>
                <button type="button" class="explore-toggle" data-listing="RENT" aria-pressed="false">For Rent</button>
            </div>
        </div>
        <div class="explore-filter-group explore-filter-group--grow">
            <span class="explore-filter-label">Specialization</span>
            <div class="explore-spec-chips" id="categoryFilters" role="tablist">
                <button type="button" class="explore-chip active" data-category="ALL" aria-pressed="true">All</button>
                <c:forEach var="opt" items="${specializationOptions}">
                    <button type="button" class="explore-chip" data-category="${opt.code}" aria-pressed="false">
                        ${opt.label}
                    </button>
                </c:forEach>
            </div>
        </div>
    </div>

    <p class="explore-results-count" id="resultsCount" aria-live="polite"></p>

    <div id="exploreLoader" class="explore-loader" style="display:none;">
        <div class="spinner"></div>
    </div>

    <div id="exploreEmpty" class="explore-empty" style="display:none;">
        <p>No active listings match your filters.</p>
        <c:if test="${loggedIn}">
            <a href="${ctx}/user/dashboard" class="btn-gold">Back to dashboard</a>
        </c:if>
    </div>

    <div class="properties-grid" id="propertiesGrid"></div>

    <section id="myEnquiriesSection" class="my-enquiries-section" style="display:none;">
        <h2 class="my-enquiries-title">My <span>Enquiries</span></h2>
        <p class="my-enquiries-sub">Track messages you sent to agents and read their replies here.</p>
        <div id="myEnquiriesList" class="my-enquiries-list"></div>
    </section>
</main>

<div id="enquiryModal" class="enquiry-modal" role="dialog" aria-labelledby="enquiryModalTitle" aria-hidden="true">
    <div class="enquiry-modal-dialog">
        <button type="button" class="enquiry-modal-close" id="enquiryModalClose" aria-label="Close">&times;</button>
        <h2 id="enquiryModalTitle">Enquire about property</h2>
        <p class="enquiry-modal-property">Property: <strong id="enquiryPropertyTitle"></strong></p>
        <form id="enquiryForm">
            <input type="hidden" id="enquiryPropertyId" name="propertyId">
            <label for="enquiryMessage">Your message to the agent</label>
            <textarea id="enquiryMessage" name="message" rows="5" required minlength="10" maxlength="2000"
                placeholder="Ask about price, visit timing, documents, amenities…"></textarea>
            <p class="enquiry-modal-hint">Minimum 10 characters. The agent will accept your enquiry and reply here.</p>
            <div class="enquiry-modal-actions">
                <button type="button" class="btn-outline" id="enquiryModalCancel">Cancel</button>
                <button type="submit" class="btn-gold">Submit enquiry</button>
            </div>
        </form>
    </div>
</div>

<div id="toast-container" class="toast-container"></div>

<script src="${ctx}/js/auth.js"></script>
<script src="${ctx}/js/properties-browse.js"></script>
<script>
PropertiesBrowse.init('${ctx}', { loggedIn: ${loggedIn} });
</script>
<%@ include file="../includes/portal-scripts.jsp" %>
</body>
</html>
