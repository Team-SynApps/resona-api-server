package synapps.resona.api.global.dto.metadata;

import lombok.Getter;

@Getter
public class ErrorMeta extends Meta {

  private final String errorCode;

  public ErrorMeta(int status, String message, String path, String apiVersion,
      String serverName, String errorCode) {
    super(status, message, path, apiVersion, serverName);
    this.errorCode = errorCode;
  }

  public static ErrorMeta createErrorMetaData(int status, String message, String path,
      String apiVersion, String serverName, String errorCode) {
    return new ErrorMeta(status, message, path, apiVersion, serverName, errorCode);
  }
}
