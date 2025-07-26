package synapps.resona.api.global.dto;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import synapps.resona.api.global.utils.DateTimeUtil;

@Getter
public abstract class Meta {

  private final int status;

  private final String message;

  private final String timestamp;

  private final String path;

  private final String apiVersion;

  private final String serverName;

  private final String requestId;

  protected Meta(int status, String message, RequestInfo info) {
    this.status = status;
    this.message = message;
    this.timestamp = DateTimeUtil.localDateTimeToStringSimpleFormat(LocalDateTime.now());
    this.path = info.path();
    this.requestId = generateRequestId();
    this.apiVersion = info.apiVersion();
    this.serverName = info.serverName();
  }

  private static String generateRequestId() {
    return UUID.randomUUID().toString();
  }

  public static Meta of(StatusCode code, RequestInfo info) {
    return new SuccessMeta(code, info);
  }
}
