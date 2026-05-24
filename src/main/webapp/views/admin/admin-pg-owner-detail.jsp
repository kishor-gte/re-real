<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="../includes/init.jsp" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Review PG Owner | EstateVault Admin</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${ctx}/css/admin.css">
    <link rel="stylesheet" href="${ctx}/css/admin-pages.css">
    <style>
        .admin-detail-grid { display:grid; grid-template-columns:repeat(auto-fit,minmax(240px,1fr)); gap:1rem; }
        .admin-detail-item label { display:block; font-size:0.72rem; text-transform:uppercase; letter-spacing:0.06em; color:var(--admin-muted); margin-bottom:0.25rem; }
        .admin-detail-item strong { font-weight:600; color:var(--admin-text); word-break:break-word; }
        .admin-doc-preview { max-width:200px; max-height:140px; border-radius:12px; border:1px solid var(--admin-glass-border); margin:0.35rem; }
        .admin-status-pills { display:flex; flex-wrap:wrap; gap:0.5rem; margin-top:0.75rem; }
    </style>
</head>
<body class="admin-body admin-bg-grid">
<%@ include file="../includes/admin-particles.jsp" %>
<div class="admin-dashboard-wrap">
    <%@ include file="../includes/admin-sidebar.jsp" %>
    <div class="admin-main">
        <header class="admin-topbar">
            <h2 class="admin-topbar-title">Review <span>PG Owner</span></h2>
            <a href="${ctx}/admin/pg-owners?status=${pgOwner.accountStatus == 'PENDING' ? 'PENDING' : pgOwner.accountStatus == 'ACTIVE' ? 'ACTIVE' : 'ALL'}" class="admin-btn-outline">&#8592; Back to list</a>
        </header>
        <div class="admin-content">
            <div class="admin-welcome-banner mb-3">
                <h1>${pgOwner.fullName}</h1>
                <p>${pgOwner.pgOwnerCode} &middot; ${pgOwner.email} &middot; ${pgOwner.mobile}</p>
                <div class="admin-status-pills">
                    <span class="badge bg-${pgOwner.accountStatus == 'ACTIVE' ? 'success' : pgOwner.accountStatus == 'PENDING' ? 'warning text-dark' : pgOwner.accountStatus == 'LOCKED' ? 'secondary' : 'danger'}">${pgOwner.accountStatus}</span>
                    <span class="badge bg-${pgOwner.verified ? 'success' : 'secondary'}">${pgOwner.verified ? 'Email verified' : 'Email not verified'}</span>
                    <c:if test="${not empty pgOwner.approvedAt}">
                        <span class="badge bg-info text-dark">Approved ${pgOwner.approvedAt}</span>
                    </c:if>
                </div>
            </div>
            <div class="admin-panel mb-3">
                <h3>Owner &amp; PG Details</h3>
                <div class="admin-detail-grid mt-3">
                    <div class="admin-detail-item"><label>Full Name</label><strong>${pgOwner.fullName}</strong></div>
                    <div class="admin-detail-item"><label>Email</label><strong>${pgOwner.email}</strong></div>
                    <div class="admin-detail-item"><label>Mobile</label><strong>${pgOwner.mobile}</strong></div>
                    <div class="admin-detail-item"><label>PG / Hostel Name</label><strong>${empty pgOwner.pgName ? '—' : pgOwner.pgName}</strong></div>
                    <div class="admin-detail-item"><label>PG Type</label><strong>${empty pgOwner.pgType ? '—' : pgOwner.pgType}</strong></div>
                    <div class="admin-detail-item"><label>Gender Allowed</label><strong>${empty pgOwner.genderAllowed ? '—' : pgOwner.genderAllowed}</strong></div>
                    <div class="admin-detail-item"><label>Experience</label><strong>${pgOwner.experience} years</strong></div>
                    <div class="admin-detail-item"><label>Business Reg. No.</label><strong>${empty pgOwner.businessRegistrationNumber ? '—' : pgOwner.businessRegistrationNumber}</strong></div>
                    <div class="admin-detail-item"><label>Address</label><strong>${empty pgOwner.officeAddress ? '—' : pgOwner.officeAddress}</strong></div>
                    <div class="admin-detail-item"><label>City / State</label><strong>${pgOwner.city}, ${pgOwner.state}</strong></div>
                    <div class="admin-detail-item"><label>Pincode</label><strong>${pgOwner.pincode}</strong></div>
                    <div class="admin-detail-item"><label>Referral Used</label><strong>${empty pgOwner.referralCodeUsed ? '—' : pgOwner.referralCodeUsed}</strong></div>
                    <div class="admin-detail-item"><label>Registered</label><strong>${pgOwner.createdAt}</strong></div>
                    <div class="admin-detail-item"><label>Last Login</label><strong>${empty pgOwner.lastLogin ? 'Never' : pgOwner.lastLogin}</strong></div>
                </div>
            </div>
            <div class="admin-panel mb-3">
                <h3>Documents</h3>
                <div class="row g-3 mt-2">
                    <div class="col-md-4">
                        <label class="small" style="color:var(--admin-muted);">Profile Photo</label>
                        <c:choose>
                            <c:when test="${not empty pgOwner.profilePhoto}">
                                <p class="mt-1"><a href="${ctx}${pgOwner.profilePhoto}" target="_blank" rel="noopener"><img src="${ctx}${pgOwner.profilePhoto}" alt="Profile" class="admin-doc-preview"></a></p>
                            </c:when>
                            <c:otherwise><p class="mt-1" style="color:var(--admin-muted);">Not uploaded</p></c:otherwise>
                        </c:choose>
                    </div>
                    <div class="col-md-8">
                        <label class="small" style="color:var(--admin-muted);">PG / Hostel Images</label>
                        <div id="pgImagesGallery" class="mt-1 d-flex flex-wrap gap-2"></div>
                        <p id="pgImagesEmpty" class="mt-1" style="color:var(--admin-muted);display:none;">Not uploaded</p>
                    </div>
                    <div class="col-md-12">
                        <label class="small" style="color:var(--admin-muted);">Government ID</label>
                        <c:choose>
                            <c:when test="${not empty pgOwner.governmentId}">
                                <p class="mt-1"><a href="${ctx}${pgOwner.governmentId}" target="_blank" rel="noopener" class="admin-btn-outline">View / Download ID</a></p>
                            </c:when>
                            <c:otherwise><p class="mt-1" style="color:var(--admin-muted);">Not uploaded</p></c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>
            <c:if test="${not empty pgOwner.rejectionReason}">
            <div class="admin-panel mb-3" style="border-color:rgba(239,68,68,0.4);">
                <h3>Rejection Reason</h3>
                <p class="mb-0 mt-2">${pgOwner.rejectionReason}</p>
            </div>
            </c:if>
            <div class="admin-panel" id="adminActionsPanel">
                <h3>Admin Actions</h3>
                <c:if test="${pgOwner.accountStatus == 'PENDING'}">
                    <p class="small mt-2" style="color:var(--admin-muted);">Review all details and documents before approving this PG owner. They can sign in only after approval.</p>
                    <c:if test="${!pgOwner.verified}">
                        <p class="small text-warning mt-2">&#9888; Email verification flag is off — approval may fail until OTP registration completed correctly.</p>
                    </c:if>
                    <div class="d-flex flex-wrap gap-2 mt-3">
                        <button type="button" class="admin-btn-gold" id="btnApprove">&#10003; Approve PG Owner</button>
                        <button type="button" class="admin-btn-outline" id="btnRejectToggle">&#10007; Reject Application</button>
                    </div>
                    <div id="rejectBox" class="mt-3" style="display:none;">
                        <label class="small" style="color:var(--admin-muted);">Rejection reason (required)</label>
                        <textarea id="rejectReason" class="admin-form-control form-control mt-1" rows="3" maxlength="500" placeholder="Explain why this application is rejected…"></textarea>
                        <button type="button" class="admin-btn-outline mt-2" id="btnConfirmReject">Confirm Rejection</button>
                    </div>
                </c:if>
                <c:if test="${pgOwner.accountStatus == 'ACTIVE'}">
                    <p class="small mt-2" style="color:var(--admin-muted);">This PG owner is active and can sign in. Suspend to block access immediately.</p>
                    <button type="button" class="admin-btn-outline mt-2" id="btnSuspend">Suspend PG Owner</button>
                </c:if>
                <c:if test="${pgOwner.accountStatus == 'DISABLED' && empty pgOwner.rejectionReason}">
                    <p class="small mt-2" style="color:var(--admin-muted);">This PG owner was suspended. Reactivate to restore login access.</p>
                    <button type="button" class="admin-btn-gold mt-2" id="btnReactivate">Reactivate PG Owner</button>
                </c:if>
                <c:if test="${pgOwner.accountStatus == 'DISABLED' && not empty pgOwner.rejectionReason}">
                    <p class="small mt-2" style="color:var(--admin-muted);">This application was rejected. The PG owner must register again if policy allows.</p>
                </c:if>
                <c:if test="${pgOwner.accountStatus == 'LOCKED'}">
                    <p class="small mt-2" style="color:var(--admin-muted);">Account is locked due to failed login attempts. Contact support or implement unlock from admin if needed.</p>
                </c:if>
            </div>
        </div>
    </div>
