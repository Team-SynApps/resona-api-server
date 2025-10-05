package com.synapps.resona.comment.event;

public record ReplyDeletedEvent(
    Long replyId,
    Long parentCommentId
) {
}