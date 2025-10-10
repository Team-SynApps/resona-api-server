package com.synapps.resona.comment.event;

import com.synapps.resona.entity.Language;

public record CommentTranslationUpdatedEvent(Long commentId, String translatedContent, Language language) {

}
