package org.meetinglog.auth.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.meetinglog.auth.dto.KakaoUserInfo;
import org.meetinglog.jpa.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Slf4j
@Service
public class KakaoOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private UserService userService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("Starting Kakao OAuth2 user loading process");

        try {
            OAuth2User oAuth2User = super.loadUser(userRequest);
            log.debug("Raw OAuth2User attributes: {}", oAuth2User.getAttributes());

            KakaoUserInfo kakaoUserInfo = objectMapper.convertValue(oAuth2User.getAttributes(), KakaoUserInfo.class);
            log.debug("Converted KakaoUserInfo: id={}, email={}",
                kakaoUserInfo.getId(),
                kakaoUserInfo.getKakao_account() != null ? kakaoUserInfo.getKakao_account().getEmail() : "null");

            User user = userService.createOrUpdateUser(kakaoUserInfo);
            log.info("User created/updated successfully: id={}, kakaoId={}", user.getId(), user.getKakaoId());

            Map<String, Object> attributes = oAuth2User.getAttributes();
            attributes.put("userId", user.getId());
            attributes.put("role", user.getRole().name());

            log.debug("Final OAuth2User attributes: {}", attributes);

            return new DefaultOAuth2User(
                    Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())),
                    attributes,
                    "id"
            );
        } catch (Exception e) {
            log.error("Error processing Kakao user info", e);
            throw new OAuth2AuthenticationException("카카오 사용자 정보 처리 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}
