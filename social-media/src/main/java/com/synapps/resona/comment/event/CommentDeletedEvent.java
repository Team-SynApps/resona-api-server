package com.synapps.resona.comment.event;

public record CommentDeletedEvent(
    Long commentId,
    Long feedId
) {
}