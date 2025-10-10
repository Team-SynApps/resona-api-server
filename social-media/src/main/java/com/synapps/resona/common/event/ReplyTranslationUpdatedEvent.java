package com.synapps.resona.common.event;

import com.synapps.resona.entity.Language;

public record ReplyTranslationUpdatedEvent(Long parentCommentId, Long replyId, String translatedContent, Language language) {
}
