package com.synapps.resona.feed.event;

import com.synapps.resona.entity.Language;

public record FeedTranslationUpdatedEvent(Long feedId, String translatedText, Language language) {
}
