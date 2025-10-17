package com.synapps.resona.filter;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.synapps.resona.code.AuthErrorCode;
import com.synapps.resona.command.entity.account.AccountInfo;
import com.synapps.resona.command.entity.account.AccountStatus;
import com.synapps.resona.command.entity.member.Member;
import com.synapps.resona.event.MemberActivityEvent;
import com.synapps.resona.exception.AuthException;
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
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {

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

    if (StringUtils.hasText(tokenStr)) {
      try {
        AuthToken token = tokenProvider.convertAuthToken(tokenStr);
        Authentication authentication = createAuthentication(token);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.debug("Set Authentication to security context for '{}', uri: {}",
            authentication.getName(), request.getRequestURI());

        publishMemberActivityEvent(authentication);
      } catch (ExpiredJwtException e) {
        log.warn("Token expired for uri: {}", request.getRequestURI());
        handleAuthenticationError(request, response, AuthErrorCode.EXPIRED_TOKEN);
        return;
      } catch (AuthException e) {
        log.warn("AuthException: {} for uri: {}", e.getMessage(), request.getRequestURI());
        handleAuthenticationError(request, response, AuthErrorCode.fromCustomCode(e.getErrorCode()));
        return;
      } catch (Exception e) {
        log.error("Could not set user authentication in security context for uri: {}", request.getRequestURI(), e);
        handleAuthenticationError(request, response, AuthErrorCode.INVALID_TOKEN);
        return;
      }
    }

    filterChain.doFilter(request, response);
  }

  private Authentication createAuthentication(AuthToken token) {
    Claims claims = token.getTokenClaims();
    String email = claims.getSubject();

    Member member = memberRepository.findWithAccountInfoByEmail(email)
        .orElseThrow(MemberException::memberNotFound);

    checkAccountStatus(member);

    UserPrincipal userPrincipal = UserPrincipal.create(member);
    return new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());
  }

  private void checkAccountStatus(Member member) {
    AccountInfo accountInfo = member.getAccountInfo();
    if (accountInfo.getStatus() == AccountStatus.BANNED) {
      if (accountInfo.isBanExpired()) {
        log.info("User '{}' ban has expired. Unbanning the account.", member.getEmail());
        accountInfo.unban();
        memberRepository.save(member);
      } else {
        log.warn("Banned user '{}' attempted access. Banned until: {}", member.getEmail(), accountInfo.getBannedUntil());
        throw AuthException.accountBanned();
      }
    }
  }

  private void publishMemberActivityEvent(Authentication authentication) {
    if (authentication.getPrincipal() instanceof UserPrincipal userPrincipal) {
      eventPublisher.publishEvent(new MemberActivityEvent(userPrincipal.getMemberId(), LocalDateTime.now()));
    }
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

    log.debug("Checking path: {}. Should not filter: {}", path, shouldNotFilter);
    return shouldNotFilter;
  }
}
