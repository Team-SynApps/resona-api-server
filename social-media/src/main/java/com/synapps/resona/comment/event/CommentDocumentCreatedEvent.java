package com.synapps.resona.comment.event;

import com.synapps.resona.entity.Language;

public record CommentDocumentCreatedEvent(
    Long commentId,
    String content,
    Language language
) {}
