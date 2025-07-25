package synapps.resona.api.global.dto;

import synapps.resona.api.global.error.core.ErrorCode;

public class ErrorResponse<T> extends BaseResponse<T>{

  protected ErrorResponse(Meta meta, T result) {
    super(meta, result);
  }

  public static <T> ErrorResponse<T> of(ErrorCode code, RequestInfo info) {
    return new ErrorResponse<>(SuccessMeta.of(code, info), null);
  }
}