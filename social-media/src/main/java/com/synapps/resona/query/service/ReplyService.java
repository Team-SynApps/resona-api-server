package com.synapps.resona.query.service;

import com.synapps.resona.query.dto.comment.ReplyDto;
import com.synapps.resona.query.dto.comment.ReplyProjectionDto;
import com.synapps.resona.query.dto.comment.request.ReplyRequest;
import com.synapps.resona.query.dto.comment.request.ReplyUpdateRequest;
import com.synapps.resona.query.dto.comment.response.CommentDeleteResponse;
import com.synapps.resona.domain.entity.comment.Comment;
import com.synapps.resona.domain.entity.comment.ContentDisplayStatus;
import com.synapps.resona.domain.entity.comment.Reply;
import com.synapps.resona.exception.CommentException;
import com.synapps.resona.exception.ReplyException;
import com.synapps.resona.domain.repository.comment.comment.CommentRepository;
import com.synapps.resona.domain.repository.comment.reply.ReplyQueryRepository;
import com.synapps.resona.domain.repository.comment.reply.ReplyRepository;
import com.synapps.resona.dto.MemberDto;
import com.synapps.resona.entity.member.Member;
import com.synapps.resona.domain.repository.feed.dsl.FeedExpressions;
import com.synapps.resona.repository.member.MemberRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReplyService {

  private final ReplyQueryRepository replyQueryRepository;
  private final ReplyProcessor replyProcessor;
  private final ReplyRepository replyRepository;
  private final CommentRepository commentRepository;
  private final MemberRepository memberRepository;

  private final FeedExpressions feedExpressions;

  @Transactional
  public ReplyDto register(ReplyRequest request, MemberDto memberDto) {
    Member member = memberRepository.findById(memberDto.getId()).orElseThrow();
    Comment comment = commentRepository.findById(request.getCommentId())
        .orElseThrow(CommentException::commentNotFound);
    comment.addReply();
    Reply reply = Reply.of(comment, member, request.getLanguageCode(), request.getContent());
    replyRepository.save(reply);

    return ReplyDto.of(reply, ContentDisplayStatus.NORMAL, reply.getContent());
  }

  @Transactional
  public ReplyDto update(ReplyUpdateRequest request) {
    Reply reply = replyRepository.findWithCommentAndMemberById(request.getReplyId())
        .orElseThrow(ReplyException::replyNotFound);
    reply.update(request.getContent());

    return ReplyDto.of(reply, ContentDisplayStatus.NORMAL, reply.getContent());
  }

  @Transactional(readOnly = true)
  public List<ReplyDto> readAll(Long viewerId, Long commentId) {
    List<ReplyProjectionDto> projections = replyQueryRepository.findAllRepliesByCommentId(viewerId, commentId);

    return replyProcessor.process(projections);
  }

  @Transactional
  public CommentDeleteResponse delete(Long replyId) {
    Reply reply = replyRepository.findById(replyId).orElseThrow(ReplyException::replyNotFound);
    Comment comment = reply.getComment();
    comment.removeReply();
    reply.softDelete();
    long commentCount = feedExpressions.fetchCommentCountByCommentId(comment.getId());
    return CommentDeleteResponse.of(commentCount);
  }
}