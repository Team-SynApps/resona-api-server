package com.synapps.resona.dto.response;

import com.synapps.resona.dto.RequestInfo;
import com.synapps.resona.dto.code.ErrorCode;
import com.synapps.resona.dto.metadata.ErrorMeta;
import com.synapps.resona.dto.metadata.Meta;
import lombok.Getter;

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
