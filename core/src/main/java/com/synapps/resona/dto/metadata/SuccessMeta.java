package com.synapps.resona.dto.metadata;

import com.synapps.resona.dto.RequestInfo;
import com.synapps.resona.dto.code.StatusCode;
import com.synapps.resona.dto.code.SuccessCode;
import lombok.Getter;

@Getter
public class SuccessMeta extends Meta {

  protected SuccessMeta(StatusCode code, RequestInfo info) {
    super(code.getStatusCode(), code.getMessage(), info);
  }

  public static SuccessMeta of(SuccessCode code, RequestInfo info) {
    return new SuccessMeta(code, info);
  }
}