</div>
<div id="toast-container" class="toast-container"></div>
<div id="loader" class="loader-overlay"><div class="spinner"></div></div>
<script src="${ctx}/js/auth.js"></script>
<script src="${ctx}/js/admin.js"></script>
<script type="application/json" id="pgImagesData"><c:out value="${pgOwner.pgImages}" escapeXml="true"/></script>
<script>
AdminPgOwnerAPI.base = '${ctx}';
const ctx = '${ctx}';
const pgOwnerId = ${pgOwner.id};

(function renderPgImages() {
  const gallery = document.getElementById('pgImagesGallery');
  const empty = document.getElementById('pgImagesEmpty');
  const raw = (document.getElementById('pgImagesData')?.textContent || '').trim();
  if (!raw) { empty.style.display = 'block'; return; }
  try {
    const paths = JSON.parse(raw);
    if (!paths || !paths.length) { empty.style.display = 'block'; return; }
    gallery.innerHTML = paths.map(function (p) {
      return '<a href="' + ctx + p + '" target="_blank" rel="noopener"><img src="' + ctx + p + '" alt="PG" class="admin-doc-preview"></a>';
    }).join('');
  } catch (e) {
    empty.style.display = 'block';
  }
})();

document.getElementById('btnApprove')?.addEventListener('click', async function () {
  if (!confirm('Approve this PG owner? They will be able to sign in after approval.')) return;
  UI.showLoader();
  try {
    await AdminPgOwnerAPI.approve(pgOwnerId);
    UI.toast('PG owner approved', 'success');
    setTimeout(function () { location.href = ctx + '/admin/pg-owners?status=ACTIVE'; }, 800);
  } catch (e) { UI.toast(e.message, 'error'); }
  finally { UI.hideLoader(); }
});

