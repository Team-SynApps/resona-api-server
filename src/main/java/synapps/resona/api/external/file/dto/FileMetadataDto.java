package synapps.resona.api.external.file.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class FileMetadataDto {
    private String originalFileName;
    private String temporaryFileName;
    private String finalFileName;
    private String uploadTime;
    private String contentType;
    private long fileSize;
}