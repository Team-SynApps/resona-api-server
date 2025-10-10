package com.synapps.resona.dto.metadata;

import com.synapps.resona.dto.RequestInfo;
import com.synapps.resona.dto.code.ErrorCode;
import lombok.Getter;

@Getter
public class ErrorMeta extends Meta {

  private final String customErrorCode;

  protected ErrorMeta(ErrorCode code, RequestInfo info) {
    super(code.getStatusCode(), code.getMessage(), info);
    this.customErrorCode = code.getCustomCode();
  }

  public static ErrorMeta of(ErrorCode code, RequestInfo info) {
    return new ErrorMeta(code, info);
  }
}
