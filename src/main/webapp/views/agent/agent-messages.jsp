<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="../includes/init.jsp" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Buyer Messages | Agent Portal</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${ctx}/css/agent.css">
    <link rel="stylesheet" href="${ctx}/css/agent-pages.css">
    <link rel="stylesheet" href="${ctx}/css/agent-portal.css">
    <link rel="stylesheet" href="${ctx}/css/rtc-chat.css?v=2">
</head>
<body class="agent-body agent-bg-grid">
<%@ include file="../includes/agent-particles.jsp" %>
<div class="agent-dashboard-wrap">
    <%@ include file="../includes/agent-sidebar.jsp" %>
    <div class="agent-main">
        <header class="agent-topbar">
            <h2 class="agent-topbar-title">Buyer <span>Messages</span></h2>
            <a href="${ctx}/agent/enquiries" class="agent-btn-gold">Manage Leads</a>
        </header>
        <div class="agent-content ap-layout">
            <div class="ap-panel">
                <p class="mb-2" style="color:var(--agent-muted);">Inquiries and messages from buyers on your listings. <strong id="msgCount">0</strong> conversation(s).</p>
                <div id="msgList">Loading messages…</div>
            </div>
        </div>
    </div>
</div>
<script src="${ctx}/js/auth.js"></script>
<script src="${ctx}/js/agent.js"></script>
<script src="${ctx}/js/agent-portal.js"></script>
<%@ include file="../includes/rtc-shell.jsp" %>
<script>
AgentAPI.base = '${ctx}';
EstateRTC.init({ ctx: '${ctx}', mode: 'agent', api: AgentAPI });
AgentPortal.init('${ctx}', 'messages');
</script>
<%@ include file="../includes/portal-scripts.jsp" %>
</body>
</html>
