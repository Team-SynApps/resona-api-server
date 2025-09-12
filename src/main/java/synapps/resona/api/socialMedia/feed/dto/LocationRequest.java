package synapps.resona.api.socialMedia.feed.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocationRequest {

  private String coordinate;
  private String address;
  private String name;
}
