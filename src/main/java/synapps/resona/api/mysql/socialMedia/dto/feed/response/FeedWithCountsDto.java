package synapps.resona.api.mysql.socialMedia.dto.feed.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import synapps.resona.api.mysql.socialMedia.entity.feed.Feed;

@Getter
@AllArgsConstructor
public class FeedWithCountsDto {
  private Feed feed;
  private long likeCount;
  private long totalCommentCount;
}
