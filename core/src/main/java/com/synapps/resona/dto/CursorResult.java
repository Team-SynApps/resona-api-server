package com.synapps.resona.dto;

import java.util.List;
import lombok.Getter;

@Getter
public class CursorResult<T> {

  private final List<T> values;
  private final boolean hasNext;
  private final String cursor;

  public CursorResult(List<T> values, boolean hasNext, String cursor) {
    this.values = values;
    this.hasNext = hasNext;
    this.cursor = cursor;
  }
}
