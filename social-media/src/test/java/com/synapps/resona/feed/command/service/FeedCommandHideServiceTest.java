
package com.synapps.resona.feed.command.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.synapps.resona.entity.member.Member;
import com.synapps.resona.feed.command.entity.Feed;
import com.synapps.resona.feed.command.entity.FeedHide;
import com.synapps.resona.feed.command.repository.FeedHideRepository;
import com.synapps.resona.feed.command.repository.FeedRepository;
import com.synapps.resona.query.member.event.FeedHiddenEvent;
import com.synapps.resona.repository.member.MemberRepository;
import com.synapps.resona.service.MemberService;
import com.synapps.resona.support.ServiceLayerTest;
import com.synapps.resona.support.fixture.FeedFixture;
import fixture.MemberFixture;
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
    FeedCommandHideService.class,
    DatabaseCleaner.class,
    TestContainerConfig.class
})
class FeedCommandHideServiceTest {

    @Autowired
    private FeedCommandHideService feedCommandHideService;

    @Autowired
    private FeedHideRepository feedHideRepository;

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
    @DisplayName("피드를 성공적으로 숨긴다.")
    void hideFeed_Success() {
        // given
        Long memberId = testMember.getId();
        Long feedId = testFeed.getId();

        when(memberService.getMember(memberId)).thenReturn(testMember);
        when(feedCommandService.getFeed(feedId)).thenReturn(testFeed);

        // when
        feedCommandHideService.hideFeed(memberId, feedId);

        // then
        Optional<FeedHide> result = feedHideRepository.findByMemberAndFeed(testMember, testFeed);
        assertThat(result).isPresent();

        long eventCount = applicationEvents.stream(FeedHiddenEvent.class).count();
        assertThat(eventCount).isEqualTo(1);
    }
}
