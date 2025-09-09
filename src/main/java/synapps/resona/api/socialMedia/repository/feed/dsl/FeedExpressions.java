package synapps.resona.api.socialMedia.repository.feed.dsl;

import static synapps.resona.api.socialMedia.entity.comment.QReply.reply;
import static synapps.resona.api.socialMedia.entity.feed.QLikes.likes;
import static synapps.resona.api.socialMedia.entity.feed.QScrap.scrap;
import static synapps.resona.api.socialMedia.entity.comment.QComment.comment;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.stereotype.Component;
import synapps.resona.api.socialMedia.entity.feed.QFeed;

@Component
public class FeedExpressions {

  public BooleanExpression isLiked(QFeed qFeed, Long viewerId) {
    if (viewerId == null) {
      return Expressions.asBoolean(false);
    }
    return JPAExpressions
        .selectOne()
        .from(likes)
        .where(likes.feed.eq(qFeed).and(likes.member.id.eq(viewerId)))
        .exists();
  }

  public BooleanExpression isScraped(QFeed qFeed, Long viewerId) {
    if (viewerId == null) {
      return Expressions.asBoolean(false);
    }
    return JPAExpressions
        .selectOne()
        .from(scrap)
        .where(scrap.feed.eq(qFeed).and(scrap.member.id.eq(viewerId)))
        .exists();
  }

  public JPQLQuery<Long> getLikeCount(QFeed qFeed) {
    return JPAExpressions.select(likes.id.count())
        .from(likes)
        .where(likes.feed.eq(qFeed));
  }

  public JPQLQuery<Long> getCommentCount(QFeed qFeed) {
    return JPAExpressions
        .select(comment.id.countDistinct().add(reply.id.count()))
        .from(comment)
        .leftJoin(reply).on(reply.comment.eq(comment))
        .where(comment.feed.eq(qFeed));
  }
}
