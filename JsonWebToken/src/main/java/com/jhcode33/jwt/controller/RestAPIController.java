package com.jhcode33.jwt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.jhcode33.jwt.config.auth.PrincipalDetails;
import com.jhcode33.jwt.model.User;
import com.jhcode33.jwt.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class RestAPIController {
	
	@Autowired
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	private final UserRepository userReposiroty;

	@GetMapping("home")
	public String home() {
		return "<h1>home</h1>";
	}
	
	@PostMapping("token")
	public String token() {
		return "<h1>token</h1>";
	}
	
	@PostMapping("join")
	public String join(@RequestBody User user) {
		user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		user.setRoles("ROLE_USER");
		userReposiroty.save(user);
		return "회원 가입 완료";
	}
	
	//user, manager, admin 접근 가능
	@GetMapping("/api/v1/user")
	public String user(Authentication authentication) {
		PrincipalDetails principal =(PrincipalDetails) authentication.getPrincipal();
		System.out.println("authentication: " + principal.getUsername());
		return "user";
	}
	
	//manager, admin 접근 가능
	@GetMapping("/api/v1/manager")
	public String manager() {
		return "manager";
	}
	
	//admin 접근 가능
	@GetMapping("/api/v1/admin")
	public String admin() {
		return "admin";
	}
}
