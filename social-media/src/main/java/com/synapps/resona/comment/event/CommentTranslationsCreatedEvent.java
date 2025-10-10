package com.synapps.resona.comment.event;

import com.synapps.resona.entity.Language;
import java.util.Map;

public record CommentTranslationsCreatedEvent(
    Long commentId,
    Map<Language, String> translatedResults
) {}