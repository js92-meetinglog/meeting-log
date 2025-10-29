package org.meetinglog.auth.service;

import lombok.extern.slf4j.Slf4j;
import org.meetinglog.auth.dto.KakaoUserInfo;
import org.meetinglog.jpa.entity.User;
import org.meetinglog.jpa.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User createOrUpdateUser(KakaoUserInfo kakaoUserInfo) {
        try {
            log.debug("Creating or updating user with KakaoUserInfo: {}", kakaoUserInfo.getId());

            String kakaoId = kakaoUserInfo.getId().toString();
            String nickname = kakaoUserInfo.getKakao_account() != null &&
                             kakaoUserInfo.getKakao_account().getProfile() != null ?
                             kakaoUserInfo.getKakao_account().getProfile().getNickname() :
                             kakaoUserInfo.getProperties().getNickname();
            String email = kakaoUserInfo.getKakao_account() != null ?
                          kakaoUserInfo.getKakao_account().getEmail() : null;
            String profileImageUrl = kakaoUserInfo.getKakao_account() != null &&
                                    kakaoUserInfo.getKakao_account().getProfile() != null ?
                                    kakaoUserInfo.getKakao_account().getProfile().getProfile_image_url() :
                                    kakaoUserInfo.getProperties().getProfile_image();

            log.debug("Extracted user data - kakaoId: {}, nickname: {}, email: {}", kakaoId, nickname, email);

            User user = userRepository.findByKakaoId(kakaoId)
                    .map(existingUser -> {
                        log.info("Updating existing user: {}", existingUser.getId());
                        return existingUser.updateProfile(nickname, email, profileImageUrl);
                    })
                    .orElseGet(() -> {
                        log.info("Creating new user with kakaoId: {}", kakaoId);
                        return userRepository.save(
                                User.builder()
                                        .kakaoId(kakaoId)
                                        .nickname(nickname)
                                        .email(email)
                                        .profileImageUrl(profileImageUrl)
                                        .role(User.Role.USER)
                                        .build()
                        );
                    });

            log.info("User successfully created/updated: id={}, kakaoId={}", user.getId(), user.getKakaoId());
            return user;

        } catch (Exception e) {
            log.error("Error creating or updating user with kakaoId: {}", kakaoUserInfo.getId(), e);
            throw e;
        }
    }

    public User getUserByKakaoId(String kakaoId) {
        try {
            log.debug("Searching for user with kakaoId: {}", kakaoId);
            User user = userRepository.findByKakaoId(kakaoId).orElse(null);
            if (user != null) {
                log.debug("Found user: id={}, nickname={}", user.getId(), user.getNickname());
            } else {
                log.debug("No user found with kakaoId: {}", kakaoId);
            }
            return user;
        } catch (Exception e) {
            log.error("Error finding user by kakaoId: {}", kakaoId, e);
            return null;
        }
    }
}
