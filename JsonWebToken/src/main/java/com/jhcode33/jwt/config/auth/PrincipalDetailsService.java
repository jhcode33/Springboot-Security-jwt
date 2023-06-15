package com.jhcode33.jwt.config.auth;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.jhcode33.jwt.model.User;
import com.jhcode33.jwt.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PrincipalDetailsService implements UserDetailsService {
	
	private final UserRepository userRepository;
	
	// http://localhost:8989/login 요청이 오면 실행이 됨. -> 근데 SecurityConfig에서 기본 LoginForm을 막았기 때문에 필터가 필요함.
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		System.out.println("PrincipalDetailsService 실행됨");
		User userEntity = userRepository.findByUsername(username);
		System.out.println("userEntity:"+userEntity);
		return new PrincipalDetails(userEntity);
		
		
	}
}
