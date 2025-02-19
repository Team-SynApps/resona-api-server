package synapps.resona.api.global.handler;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import synapps.resona.api.external.email.exception.EmailException;
import synapps.resona.api.global.config.server.ServerInfoConfig;
import synapps.resona.api.global.dto.metadata.ErrorMetaDataDto;
import synapps.resona.api.global.dto.response.ErrorResponse;
import synapps.resona.api.global.dto.response.ResponseDto;
import synapps.resona.api.global.exception.AuthException;
import synapps.resona.api.global.exception.BaseException;
import synapps.resona.api.global.exception.ErrorCode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
        ErrorMetaDataDto metaData = ErrorMetaDataDto.createErrorMetaData(
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
        ErrorMetaDataDto metaData = createErrorMetaData(
                ErrorCode.INTERNAL_SERVER_ERROR.getStatus().value(),
                ErrorCode.INTERNAL_SERVER_ERROR.getMessage(),
                request.getRequestURI(),
                ErrorCode.INTERNAL_SERVER_ERROR.name()
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
    protected ResponseEntity<ResponseDto> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        // ErrorMetaDataDto 생성
        ErrorMetaDataDto metaData = createErrorMetaData(
                ErrorCode.INVALID_INPUT.getStatus().value(),
                ErrorCode.INVALID_INPUT.getMessage(),
                request.getRequestURI(),
                ErrorCode.INVALID_INPUT.name()
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
        ErrorMetaDataDto metaData = createErrorMetaData(ex.getStatus().value(), ex.getMessage(), request.getRequestURI(), ex.getErrorCode());
        logger.error(ex.getMessage(), ex);
        return createErrorResponse(metaData, ex.getStatus(), ex.getMessage());
    }

    private ErrorMetaDataDto createErrorMetaData(int statusCode, String message, String requestUri, String errorCode) {
        return ErrorMetaDataDto.createErrorMetaData(
                statusCode,
                message,
                requestUri,
                serverInfo.getVersionNumber(),
                serverInfo.getServerName(),
                errorCode
        );
    }

    private ResponseEntity<?> createErrorResponse(ErrorMetaDataDto metaData, HttpStatus status, String message) {
        ResponseDto responseData = new ResponseDto(metaData, List.of(new ErrorResponse(message)));
        return new ResponseEntity<>(responseData, status);
    }
}