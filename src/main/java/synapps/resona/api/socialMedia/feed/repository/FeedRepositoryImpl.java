package synapps.resona.api.socialMedia.feed.repository;

import static synapps.resona.api.socialMedia.entity.feed.QFeed.feed;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import synapps.resona.api.socialMedia.feed.dto.FeedDetailDto;
import synapps.resona.api.socialMedia.feed.repository.dsl.FeedExpressions;

@RequiredArgsConstructor
public class FeedRepositoryImpl implements FeedRepositoryCustom {
  private final JPAQueryFactory queryFactory;
  private final FeedExpressions feedExpressions;

  @Override
  public FeedDetailDto findFeedDetailById(Long feedId, Long viewerId) {
    return queryFactory
        .select(Projections.constructor(FeedDetailDto.class,
            feed,
            feedExpressions.getLikeCount(feed),
            feedExpressions.getCommentCount(feed),
            feedExpressions.isLiked(feed, viewerId),
            feedExpressions.isScraped(feed, viewerId)
        ))
        .from(feed)
        .where(feed.id.eq(feedId))
        .fetchOne();
  }
}