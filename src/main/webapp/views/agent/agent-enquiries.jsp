<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="../includes/init.jsp" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Leads &amp; Inquiries | Agent Portal</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${ctx}/css/agent.css">
    <link rel="stylesheet" href="${ctx}/css/agent-pages.css">
    <link rel="stylesheet" href="${ctx}/css/rtc-chat.css?v=3">
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
</head>
<body class="agent-body agent-bg-grid">
<%@ include file="../includes/agent-particles.jsp" %>
<div class="agent-dashboard-wrap">
    <%@ include file="../includes/agent-sidebar.jsp" %>
    <div class="agent-main">
        <header class="agent-topbar">
            <h2 class="agent-topbar-title">Leads &amp; <span>Inquiries</span></h2>
        </header>
        <div class="agent-content">
            <div id="toast-container" class="toast-container"></div>
            <div id="loader" class="loader-overlay"><div class="spinner"></div></div>
            <p class="agent-panel-hint">Accept buyer enquiries, then send your reply. Buyers see replies on their Explore Properties page.</p>
            <div id="enquiriesEmpty" class="agent-panel text-center py-5" style="display:none;">
                <p style="color:var(--agent-muted);">No enquiries yet. They appear when users enquire on your active listings.</p>
            </div>
            <div id="enquiriesList" class="agent-enquiries-list"></div>
        </div>
    </div>
</div>
<%@ include file="../includes/rtc-shell.jsp" %>
<script src="${ctx}/js/auth.js"></script>
<script src="${ctx}/js/agent.js"></script>
<script src="${ctx}/js/agent-enquiries.js"></script>
<script>
AgentAPI.base = '${ctx}';
EstateRTC.init({ ctx: '${ctx}', mode: 'agent', api: AgentAPI });
AgentEnquiries.init('${ctx}');
</script>
<%@ include file="../includes/portal-scripts.jsp" %>
</body>
</html>
