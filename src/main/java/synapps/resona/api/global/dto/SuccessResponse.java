package synapps.resona.api.global.dto;

import synapps.resona.api.global.success.SuccessCode;

public class SuccessResponse<T> extends BaseResponse<T> {

  protected SuccessResponse(Meta meta, T result) {
    super(meta, result);
  }

  public static <T> SuccessResponse<T> of(SuccessCode code, RequestInfo info) {
    return new SuccessResponse<>(SuccessMeta.of(code, info), null);
  }

  public static <T> SuccessResponse<T> of(SuccessCode code, RequestInfo info, T result) {
    return new SuccessResponse<>(SuccessMeta.of(code, info), result);
  }
}