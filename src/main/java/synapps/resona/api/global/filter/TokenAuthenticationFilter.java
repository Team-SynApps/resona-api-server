package synapps.resona.api.global.filter;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import synapps.resona.api.global.config.server.ServerInfoConfig;
import synapps.resona.api.global.dto.RequestInfo;
import synapps.resona.api.global.dto.response.ErrorResponse;
import synapps.resona.api.global.error.GlobalErrorCode;
import synapps.resona.api.global.utils.HeaderUtil;
import synapps.resona.api.mysql.member.code.AuthErrorCode;
import synapps.resona.api.mysql.member.entity.account.AccountInfo; // Import 추가
import synapps.resona.api.mysql.member.entity.member.Member; // Import 추가
import synapps.resona.api.mysql.member.exception.MemberException;
import synapps.resona.api.mysql.member.repository.member.MemberRepository; // Import 추가
import synapps.resona.api.mysql.token.AuthToken;
import synapps.resona.api.mysql.token.AuthTokenProvider;
import synapps.resona.api.oauth.entity.UserPrincipal; // Import 추가

@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {

  private static final Logger logger = LogManager.getLogger(TokenAuthenticationFilter.class);

  private final AuthTokenProvider tokenProvider;
  private final ObjectMapper objectMapper;
  private final ServerInfoConfig serverInfo;
  private final MemberRepository memberRepository;

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

          Member member = memberRepository.findByEmailWithAccountInfo(email)
              .orElseThrow(MemberException::memberNotFound);
          AccountInfo accountInfo = member.getAccountInfo();

          UserPrincipal userPrincipal = UserPrincipal.create(member, accountInfo);

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
    } else {
      if (!request.getRequestURI().equals("/api/v1/actuator/prometheus")) {
        logger.warn("No token found in request headers, uri: {}", request.getRequestURI());
      }
      // SecurityContextHolder.clearContext(); // 주석 처리 또는 제거: 토큰이 없을 때 익명 사용자로 처리되도록 함
    }

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