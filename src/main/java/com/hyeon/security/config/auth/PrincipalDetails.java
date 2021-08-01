package com.hyeon.security.config.auth;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.hyeon.security.model.User;

import lombok.Data;

// 시큐리티가 /login 주소 요청이 오면 낚아채서 로그인을 진행시킨다.
// 로그인 진행이 완료가 되면 시큐리티 session을 만들어준다. (Security ContextHolder)
// 오브젝트 타입은 Authentication 타입의 객체여야 함.
// Authentication 안에 User 정보가 있어야 함.
// User 오브젝트의 타입은 UserDetails 타입의 객체여야 함.

// Security Session => Authentication => UserDetails

@Data
public class PrincipalDetails implements UserDetails, OAuth2User {

	private User user; // 콤포지션
	private Map<String, Object> attributes;
	
	// 일반 로그인
	public PrincipalDetails(User user) {
		this.user = user;
	}
	
	// OAuth 로그인
	public PrincipalDetails(User user, Map<String, Object> attributes) {
		this.user = user;
		this.attributes = attributes;
	}
	
	@Override // 해당 User의 권한을 리턴하는 곳
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Collection<GrantedAuthority> collection = new ArrayList<>();
		collection.add(new GrantedAuthority() {
			@Override
			public String getAuthority() {
				return user.getRole();
			}
		});
		return collection;
	}

	@Override
	public String getPassword() {
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		return user.getUsername();
	}

	@Override // 계정 만료 여부
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override // 계정이 잠겼는지 여부
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override // 계정의 비번을 유효기간의 여부
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override // 계정 활성화 여부
	public boolean isEnabled() {
		return true;
	}

	@Override
	public Map<String, Object> getAttributes() {
		return attributes;
	}

	@Override
	public String getName() {
//		return attributes.get("sub");
		return null;
	}
	
}