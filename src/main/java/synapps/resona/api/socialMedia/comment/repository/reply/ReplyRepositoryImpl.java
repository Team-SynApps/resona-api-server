package synapps.resona.api.socialMedia.comment.repository.reply;


import static synapps.resona.api.socialMedia.comment.entity.QReply.reply;
import static synapps.resona.api.socialMedia.comment.entity.QComment.comment;
import static synapps.resona.api.socialMedia.restriction.entity.QReplyHide.replyHide;
import static synapps.resona.api.socialMedia.restriction.entity.QBlock.block;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import synapps.resona.api.socialMedia.comment.entity.Reply;

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
