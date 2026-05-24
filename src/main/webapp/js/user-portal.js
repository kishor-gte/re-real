/**
 * User portal sidebar toggle + shared helpers.
 */
(function (global) {
  'use strict';

  function initSidebar() {
    const sidebar = document.querySelector('.user-sidebar');
    if (sidebar && sidebar.getAttribute('data-portal-mobile-bound') === '1') return;
    const toggle = document.getElementById('userSidebarToggle');
    const backdrop = document.getElementById('userSidebarBackdrop');
    if (!toggle || !sidebar) return;

    function close() {
      sidebar.classList.remove('open');
      backdrop?.classList.remove('open');
    }

    function open() {
      sidebar.classList.add('open');
      backdrop?.classList.add('open');
    }

    toggle.addEventListener('click', function () {
      if (sidebar.classList.contains('open')) close();
      else open();
    });
    backdrop?.addEventListener('click', close);
    sidebar.querySelectorAll('a').forEach(function (a) {
      a.addEventListener('click', function () {
        if (window.innerWidth <= 992) close();
      });
    });
  }

  global.UserPortal = { initSidebar: initSidebar };
})(typeof window !== 'undefined' ? window : this);
