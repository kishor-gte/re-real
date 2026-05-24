<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ include file="../includes/init.jsp" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Properties | Agent Portal</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${ctx}/css/agent.css">
    <link rel="stylesheet" href="${ctx}/css/agent-pages.css">
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
</head>
<body class="agent-body agent-bg-grid">
<%@ include file="../includes/agent-particles.jsp" %>
<div class="agent-dashboard-wrap">
    <%@ include file="../includes/agent-sidebar.jsp" %>
    <div class="agent-main">
        <header class="agent-topbar">
            <h2 class="agent-topbar-title">My <span>Properties</span></h2>
            <a href="${ctx}/agent/properties/add" class="agent-btn-gold">+ Add Property</a>
        </header>
        <div class="agent-content">
            <div id="toast-container" class="toast-container"></div>
            <div id="loader" class="loader-overlay"><div class="spinner"></div></div>
            <div id="propertiesEmpty" class="agent-panel text-center py-5" style="display:none;">
                <p style="color:var(--agent-muted);">No listings yet. Add your first property.</p>
                <a href="${ctx}/agent/properties/add" class="agent-btn-gold mt-2">Add Property</a>
            </div>
            <div id="propertiesGrid" class="agent-properties-grid"></div>
        </div>
    </div>
</div>
<script src="${ctx}/js/auth.js"></script>
<script src="${ctx}/js/agent.js"></script>
<script src="${ctx}/js/agent-property.js"></script>
<script>
AgentProperty.initList('${ctx}');
</script>
<%@ include file="../includes/portal-scripts.jsp" %>
</body>
</html>
