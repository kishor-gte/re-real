<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="includes/init.jsp" %>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>EstateVault | Premium Real Estate &amp; Construction</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${ctx}/css/auth.css">
    <link rel="stylesheet" href="${ctx}/css/home.css">
</head>
<body class="auth-body auth-bg-orbs home-page">

<header class="auth-nav home-nav" id="homeNav">
    <a class="brand" href="/">EstateVault</a>
    <button type="button" class="home-nav-toggle" id="navToggle" aria-label="Menu">&#9776;</button>
    <ul class="home-nav-links" id="navLinks">
        <li><a href="#home">Home</a></li>
        <li><a href="#properties">Properties</a></li>
        <li><a href="#services">Services</a></li>
        <li><a href="#about">About</a></li>
        <li><a href="#contact">Contact</a></li>
        <% if (Boolean.TRUE.equals(request.getAttribute("loggedIn"))) { %>
            <li class="home-nav-auth-mobile"><a href="/user/dashboard">Dashboard</a></li>
            <li class="home-nav-auth-mobile"><a href="/user/logout">Logout</a></li>
        <% } else { %>
            <li class="home-nav-auth-mobile"><a href="${ctx}/user/login">User Login</a></li>
            <li class="home-nav-auth-mobile"><a href="${ctx}/agent/login">Agent Login</a></li>
            <li class="home-nav-auth-mobile"><a href="${ctx}/pg-owner/login">PG Owner Login</a></li>
            <li class="home-nav-auth-mobile"><a href="${ctx}/user/register">Register</a></li>
        <% } %>
    </ul>
    <div class="d-flex gap-2 home-nav-actions-desktop">
        <% if (Boolean.TRUE.equals(request.getAttribute("loggedIn"))) { %>
            <a href="/user/dashboard" class="btn-primary-gold">Dashboard</a>
            <a href="/user/logout" class="btn-outline">Logout</a>
        <% } else { %>
            <div class="home-login-dropdown" id="homeLoginDropdown">
                <button type="button" class="btn-outline home-login-drop-btn" id="homeLoginDropBtn" aria-haspopup="true" aria-expanded="false">
                    Login <span class="home-login-caret" aria-hidden="true">&#9662;</span>
                </button>
                <div class="home-login-drop-menu" role="menu" aria-label="Login options">
                    <a href="${ctx}/user/login" role="menuitem">User Login</a>
                    <a href="${ctx}/agent/login" role="menuitem">Agent Login</a>
                    <a href="${ctx}/pg-owner/login" role="menuitem">PG Owner Login</a>
                </div>
            </div>
            <a href="${ctx}/user/register" class="btn-primary-gold">Register</a>
        <% } %>
    </div>
</header>

<!-- Hero -->
<section class="home-hero" id="home">
    <div class="hero-content">
        <span class="hero-badge">Trusted Since 2010</span>
        <h1 class="hero-title">Find Your <span>Dream Property</span> With Confidence</h1>
        <p class="hero-sub">
            EstateVault connects buyers, sellers, and investors with premium homes, commercial spaces,
            and construction expertise — all in one classic, trusted platform.
        </p>
        <div class="hero-search">
            <select aria-label="Property type">
                <option>Buy</option>
                <option>Rent</option>
                <option>Commercial</option>
            </select>
            <input type="text" placeholder="City, area or landmark" aria-label="Location">
            <button type="button" class="btn-primary-gold" id="heroSearchBtn">Search</button>
        </div>
        <div class="hero-actions">
            <% if (Boolean.TRUE.equals(request.getAttribute("loggedIn"))) { %>
                <a href="/user/dashboard" class="btn-primary-gold">Go to Dashboard</a>
            <% } else { %>
                <a href="/user/register" class="btn-primary-gold">Start Free Registration</a>
                <a href="/user/login" class="btn-outline">Sign In</a>
            <% } %>
            <a href="#properties" class="btn-outline">Browse Listings</a>
        </div>
    </div>
    <div class="hero-visual">
        <div class="hero-image-wrap">
            <img src="https://images.unsplash.com/photo-1600596542815-ffad4c1539a9?w=800&q=80"
                 alt="Luxury modern home exterior" loading="eager">
        </div>
        <div class="hero-float-card card-1">
            <strong>2,400+</strong>
            <span>Properties Listed</span>
        </div>
        <div class="hero-float-card card-2">
            <strong>98%</strong>
            <span>Client Satisfaction</span>
        </div>
    </div>
</section>

