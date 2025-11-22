package org.meetinglog.auth.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.time.LocalDateTime;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.io.Decoders;

@Component
public class JwtTokenProvider {

  @Value("${jwt.secret}")
  private String secretKey;

  @Value("${jwt.access-token-expiration-seconds}")
  private long accessTokenExpirationSeconds;

  @Value("${jwt.refresh-token-expiration-days}")
  private long refreshTokenExpirationDays;

  private Key key;

  private Key getSigningKey() {
    if (this.key == null) {
      byte[] keyBytes = Decoders.BASE64.decode(secretKey);
      this.key = Keys.hmacShaKeyFor(keyBytes);
    }
    return this.key;
  }


  /**
   * Access Token을 생성합니다. 클레임: 'sub' (UNQ_ID)
   * @param unqId 사용자 고유 식별자
   * @return 생성된 Access Token
   */
  public String generateAccessToken(String unqId) {
    long now = (new Date()).getTime();
    Date accessTokenExpiresIn = new Date(now + this.accessTokenExpirationSeconds * 1000);

    return Jwts.builder()
        .setSubject(unqId) // 토큰 주체 (사용자 식별자 UNQ_ID)
        .claim("token_type", "access") // 클레임 추가
        .setExpiration(accessTokenExpiresIn) // 만료 시간 설정
        .signWith(getSigningKey(), SignatureAlgorithm.HS512) // 서명 알고리즘 및 키 설정
        .compact();
  }

  // --- 2. Refresh Token 생성 ---

  /**
   * Refresh Token을 생성합니다. 클레임: 'sub' (UNQ_ID)
   * @param unqId 사용자 고유 식별자
   * @return 생성된 Refresh Token
   */
  public String generateRefreshToken(String unqId) {
    long now = (new Date()).getTime();
    Date refreshTokenExpiresIn = new Date(now + this.refreshTokenExpirationDays * 24 * 60 * 60 * 1000);

    return Jwts.builder()
        .setSubject(unqId) // 토큰 주체 (사용자 식별자 UNQ_ID)
        .claim("token_type", "refresh") // 클레임 추가
        .setExpiration(refreshTokenExpiresIn) // 만료 시간 설정
        .signWith(getSigningKey(), SignatureAlgorithm.HS512) // 서명 알고리즘 및 키 설정
        .compact();
  }

  public LocalDateTime getAccessTokenExpiryDate() {
    long durationSeconds = this.accessTokenExpirationSeconds;
    return LocalDateTime.now().plusSeconds(durationSeconds);
  }

  public LocalDateTime getRefreshTokenExpiryDate() {
    long durationSeconds = this.refreshTokenExpirationDays * 24 * 60 * 60;
    return LocalDateTime.now().plusSeconds(durationSeconds);
  }
}