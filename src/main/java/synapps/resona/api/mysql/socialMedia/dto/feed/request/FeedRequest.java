package synapps.resona.api.mysql.socialMedia.dto.feed.request;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import synapps.resona.api.mysql.socialMedia.dto.feed.ContentDeserializer;
import synapps.resona.api.mysql.socialMedia.dto.location.LocationRequest;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedRequest {

  @JsonDeserialize(using = ContentDeserializer.class)
  private String content;
  private String category;
  private LocationRequest location;
}
