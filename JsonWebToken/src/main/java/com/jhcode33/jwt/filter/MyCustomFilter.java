package com.jhcode33.jwt.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.AbstractSecurityBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.stereotype.Component;

import com.jhcode33.jwt.config.CorsConfig;
import com.jhcode33.jwt.config.jwt.JwtAuthenticationFilter;
import com.jhcode33.jwt.config.jwt.JwtAuthorizationFilter;
import com.jhcode33.jwt.repository.UserRepository;

@Component
@Configuration
public class MyCustomFilter extends AbstractHttpConfigurer<MyCustomFilter, HttpSecurity> {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CorsConfig corsConfig;
	
	@Override
	public void configure(HttpSecurity http) throws Exception {
		AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
	
		http.addFilter(corsConfig.corsFilter())
			.addFilter(new JwtAuthenticationFilter(authenticationManager))
			.addFilter(new JwtAuthorizationFilter(authenticationManager, userRepository));
			
	}
}
