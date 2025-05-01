package synapps.resona.api.oauth.handler;

import static synapps.resona.api.oauth.respository.CustomOAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import synapps.resona.api.global.dto.metadata.ErrorMetaDataDto;
import synapps.resona.api.global.dto.response.ResponseDto;
import synapps.resona.api.global.exception.ErrorCode;
import synapps.resona.api.global.utils.CookieUtil;
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

    Throwable cause = exception.getCause();
    String message = exception.getMessage();
    String errorCode = "AUTH000";
    int status = HttpServletResponse.SC_UNAUTHORIZED;

    if (cause instanceof OAuthException oAuthException) {
      message = oAuthException.getMessage();
      errorCode = ErrorCode.PROVIDER_TYPE_MISSMATCH.getCode();
    }

    ErrorMetaDataDto metaData = ErrorMetaDataDto.createErrorMetaData(
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
