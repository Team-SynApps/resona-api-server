package synapps.resona.api.oauth.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import synapps.resona.api.global.dto.metadata.ErrorMeta;
import synapps.resona.api.global.dto.response.ResponseDto;
import synapps.resona.api.oauth.exception.OAuthException;
import synapps.resona.api.oauth.respository.CustomOAuth2AuthorizationRequestRepository;


@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationFailureHandler implements AuthenticationFailureHandler {

  private final ObjectMapper objectMapper;
  private final CustomOAuth2AuthorizationRequestRepository authorizationRequestRepository;

  @Override
  public void onAuthenticationFailure(HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException exception) throws IOException {

    authorizationRequestRepository.removeAuthorizationRequestCookies(request, response);

    String message = exception.getMessage();
    String errorCode = "AUTH000";
    int status = HttpServletResponse.SC_UNAUTHORIZED;

    // OAuthException이면 커스텀 정보 사용
    if (exception instanceof OAuthException oAuthException) {
      message = oAuthException.getMessage();
      errorCode = oAuthException.getErrorCode();
      status = oAuthException.getStatus().value();
    }

    ErrorMeta metaData = ErrorMeta.createErrorMetaData(
        status,
        message,
        request.getRequestURI(),
        "1", // API version
        "resona", // Server name
        errorCode
    );

    ResponseDto responseDto = new ResponseDto(
        metaData,
        List.of(Map.of("error", message))
    );

    response.setStatus(status);
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding("UTF-8");
    response.getWriter().write(objectMapper.writeValueAsString(responseDto));
  }
}
