package com.synapps.resona.comment.command.service;

import com.synapps.resona.comment.command.entity.comment.Comment;
import com.synapps.resona.comment.command.entity.reply.Reply;
import com.synapps.resona.comment.command.repository.MentionRepository;
import com.synapps.resona.comment.command.repository.comment.CommentLikesRepository;
import com.synapps.resona.comment.command.repository.comment.CommentRepository;
import com.synapps.resona.comment.command.repository.reply.ReplyLikesRepository;
import com.synapps.resona.comment.command.repository.reply.ReplyRepository;
import com.synapps.resona.comment.dto.CommentDto;
import com.synapps.resona.comment.dto.CommentRequest;
import com.synapps.resona.comment.dto.ReplyDto;
import com.synapps.resona.comment.dto.ReplyRequest;
import com.synapps.resona.comment.event.CommentCreatedEvent;
import com.synapps.resona.comment.event.CommentDeletedEvent;
import com.synapps.resona.comment.event.ReplyCreatedEvent;
import com.synapps.resona.comment.event.ReplyDeletedEvent;
import com.synapps.resona.comment.exception.CommentException;
import com.synapps.resona.comment.exception.ReplyException;
import com.synapps.resona.comment.query.entity.MentionedMember;
import com.synapps.resona.comment.query.entity.ReplyEmbed;
import com.synapps.resona.common.entity.Author;
import com.synapps.resona.entity.Language;
import com.synapps.resona.entity.member.Member;
import com.synapps.resona.feed.command.entity.Feed;
import com.synapps.resona.feed.command.service.FeedCommandService;
import com.synapps.resona.service.MemberService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentCommandService {

  private final CommentRepository commentRepository;
  private final ReplyRepository replyRepository;
  private final MentionRepository mentionRepository;
  private final CommentLikesRepository commentLikesRepository;

  private final MemberService memberService;
  private final FeedCommandService feedCommandService;
  private final MentionService mentionService;
  private final ApplicationEventPublisher eventPublisher;
  private final ReplyLikesRepository replyLikesRepository;

  @Transactional
  public CommentDto createComment(Long memberId, CommentRequest request) {
    Member member = memberService.getMemberWithProfile(memberId);
    Feed feed = feedCommandService.getFeed(request.getFeedId());

    Comment comment = Comment.of(feed, member, request.getLanguageCode(), request.getContent());
    commentRepository.save(comment);

    feed.increaseCommentCount();

    List<MentionedMember> mentionedMembers = mentionService.parseMentions(request.getMentionMemberIds());

    CommentCreatedEvent event = new CommentCreatedEvent(
        comment.getId(),
        request.getFeedId(),
        Author.of(member.getId(), member.getProfile().getNickname(), member.getProfile().getProfileImageUrl(), member.getProfile().getCountryOfResidence()),
        comment.getContent(),
        comment.getLanguage(),
        comment.getCreatedAt(),
        mentionedMembers
        );

    eventPublisher.publishEvent(event);
    return CommentDto.of(comment);
  }

  @Transactional
  public ReplyDto createReply(Long memberId, ReplyRequest request) {
    Member member = memberService.getMemberWithProfile(memberId);
    Comment comment = commentRepository.findById(request.getCommentId()).orElseThrow(CommentException::commentNotFound);

    Reply reply = Reply.of(comment, member, request.getLanguageCode(), request.getContent());
    replyRepository.save(reply);

    comment.increaseReplyCount();

    List<MentionedMember> mentionedMembers = mentionService.parseMentions(request.getMentionMemberIds());

    ReplyEmbed replyEmbed = ReplyEmbed.of(
        reply.getId(),
        Author.of(member.getId(), member.getProfile().getNickname(), member.getProfile().getProfileImageUrl(), member.getProfile().getCountryOfResidence()),
        Language.fromCode(request.getLanguageCode()),
        request.getContent(),
        mentionedMembers
        );

    eventPublisher.publishEvent(new ReplyCreatedEvent(request.getCommentId(), replyEmbed));
    return ReplyDto.of(reply);
  }

  @Transactional
  public void deleteComment(Long memberId, Long commentId) {
    Comment comment = commentRepository.findById(commentId).orElseThrow(CommentException::commentNotFound);
    
    if (!comment.getMember().getId().equals(memberId)) {
      throw CommentException.unauthorized();
    }

    Feed feed = comment.getFeed();
    feed.decreaseCommentCount();

    mentionRepository.softDeleteAllByTarget(comment);
    commentLikesRepository.softDeleteAllByComment(comment);

    comment.softDelete();

    eventPublisher.publishEvent(new CommentDeletedEvent(commentId, feed.getId()));
  }

  @Transactional
  public void deleteReply(Long memberId, Long replyId) {
    Reply reply = replyRepository.findById(replyId).orElseThrow(ReplyException::replyNotFound);
    
    if (!reply.getMember().getId().equals(memberId)) {
      throw ReplyException.unauthorized();
    }

    Comment comment = reply.getComment();
    comment.decreaseReplyCount();

    mentionRepository.softDeleteAllByTarget(comment);
    replyLikesRepository.softDeleteAllByReply(reply);

    reply.softDelete();

    eventPublisher.publishEvent(new ReplyDeletedEvent(replyId, comment.getId()));
  }
}