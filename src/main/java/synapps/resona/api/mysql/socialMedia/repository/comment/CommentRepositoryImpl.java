package synapps.resona.api.mysql.socialMedia.repository.comment;

import static synapps.resona.api.mysql.socialMedia.entity.comment.QComment.comment;
import static synapps.resona.api.mysql.socialMedia.entity.comment.QReply.reply;
import static synapps.resona.api.mysql.socialMedia.entity.feed.QFeed.feed;
import static synapps.resona.api.mysql.socialMedia.entity.restriction.QBlock.block;
import static synapps.resona.api.mysql.socialMedia.entity.restriction.QCommentHide.commentHide;
import static synapps.resona.api.mysql.socialMedia.entity.restriction.QFeedHide.feedHide;
import static synapps.resona.api.mysql.socialMedia.entity.restriction.QReplyHide.replyHide;

import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import synapps.resona.api.mysql.socialMedia.entity.comment.Comment;

@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepositoryCustom {
  private final JPAQueryFactory queryFactory;

  @Override
  public List<Comment> findAllCommentsByFeedIdWithReplies(Long viewerId, Long feedId) {
    return queryFactory
        .select(comment)
        .from(comment)
        .join(comment.feed, feed).fetchJoin()
        .leftJoin(commentHide).on(
            commentHide.comment.id.eq(comment.id)
                .and(commentHide.member.id.eq(viewerId)))
        .leftJoin(block).on(
            block.blocker.id.eq(viewerId)
                .and(block.blocked.id.eq(comment.member.id))
        )
        .where(block.id.isNull(),
            commentHide.id.isNull(),
            comment.feed.id.eq(feedId))
        .fetch();
  }
}
