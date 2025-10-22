package com.synapps.resona.retrieval.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.synapps.resona.common.entity.Author;
import com.synapps.resona.common.entity.Translation;
import com.synapps.resona.entity.Language;
import com.synapps.resona.command.entity.profile.CountryCode;
import com.synapps.resona.feed.command.entity.FeedCategory;
import com.synapps.resona.query.entity.MemberStateDocument;
import com.synapps.resona.query.service.MemberStateService;
import com.synapps.resona.retrieval.dto.FeedDto;
import com.synapps.resona.retrieval.dto.FeedViewerContext;
import com.synapps.resona.retrieval.query.entity.FeedDocument;
import com.synapps.resona.translation.service.TranslationService;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;

@ExtendWith(MockitoExtension.class)
class FeedQueryHelperTest {

    @InjectMocks
    private FeedQueryHelper feedQueryHelper;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private MemberStateService memberStateService;

    @Mock
    private TranslationService translationService;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @Mock
    private SetOperations<String, String> setOperations;

    @Test
    @DisplayName("숨김 피드 ID 조회 - 캐시 히트")
    void getHiddenFeedIds_CacheHit() {
        // given
        Long memberId = 1L;
        String key = "user:" + memberId + ":hidden_feeds";
        Set<String> cachedIds = Set.of("10", "20", "30");

        when(redisTemplate.opsForSet()).thenReturn(setOperations);
        when(setOperations.members(key)).thenReturn(cachedIds);

        // when
        Set<Long> hiddenFeedIds = feedQueryHelper.getHiddenFeedIds(memberId);

        // then
        assertThat(hiddenFeedIds).containsExactlyInAnyOrder(10L, 20L, 30L);
        verify(memberStateService, never()).getMemberStateDocument(anyLong());
    }

    @Test
    @DisplayName("숨김 피드 ID 조회 - 캐시 미스")
    void getHiddenFeedIds_CacheMiss() {
        // given
        Long memberId = 1L;
        String key = "user:" + memberId + ":hidden_feeds";
        Set<Long> dbIds = Set.of(10L, 20L, 30L);

        when(redisTemplate.opsForSet()).thenReturn(setOperations);
        when(setOperations.members(key)).thenReturn(Collections.emptySet());

        MemberStateDocument memberState = MemberStateDocument.create(memberId);
        memberState.getHiddenFeedIds().addAll(dbIds);
        when(memberStateService.getMemberStateDocument(memberId)).thenReturn(memberState);

        // when
        Set<Long> hiddenFeedIds = feedQueryHelper.getHiddenFeedIds(memberId);

        // then
        assertThat(hiddenFeedIds).containsExactlyInAnyOrder(10L, 20L, 30L);
        verify(setOperations).add(eq(key), any(String[].class));
    }

    @Test
    @DisplayName("차단 사용자 ID 조회 - 캐시 히트")
    void getBlockedMemberIds_CacheHit() {
        // given
        Long memberId = 1L;
        String key = "user:" + memberId + ":blocked_users";
        Set<String> cachedIds = Set.of("100", "200", "300");

        when(redisTemplate.opsForSet()).thenReturn(setOperations);
        when(setOperations.members(key)).thenReturn(cachedIds);

        // when
        Set<Long> blockedMemberIds = feedQueryHelper.getBlockedMemberIds(memberId);

        // then
        assertThat(blockedMemberIds).containsExactlyInAnyOrder(100L, 200L, 300L);
        verify(memberStateService, never()).getMemberStateDocument(anyLong());
    }

    @Test
    @DisplayName("차단 사용자 ID 조회 - 캐시 미스")
    void getBlockedMemberIds_CacheMiss() {
        // given
        Long memberId = 1L;
        String key = "user:" + memberId + ":blocked_users";
        Set<Long> dbIds = Set.of(100L, 200L, 300L);

        when(redisTemplate.opsForSet()).thenReturn(setOperations);
        when(setOperations.members(key)).thenReturn(Collections.emptySet());

        MemberStateDocument memberState = MemberStateDocument.create(memberId);
        memberState.getBlockedMemberIds().addAll(dbIds);
        when(memberStateService.getMemberStateDocument(memberId)).thenReturn(memberState);

        // when
        Set<Long> blockedMemberIds = feedQueryHelper.getBlockedMemberIds(memberId);

        // then
        assertThat(blockedMemberIds).containsExactlyInAnyOrder(100L, 200L, 300L);
        verify(setOperations).add(eq(key), any(String[].class));
    }

