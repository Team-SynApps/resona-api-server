package synapps.resona.api.global.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import synapps.resona.api.global.config.server.ServerInfoConfig;
import synapps.resona.api.global.dto.response.ErrorResponse;
import synapps.resona.api.global.dto.RequestInfo;
import synapps.resona.api.global.dto.code.ErrorCode;
import synapps.resona.api.global.error.GlobalErrorCode;
import synapps.resona.api.mysql.member.code.AuthErrorCode;

// @Component 어노테이션을 추가하여 Bean으로 등록합니다.
@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

  private final ObjectMapper objectMapper;
  private final ServerInfoConfig serverInfo;

  @Override
  public void commence(
      HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException authException
  ) throws IOException, ServletException {
    // 인증 실패 시, 여기서 커스텀 ErrorResponse를 생성합니다.
    ErrorCode errorCode = AuthErrorCode.LOGIN_FAILED;
    RequestInfo requestInfo = new RequestInfo(serverInfo.getApiVersion(), serverInfo.getServerName(), request.getQueryString());

    // 응답 상태 코드 설정
    response.setStatus(errorCode.getStatusCode());
    // 응답 컨텐츠 타입 설정
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding("UTF-8");

    // 응답 바디에 JSON 쓰기
    ErrorResponse<String> errorResponse = ErrorResponse.of(errorCode, requestInfo, authException.getMessage());
    String jsonResponse = objectMapper.writeValueAsString(errorResponse);
    response.getWriter().write(jsonResponse);
  }
}