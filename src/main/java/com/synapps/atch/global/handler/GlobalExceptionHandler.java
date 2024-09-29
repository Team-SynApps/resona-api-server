package com.synapps.atch.global.handler;

import com.synapps.atch.global.config.ServerInfoConfig;
import com.synapps.atch.global.dto.ErrorMetaDataDto;
import com.synapps.atch.global.dto.ResponseDto;
import com.synapps.atch.global.exception.AuthException;
import com.synapps.atch.global.exception.BaseException;
import com.synapps.atch.mysql.member.exception.MemberException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Collections;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private final ServerInfoConfig serverInfoConfig;

    @Autowired
    public GlobalExceptionHandler(ServerInfoConfig serverInfoConfig) {
        this.serverInfoConfig = serverInfoConfig;
    }

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<?> handleBaseException(BaseException ex, HttpServletRequest request) {
        ErrorMetaDataDto metaData = ErrorMetaDataDto.createErrorMetaData(
                ex.getStatus().value(),
                ex.getMessage(),
                request.getRequestURI(),
                serverInfoConfig.getVersionNumber(), // apiVersion
                serverInfoConfig.getServerName(),
                ex.getErrorCode()
        );
        ResponseDto responseData = new ResponseDto(metaData, Collections.emptyList());
        return new ResponseEntity<>(responseData, ex.getStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String errorMessage = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        ErrorMetaDataDto metaData = ErrorMetaDataDto.createErrorMetaData(
                HttpStatus.BAD_REQUEST.value(),
                errorMessage,
                request.getRequestURI(),
                serverInfoConfig.getVersionNumber(),
                serverInfoConfig.getServerName(),
                "VAL001"
        );
        ResponseDto responseData = new ResponseDto(metaData, Collections.emptyList());
        return new ResponseEntity<>(responseData, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception ex, HttpServletRequest request) {
        ErrorMetaDataDto metaData = ErrorMetaDataDto.createErrorMetaData(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal server error",
                request.getRequestURI(),
                serverInfoConfig.getVersionNumber(),
                serverInfoConfig.getServerName(),
                "SYS001"
        );
        ResponseDto responseData = new ResponseDto(metaData, Collections.emptyList());
        return new ResponseEntity<>(responseData, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MemberException.class)
    public ResponseEntity<?> handleMemberException(MemberException ex, HttpServletRequest request) {
        ErrorMetaDataDto metaData = ErrorMetaDataDto.createErrorMetaData(
                ex.getStatus().value(),
                ex.getMessage(),
                request.getRequestURI(),
                serverInfoConfig.getVersionNumber(),
                serverInfoConfig.getServerName(),
                ex.getErrorCode()
        );
        ResponseDto responseData = new ResponseDto(metaData, Collections.emptyList());
        return new ResponseEntity<>(responseData, ex.getStatus());
    }

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<?> handleAuthException(AuthException ex, HttpServletRequest request) {
        ErrorMetaDataDto metaData = ErrorMetaDataDto.createErrorMetaData(
                ex.getStatus().value(),
                ex.getMessage(),
                request.getRequestURI(),
                serverInfoConfig.getVersionNumber(),
                serverInfoConfig.getServerName(),
                ex.getErrorCode()
        );
        ResponseDto responseData = new ResponseDto(metaData, Collections.emptyList());
        return new ResponseEntity<>(responseData, ex.getStatus());
    }
}