package com.synapps.resona.dto.response;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.synapps.resona.dto.metadata.Meta;
import lombok.Getter;

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
