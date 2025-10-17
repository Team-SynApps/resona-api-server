package com.synapps.resona.comment.query.service;

import com.synapps.resona.comment.event.CommentCreatedEvent;
import com.synapps.resona.comment.event.CommentDeletedEvent;
import com.synapps.resona.comment.event.CommentLikeChangedEvent;
import com.synapps.resona.comment.event.ReplyCreatedEvent;
import com.synapps.resona.comment.event.ReplyDeletedEvent;
import com.synapps.resona.comment.event.ReplyLikeChangedEvent;
import com.synapps.resona.comment.exception.CommentException;
import com.synapps.resona.comment.port.in.CommentReadModelSyncUseCase;
import com.synapps.resona.comment.query.entity.CommentDocument;
import com.synapps.resona.comment.query.entity.ReplyEmbed;
import com.synapps.resona.comment.query.repository.CommentDocumentRepository;
import com.synapps.resona.common.entity.Translation;
import com.synapps.resona.query.event.CommentLikedEvent;
import com.synapps.resona.query.event.CommentUnlikedEvent;
import com.synapps.resona.query.event.ReplyLikedEvent;
import com.synapps.resona.query.event.ReplyUnlikedEvent;
import com.synapps.resona.retrieval.service.FeedDocumentUpdateService;
import com.synapps.resona.translation.service.TranslationService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentReadModelSyncService implements CommentReadModelSyncUseCase {

  private final CommentDocumentRepository commentDocumentRepository;
  private final CommentDocumentUpdateService commentDocumentUpdateService;
  private final FeedDocumentUpdateService feedDocumentUpdateService;
  // 추후 분리 필요: mongoDB replica set
  private final TranslationService translationService;
  private final ApplicationEventPublisher eventPublisher;

  @Override
  public void syncCreatedComment(CommentCreatedEvent event) {
    log.info("Received CommentCreatedEvent for commentId: {}", event.commentId());

    List<Translation> translations = translationService.translateToTargetLanguages(event.content(), event.language());

    CommentDocument document = CommentDocument.of(
        event.commentId(),
        event.feedId(),
        event.author(),
        event.language(),
        event.content(),
        event.mentionedMembers(),
        translations
    );

    commentDocumentRepository.save(document);
    log.info("Saved CommentDocument for commentId: {}", event.commentId());

    // 현재 불가능 - race condition 발생
//    eventPublisher.publishEvent(new CommentDocumentCreatedEvent(event.commentId(), event.content(), event.language()));
  }

  @Override
  public void syncCreatedReply(ReplyCreatedEvent event) {
    log.info("Received ReplyCreatedEvent for parentCommentId: {}", event.parentCommentId());

    List<Translation> translations = translationService.translateToTargetLanguages(event.reply().getContent(), event.reply().getLanguage());
    ReplyEmbed replyEmbed = event.reply();
    replyEmbed.addTranslations(translations);

    commentDocumentUpdateService.addReplyToComment(event.parentCommentId(), replyEmbed);

    // 현재 불가능 - race condition 발생
//    eventPublisher.publishEvent(new ReplyDocumentCreatedEvent(event.parentCommentId(), event.reply()));
  }

  @Override
  public void syncDeletedComment(CommentDeletedEvent event) {
    log.info("Received CommentDeletedEvent for commentId: {}", event.commentId());

    CommentDocument document = commentDocumentRepository.findByCommentId(event.commentId()).orElseThrow(
        CommentException::commentNotFound);
    document.softDelete();

    feedDocumentUpdateService.updateFeedCommentCount(event.feedId(), -1);
  }

  @Override
  public void syncDeletedReply(ReplyDeletedEvent event) {
    log.info("Received ReplyDeletedEvent for replyId: {}", event.replyId());

    commentDocumentUpdateService.softDeleteReplyInComment(event.parentCommentId(), event.replyId());
  }

  @Override
  public void syncLikedComment(CommentLikeChangedEvent event) {
    if (event.delta() > 0) {
      eventPublisher.publishEvent(new CommentLikedEvent(event.memberId(), event.commentId()));
    } else {
      eventPublisher.publishEvent(new CommentUnlikedEvent(event.memberId(), event.commentId()));
    }
    commentDocumentUpdateService.updateCommentLikeCount(event.commentId(), event.delta());
  }

  @Override
  public void syncLikedReply(ReplyLikeChangedEvent event) {
    if (event.delta() > 0) {
      eventPublisher.publishEvent(new ReplyLikedEvent(event.memberId(), event.replyId()));
    } else {
      eventPublisher.publishEvent(new ReplyUnlikedEvent(event.memberId(), event.replyId()));
    }
    commentDocumentUpdateService.updateReplyLikeCount(event.parentCommentId(), event.replyId(), event.delta());
  }

}
