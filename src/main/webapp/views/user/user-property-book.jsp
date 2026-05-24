<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="../includes/init.jsp" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Property Details | EstateVault</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${ctx}/css/auth.css">
    <link rel="stylesheet" href="${ctx}/css/home.css">
    <link rel="stylesheet" href="${ctx}/css/user-properties.css">
</head>
<body class="auth-body auth-bg-orbs property-book-page">
<%@ include file="../includes/auth-particles.jsp" %>

<nav class="auth-nav dashboard-nav">
    <a class="brand" href="${ctx}/">EstateVault</a>
    <div class="dashboard-nav-actions">
        <a href="${ctx}/user/properties" class="btn-outline">&#8592; Back to Explore</a>
        <c:if test="${loggedIn}">
            <a href="${ctx}/user/dashboard" class="btn-outline">Dashboard</a>
        </c:if>
        <c:if test="${!loggedIn}">
            <a href="${ctx}/user/login" class="btn-outline">User Login</a>
        </c:if>
    </div>
</nav>

<main class="property-book-main">
    <div id="propertyBookLoader" class="property-book-loader">
        <div class="spinner"></div>
        <p>Loading property details…</p>
    </div>
    <div id="propertyBookError" class="property-book-error" style="display:none;"></div>
    <div id="propertyBookContent" style="display:none;"></div>
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
            <p class="enquiry-modal-hint">Minimum 10 characters. The agent will accept your enquiry and reply on your enquiries page.</p>
            <div class="enquiry-modal-actions">
                <button type="button" class="btn-outline" id="enquiryModalCancel">Cancel</button>
                <button type="submit" class="btn-gold">Submit enquiry</button>
            </div>
        </form>
    </div>
</div>

<div id="toast-container" class="toast-container"></div>

<script src="${ctx}/js/auth.js"></script>
<script src="${ctx}/js/property-book.js"></script>
<script>
PropertyBook.init('${ctx}', { propertyId: ${propertyId}, loggedIn: ${loggedIn} });
</script>
<%@ include file="../includes/portal-scripts.jsp" %>
</body>
</html>
