package com.synapps.resona.comment.repository.reply;

import static com.synapps.resona.comment.entity.QReply.reply;
import static com.synapps.resona.entity.member.QMember.member;
import static com.synapps.resona.restriction.entity.QBlock.block;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.synapps.resona.comment.dto.ReplyProjectionDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ReplyQueryRepository {

  private final JPAQueryFactory queryFactory;

  public List<ReplyProjectionDto> findAllRepliesByCommentId(Long viewerId, Long commentId) {
    return queryFactory
        .select(Projections.constructor(ReplyProjectionDto.class,
            reply,
            block.id.isNotNull()
        ))
        .from(reply)
        .join(reply.member, member).fetchJoin()
        .leftJoin(block).on(
            block.blocker.id.eq(viewerId)
                .and(block.blocked.id.eq(reply.member.id))
        )
        .where(
            reply.comment.id.eq(commentId),
            reply.deleted.isFalse()
        )
        .orderBy(reply.createdAt.asc())
        .fetch();
  }
}