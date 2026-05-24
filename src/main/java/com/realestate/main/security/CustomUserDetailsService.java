package com.realestate.main.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.realestate.main.repository.UserRepository;
import com.realestate.main.service.UserLookupService;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;

	public CustomUserDetailsService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return userRepository.findByEmail(UserLookupService.normalizeEmail(username))
				.map(user -> JwtUserPrincipal.from(user, ""))
				.orElseThrow(() -> new UsernameNotFoundException("User not found"));
	}
}
