package org.meetinglog.auth.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

public class AuthResponseDTO {

  public record TokenResponse(
      String tokenType, // 토큰 타입 (예: Bearer)
      String accessToken, // 발급된 Access Token
      LocalDateTime accessTokenExpiresIn, // Access Token 만료 시간
      String refreshToken // 발급된 Refresh Token
  ) {}

}
