package synapps.resona.api.socialMedia.dto.feed.condition;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import synapps.resona.api.socialMedia.dto.feed.FeedSortBy;
import synapps.resona.api.socialMedia.dto.feed.request.FeedQueryRequest;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(staticName = "of")
public class DefaultFeedSearchCondition implements FeedQueryRequest {
  private Long memberId;
  private LocalDateTime cursor;
  private FeedSortBy sortBy = FeedSortBy.LATEST; // 기본값은 최신순
}
