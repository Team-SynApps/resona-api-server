
package com.synapps.resona.comment.command.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.synapps.resona.comment.command.entity.comment.Comment;
import com.synapps.resona.comment.command.entity.comment.CommentLikes;
import com.synapps.resona.comment.command.entity.reply.Reply;
import com.synapps.resona.comment.command.entity.reply.ReplyLikes;
import com.synapps.resona.comment.command.repository.comment.CommentLikesRepository;
import com.synapps.resona.comment.command.repository.comment.CommentRepository;
import com.synapps.resona.comment.command.repository.reply.ReplyLikesRepository;
import com.synapps.resona.comment.command.repository.reply.ReplyRepository;
import com.synapps.resona.comment.event.CommentLikeChangedEvent;
import com.synapps.resona.comment.event.ReplyLikeChangedEvent;
import com.synapps.resona.command.entity.member.Member;
import com.synapps.resona.feed.command.entity.Feed;
import com.synapps.resona.feed.command.repository.FeedRepository;
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
@Import({CommentLikeService.class, DatabaseCleaner.class, TestContainerConfig.class})
class CommentLikeServiceTest {

    @Autowired
    private CommentLikeService commentLikeService;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private CommentLikesRepository commentLikesRepository;

    @Autowired
    private ReplyRepository replyRepository;

    @Autowired
    private ReplyLikesRepository replyLikesRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private FeedRepository feedRepository;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @MockBean
    private MemberService memberService;

    @Autowired
    private ApplicationEvents applicationEvents;

    private Member testMember;
    private Feed testFeed;
    private Comment testComment;
    private Reply testReply;

    @BeforeEach
    void setUp() {
        databaseCleaner.execute();
        testMember = MemberFixture.createProfileTestMember("test@gmail.com");
        memberRepository.save(testMember);
        testFeed = FeedFixture.createDeletableFeed(testMember);
        feedRepository.save(testFeed);
        testComment = Comment.of(testFeed, testMember, "ko", "test comment");
        commentRepository.save(testComment);
        testReply = Reply.of(testComment, testMember, "ko", "test reply");
        replyRepository.save(testReply);
    }

    @Test
    @DisplayName("댓글에 좋아요를 성공적으로 등록한다.")
    void likeComment_Success() {
        // given
        when(memberService.getMember(any())).thenReturn(testMember);

        // when
        commentLikeService.likeComment(testMember.getId(), testComment.getId());

        // then
        CommentLikes savedLike = commentLikesRepository.findAll().get(0);
        assertThat(savedLike).isNotNull();
        assertThat(savedLike.getMember().getId()).isEqualTo(testMember.getId());
        assertThat(savedLike.getComment().getId()).isEqualTo(testComment.getId());

        long eventCount = applicationEvents.stream(CommentLikeChangedEvent.class).count();
        assertThat(eventCount).isEqualTo(1);
    }

    @Test
    @DisplayName("댓글 좋아요를 성공적으로 취소한다.")
    void unlikeComment_Success() {
        // given
        CommentLikes like = CommentLikes.of(testMember, testComment);
        commentLikesRepository.save(like);
        when(memberService.getMember(any())).thenReturn(testMember);

        // when
        commentLikeService.unlikeComment(testMember.getId(), testComment.getId());

        // then
        Optional<CommentLikes> result = commentLikesRepository.findById(like.getId());
        assertThat(result).isPresent();
        assertThat(result.get().isDeleted()).isTrue();

        long eventCount = applicationEvents.stream(CommentLikeChangedEvent.class).count();
        assertThat(eventCount).isEqualTo(1);
    }

    @Test
    @DisplayName("답글에 좋아요를 성공적으로 등록한다.")
    void likeReply_Success() {
        // given
        when(memberService.getMember(any())).thenReturn(testMember);

        // when
        commentLikeService.likeReply(testMember.getId(), testReply.getId());

        // then
        ReplyLikes savedLike = replyLikesRepository.findAll().get(0);
        assertThat(savedLike).isNotNull();
        assertThat(savedLike.getMember().getId()).isEqualTo(testMember.getId());
        assertThat(savedLike.getReply().getId()).isEqualTo(testReply.getId());

        long eventCount = applicationEvents.stream(ReplyLikeChangedEvent.class).count();
        assertThat(eventCount).isEqualTo(1);
    }

    @Test
    @DisplayName("답글 좋아요를 성공적으로 취소한다.")
    void unlikeReply_Success() {
        // given
        ReplyLikes like = ReplyLikes.of(testMember, testReply);
        replyLikesRepository.save(like);
        when(memberService.getMember(any())).thenReturn(testMember);

        // when
        commentLikeService.unlikeReply(testMember.getId(), testReply.getId());

        // then
        Optional<ReplyLikes> result = replyLikesRepository.findById(like.getId());
        assertThat(result).isPresent();
        assertThat(result.get().isDeleted()).isTrue();

        long eventCount = applicationEvents.stream(ReplyLikeChangedEvent.class).count();
        assertThat(eventCount).isEqualTo(1);
    }
}
