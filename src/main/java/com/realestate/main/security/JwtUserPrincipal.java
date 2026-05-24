package com.realestate.main.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.realestate.main.entity.User;

public class JwtUserPrincipal implements UserDetails {

	private final Long userId;
	private final String email;
	private final String fullName;
	private final String role;
	private final String sessionId;
	private final String passwordHash;

	public JwtUserPrincipal(Long userId, String email, String fullName, String role, String sessionId,
			String passwordHash) {
		this.userId = userId;
		this.email = email;
		this.fullName = fullName;
		this.role = role;
		this.sessionId = sessionId;
		this.passwordHash = passwordHash;
	}

	public static JwtUserPrincipal from(User user, String sessionId) {
		return new JwtUserPrincipal(user.getId(), user.getEmail(), user.getFullName(), user.getRole().name(),
				sessionId, user.getPassword());
	}

	public Long getUserId() {
		return userId;
	}

	public String getFullName() {
		return fullName;
	}

	public String getEmail() {
		return email;
	}

	public String getRole() {
		return role;
	}

	public String getSessionId() {
		return sessionId;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority("ROLE_" + role));
	}

	@Override
	public String getPassword() {
		return passwordHash;
	}

	@Override
	public String getUsername() {
		return email;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}
