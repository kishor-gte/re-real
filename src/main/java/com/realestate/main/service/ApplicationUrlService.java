package com.realestate.main.service;

import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Builds absolute URLs from configuration, browser Origin, or the current HTTP request (dynamic).
 */
@Service
public class ApplicationUrlService {

	@Value("${app.public-base-url:}")
	private String publicBaseUrl;

	public String buildResetPasswordUrl(HttpServletRequest request, String rawToken, String clientOrigin) {
		return resolvePublicBaseUrl(request, clientOrigin) + "/user/reset-password?token=" + rawToken;
	}

	public String buildAdminResetPasswordUrl(HttpServletRequest request, String rawToken, String clientOrigin) {
		return resolvePublicBaseUrl(request, clientOrigin) + "/admin/reset-password?token=" + rawToken;
	}

	public String buildAgentResetPasswordUrl(HttpServletRequest request, String rawToken, String clientOrigin) {
		return resolvePublicBaseUrl(request, clientOrigin) + "/agent/reset-password?token=" + rawToken;
	}

	public String buildAgentLoginUrl(HttpServletRequest request) {
		return resolvePublicBaseUrl(request, null) + "/agent/login";
	}

	public String buildPgOwnerResetPasswordUrl(HttpServletRequest request, String rawToken, String clientOrigin) {
		return resolvePublicBaseUrl(request, clientOrigin) + "/pg-owner/reset-password?token=" + rawToken;
	}

	public String buildPgOwnerLoginUrl(HttpServletRequest request) {
		return resolvePublicBaseUrl(request, null) + "/pg-owner/login";
	}

	public String buildAdminLoginUrl(HttpServletRequest request) {
		return resolvePublicBaseUrl(request, null) + "/admin/login";
	}

	public String buildLoginUrl(HttpServletRequest request) {
		return resolvePublicBaseUrl(request, null) + "/user/login";
	}

	/** For async emails without an HTTP request (uses {@code app.public-base-url} only). */
	public String resolveConfiguredBaseUrlOrNull() {
		if (hasText(publicBaseUrl)) {
			return normalizeBase(publicBaseUrl);
		}
		return null;
	}

	public String resolvePublicBaseUrl(HttpServletRequest request, String clientOrigin) {
		if (hasText(publicBaseUrl)) {
			return normalizeBase(publicBaseUrl);
		}

		if (hasText(clientOrigin) && isTrustedClientOrigin(request, clientOrigin)) {
			String fromClient = baseFromUrl(clientOrigin, request.getContextPath());
			if (fromClient != null) {
				return fromClient;
			}
		}

		String fromOrigin = extractOriginOrReferer(request);
		if (fromOrigin != null) {
			return fromOrigin;
		}

		return buildFromServletRequest(request);
	}

	private boolean isTrustedClientOrigin(HttpServletRequest request, String clientOrigin) {
		String normalized = normalizeBase(clientOrigin.trim());
		String origin = request.getHeader("Origin");
		if (origin != null && !origin.isBlank()) {
			return normalizeBase(origin).equalsIgnoreCase(normalized);
		}
		String referer = request.getHeader("Referer");
		if (referer != null && !referer.isBlank()) {
			try {
				URI ref = new URI(referer.trim());
				String refBase = ref.getScheme() + "://" + ref.getAuthority();
				return normalizeBase(refBase).equalsIgnoreCase(normalized);
			} catch (URISyntaxException e) {
				return false;
			}
		}
		return isValidHttpUrl(clientOrigin);
	}

	private String extractOriginOrReferer(HttpServletRequest request) {
		String origin = request.getHeader("Origin");
		if (isValidHttpUrl(origin)) {
			return baseFromUrl(origin, request.getContextPath());
		}
		String referer = request.getHeader("Referer");
		if (referer != null && !referer.isBlank()) {
			return baseFromUrl(referer, request.getContextPath());
		}
		return null;
	}

