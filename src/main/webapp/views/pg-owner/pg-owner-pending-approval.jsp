<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="../includes/init.jsp" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Pending Approval | EstateVault PG Owner</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${ctx}/css/pg-owner.css">
    <link rel="stylesheet" href="${ctx}/css/pg-owner-pages.css">
</head>
<body class="pgo-body pgo-bg-grid pgo-page">
<%@ include file="../includes/pg-owner-particles.jsp" %>
<nav class="pgo-nav">
    <a class="pgo-brand" href="${ctx}/"><span class="pgo-brand-badge">PG Owner</span> EstateVault</a>
</nav>
<main class="pgo-form-panel" style="min-height:80vh;display:flex;align-items:center;justify-content:center;">
    <div class="pgo-glass pgo-glass-neon pgo-card-animate text-center" style="max-width:520px;padding:2.5rem;">
        <div class="pgo-otp-ring" style="font-size:3rem;">&#9203;</div>
        <h1 style="margin:1rem 0 0.5rem;">Application On Hold</h1>
        <p style="color:var(--pgo-muted);">Your email is verified and your PG owner application has been submitted.</p>
        <c:if test="${not empty email}">
            <p class="mt-2"><strong style="color:var(--pgo-gold);">${email}</strong></p>
        </c:if>
        <div class="mt-4 p-3 rounded" style="background:rgba(234,179,8,0.12);border:1px solid rgba(234,179,8,0.35);">
            <strong style="color:#eab308;">Awaiting admin approval</strong>
            <p class="small mb-0 mt-2" style="color:var(--pgo-muted);">
                An administrator will review your PG details, documents, and uploaded images.
                You will receive an email when your account is approved.
            </p>
        </div>
        <p class="small mt-4 mb-0" style="color:var(--pgo-muted);">You cannot sign in until approval is complete.</p>
        <a href="${ctx}/pg-owner/login" class="pgo-btn-outline d-inline-block mt-4" style="text-decoration:none;">Back to Login</a>
    </div>
</main>
<%@ include file="../includes/portal-scripts.jsp" %>
</body>
</html>

