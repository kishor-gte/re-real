<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="../includes/init.jsp" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Manage PGs | EstateVault</title>
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
            <h2 class="pgo-topbar-title">Manage <span>PG Properties</span></h2>
            <a href="${ctx}/pg-owner/properties/add" class="btn btn-primary btn-sm">+ Add Property</a>
        </header>
        <div class="pgo-content">
            <div id="pgpManageEmpty" class="pgo-panel text-center d-none">
                <p style="color:var(--pgo-muted)">No PG properties yet.</p>
                <a href="${ctx}/pg-owner/properties/add" class="btn btn-primary mt-2">Add your first PG</a>
            </div>
            <div class="pgp-manage-grid" id="pgpManageGrid"></div>
        </div>
    </div>
</div>
<script src="${ctx}/js/pg-owner-property.js"></script>
<script>
document.addEventListener('DOMContentLoaded', function () {
    PgPropertyUI.initManagePage('${ctx}');
});
</script>
<%@ include file="../includes/portal-scripts.jsp" %>
</body>
</html>
