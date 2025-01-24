package synapps.resona.api.global.dto.metadata;

import lombok.Getter;

@Getter
public class CursorBasedMetaDataDto extends MetaDataDto {
    private final String cursor;
    private final int size;
    private final boolean hasNext;

    public CursorBasedMetaDataDto(int status, String message, String path, String apiVersion, String serverName,
                                  String cursor, int size, boolean hasNext) {
        super(status, message, path, apiVersion, serverName);
        this.cursor = cursor;
        this.size = size;
        this.hasNext = hasNext;
    }

    public static CursorBasedMetaDataDto createSuccessMetaData(String path, String apiVersion,
                                                                        String serverName, String cursor, int size,
                                                                        boolean hasNext) {
        return new CursorBasedMetaDataDto(200, "Success", path, apiVersion, serverName,
                cursor, size, hasNext);
    }
}