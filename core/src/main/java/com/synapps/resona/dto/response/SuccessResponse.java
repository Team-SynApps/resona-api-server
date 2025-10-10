package com.synapps.resona.dto.response;

import com.synapps.resona.dto.RequestInfo;
import com.synapps.resona.dto.code.SuccessCode;
import com.synapps.resona.dto.metadata.CursorMeta;
import com.synapps.resona.dto.metadata.Meta;
import com.synapps.resona.dto.metadata.SuccessMeta;
import lombok.Getter;

@Getter
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

  public static <T> SuccessResponse<T> of(SuccessCode code, RequestInfo info, T result, String cursor, Integer size, boolean hasNext) {
    return new SuccessResponse<>(CursorMeta.of(code, info, cursor, size, hasNext), result);
  }
}