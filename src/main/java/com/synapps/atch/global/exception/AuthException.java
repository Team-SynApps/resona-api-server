package com.synapps.atch.global.exception;

import org.springframework.http.HttpStatus;

public class AuthException extends BaseException {

    public AuthException(String message, HttpStatus status, String errorCode) {
        super(message, status, errorCode);
    }

    private static AuthException of(ErrorCode errorCode) {
        return new AuthException(errorCode.getMessage(), errorCode.getStatus(), errorCode.getCode());
    }

    public static AuthException invalidToken() {
        return of(ErrorCode.INVALID_TOKEN);
    }

    public static AuthException expiredToken() {
        return of(ErrorCode.EXPIRED_TOKEN);
    }

    public static AuthException invalidAccessToken() {
        return of(ErrorCode.INVALID_TOKEN);
    }

    public static AuthException invalidRefreshToken() {
        return of(ErrorCode.INVALID_REFRESH_TOKEN);
    }

    public static AuthException refreshTokenNotFound() {
        return of(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
    }

    public static AuthException accessTokenNotFound() {
        return of(ErrorCode.TOKEN_NOT_FOUND);
    }

}