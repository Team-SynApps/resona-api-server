package synapps.resona.api.mysql.socialMedia.service;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.transaction.annotation.Transactional;
import synapps.resona.api.IntegrationTestSupport;
import synapps.resona.api.mysql.member.entity.account.AccountInfo;
import synapps.resona.api.mysql.member.entity.account.AccountStatus;
import synapps.resona.api.mysql.member.entity.account.RoleType;
import synapps.resona.api.mysql.member.entity.member.*;
import synapps.resona.api.mysql.member.entity.member_details.MemberDetails;
import synapps.resona.api.mysql.member.entity.profile.Profile;
import synapps.resona.api.mysql.member.repository.MemberRepository;
import synapps.resona.api.mysql.socialMedia.dto.comment.request.CommentRequest;
import synapps.resona.api.mysql.socialMedia.dto.comment.request.CommentUpdateRequest;
import synapps.resona.api.mysql.socialMedia.dto.comment.response.CommentPostResponse;
import synapps.resona.api.mysql.socialMedia.dto.comment.response.CommentReadResponse;
import synapps.resona.api.mysql.socialMedia.dto.comment.response.CommentUpdateResponse;
import synapps.resona.api.mysql.socialMedia.entity.Comment;
import synapps.resona.api.mysql.socialMedia.entity.feed.Feed;
import synapps.resona.api.mysql.socialMedia.repository.CommentRepository;
import synapps.resona.api.mysql.socialMedia.repository.FeedRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
class CommentServiceTest extends IntegrationTestSupport {

    @Autowired
    private CommentService commentService;

    @Autowired
    private FeedRepository feedRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private EntityManager em;

    private Member member;
    private Feed feed;

    @BeforeEach
    void setUp() {
        AccountInfo accountInfo = AccountInfo.of(
                RoleType.USER,
                synapps.resona.api.oauth.entity.ProviderType.LOCAL,
                AccountStatus.ACTIVE
        );
        member = Member.of(
                accountInfo,
                MemberDetails.empty(),
                Profile.empty(),
                "user@example.com",
                "securePass123!",
                LocalDateTime.now()
        );
        member.encodePassword("securePass123!");
        memberRepository.save(member);

        feed = Feed.of(member, "테스트 피드입니다.", "DAILY");
        feedRepository.save(feed);

        setAuthentication(member.getEmail());
    }

    private void setAuthentication(String email) {
        User userPrincipal = new User(email, "", java.util.Collections.emptyList());
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    @DisplayName("댓글을 등록한다.")
    void registerComment() {
        // given
        CommentRequest request = new CommentRequest(feed.getId(), "댓글 내용입니다.");

        em.flush();
        em.clear();

        // when
        CommentPostResponse response = commentService.register(request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getContent()).isEqualTo("댓글 내용입니다.");
    }

    @Test
    @DisplayName("댓글을 수정한다.")
    void updateComment() {
        // given
        Comment comment = Comment.of(feed, member, "기존 댓글");
        commentRepository.save(comment);
        CommentUpdateRequest request = new CommentUpdateRequest(comment.getId(), "수정된 댓글");

        em.flush();
        em.clear();

        // when
        CommentUpdateResponse response = commentService.edit(request);

        // then
        assertThat(response.getCommentId()).isEqualTo(comment.getId());
        assertThat(response.getContent()).isEqualTo("수정된 댓글");
    }

    @Test
    @DisplayName("댓글을 단일 조회한다.")
    void readComment() {
        // given
        Comment comment = Comment.of(feed, member, "단일 댓글");
        commentRepository.save(comment);

        em.flush();
        em.clear();

        // when
        CommentReadResponse response = commentService.getComment(comment.getId());

        // then
        assertThat(response.getCommentId()).isEqualTo(comment.getId());
        assertThat(response.getContent()).isEqualTo("단일 댓글");
    }

    @Test
    @DisplayName("피드에 등록된 댓글 목록을 조회한다.")
    void readCommentsByFeedId() {
        // given
        Comment comment1 = Comment.of(feed, member, "댓글1");
        Comment comment2 = Comment.of(feed, member, "댓글2");
        commentRepository.saveAll(List.of(comment1, comment2));

        // 1차 캐시 초기화
        em.flush();
        em.clear();

        // when
        List<CommentPostResponse> responseList = commentService.getCommentsByFeedId(feed.getId());

        // then
        assertThat(responseList).hasSize(2);
        assertThat(responseList).extracting("content").containsExactlyInAnyOrder("댓글1", "댓글2");
    }

    @Test
    @DisplayName("댓글을 삭제한다 (soft delete).")
    void deleteComment() {
        // given
        Comment comment = Comment.of(feed, member, "삭제될 댓글");
        commentRepository.save(comment);

        em.flush();
        em.clear();

        // when
        Comment deleted = commentService.deleteComment(comment.getId());

        // then
        assertThat(deleted.isDeleted()).isTrue();
    }
}
