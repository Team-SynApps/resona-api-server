package com.synapps.resona.translation.listener;

import com.synapps.resona.comment.command.entity.comment.Comment;
import com.synapps.resona.comment.command.entity.comment.CommentTranslation;
import com.synapps.resona.comment.command.entity.reply.Reply;
import com.synapps.resona.comment.command.entity.reply.ReplyTranslation;
import com.synapps.resona.comment.command.repository.CommentTranslationRepository;
import com.synapps.resona.comment.command.repository.ReplyTranslationRepository;
import com.synapps.resona.comment.command.repository.comment.CommentRepository;
import com.synapps.resona.comment.command.repository.reply.ReplyRepository;
import com.synapps.resona.common.entity.Translation;
import com.synapps.resona.common.event.CommentTranslationUpdatedEvent;
import com.synapps.resona.common.event.CommentTranslationsCreatedEvent;

import com.synapps.resona.common.event.ReplyTranslationUpdatedEvent;
import com.synapps.resona.common.event.ReplyTranslationsCreatedEvent;
import com.synapps.resona.comment.exception.CommentException;
import com.synapps.resona.comment.exception.ReplyException;
import com.synapps.resona.entity.Language;
import com.synapps.resona.feed.command.entity.Feed;
import com.synapps.resona.feed.command.entity.FeedTranslation;
import com.synapps.resona.feed.command.repository.FeedRepository;
import com.synapps.resona.feed.command.repository.FeedTranslationRepository;
import com.synapps.resona.common.event.FeedTranslationUpdatedEvent;
import com.synapps.resona.common.event.FeedTranslationsCreatedEvent;
import com.synapps.resona.feed.event.FeedCreatedEvent;
import com.synapps.resona.feed.exception.FeedException;
import com.synapps.resona.translation.service.TranslationService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class WriteModelTranslationListener {
  private final FeedRepository feedRepository;
  private final CommentRepository commentRepository;
  private final ReplyRepository replyRepository;

  private final FeedTranslationRepository feedTranslationRepository;
  private final CommentTranslationRepository commentTranslationRepository;
  private final ReplyTranslationRepository replyTranslationRepository;

  private final TranslationService translationService;

  @TransactionalEventListener
  public void handleFeedCreatedEvent(FeedCreatedEvent event) {
    Feed feed = feedRepository.findById(event.feedId()).orElseThrow(FeedException::feedNotFoundException);
    List<Translation> translations = translationService.translateToTargetLanguages(feed.getContent(), feed.getLanguage());

    for (Translation translation : translations) {
      Language translatedLanguage = Language.fromCode(translation.languageCode());
      FeedTranslation feedTranslation = FeedTranslation.of(feed, translatedLanguage, translation.content());
      feedTranslationRepository.save(feedTranslation);
    }
  }

  @TransactionalEventListener
  public void handleFeedTranslationsCreatedEvent(FeedTranslationsCreatedEvent event) {
    Feed feed = feedRepository.findById(event.feedId()).orElseThrow(FeedException::feedNotFoundException);

    event.translatedResults().forEach((language, text) -> {
      FeedTranslation translation = FeedTranslation.of(feed, language, text);
      feedTranslationRepository.save(translation);
    });
  }

  @TransactionalEventListener
  public void handleFeedTranslationUpdatedEvent(FeedTranslationUpdatedEvent event) {
    Feed feed = feedRepository.findById(event.feedId())
        .orElseThrow(FeedException::feedNotFoundException);

    FeedTranslation feedTranslation = feedTranslationRepository.findByFeedAndLanguage(feed, event.language())
        .map(translation -> {
          translation.updateText(event.translatedText());
          return translation;
        })
        .orElseGet(() -> FeedTranslation.of(feed, event.language(), event.translatedText()));

    feedTranslationRepository.save(feedTranslation);
  }

  @TransactionalEventListener
  public void handleCommentTranslationsCreatedEvent(CommentTranslationsCreatedEvent event) {
    Comment comment = commentRepository.findById(event.commentId()).orElseThrow(CommentException::commentNotFound);

    event.translatedResults().forEach((language, text) -> {
      CommentTranslation translation = CommentTranslation.of(comment, language, text);
      commentTranslationRepository.save(translation);
    });
  }

  @TransactionalEventListener
  public void handleCommentTranslationUpdatedEvent(CommentTranslationUpdatedEvent event) {
    Comment comment = commentRepository.findById(event.commentId())
        .orElseThrow(CommentException::commentNotFound);

    CommentTranslation translation = CommentTranslation.of(comment, event.language(), event.translatedContent());
    commentTranslationRepository.save(translation);
  }

  @TransactionalEventListener
  public void handleReplyTranslationsCreatedEvent(ReplyTranslationsCreatedEvent event) {
    Reply reply = replyRepository.findById(event.replyId()).orElseThrow();

    event.translatedResults().forEach((language, text) -> {
      ReplyTranslation translation = ReplyTranslation.of(reply, language, text);
      replyTranslationRepository.save(translation);
    });
  }

  @TransactionalEventListener
  public void handleReplyTranslationUpdatedEvent(ReplyTranslationUpdatedEvent event) {
    Reply reply = replyRepository.findById(event.replyId()).orElseThrow(ReplyException::replyNotFound);

    ReplyTranslation translation = ReplyTranslation.of(reply, event.language(),
        event.translatedContent());

    replyTranslationRepository.save(translation);
  }

}
