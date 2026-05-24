/**
 * EstateVault — mobile navigation for all portals (agent, admin, PG owner, user).
 */
(function (global) {
  'use strict';

  var BP = 992;
  var NAV_BP = 768;

  var PORTALS = [
    {
      id: 'agent',
      bodyClass: 'agent-body',
      sidebar: '.agent-sidebar',
      toggleId: 'agentSidebarToggle',
      backdropId: 'agentSidebarBackdrop',
      brandHref: '/agent/dashboard',
      brandText: 'Agent Portal'
    },
    {
      id: 'admin',
      bodyClass: 'admin-body',
      sidebar: '.admin-sidebar',
      toggleId: 'adminSidebarToggle',
      backdropId: 'adminSidebarBackdrop',
      brandHref: '/admin/dashboard',
      brandText: 'Admin'
    },
    {
      id: 'pgo',
      bodyClass: 'pgo-body',
      sidebar: '.pgo-sidebar',
      toggleId: 'pgoSidebarToggle',
      backdropId: 'pgoSidebarBackdrop',
      brandHref: '/pg-owner/dashboard',
      brandText: 'PG Owner'
    },
    {
      id: 'user',
      bodyClass: 'user-portal-page',
      sidebar: '.user-sidebar',
      toggleId: 'userSidebarToggle',
      backdropId: 'userSidebarBackdrop',
      brandHref: '/user/dashboard',
      brandText: 'EstateVault',
      useExistingTopnav: true
    }
  ];

  function ctx() {
    var c = global.__APP_CTX__;
    if (c != null && c !== '') return c;
    var base = document.querySelector('base');
    if (base && base.getAttribute('href')) {
      c = base.getAttribute('href').replace(/\/$/, '');
    } else {
      var link = document.querySelector('link[href*="/css/"]');
      if (link) {
        var href = link.getAttribute('href') || '';
        var i = href.indexOf('/css/');
        c = i > 0 ? href.substring(0, i) : '';
      } else {
        c = '';
      }
    }
    global.__APP_CTX__ = c;
    return c;
  }

  function isMobile() {
    return global.innerWidth <= BP;
  }

  function lockBody(lock) {
    document.body.classList.toggle('portal-menu-open', !!lock);
  }

  function injectPortalTopbar(cfg) {
    if (document.getElementById(cfg.toggleId)) return;
    var c = ctx();
    var header = document.createElement('header');
    header.className = 'portal-mobile-topbar portal-mobile-topbar--' + cfg.id;
    header.innerHTML =
      '<button type="button" class="portal-mobile-toggle" id="' + cfg.toggleId + '" aria-label="Open menu" aria-expanded="false">&#9776;</button>' +
      '<a class="portal-mobile-brand" href="' + c + cfg.brandHref + '">' + esc(cfg.brandText) + '</a>';
    var backdrop = document.createElement('div');
    backdrop.className = 'portal-sidebar-backdrop portal-sidebar-backdrop--' + cfg.id;
    backdrop.id = cfg.backdropId;
    backdrop.setAttribute('aria-hidden', 'true');
    document.body.insertBefore(backdrop, document.body.firstChild);
    document.body.insertBefore(header, document.body.firstChild);
  }

  function injectUserTopnav(cfg) {
    if (document.getElementById(cfg.toggleId)) return;
    if (!document.querySelector(cfg.sidebar)) return;
    var c = ctx();
    var header = document.createElement('header');
    header.className = 'user-portal-topnav';
    header.innerHTML =
      '<button type="button" class="user-sidebar-toggle" id="' + cfg.toggleId + '" aria-label="Open menu" aria-expanded="false">&#9776;</button>' +
      '<a class="brand" href="' + c + cfg.brandHref + '">EstateVault</a>' +
      '<a href="' + c + '/" class="btn-outline portal-topnav-home">Home</a>';
    var backdrop = document.createElement('div');
    backdrop.className = 'user-sidebar-backdrop';
    backdrop.id = cfg.backdropId;
    backdrop.setAttribute('aria-hidden', 'true');
    document.body.insertBefore(backdrop, document.body.firstChild);
    document.body.insertBefore(header, document.body.firstChild);
    if (!document.body.classList.contains('user-portal-page')) {
      document.body.classList.add('user-portal-page');
    }
  }

  function esc(s) {
    return String(s || '').replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
  }

  function bindSidebar(cfg) {
    var sidebar = document.querySelector(cfg.sidebar);
    if (!sidebar) return;

    var toggle = document.getElementById(cfg.toggleId);
    var backdrop = document.getElementById(cfg.backdropId);
    if (!toggle) {
      if (cfg.useExistingTopnav) injectUserTopnav(cfg);
      toggle = document.getElementById(cfg.toggleId);
    }
    if (!toggle) return;

    if (sidebar.getAttribute('data-portal-mobile-bound') === '1') return;
    sidebar.setAttribute('data-portal-mobile-bound', '1');

    function close() {
      sidebar.classList.remove('open');
      if (backdrop) backdrop.classList.remove('open');
      toggle.setAttribute('aria-expanded', 'false');
      lockBody(false);
    }

    function open() {
      sidebar.classList.add('open');
      if (backdrop) backdrop.classList.add('open');
      toggle.setAttribute('aria-expanded', 'true');
      if (isMobile()) lockBody(true);
    }

    toggle.addEventListener('click', function (e) {
      e.preventDefault();
      e.stopPropagation();
      if (sidebar.classList.contains('open')) close();
      else open();
    });

    if (backdrop) {
      backdrop.addEventListener('click', close);
    }

    sidebar.querySelectorAll('a[href]').forEach(function (a) {
      a.addEventListener('click', function () {
        if (isMobile()) close();
      });
    });

    global.addEventListener('resize', function () {
      if (!isMobile()) close();
    });

    document.addEventListener('keydown', function (e) {
      if (e.key === 'Escape') close();
    });
  }

  function initDashboardNav() {
    document.querySelectorAll('.auth-nav.dashboard-nav').forEach(function (nav) {
      if (nav.getAttribute('data-portal-nav-bound') === '1') return;
      var actions = nav.querySelector('.dashboard-nav-actions');
      if (!actions) return;

      nav.setAttribute('data-portal-nav-bound', '1');

      var btn = nav.querySelector('.dashboard-nav-toggle');
      if (!btn) {
        btn = document.createElement('button');
        btn.type = 'button';
        btn.className = 'dashboard-nav-toggle';
        btn.setAttribute('aria-label', 'Toggle menu');
        btn.setAttribute('aria-expanded', 'false');
        btn.innerHTML = '&#9776;';
        var brand = nav.querySelector('.brand');
        if (brand && brand.nextSibling) {
          nav.insertBefore(btn, brand.nextSibling);
        } else if (brand) {
          brand.after(btn);
        } else {
          nav.insertBefore(btn, nav.firstChild);
        }
      }

      btn.addEventListener('click', function (e) {
        e.preventDefault();
        e.stopPropagation();
        var open = nav.classList.toggle('nav-open');
        btn.setAttribute('aria-expanded', open ? 'true' : 'false');
      });

      actions.querySelectorAll('a[href]').forEach(function (a) {
        a.addEventListener('click', function () {
          if (global.innerWidth <= NAV_BP) {
            nav.classList.remove('nav-open');
            btn.setAttribute('aria-expanded', 'false');
          }
        });
      });
    });
  }

  function initPortals() {
    var body = document.body;
    if (!body) return;

    PORTALS.forEach(function (cfg) {
      var sidebar = document.querySelector(cfg.sidebar);
      if (!sidebar) return;

      var match = !cfg.bodyClass || body.classList.contains(cfg.bodyClass);
      if (!match && cfg.id === 'user') {
        match = true;
      }
      if (!match) return;

      if (cfg.useExistingTopnav) {
        injectUserTopnav(cfg);
      } else if (body.classList.contains(cfg.bodyClass)) {
        injectPortalTopbar(cfg);
      }

      bindSidebar(cfg);
    });
  }

  function init() {
    if (global.__portalMobileInit) return;
    global.__portalMobileInit = true;
    initPortals();
    initDashboardNav();
  }

  function boot() {
    init();
  }

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', boot);
  } else {
    boot();
  }
  global.addEventListener('load', boot);

  global.PortalMobile = { init: init, ctx: ctx };
})(typeof window !== 'undefined' ? window : this);
