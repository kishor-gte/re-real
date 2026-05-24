package com.realestate.main.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.realestate.main.security.JwtUserPrincipal;

@Controller
public class HomeController {

	@GetMapping("/")
	public String index(Model model) {
		model.addAttribute("loggedIn", isLoggedIn());
		return "index";
	}

	private boolean isLoggedIn() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof JwtUserPrincipal;
	}
}
