package synapps.resona.api.mysql.socialMedia.dto.feed.condition;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import synapps.resona.api.mysql.socialMedia.dto.feed.FeedSortBy;
import synapps.resona.api.mysql.socialMedia.dto.feed.request.FeedQueryRequest;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(staticName = "of")
public class MemberFeedSearchCondition implements FeedQueryRequest {
  private Long viewerId; // 조회를 요청한 사람
  private Long targetMemberId; // 피드 목록의 주인
  private LocalDateTime cursor;
  private FeedSortBy sortBy = FeedSortBy.LATEST;
}