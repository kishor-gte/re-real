package com.realestate.main.security;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realestate.main.dto.response.ApiResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

	private final ObjectMapper objectMapper;

	public JwtAuthenticationEntryPoint(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException {

		if (request.getRequestURI().startsWith("/api/")) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.setContentType(MediaType.APPLICATION_JSON_VALUE);
			response.setCharacterEncoding(StandardCharsets.UTF_8.name());
			objectMapper.writeValue(response.getWriter(),
					ApiResponse.fail("Unauthorized. Please login."));
			return;
		}

		String redirect = request.getRequestURI();
		if (request.getQueryString() != null) {
			redirect += "?" + request.getQueryString();
		}
		response.sendRedirect("/user/login?redirect=" + java.net.URLEncoder.encode(redirect, StandardCharsets.UTF_8));
	}
}
