
package com.synapps.resona.retrieval.service.strategy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.synapps.resona.feed.command.entity.FeedCategory;
import com.synapps.resona.entity.profile.CountryCode;
import com.synapps.resona.retrieval.query.entity.FeedDocument;
import com.synapps.resona.retrieval.query.repository.FeedReadRepository;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class RecentAllFallbackStrategyTest {

    @InjectMocks
    private RecentAllFallbackStrategy strategy;

    @Mock
    private FeedReadRepository feedReadRepository;

    @Test
    @DisplayName("국가와 카테고리가 null일 때 지원한다.")
    void supports_CountryAndCategoryNull_ReturnsTrue() {
        // given
        FallbackStrategyContext context = new FallbackStrategyContext(PageRequest.of(0, 10), null, null);

        // when
        boolean supports = strategy.supports(context);

        // then
        assertThat(supports).isTrue();
    }

    @Test
    @DisplayName("국가가 null이 아닐 때 지원하지 않는다.")
    void supports_CountryNotNull_ReturnsFalse() {
        // given
        FallbackStrategyContext context = new FallbackStrategyContext(PageRequest.of(0, 10), null, CountryCode.KR);

        // when
        boolean supports = strategy.supports(context);

        // then
        assertThat(supports).isFalse();
    }

    @Test
    @DisplayName("카테고리가 null이 아닐 때 지원하지 않는다.")
    void supports_CategoryNotNull_ReturnsFalse() {
        // given
        FallbackStrategyContext context = new FallbackStrategyContext(PageRequest.of(0, 10), FeedCategory.DAILY, null);

        // when
        boolean supports = strategy.supports(context);

        // then
        assertThat(supports).isFalse();
    }

    @Test
    @DisplayName("최신 피드를 성공적으로 조회한다.")
    void findFeeds_Success() {
        // given
        PageRequest pageable = PageRequest.of(0, 10);
        FallbackStrategyContext context = new FallbackStrategyContext(pageable, null, null);
        FeedDocument feedDocument = mock(FeedDocument.class);
        List<FeedDocument> expectedFeeds = Collections.singletonList(feedDocument);

        when(feedReadRepository.findAllByOrderByCreatedAtDesc(pageable)).thenReturn(expectedFeeds);

        // when
        List<FeedDocument> result = strategy.findFeeds(context);

        // then
        assertThat(result).isEqualTo(expectedFeeds);
    }
}
