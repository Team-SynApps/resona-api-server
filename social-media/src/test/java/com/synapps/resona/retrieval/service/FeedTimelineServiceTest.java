
package com.synapps.resona.retrieval.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.synapps.resona.common.entity.Author;
import com.synapps.resona.dto.CursorResult;
import com.synapps.resona.entity.Language;
import com.synapps.resona.command.entity.profile.CountryCode;
import com.synapps.resona.feed.command.entity.FeedCategory;
import com.synapps.resona.properties.RedisTtlProperties;
import com.synapps.resona.retrieval.dto.FeedDto;
import com.synapps.resona.retrieval.query.entity.FeedDocument;
import com.synapps.resona.retrieval.query.repository.FeedReadRepository;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ZSetOperations;

@Disabled
@ExtendWith(MockitoExtension.class)
class FeedTimelineServiceTest {

    @InjectMocks
    private FeedTimelineService feedTimelineService;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private FeedReadRepository feedReadRepository;

    @Mock
    private FeedQueryHelper feedQueryHelper;

    @Mock
    private RedisTtlProperties redisTtlProperties;

    @Mock
    private ZSetOperations<String, String> zSetOperations;

    @Mock
    private SetOperations<String, String> setOperations;

    @Test
    @DisplayName("홈 피드 조회 - 타임라인에 데이터가 있는 경우")
    void getHomeFeeds_Success() {
        // given
        Long memberId = 1L;
        Language targetLanguage = Language.ko;
        String cursor = null;
        int size = 10;
        FeedCategory category = FeedCategory.DAILY;
        String timelineKey = "timeline:" + memberId + ":" + category.name();

        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
        when(zSetOperations.zCard(timelineKey)).thenReturn(15L);

        Set<ZSetOperations.TypedTuple<String>> candidateTuples = LongStream.range(1, 12)
            .mapToObj(i -> ZSetOperations.TypedTuple.of(String.valueOf(i), (double) (12 - i)))
            .collect(Collectors.toSet());
        when(zSetOperations.reverseRangeByScoreWithScores(eq(timelineKey), anyDouble(), anyDouble(), anyLong(), anyInt()))
            .thenReturn(candidateTuples);

        when(redisTemplate.opsForSet()).thenReturn(setOperations);
        when(setOperations.members(anyString())).thenReturn(Collections.emptySet());
        when(feedQueryHelper.getHiddenFeedIds(memberId)).thenReturn(Collections.emptySet());

        List<FeedDocument> feedDocuments = LongStream.range(1, 12)
            .mapToObj(i -> {
                Author author = Author.of(2L, "test", "test.jpg", CountryCode.US);
                return FeedDocument.of(i, author, "content", Collections.emptyList(), null, FeedCategory.DAILY, Language.ko, Collections.emptyList());
            })
            .toList();
        when(feedReadRepository.findAllByFeedIdIn(any())).thenReturn(feedDocuments);

        // when
        CursorResult<FeedDto> result = feedTimelineService.getHomeFeeds(memberId, targetLanguage, cursor, size, category);

        // then
        assertThat(result.getValues()).hasSize(10);
        assertThat(result.isHasNext()).isTrue();
        assertThat(result.getNextCursor()).isNotNull();
        verify(feedQueryHelper, times(10)).translateAndConvertToDto(any(FeedDocument.class), eq(targetLanguage));
    }

    @Test
    @DisplayName("홈 피드 조회 - 콜드 스타트 사용자의 경우 Fallback 로직 실행")
    void getHomeFeeds_ColdStart_Fallback() {
        // given
        Long memberId = 1L;
        Language targetLanguage = Language.ko;
        String cursor = null;
        int size = 10;
        FeedCategory category = FeedCategory.DAILY;
        String timelineKey = "timeline:" + memberId + ":" + category.name();

        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
        when(zSetOperations.zCard(timelineKey)).thenReturn(0L);

        // Fallback (getExploreFeeds) mocking
        String exploreTimelineKey = "timeline:category:" + category.name();
        when(zSetOperations.zCard(exploreTimelineKey)).thenReturn(5L);
        Set<ZSetOperations.TypedTuple<String>> exploreTuples = LongStream.range(1, 6)
            .mapToObj(i -> ZSetOperations.TypedTuple.of(String.valueOf(i), (double) (6 - i)))
            .collect(Collectors.toSet());
        when(zSetOperations.reverseRangeByScoreWithScores(eq(exploreTimelineKey), anyDouble(), anyDouble(), anyLong(), anyInt()))
            .thenReturn(exploreTuples);

        when(redisTemplate.opsForSet()).thenReturn(setOperations);
        when(setOperations.members(anyString())).thenReturn(Collections.emptySet());
        when(feedQueryHelper.translateAndConvertToDto(any(FeedDocument.class), eq(targetLanguage)))
            .thenAnswer(invocation -> {
                FeedDocument doc = invocation.getArgument(0);
                Author author = Author.of(1L, "test", "test.jpg", CountryCode.KR);
                return new FeedDto(doc.getFeedId(), author, "content", Collections.emptyList(), null, "DAILY", "KO", 0, 0, null);
            });

        // when
        CursorResult<FeedDto> result = feedTimelineService.getHomeFeeds(memberId, targetLanguage, cursor, size, category);

        // then
        assertThat(result.getValues()).hasSize(5);
        assertThat(result.isHasNext()).isFalse();
        verify(zSetOperations, times(1)).zCard(timelineKey);
        verify(zSetOperations, times(1)).zCard(exploreTimelineKey);
    }

