package com.realestate.main.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

	private final AdminAuthInterceptor adminAuthInterceptor;
	private final AgentAuthInterceptor agentAuthInterceptor;
	private final PgOwnerAuthInterceptor pgOwnerAuthInterceptor;

	public WebMvcConfig(AdminAuthInterceptor adminAuthInterceptor, AgentAuthInterceptor agentAuthInterceptor,
			PgOwnerAuthInterceptor pgOwnerAuthInterceptor) {
		this.adminAuthInterceptor = adminAuthInterceptor;
		this.agentAuthInterceptor = agentAuthInterceptor;
		this.pgOwnerAuthInterceptor = pgOwnerAuthInterceptor;
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(adminAuthInterceptor)
				.addPathPatterns("/admin/dashboard", "/admin/agents", "/admin/agents/**", "/admin/pg-owners",
						"/admin/pg-owners/**", "/admin/users", "/admin/users/**", "/admin/properties",
						"/admin/properties/**", "/admin/subscriptions", "/admin/subscriptions/**")
				.excludePathPatterns("/api/admin/auth/**");
		registry.addInterceptor(agentAuthInterceptor)
				.addPathPatterns("/agent/dashboard", "/agent/properties", "/agent/properties/**", "/agent/enquiries",
						"/agent/enquiries/**", "/agent/bookings", "/agent/bookings/**", "/agent/subscription",
						"/agent/subscription/**", "/agent/analytics", "/agent/earnings", "/agent/referrals",
						"/agent/messages", "/agent/notifications")
				.excludePathPatterns("/api/agent/auth/**");
		registry.addInterceptor(pgOwnerAuthInterceptor)
				.addPathPatterns("/pg-owner/dashboard", "/pg-owner/bookings", "/pg-owner/properties",
						"/pg-owner/properties/**")
				.excludePathPatterns("/api/pg-owner/auth/**");
	}

	@Bean
	public ViewResolver jspViewResolver() {
		InternalResourceViewResolver resolver = new InternalResourceViewResolver();
		resolver.setPrefix("/views/");
		resolver.setSuffix(".jsp");
		resolver.setOrder(1);
		return resolver;
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		// classpath (Maven resources) + servlet context /css, /js (src/main/webapp)
		registry.addResourceHandler("/css/**")
				.addResourceLocations("/css/", "classpath:/static/css/");
		registry.addResourceHandler("/js/**")
				.addResourceLocations("/js/", "classpath:/static/js/");
		registry.addResourceHandler("/uploads/**").addResourceLocations("file:uploads/");
	}
}
