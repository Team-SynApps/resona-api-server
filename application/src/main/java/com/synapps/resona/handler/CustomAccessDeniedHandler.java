package com.synapps.resona.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.synapps.resona.code.AuthErrorCode;
import com.synapps.resona.config.server.ServerInfoConfig;
import com.synapps.resona.dto.RequestInfo;
import com.synapps.resona.dto.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

  private static final Logger logger = LoggerFactory.getLogger(CustomAccessDeniedHandler.class);

  private final ServerInfoConfig serverInfo;

  @Override
  public void handle(HttpServletRequest request, HttpServletResponse response,
      AccessDeniedException accessDeniedException) throws IOException {
    // 403 상태 코드와 함께 처리
    logger.error("Access Denied: {}", accessDeniedException.getMessage(), accessDeniedException);

    response.setContentType("application/json;charset=UTF-8");

    ErrorResponse<Object> errorResponse = ErrorResponse.of(
        AuthErrorCode.FORBIDDEN,
        new RequestInfo(serverInfo.getApiVersion(), serverInfo.getServerName(), request.getQueryString())
    );

    // 커스텀 에러 응답 반환
    response.setStatus(HttpStatus.FORBIDDEN.value());  // 403 상태 코드
    response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
    response.getWriter().flush();  // 응답 전송
    response.getWriter().close();  // 스트림 종료
  }
}
