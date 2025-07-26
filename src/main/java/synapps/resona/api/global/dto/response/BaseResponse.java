package synapps.resona.api.global.dto.response;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import synapps.resona.api.global.dto.metadata.Meta;

@Getter
public abstract class BaseResponse<T> {
  private Meta meta;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private final T data;

  protected BaseResponse(Meta meta, T data) {
    this.meta = meta;
    this.data = data;
  }
}
