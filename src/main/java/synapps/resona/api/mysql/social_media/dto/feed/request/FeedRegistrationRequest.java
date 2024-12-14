package synapps.resona.api.mysql.social_media.dto.feed.request;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import synapps.resona.api.external.file.dto.FileMetadataDto;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedRegistrationRequest {
    @Valid
    private List<FileMetadataDto> metadataList;

    @Valid
    private FeedRequest feedRequest;
}
