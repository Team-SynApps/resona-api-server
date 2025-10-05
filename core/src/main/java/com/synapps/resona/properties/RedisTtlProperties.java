package com.synapps.resona.properties;

import java.util.concurrent.TimeUnit;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 * Redis TTL(유효기간) 설정을 관리하는 Properties 클래스
 * 값의 단위는 'unit' 필드에 의해 결정됩니다 (기본값: DAYS)
 */
@ConfigurationProperties(prefix = "redis.ttl")
public record RedisTtlProperties(
    @DefaultValue("30") long personalTimeline, // 개인 타임라인
    @DefaultValue("7") long publicTimeline,   // 공용 타임라인
    @DefaultValue("7") long seenFeeds,        // 최근 본 게시물
    @DefaultValue("DAYS") TimeUnit unit       // 시간 단위
) {
}