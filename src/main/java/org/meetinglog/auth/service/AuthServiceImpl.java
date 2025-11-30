package org.meetinglog.auth.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.StoredProcedureQuery;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.meetinglog.auth.dto.AuthRequestDTO.LoginRequest;
import org.meetinglog.auth.dto.AuthRequestDTO.SignupRequest;
import org.meetinglog.auth.dto.AuthResponseDTO.TokenResponse;
import org.meetinglog.auth.jwt.JwtTokenProvider;
import org.meetinglog.common.exception.BusinessException;
import org.meetinglog.jpa.entity.UserAuthInfo;
import org.meetinglog.jpa.entity.UserMst;
import org.meetinglog.jpa.repository.UserAuthInfoRepository;
import org.meetinglog.jpa.repository.UserMstRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import static org.meetinglog.common.enums.ErrorMessage.*;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService{
  private final UserMstRepository userMstRepository;
  private final UserAuthInfoRepository userAuthInfoRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtTokenProvider jwtTokenProvider;
  private final EntityManager entityManager;

  @Override
  @Transactional
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

  @Override
  @Transactional
  public UserMst signupNormalUser(SignupRequest request) {

    if (userMstRepository.findByUserId(request.userId()).isPresent()) {
      throw new BusinessException(DUPLICATE_ID.getMessage());
    }

    String unqId = generateNewUnqIdWithProcedure();
    String encodedPassword = passwordEncoder.encode(request.password());

    UserMst userMst = UserMst.builder()
        .unqId(unqId)
        .userId(request.userId())
        .userNm(request.userNm())
        .telNo(request.telNo())
        .build();

    UserAuthInfo userAuthInfo = UserAuthInfo.builder()
        .unqId(unqId)
        .loginType("NORMAL")
        .password(encodedPassword)
        .build();

    userMstRepository.save(userMst);
    userAuthInfoRepository.save(userAuthInfo);
    return userMst;
  }

  private String generateNewUnqIdWithProcedure() {
    StoredProcedureQuery query = entityManager.createStoredProcedureQuery("SP_GET_NEXT_UNQ_ID");
    query.registerStoredProcedureParameter("p_next_unq_id", String.class, ParameterMode.OUT);
    query.execute();
    String newUnqId = (String) query.getOutputParameterValue("p_next_unq_id");

    if (newUnqId == null || newUnqId.isEmpty()) {
      throw new BusinessException(DB_PROCEDURES_ERROR.getMessage());
    }

    return newUnqId;
  }
}