<!-- Stats -->
<div class="home-stats reveal">
    <div class="stats-grid">
        <div class="stat-item">
            <div class="stat-num" data-count="2400" data-suffix="+">0</div>
            <div class="stat-label">Active Listings</div>
        </div>
        <div class="stat-item">
            <div class="stat-num" data-count="850" data-suffix="+">0</div>
            <div class="stat-label">Happy Families</div>
        </div>
        <div class="stat-item">
            <div class="stat-num" data-count="120" data-suffix="+">0</div>
            <div class="stat-label">Expert Agents</div>
        </div>
        <div class="stat-item">
            <div class="stat-num" data-count="15" data-suffix="+">0</div>
            <div class="stat-label">Years Experience</div>
        </div>
    </div>
</div>

<!-- Partners marquee -->
<div class="partners-marquee reveal">
    <div class="marquee-track">
        <span>Premium Homes</span><span>Commercial Realty</span><span>Verified Listings</span>
        <span>Construction Partners</span><span>Investment Advisory</span><span>Property Management</span>
        <span>Premium Homes</span><span>Commercial Realty</span><span>Verified Listings</span>
        <span>Construction Partners</span><span>Investment Advisory</span><span>Property Management</span>
    </div>
</div>

<!-- Featured Properties -->
<section class="home-section" id="properties">
    <div class="section-head reveal">
        <span class="section-tag">Featured Listings</span>
        <h2>Handpicked Properties For You</h2>
        <p>Explore curated residences and commercial spaces with transparent pricing and verified ownership.</p>
    </div>
    <div class="properties-grid">
        <article class="property-card reveal reveal-delay-1">
            <div class="property-img">
                <img src="https://images.unsplash.com/photo-1613490493576-7fde63acd811?w=600&q=80" alt="Villa with pool" loading="lazy">
                <span class="property-tag sale">For Sale</span>
            </div>
            <div class="property-body">
                <h3>Azure Villa Estate</h3>
                <p class="property-loc">&#128205; Whitefield, Bengaluru</p>
                <div class="property-meta">
                    <span class="property-price">&#8377; 2.45 Cr</span>
                    <span class="property-specs">4 BHK &bull; 3,200 sq.ft</span>
                </div>
            </div>
        </article>
        <article class="property-card reveal reveal-delay-2">
            <div class="property-img">
                <img src="https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?w=600&q=80" alt="Modern apartment" loading="lazy">
                <span class="property-tag rent">For Rent</span>
            </div>
            <div class="property-body">
                <h3>Skyline Residency</h3>
                <p class="property-loc">&#128205; Bandra West, Mumbai</p>
                <div class="property-meta">
                    <span class="property-price">&#8377; 85,000/mo</span>
                    <span class="property-specs">3 BHK &bull; 1,450 sq.ft</span>
                </div>
            </div>
        </article>
        <article class="property-card reveal reveal-delay-3">
            <div class="property-img">
                <img src="https://images.unsplash.com/photo-1600585154340-be6161a56a0c?w=600&q=80" alt="Contemporary house" loading="lazy">
                <span class="property-tag sale">For Sale</span>
            </div>
            <div class="property-body">
                <h3>Heritage Grove Homes</h3>
                <p class="property-loc">&#128205; Jubilee Hills, Hyderabad</p>
                <div class="property-meta">
                    <span class="property-price">&#8377; 1.89 Cr</span>
                    <span class="property-specs">3 BHK &bull; 2,100 sq.ft</span>
                </div>
            </div>
        </article>
        <article class="property-card reveal">
            <div class="property-img">
                <img src="https://images.unsplash.com/photo-1560448204-e02f11c3d0e2?w=600&q=80" alt="Penthouse interior view" loading="lazy">
                <span class="property-tag rent">For Rent</span>
            </div>
            <div class="property-body">
                <h3>Marina View Penthouse</h3>
                <p class="property-loc">&#128205; Anna Nagar, Chennai</p>
                <div class="property-meta">
                    <span class="property-price">&#8377; 1.2 L/mo</span>
                    <span class="property-specs">5 BHK &bull; 4,800 sq.ft</span>
                </div>
            </div>
        </article>
        <article class="property-card reveal reveal-delay-1">
            <div class="property-img">
                <img src="https://images.unsplash.com/photo-1600607687939-ce8a6c25118c?w=600&q=80" alt="Family home" loading="lazy">
                <span class="property-tag sale">For Sale</span>
            </div>
            <div class="property-body">
                <h3>Maple Creek Residence</h3>
                <p class="property-loc">&#128205; Koregaon Park, Pune</p>
                <div class="property-meta">
                    <span class="property-price">&#8377; 1.15 Cr</span>
                    <span class="property-specs">2 BHK &bull; 1,180 sq.ft</span>
                </div>
            </div>
        </article>
        <article class="property-card reveal reveal-delay-2">
            <div class="property-img">
                <img src="https://images.unsplash.com/photo-1560448204-e02f11c3d0e2?w=600&q=80" alt="Luxury interior" loading="lazy">
                <span class="property-tag sale">For Sale</span>
            </div>
            <div class="property-body">
                <h3>Golden Gate Commercial</h3>
                <p class="property-loc">&#128205; SG Highway, Ahmedabad</p>
                <div class="property-meta">
                    <span class="property-price">&#8377; 3.2 Cr</span>
                    <span class="property-specs">Office &bull; 5,500 sq.ft</span>
                </div>
            </div>
        </article>
    </div>
