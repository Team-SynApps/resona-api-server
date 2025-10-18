
package com.synapps.resona.feed.command.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.synapps.resona.command.entity.member.Member;
import com.synapps.resona.feed.command.entity.Feed;
import com.synapps.resona.feed.command.entity.Scrap;
import com.synapps.resona.feed.command.repository.FeedRepository;
import com.synapps.resona.feed.command.repository.ScrapRepository;
import com.synapps.resona.feed.dto.ScrapResponse;
import com.synapps.resona.feed.event.FeedScrappedEvent;
import com.synapps.resona.feed.exception.ScrapException;
import com.synapps.resona.command.repository.member.MemberRepository;
import com.synapps.resona.command.service.MemberService;
import com.synapps.resona.support.ServiceLayerTest;
import com.synapps.resona.support.fixture.FeedFixture;
import fixture.MemberFixture;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.event.ApplicationEvents;
import support.config.TestContainerConfig;
import support.database.DatabaseCleaner;

@ServiceLayerTest
@Import({
    ScrapCommandService.class,
    DatabaseCleaner.class,
    TestContainerConfig.class
})
class ScrapCommandServiceTest {

    @Autowired
    private ScrapCommandService scrapCommandService;

    @Autowired
    private ScrapRepository scrapRepository;

    @Autowired
    private FeedRepository feedRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @Autowired
    private ApplicationEvents applicationEvents;

    @MockBean
    private FeedCommandService feedCommandService;

    @MockBean
    private MemberService memberService;

    private Member testMember;
    private Feed testFeed;

    @BeforeEach
    void setUp() {
        databaseCleaner.execute();
        testMember = MemberFixture.createProfileTestMember("test@gmail.com");
        memberRepository.save(testMember);
        testFeed = FeedFixture.createDeletableFeed(testMember);
        feedRepository.save(testFeed);
    }

    @Test
    @DisplayName("피드를 성공적으로 스크랩한다.")
    void scrapFeed_Success() {
        // given
        Long memberId = testMember.getId();
        Long feedId = testFeed.getId();

        when(memberService.getMember(memberId)).thenReturn(testMember);
        when(feedCommandService.getFeed(feedId)).thenReturn(testFeed);

        // when
        ScrapResponse result = scrapCommandService.scrapFeed(memberId, feedId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getFeedId()).isEqualTo(feedId);

        Optional<Scrap> savedScrap = scrapRepository.findByMemberAndFeed(testMember, testFeed);
        assertThat(savedScrap).isPresent();

        long eventCount = applicationEvents.stream(FeedScrappedEvent.class).count();
        assertThat(eventCount).isEqualTo(1);
    }

    @Test
    @DisplayName("이미 스크랩한 피드를 다시 스크랩하면 예외가 발생한다.")
    void scrapFeed_AlreadyExists_ThrowsException() {
        // given
        Long memberId = testMember.getId();
        Long feedId = testFeed.getId();

        scrapRepository.save(Scrap.of(testMember, testFeed, LocalDateTime.now()));

        when(memberService.getMember(memberId)).thenReturn(testMember);
        when(feedCommandService.getFeed(feedId)).thenReturn(testFeed);

        // when & then
        assertThrows(ScrapException.class, () -> {
            scrapCommandService.scrapFeed(memberId, feedId);
        });
    }

    @Test
    @DisplayName("피드 스크랩을 성공적으로 취소한다.")
    void unscrapFeed_Success() {
        // given
        Long memberId = testMember.getId();
        Long feedId = testFeed.getId();

        Scrap scrap = Scrap.of(testMember, testFeed, LocalDateTime.now());
        scrapRepository.save(scrap);

        when(memberService.getMember(memberId)).thenReturn(testMember);
        when(feedCommandService.getFeed(feedId)).thenReturn(testFeed);

        // when
        scrapCommandService.unscrapFeed(memberId, feedId);

        // then
        Optional<Scrap> result = scrapRepository.findById(scrap.getId());
        assertThat(result).isPresent();
        assertThat(result.get().isDeleted()).isTrue();

        long eventCount = applicationEvents.stream(FeedScrappedEvent.class).count();
        assertThat(eventCount).isEqualTo(1);
    }
}
