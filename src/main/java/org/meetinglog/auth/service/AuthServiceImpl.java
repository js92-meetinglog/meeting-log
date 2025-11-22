package org.meetinglog.auth.service;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.meetinglog.auth.dto.AuthRequestDTO.LoginRequest;
import org.meetinglog.auth.dto.AuthResponseDTO.TokenResponse;
import org.meetinglog.auth.jwt.JwtTokenProvider;
import org.meetinglog.jpa.entity.UserAuthInfo;
import org.meetinglog.jpa.entity.UserMst;
import org.meetinglog.jpa.repository.UserAuthInfoRepository;
import org.meetinglog.jpa.repository.UserMstRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import static org.meetinglog.common.enums.ErrorMessage.*;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService{
  private final UserMstRepository userMstRepository;
  private final UserAuthInfoRepository userAuthInfoRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtTokenProvider jwtTokenProvider;

  @Override
  public TokenResponse authenticateNormalUser(LoginRequest request) {

    UserMst user = userMstRepository.findByUserIdAndUseYn(request.userId(), "Y")
        .orElseThrow(() -> new IllegalArgumentException(LOGIN_FAILED.getMessage()));

    UserAuthInfo authInfo = userAuthInfoRepository.findByUnqIdAndLoginTypeAndUseYn(user.getUnqId(), "NORMAL", "Y")
        .orElseThrow(() -> new IllegalArgumentException(AUTHENTICATION_FAILED.getMessage()));

    if (!passwordEncoder.matches(request.password(), authInfo.getPassword())) {
      throw new IllegalArgumentException(LOGIN_FAILED.getMessage());
    }

    String userUnqId = user.getUnqId();
    String accessToken = jwtTokenProvider.generateAccessToken(userUnqId);
    String refreshToken = jwtTokenProvider.generateRefreshToken(userUnqId);

    LocalDateTime accessTokenExpIn = jwtTokenProvider.getAccessTokenExpiryDate();
    LocalDateTime refreshTokenExpIn = jwtTokenProvider.getRefreshTokenExpiryDate();

    authInfo.setAccessToken(accessToken);
    authInfo.setExpIn(accessTokenExpIn);
    authInfo.setRefreshToken(refreshToken);
    authInfo.setRefreshTokenExpIn(refreshTokenExpIn);
    userAuthInfoRepository.save(authInfo);

    return new TokenResponse(
        "Bearer",
        accessToken,
        accessTokenExpIn,
        refreshToken
    );
  }
}