document.getElementById('btnRejectToggle')?.addEventListener('click', function () {
  const box = document.getElementById('rejectBox');
  box.style.display = box.style.display === 'none' ? 'block' : 'none';
});

document.getElementById('btnConfirmReject')?.addEventListener('click', async function () {
  const reason = (document.getElementById('rejectReason').value || '').trim();
  if (!reason) { UI.toast('Rejection reason is required', 'error'); return; }
  if (!confirm('Reject this PG owner application?')) return;
  UI.showLoader();
  try {
    await AdminPgOwnerAPI.reject(pgOwnerId, reason);
    UI.toast('Application rejected', 'success');
    setTimeout(function () { location.href = ctx + '/admin/pg-owners?status=DISABLED'; }, 800);
  } catch (e) { UI.toast(e.message, 'error'); }
  finally { UI.hideLoader(); }
});

document.getElementById('btnSuspend')?.addEventListener('click', async function () {
  if (!confirm('Suspend this active PG owner? They will lose login access.')) return;
  UI.showLoader();
  try {
    await AdminPgOwnerAPI.suspend(pgOwnerId);
    UI.toast('PG owner suspended', 'success');
    location.reload();
  } catch (e) { UI.toast(e.message, 'error'); }
  finally { UI.hideLoader(); }
});

document.getElementById('btnReactivate')?.addEventListener('click', async function () {
  if (!confirm('Reactivate this PG owner?')) return;
  UI.showLoader();
  try {
    await AdminPgOwnerAPI.reactivate(pgOwnerId);
    UI.toast('PG owner reactivated', 'success');
    setTimeout(function () { location.href = ctx + '/admin/pg-owners?status=ACTIVE'; }, 800);
  } catch (e) { UI.toast(e.message, 'error'); }
  finally { UI.hideLoader(); }
});
</script>
<%@ include file="../includes/portal-scripts.jsp" %>
</body>
</html>
