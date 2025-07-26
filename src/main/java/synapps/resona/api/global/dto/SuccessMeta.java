package synapps.resona.api.global.dto;

import lombok.Getter;
import synapps.resona.api.global.success.SuccessCode;

@Getter
public class SuccessMeta extends Meta {

  protected SuccessMeta(StatusCode code, RequestInfo info) {
    super(code.getStatusCode(), code.getMessage(), info);
  }

  public static SuccessMeta of(SuccessCode code, RequestInfo info) {
    return new SuccessMeta(code, info);
  }
}