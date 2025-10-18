package com.synapps.resona.comment.command.service;

import com.synapps.resona.comment.command.entity.comment.Comment;
import com.synapps.resona.comment.command.entity.comment.CommentLikes;
import com.synapps.resona.comment.command.entity.reply.Reply;
import com.synapps.resona.comment.command.entity.reply.ReplyLikes;
import com.synapps.resona.comment.command.repository.comment.CommentLikesRepository;
import com.synapps.resona.comment.command.repository.comment.CommentRepository;
import com.synapps.resona.comment.command.repository.reply.ReplyLikesRepository;
import com.synapps.resona.comment.command.repository.reply.ReplyRepository;
import com.synapps.resona.comment.event.CommentLikeChangedEvent;
import com.synapps.resona.comment.event.ReplyLikeChangedEvent;
import com.synapps.resona.comment.exception.CommentException;
import com.synapps.resona.comment.exception.ReplyException;
import com.synapps.resona.command.entity.member.Member;
import com.synapps.resona.command.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentLikeService {
  private final CommentRepository commentRepository;
  private final ReplyRepository replyRepository;
  private final CommentLikesRepository commentLikesRepository;
  private final ReplyLikesRepository replyLikesRepository;

  private final MemberService memberService;
  private final ApplicationEventPublisher eventPublisher;

  @Transactional
  public void likeComment(Long memberId, Long commentId) {
    Member member = memberService.getMember(memberId);
    Comment comment = commentRepository.findById(commentId).orElseThrow(CommentException::commentNotFound);

    if (commentLikesRepository.findByMemberAndComment(member, comment).isPresent()) return;

    commentLikesRepository.save(CommentLikes.of(member, comment));
    comment.increaseLikeCount();
    eventPublisher.publishEvent(new CommentLikeChangedEvent(memberId, commentId, 1));
  }

  @Transactional
  public void unlikeComment(Long memberId, Long commentId) {
    Member member = memberService.getMember(memberId);
    Comment comment = commentRepository.findById(commentId).orElseThrow(CommentException::commentNotFound);

    commentLikesRepository.findByMemberAndComment(member, comment).ifPresent(like -> {
      like.softDelete();
      comment.decreaseLikeCount();
      eventPublisher.publishEvent(new CommentLikeChangedEvent(memberId, commentId, -1));
    });
  }

  @Transactional
  public void likeReply(Long memberId, Long replyId) {
    Member member = memberService.getMember(memberId);
    Reply reply = replyRepository.findById(replyId).orElseThrow(ReplyException::replyNotFound);

    if (replyLikesRepository.findByMemberAndReply(member, reply).isPresent()) return;

    replyLikesRepository.save(ReplyLikes.of(member, reply));
    reply.increaseLikeCount();

    eventPublisher.publishEvent(new ReplyLikeChangedEvent(memberId, replyId, reply.getComment().getId(), 1));
  }

  @Transactional
  public void unlikeReply(Long memberId, Long replyId) {
    Member member = memberService.getMember(memberId);
    Reply reply = replyRepository.findById(replyId).orElseThrow(ReplyException::replyNotFound);

    replyLikesRepository.findByMemberAndReply(member, reply).ifPresent(like -> {
      like.softDelete();
      reply.decreaseLikeCount();
      eventPublisher.publishEvent(new ReplyLikeChangedEvent(memberId, replyId, reply.getComment().getId(), -1));
    });
  }

}
