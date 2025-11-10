package org.meetinglog.config;

import org.meetinglog.auth.service.KakaoOAuth2UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private KakaoOAuth2UserService kakaoOAuth2UserService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                    .requestMatchers("/**").permitAll() // 모든 경로(/**)를 허용
                    .anyRequest().authenticated()
            )
            .csrf(AbstractHttpConfigurer::disable);
//            .csrf(csrf -> csrf.disable())
//            .authorizeHttpRequests(auth -> auth
//                .requestMatchers("/", "/login", "/oauth2/**", "/login/oauth2/code/**").permitAll()
//                .requestMatchers("/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
//                .anyRequest().authenticated()
//            )
//            .oauth2Login(oauth2 -> oauth2
//                .loginPage("/login")
//                .defaultSuccessUrl("/dashboard", true)
//                .userInfoEndpoint(userInfo -> userInfo
//                    .userService(kakaoOAuth2UserService)
//                )
//            )
//            .logout(logout -> logout
//                .logoutSuccessUrl("/")
//                .invalidateHttpSession(true)
//                .clearAuthentication(true)
//            );

        return http.build();
    }
}
