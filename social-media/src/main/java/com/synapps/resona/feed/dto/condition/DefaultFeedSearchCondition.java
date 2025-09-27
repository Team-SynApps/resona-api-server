package com.synapps.resona.feed.dto.condition;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.synapps.resona.feed.dto.FeedSortBy;
import com.synapps.resona.feed.dto.request.FeedQueryRequest;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(staticName = "of")
public class DefaultFeedSearchCondition implements FeedQueryRequest {
  private Long memberId;
  private LocalDateTime cursor;
  private FeedSortBy sortBy = FeedSortBy.LATEST; // 기본값은 최신순
}
