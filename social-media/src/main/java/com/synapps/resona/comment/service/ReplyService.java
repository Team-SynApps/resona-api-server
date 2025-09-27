package com.synapps.resona.comment.service;

import com.synapps.resona.comment.dto.ReplyDto;
import com.synapps.resona.comment.dto.ReplyProjectionDto;
import com.synapps.resona.comment.dto.request.ReplyRequest;
import com.synapps.resona.comment.dto.request.ReplyUpdateRequest;
import com.synapps.resona.comment.dto.response.CommentDeleteResponse;
import com.synapps.resona.comment.entity.Comment;
import com.synapps.resona.comment.entity.ContentDisplayStatus;
import com.synapps.resona.comment.entity.Reply;
import com.synapps.resona.comment.exception.CommentException;
import com.synapps.resona.comment.exception.ReplyException;
import com.synapps.resona.comment.repository.comment.CommentRepository;
import com.synapps.resona.comment.repository.reply.ReplyQueryRepository;
import com.synapps.resona.comment.repository.reply.ReplyRepository;
import com.synapps.resona.dto.MemberDto;
import com.synapps.resona.entity.member.Member;
import com.synapps.resona.feed.repository.dsl.FeedExpressions;
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
    Reply reply = Reply.of(comment, member, request.getContent());
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