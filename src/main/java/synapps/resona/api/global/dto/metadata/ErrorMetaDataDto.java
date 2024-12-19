package synapps.resona.api.global.dto.metadata;

import lombok.Getter;

@Getter
public class ErrorMetaDataDto extends MetaDataDto {
    private final String errorCode;

    public ErrorMetaDataDto(int status, String message, String path, String apiVersion, String serverName, String errorCode) {
        super(status, message, path, apiVersion, serverName);
        this.errorCode = errorCode;
    }

    public static ErrorMetaDataDto createErrorMetaData(int status, String message, String path, String apiVersion, String serverName, String errorCode) {
        return new ErrorMetaDataDto(status, message, path, apiVersion, serverName, errorCode);
    }
}
