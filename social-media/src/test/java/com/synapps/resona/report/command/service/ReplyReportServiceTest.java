
package com.synapps.resona.report.command.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.synapps.resona.comment.command.entity.comment.Comment;
import com.synapps.resona.comment.command.entity.reply.Reply;
import com.synapps.resona.comment.command.repository.comment.CommentRepository;
import com.synapps.resona.comment.command.repository.reply.ReplyRepository;
import com.synapps.resona.entity.member.Member;
import com.synapps.resona.feed.command.entity.Feed;
import com.synapps.resona.feed.command.repository.FeedRepository;
import com.synapps.resona.report.command.entity.ReplyReport;
import com.synapps.resona.report.command.repository.ReplyReportRepository;
import com.synapps.resona.report.common.entity.ReportCategory;
import com.synapps.resona.report.event.ReplyReportedEvent;
import com.synapps.resona.repository.member.MemberRepository;
import com.synapps.resona.service.MemberService;
import com.synapps.resona.support.ServiceLayerTest;
import com.synapps.resona.support.fixture.FeedFixture;
import fixture.MemberFixture;
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
@Import({ReplyReportService.class, DatabaseCleaner.class, TestContainerConfig.class})
class ReplyReportServiceTest {

    @Autowired
    private ReplyReportService replyReportService;

    @Autowired
    private ReplyReportRepository replyReportRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private FeedRepository feedRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ReplyRepository replyRepository;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @MockBean
    private MemberService memberService;

    @Autowired
    private ApplicationEvents applicationEvents;

    private Member reporter;
    private Member reported;
    private Feed testFeed;
    private Comment testComment;
    private Reply testReply;

    @BeforeEach
    void setUp() {
        databaseCleaner.execute();
        reporter = MemberFixture.createProfileTestMember("reporter@gmail.com");
        reported = MemberFixture.createProfileTestMember("reported@gmail.com");
        memberRepository.saveAll(java.util.List.of(reporter, reported));

        testFeed = FeedFixture.createDeletableFeed(reported);
        feedRepository.save(testFeed);

        testComment = Comment.of(testFeed, reported, "ko", "test comment");
        commentRepository.save(testComment);

        testReply = Reply.of(testComment, reported, "ko", "test reply");
        replyRepository.save(testReply);
    }

    @Test
    @DisplayName("답글을 성공적으로 신고한다.")
    void reportReply_Success() {
        // given
        when(memberService.getMember(reporter.getId())).thenReturn(reporter);

        // when
        replyReportService.reportReply(reporter.getId(), testReply.getId(), ReportCategory.SPAM, false);

        // then
        ReplyReport savedReport = replyReportRepository.findAll().get(0);
        assertThat(savedReport).isNotNull();
        assertThat(savedReport.getReporter().getId()).isEqualTo(reporter.getId());
        assertThat(savedReport.getReported().getId()).isEqualTo(reported.getId());
        assertThat(savedReport.getReply().getId()).isEqualTo(testReply.getId());
        assertThat(savedReport.getCategory()).isEqualTo(ReportCategory.SPAM);

        long eventCount = applicationEvents.stream(ReplyReportedEvent.class).count();
        assertThat(eventCount).isEqualTo(1);
    }
}
