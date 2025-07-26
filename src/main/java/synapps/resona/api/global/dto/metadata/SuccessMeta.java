package synapps.resona.api.global.dto.metadata;

import lombok.Getter;
import synapps.resona.api.global.dto.RequestInfo;
import synapps.resona.api.global.dto.code.StatusCode;
import synapps.resona.api.global.dto.code.SuccessCode;

@Getter
public class SuccessMeta extends Meta {

  protected SuccessMeta(StatusCode code, RequestInfo info) {
    super(code.getStatusCode(), code.getMessage(), info);
  }

  public static SuccessMeta of(SuccessCode code, RequestInfo info) {
    return new SuccessMeta(code, info);
  }
}