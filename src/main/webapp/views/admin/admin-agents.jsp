<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="../includes/init.jsp" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Manage Agents | EstateVault Admin</title>
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
            <h2 class="admin-topbar-title">Agent <span>Management</span></h2>
        </header>
        <div class="admin-content">
            <div class="admin-panel mb-3">
                <div class="d-flex flex-wrap gap-2 align-items-center justify-content-between">
                    <div class="btn-group" role="group" id="statusTabs">
                        <a href="${ctx}/admin/agents?status=PENDING" class="admin-btn-outline ${statusFilter == 'PENDING' ? 'active' : ''}" data-status="PENDING">Pending</a>
                        <a href="${ctx}/admin/agents?status=ACTIVE" class="admin-btn-outline ${statusFilter == 'ACTIVE' ? 'active' : ''}" data-status="ACTIVE">Active</a>
                        <a href="${ctx}/admin/agents?status=DISABLED" class="admin-btn-outline ${statusFilter == 'DISABLED' ? 'active' : ''}" data-status="DISABLED">Rejected</a>
                        <a href="${ctx}/admin/agents?status=ALL" class="admin-btn-outline ${statusFilter == 'ALL' ? 'active' : ''}" data-status="ALL">All</a>
                    </div>
                    <span class="small" style="color:var(--admin-muted);" id="listSummary">Loading…</span>
                </div>
            </div>
            <div class="admin-panel">
                <div class="table-responsive">
                    <table class="table table-dark table-hover align-middle mb-0" style="--bs-table-bg:transparent;">
                        <thead>
                            <tr>
                                <th>Agent ID</th>
                                <th>Name</th>
                                <th>Agency</th>
                                <th>Email</th>
                                <th>RERA</th>
                                <th>City</th>
                                <th>Status</th>
                                <th>Registered</th>
                                <th></th>
                            </tr>
                        </thead>
                        <tbody id="agentsTableBody">
                            <tr><td colspan="9" class="text-center py-4" style="color:var(--admin-muted);">Loading agents…</td></tr>
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
AdminAgentAPI.base = '${ctx}';
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

async function loadAgents() {
  UI.showLoader();
  try {
    const r = await AdminAgentAPI.list(statusFilter);
    const agents = r.data || [];
    const tbody = document.getElementById('agentsTableBody');
    document.getElementById('listSummary').textContent = agents.length + ' agent(s)';
    if (!agents.length) {
      tbody.innerHTML = '<tr><td colspan="9" class="text-center py-4" style="color:var(--admin-muted);">No agents found</td></tr>';
      return;
    }
    tbody.innerHTML = agents.map(function (a) {
      return '<tr>' +
        '<td><strong>' + (a.agentCode || '—') + '</strong></td>' +
        '<td>' + (a.fullName || '') + '</td>' +
        '<td>' + (a.agencyName || '') + '</td>' +
        '<td class="small">' + (a.email || '') + '</td>' +
        '<td class="small">' + (a.reraNumber || '') + '</td>' +
        '<td>' + (a.city || '') + '</td>' +
        '<td>' + statusBadge(a.accountStatus) + '</td>' +
        '<td class="small">' + formatDate(a.createdAt) + '</td>' +
        '<td><a class="admin-btn-gold btn-sm" style="padding:0.35rem 0.75rem;text-decoration:none;" href="' + ctx + '/admin/agents/' + a.id + '">Review</a></td>' +
        '</tr>';
    }).join('');
  } catch (e) {
    UI.toast(e.message, 'error');
  } finally {
    UI.hideLoader();
  }
}
loadAgents();
</script>
<%@ include file="../includes/portal-scripts.jsp" %>
</body>
</html>
