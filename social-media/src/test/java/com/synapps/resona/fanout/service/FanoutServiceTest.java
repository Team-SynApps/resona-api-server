package com.synapps.resona.fanout.service;

import com.synapps.resona.entity.Language;
import com.synapps.resona.command.entity.profile.CountryCode;
import com.synapps.resona.feed.command.entity.FeedCategory;
import com.synapps.resona.feed.event.FeedCreatedEvent;
import com.synapps.resona.feed.event.FeedCreatedEvent.AuthorInfo;
import com.synapps.resona.properties.RedisTtlProperties;
import com.synapps.resona.command.service.FollowService;
import com.synapps.resona.query.service.retrieval.FollowQueryService;
import com.synapps.resona.support.ServiceLayerTest;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.StringRedisTemplate;
import support.config.TestContainerConfig;
import support.database.DatabaseCleaner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ServiceLayerTest
@Import({FanoutService.class, TestContainerConfig.class, DatabaseCleaner.class})
class FanoutServiceTest {

    @Autowired
    private FanoutService fanoutService;

    @MockBean
    private FollowQueryService followQueryService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RedisTtlProperties redisTtlProperties;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @AfterEach
    void tearDown() {
        databaseCleaner.execute();
    }

    @Test
    @DisplayName("피드 생성 이벤트 발생 시 팔로워에게 성공적으로 팬아웃한다.")
    void fanoutFeed_Success() {
        // given
        AuthorInfo authorInfo = new AuthorInfo(1L, "nickname", "url", false, CountryCode.KR);
        FeedCreatedEvent event = new FeedCreatedEvent(2L, "content", FeedCategory.DAILY, Language.ko, authorInfo, Collections.emptyList(), Optional.empty(), LocalDateTime.now());

        when(followQueryService.getFollowerIds(anyLong(), any(PageRequest.class)))
            .thenReturn(new PageImpl<>(List.of(3L, 4L)));

        // when
        fanoutService.fanoutFeed(event);

        // then
        verify(followQueryService, times(1)).getFollowerIds(anyLong(), any(PageRequest.class));
        assertThat(redisTemplate.opsForZSet().score("feeds:recent", "2")).isNotNull();
        assertThat(redisTemplate.opsForZSet().score("timeline:category:DAILY", "2")).isNotNull();
        assertThat(redisTemplate.opsForZSet().score("timeline:country:KR", "2")).isNotNull();
        assertThat(redisTemplate.opsForZSet().score("timeline:country:KR:DAILY", "2")).isNotNull();
        assertThat(redisTemplate.opsForZSet().score("timeline:3:ALL", "2")).isNotNull();
        assertThat(redisTemplate.opsForZSet().score("timeline:3:DAILY", "2")).isNotNull();
        assertThat(redisTemplate.opsForZSet().score("timeline:4:ALL", "2")).isNotNull();
        assertThat(redisTemplate.opsForZSet().score("timeline:4:DAILY", "2")).isNotNull();
    }

    @Test
    @DisplayName("셀러브리티 피드는 팔로워에게 팬아웃하지 않는다.")
    void fanoutFeed_Celebrity_SkipsFollowers() {
        // given
        AuthorInfo authorInfo = new AuthorInfo(1L, "nickname", "url", true, CountryCode.KR);
        FeedCreatedEvent event = new FeedCreatedEvent(2L, "content", FeedCategory.DAILY, Language.ko, authorInfo, Collections.emptyList(), Optional.empty(), LocalDateTime.now());

        // when
        fanoutService.fanoutFeed(event);

        // then
        verify(followQueryService, times(0)).getFollowerIds(anyLong(), any(PageRequest.class));
        assertThat(redisTemplate.opsForZSet().score("feeds:recent", "2")).isNotNull();
        assertThat(redisTemplate.opsForZSet().score("timeline:category:DAILY", "2")).isNotNull();
        assertThat(redisTemplate.opsForZSet().score("timeline:country:KR", "2")).isNotNull();
        assertThat(redisTemplate.opsForZSet().score("timeline:country:KR:DAILY", "2")).isNotNull();
        assertThat(redisTemplate.opsForZSet().score("timeline:3:ALL", "2")).isNull();
    }
}
