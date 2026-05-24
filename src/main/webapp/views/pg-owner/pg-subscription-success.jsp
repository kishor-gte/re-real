<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="../includes/init.jsp" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Subscription Success</title>
    <link rel="stylesheet" href="${ctx}/css/pg-owner.css">
    <link rel="stylesheet" href="${ctx}/css/subscription.css">
</head>
<body class="pgo-body">
<script>window.location.replace('${ctx}/pg-owner/subscription?success=1');</script>
<p>Redirecting… <a href="${ctx}/pg-owner/subscription?success=1">Continue</a></p>
<%@ include file="../includes/portal-scripts.jsp" %>
</body>
</html>
