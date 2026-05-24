<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="../includes/init.jsp" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Manage PG Owners | EstateVault Admin</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${ctx}/css/admin.css">
    <link rel="stylesheet" href="${ctx}/css/admin-pages.css">
</head>
<body class="admin-body admin-bg-grid">
<%@ include file="../includes/admin-particles.jsp" %>
<div class="admin-dashboard-wrap">
    <%@ include file="../includes/admin-sidebar.jsp" %>
    <div class="admin-main">
        <header class="admin-topbar">
            <h2 class="admin-topbar-title">PG Owner <span>Management</span></h2>
        </header>
        <div class="admin-content">
            <div class="admin-panel mb-3">
                <div class="d-flex flex-wrap gap-2 align-items-center justify-content-between">
                    <div class="btn-group" role="group">
                        <a href="${ctx}/admin/pg-owners?status=PENDING" class="admin-btn-outline ${statusFilter == 'PENDING' ? 'active' : ''}">Pending</a>
                        <a href="${ctx}/admin/pg-owners?status=ACTIVE" class="admin-btn-outline ${statusFilter == 'ACTIVE' ? 'active' : ''}">Active</a>
                        <a href="${ctx}/admin/pg-owners?status=DISABLED" class="admin-btn-outline ${statusFilter == 'DISABLED' ? 'active' : ''}">Rejected</a>
                        <a href="${ctx}/admin/pg-owners?status=ALL" class="admin-btn-outline ${statusFilter == 'ALL' ? 'active' : ''}">All</a>
                    </div>
                    <span class="small" style="color:var(--admin-muted);" id="listSummary">Loading...</span>
                </div>
            </div>
            <div class="admin-panel">
                <div class="table-responsive">
                    <table class="table table-dark table-hover align-middle mb-0" style="--bs-table-bg:transparent;">
                        <thead>
                            <tr>
                                <th>Owner ID</th>
                                <th>Name</th>
                                <th>Email</th>
                                <th>Mobile</th>
                                <th>City</th>
                                <th>Verified</th>
                                <th>Status</th>
                                <th>Registered</th>
                                <th></th>
                            </tr>
                        </thead>
                        <tbody id="pgOwnersTableBody">
                            <tr><td colspan="9" class="text-center py-4" style="color:var(--admin-muted);">Loading...</td></tr>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>
<div id="toast-container" class="toast-container"></div>
<div id="loader" class="loader-overlay"><div class="spinner"></div></div>
<script src="${ctx}/js/auth.js"></script>
<script src="${ctx}/js/admin.js"></script>
<script>
AdminPgOwnerAPI.base = '${ctx}';
const ctx = '${ctx}';
const statusFilter = '${statusFilter}';

function statusBadge(s) {
  const map = { PENDING: 'warning', ACTIVE: 'success', DISABLED: 'danger', LOCKED: 'secondary' };
  return '<span class="badge bg-' + (map[s] || 'secondary') + '">' + s + '</span>';
}
function formatDate(iso) {
  if (!iso) return '—';
  try { return new Date(iso).toLocaleString(); } catch (e) { return iso; }
}
function verifiedBadge(v) {
  return v ? '<span class="badge bg-success">Yes</span>' : '<span class="badge bg-secondary">No</span>';
}
async function loadPgOwners() {
  UI.showLoader();
  try {
    const r = await AdminPgOwnerAPI.list(statusFilter);
    const list = r.data || [];
    const tbody = document.getElementById('pgOwnersTableBody');
    document.getElementById('listSummary').textContent = list.length + ' PG owner(s)';
    if (!list.length) {
      tbody.innerHTML = '<tr><td colspan="9" class="text-center py-4" style="color:var(--admin-muted);">No PG owners found</td></tr>';
      return;
    }
    tbody.innerHTML = list.map(function (o) {
      const actionLabel = o.accountStatus === 'PENDING' ? 'Review' : 'Manage';
      return '<tr>' +
        '<td><strong>' + (o.pgOwnerCode || '—') + '</strong></td>' +
        '<td>' + (o.fullName || '') + '</td>' +
        '<td class="small">' + (o.email || '') + '</td>' +
        '<td class="small">' + (o.mobile || '') + '</td>' +
        '<td>' + (o.city || '') + '</td>' +
        '<td>' + verifiedBadge(o.verified) + '</td>' +
        '<td>' + statusBadge(o.accountStatus) + '</td>' +
        '<td class="small">' + formatDate(o.createdAt) + '</td>' +
        '<td><a class="admin-btn-gold btn-sm" style="padding:0.35rem 0.75rem;text-decoration:none;" href="' + ctx + '/admin/pg-owners/' + o.id + '">' + actionLabel + '</a></td>' +
        '</tr>';
    }).join('');
  } catch (e) {
    UI.toast(e.message, 'error');
  } finally {
    UI.hideLoader();
  }
}
loadPgOwners();
</script>
<%@ include file="../includes/portal-scripts.jsp" %>
</body>
</html>
