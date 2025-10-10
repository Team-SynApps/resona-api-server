package com.synapps.resona.test.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TestRedisService {

    private final RedisTemplate<String, String> redisTemplate;

    public void clearViewedFeeds(Long memberId) {
        String key = "user:" + memberId + ":seen_feeds";
        redisTemplate.delete(key);
    }
}
