<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="../includes/init.jsp" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Manage Users | EstateVault Admin</title>
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
            <h2 class="admin-topbar-title">User <span>Management</span></h2>
        </header>
        <div class="admin-content">
            <div class="admin-panel mb-3">
                <div class="d-flex flex-wrap gap-2 align-items-center justify-content-between mb-3">
                    <div class="btn-group" role="group">
                        <a href="${ctx}/admin/users?role=ALL&status=${statusFilter}" class="admin-btn-outline ${roleFilter == 'ALL' ? 'active' : ''}">All roles</a>
                        <a href="${ctx}/admin/users?role=BUYER&status=${statusFilter}" class="admin-btn-outline ${roleFilter == 'BUYER' ? 'active' : ''}">Buyers</a>
                        <a href="${ctx}/admin/users?role=SELLER&status=${statusFilter}" class="admin-btn-outline ${roleFilter == 'SELLER' ? 'active' : ''}">Sellers</a>
                    </div>
                    <div class="btn-group" role="group">
                        <a href="${ctx}/admin/users?role=${roleFilter}&status=ALL" class="admin-btn-outline ${statusFilter == 'ALL' ? 'active' : ''}">All status</a>
                        <a href="${ctx}/admin/users?role=${roleFilter}&status=ACTIVE" class="admin-btn-outline ${statusFilter == 'ACTIVE' ? 'active' : ''}">Active</a>
                        <a href="${ctx}/admin/users?role=${roleFilter}&status=PENDING" class="admin-btn-outline ${statusFilter == 'PENDING' ? 'active' : ''}">Pending</a>
                    </div>
                </div>
                <div class="row g-2 align-items-center">
                    <div class="col-md-8">
                        <input type="search" id="userSearch" class="admin-form-control form-control" placeholder="Search by name, email, mobile, city, role…" autocomplete="off">
                    </div>
                    <div class="col-md-4 text-md-end">
                        <span class="small" style="color:var(--admin-muted);" id="listSummary">Loading…</span>
                    </div>
                </div>
            </div>
            <div class="admin-panel">
                <div class="table-responsive">
                    <table class="table table-dark table-hover align-middle mb-0" style="--bs-table-bg:transparent;">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Name</th>
                                <th>Email</th>
                                <th>Mobile</th>
                                <th>Role</th>
                                <th>City</th>
                                <th>Verified</th>
                                <th>Status</th>
                                <th>Registered</th>
                            </tr>
                        </thead>
                        <tbody id="usersTableBody">
                            <tr><td colspan="9" class="text-center py-4" style="color:var(--admin-muted);">Loading users…</td></tr>
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
AdminUserAPI.base = '${ctx}';
const roleFilter = '${roleFilter}';
const statusFilter = '${statusFilter}';
let searchTimer;

function statusBadge(s) {
  const map = { PENDING: 'warning', ACTIVE: 'success', DISABLED: 'danger', LOCKED: 'secondary' };
  return '<span class="badge bg-' + (map[s] || 'secondary') + '">' + s + '</span>';
}
function verifiedBadge(v) {
  return v ? '<span class="badge bg-success">Yes</span>' : '<span class="badge bg-secondary">No</span>';
}
function formatDate(iso) {
  if (!iso) return '—';
  try { return new Date(iso).toLocaleString(); } catch (e) { return iso; }
}

async function loadUsers() {
  const q = (document.getElementById('userSearch').value || '').trim();
  UI.showLoader();
  try {
    const r = await AdminUserAPI.list(roleFilter, statusFilter, q);
    const users = r.data || [];
    const tbody = document.getElementById('usersTableBody');
    document.getElementById('listSummary').textContent = users.length + ' user(s)';
    if (!users.length) {
      tbody.innerHTML = '<tr><td colspan="9" class="text-center py-4" style="color:var(--admin-muted);">No users found</td></tr>';
      return;
    }
    tbody.innerHTML = users.map(function (u) {
      return '<tr>' +
        '<td><strong>#' + u.id + '</strong></td>' +
        '<td>' + (u.fullName || '') + '</td>' +
        '<td class="small">' + (u.email || '') + '</td>' +
        '<td class="small">' + (u.mobile || '') + '</td>' +
        '<td><span class="badge bg-info text-dark">' + (u.role || '') + '</span></td>' +
        '<td>' + (u.city || '—') + '</td>' +
        '<td>' + verifiedBadge(u.verified) + '</td>' +
        '<td>' + statusBadge(u.accountStatus) + '</td>' +
        '<td class="small">' + formatDate(u.createdAt) + '</td>' +
        '</tr>';
    }).join('');
  } catch (e) {
    UI.toast(e.message, 'error');
  } finally {
    UI.hideLoader();
  }
}

document.getElementById('userSearch').addEventListener('input', function () {
  clearTimeout(searchTimer);
  searchTimer = setTimeout(loadUsers, 350);
});

loadUsers();
</script>
<%@ include file="../includes/portal-scripts.jsp" %>
</body>
</html>
