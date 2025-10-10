
package com.synapps.resona.retrieval.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.synapps.resona.dto.CursorResult;
import com.synapps.resona.entity.Language;
import com.synapps.resona.feed.command.entity.FeedCategory;
import com.synapps.resona.retrieval.dto.FeedDto;
import com.synapps.resona.retrieval.query.entity.FeedDocument;
import com.synapps.resona.retrieval.query.repository.FeedReadRepository;
import com.synapps.resona.retrieval.service.strategy.FallbackStrategyContext;
import com.synapps.resona.query.member.service.MemberStateService;
import java.util.Collections;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class FeedRetrievalServiceTest {

    @InjectMocks
    private FeedRetrievalService feedRetrievalService;

    @Mock
    private FeedReadRepository feedReadRepository;

    @Mock
    private MemberStateService memberStateService;

    @Mock
    private FeedQueryHelper feedQueryHelper;

    @Mock
    private FeedTimelineService feedTimelineService;

    @Mock
    private MemberFeedService memberFeedService;

    @Mock
    private FallbackStrategyContext fallbackStrategyContext;

    @Test
    @DisplayName("홈 피드 목록을 성공적으로 조회한다.")
    void getHomeFeeds_Success() {
        // given
        Long memberId = 1L;
        Language targetLanguage = Language.en;
        String cursor = null;
        int size = 10;
        FeedCategory category = FeedCategory.DAILY;

        CursorResult<FeedDto> expectedResult = new CursorResult<>(Collections.singletonList(mock(FeedDto.class)), false, null);

        when(feedTimelineService.getHomeFeeds(anyLong(), any(Language.class), nullable(String.class), anyInt(), any(FeedCategory.class)))
            .thenReturn(expectedResult);

        // when
        CursorResult<FeedDto> result = feedRetrievalService.getHomeFeeds(memberId, targetLanguage, cursor, size, category);

        // then
        assertThat(result.getValues()).hasSize(1);
    }
}
