package com.synapps.resona.translation.listener;

import com.synapps.resona.comment.event.CommentDocumentCreatedEvent;
import com.synapps.resona.comment.event.CommentTranslationUpdatedEvent;
import com.synapps.resona.comment.event.CommentTranslationsCreatedEvent;
import com.synapps.resona.comment.event.ReplyDocumentCreatedEvent;
import com.synapps.resona.comment.event.ReplyTranslationUpdatedEvent;
import com.synapps.resona.comment.event.ReplyTranslationsCreatedEvent;
import com.synapps.resona.comment.query.service.CommentDocumentUpdateService;

import com.synapps.resona.common.entity.Translation;
import com.synapps.resona.feed.event.FeedTranslationUpdatedEvent;
import com.synapps.resona.feed.event.FeedTranslationsCreatedEvent;
import com.synapps.resona.retrieval.service.FeedDocumentUpdateService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReadModelTranslationListener {

  private final FeedDocumentUpdateService feedDocumentUpdateService;
  private final CommentDocumentUpdateService commentDocumentUpdateService;


  @EventListener
  public void handleFeedTranslationUpdated(FeedTranslationUpdatedEvent event) {
    log.info("Updating read model with new translations for feedId: {}", event.feedId());
    feedDocumentUpdateService.updateOrAddTranslation(event.feedId(), new Translation(event.language().getCode(), event.translatedText()));
  }

  @EventListener
  public void handleCommentTranslationUpdatedEvent(CommentTranslationUpdatedEvent event) {
    commentDocumentUpdateService.addTranslationToComment(event.commentId(), new Translation(event.language().getCode(), event.translatedContent()));
  }

  @EventListener
  public void handleReplyTranslationUpdatedEvent(ReplyTranslationUpdatedEvent event) {
    commentDocumentUpdateService.addTranslationToReply(event.parentCommentId(), event.replyId(), new Translation(event.language().getCode(), event.translatedContent()));
  }


  /**
   * MongoDB replica set을 사용하지 않는 한 아직 사용할 수 없음. race condition 발생
   * - 스프링 트랜잭션이 보장이 되지 않음
   * - MongoDB replica set을 사용하는 경우, 스프링 트랜잭션에 적용 가능
   * @param event
   */
  @EventListener
  public void handleFeedTranslationsCreatedEvent(FeedTranslationsCreatedEvent event) {
    List<Translation> feedTranslations = event.translatedResults().entrySet().stream()
        .map(entry -> new Translation(entry.getKey().getCode(), entry.getValue()))
        .toList();

    feedDocumentUpdateService.addTranslations(event.feedId(), feedTranslations);
  }

  /**
   * MongoDB replica set을 사용하지 않는 한 아직 사용할 수 없음. race condition 발생
   * - 스프링 트랜잭션이 보장이 되지 않음
   * - MongoDB replica set을 사용하는 경우, 스프링 트랜잭션에 적용 가능
   * @param event
   */
  @EventListener
  public void handleCommentTranslationsCreated(CommentTranslationsCreatedEvent event) {
    log.info("Updating read model with new translations for commentId: {}", event.commentId());

    List<Translation> commentTranslations = event.translatedResults().entrySet().stream()
        .map(entry -> new Translation(entry.getKey().getCode(), entry.getValue()))
        .toList();


    commentTranslations.forEach(translation ->
        commentDocumentUpdateService.addTranslationToComment(event.commentId(), translation)
    );
  }

  /**
   * MongoDB replica set을 사용하지 않는 한 아직 사용할 수 없음. race condition 발생
   * - 스프링 트랜잭션이 보장이 되지 않음
   * - MongoDB replica set을 사용하는 경우, 스프링 트랜잭션에 적용 가능
   * @param event
   */
  @EventListener
  public void handleReplyTranslationsCreated(ReplyTranslationsCreatedEvent event) {
    log.info("Updating read model with new translations for replyId: {}", event.replyId());

    List<Translation> replyTranslations = event.translatedResults().entrySet().stream()
        .map(entry -> new Translation(entry.getKey().getCode(), entry.getValue()))
        .toList();

    replyTranslations.forEach(translation ->
        commentDocumentUpdateService.addTranslationToReply(event.parentCommentId(), event.replyId(), translation)
    );
  }
}