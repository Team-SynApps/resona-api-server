package com.synapps.resona.fanout.service;

import com.synapps.resona.command.entity.profile.CountryCode;
import com.synapps.resona.fanout.port.in.FanoutUseCase;
import com.synapps.resona.feed.command.entity.FeedCategory;
import com.synapps.resona.feed.event.FeedCreatedEvent;
import com.synapps.resona.properties.RedisTtlProperties;
import com.synapps.resona.query.service.retrieval.FollowQueryService;
import java.time.ZoneOffset;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FanoutService implements FanoutUseCase {
  private final FollowQueryService followQueryService;
  private final StringRedisTemplate redisTemplate;

  private static final int PAGE_SIZE = 1000; // 한 번에 조회할 팔로워 수
  private static final int FEED_MAX_SIZE = 500; // 개인 타임라인에 유지할 최대 피드 수
  private static final int GLOBAL_FEED_MAX_SIZE = 1000; // 공용 타임라인에 유지할 최대 피드 수
  private final RedisTtlProperties redisTtlProperties;

  @Override
  public void fanoutFeed(FeedCreatedEvent event) {
    log.info("Starting fan-out for feedId: {}", event.feedId());

    // 모든 공용 타임라인을 업데이트
    updateGlobalTimelines(event);

    // 셀러브리티가 아닐 경우에만 팔로워에게 팬아웃
    if (event.authorInfo().isCelebrity()) {
      log.info("Author {} is a celebrity. Skipping fan-out to followers.", event.authorInfo().memberId());
      return;
    }

    // 팔로워들의 개인 타임라인을 업데이트
    fanoutToFollowers(event);

    log.info("Finished fan-out for feedId: {}", event.feedId());
  }

  private void updateGlobalTimelines(FeedCreatedEvent event) {
    long timestamp = event.createdAt().toInstant(ZoneOffset.UTC).toEpochMilli();
    String feedId = event.feedId().toString();
    long uniqueScoreLong = (timestamp << 22) | (event.feedId() & 0x3FFFFFL);
    double finalScore = (double) uniqueScoreLong;

    FeedCategory category = event.category();
    CountryCode country = event.authorInfo().countryOfResidence();

    redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
      // 전체 최신 피드 (feeds:recent)
      String recentKey = "feeds:recent";
      redisTemplate.opsForZSet().add(recentKey, feedId, finalScore);
      redisTemplate.opsForZSet().removeRange(recentKey, 0, -(GLOBAL_FEED_MAX_SIZE + 1));
      redisTemplate.expire(recentKey, redisTtlProperties.publicTimeline(), redisTtlProperties.unit());

      // 전체 국가의 카테고리별 피드 (timeline:category:{CATEGORY})
      String categoryKey = "timeline:category:" + category.name();
      redisTemplate.opsForZSet().add(categoryKey, feedId, finalScore);
      redisTemplate.opsForZSet().removeRange(categoryKey, 0, -(GLOBAL_FEED_MAX_SIZE + 1));
      redisTemplate.expire(categoryKey, redisTtlProperties.publicTimeline(), redisTtlProperties.unit());

      // 특정 국가의 전체 카테고리 피드 (timeline:country:{COUNTRY})
      String countryKey = "timeline:country:" + country.name();
      redisTemplate.opsForZSet().add(countryKey, feedId, finalScore);
      redisTemplate.opsForZSet().removeRange(countryKey, 0, -(GLOBAL_FEED_MAX_SIZE + 1));
      redisTemplate.expire(countryKey, redisTtlProperties.publicTimeline(), redisTtlProperties.unit());

      // 특정 국가의 특정 카테고리 피드 (timeline:country:{COUNTRY}:{CATEGORY})
      String countryCategoryKey = "timeline:country:" + country.name() + ":" + category.name();
      redisTemplate.opsForZSet().add(countryCategoryKey, feedId, finalScore);
      redisTemplate.opsForZSet().removeRange(countryCategoryKey, 0, -(GLOBAL_FEED_MAX_SIZE + 1));
      redisTemplate.expire(countryCategoryKey, redisTtlProperties.publicTimeline(), redisTtlProperties.unit());

      return null;
    });
    log.info("Updated global timelines for feedId: {}", feedId);
  }

  private void fanoutToFollowers(FeedCreatedEvent event) {
    int currentPage = 0;
    Page<Long> followersPage;
    do {
      followersPage = followQueryService.getFollowerIds(
          event.authorInfo().memberId(), PageRequest.of(currentPage, PAGE_SIZE)
      );

      List<Long> followerIds = followersPage.getContent();
      if (!followerIds.isEmpty()) {
        pushToFollowersBatch(followerIds, event);
      }
      currentPage++;
    } while (followersPage.hasNext());
  }

  private void pushToFollowersBatch(List<Long> followerIds, FeedCreatedEvent event) {
    long timestamp = event.createdAt().toInstant(ZoneOffset.UTC).toEpochMilli();
    String feedId = event.feedId().toString();
    long uniqueScoreLong = (timestamp << 22) | (event.feedId() & 0x3FFFFFL);
    double finalScore = (double) uniqueScoreLong;

    FeedCategory category = event.category();

    redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
      for (Long followerId : followerIds) {
        // 개인화된 전체 타임라인
        String allTimelineKey = "timeline:" + followerId + ":ALL";
        redisTemplate.opsForZSet().add(allTimelineKey, feedId, finalScore);
        redisTemplate.opsForZSet().removeRange(allTimelineKey, 0, -(FEED_MAX_SIZE + 1));
        redisTemplate.expire(allTimelineKey, redisTtlProperties.personalTimeline(), redisTtlProperties.unit());

        // 개인화된 카테고리별 타임라인
        String categoryTimelineKey = "timeline:" + followerId + ":" + category.name();
        redisTemplate.opsForZSet().add(categoryTimelineKey, feedId, finalScore);
        redisTemplate.opsForZSet().removeRange(categoryTimelineKey, 0, -(FEED_MAX_SIZE + 1));
        redisTemplate.expire(categoryTimelineKey, redisTtlProperties.personalTimeline(), redisTtlProperties.unit());
      }
      return null;
    });
    log.info("Pushed feedId: {} to {} followers.", feedId, followerIds.size());
  }
}