package synapps.resona.api.global.handler;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import synapps.resona.api.external.email.code.EmailErrorCode;
import synapps.resona.api.external.email.dto.EmailCheckExceptionDto;
import synapps.resona.api.external.email.exception.EmailException;
import synapps.resona.api.global.config.server.ServerInfoConfig;
import synapps.resona.api.global.dto.response.ErrorResponse;
import synapps.resona.api.global.dto.RequestInfo;
import synapps.resona.api.global.error.exception.BaseException;
import synapps.resona.api.global.dto.code.ErrorCode;
import synapps.resona.api.global.error.GlobalErrorCode;
import synapps.resona.api.mysql.member.code.AuthErrorCode;
import synapps.resona.api.mysql.member.exception.AuthException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger logger = LogManager.getLogger(GlobalExceptionHandler.class);

  private final ServerInfoConfig serverInfo;

  public GlobalExceptionHandler(ServerInfoConfig serverInfo) {
    this.serverInfo = serverInfo;
  }

  private RequestInfo createRequestInfo(HttpServletRequest request) {
    return new RequestInfo(serverInfo.getApiVersion(), serverInfo.getServerName(), request.getQueryString());
  }

  @ExceptionHandler(BaseException.class)
  protected ResponseEntity<ErrorResponse<String>> handleBaseException(BaseException ex, HttpServletRequest request) {
    logger.error("BaseException: {}", ex.getMessage(), ex);
    RequestInfo requestInfo = createRequestInfo(request);
    return createErrorResponse(GlobalErrorCode.INTERNAL_SERVER_ERROR, requestInfo);
  }

  @ExceptionHandler(Exception.class)
  protected ResponseEntity<ErrorResponse<Exception>> handleUnexpectedException(Exception ex, HttpServletRequest request) {
    logger.error("Unexpected Error: {}", ex.getMessage(), ex);
    RequestInfo requestInfo = createRequestInfo(request);
    return createErrorResponse(GlobalErrorCode.INTERNAL_SERVER_ERROR, requestInfo);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  protected ResponseEntity<ErrorResponse<Map<String, String>>> handleValidationExceptions(
      MethodArgumentNotValidException ex, HttpServletRequest request) {
    logger.error("validation Error: {}", ex.getMessage(), ex);
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getFieldErrors().forEach(error ->
        errors.put(error.getField(), error.getDefaultMessage())
    );
    RequestInfo requestInfo = createRequestInfo(request);
    return createErrorResponse(GlobalErrorCode.INVALID_INPUT, requestInfo, errors);
  }

  @ExceptionHandler(EmailException.class)
  public ResponseEntity<?> handleEmailException(EmailException ex, HttpServletRequest request) {
    RequestInfo requestInfo = createRequestInfo(request);
    if (ex.getErrorCode().equals(EmailErrorCode.INVALID_EMAIL_CODE.getCustomCode())) {
      EmailCheckExceptionDto body = new EmailCheckExceptionDto(ex.getMessage(),
          ex.getMailCheckCountLeft());
      return createErrorResponse(EmailErrorCode.INVALID_EMAIL_CODE, requestInfo, body);
    }
    logger.error(ex.getMessage(), ex);
    return createErrorResponse(GlobalErrorCode.INTERNAL_SERVER_ERROR, requestInfo);
  }

  @ExceptionHandler(AuthException.class)
  public ResponseEntity<?> handleAuthException(AuthException ex, HttpServletRequest request) { // 파라미터 타입을 AuthException으로 변경
    logger.error(ex.getMessage(), ex);
    RequestInfo requestInfo = createRequestInfo(request);
    return createErrorResponse(GlobalErrorCode.INTERNAL_SERVER_ERROR, requestInfo);
  }

  @ExceptionHandler({AuthenticationException.class, BadCredentialsException.class})
  protected ResponseEntity<ErrorResponse<String>> handleAuthenticationException(AuthenticationException ex, HttpServletRequest request) {
    logger.error("Authentication failed: {}", ex.getMessage());
    RequestInfo requestInfo = createRequestInfo(request);

    return createErrorResponse(AuthErrorCode.LOGIN_FAILED, requestInfo, ex.getMessage());
  }

  private <T> ResponseEntity<ErrorResponse<T>> createErrorResponse(ErrorCode code, RequestInfo info) {
    logger.error(code.getMessage());
    return ResponseEntity
        .status(code.getStatusCode())
        .body(ErrorResponse.of(code, info));
  }

  private <T> ResponseEntity<ErrorResponse<T>> createErrorResponse(ErrorCode code, RequestInfo info, T error) {
    logger.error(code.getMessage());
    return ResponseEntity
        .status(code.getStatusCode())
        .body(ErrorResponse.of(code, info, error));
  }
}