    @Test
    @DisplayName("탐색 피드 조회 - 국가와 카테고리 지정")
    void getExploreFeeds_WithCountryAndCategory() {
        // given
        Long memberId = 1L;
        Language targetLanguage = Language.en;
        String cursor = null;
        int size = 5;
        CountryCode residence = CountryCode.KR;
        FeedCategory category = FeedCategory.TRAVEL;
        String timelineKey = "timeline:country:" + residence.name() + ":" + category.name();

        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
        when(zSetOperations.zCard(timelineKey)).thenReturn(8L);

        Set<ZSetOperations.TypedTuple<String>> candidateTuples = LongStream.range(1, 7)
            .mapToObj(i -> ZSetOperations.TypedTuple.of(String.valueOf(i), (double) (7 - i)))
            .collect(Collectors.toSet());
        when(zSetOperations.reverseRangeByScoreWithScores(eq(timelineKey), anyDouble(), anyDouble(), anyLong(), anyInt()))
            .thenReturn(candidateTuples);

        when(redisTemplate.opsForSet()).thenReturn(setOperations);
        when(setOperations.members(anyString())).thenReturn(Collections.emptySet());
        when(feedQueryHelper.getHiddenFeedIds(memberId)).thenReturn(Collections.emptySet());

        List<FeedDocument> feedDocuments = LongStream.range(1, 6)
            .mapToObj(i -> {
                FeedDocument doc = mock(FeedDocument.class);
                Author author = Author.of(2L, "test", "test.jpg", CountryCode.US);
                when(doc.getFeedId()).thenReturn(i);
                when(doc.getAuthor()).thenReturn(author);
                return doc;
            })
            .toList();
        when(feedReadRepository.findAllByFeedIdIn(any())).thenReturn(feedDocuments);
        when(feedQueryHelper.translateAndConvertToDto(any(FeedDocument.class), eq(targetLanguage)))
            .thenAnswer(invocation -> {
                FeedDocument doc = invocation.getArgument(0);
                Author author = Author.of(1L, "test", "test.jpg", CountryCode.KR);
                return new FeedDto(doc.getFeedId(), author, "content", Collections.emptyList(), null, "TRAVEL", "EN", 0, 0, null);
            });

        // when
        CursorResult<FeedDto> result = feedTimelineService.getExploreFeeds(memberId, targetLanguage, cursor, size, residence, category);

        // then
        assertThat(result.getValues()).hasSize(5);
        assertThat(result.isHasNext()).isTrue();
        assertThat(result.getNextCursor()).isNotNull();
    }

    @Test
    @DisplayName("피드를 봤음으로 표시")
    void markFeedsAsSeen() {
        // given
        Long memberId = 1L;
        List<Long> feedIds = List.of(101L, 102L, 103L);
        String key = "user:" + memberId + ":seen_feeds";
        String[] feedIdStrings = feedIds.stream().map(String::valueOf).toArray(String[]::new);
        long ttl = 3600L;

        when(redisTemplate.opsForSet()).thenReturn(setOperations);
        when(redisTtlProperties.seenFeeds()).thenReturn(ttl);
        when(redisTtlProperties.unit()).thenReturn(TimeUnit.SECONDS);

        // when
        feedTimelineService.markFeedsAsSeen(memberId, feedIds);

        // then
        verify(setOperations).add(key, feedIdStrings);
        verify(redisTemplate).expire(key, ttl, TimeUnit.SECONDS);
    }

    @Test
    @DisplayName("빈 ID 목록으로 봤음 표시 요청 시 아무 작업도 하지 않음")
    void markFeedsAsSeen_EmptyList() {
        // given
        Long memberId = 1L;
        List<Long> feedIds = Collections.emptyList();

        // when
        feedTimelineService.markFeedsAsSeen(memberId, feedIds);

        // then
        verify(redisTemplate, never()).opsForSet();
    }
}