	private String baseFromUrl(String url, String contextPath) {
		if (url == null || url.isBlank()) {
			return null;
		}
		try {
			URI uri = new URI(url.trim());
			if (uri.getScheme() == null || uri.getHost() == null) {
				return null;
			}
			int port = uri.getPort();
			String authority = formatAuthority(uri.getScheme(), uri.getHost(), port);
			String base = uri.getScheme() + "://" + authority;
			String ctx = contextPath;
			if (ctx != null && !ctx.isBlank() && !"/".equals(ctx)
					&& (uri.getPath() == null || !uri.getPath().startsWith(ctx))) {
				base += ctx.startsWith("/") ? ctx : "/" + ctx;
			}
			return normalizeBase(base);
		} catch (URISyntaxException e) {
			return null;
		}
	}

	private String buildFromServletRequest(HttpServletRequest request) {
		String scheme = firstNonBlank(request.getHeader("X-Forwarded-Proto"), request.getScheme(), "http");
		if (scheme.contains(",")) {
			scheme = scheme.split(",")[0].trim();
		}

		String hostHeader = firstNonBlank(request.getHeader("X-Forwarded-Host"), request.getHeader("Host"));
		String hostname;
		int port = -1;

		if (hostHeader != null && !hostHeader.isBlank()) {
			if (hostHeader.contains(",")) {
				hostHeader = hostHeader.split(",")[0].trim();
			}
			if (hostHeader.startsWith("[")) {
				int end = hostHeader.indexOf(']');
				hostname = hostHeader.substring(1, end);
				if (hostHeader.length() > end + 1 && hostHeader.charAt(end + 1) == ':') {
					port = parsePort(hostHeader.substring(end + 2), request.getServerPort());
				}
			} else if (hostHeader.contains(":")) {
				int colon = hostHeader.lastIndexOf(':');
				hostname = hostHeader.substring(0, colon);
				port = parsePort(hostHeader.substring(colon + 1), request.getServerPort());
			} else {
				hostname = hostHeader;
				port = request.getServerPort();
			}
		} else {
			hostname = request.getServerName();
			port = request.getServerPort();
		}

		String authority = formatAuthority(scheme, hostname, port);
		String contextPath = request.getContextPath();
		if (contextPath == null || contextPath.isBlank() || "/".equals(contextPath)) {
			return normalizeBase(scheme + "://" + authority);
		}
		return normalizeBase(scheme + "://" + authority + contextPath);
	}

	private static String formatAuthority(String scheme, String hostname, int port) {
		if (hostname == null || hostname.isBlank()) {
			return "localhost";
		}
		if (!shouldIncludePort(scheme, port)) {
			return hostname;
		}
		return hostname + ":" + port;
	}

	private static boolean shouldIncludePort(String scheme, int port) {
		if (port <= 0) {
			return false;
		}
		return ("http".equalsIgnoreCase(scheme) && port != 80)
				|| ("https".equalsIgnoreCase(scheme) && port != 443);
	}

	private static int parsePort(String portStr, int fallback) {
		try {
			return Integer.parseInt(portStr.trim());
		} catch (NumberFormatException e) {
			return fallback;
		}
	}

	private static boolean isValidHttpUrl(String url) {
		if (url == null || url.isBlank()) {
			return false;
		}
		try {
			URI uri = new URI(url.trim());
			String scheme = uri.getScheme();
			return ("http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme))
					&& uri.getHost() != null && !uri.getHost().isBlank();
		} catch (URISyntaxException e) {
			return false;
		}
	}

	private static String normalizeBase(String base) {
		String trimmed = base.trim();
		while (trimmed.endsWith("/")) {
			trimmed = trimmed.substring(0, trimmed.length() - 1);
		}
		return trimmed;
	}

	private static String firstNonBlank(String... values) {
		for (String v : values) {
			if (v != null && !v.isBlank()) {
				return v.trim();
			}
		}
		return null;
	}

	private static boolean hasText(String value) {
		return value != null && !value.isBlank();
	}
}
