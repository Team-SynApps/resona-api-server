package synapps.resona.api.socialMedia.feed.dto.request;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import synapps.resona.api.socialMedia.feed.dto.ContentDeserializer;
import synapps.resona.api.socialMedia.feed.dto.LocationRequest;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedRequest {

  @JsonDeserialize(using = ContentDeserializer.class)
  private String content;
  private String category;
  private LocationRequest location;
  private String languageCode;
}
