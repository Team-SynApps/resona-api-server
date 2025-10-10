package com.synapps.resona.comment.adapter.in;

import com.synapps.resona.comment.event.CommentCreatedEvent;
import com.synapps.resona.comment.event.CommentDeletedEvent;
import com.synapps.resona.comment.event.CommentLikeChangedEvent;
import com.synapps.resona.comment.event.ReplyCreatedEvent;
import com.synapps.resona.comment.event.ReplyDeletedEvent;
import com.synapps.resona.comment.event.ReplyLikeChangedEvent;
import com.synapps.resona.comment.port.in.CommentReadModelSyncUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class SpringCommentEventListener {
  private final CommentReadModelSyncUseCase commentReadModelSyncUseCase;

  @Async
  @TransactionalEventListener
  public void handleCommentCreated(CommentCreatedEvent event) {
    commentReadModelSyncUseCase.syncCreatedComment(event);
  }

  @Async
  @TransactionalEventListener
  public void handleReplyCreated(ReplyCreatedEvent event) {
    commentReadModelSyncUseCase.syncCreatedReply(event);
  }

  @Async
  @TransactionalEventListener
  public void handleCommentDeleted(CommentDeletedEvent event) {
    commentReadModelSyncUseCase.syncDeletedComment(event);
  }

  @Async
  @TransactionalEventListener
  public void handleReplyDeleted(ReplyDeletedEvent event) {
    commentReadModelSyncUseCase.syncDeletedReply(event);
  }

  @Async
  @TransactionalEventListener
  public void handleCommentLikeChanged(CommentLikeChangedEvent event) {
    commentReadModelSyncUseCase.syncLikedComment(event);
  }

  @Async
  @TransactionalEventListener
  public void handleReplyLikeChanged(ReplyLikeChangedEvent event) {
    commentReadModelSyncUseCase.syncLikedReply(event);
  }
}