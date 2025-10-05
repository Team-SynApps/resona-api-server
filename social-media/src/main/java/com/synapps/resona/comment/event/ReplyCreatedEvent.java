package com.synapps.resona.comment.event;

import com.synapps.resona.comment.query.entity.ReplyEmbed;

public record ReplyCreatedEvent(
    Long parentCommentId,
    ReplyEmbed replyEmbed
) {
}