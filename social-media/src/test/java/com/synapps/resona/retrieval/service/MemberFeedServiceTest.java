
package com.synapps.resona.retrieval.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.synapps.resona.entity.Language;
import com.synapps.resona.query.service.MemberStateService;
import com.synapps.resona.retrieval.dto.FeedDto;
import com.synapps.resona.retrieval.query.entity.FeedDocument;
import com.synapps.resona.retrieval.query.repository.FeedReadRepository;
import com.synapps.resona.translation.service.TranslationService;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class MemberFeedServiceTest {

    @InjectMocks
    private MemberFeedService memberFeedService;

    @Mock
    private FeedReadRepository feedReadRepository;

    @Mock
    private MemberStateService memberStateService;

    @Mock
    private TranslationService translationService;

    @Mock
    private FeedQueryHelper feedQueryHelper;

    @Test
    @DisplayName("내 피드 목록을 성공적으로 조회한다.")
    void getMyFeeds_Success() {
        // given
        Long memberId = 1L;
        Language targetLanguage = Language.en;
        Pageable pageable = PageRequest.of(0, 10);
        FeedDocument feedDocument = mock(FeedDocument.class);
        when(feedDocument.getFeedId()).thenReturn(1L);
        Page<FeedDocument> feedPage = new PageImpl<>(Collections.singletonList(feedDocument));

        when(feedQueryHelper.getHiddenFeedIds(anyLong())).thenReturn(Collections.emptySet());
        when(feedReadRepository.findByAuthor_MemberIdOrderByCreatedAtDesc(memberId, pageable)).thenReturn(feedPage);
        when(feedQueryHelper.translateAndConvertToDto(any(FeedDocument.class), any(Language.class))).thenReturn(mock(FeedDto.class));

        // when
        Page<FeedDto> result = memberFeedService.getMyFeeds(memberId, targetLanguage, pageable);

        // then
        assertThat(result).hasSize(1);
    }

    @Test
    @Disabled
    @DisplayName("스크랩한 피드 목록을 성공적으로 조회한다.")
    void getMyScrappedFeeds_Success() {
        // given
        Long memberId = 1L;
        Language targetLanguage = Language.en;
        Pageable pageable = PageRequest.of(0, 10);
        FeedDocument feedDocument = mock(FeedDocument.class);
        when(feedDocument.getFeedId()).thenReturn(1L);
        Page<FeedDocument> feedPage = new PageImpl<>(Collections.singletonList(feedDocument));

        Set<Long> scrappedFeedIds = new HashSet<>();
        scrappedFeedIds.add(1L);

        lenient().when(memberStateService.getScrappedFeedIds(anyLong())).thenReturn(scrappedFeedIds);
        lenient().when(feedReadRepository.findByFeedIdInOrderByCreatedAtDesc(scrappedFeedIds, pageable)).thenReturn(feedPage);
        lenient().when(feedQueryHelper.translateAndConvertToDto(any(FeedDocument.class), any(Language.class))).thenReturn(mock(FeedDto.class));

        // when
        Page<FeedDto> result = memberFeedService.getMyScrappedFeeds(memberId, targetLanguage, pageable);

        // then
        assertThat(result).hasSize(1);
    }
}
