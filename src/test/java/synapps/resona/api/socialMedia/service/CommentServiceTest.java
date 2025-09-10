package synapps.resona.api.socialMedia.service;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.EntityManager;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.transaction.annotation.Transactional;
import synapps.resona.api.support.IntegrationTestSupport;
import synapps.resona.api.fixture.CommentFixture;
import synapps.resona.api.fixture.FeedFixture;
import synapps.resona.api.fixture.MemberFixture;
import synapps.resona.api.member.entity.member.Member;
import synapps.resona.api.member.repository.member.MemberRepository;
import synapps.resona.api.socialMedia.dto.comment.CommentDto;
import synapps.resona.api.socialMedia.dto.comment.request.CommentRequest;
import synapps.resona.api.socialMedia.dto.comment.request.CommentUpdateRequest;
import synapps.resona.api.socialMedia.entity.comment.Comment;
import synapps.resona.api.socialMedia.entity.feed.Feed;
import synapps.resona.api.socialMedia.repository.comment.comment.CommentRepository;
import synapps.resona.api.socialMedia.repository.feed.FeedRepository;
import synapps.resona.api.socialMedia.service.comment.CommentService;

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
    member = MemberFixture.createMember("user@example.com", "testUser");
    member.encodePassword("securePass123!");
    memberRepository.save(member);

    feed = FeedFixture.createFeed(member, "테스트 피드입니다.");
    feedRepository.save(feed);

    setAuthentication(member.getEmail());
  }

  private void setAuthentication(String email) {
    User userPrincipal = new User(email, "", java.util.Collections.emptyList());
    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
        userPrincipal, null, userPrincipal.getAuthorities());
    SecurityContextHolder.getContext().setAuthentication(auth);
  }

  @Test
  @DisplayName("댓글을 등록한다.")
  void registerComment() {
    // given
    CommentRequest request = CommentFixture.createCommentRequest(feed.getId(), "댓글 내용입니다.");

    em.flush();
    em.clear();

    // when
    CommentDto response = commentService.register(request);

    // then
    assertThat(response).isNotNull();
    assertThat(response.getContent()).isEqualTo("댓글 내용입니다.");
  }

  @Test
  @DisplayName("댓글을 수정한다.")
  void updateComment() {
    // given
    Comment comment = CommentFixture.createComment(feed, member, "기존 댓글");
    commentRepository.save(comment);
    CommentUpdateRequest request = CommentFixture.createCommentUpdateRequest(comment.getId(), "수정된 댓글");

    em.flush();
    em.clear();

    // when
    CommentDto response = commentService.edit(request);

    // then
    assertThat(response.getCommentId()).isEqualTo(comment.getId());
    assertThat(response.getContent()).isEqualTo("수정된 댓글");
  }

  @Test
  @DisplayName("댓글을 단일 조회한다.")
  void readComment() {
    // given
    Comment comment = CommentFixture.createComment(feed, member, "단일 댓글");
    commentRepository.save(comment);

    em.flush();
    em.clear();

    // when
    CommentDto response = commentService.getComment(member.getId(), comment.getId());

    // then
    assertThat(response.getCommentId()).isEqualTo(comment.getId());
    assertThat(response.getContent()).isEqualTo("단일 댓글");
  }

  @Test
  @DisplayName("피드에 등록된 댓글 목록을 조회한다.")
  void readCommentsByFeedId() {
    // given
    Comment comment1 = CommentFixture.createComment(feed, member, "댓글1");
    Comment comment2 = CommentFixture.createComment(feed, member, "댓글2");
    commentRepository.saveAll(List.of(comment1, comment2));

    // 1차 캐시 초기화
    em.flush();
    em.clear();

    // when
    List<CommentDto> responseList = commentService.getCommentsByFeedId(1L, feed.getId());

    // then
    assertThat(responseList).hasSize(2);
    assertThat(responseList).extracting("content").containsExactlyInAnyOrder("댓글1", "댓글2");
  }

  @Test
  @DisplayName("댓글을 삭제한다 (soft delete).")
  void deleteComment() {
    // given
    Comment comment = CommentFixture.createComment(feed, member, "삭제될 댓글");
    commentRepository.save(comment);

    em.flush();
    em.clear();

    // when
    commentService.deleteComment(comment.getId());
    // then
    Comment deletedComment = commentRepository.findById(comment.getId()).get();
    assertThat(deletedComment.isDeleted()).isTrue();
  }
}

