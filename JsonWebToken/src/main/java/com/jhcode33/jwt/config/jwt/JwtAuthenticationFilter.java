package com.jhcode33.jwt.config.jwt;

import java.io.IOException;
import java.util.Date;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jhcode33.jwt.config.auth.PrincipalDetails;
import com.jhcode33.jwt.model.User;

import lombok.RequiredArgsConstructor;

//스프링 시큐리티에서 UsernamePasswordAuthenticationFilter가 있음
// /login 요청해서 username, password을 post로 전송하면 동작함.
// 기본 /login 요청에 해당하는 것을 동작하지 않도록 했기 때문에, 필터를 다시 SecurityConfig에서 설정을 해야함.
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
	
	private final AuthenticationManager authenticationManager;
	
	// /login 요청을 하면 로그인 시도를 위해서 실행되는 메소드이다.
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		System.out.println("JwtAuthentication : 로그인 시도중");
		
		try {
			// 1. username과 password를 받아서
			// 2. 정상인지 로그인 시도를 해보는 것, authenticationManager로 로그인 시도를 하면 PrincipalDetailsService가 호출됨
			//-> loadUserByUsername()메소드 실행 
			// 3. PrinciaplDetails를 세션에 담고(권한 관리를 위해서 담는다)
			// 4. JWT 토큰을 만들어줌.
			
//			BufferedReader br = request.getReader();
//			String input = null;
//			while((input = br.readLine()) != null) {
//				System.out.println(input);
//			}

//			System.out.println(request.getInputStream().toString());
			
			
			
			// JSON 데이터 Parsing, key:value 형식으로 해줌.
			ObjectMapper om = new ObjectMapper();
			User user = om.readValue(request.getInputStream(), User.class);
			
			UsernamePasswordAuthenticationToken authenticationToken = 
					new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());
			
			//PrincipalDetailsService의 loadUserByUsername() 메소드가 실행됨.
			//Authentication에는 PricipalDetailsService를 통해 생성된 PricipalDetailsService(userEntity)가 담김
			Authentication authentication = authenticationManager.authenticate(authenticationToken);
			
			//== PrincipalDetails가 Authenication 안에 있다는 것은, authenticationManager가 authenticationToken을 사용해서
			//==> PrincipalDetailsService의 loadUserByUsername() 메소드를 호출하고, PrincipalDetails 객체를 생성해서
			//==> Security Session 영역에 저장하는 했다는 말임. 아래 코드를 저장한 것을 확인하는 코드
			PrincipalDetails principalDetails = (PrincipalDetails)authentication.getPrincipal();
			System.out.println("로그인 완료: " + principalDetails.getUser().getUsername());
			
			//Authentication 객체는 Security Session 영역에 저장됨. => 로그인이 되었다는 뜻
			return authentication;
			
		} catch (Exception e) {
			e.printStackTrace();
			
		}
		return null;
	}

	//attemptAuthentication 실행 후 인증이 정상적으로 되었으면 successfulAuthentication 메소드가 실행됨.
	// JWT 토큰을 만들어서 request 요청한 사용자에게 JWT 토큰을 response 해주는 역할
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {
		System.out.println("successfulAuthentication 실행됨: 인증완료 및 토큰 발행");
		
		PrincipalDetails principalDetails = (PrincipalDetails)authResult.getPrincipal();
		
		// jwt 생성, Hash 암호 방식
		String jwtToken = JWT.create()
				//header
				.withSubject("cos토큰")
				.withExpiresAt(new Date(System.currentTimeMillis()+(1000L*60L*10))) //토큰 만료 시간
				
				//payload 부분
				.withClaim("id",principalDetails.getUser().getId())
				.withClaim("username",principalDetails.getUser().getUsername())
				
				//서명
				.sign(Algorithm.HMAC256("cos")); //server만의 secret key값
		
		response.addHeader("Authorization", "Bearer "+jwtToken); //header에 담겨서 client에게 넘어감.
	}
	
	
	
	

}
