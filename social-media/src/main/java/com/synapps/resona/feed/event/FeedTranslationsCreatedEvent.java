package com.synapps.resona.feed.event;

import com.synapps.resona.entity.Language;
import java.util.Map;

public record FeedTranslationsCreatedEvent(Long feedId, Map<Language, String> translatedResults) {
}
