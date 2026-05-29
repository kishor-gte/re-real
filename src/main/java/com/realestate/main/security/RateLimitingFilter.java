package com.realestate.main.security;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

public class RateLimitingFilter extends OncePerRequestFilter {

    private static final int LIMIT = 300; // requests
    private static final long WINDOW_MS = 60_000L; // 1 minute

    private final Map<String, Window> windows = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String ip = request.getRemoteAddr();
        Window w = windows.computeIfAbsent(ip, k -> new Window());
        synchronized (w) {
            long now = Instant.now().toEpochMilli();
            if (now - w.start > WINDOW_MS) {
                w.start = now;
                w.count.set(0);
            }
            int cur = w.count.incrementAndGet();
            if (cur > LIMIT) {
                response.setStatus(429);
                response.getWriter().write("Too many requests");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private static class Window {
        long start = Instant.now().toEpochMilli();
        AtomicInteger count = new AtomicInteger(0);
    }
}
