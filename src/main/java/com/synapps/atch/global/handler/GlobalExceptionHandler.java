package com.synapps.atch.global.handler;

import com.synapps.atch.global.config.ServerInfoConfig;
import com.synapps.atch.global.dto.ErrorMetaDataDto;
import com.synapps.atch.global.dto.ResponseDto;
import com.synapps.atch.global.exception.AuthException;
import com.synapps.atch.global.exception.BaseException;
import com.synapps.atch.mysql.member.exception.MemberException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    private final ServerInfoConfig serverInfoConfig;

    @Autowired
    public GlobalExceptionHandler(ServerInfoConfig serverInfoConfig) {
        this.serverInfoConfig = serverInfoConfig;
    }

    @ExceptionHandler({BaseException.class, MemberException.class, AuthException.class})
    public ResponseEntity<?> handleCustomException(BaseException ex, HttpServletRequest request) {
        ErrorMetaDataDto metaData = createErrorMetaData(ex.getStatus().value(), ex.getMessage(), request.getRequestURI(), ex.getErrorCode());
        log.error(ex.getMessage(), ex);
        return createErrorResponse(metaData, ex.getStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<String> errors = ex.getBindingResult().getAllErrors().stream()
                .map(error -> {
                    if (error instanceof FieldError) {
                        return ((FieldError) error).getField() + ": " + error.getDefaultMessage();
                    }
                    return error.getDefaultMessage();
                })
                .collect(Collectors.toList());

        String errorMessage = String.join(", ", errors);
        ErrorMetaDataDto metaData = createErrorMetaData(HttpStatus.BAD_REQUEST.value(), errorMessage, request.getRequestURI(), "VAL001");
        log.error(errorMessage, ex);
        return createErrorResponse(metaData, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception ex, HttpServletRequest request) {
        ErrorMetaDataDto metaData = createErrorMetaData(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error", request.getRequestURI(), "SYS001");
        log.error(ex.getMessage(), ex);
        return createErrorResponse(metaData, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ErrorMetaDataDto createErrorMetaData(int statusCode, String message, String requestUri, String errorCode) {
        return ErrorMetaDataDto.createErrorMetaData(
                statusCode,
                message,
                requestUri,
                serverInfoConfig.getVersionNumber(),
                serverInfoConfig.getServerName(),
                errorCode
        );
    }

    private ResponseEntity<?> createErrorResponse(ErrorMetaDataDto metaData, HttpStatus status) {
        ResponseDto responseData = new ResponseDto(metaData, Collections.emptyList());
        return new ResponseEntity<>(responseData, status);
    }
}