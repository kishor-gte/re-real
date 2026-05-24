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
    <link rel="stylesheet" href="${ctx}/css/pg-owner.css">
    <link rel="stylesheet" href="${ctx}/css/pg-owner-pages.css">
    <link rel="stylesheet" href="${ctx}/css/pg-owner-property.css">
</head>
<body class="pgo-body pgo-bg-grid">
<%@ include file="../includes/pg-owner-particles.jsp" %>
<div class="pgo-dashboard-wrap">
    <%@ include file="../includes/pg-owner-sidebar.jsp" %>
    <div class="pgo-main">
        <header class="pgo-topbar">
            <h2 class="pgo-topbar-title">PG <span>Details</span></h2>
            <div class="d-flex gap-2">
                <a href="${ctx}/pg-owner/properties/${propertyId}/edit" class="btn btn-sm btn-primary">Edit</a>
                <a href="${ctx}/pg-owner/properties/${propertyId}/gallery" class="btn btn-sm btn-outline-warning">Gallery</a>
                <a href="${ctx}/pg-owner/properties" class="btn btn-sm btn-outline-light">Back</a>
            </div>
        </header>
        <div class="pgo-content" id="pgpDetailsRoot">
            <p style="color:var(--pgo-muted)">Loading...</p>
        </div>
    </div>
</div>
<script src="${ctx}/js/pg-owner-property.js"></script>
<script>
document.addEventListener('DOMContentLoaded', function () {
    PgPropertyUI.initDetails('${ctx}', ${propertyId});
});
</script>
<%@ include file="../includes/portal-scripts.jsp" %>
</body>
</html>
