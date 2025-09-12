package synapps.resona.api.socialMedia.feed.repository.dsl;

import static synapps.resona.api.socialMedia.comment.entity.QReply.reply;
import static synapps.resona.api.socialMedia.feed.entity.QScrap.scrap;
import static synapps.resona.api.socialMedia.likes.entity.QFeedLikes.feedLikes;
import static synapps.resona.api.socialMedia.comment.entity.QComment.comment;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import synapps.resona.api.socialMedia.feed.dto.FeedMetaData;
import synapps.resona.api.socialMedia.feed.entity.QFeed;

@Component
@RequiredArgsConstructor
public class FeedExpressions {

  private final JPAQueryFactory queryFactory;

  public BooleanExpression isLiked(QFeed qFeed, Long viewerId) {
    if (viewerId == null) {
      return Expressions.asBoolean(false);
    }
    return JPAExpressions
        .selectOne()
        .from(feedLikes)
        .where(feedLikes.feed.eq(qFeed).and(feedLikes.member.id.eq(viewerId)).and(feedLikes.deleted.isFalse()))
        .exists();
  }

  public BooleanExpression isScraped(QFeed qFeed, Long viewerId) {
    if (viewerId == null) {
      return Expressions.asBoolean(false);
    }
    return JPAExpressions
        .selectOne()
        .from(scrap)
        .where(scrap.feed.eq(qFeed).and(scrap.member.id.eq(viewerId)).and(scrap.deleted.isFalse()))
        .exists();
  }

  public JPQLQuery<Long> getLikeCount(QFeed qFeed) {
    return JPAExpressions.select(feedLikes.id.count())
        .from(feedLikes)
        .where(
            feedLikes.feed.eq(qFeed),
            feedLikes.deleted.isFalse()
        );
  }

  public JPQLQuery<Long> getCommentCount(QFeed qFeed) {
    return JPAExpressions
        .select(comment.id.countDistinct().add(reply.id.count()))
        .from(comment)
        .leftJoin(reply).on(
            reply.comment.eq(comment)
                .and(reply.deleted.isFalse())
        )
        .where(
            comment.feed.eq(qFeed),
            comment.deleted.isFalse()
        );
  }

  public long fetchCommentCount(Long feedId) {
    QFeed feed = QFeed.feed;

    Long count = queryFactory
        .select(getCommentCount(feed))
        .from(feed)
        .where(feed.id.eq(feedId))
        .fetchOne();

    return count != null ? count : 0L;
  }

  public long fetchCommentCountByCommentId(Long commentId) {
    QFeed feed = QFeed.feed;

    Long count = queryFactory
        .select(getCommentCount(feed))
        .from(feed)
        .where(feed.id.eq(
            JPAExpressions
                .select(comment.feed.id)
                .from(comment)
                .where(comment.id.eq(commentId))
        ))
        .fetchOne();

    return count != null ? count : 0L;
  }

  public FeedMetaData fetchFeedMetaData(Long feedId, Long viewerId) {
    QFeed feed = QFeed.feed;

    return queryFactory
        .select(Projections.constructor(FeedMetaData.class,
            getLikeCount(feed),
            getCommentCount(feed),
            isLiked(feed, viewerId),
            isScraped(feed, viewerId)
        ))
        .from(feed)
        .where(feed.id.eq(feedId).and(feed.deleted.isFalse()))
        .fetchOne();
  }
}
