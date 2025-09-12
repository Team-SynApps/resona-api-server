package synapps.resona.api.socialMedia.feed.repository;

import static synapps.resona.api.socialMedia.feed.entity.QFeed.feed;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import synapps.resona.api.socialMedia.feed.dto.FeedDetailDto;
import synapps.resona.api.socialMedia.feed.dto.FeedMetaData;
import synapps.resona.api.socialMedia.feed.entity.Feed;
import synapps.resona.api.socialMedia.feed.repository.dsl.FeedExpressions;

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