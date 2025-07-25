package synapps.resona.api.global.handler;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import synapps.resona.api.global.config.server.ServerInfoConfig;
import synapps.resona.api.global.dto.metadata.ErrorMeta;
import synapps.resona.api.global.dto.response.ResponseDto;
import synapps.resona.api.global.error.core.GlobalErrorCode;
import synapps.resona.api.oauth.exception.OAuthException;

/**
 * Spring Security의 인증 실패 처리를 담당하는 커스텀 엔트리포인트 클래스 인증되지 않은 사용자의 보호된 리소스 접근 시도를 처리
 * <p>
 * 주요 기능: - 인증되지 않은 요청 감지 및 처리 - 표준화된 에러 응답 생성 (JSON 형식) - 상세한 에러 로깅
 * <p>
 * 응답에는 다음 정보가 포함됨: - HTTP 상태 코드 - 에러 메시지 - 서버 정보 - API 버전 - 요청 URI
 * <p>
 * 기본적으로 모든 인증 실패는 TOKEN_NOT_FOUND로 처리 응답은 항상 JSON 형식으로 반환
 */
@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

  private static final Logger logger = LogManager.getLogger(CustomAuthenticationEntryPoint.class);
  private final ObjectMapper objectMapper;
  private final ServerInfoConfig serverInfo;

  @Override
  public void commence(HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException authException) throws IOException {

    Throwable cause = authException.getCause();

    // 기본 값은 authException에서 가져오기
    String message = authException.getMessage();
    String errorCode = "AUTH000"; // default error code
    int status = HttpServletResponse.SC_UNAUTHORIZED; // 401

    // cause가 OAuthException이라면 더 구체적인 메시지와 코드 사용
    if (cause instanceof OAuthException oAuthException) {
      message = oAuthException.getMessage();
      errorCode = GlobalErrorCode.PROVIDER_TYPE_MISSMATCH.getCode();
    }

    logger.error("Authentication error: {}", message, authException);

    ErrorMeta metaData = ErrorMeta.createErrorMetaData(
        status,
        message,
        request.getRequestURI(),
        serverInfo.getVersionNumber(),
        serverInfo.getServerName(),
        errorCode
    );

    ResponseDto responseData = new ResponseDto(
        metaData,
        List.of(Map.of("error", message))
    );

    response.setStatus(status);
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding("UTF-8");

    String jsonResponse = objectMapper.writeValueAsString(responseData);
    response.getWriter().write(jsonResponse);
  }

}