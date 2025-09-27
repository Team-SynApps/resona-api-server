package com.synapps.resona.domain.repository.feed;

import static com.synapps.resona.feed.entity.QFeed.feed;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.synapps.resona.query.dto.feed.FeedDetailDto;
import com.synapps.resona.query.dto.feed.FeedMetaData;
import com.synapps.resona.domain.entity.feed.Feed;
import com.synapps.resona.domain.repository.feed.dsl.FeedExpressions;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FeedRepositoryImpl implements FeedRepositoryCustom {
  private final JPAQueryFactory queryFactory;
  private final FeedExpressions feedExpressions;

  @Override
  public FeedDetailDto findFeedDetailById(Long feedId, Long viewerId) {
    Feed feedEntity = queryFactory
        .selectFrom(feed)
        .where(feed.id.eq(feedId))
        .fetchOne();

    if (feedEntity == null) {
      return null;
    }

    FeedMetaData metaData = feedExpressions.fetchFeedMetaData(feedId, viewerId);

    return FeedDetailDto.of(feedEntity, metaData);
  }
}