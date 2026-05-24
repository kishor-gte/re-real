<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="../includes/init.jsp" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Pending Approval | EstateVault Agent</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${ctx}/css/agent.css">
    <link rel="stylesheet" href="${ctx}/css/agent-pages.css">
</head>
<body class="agent-body agent-bg-grid agent-page">
<%@ include file="../includes/agent-particles.jsp" %>
<nav class="agent-nav">
    <a class="agent-brand" href="${ctx}/"><span class="agent-brand-badge">Agent</span> EstateVault</a>
</nav>
<main class="agent-form-panel" style="min-height:80vh;display:flex;align-items:center;justify-content:center;">
    <div class="agent-glass agent-glass-neon agent-card-animate text-center" style="max-width:520px;padding:2.5rem;">
        <div class="agent-otp-ring" style="font-size:3rem;">&#9203;</div>
        <h1 style="margin:1rem 0 0.5rem;">Application On Hold</h1>
        <p style="color:var(--agent-muted);">Your email is verified and your broker application has been submitted.</p>
        <c:if test="${not empty email}">
            <p class="mt-2"><strong style="color:var(--agent-gold);">${email}</strong></p>
        </c:if>
        <div class="mt-4 p-3 rounded" style="background:rgba(234,179,8,0.12);border:1px solid rgba(234,179,8,0.35);">
            <strong style="color:#eab308;">Awaiting admin approval</strong>
            <p class="small mb-0 mt-2" style="color:var(--agent-muted);">
                An administrator will review your RERA credentials, agency details, and documents.
                You will receive an email when your account is approved.
            </p>
        </div>
        <p class="small mt-4 mb-0" style="color:var(--agent-muted);">You cannot sign in until approval is complete.</p>
        <a href="${ctx}/agent/login" class="agent-btn-outline d-inline-block mt-4" style="text-decoration:none;">Back to Login</a>
    </div>
</main>
<%@ include file="../includes/portal-scripts.jsp" %>
</body>
</html>
