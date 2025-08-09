package synapps.resona.api.socialMedia.dto.feed.request;

import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import synapps.resona.api.external.file.dto.FileMetadataDto;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedRegistrationRequest {

  @Valid
  private List<FileMetadataDto> metadataList;

  @Valid
  private FeedRequest feedRequest;
}
