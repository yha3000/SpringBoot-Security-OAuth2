package com.hyeon.security.config.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.hyeon.security.model.User;
import com.hyeon.security.repository.UserRepository;

@Service
public class PrincipalOAuth2UserService extends DefaultOAuth2UserService {
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	private UserRepository userRepository;

	// 구글로 부터 받은 userRequest 데이터에 대한 후처리되는 함수
	// 함수 종료 시 @AthenticationPrincipal 어노테이션이 만들어진다
	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		// RegistrationId로 어떤 OAuth로 로그인 했는지 확인 가능
//		System.out.println("getClientRegistration : " +userRequest.getClientRegistration());
//		System.out.println("getAccessToken : " + userRequest.getAccessToken().getTokenValue());

		// 구글 로그인 버튼 클릭 -> 구글로그인창 -> 로그인을 완료 -> code를 리턴(OAuth-Client라이브러리) -> AccessToken 요청
		// userRequest 정보 -> loadUser 함수 호출 -> 구글로부터 회원프로필을 받아준다.
//		System.out.println("getAttributes : " + super.loadUser(userRequest).getAttributes());
		
		OAuth2User oauth2User = super.loadUser(userRequest);
		
		String provider = userRequest.getClientRegistration().getClientId();
		String provicerId = oauth2User.getAttribute("sub");
		String username = provider + "_" + provicerId;
		String password = bCryptPasswordEncoder.encode("겟인데어");
		String email = oauth2User.getAttribute("email");
		String role = "ROLE_USER";
		
		User userEntity = userRepository.findByUsername(username);
		
		if(userEntity == null) {
			System.out.println("구글 로그인이 최초입니다");
			userEntity = User.builder()
										.username(username)
										.password(password)
										.email(email)
										.role(role)
										.provider(provider)
										.providerId(provicerId)
										.build();
			userRepository.save(userEntity);
		} else {
			System.out.println("구글 로그인을 이미 한 적이 있습니다. 당신은 자동회원가입이 되어 있습니다.");
		}
		
		return new PrincipalDetails(userEntity, oauth2User.getAttributes());
	}
	
}