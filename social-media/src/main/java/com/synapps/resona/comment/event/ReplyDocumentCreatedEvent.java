package com.synapps.resona.comment.event;

import com.synapps.resona.comment.query.entity.ReplyEmbed;

public record ReplyDocumentCreatedEvent(Long parentCommentId, ReplyEmbed reply) {

}
