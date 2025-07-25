package synapps.resona.api.global.dto.metadata;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import synapps.resona.api.global.utils.DateTimeUtil;

@Getter
public class Meta {

  private final int status;
  private final String message;
  private final String timestamp;
  private final String path;
  private final String apiVersion;
  private final String serverName;
  private final String requestId;
//    private Long processingTime;

  public Meta(int status, String message, String path, String apiVersion,
      String serverName) {
    this.status = status;
    this.message = message;
    this.timestamp = DateTimeUtil.localDateTimeToStringSimpleFormat(LocalDateTime.now());
    this.path = path;
    this.apiVersion = apiVersion;
    this.serverName = serverName;
    this.requestId = generateRequestId();
  }

  private static String generateRequestId() {
    return UUID.randomUUID().toString();
  }

//    public void setProcessingTime(Long processingTime) {
//        this.processingTime = processingTime;
//    }

  public static Meta createSuccessMetaData(String path, String apiVersion,
      String serverName) {
    return new Meta(200, "Success", path, apiVersion, serverName);
  }

  public static Meta createErrorMetaData(int status, String message, String path,
      String apiVersion, String serverName) {
    return new Meta(status, message, path, apiVersion, serverName);
  }
}