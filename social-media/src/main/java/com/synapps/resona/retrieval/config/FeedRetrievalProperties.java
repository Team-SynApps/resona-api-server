package com.synapps.resona.retrieval.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "feed.retrieval")
public record FeedRetrievalProperties(int defaultPageSize) {
}
