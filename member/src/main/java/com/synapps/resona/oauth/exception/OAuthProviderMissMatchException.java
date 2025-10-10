package com.synapps.resona.oauth.exception;

import com.synapps.resona.error.GlobalErrorCode;
import com.synapps.resona.error.exception.BaseException;
import org.springframework.http.HttpStatus;

public class OAuthProviderMissMatchException extends BaseException {

  protected OAuthProviderMissMatchException(String message, HttpStatus status, String errorCode) {
    super(message, status, errorCode);
  }

  private static OAuthProviderMissMatchException of(GlobalErrorCode globalErrorCode) {
    return new OAuthProviderMissMatchException(globalErrorCode.getMessage(), globalErrorCode.getStatus(),
        globalErrorCode.getCustomCode());
  }


}