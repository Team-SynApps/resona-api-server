package com.synapps.resona.comment.event;

import com.synapps.resona.entity.Language;
import java.util.Map;

public record ReplyTranslationsCreatedEvent(
    Long parentCommentId,
    Long replyId,
    Map<Language, String> translatedResults
) {}