package com.realestate.main.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.realestate.main.security.CustomUserDetailsService;
import com.realestate.main.security.JwtAuthenticationEntryPoint;
import com.realestate.main.security.JwtAuthenticationFilter;
import com.realestate.main.security.RateLimitingFilter;
import org.springframework.beans.factory.annotation.Value;
import com.realestate.main.security.SecurityHeadersFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	private final JwtAuthenticationFilter jwtAuthenticationFilter;
	private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
	private final CustomUserDetailsService userDetailsService;

	public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
			JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
			CustomUserDetailsService userDetailsService) {
		this.jwtAuthenticationFilter = jwtAuthenticationFilter;
		this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
		this.userDetailsService = userDetailsService;
	}

	@Value("${APP_ENFORCE_HTTPS:false}")
	private boolean enforceHttps;

	private final RateLimitingFilter rateLimitingFilter = new RateLimitingFilter();
	private final SecurityHeadersFilter securityHeadersFilter = new SecurityHeadersFilter();

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
			http.csrf(csrf -> csrf
					.ignoringRequestMatchers("/api/**")
					.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.userDetailsService(userDetailsService)
				.exceptionHandling(ex -> ex.authenticationEntryPoint(jwtAuthenticationEntryPoint))
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/", "/user/login", "/user/register", "/user/otp-verification",
								"/user/forgot-password", "/user/reset-password").permitAll()
						.requestMatchers("/admin/login", "/admin/register", "/admin/otp-verification",
								"/admin/forgot-password", "/admin/reset-password").permitAll()
						.requestMatchers("/agent/login", "/agent/register", "/agent/otp-verification",
								"/agent/forgot-password", "/agent/reset-password", "/agent/pending-approval")
								.permitAll()
						.requestMatchers("/pg-owner/login", "/pg-owner/register", "/pg-owner/otp-verification",
								"/pg-owner/forgot-password", "/pg-owner/reset-password", "/pg-owner/pending-approval")
								.permitAll()
						.requestMatchers("/api/auth/**", "/api/admin/auth/**", "/api/agent/auth/**",
								"/api/pg-owner/auth/**")
								.permitAll()
						.requestMatchers("/api/user/check-email", "/api/user/check-mobile").permitAll()
						.requestMatchers("/api/public/properties", "/api/public/properties/**").permitAll()
						.requestMatchers("/api/public/pgs", "/api/public/pgs/**").permitAll()
						.requestMatchers("/user/properties", "/user/properties/**").permitAll()
						.requestMatchers("/user/pgs", "/user/pgs/*/book").permitAll()
						.requestMatchers("/user/pgs/*/booking", "/user/pg-booking/**").authenticated()
						.requestMatchers("/css/**", "/js/**", "/uploads/**", "/ws/**", "/error").permitAll()
						.requestMatchers("/user/booking/**", "/user/bookings", "/user/bookings/**")
								.authenticated()
						.requestMatchers("/user/dashboard", "/user/enquiries", "/user/logout", "/api/user/**")
								.authenticated()
						.anyRequest().permitAll())
					.addFilterBefore(securityHeadersFilter, UsernamePasswordAuthenticationFilter.class)
					.addFilterBefore(rateLimitingFilter, UsernamePasswordAuthenticationFilter.class)
					.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

		if (enforceHttps) {
			http.requiresChannel(channel -> channel.anyRequest().requiresSecure());
		}

		return http.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(12);
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
		return configuration.getAuthenticationManager();
	}
}
