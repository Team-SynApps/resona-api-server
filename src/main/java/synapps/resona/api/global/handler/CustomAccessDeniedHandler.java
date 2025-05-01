package synapps.resona.api.global.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import synapps.resona.api.global.config.server.ServerInfoConfig;
import synapps.resona.api.global.dto.metadata.ErrorMetaDataDto;
import synapps.resona.api.global.dto.response.ErrorResponse;
import synapps.resona.api.global.dto.response.ResponseDto;
import synapps.resona.api.global.exception.ErrorCode;

@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

  private static final Logger logger = LogManager.getLogger(CustomAccessDeniedHandler.class);

  private final ServerInfoConfig serverInfo;

  @Override
  public void handle(HttpServletRequest request, HttpServletResponse response,
      AccessDeniedException accessDeniedException) throws IOException {
    // 403 상태 코드와 함께 처리
    logger.error("Access Denied: {}", accessDeniedException.getMessage(), accessDeniedException);

    response.setContentType("application/json;charset=UTF-8");

    // ErrorMetaDataDto 생성
    ErrorMetaDataDto metaData = ErrorMetaDataDto.createErrorMetaData(
        ErrorCode.FORBIDDEN.getStatus().value(),
        ErrorCode.FORBIDDEN.getMessage(),
        request.getRequestURI(),
        serverInfo.getApiVersion(),
        serverInfo.getServerName(),
        ErrorCode.FORBIDDEN.getCode()
    );

    // 응답 생성
    ResponseDto responseData = new ResponseDto(
        metaData,
        List.of(new ErrorResponse("Access Denied: You do not have permission"))
    );

    // 커스텀 에러 응답 반환
    response.setStatus(HttpStatus.FORBIDDEN.value());  // 403 상태 코드
    response.getWriter().write(new ObjectMapper().writeValueAsString(responseData));
    response.getWriter().flush();  // 응답 전송
    response.getWriter().close();  // 스트림 종료
  }
}
