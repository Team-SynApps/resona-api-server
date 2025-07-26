package synapps.resona.api.oauth.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import synapps.resona.api.global.config.server.ServerInfoConfig;
import synapps.resona.api.global.dto.ErrorResponse;
import synapps.resona.api.global.dto.RequestInfo;
import synapps.resona.api.global.error.core.GlobalErrorCode;
import synapps.resona.api.oauth.exception.OAuthException;
import synapps.resona.api.oauth.respository.CustomOAuth2AuthorizationRequestRepository;


@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationFailureHandler implements AuthenticationFailureHandler {

  private final ObjectMapper objectMapper;
  private final CustomOAuth2AuthorizationRequestRepository authorizationRequestRepository;
  private final ServerInfoConfig serverInfo;

  @Override
  public void onAuthenticationFailure(HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException exception) throws IOException {

    authorizationRequestRepository.removeAuthorizationRequestCookies(request, response);

    String message = "인증에 실패하였습니다.";
    int status = HttpServletResponse.SC_UNAUTHORIZED;

    // 커스텀 OAuthException인 경우, 해당 예외의 정보를 사용
    if (exception instanceof OAuthException oAuthException) {
      message = oAuthException.getMessage();
      status = oAuthException.getStatus().value();
    } else if (exception.getCause() != null) {
      // 더 구체적인 원인 메시지가 있다면 사용
      message = exception.getCause().getMessage();
    } else if (exception.getMessage() != null) {
      message = exception.getMessage();
    }

    // 1. 표준 RequestInfo 생성
    RequestInfo requestInfo = new RequestInfo(
        serverInfo.getApiVersion(),
        serverInfo.getServerName(),
        request.getRequestURI()
    );

    // 2. 표준 ErrorResponse 생성 (동적 에러 정보 사용)
    ErrorResponse<String> errorResponse = ErrorResponse.of(
        GlobalErrorCode.UNAUTHORIZED,
        requestInfo,
        message
    );

    // 3. 응답 스트림에 직접 작성
    response.setStatus(status);
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding("UTF-8");
    response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
  }
}