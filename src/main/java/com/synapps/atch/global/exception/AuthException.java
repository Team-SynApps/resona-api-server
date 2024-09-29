package com.synapps.atch.global.exception;

import org.springframework.http.HttpStatus;

public class AuthException extends BaseException {
    public static final String INVALID_TOKEN = "AUTH001";
    public static final String EXPIRED_TOKEN = "AUTH002";
    public static final String INVALID_REFRESH_TOKEN = "AUTH003";

    public AuthException(String message, HttpStatus status, String errorCode) {
        super(message, status, errorCode);
    }

    public static AuthException invalidToken() {
        return new AuthException("Invalid token", HttpStatus.UNAUTHORIZED, INVALID_TOKEN);
    }

    public static AuthException expiredToken() {
        return new AuthException("Expired token", HttpStatus.UNAUTHORIZED, EXPIRED_TOKEN);
    }

    public static AuthException invalidAccessToken() {
        return new AuthException("Invalid access token", HttpStatus.UNAUTHORIZED, INVALID_TOKEN);
    }

    public static AuthException invalidRefreshToken() {
        return new AuthException("Invalid refresh token", HttpStatus.UNAUTHORIZED, INVALID_REFRESH_TOKEN);
    }

    public static AuthException refreshTokenNotFound() {
        return new AuthException("Refresh token not found", HttpStatus.UNAUTHORIZED, INVALID_REFRESH_TOKEN);
    }

    public static AuthException accessTokenNotFound() {
        return new AuthException("Access token not found", HttpStatus.UNAUTHORIZED, INVALID_TOKEN);
    }

}