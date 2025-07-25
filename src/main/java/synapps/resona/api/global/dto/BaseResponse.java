package synapps.resona.api.global.dto;


import com.fasterxml.jackson.annotation.JsonInclude;

public abstract class BaseResponse<T> {
  private Meta meta;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private final T result;

  protected BaseResponse(Meta meta, T result) {
    this.meta = meta;
    this.result = result;
  }
}
