package com.synapps.resona.common.event;

import com.synapps.resona.entity.Language;

public record FeedTranslationUpdatedEvent(Long feedId, String translatedText, Language language) {
}
