package org.meetinglog.common.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.meetinglog.auth.jwt.JwtTokenProvider;
import org.meetinglog.common.context.CurrentUserData;
import org.meetinglog.common.context.UserContext;
import org.meetinglog.common.enums.ErrorMessage;
import org.meetinglog.common.exception.BusinessException;
import org.meetinglog.jpa.entity.UserMst;
import org.meetinglog.jpa.repository.UserMstRepository;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtTokenProvider jwtTokenProvider;
  private final UserMstRepository userMstRepository;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    try {
      String token = resolveToken(request);

      if (token != null && jwtTokenProvider.validateToken(token)) {
        String unqId = jwtTokenProvider.getUnqIdFromToken(token);
        UserMst userMst = userMstRepository.findByUnqId(unqId)
            .orElseThrow(() -> new BusinessException(ErrorMessage.AUTHENTICATION_FAILED.getMessage()));

        CurrentUserData currentUser = CurrentUserData.builder()
            .unqId(unqId)
            .userId(userMst.getUserId())
            .UserNm(userMst.getUserNm())
            .build();

        UserContext.set(currentUser);
        log.debug("User Context Saved: {}", currentUser);
      }

      filterChain.doFilter(request, response);

    } catch (Exception e) {
      log.error("Security Filter Error", e);
      request.setAttribute("exception", e);
    } finally {
      UserContext.clear();
    }
  }

  private String resolveToken(HttpServletRequest request) {
    String bearerToken = request.getHeader("Authorization");
    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
      return bearerToken.substring(7);
    }
    return null;
  }
}