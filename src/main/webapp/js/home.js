(function () {
  const nav = document.getElementById('homeNav');
  const navToggle = document.getElementById('navToggle');
  const navLinks = document.getElementById('navLinks');

  if (nav) {
    window.addEventListener('scroll', () => {
      nav.classList.toggle('scrolled', window.scrollY > 40);
    });
  }

  if (navToggle && navLinks) {
    navToggle.addEventListener('click', () => {
      navLinks.classList.toggle('open');
    });
    navLinks.querySelectorAll('a').forEach(a => {
      a.addEventListener('click', () => navLinks.classList.remove('open'));
    });
  }

  const loginDropdown = document.getElementById('homeLoginDropdown');
  const loginDropBtn = document.getElementById('homeLoginDropBtn');
  if (loginDropdown && loginDropBtn) {
    loginDropBtn.addEventListener('click', function (e) {
      e.stopPropagation();
      const open = loginDropdown.classList.toggle('open');
      loginDropBtn.setAttribute('aria-expanded', open ? 'true' : 'false');
    });
    document.addEventListener('click', function () {
      loginDropdown.classList.remove('open');
      loginDropBtn.setAttribute('aria-expanded', 'false');
    });
    loginDropdown.querySelector('.home-login-drop-menu')?.addEventListener('click', function (e) {
      e.stopPropagation();
    });
  }

  const revealEls = document.querySelectorAll('.reveal');
  if (revealEls.length && 'IntersectionObserver' in window) {
    const observer = new IntersectionObserver(
      entries => {
        entries.forEach(entry => {
          if (entry.isIntersecting) {
            entry.target.classList.add('visible');
            observer.unobserve(entry.target);
          }
        });
      },
      { threshold: 0.12, rootMargin: '0px 0px -40px 0px' }
    );
    revealEls.forEach(el => observer.observe(el));
  } else {
    revealEls.forEach(el => el.classList.add('visible'));
  }

  const counters = document.querySelectorAll('[data-count]');
  const runCounter = el => {
    const target = parseInt(el.getAttribute('data-count'), 10);
    const suffix = el.getAttribute('data-suffix') || '';
    const duration = 1800;
    const start = performance.now();
    const step = now => {
      const p = Math.min((now - start) / duration, 1);
      const ease = 1 - Math.pow(1 - p, 3);
      el.textContent = Math.floor(target * ease).toLocaleString() + suffix;
      if (p < 1) requestAnimationFrame(step);
    };
    requestAnimationFrame(step);
  };

  if (counters.length && 'IntersectionObserver' in window) {
    const counterObs = new IntersectionObserver(
      entries => {
        entries.forEach(entry => {
          if (entry.isIntersecting) {
            runCounter(entry.target);
            counterObs.unobserve(entry.target);
          }
        });
      },
      { threshold: 0.5 }
    );
    counters.forEach(c => counterObs.observe(c));
  }

  document.querySelectorAll('a[href^="#"]').forEach(anchor => {
    anchor.addEventListener('click', e => {
      const id = anchor.getAttribute('href');
      if (id.length < 2) return;
      const target = document.querySelector(id);
      if (target) {
        e.preventDefault();
        target.scrollIntoView({ behavior: 'smooth', block: 'start' });
      }
    });
  });

  const heroSearchBtn = document.getElementById('heroSearchBtn');
  if (heroSearchBtn) {
    heroSearchBtn.addEventListener('click', () => {
      const props = document.getElementById('properties');
      if (props) props.scrollIntoView({ behavior: 'smooth' });
    });
  }
})();
