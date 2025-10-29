package org.meetinglog.jpa.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Builder;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String kakaoId;

    @Column(nullable = false)
    private String nickname;

    @Column
    private String email;

    @Column
    private String profileImageUrl;

    @Enumerated(EnumType.STRING)
    private Role role;

    public enum Role {
        USER, ADMIN
    }

    public User updateProfile(String nickname, String email, String profileImageUrl) {
        this.nickname = nickname;
        this.email = email;
        this.profileImageUrl = profileImageUrl;
        return this;
    }
}
