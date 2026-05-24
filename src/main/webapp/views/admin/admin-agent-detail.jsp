<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="../includes/init.jsp" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Review Agent | EstateVault Admin</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${ctx}/css/admin.css">
    <link rel="stylesheet" href="${ctx}/css/admin-pages.css">
    <style>
        .admin-detail-grid { display:grid; grid-template-columns:repeat(auto-fit,minmax(240px,1fr)); gap:1rem; }
        .admin-detail-item label { display:block; font-size:0.72rem; text-transform:uppercase; letter-spacing:0.06em; color:var(--admin-muted); margin-bottom:0.25rem; }
        .admin-detail-item strong { font-weight:600; color:var(--admin-text); word-break:break-word; }
        .admin-doc-preview { max-width:200px; max-height:140px; border-radius:12px; border:1px solid var(--admin-glass-border); }
    </style>
</head>
<body class="admin-body admin-bg-grid">
<%@ include file="../includes/admin-particles.jsp" %>
<div class="admin-dashboard-wrap">
    <%@ include file="../includes/admin-sidebar.jsp" %>
    <div class="admin-main">
        <header class="admin-topbar">
            <h2 class="admin-topbar-title">Review <span>Agent</span></h2>
            <a href="${ctx}/admin/agents?status=PENDING" class="admin-btn-outline">&#8592; Back to list</a>
        </header>
        <div class="admin-content">
            <div class="admin-welcome-banner mb-3">
                <h1>${agent.fullName}</h1>
                <p>${agent.agentCode} &middot; ${agent.accountStatus} &middot; ${agent.email}</p>
            </div>

            <div class="admin-panel mb-3">
                <h3>Personal &amp; Contact</h3>
                <div class="admin-detail-grid mt-3">
                    <div class="admin-detail-item"><label>Full Name</label><strong>${agent.fullName}</strong></div>
                    <div class="admin-detail-item"><label>Email</label><strong>${agent.email}</strong></div>
                    <div class="admin-detail-item"><label>Mobile</label><strong>${agent.mobile}</strong></div>
                    <div class="admin-detail-item"><label>Agent ID</label><strong>${agent.agentCode}</strong></div>
                    <div class="admin-detail-item"><label>Referral Code</label><strong>${agent.referralCode}</strong></div>
                </div>
            </div>

            <div class="admin-panel mb-3">
                <h3>Professional Details</h3>
                <div class="admin-detail-grid mt-3">
                    <div class="admin-detail-item"><label>Agency Name</label><strong>${agent.agencyName}</strong></div>
                    <div class="admin-detail-item"><label>RERA Number</label><strong>${agent.reraNumber}</strong></div>
                    <div class="admin-detail-item"><label>Experience</label><strong>${agent.experience} years</strong></div>
                    <div class="admin-detail-item"><label>Specialization</label><strong>${agent.specialization}</strong></div>
                    <div class="admin-detail-item"><label>Office Address</label><strong>${agent.officeAddress}</strong></div>
                    <div class="admin-detail-item"><label>City / State</label><strong>${agent.city}, ${agent.state}</strong></div>
                    <div class="admin-detail-item"><label>Pincode</label><strong>${agent.pincode}</strong></div>
                </div>
            </div>

            <div class="admin-panel mb-3">
                <h3>Documents</h3>
                <div class="row g-3 mt-2">
                    <div class="col-md-4">
                        <label class="small" style="color:var(--admin-muted);">Profile Photo</label>
                        <c:choose>
                            <c:when test="${not empty agent.profilePhoto}">
                                <p class="mt-1"><a href="${ctx}${agent.profilePhoto}" target="_blank" rel="noopener"><img src="${ctx}${agent.profilePhoto}" alt="Profile" class="admin-doc-preview"></a></p>
                            </c:when>
                            <c:otherwise><p class="mt-1" style="color:var(--admin-muted);">Not uploaded</p></c:otherwise>
                        </c:choose>
                    </div>
                    <div class="col-md-4">
                        <label class="small" style="color:var(--admin-muted);">Agency Logo</label>
                        <c:choose>
                            <c:when test="${not empty agent.agencyLogo}">
                                <p class="mt-1"><a href="${ctx}${agent.agencyLogo}" target="_blank" rel="noopener"><img src="${ctx}${agent.agencyLogo}" alt="Logo" class="admin-doc-preview"></a></p>
                            </c:when>
                            <c:otherwise><p class="mt-1" style="color:var(--admin-muted);">Not uploaded</p></c:otherwise>
                        </c:choose>
                    </div>
                    <div class="col-md-4">
                        <label class="small" style="color:var(--admin-muted);">Government ID</label>
                        <c:choose>
                            <c:when test="${not empty agent.governmentId}">
                                <p class="mt-1"><a href="${ctx}${agent.governmentId}" target="_blank" rel="noopener" class="admin-btn-outline">View / Download ID</a></p>
                            </c:when>
                            <c:otherwise><p class="mt-1" style="color:var(--admin-muted);">Not uploaded</p></c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>

            <c:if test="${not empty agent.rejectionReason}">
            <div class="admin-panel mb-3" style="border-color:rgba(239,68,68,0.4);">
                <h3>Rejection Reason</h3>
                <p class="mb-0 mt-2">${agent.rejectionReason}</p>
            </div>
            </c:if>

            <div class="admin-panel" id="adminActionsPanel">
                <h3>Admin Actions</h3>
                <c:if test="${agent.accountStatus == 'PENDING'}">
                    <p class="small mt-2" style="color:var(--admin-muted);">Review all details and documents before approving this broker account.</p>
                    <div class="d-flex flex-wrap gap-2 mt-3">
                        <button type="button" class="admin-btn-gold" id="btnApprove">&#10003; Approve Agent</button>
                        <button type="button" class="admin-btn-outline" id="btnRejectToggle">&#10007; Reject Application</button>
                    </div>
                    <div id="rejectBox" class="mt-3" style="display:none;">
                        <label class="small" style="color:var(--admin-muted);">Rejection reason (required)</label>
                        <textarea id="rejectReason" class="admin-form-control form-control mt-1" rows="3" maxlength="500" placeholder="Explain why this application is rejected…"></textarea>
                        <button type="button" class="admin-btn-outline mt-2" id="btnConfirmReject">Confirm Rejection</button>
                    </div>
                </c:if>
                <c:if test="${agent.accountStatus == 'ACTIVE'}">
                    <button type="button" class="admin-btn-outline" id="btnSuspend">Suspend Agent</button>
                </c:if>
                <c:if test="${agent.accountStatus == 'DISABLED' && empty agent.rejectionReason}">
                    <button type="button" class="admin-btn-gold" id="btnReactivate">Reactivate Agent</button>
                </c:if>
                <c:if test="${agent.accountStatus == 'DISABLED' && not empty agent.rejectionReason}">
                    <p class="small mt-2" style="color:var(--admin-muted);">This application was rejected. The agent must register again if policy allows.</p>
                </c:if>
            </div>
        </div>
    </div>
