
package com.synapps.resona.comment.command.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.synapps.resona.comment.command.entity.comment.Comment;
import com.synapps.resona.comment.command.entity.reply.Reply;
import com.synapps.resona.comment.command.repository.comment.CommentRepository;
import com.synapps.resona.comment.command.repository.reply.ReplyRepository;
import com.synapps.resona.comment.event.CommentCreatedEvent;
import com.synapps.resona.comment.event.CommentDeletedEvent;
import com.synapps.resona.comment.event.ReplyCreatedEvent;
import com.synapps.resona.comment.event.ReplyDeletedEvent;
import com.synapps.resona.command.entity.member.Member;
import com.synapps.resona.feed.command.entity.Feed;
import com.synapps.resona.feed.command.repository.FeedRepository;
import com.synapps.resona.feed.command.service.FeedCommandService;
import com.synapps.resona.command.repository.member.MemberRepository;
import com.synapps.resona.command.service.MemberService;
import com.synapps.resona.support.ServiceLayerTest;
import com.synapps.resona.support.fixture.CommentFixture;
import com.synapps.resona.support.fixture.FeedFixture;
import fixture.MemberFixture;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.event.ApplicationEvents;
import support.config.TestContainerConfig;
import support.database.DatabaseCleaner;

@ServiceLayerTest
@Import({CommentCommandService.class, DatabaseCleaner.class, TestContainerConfig.class})
@Disabled
class CommentCommandServiceTest {

    @Autowired
    private CommentCommandService commentCommandService;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ReplyRepository replyRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private FeedRepository feedRepository;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @MockBean
    private MemberService memberService;

    @MockBean
    private FeedCommandService feedCommandService;

    @MockBean
    private MentionService mentionService;

    @Autowired
    private ApplicationEvents applicationEvents;

    private Member testMember;
    private Feed testFeed;
    private Comment testComment;

    @BeforeEach
    void setUp() {
        databaseCleaner.execute();
        testMember = MemberFixture.createProfileTestMember("test@gmail.com");
        memberRepository.save(testMember);
        testFeed = FeedFixture.createDeletableFeed(testMember);
        feedRepository.save(testFeed);
        testComment = Comment.of(testFeed, testMember, "en", "test comment");
        commentRepository.save(testComment);
    }

    @Test
    @DisplayName("새로운 댓글을 성공적으로 등록한다.")
    void createComment_Success() {
        var commentRequest = CommentFixture.createCommentRequest(testFeed.getId());

        when(memberService.getMemberWithProfile(any())).thenReturn(testMember);
        when(feedCommandService.getFeed(any())).thenReturn(testFeed);
        when(mentionService.parseMentions(any())).thenReturn(Collections.emptyList());

        // when
        commentCommandService.createComment(testMember.getId(), commentRequest);

        // then
        assertThat(commentRepository.count()).isEqualTo(2); // setUp에서 하나, 테스트에서 하나
        long eventCount = applicationEvents.stream(CommentCreatedEvent.class).count();
        assertThat(eventCount).isEqualTo(1);
    }

    @Test
    @DisplayName("새로운 답글을 성공적으로 등록한다.")
    void createReply_Success() {
        var replyRequest = CommentFixture.createReplyRequest(testComment.getId());

        when(memberService.getMemberWithProfile(any())).thenReturn(testMember);
        when(mentionService.parseMentions(any())).thenReturn(Collections.emptyList());

        // when
        commentCommandService.createReply(testMember.getId(), replyRequest);

        // then
        Reply savedReply = replyRepository.findAll().get(0);
        assertThat(savedReply).isNotNull();
        assertThat(savedReply.getContent()).isEqualTo(CommentFixture.REPLY_CONTENT);
        assertThat(savedReply.getMember().getId()).isEqualTo(testMember.getId());
        assertThat(savedReply.getComment().getId()).isEqualTo(testComment.getId());

        long eventCount = applicationEvents.stream(ReplyCreatedEvent.class).count();
        assertThat(eventCount).isEqualTo(1);
    }

    @Test
    @DisplayName("댓글을 성공적으로 삭제한다.")
    void deleteComment_Success() {
        // when
        commentCommandService.deleteComment(testMember.getId(), testComment.getId());

        // then
        Optional<Comment> result = commentRepository.findById(testComment.getId());
        assertThat(result).isPresent();
        assertThat(result.get().isDeleted()).isTrue();

        long eventCount = applicationEvents.stream(CommentDeletedEvent.class).count();
        assertThat(eventCount).isEqualTo(1);
    }

    @Test
    @DisplayName("답글을 성공적으로 삭제한다.")
    void deleteReply_Success() {
        Reply reply = Reply.of(testComment, testMember, "ko", "test reply");
        replyRepository.save(reply);

        // when
        commentCommandService.deleteReply(testMember.getId(), reply.getId());

        // then
        Optional<Reply> result = replyRepository.findById(reply.getId());
        assertThat(result).isPresent();
        assertThat(result.get().isDeleted()).isTrue();

        long eventCount = applicationEvents.stream(ReplyDeletedEvent.class).count();
        assertThat(eventCount).isEqualTo(1);
    }
}
