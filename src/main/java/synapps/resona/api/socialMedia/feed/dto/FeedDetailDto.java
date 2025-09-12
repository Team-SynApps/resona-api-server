package synapps.resona.api.socialMedia.feed.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import synapps.resona.api.socialMedia.feed.entity.Feed;

@Getter
@AllArgsConstructor
public class FeedDetailDto {
  private Feed feed;
  private long likeCount;
  private long commentCount;

  private boolean hasLiked;
  private boolean hasScraped;
}
