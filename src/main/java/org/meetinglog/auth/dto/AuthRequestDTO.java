package org.meetinglog.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AuthRequestDTO {

  public record LoginRequest(
      @NotBlank(message = "아이디를 입력해주세요.")
      String userId, // 사용자가 입력한 로그인 ID

      @NotBlank(message = "비밀번호를 입력해주세요.")
      String password // 사용자가 입력한 비밀번호 (평문)
  ) {}

  public record SignupRequest(
      @NotBlank(message = "아이디는 필수 입력 값입니다.")
      @Size(min = 4, max = 20, message = "아이디는 4~20자 사이여야 합니다.")
      String userId,

      @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
      @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
      String password,

      @NotBlank(message = "이름은 필수 입력 값입니다.")
      String userNm,

      String telNo // 선택 사항
  ) {}
}