</div>
<div id="toast-container" class="toast-container"></div>
<div id="loader" class="loader-overlay"><div class="spinner"></div></div>
<script src="${ctx}/js/auth.js"></script>
<script src="${ctx}/js/admin.js"></script>
<script>
AdminAgentAPI.base = '${ctx}';
const agentId = ${agent.id};
const ctx = '${ctx}';

document.getElementById('btnApprove')?.addEventListener('click', async function () {
  if (!confirm('Approve this agent? They will be able to sign in immediately.')) return;
  UI.showLoader();
  try {
    await AdminAgentAPI.approve(agentId);
    UI.toast('Agent approved');
    setTimeout(function () { location.href = ctx + '/admin/agents?status=ACTIVE'; }, 800);
  } catch (e) { UI.toast(e.message, 'error'); }
  finally { UI.hideLoader(); }
});

document.getElementById('btnRejectToggle')?.addEventListener('click', function () {
  const box = document.getElementById('rejectBox');
  box.style.display = box.style.display === 'none' ? 'block' : 'none';
});

document.getElementById('btnConfirmReject')?.addEventListener('click', async function () {
  const reason = document.getElementById('rejectReason').value.trim();
  if (!reason) { UI.toast('Rejection reason is required', 'error'); return; }
  if (!confirm('Reject this agent application?')) return;
  UI.showLoader();
  try {
    await AdminAgentAPI.reject(agentId, reason);
    UI.toast('Application rejected');
    setTimeout(function () { location.href = ctx + '/admin/agents?status=DISABLED'; }, 800);
  } catch (e) { UI.toast(e.message, 'error'); }
  finally { UI.hideLoader(); }
});

document.getElementById('btnSuspend')?.addEventListener('click', async function () {
  if (!confirm('Suspend this active agent?')) return;
  UI.showLoader();
  try {
    await AdminAgentAPI.suspend(agentId);
    UI.toast('Agent suspended');
    location.reload();
  } catch (e) { UI.toast(e.message, 'error'); }
  finally { UI.hideLoader(); }
});

document.getElementById('btnReactivate')?.addEventListener('click', async function () {
  if (!confirm('Reactivate this agent?')) return;
  UI.showLoader();
  try {
    await AdminAgentAPI.reactivate(agentId);
    UI.toast('Agent reactivated');
    setTimeout(function () { location.href = ctx + '/admin/agents?status=ACTIVE'; }, 800);
  } catch (e) { UI.toast(e.message, 'error'); }
  finally { UI.hideLoader(); }
});
</script>
<%@ include file="../includes/portal-scripts.jsp" %>
</body>
</html>
