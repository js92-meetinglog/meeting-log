package org.meetinglog.auth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class KakaoUserInfo {
    private Long id;
    private KakaoAccount kakao_account;
    private Properties properties;

    @Getter
    @NoArgsConstructor
    public static class KakaoAccount {
        private String email;
        private Profile profile;

        @Getter
        @NoArgsConstructor
        public static class Profile {
            private String nickname;
            private String profile_image_url;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class Properties {
        private String nickname;
        private String profile_image;
    }
}
