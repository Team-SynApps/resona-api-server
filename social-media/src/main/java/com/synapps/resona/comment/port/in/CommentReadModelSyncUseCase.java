package com.synapps.resona.comment.port.in;

import com.synapps.resona.comment.event.CommentCreatedEvent;
import com.synapps.resona.comment.event.CommentDeletedEvent;
import com.synapps.resona.comment.event.CommentLikeChangedEvent;
import com.synapps.resona.comment.event.ReplyCreatedEvent;
import com.synapps.resona.comment.event.ReplyDeletedEvent;
import com.synapps.resona.comment.event.ReplyLikeChangedEvent;

public interface CommentReadModelSyncUseCase {
  void syncCreatedComment(CommentCreatedEvent event);
  void syncCreatedReply(ReplyCreatedEvent event);
  void syncDeletedComment(CommentDeletedEvent event);
  void syncDeletedReply(ReplyDeletedEvent event);
  void syncLikedComment(CommentLikeChangedEvent event);
  void syncLikedReply(ReplyLikeChangedEvent event);
}
