<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="../includes/init.jsp" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>PG Details | EstateVault</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${ctx}/css/auth.css">
    <link rel="stylesheet" href="${ctx}/css/home.css">
    <link rel="stylesheet" href="${ctx}/css/user-properties.css">
    <link rel="stylesheet" href="${ctx}/css/user-pgs.css">
</head>
<body class="auth-body auth-bg-orbs property-book-page pg-book-page">
<%@ include file="../includes/auth-particles.jsp" %>

<nav class="auth-nav dashboard-nav">
    <a class="brand" href="${ctx}/">EstateVault</a>
    <div class="dashboard-nav-actions">
        <a href="${ctx}/user/pgs" class="btn-outline">&#8592; Back to Explore PG</a>
        <c:if test="${loggedIn}">
            <a href="${ctx}/user/dashboard" class="btn-outline">Dashboard</a>
        </c:if>
        <c:if test="${!loggedIn}">
            <a href="${ctx}/user/login" class="btn-outline">User Login</a>
        </c:if>
    </div>
</nav>

<main class="property-book-main">
    <div id="pgBookLoader" class="property-book-loader">
        <div class="spinner"></div>
        <p>Loading PG details…</p>
    </div>
    <div id="pgBookError" class="property-book-error" style="display:none;"></div>
    <div id="pgBookContent" style="display:none;"></div>
</main>

<div id="toast-container" class="toast-container"></div>

<script src="${ctx}/js/auth.js"></script>
<script src="${ctx}/js/pg-book.js"></script>
<script>
PgBook.init('${ctx}', { pgId: ${pgId}, loggedIn: ${loggedIn} });
</script>
<%@ include file="../includes/portal-scripts.jsp" %>
</body>
</html>
