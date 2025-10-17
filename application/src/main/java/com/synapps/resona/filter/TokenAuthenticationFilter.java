package com.synapps.resona.filter;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.synapps.resona.code.AuthErrorCode;
import com.synapps.resona.command.entity.member.Member;
import com.synapps.resona.event.MemberActivityEvent;
import com.synapps.resona.exception.MemberException;
import com.synapps.resona.config.server.ServerInfoConfig;
import com.synapps.resona.dto.RequestInfo;
import com.synapps.resona.dto.response.ErrorResponse;
import com.synapps.resona.command.entity.token.AuthToken;
import com.synapps.resona.command.entity.token.AuthTokenProvider;
import com.synapps.resona.utils.HeaderUtil;
import com.synapps.resona.command.entity.member.UserPrincipal;
import com.synapps.resona.command.repository.member.MemberRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {

  private final Logger logger = LoggerFactory.getLogger(TokenAuthenticationFilter.class);

  private final AuthTokenProvider tokenProvider;
  private final ObjectMapper objectMapper;
  private final ServerInfoConfig serverInfo;
  private final MemberRepository memberRepository;
  private final ApplicationEventPublisher eventPublisher;

  private static final AntPathMatcher pathMatcher = new AntPathMatcher();
  private static final List<String> PERMIT_ALL_URLS = Arrays.asList(
      /* swagger v3 */
      "/v3/api-docs/**",
      "/swagger-ui/**",
      "/swagger-resources/**",
      /* auth endpoints */
      "/auth/**",
      /* member join */
      "/member/join",
      /* other public endpoints */
      "/actuator/**",
      "/email/**",
      "/metrics",
      "/error"
  );

  @Override
  protected void doFilterInternal(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    String tokenStr = HeaderUtil.getAccessToken(request);
    logger.debug("Received token: {}", tokenStr);

    if (StringUtils.hasText(tokenStr)) {
      try {
        AuthToken token = tokenProvider.convertAuthToken(tokenStr);

        if (token.validate()) {
          Claims claims = token.getTokenClaims();
          String email = claims.getSubject();

          Member member = memberRepository.findWithAccountInfoByEmail(email)
              .orElseThrow(MemberException::memberNotFound);

          UserPrincipal userPrincipal = UserPrincipal.create(member);

          Authentication authentication = new UsernamePasswordAuthenticationToken(
              userPrincipal,
              null,
              userPrincipal.getAuthorities()
          );

          logger.debug("Token authorities before setting context: {}",
              authentication.getAuthorities());
          SecurityContextHolder.getContext().setAuthentication(authentication);
          logger.debug("Set Authentication to security context for '{}', uri: {}, authorities: {}",
              authentication.getName(),
              request.getRequestURI(),
              authentication.getAuthorities());

          // Member 활동 이벤트 발행
          eventPublisher.publishEvent(new MemberActivityEvent(userPrincipal.getMemberId(), LocalDateTime.now()));
        } else {
          logger.warn("Invalid token, uri: {}", request.getRequestURI());
          SecurityContextHolder.clearContext();
          handleAuthenticationError(request, response, AuthErrorCode.INVALID_TOKEN);
          return;
        }
      } catch (ExpiredJwtException e) {
        logger.error("Token expired", e);
        SecurityContextHolder.clearContext();
        handleAuthenticationError(request, response, AuthErrorCode.EXPIRED_TOKEN);
        return;
      } catch (Exception e) {
        logger.error("Could not set user authentication in security context", e);
        SecurityContextHolder.clearContext();
        handleAuthenticationError(request, response, AuthErrorCode.INVALID_TOKEN);
        return;
      }
    }
//    else {
//      if (!request.getRequestURI().equals("/api/v1/actuator/prometheus")) {
//        logger.warn("No token found in request headers, uri: {}", request.getRequestURI());
//      }
//    }

    filterChain.doFilter(request, response);
  }

  private void handleAuthenticationError(
      HttpServletRequest request,
      HttpServletResponse response,
      AuthErrorCode errorCode) throws IOException {

    RequestInfo requestInfo = new RequestInfo(
        serverInfo.getApiVersion(),
        serverInfo.getServerName(),
        request.getRequestURI()
    );

    ErrorResponse<String> errorResponse = ErrorResponse.of(
        errorCode,
        requestInfo,
        errorCode.getMessage()
    );

    response.setStatus(errorCode.getStatus().value());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding("UTF-8");

    String jsonResponse = objectMapper.writeValueAsString(errorResponse);
    response.getWriter().write(jsonResponse);
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    String path = request.getRequestURI();

    boolean shouldNotFilter = PERMIT_ALL_URLS.stream()
        .anyMatch(pattern -> pathMatcher.match(pattern, path));

    logger.debug("Checking path: {}. Should not filter: {}", path, shouldNotFilter);
    return shouldNotFilter;
  }
}
