package synapps.resona.api.mysql.socialMedia.dto.feed.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import synapps.resona.api.mysql.socialMedia.dto.media.FeedImageDto;
import synapps.resona.api.mysql.socialMedia.entity.feed.Feed;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedReadResponse {

  private String id;
  private String content;
  private String createdAt;
  private List<FeedImageDto> feedImageDtos;

  public static FeedReadResponse from(Feed feed) {
    return FeedReadResponse.builder()
        .id(feed.getId().toString())
        .content(feed.getContent())
        .createdAt(feed.getCreatedAt().toString())
        .feedImageDtos(feed.getImages().stream().map(FeedImageDto::from).toList())
        .build();
  }
}