</section>

<!-- Services -->
<section class="home-section" id="services">
    <div class="section-head reveal">
        <span class="section-tag">Our Services</span>
        <h2>Complete Real Estate Solutions</h2>
        <p>From discovery to keys in hand — EstateVault supports every step of your property journey.</p>
    </div>
    <div class="services-grid">
        <div class="service-card reveal">
            <div class="service-icon">&#127968;</div>
            <h3>Buy Property</h3>
            <p>Verified listings, legal checks, and guided tours for residential and commercial purchases.</p>
        </div>
        <div class="service-card reveal reveal-delay-1">
            <div class="service-icon">&#128273;</div>
            <h3>Rent &amp; Lease</h3>
            <p>Flexible rental options with background-verified tenants and transparent agreements.</p>
        </div>
        <div class="service-card reveal reveal-delay-2">
            <div class="service-icon">&#128200;</div>
            <h3>Sell &amp; List</h3>
            <p>Professional photography, market pricing, and promotion to qualified buyers nationwide.</p>
        </div>
        <div class="service-card reveal reveal-delay-3">
            <div class="service-icon">&#127959;</div>
            <h3>Construction</h3>
            <p>Partner with trusted builders for new developments, renovations, and turnkey projects.</p>
        </div>
    </div>
</section>

<!-- Why Us -->
<section class="home-section" id="about">
    <div class="why-grid">
        <div class="reveal">
            <span class="section-tag">Why EstateVault</span>
            <h2 style="font-size:clamp(1.75rem,4vw,2.35rem);margin:0 0 1rem;">A Classic Approach To Modern Real Estate</h2>
            <p style="color:var(--muted);line-height:1.7;margin:0;">
                We blend timeless service values with technology — so every client enjoys clarity,
                integrity, and personal attention at every stage.
            </p>
            <ul class="why-list">
                <li>
                    <span class="why-icon">&#10003;</span>
                    <div>
                        <h4>Verified Properties Only</h4>
                        <p>Every listing passes document and ownership verification before going live.</p>
                    </div>
                </li>
                <li>
                    <span class="why-icon">&#9733;</span>
                    <div>
                        <h4>Dedicated Relationship Managers</h4>
                        <p>One point of contact from inquiry through registration and beyond.</p>
                    </div>
                </li>
                <li>
                    <span class="why-icon">&#9878;</span>
                    <div>
                        <h4>Legal &amp; Financial Guidance</h4>
                        <p>Assistance with loans, registrations, and compliance for peace of mind.</p>
                    </div>
                </li>
            </ul>
        </div>
        <div class="why-image reveal reveal-delay-2">
            <img src="https://images.unsplash.com/photo-1600585154526-990dced4db0d?w=700&q=80"
                 alt="Elegant living room interior" loading="lazy">
        </div>
    </div>
</section>

<!-- How it works -->
<section class="home-section">
    <div class="section-head reveal">
        <span class="section-tag">How It Works</span>
        <h2>Your Journey In Four Simple Steps</h2>
        <p>Register once, explore listings, connect with experts, and close deals with confidence.</p>
    </div>
    <div class="steps-row">
        <div class="step-card reveal">
            <div class="step-num">1</div>
            <h3>Create Account</h3>
            <p>Quick registration with email verification and secure login.</p>
        </div>
        <div class="step-card reveal reveal-delay-1">
            <div class="step-num">2</div>
            <h3>Browse &amp; Shortlist</h3>
            <p>Filter by location, budget, and property type to find ideal matches.</p>
        </div>
        <div class="step-card reveal reveal-delay-2">
            <div class="step-num">3</div>
            <h3>Schedule Visits</h3>
            <p>Book guided tours and receive detailed property reports.</p>
        </div>
        <div class="step-card reveal reveal-delay-3">
            <div class="step-num">4</div>
            <h3>Close Securely</h3>
            <p>Complete paperwork and handover with our expert support team.</p>
        </div>
    </div>
