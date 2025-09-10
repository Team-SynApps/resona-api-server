package synapps.resona.api.socialMedia.repository.comment.reply;

import static synapps.resona.api.socialMedia.entity.comment.QComment.comment;
import static synapps.resona.api.socialMedia.entity.comment.QReply.reply;
import static synapps.resona.api.socialMedia.entity.restriction.QBlock.block;
import static synapps.resona.api.socialMedia.entity.restriction.QReplyHide.replyHide;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import synapps.resona.api.socialMedia.entity.comment.Reply;

@RequiredArgsConstructor
public class ReplyRepositoryImpl implements ReplyRepositoryCustom {
  private final JPAQueryFactory queryFactory;

  @Override
  public List<Reply> findAllRepliesByCommentId(Long viewerId, Long commentId) {
    return queryFactory
        .select(reply)
        .from(reply)
        .join(reply.comment, comment).fetchJoin()
        .leftJoin(replyHide).on(
            replyHide.reply.id.eq(reply.id)
                .and(replyHide.member.id.eq(viewerId)))
        .leftJoin(block).on(
            block.blocker.id.eq(viewerId)
                .and(block.blocked.id.eq(reply.member.id))
        )
        .where(block.id.isNull(),
            replyHide.id.isNull(),
            reply.comment.id.eq(commentId))
        .fetch();
  }
}
