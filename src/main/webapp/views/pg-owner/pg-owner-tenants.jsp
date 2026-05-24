<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="../includes/init.jsp" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Tenants | PG Owner</title>
    <link rel="stylesheet" href="${ctx}/css/pg-owner.css">
</head>
<body class="pgo-body pgo-bg-grid">
<div class="pgo-dashboard-wrap">
    <%@ include file="../includes/pg-owner-sidebar.jsp" %>
    <div class="pgo-main">
        <header class="pgo-topbar"><h2 class="pgo-topbar-title">Tenants</h2></header>
        <div class="pgo-content">
            <div class="pgo-panel">
                <p>View active tenants from <a href="${ctx}/pg-owner/bookings">PG Bookings</a>. Full tenant management is available on Pro and Business plans.</p>
            </div>
        </div>
    </div>
</div>
<%@ include file="../includes/portal-scripts.jsp" %>
</body>
</html>
