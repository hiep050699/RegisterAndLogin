package com.example.demo.security.oauth;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

	// ghi đè phương thức loadUser () sẽ được Spring OAuth2 gọi khi xác thực thành
	// công và nó trả về một đối tượng CustomOAuth2User mới .

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		OAuth2User user =  super.loadUser(userRequest);
		//System.out.println("loadUser....");
		return new CustomOAuth2User(user);
	}

}
