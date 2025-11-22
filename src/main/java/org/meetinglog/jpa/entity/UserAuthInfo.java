package org.meetinglog.jpa.entity;

import jakarta.persistence.*;
import lombok.*;
import org.meetinglog.entity.BaseEntity;
import java.time.LocalDateTime;

@Entity
@Table(name = "USER_AUTH")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserAuthInfo extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_AUTH_ID")
    private Long userAuthId;

    @Column(name = "LOGIN_TYPE", nullable = false, length = 20)
    private String loginType; // LOGIN_TYPE (KAKAO, NORMAL 등)

    @Column(name = "PROVIDER_ID", unique = true, length = 255)
    private String providerId; // PROVIDER_ID (카카오 고유 ID)

    @Column(name = "PASSWORD", length = 255)
    private String password; // PASSWORD

    @Column(name = "ACCESS_TOKEN")
    @Lob
    private String accessToken; // ACCESS_TOKEN

    @Column(name = "EXP_IN")
    private LocalDateTime expIn; // EXP_IN (Access Token 만료 시간)

    @Column(name = "REFRESH_TOKEN")
    @Lob
    private String refreshToken; // REFRESH_TOKEN

    @Column(name = "REFRESH_TOKEN_EXP_IN")
    private LocalDateTime refreshTokenExpIn; // REFRESH_TOKEN_EXP_IN (Refresh Token 만료 시간)
}