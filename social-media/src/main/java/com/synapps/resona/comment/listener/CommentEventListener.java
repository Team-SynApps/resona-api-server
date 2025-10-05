package com.synapps.resona.comment.listener;

import com.synapps.resona.comment.event.CommentCreatedEvent;
import com.synapps.resona.comment.event.CommentDeletedEvent;
import com.synapps.resona.comment.event.CommentLikeChangedEvent;
import com.synapps.resona.comment.event.ReplyCreatedEvent;
import com.synapps.resona.comment.event.ReplyDeletedEvent;
import com.synapps.resona.comment.event.ReplyLikeChangedEvent;
import com.synapps.resona.comment.exception.CommentException;
import com.synapps.resona.comment.query.entity.CommentDocument;
import com.synapps.resona.comment.query.repository.CommentDocumentRepository;
import com.synapps.resona.comment.query.service.CommentDocumentUpdateService;
import com.synapps.resona.query.member.event.CommentLikedEvent;
import com.synapps.resona.query.member.event.CommentUnlikedEvent;
import com.synapps.resona.query.member.event.ReplyLikedEvent;
import com.synapps.resona.query.member.event.ReplyUnlikedEvent;
import com.synapps.resona.retrieval.service.FeedDocumentUpdateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommentEventListener {

  private final CommentDocumentRepository commentDocumentRepository;
  private final CommentDocumentUpdateService commentDocumentUpdateService;
  private final FeedDocumentUpdateService feedDocumentUpdateService;
  private final ApplicationEventPublisher eventPublisher;

  @Async
  @EventListener
  public void handleCommentCreated(CommentCreatedEvent event) {
    log.info("Received CommentCreatedEvent for commentId: {}", event.commentId());

    CommentDocument document = CommentDocument.of(
        event.commentId(),
        event.feedId(),
        event.author(),
        event.language(),
        event.content(),
        event.mentionedMembers()
    );

    commentDocumentRepository.save(document);
    log.info("Saved CommentDocument for commentId: {}", event.commentId());
  }

  @Async
  @EventListener
  public void handleReplyCreated(ReplyCreatedEvent event) {
    log.info("Received ReplyCreatedEvent for parentCommentId: {}", event.parentCommentId());

    commentDocumentUpdateService.addReplyToComment(event.parentCommentId(), event.replyEmbed());
  }

  @Async
  @EventListener
  public void handleCommentDeleted(CommentDeletedEvent event) {
    log.info("Received CommentDeletedEvent for commentId: {}", event.commentId());

    CommentDocument document = commentDocumentRepository.findByCommentId(event.commentId()).orElseThrow(CommentException::commentNotFound);
    document.softDelete();

    feedDocumentUpdateService.updateFeedCommentCount(event.feedId(), -1);
  }

  @Async
  @EventListener
  public void handleReplyDeleted(ReplyDeletedEvent event) {
    log.info("Received ReplyDeletedEvent for replyId: {}", event.replyId());

    commentDocumentUpdateService.softDeleteReplyInComment(event.parentCommentId(), event.replyId());
  }

  @Async
  @EventListener
  public void handleCommentLikeChanged(CommentLikeChangedEvent event) {
    if (event.delta() > 0) {
      eventPublisher.publishEvent(new CommentLikedEvent(event.memberId(), event.commentId()));
    } else {
      eventPublisher.publishEvent(new CommentUnlikedEvent(event.memberId(), event.commentId()));
    }
    commentDocumentUpdateService.updateCommentLikeCount(event.commentId(), event.delta());
  }

  @Async
  @EventListener
  public void handleReplyLikeChanged(ReplyLikeChangedEvent event) {
    if (event.delta() > 0) {
      eventPublisher.publishEvent(new ReplyLikedEvent(event.memberId(), event.replyId()));
    } else {
      eventPublisher.publishEvent(new ReplyUnlikedEvent(event.memberId(), event.replyId()));
    }
    commentDocumentUpdateService.updateReplyLikeCount(event.parentCommentId(), event.replyId(), event.delta());
  }
}