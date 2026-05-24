<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="../includes/init.jsp" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Manage Properties | EstateVault Admin</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${ctx}/css/admin.css">
    <link rel="stylesheet" href="${ctx}/css/admin-pages.css">
    <style>
        .agent-group-header { cursor:pointer; user-select:none; }
        .agent-group-header:hover { background:rgba(255,255,255,0.03); }
        .agent-group-body { display:none; }
        .agent-group.open .agent-group-body { display:block; }
        .agent-group.open .group-chevron { transform:rotate(90deg); }
        .group-chevron { display:inline-block; transition:transform .2s; margin-right:.35rem; }
    </style>
</head>
<body class="admin-body admin-bg-grid">
<%@ include file="../includes/admin-particles.jsp" %>
<div class="admin-dashboard-wrap">
    <%@ include file="../includes/admin-sidebar.jsp" %>
    <div class="admin-main">
        <header class="admin-topbar">
            <h2 class="admin-topbar-title">Property <span>Management</span></h2>
        </header>
        <div class="admin-content">
            <div class="admin-panel mb-3">
                <div class="d-flex flex-wrap gap-2 align-items-center justify-content-between mb-3">
                    <div class="btn-group" role="group">
                        <a href="${ctx}/admin/properties?status=ALL" class="admin-btn-outline ${statusFilter == 'ALL' ? 'active' : ''}">All</a>
                        <a href="${ctx}/admin/properties?status=ACTIVE" class="admin-btn-outline ${statusFilter == 'ACTIVE' ? 'active' : ''}">Active</a>
                        <a href="${ctx}/admin/properties?status=INACTIVE" class="admin-btn-outline ${statusFilter == 'INACTIVE' ? 'active' : ''}">Inactive</a>
                        <a href="${ctx}/admin/properties?status=DRAFT" class="admin-btn-outline ${statusFilter == 'DRAFT' ? 'active' : ''}">Draft</a>
                        <a href="${ctx}/admin/properties?status=SOLD" class="admin-btn-outline ${statusFilter == 'SOLD' ? 'active' : ''}">Sold</a>
                        <a href="${ctx}/admin/properties?status=RENTED" class="admin-btn-outline ${statusFilter == 'RENTED' ? 'active' : ''}">Rented</a>
                    </div>
                    <span class="small" style="color:var(--admin-muted);" id="listSummary">Loading…</span>
                </div>
                <input type="search" id="propertySearch" class="admin-form-control form-control" placeholder="Search property, code, city, agent name, agency…" autocomplete="off">
            </div>
            <div id="propertyGroups" class="d-flex flex-column gap-3">
                <div class="admin-panel text-center py-4" style="color:var(--admin-muted);">Loading properties…</div>
            </div>
        </div>
    </div>
</div>
<div id="toast-container" class="toast-container"></div>
<div id="loader" class="loader-overlay"><div class="spinner"></div></div>
<script src="${ctx}/js/auth.js"></script>
<script src="${ctx}/js/admin.js"></script>
<script>
AdminPropertyAPI.base = '${ctx}';
const ctx = '${ctx}';
const statusFilter = '${statusFilter}';
let searchTimer;

function statusBadge(s) {
  const map = { ACTIVE: 'success', INACTIVE: 'secondary', DRAFT: 'warning text-dark', SOLD: 'info text-dark', RENTED: 'primary' };
  return '<span class="badge bg-' + (map[s] || 'secondary') + '">' + s + '</span>';
}
function formatDate(iso) {
  if (!iso) return '—';
  try { return new Date(iso).toLocaleString(); } catch (e) { return iso; }
}
function formatPrice(p) {
  if (p == null) return '—';
  return '\u20B9' + Number(p).toLocaleString('en-IN');
}

function renderGroups(groups) {
  const container = document.getElementById('propertyGroups');
  let totalProps = 0;
  groups.forEach(function (g) { totalProps += (g.properties || []).length; });
  document.getElementById('listSummary').textContent = totalProps + ' propert' + (totalProps === 1 ? 'y' : 'ies') + ' · ' + groups.length + ' agent(s)';

  if (!groups.length) {
    container.innerHTML = '<div class="admin-panel text-center py-4" style="color:var(--admin-muted);">No properties found</div>';
    return;
  }

  container.innerHTML = groups.map(function (g, idx) {
    const props = g.properties || [];
    const rows = props.map(function (p) {
      return '<tr>' +
        '<td><strong>' + (p.propertyCode || '—') + '</strong></td>' +
        '<td>' + (p.title || '') + '</td>' +
        '<td>' + (p.listingType || '') + '</td>' +
        '<td class="small">' + ((p.category || '').replace(/_/g, ' ')) + '</td>' +
        '<td>' + formatPrice(p.price) + '</td>' +
        '<td>' + (p.city || '') + '</td>' +
        '<td>' + statusBadge(p.status) + '</td>' +
        '<td>' + (p.viewCount || 0) + '</td>' +
        '<td class="small">' + formatDate(p.createdAt) + '</td>' +
        '</tr>';
    }).join('');

    return '<div class="admin-panel agent-group open" data-group="' + idx + '">' +
      '<div class="agent-group-header d-flex flex-wrap justify-content-between align-items-center gap-2 p-1" data-toggle-group="' + idx + '">' +
        '<div>' +
          '<span class="group-chevron">&#9654;</span>' +
          '<strong>' + (g.agentName || 'Unknown agent') + '</strong>' +
          ' <span class="small" style="color:var(--admin-muted);">(' + (g.agentCode || '—') + ' · ' + (g.agencyName || '—') + ')</span>' +
        '</div>' +
        '<div class="small" style="color:var(--admin-muted);">' +
          (g.agentEmail || '') + (g.agentCity ? ' · ' + g.agentCity : '') +
          ' · <strong>' + props.length + '</strong> listing(s)' +
        '</div>' +
      '</div>' +
      '<div class="agent-group-body mt-3">' +
        '<div class="table-responsive">' +
          '<table class="table table-dark table-hover align-middle mb-0" style="--bs-table-bg:transparent;">' +
            '<thead><tr><th>Code</th><th>Title</th><th>Type</th><th>Category</th><th>Price</th><th>City</th><th>Status</th><th>Views</th><th>Listed</th></tr></thead>' +
            '<tbody>' + rows + '</tbody>' +
          '</table>' +
        '</div>' +
      '</div>' +
    '</div>';
  }).join('');

  container.querySelectorAll('[data-toggle-group]').forEach(function (el) {
    el.addEventListener('click', function () {
      el.closest('.agent-group').classList.toggle('open');
    });
  });
}

async function loadProperties() {
  const q = (document.getElementById('propertySearch').value || '').trim();
  UI.showLoader();
  try {
    const r = await AdminPropertyAPI.listByAgent(statusFilter, q);
    renderGroups(r.data || []);
  } catch (e) {
    UI.toast(e.message, 'error');
  } finally {
    UI.hideLoader();
  }
}

document.getElementById('propertySearch').addEventListener('input', function () {
  clearTimeout(searchTimer);
  searchTimer = setTimeout(loadProperties, 350);
});

loadProperties();
</script>
<%@ include file="../includes/portal-scripts.jsp" %>
</body>
</html>
