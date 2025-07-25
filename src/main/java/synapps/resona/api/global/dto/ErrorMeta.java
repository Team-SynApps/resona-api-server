package synapps.resona.api.global.dto;

import synapps.resona.api.global.error.core.ErrorCode;

public class ErrorMeta extends Meta {

  private final String customErrorCode;

  protected ErrorMeta(ErrorCode code, RequestInfo info) {
    super(code.getStatusCode(),code.getMessage(), info);
    this.customErrorCode = code.getCustomCode();
  }

  public static ErrorMeta of(ErrorCode code, RequestInfo info) {
    return new ErrorMeta(code, info);
  }
}
