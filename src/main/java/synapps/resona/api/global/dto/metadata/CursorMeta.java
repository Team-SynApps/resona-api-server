package synapps.resona.api.global.dto.metadata;

import lombok.Getter;
import synapps.resona.api.global.dto.RequestInfo;
import synapps.resona.api.global.dto.code.SuccessCode;

@Getter
public class CursorMeta extends Meta {
  private final String cursor;
  private final int size;
  private final boolean hasNext;

  protected CursorMeta(SuccessCode code, RequestInfo info, String cursor, int size, boolean hasNext) {
    super(code.getStatusCode(), code.getMessage(), info);
    this.cursor = cursor;
    this.size = size;
    this.hasNext = hasNext;
  }

  public static CursorMeta of(SuccessCode code, RequestInfo info,
      String cursor, int size, boolean hasNext) {
    return new CursorMeta(code, info, cursor, size, hasNext);
  }
}
