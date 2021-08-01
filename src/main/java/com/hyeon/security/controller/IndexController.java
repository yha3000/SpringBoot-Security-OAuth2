package com.hyeon.security.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hyeon.security.config.auth.PrincipalDetails;
import com.hyeon.security.model.User;
import com.hyeon.security.repository.UserRepository;

@Controller // View를 리턴하겠다
public class IndexController {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
//	@GetMapping("/test/login")
//	public @ResponseBody String testLogin(Authentication authentication, @AuthenticationPrincipal PrincipalDetails userDetails) { // DI (의존성 주입)
//		System.out.println("/test/login =======================");
//		PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
//		System.out.println("authentication : " + principalDetails.getUser());
//		
//		System.out.println("userDetails : " + userDetails.getUser());
//		return "세션 정보 확인하기";
//	}
	
//	@GetMapping("/test/oauth/login")
//	public @ResponseBody String testOAuthLogin(Authentication authentication, @AuthenticationPrincipal OAuth2User oauth) { // DI (의존성 주입)
//		System.out.println("/test/oauth/login =======================");
//		OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
//		System.out.println("authentication : " + oAuth2User.getAttributes());
//		
//		System.out.println("oauth2User : " + oauth.getAttributes());
//		return "OAuth 세션 정보 확인";
//	}
	
	// localhost:8080/
	// localhost:8080
	@GetMapping({"", "/"})
	public String index() {
		// mustache : 기본 폴더 - src/main/resources/
		// viewResolver : templates (prefix), .mustache (suffix)
		return "index";
	}
	
	// OAuth 로그인을 해도 PrincipalDetails
	// 일반 로그인을 해도 PrincipalDetails
	@GetMapping("/user")
	public @ResponseBody String user(@AuthenticationPrincipal PrincipalDetails principalDetails) { // DI (의존성 주입)
		System.out.println("principalDetails : " + principalDetails.getUser());
		return "user";
	}
	
	@GetMapping("/admin")
	public @ResponseBody String admin() {
		return "admin";
	}
	
	@GetMapping("/manager")
	public @ResponseBody String manager() {
		return "manager";
	}
	
	// 스프링시큐리티 해당 메소드를 낚아 챔
	// SecurityConfig 파일 생성 후 작동 안함
	@GetMapping("/loginForm")
	public String loginForm() {
		return "loginForm";
	}
	
	@GetMapping("/joinForm")
	public String joinForm() {
		return "joinForm";
	}
		
	@PostMapping("/joinProc")
	public String joinProc(User user) {
		String rawPassword = user.getPassword();
		String encPassword = bCryptPasswordEncoder.encode(rawPassword);
		user.setPassword(encPassword);
		user.setRole("ROLE_USER");
		userRepository.save(user); // 비밀번호가 암호화가 안됨 (시큐리티 로그인이 안됨)
		return "redirect:/loginForm";
	}
	
	@Secured("ROLE_ADMIN")
	@GetMapping("/info")
	public @ResponseBody String info() {
		return "개인정보";
	}
	
	@PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')") // 메소드가 실행되기 전에 실행 됨
// 	@PostAuthorize // 메소드가 실행된 뒤에 실행 됨
	@GetMapping("/data")
	public @ResponseBody String data() {
		return "Data 정보";
	}
	
}