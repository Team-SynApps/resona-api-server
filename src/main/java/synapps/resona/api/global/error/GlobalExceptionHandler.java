package synapps.resona.api.global.error;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import synapps.resona.api.external.email.dto.EmailCheckExceptionDto;
import synapps.resona.api.external.email.exception.EmailException;
import synapps.resona.api.global.config.server.ServerInfoConfig;
import synapps.resona.api.global.dto.metadata.ErrorMeta;
import synapps.resona.api.global.dto.response.ErrorResponse;
import synapps.resona.api.global.dto.response.ResponseDto;
import synapps.resona.api.global.error.core.BaseException;
import synapps.resona.api.global.error.core.GlobalErrorCode;
import synapps.resona.api.mysql.member.exception.AuthException;

/**
 * 전역 예외 처리를 담당하는 핵심 클래스 애플리케이션에서 발생하는 모든 예외를 일관된 형식으로 처리하고 클라이언트에게 적절한 응답을 제공
 * <p>
 * 주요 기능: - 기본 예외(BaseException) 처리 - 일반적인 예외(Exception) 처리 - 데이터 유효성 검증
 * 실패(MethodArgumentNotValidException) 처리 - 이메일 관련 예외(EmailException) 처리
 * <p>
 * 모든 예외 응답은 ErrorMetaDataDto를 포함하여 일관된 형식으로 반환 서버 정보, API 버전, 에러 코드 등의 메타데이터와 함께 구체적인 에러 메시지를 제공
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger logger = LogManager.getLogger(GlobalExceptionHandler.class);

  private final ServerInfoConfig serverInfo;
  private final HttpServletRequest request;

  public GlobalExceptionHandler(ServerInfoConfig serverInfo, HttpServletRequest request) {
    this.serverInfo = serverInfo;
    this.request = request;
  }

  @ExceptionHandler(BaseException.class)
  protected ResponseEntity<ResponseDto> handleBaseException(BaseException ex) {
    logger.error("BaseException: {}", ex.getMessage(), ex);

    // ErrorMetaDataDto 생성
    ErrorMeta metaData = ErrorMeta.createErrorMetaData(
        ex.getStatus().value(),
        ex.getMessage(),
        request.getRequestURI(),
        serverInfo.getApiVersion(),
        serverInfo.getServerName(),
        ex.getErrorCode()
    );

    // 응답 생성
    ResponseDto responseData = new ResponseDto(
        metaData,
        List.of(Map.of("error", ex.getMessage()))
    );

    return new ResponseEntity<>(
        responseData,
        HttpStatus.valueOf(ex.getErrorCode())
    );
  }

  // 일반 예외 처리
  @ExceptionHandler(Exception.class)
  protected ResponseEntity<ResponseDto> handleUnexpectedException(Exception ex) {
    logger.error("Unexpected Error: {}", ex.getMessage(), ex);

    // ErrorMetaDataDto 생성
    ErrorMeta metaData = createErrorMetaData(
        GlobalErrorCode.INTERNAL_SERVER_ERROR.getStatus().value(),
        GlobalErrorCode.INTERNAL_SERVER_ERROR.getMessage(),
        request.getRequestURI(),
        GlobalErrorCode.INTERNAL_SERVER_ERROR.name()
    );

    // 응답 생성
    ResponseDto responseData = new ResponseDto(
        metaData,
        List.of(Map.of("error", ex.getMessage()))
    );

    return new ResponseEntity<>(
        responseData,
        HttpStatus.INTERNAL_SERVER_ERROR
    );
  }

  // 데이터 유효성 검증 실패 예외 처리
  @ExceptionHandler(MethodArgumentNotValidException.class)
  protected ResponseEntity<ResponseDto> handleValidationExceptions(
      MethodArgumentNotValidException ex) {
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getFieldErrors().forEach(error ->
        errors.put(error.getField(), error.getDefaultMessage())
    );

    // ErrorMetaDataDto 생성
    ErrorMeta metaData = createErrorMetaData(
        GlobalErrorCode.INVALID_INPUT.getStatus().value(),
        GlobalErrorCode.INVALID_INPUT.getMessage(),
        request.getRequestURI(),
        GlobalErrorCode.INVALID_INPUT.name()
    );

    // 응답 생성
    ResponseDto responseData = new ResponseDto(
        metaData,
        List.of(errors)
    );

    return new ResponseEntity<>(
        responseData,
        HttpStatus.BAD_REQUEST
    );
  }

  @ExceptionHandler(EmailException.class)
  public ResponseEntity<?> handleEmailException(EmailException ex, HttpServletRequest request) {
    ErrorMeta metaData = createErrorMetaData(ex.getStatus().value(), ex.getMessage(),
        request.getRequestURI(), ex.getErrorCode());
    if (ex.getErrorCode().equals(GlobalErrorCode.INVALID_EMAIL_CODE.getCode())) {
      EmailCheckExceptionDto body = new EmailCheckExceptionDto(ex.getMessage(),
          ex.getMailCheckCountLeft());
      return createEmailErrorResponse(metaData, ex.getStatus(), body);
    }
    logger.error(ex.getMessage(), ex);
    return createErrorResponse(metaData, ex.getStatus(), ex.getMessage());
  }

  @ExceptionHandler(AuthException.class)
  public ResponseEntity<?> handleAuthException(EmailException ex, HttpServletRequest request) {
    ErrorMeta metaData = createErrorMetaData(ex.getStatus().value(), ex.getMessage(),
        request.getRequestURI(), ex.getErrorCode());
    logger.error(ex.getMessage(), ex);
    return createErrorResponse(metaData, ex.getStatus(), ex.getMessage());
  }

  private ErrorMeta createErrorMetaData(int statusCode, String message, String requestUri,
      String errorCode) {
    return ErrorMeta.createErrorMetaData(
        statusCode,
        message,
        requestUri,
        serverInfo.getVersionNumber(),
        serverInfo.getServerName(),
        errorCode
    );
  }

  private ResponseEntity<?> createErrorResponse(ErrorMeta metaData, HttpStatus status,
      String message) {
    ResponseDto responseData = new ResponseDto(metaData, List.of(new ErrorResponse(message)));
    return new ResponseEntity<>(responseData, status);
  }

  private ResponseEntity<?> createEmailErrorResponse(ErrorMeta metaData, HttpStatus status,
      EmailCheckExceptionDto body) {
    ResponseDto responseData = new ResponseDto(metaData, List.of(body));
    return new ResponseEntity<>(responseData, status);
  }
}