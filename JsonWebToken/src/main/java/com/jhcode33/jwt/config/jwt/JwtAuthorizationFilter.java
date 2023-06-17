package com.jhcode33.jwt.config.jwt;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.jhcode33.jwt.config.auth.PrincipalDetails;
import com.jhcode33.jwt.model.User;
import com.jhcode33.jwt.repository.UserRepository;

// Security가 filter을 가지고 있는데 그 필터 중에 BasicAuthenticationFilter이 있다.
// 권한이나 인증이 필요한 특정 주소를 요청했을 때 위 필터를 무조건 거치게 되어있다.
// 권한, 인증이 필요한 주소가 아니라면 이 필터를 사용하지 않음.
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {
	
	private UserRepository userRepository;
	
	public JwtAuthorizationFilter(AuthenticationManager authenticationManager, UserRepository userRepository) {
		super(authenticationManager);
		this.userRepository = userRepository;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		System.out.println("권한, 인증이 있는 주소 요청됨");
		
		String jwtHeader = request.getHeader("Authorization");
		System.out.println("jwtHeader: " + jwtHeader);
		
		// jwt 토큰의 Header이 있는지 확인.
		if(jwtHeader == null || !jwtHeader.startsWith("Bearer")) {
			chain.doFilter(request, response);
			return;
		}
		
		//== JWT 토큰을 검증해서 정상적인 사용자인지 확인
		//Bearer을 빼고 token만 가져옴.
		String jwtToken = request.getHeader("Authorization").replace("Bearer ", "");
		
		String username = JWT.require(Algorithm.HMAC256("cos")).build().verify(jwtToken)
							 .getClaim("username").asString();
		
		if(username != null) {
			User userEntity = userRepository.findByUsername(username);
			
			PrincipalDetails principalDetails = new PrincipalDetails(userEntity);
			
			//기존 토큰의 서명과 강제로 만든 토큰의 서명을 비교해서 토큰이 유효한지 확인하는 코드
			//DB에서 username으로 정보를 조회해서 가져왔기 때문에 인증된 사용자이고, 서명이 일치하는지 확인하는 것임.
			//서명을 확인하기 위해서 토큰을 강제로 만듬.
			// Jwt 토큰 서명을 통해서 서명이 정상이면 Authentication 객체를 만들어준다.
			Authentication authentication = 
					new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities());
			
			//Security Session 공간에 접근하여 Authentication 객체를 저장함.
			SecurityContextHolder.getContext().setAuthentication(authentication);
		}
		chain.doFilter(request, response);
	}
}
