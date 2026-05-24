package com.realestate.main.config;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.realestate.main.util.AgentSessionConstants;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class AgentAuthInterceptor implements HandlerInterceptor {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		HttpSession session = request.getSession(false);
		if (session != null && session.getAttribute(AgentSessionConstants.AGENT_ID) != null) {
			return true;
		}
		String ctx = request.getContextPath();
		response.sendRedirect(ctx + "/agent/login?redirect=" + request.getRequestURI());
		return false;
	}
}
