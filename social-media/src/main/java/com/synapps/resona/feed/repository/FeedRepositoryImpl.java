package com.synapps.resona.feed.repository;

import static com.synapps.resona.feed.entity.QFeed.feed;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.synapps.resona.feed.dto.FeedDetailDto;
import com.synapps.resona.feed.dto.FeedMetaData;
import com.synapps.resona.feed.entity.Feed;
import com.synapps.resona.feed.repository.dsl.FeedExpressions;
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