package com.synapps.resona.comment.repository.comment;


import static com.synapps.resona.comment.entity.QComment.comment;
import static com.synapps.resona.comment.entity.QReply.reply;
import static com.synapps.resona.likes.entity.QCommentLikes.commentLikes;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.synapps.resona.comment.dto.CommentProjectionDto;
import com.synapps.resona.comment.dto.ReplyProjectionDto;
import com.synapps.resona.comment.entity.Comment;
import com.synapps.resona.entity.member.QMember;
import com.synapps.resona.restriction.entity.QBlock;
import com.synapps.resona.restriction.entity.QCommentHide;
import com.synapps.resona.restriction.entity.QReplyHide;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
@Repository
@RequiredArgsConstructor
public class CommentQueryRepository {
  private final JPAQueryFactory queryFactory;

  public Optional<CommentProjectionDto> findCommentProjectionById(Long viewerId, Long commentId) {
    QMember commentMember = new QMember("commentMember");
    QBlock block = new QBlock("block");
    QCommentHide commentHide = new QCommentHide("commentHide");

    // 댓글 단건 정보 조회 (대댓글 제외)
    Tuple commentTuple = queryFactory
        .select(
            comment,
            block.id.isNotNull(),
            commentHide.id.isNotNull(),
            JPAExpressions.select(commentLikes.count())
                .from(commentLikes)
                .where(commentLikes.comment.eq(comment))
        )
        .from(comment)
        .join(comment.member, commentMember).fetchJoin()
        .leftJoin(commentMember.profile).fetchJoin()
        .leftJoin(commentHide).on(
            commentHide.comment.id.eq(comment.id)
                .and(commentHide.member.id.eq(viewerId)))
        .leftJoin(block).on(
            block.blocker.id.eq(viewerId)
                .and(block.blocked.id.eq(commentMember.id))
        )
        .where(comment.id.eq(commentId))
        .fetchOne();

    // 조회된 댓글이 없으면 빈 Optional 반환
    if (commentTuple == null) {
      return Optional.empty();
    }

    Comment foundComment = commentTuple.get(comment);
    boolean isBlocked = Boolean.TRUE.equals(commentTuple.get(1, Boolean.class));
    boolean isHidden = Boolean.TRUE.equals(commentTuple.get(2, Boolean.class));
    Long likeCount = commentTuple.get(3, Long.class);

    // 해당 댓글의 대댓글 목록 조회
    QMember replyMember = new QMember("replyMember");
    QBlock replyBlock = new QBlock("replyBlock");
    QReplyHide replyHide = new QReplyHide("replyHide");

    assert foundComment != null;
    List<ReplyProjectionDto> replies = queryFactory
        .select(Projections.constructor(ReplyProjectionDto.class,
            reply,
            replyBlock.id.isNotNull(),
            replyHide.id.isNotNull()
        ))
        .from(reply)
        .join(reply.member, replyMember).fetchJoin()
        .leftJoin(replyMember.profile).fetchJoin()
        .leftJoin(replyBlock).on(
            replyBlock.blocker.id.eq(viewerId)
                .and(replyBlock.blocked.id.eq(replyMember.id))
        )
        .leftJoin(replyHide).on(
            replyHide.reply.id.eq(reply.id)
                .and(replyHide.member.id.eq(viewerId))
        )
        .where(reply.comment.id.eq(foundComment.getId()))
        .orderBy(reply.createdAt.asc())
        .fetch();

    // 댓글과 대댓글 조합
    CommentProjectionDto resultDto = new CommentProjectionDto(
        foundComment,
        isBlocked,
        isHidden,
        likeCount,
        replies
    );

    return Optional.of(resultDto);
  }

  public List<CommentProjectionDto> findAllCommentsByFeedIdWithReplies(Long viewerId, Long feedId) {
    QMember commentMember = new QMember("commentMember");
    QMember replyMember = new QMember("replyMember");
    QBlock block = new QBlock("block");
    QBlock replyBlock = new QBlock("replyBlock");
    QCommentHide commentHide = new QCommentHide("commentHide");
    QReplyHide replyHide = new QReplyHide("replyHide");

    List<Tuple> commentTuples = queryFactory
        .select(
            comment,
            block.id.isNotNull(),
            commentHide.id.isNotNull(),
            JPAExpressions.select(commentLikes.count())
                .from(commentLikes)
                .where(commentLikes.comment.eq(comment))
        )
        .from(comment)
        .join(comment.member, commentMember).fetchJoin()
        .leftJoin(commentMember.profile).fetchJoin()
        .leftJoin(commentHide).on(
            commentHide.comment.id.eq(comment.id)
                .and(commentHide.member.id.eq(viewerId)))
        .leftJoin(block).on(
            block.blocker.id.eq(viewerId)
                .and(block.blocked.id.eq(commentMember.id))
        )
        .where(comment.feed.id.eq(feedId))
        .orderBy(comment.createdAt.asc())
        .fetch();

    if (commentTuples.isEmpty()) {
      return Collections.emptyList();
    }

    List<Long> commentIds = commentTuples.stream()
        .map(tuple -> Objects.requireNonNull(tuple.get(comment)).getId())
        .collect(Collectors.toList());

    List<Tuple> replyTuples = queryFactory
        .select(
            reply.comment.id,
            Projections.constructor(ReplyProjectionDto.class,
                reply,
                replyBlock.id.isNotNull(),
                replyHide.id.isNotNull()
            )
        )
        .from(reply)
        .join(reply.member, replyMember).fetchJoin()
        .leftJoin(replyMember.profile).fetchJoin()
        .leftJoin(replyBlock).on(
            replyBlock.blocker.id.eq(viewerId)
                .and(replyBlock.blocked.id.eq(replyMember.id))
        )
        .leftJoin(replyHide).on(
            replyHide.reply.id.eq(reply.id)
                .and(replyHide.member.id.eq(viewerId))
        )
        .where(reply.comment.id.in(commentIds))
        .orderBy(reply.createdAt.asc())
        .fetch();

    Map<Long, List<ReplyProjectionDto>> repliesByCommentId = replyTuples.stream()
        .collect(Collectors.groupingBy(
            tuple -> tuple.get(0, Long.class),
            Collectors.mapping(tuple -> tuple.get(1, ReplyProjectionDto.class), Collectors.toList())
        ));

    return commentTuples.stream()
        .map(tuple -> {
          Comment currentComment = tuple.get(comment);
          boolean isBlocked = Boolean.TRUE.equals(tuple.get(1, Boolean.class));
          boolean isHidden = Boolean.TRUE.equals(tuple.get(2, Boolean.class));
          Long likeCount = tuple.get(3, Long.class);
          assert currentComment != null;
          List<ReplyProjectionDto> replies = repliesByCommentId.getOrDefault(
              currentComment.getId(), Collections.emptyList()
          );
          return new CommentProjectionDto(currentComment, isBlocked, isHidden, likeCount, replies);
        })
        .collect(Collectors.toList());
  }
}