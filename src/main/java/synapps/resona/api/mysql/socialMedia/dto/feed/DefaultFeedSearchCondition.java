package synapps.resona.api.mysql.socialMedia.dto.feed;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import synapps.resona.api.mysql.socialMedia.dto.feed.request.FeedQueryRequest;

@Getter
@Setter
public class DefaultFeedSearchCondition implements FeedQueryRequest {
  private Long memberId;
  private LocalDateTime cursor;
  private FeedSortBy sortBy = FeedSortBy.LATEST; // 기본값은 최신순
}
