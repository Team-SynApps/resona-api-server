package com.synapps.resona.translation.listener;

import com.synapps.resona.feed.event.FeedCreatedEvent;
import com.synapps.resona.translation.service.TranslationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class TranslationListener {
  private final TranslationService translationService;

  @TransactionalEventListener
  public void handleFeedCreatedEvent(FeedCreatedEvent event) {
    translationService.preTranslateAndSave(
        event.feedId(),
        event.content(),
        event.language()
    );
  }

  // @TransactionalEventListener
  // public void handleCommentCreatedEvent(CommentCreatedEvent event) {}
}