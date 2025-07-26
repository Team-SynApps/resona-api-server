package synapps.resona.api.global.dto.response;

import lombok.Getter;
import synapps.resona.api.global.dto.metadata.ErrorMeta;
import synapps.resona.api.global.dto.metadata.Meta;
import synapps.resona.api.global.dto.RequestInfo;
import synapps.resona.api.global.dto.code.ErrorCode;

@Getter
public class ErrorResponse<T> extends BaseResponse<T>{

  protected ErrorResponse(Meta meta, T result) {
    super(meta, result);
  }

  public static <T> ErrorResponse<T> of(ErrorCode code, RequestInfo info) {
    return new ErrorResponse<>(ErrorMeta.of(code, info), null);
  }

  public static <T> ErrorResponse<T> of(ErrorCode code, RequestInfo info, T result) {
    return new ErrorResponse<>(ErrorMeta.of(code, info), result);
  }
}
