package com.realestate.main.config;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.realestate.main.util.PgOwnerSessionConstants;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class PgOwnerAuthInterceptor implements HandlerInterceptor {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		HttpSession session = request.getSession(false);
		if (session != null && session.getAttribute(PgOwnerSessionConstants.PG_OWNER_ID) != null) {
			return true;
		}
		String ctx = request.getContextPath();
		response.sendRedirect(ctx + "/pg-owner/login?redirect=" + request.getRequestURI());
		return false;
	}
}
