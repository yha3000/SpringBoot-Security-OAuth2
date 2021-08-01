package com.hyeon.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.hyeon.security.config.auth.PrincipalOAuth2UserService;

@Configuration
@EnableWebSecurity // 활성화 해줌 : 스프링 시큐리티 필터가 기본 스프링 필터 체인에 등록이 된다
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true) // secured 어노테이션 활성화, preAuthorize 어노테이션 활성화
public class SecurityConfig  extends WebSecurityConfigurerAdapter {

	@Autowired
	private PrincipalOAuth2UserService principalOAuth2UserService;
	
	@Bean // 해당 메서드의 리턴되는 오브젝트를 IoC로 등록해준다.
	public BCryptPasswordEncoder encodePwd() {
		return new BCryptPasswordEncoder();
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable();
		http.authorizeRequests() 
				.antMatchers("/user/**").authenticated() // 인증이 필요함
				.antMatchers("/manager/**").access("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')") // 권한이 필요함
				.antMatchers("/admin/**").access("hasRole('ROLE_ADMIN')") // 권한이 필요함
				.anyRequest().permitAll() // 나머지는 권한이 없어도 접속 가능
				.and()
				.formLogin()
				.loginPage("/loginForm")
//				.usernameParameter("username2") // 파라미터 변경시에 넣어줘야 함
				.loginProcessingUrl("/login") // /login 주소가 호출이 되면 시큐리티가 낚아채서 대신 로그인을 진행해 줌
				.defaultSuccessUrl("/")
				.and()
				.oauth2Login()
				.loginPage("/loginForm") 
				.userInfoEndpoint()
				.userService(principalOAuth2UserService)
		;
				// 구글 로그인이 완료된 뒤의 후처리가 필요함
				// Tip. 코드X ( 엑세스토큰+사용자프로필정보 O)
				// 1. 코드받기 
				// 2. 액세스토큰 
				// 3. 사용자프로필 정보를 가져옴 
				// 4-1. 그 정보를 토대로 회원가입을 자동으로 진행
				// 4-2. 이메일, 전화번호, 이름, 아이디를 받는데 쇼핑몰인 경우 추가적인 집주소가 필요하므로 회원가입을 수동으로 진행
	}
	
}