package com.synapps.resona.handler;

import com.synapps.resona.code.AuthErrorCode;
import com.synapps.resona.email.code.EmailErrorCode;
import com.synapps.resona.email.dto.EmailCheckExceptionDto;
import com.synapps.resona.email.exception.EmailException;
import com.synapps.resona.exception.AuthException;
import com.synapps.resona.config.server.ServerInfoConfig;
import com.synapps.resona.dto.RequestInfo;
import com.synapps.resona.dto.code.ErrorCode;
import com.synapps.resona.dto.response.ErrorResponse;
import com.synapps.resona.error.GlobalErrorCode;
import com.synapps.resona.error.exception.BaseException;
import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

public class GlobalExceptionHandler {

  private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

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
    logger.error("EmailException: code={}, message={}", ex.getErrorCode(), ex.getMessage(), ex);
    RequestInfo requestInfo = createRequestInfo(request);

    EmailErrorCode errorCode = EmailErrorCode.fromErrorCode(ex.getErrorCode());

    if (errorCode == EmailErrorCode.INVALID_EMAIL_CODE) {
      EmailCheckExceptionDto body = new EmailCheckExceptionDto(ex.getMessage(), ex.getMailCheckCountLeft());
      return createErrorResponse(errorCode, requestInfo, body);
    }

    return createErrorResponse(errorCode, requestInfo);
  }

  @ExceptionHandler(AuthException.class)
  public ResponseEntity<ErrorResponse<String>> handleAuthException(AuthException ex, HttpServletRequest request) {
    logger.error("AuthException: code={}, message={}", ex.getErrorCode(), ex.getMessage(), ex);
    RequestInfo requestInfo = createRequestInfo(request);

    AuthErrorCode errorCode = AuthErrorCode.fromCustomCode(ex.getErrorCode());

    return createErrorResponse(errorCode, requestInfo, errorCode.getMessage());
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