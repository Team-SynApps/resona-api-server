package synapps.resona.api.external.file.dto;

import lombok.*;

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileMetadataDto {
    private String originalFileName;
    private String temporaryFileName;
    private String uploadTime;
    private String contentType;
    private int width;
    private int height;
    private long fileSize;
    private int index;
}