package com.jhcode33.jwt.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.filter.CorsFilter;

import com.jhcode33.jwt.config.jwt.JwtAuthenticationFilter;
import com.jhcode33.jwt.config.jwt.JwtAuthorizationFilter;
import com.jhcode33.jwt.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig{
	
	@Autowired
	private final UserRepository userRepository;
	
	@Autowired
	private final CorsConfig corsConfig;
	
//	@Override
//	protected void configure(HttpSecurity http) throws Exception {
//		//http.addFilterBefore(new MyFilter3(), SecurityContextPersistenceFilter.class); //BasicAuthenticationFilter가 실행되기 전에 실행한다. 
//		
//		http.csrf().disable();
//		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) //세션을 사용하지 않겠다.
//			.and()
//				.addFilter(corsFilter) //@CrossOrigin(인증X), 시큐리티 필터에 등록(인증O)
//			
//				.formLogin().disable() //-> login 기본 form을 막았기 때문에 login페이지에서 동작하는 PrincipalDetailsService가 동작하기 위해선 필터가 필요함.
//				.httpBasic().disable() //Basic 방식이 아닌 Bearer 방식으로 사용하기 위해 Basic을 사용하지 않음, 설정
//				
//				.addFilter(new JwtAuthenticationFilter(authenticationManager())) //Authentication Manager을 반드시 전달해줘야함. WebSecurityConfigurerAdapter이 필드로 가지고 있음.
//				.addFilter(new JwtAuthorizationFilter(authenticationManager(), userRepository))  //권한, 인증이 필요할 때 거치는 필터
//				
//				.authorizeRequests()
//				.antMatchers("/api/v1/user/**")
//				.access("hasRole('ROLE_USER') or hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
//				.antMatchers("/api/v1/manager/**")
//				.access("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
//				
//				.antMatchers("/api/v1/amdin/**")
//				.access("hasRole('ROLE_ADMIN')") //이 부분이 권한 설정이 안 되는데 확인해봐야 할듯
//				.anyRequest().permitAll();
//	}
	
	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
		return http		
					.csrf(csrf -> csrf.disable())
				   	.sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				 	.formLogin(formLogin -> formLogin.disable())
				 	.httpBasic(httpBasic -> httpBasic.disable())
				 	
				 	//URL에 대한 권한 설정
				 	.authorizeRequests(authroize -> authroize.antMatchers("/api/v1/user/**")
				 											.access("hasRole('ROLE_USER') or hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
				 											.antMatchers("/api/v1/manager/**")
				 											.access("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
				 											.antMatchers("/api/v1/admin/**")
				 											.access("hasRole('ROLE_ADMIN')")
				 											.anyRequest().permitAll())
				 	.build();
				 	
	}
	
	public class MyCustomDsl extends AbstractHttpConfigurer<MyCustomDsl, HttpSecurity> {
		@Override
		public void configure(HttpSecurity http) throws Exception {
			AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
			http
					.addFilter(corsConfig.corsFilter())
					.addFilter(new JwtAuthenticationFilter(authenticationManager))
					.addFilter(new JwtAuthorizationFilter(authenticationManager, userRepository));
		}
	}
}
