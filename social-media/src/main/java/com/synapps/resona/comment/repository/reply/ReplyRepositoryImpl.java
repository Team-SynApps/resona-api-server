package com.synapps.resona.comment.repository.reply;

import static com.synapps.resona.comment.entity.QComment.comment;
import static com.synapps.resona.comment.entity.QReply.reply;
import static com.synapps.resona.restriction.entity.QBlock.block;
import static com.synapps.resona.restriction.entity.QReplyHide.replyHide;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.synapps.resona.comment.entity.Reply;
import java.util.List;
import lombok.RequiredArgsConstructor;

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
