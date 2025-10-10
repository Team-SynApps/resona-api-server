package com.synapps.resona.properties;

import java.util.concurrent.TimeUnit;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "redis.ttl")
public record RedisTtlProperties(
    @DefaultValue("30") long personalTimeline, // 개인 타임라인
    @DefaultValue("7") long publicTimeline,   // 공용 타임라인
    @DefaultValue("7") long seenFeeds,        // 최근 본 게시물
    @DefaultValue("DAYS") TimeUnit unit       // 시간 단위
) {
}