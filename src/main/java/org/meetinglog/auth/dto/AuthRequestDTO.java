package org.meetinglog.auth.dto;

import jakarta.validation.constraints.NotBlank;

public class AuthRequestDTO {

  public record LoginRequest(
      @NotBlank(message = "아이디를 입력해주세요.")
      String userId, // 사용자가 입력한 로그인 ID

      @NotBlank(message = "비밀번호를 입력해주세요.")
      String password // 사용자가 입력한 비밀번호 (평문)
  ) {}


}
