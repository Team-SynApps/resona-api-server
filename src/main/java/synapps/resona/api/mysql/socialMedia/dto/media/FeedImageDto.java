package synapps.resona.api.mysql.socialMedia.dto.media;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import synapps.resona.api.mysql.socialMedia.entity.media.FeedMedia;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedImageDto {

  private String url;
  private int index;

  public static FeedImageDto from(FeedMedia media) {
    return FeedImageDto.builder()
        .index(media.getIndex())
        .url(media.getUrl())
        .build();
  }
}