    @Test
    @DisplayName("DTO 변환 - 번역본이 존재하는 경우")
    void translateAndConvertToDto_TranslationExists() {
        // given
        Author author = Author.of(1L, "test", "test.jpg", CountryCode.KR);
        FeedDocument doc = FeedDocument.of(1L, author, "Hello", Collections.emptyList(), null, FeedCategory.DAILY, Language.en, List.of(new Translation("ko", "안녕하세요")));
        FeedViewerContext context = new FeedViewerContext(0L, Collections.emptySet(), Collections.emptySet(), Collections.emptySet(), Collections.emptySet());

        // when
        FeedDto feedDto = feedQueryHelper.translateAndConvertToDto(doc, Language.ko, context);

        // then
        assertThat(feedDto.translatedContent()).isEqualTo("안녕하세요");
        verify(translationService, never()).translateForRealTime(anyString(), any(Language.class), any(Language.class));
    }

    @Test
    @DisplayName("DTO 변환 - 번역본이 없고, 원본과 대상 언어가 같은 경우")
    void translateAndConvertToDto_NoTranslation_SameLanguage() {
        // given
        Author author = Author.of(1L, "test", "test.jpg", CountryCode.KR);
        FeedDocument doc = FeedDocument.of(1L, author, "Hello", Collections.emptyList(), null, FeedCategory.DAILY, Language.en, Collections.emptyList());
        FeedViewerContext context = new FeedViewerContext(0L, Collections.emptySet(), Collections.emptySet(), Collections.emptySet(), Collections.emptySet());

        // when
        FeedDto feedDto = feedQueryHelper.translateAndConvertToDto(doc, Language.en, context);

        // then
        assertThat(feedDto.translatedContent()).isEqualTo("Hello");
        verify(translationService, never()).translateForRealTime(anyString(), any(Language.class), any(Language.class));
    }

    @Test
    @DisplayName("DTO 변환 - 실시간 번역이 필요한 경우")
    void translateAndConvertToDto_RealTimeTranslation() {
        // given
        Author author = Author.of(1L, "test", "test.jpg", CountryCode.KR);
        FeedDocument doc = FeedDocument.of(1L, author, "Hello", Collections.emptyList(), null, FeedCategory.DAILY, Language.en, Collections.emptyList());
        FeedViewerContext context = new FeedViewerContext(0L, Collections.emptySet(), Collections.emptySet(), Collections.emptySet(), Collections.emptySet());
        when(translationService.translateForRealTime("Hello", Language.en, Language.ko)).thenReturn("안녕하세요");

        // when
        FeedDto feedDto = feedQueryHelper.translateAndConvertToDto(doc, Language.ko, context);

        // then
        assertThat(feedDto.translatedContent()).isEqualTo("안녕하세요");
        verify(translationService).translateForRealTime("Hello", Language.en, Language.ko);
    }

    @Test
    @DisplayName("FeedDocument를 FeedDto로 변환")
    void toDto() {
        // given
        Author author = Author.of(1L, "test", "test.jpg", CountryCode.KR);
        FeedDocument doc = FeedDocument.of(1L, author, "Hello", Collections.emptyList(), null, FeedCategory.DAILY, Language.en, Collections.emptyList());
        FeedViewerContext context = new FeedViewerContext(0L, Collections.emptySet(), Collections.emptySet(), Collections.emptySet(), Collections.emptySet());

        // when
        FeedDto feedDto = feedQueryHelper.toDto(doc, "안녕하세요", context);

        // then
        assertThat(feedDto.feedId()).isEqualTo(1L);
        assertThat(feedDto.author().getNickname()).isEqualTo("test");
        assertThat(feedDto.content()).isEqualTo("Hello");
        assertThat(feedDto.translatedContent()).isEqualTo("안녕하세요");
    }
}