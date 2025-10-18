
package com.synapps.resona.feed.command.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.synapps.resona.command.entity.member.Member;
import com.synapps.resona.feed.command.entity.Feed;
import com.synapps.resona.feed.command.entity.FeedLikes;
import com.synapps.resona.feed.command.repository.FeedLikesRepository;
import com.synapps.resona.feed.command.repository.FeedRepository;
import com.synapps.resona.feed.event.FeedLikeChangedEvent;
import com.synapps.resona.command.repository.member.MemberRepository;
import com.synapps.resona.command.service.MemberService;
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
    FeedCommandLikesService.class,
    DatabaseCleaner.class,
    TestContainerConfig.class
})
class FeedCommandLikesServiceTest {

    @Autowired
    private FeedCommandLikesService feedCommandLikesService;

    @Autowired
    private FeedLikesRepository feedLikesRepository;

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
    @DisplayName("피드에 좋아요를 성공적으로 누른다.")
    void likeFeed_Success() {
        // given
        Long memberId = testMember.getId();
        Long feedId = testFeed.getId();

        when(memberService.getMember(memberId)).thenReturn(testMember);
        when(feedCommandService.getFeed(feedId)).thenReturn(testFeed);

        // when
        feedCommandLikesService.likeFeed(memberId, feedId);

        // then
        Optional<FeedLikes> result = feedLikesRepository.findByMemberAndFeed(testMember, testFeed);
        assertThat(result).isPresent();

        Feed updatedFeed = feedRepository.findById(feedId).get();
        assertThat(updatedFeed.getLikeCount()).isEqualTo(1);

        long eventCount = applicationEvents.stream(FeedLikeChangedEvent.class).count();
        assertThat(eventCount).isEqualTo(1);
    }

    @Test
    @DisplayName("피드 좋아요를 성공적으로 취소한다.")
    void unlikeFeed_Success() {
        // given
        Long memberId = testMember.getId();
        Long feedId = testFeed.getId();

        FeedLikes feedLike = FeedLikes.of(testMember, testFeed);
        feedLikesRepository.save(feedLike);
        testFeed.increaseLikeCount();
        feedRepository.save(testFeed);

        when(memberService.getMember(memberId)).thenReturn(testMember);
        when(feedCommandService.getFeed(feedId)).thenReturn(testFeed);

        // when
        feedCommandLikesService.unlikeFeed(memberId, feedId);

        // then
        Optional<FeedLikes> result = feedLikesRepository.findByMemberAndFeed(testMember, testFeed);
        assertThat(result).isPresent();
        assertThat(result.get().isDeleted()).isTrue();

        Feed updatedFeed = feedRepository.findById(feedId).get();
        assertThat(updatedFeed.getLikeCount()).isZero();

        long eventCount = applicationEvents.stream(FeedLikeChangedEvent.class).count();
        assertThat(eventCount).isEqualTo(1);
    }
}
