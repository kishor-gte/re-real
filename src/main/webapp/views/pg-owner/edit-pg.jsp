<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="../includes/init.jsp" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Edit PG Property | EstateVault</title>
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
            <h2 class="pgo-topbar-title">Edit <span>PG Property</span></h2>
            <span><strong>${pgOwnerName}</strong></span>
        </header>
        <div class="pgo-content pgp-wizard-wrap">
            <div id="pgpToast" class="alert d-none" role="alert"></div>
            <div class="pgp-progress-bar"><div class="pgp-progress-bar-fill" id="pgpProgressFill"></div></div>
            <div class="pgp-progress" id="pgpSteps"></div>
            <%@ include file="pg-property-wizard-form.jsp" %>
        </div>
    </div>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script>window.PG_OWNER_CTX = '${ctx}';</script>
<script src="${ctx}/js/pg-owner-property.js"></script>
<script>
document.addEventListener('DOMContentLoaded', function () {
    PgPropertyUI.initWizard('${ctx}', { editMode: true, propertyId: ${propertyId} });
});
</script>
<%@ include file="../includes/portal-scripts.jsp" %>
</body>
</html>