</section>

<!-- Testimonials -->
<section class="home-section">
    <div class="section-head reveal">
        <span class="section-tag">Testimonials</span>
        <h2>What Our Clients Say</h2>
        <p>Thousands of families and investors trust EstateVault for their most important decisions.</p>
    </div>
    <div class="testimonials-wrap">
        <div class="testimonial-card reveal">
            <div class="testimonial-stars">&#9733;&#9733;&#9733;&#9733;&#9733;</div>
            <blockquote>&ldquo;EstateVault made buying our first home stress-free. Every document was clear, and our agent was exceptional.&rdquo;</blockquote>
            <div class="testimonial-author">
                <img src="https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=100&q=80" alt="Client">
                <div>
                    <strong>Priya Sharma</strong>
                    <span>Home Buyer, Bengaluru</span>
                </div>
            </div>
        </div>
        <div class="testimonial-card reveal reveal-delay-1">
            <div class="testimonial-stars">&#9733;&#9733;&#9733;&#9733;&#9733;</div>
            <blockquote>&ldquo;Listed my commercial space and received serious inquiries within a week. Professional and classic service throughout.&rdquo;</blockquote>
            <div class="testimonial-author">
                <img src="https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=100&q=80" alt="Client">
                <div>
                    <strong>Rajesh Mehta</strong>
                    <span>Property Seller, Mumbai</span>
                </div>
            </div>
        </div>
        <div class="testimonial-card reveal reveal-delay-2">
            <div class="testimonial-stars">&#9733;&#9733;&#9733;&#9733;&#9733;</div>
            <blockquote>&ldquo;The platform feels premium yet easy to use. Registration was smooth, and support responded within hours.&rdquo;</blockquote>
            <div class="testimonial-author">
                <img src="https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=100&q=80" alt="Client">
                <div>
                    <strong>Ananya Reddy</strong>
                    <span>Investor, Hyderabad</span>
                </div>
            </div>
        </div>
    </div>
</section>

<!-- CTA -->
<div class="home-cta reveal" id="contact">
    <div class="cta-box">
        <h2>Ready To Find Your Next Property?</h2>
        <p>Join EstateVault today — register free, explore listings, and connect with verified agents across India.</p>
        <div class="d-flex flex-wrap gap-3 justify-content-center">
            <% if (Boolean.TRUE.equals(request.getAttribute("loggedIn"))) { %>
                <a href="/user/dashboard" class="btn-primary-gold">Open Dashboard</a>
            <% } else { %>
                <a href="/user/register" class="btn-primary-gold">Create Free Account</a>
                <a href="/user/login" class="btn-outline">Sign In</a>
            <% } %>
        </div>
    </div>
</div>

<!-- Footer -->
<footer class="home-footer">
    <div class="footer-inner">
        <div class="footer-brand">
            <a class="brand" href="/">EstateVault</a>
            <p>Premium real estate and construction platform — helping you buy, sell, rent, and build with trust.</p>
        </div>
        <div class="footer-col">
            <h4>Explore</h4>
            <ul>
                <li><a href="#properties">Properties</a></li>
                <li><a href="#services">Services</a></li>
                <li><a href="#about">About Us</a></li>
                <li><a href="/user/register">Register</a></li>
            </ul>
        </div>
        <div class="footer-col">
            <h4>Account</h4>
            <ul>
                <li><a href="${ctx}/user/login">User Login</a></li>
                <li><a href="${ctx}/agent/login">Agent Login</a></li>
                <li><a href="${ctx}/user/register">Sign Up</a></li>
                <li><a href="${ctx}/user/dashboard">Dashboard</a></li>
                <li><a href="${ctx}/admin/login">Admin Portal</a></li>
            </ul>
        </div>
        <div class="footer-col">
            <h4>Contact</h4>
            <ul>
                <li><a href="mailto:support@estatevault.com">support@estatevault.com</a></li>
                <li><a href="tel:+911800123456">+91 1800-123-456</a></li>
                <li><a href="#contact">Help Center</a></li>
            </ul>
        </div>
    </div>
    <div class="footer-bottom">
        <span>&copy; 2026 EstateVault. All rights reserved.</span>
        <span>Crafted with care for modern living</span>
    </div>
</footer>

<script src="${ctx}/js/home.js"></script>
<%@ include file="includes/portal-scripts.jsp" %>
</body>
</html>
