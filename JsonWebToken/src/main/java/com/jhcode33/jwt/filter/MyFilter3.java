package com.jhcode33.jwt.filter;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MyFilter3 implements Filter {

	//필터들은 시큐리티가 동작하기 전에 작동해야 함. 이건 SecurityConfig에서 설정한다.
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		
		// id pw 정상적으로 들어와서 로그인 완료 되면 토큰을 만들어주고 그걸 응답으로 준다.
		// 요청할 때마다 header에 Authorization에 value 값으로 토큰을 가지고 온다.
		// 그때 토큰이 넘어오면 이 토큰이 내가 만든 토큰이 맞는지만 검증하면 됨(RSA, HS256)
		if(req.getMethod().equals("POST")) {
			System.out.println("Post 요청됨");
			String headerAuth = req.getHeader("Authorization");
			System.out.println(headerAuth);
			
			// 토큰 : cos -> 토큰이 cos로 날라올 때만 작동하도록.
			if(headerAuth.equals("cos")) { //이게 서명을 만들 때 사용하는 server의 고유 키 값이 되는 것임.
				System.out.println("필터3");
				chain.doFilter(req, res);
				
			} else {
				PrintWriter out = res.getWriter();
				out.println("인증 안됨");
			}
			
		}
	}
}
