package org.meetinglog.auth.controller;

import static org.meetinglog.common.enums.ErrorMessage.AUTHENTICATION_FAILED;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.meetinglog.auth.dto.AuthRequestDTO;
import org.meetinglog.auth.dto.AuthRequestDTO.LoginRequest;
import org.meetinglog.auth.dto.AuthResponseDTO.TokenResponse;
import org.meetinglog.auth.service.AuthService;
import org.meetinglog.common.dto.ApiResponse;
import org.meetinglog.common.enums.ErrorMessage;
import org.meetinglog.common.exception.BusinessException;
import org.meetinglog.jpa.entity.UserMst;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("v1/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  @PostMapping("/login")
  public ApiResponse<TokenResponse> login(@RequestBody LoginRequest loginRequest) {

    try {
      TokenResponse response = authService.authenticateNormalUser(loginRequest);

      return ApiResponse.success(response);
    } catch (IllegalArgumentException e) {

      return ApiResponse.error(401, e.getMessage());
    }
  }

  @PostMapping("/signup")
  public ApiResponse<UserMst> signup(@RequestBody @Valid AuthRequestDTO.SignupRequest signupRequest) {

    try {
      UserMst newUser = authService.signupNormalUser(signupRequest);
      return ApiResponse.success(newUser);

    } catch (IllegalArgumentException e) {
      return ApiResponse.error(401, e.getMessage());
    }
  }
//
//  @GetMapping("/user")
//  public ResponseEntity<Map<String, Object>> getCurrentUser(Authentication authentication) {
//    try {
//      log.debug("Getting current user info. Authentication: {}",
//          authentication != null ? authentication.getName() : "null");
//
//      if (authentication == null || !authentication.isAuthenticated()) {
//        log.warn("User not authenticated");
//        return ResponseEntity.ok().body(Map.of("authenticated", false));
//      }
//
//      OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
//      log.debug("OAuth2User attributes: {}", oauth2User.getAttributes());
//
//      Map<String, Object> userInfo = new HashMap<>();
//      userInfo.put("authenticated", true);
//      userInfo.put("id", oauth2User.getAttribute("id"));
//      userInfo.put("userId", oauth2User.getAttribute("userId"));
//      userInfo.put("role", oauth2User.getAttribute("role"));
//
//      Map<String, Object> kakaoAccount = (Map<String, Object>) oauth2User.getAttribute(
//          "kakao_account");
//      if (kakaoAccount != null) {
//        userInfo.put("email", kakaoAccount.get("email"));
//        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
//        if (profile != null) {
//          userInfo.put("nickname", profile.get("nickname"));
//          userInfo.put("profileImageUrl", profile.get("profile_image_url"));
//        }
//      }
//
//      log.info("Successfully retrieved user info for user ID: {}",
//          Optional.ofNullable(oauth2User.getAttribute("userId")));
//      return ResponseEntity.ok(userInfo);
//
//    } catch (Exception e) {
//      log.error("Error getting current user info", e);
//      return ResponseEntity.ok()
//          .body(Map.of("authenticated", false, "error", "사용자 정보를 가져오는 중 오류가 발생했습니다."));
//    }
//  }

  @GetMapping("/login/kakao")
  public ResponseEntity<Map<String, String>> kakaoLogin() {
    try {
      log.info("Kakao login requested");
      return ResponseEntity.ok().body(Map.of(
          "message", "카카오 로그인을 시작합니다.",
          "loginUrl", "/oauth2/authorization/kakao"
      ));
    } catch (Exception e) {
      log.error("Error initiating Kakao login", e);
      return ResponseEntity.ok().body(Map.of("error", "카카오 로그인 시작 중 오류가 발생했습니다."));
    }
  }
}
