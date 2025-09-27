package com.synapps.resona.comment.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.synapps.resona.query.dto.comment.CommentProjectionDto;
import com.synapps.resona.domain.entity.comment.Comment;
import com.synapps.resona.domain.repository.comment.comment.CommentQueryRepository;
import com.synapps.resona.entity.member.Member;
import com.synapps.resona.domain.entity.feed.Feed;
import com.synapps.resona.domain.repository.feed.dsl.FeedExpressions;
import com.synapps.resona.fixture.CommentFixture;
import com.synapps.resona.fixture.FeedFixture;
import com.synapps.resona.fixture.FeedMemberFixture;
import com.synapps.resona.fixture.RestrictionFixture;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import support.anotation.TestQueryDslConfig;


@DataJpaTest
@Import({TestQueryDslConfig.class, FeedExpressions.class, CommentQueryRepository.class})
@Disabled
class CommentQueryRepositoryTest {

  @Autowired
  private EntityManager em;

  @Autowired
  private JPAQueryFactory queryFactory;

  @Autowired
  private CommentQueryRepository commentRepository;

  // 테스트용 데이터
  private Member viewer, writer, blockedWriter;
  private Feed feed;
  private Comment normalComment, hiddenComment, blockedComment, otherFeedComment;

  @BeforeEach
  void setUp() {
    commentRepository = new CommentQueryRepository(queryFactory);

    viewer = FeedMemberFixture.createMember("viewer@test.com", "viewer");
    writer = FeedMemberFixture.createMember("writer@test.com", "writer");
    blockedWriter = FeedMemberFixture.createMember("blocked@test.com", "blocked");
    persistMember(viewer);
    persistMember(writer);
    persistMember(blockedWriter);

    em.persist(RestrictionFixture.createBlock(viewer, blockedWriter));

    feed = FeedFixture.createFeed(writer, "Test Feed Content");
    Feed anotherFeed = FeedFixture.createFeed(writer, "Another Feed");
    em.persist(feed);
    em.persist(anotherFeed);

    normalComment = CommentFixture.createComment(feed, writer, "This is a normal comment.");
    hiddenComment = CommentFixture.createComment(feed, writer, "This is a hidden comment.");
    blockedComment = CommentFixture.createComment(feed, blockedWriter, "This is from a blocked writer.");
    otherFeedComment = CommentFixture.createComment(anotherFeed, writer, "This is on another feed.");
    em.persist(normalComment);
    em.persist(hiddenComment);
    em.persist(blockedComment);
    em.persist(otherFeedComment);

    em.persist(RestrictionFixture.createCommentHide(viewer, hiddenComment));

    // 좋아요 수 데이터 추가
    // normalComment: 2개
    em.persist(CommentFixture.createCommentLike(viewer, normalComment));
    em.persist(CommentFixture.createCommentLike(writer, normalComment));
    // hiddenComment: 1개
    em.persist(CommentFixture.createCommentLike(viewer, hiddenComment));
    // blockedComment: 0개

    em.flush();
    em.clear();
  }

  @Test
  @DisplayName("ID로 댓글 단건 조회 시, 차단/숨김/좋아요 수를 정확히 포함하여 조회한다")
  void findCommentProjectionByIdTest() {
    // Case 1: 정상 댓글 조회
    Optional<CommentProjectionDto> normalResult = commentRepository.findCommentProjectionById(viewer.getId(), normalComment.getId());
    assertThat(normalResult).isPresent();
    normalResult.ifPresent(dto -> {
      assertThat(dto.isBlocked()).isFalse();
      assertThat(dto.isHidden()).isFalse();
      assertThat(dto.getLikeCount()).isEqualTo(2L);
      assertThat(dto.getComment().getId()).isEqualTo(normalComment.getId());
    });

    // Case 2: 숨김 처리한 댓글 조회
    Optional<CommentProjectionDto> hiddenResult = commentRepository.findCommentProjectionById(viewer.getId(), hiddenComment.getId());
    assertThat(hiddenResult).isPresent();
    hiddenResult.ifPresent(dto -> {
      assertThat(dto.isBlocked()).isFalse();
      assertThat(dto.isHidden()).isTrue();
      assertThat(dto.getLikeCount()).isEqualTo(1L);
      assertThat(dto.getComment().getId()).isEqualTo(hiddenComment.getId());
    });

    // Case 3: 차단된 사용자의 댓글 조회
    Optional<CommentProjectionDto> blockedResult = commentRepository.findCommentProjectionById(viewer.getId(), blockedComment.getId());
    assertThat(blockedResult).isPresent();
    blockedResult.ifPresent(dto -> {
      assertThat(dto.isBlocked()).isTrue();
      assertThat(dto.isHidden()).isFalse();
      assertThat(dto.getComment().getId()).isEqualTo(blockedComment.getId());
      assertThat(dto.getLikeCount()).isZero();
    });

    // Case 4: 존재하지 않는 댓글 조회
    Optional<CommentProjectionDto> nonExistentResult = commentRepository.findCommentProjectionById(viewer.getId(), 9999L);
    assertThat(nonExistentResult).isEmpty();
  }

  @Test
  @DisplayName("피드 ID로 댓글 목록 조회 시, 차단/숨김/좋아요 수를 정확히 포함하여 모든 댓글을 조회한다")
  void findAllCommentsByFeedIdWithRepliesTest() {
    // when
    List<CommentProjectionDto> result = commentRepository.findAllCommentsByFeedIdWithReplies(viewer.getId(), feed.getId());

    // then
    assertThat(result).hasSize(3);

    // normalComment 검증
    CommentProjectionDto normalDto = result.stream()
        .filter(dto -> dto.getComment().getId().equals(normalComment.getId()))
        .findFirst().orElseThrow();
    assertThat(normalDto.isBlocked()).isFalse();
    assertThat(normalDto.isHidden()).isFalse();
    assertThat(normalDto.getLikeCount()).isEqualTo(2L);
    assertThat(normalDto.getComment().getContent()).isEqualTo("This is a normal comment.");

    // hiddenComment 검증
    CommentProjectionDto hiddenDto = result.stream()
        .filter(dto -> dto.getComment().getId().equals(hiddenComment.getId()))
        .findFirst().orElseThrow();
    assertThat(hiddenDto.isBlocked()).isFalse();
    assertThat(hiddenDto.isHidden()).isTrue();
    assertThat(hiddenDto.getLikeCount()).isEqualTo(1L);

    // blockedComment 검증
    CommentProjectionDto blockedDto = result.stream()
        .filter(dto -> dto.getComment().getId().equals(blockedComment.getId()))
        .findFirst().orElseThrow();
    assertThat(blockedDto.isBlocked()).isTrue();
    assertThat(blockedDto.isHidden()).isFalse();
    assertThat(blockedDto.getLikeCount()).isZero();

    // otherFeedComment가 없는지 확인
    List<Long> commentIds = result.stream().map(dto -> dto.getComment().getId()).toList();
    assertThat(commentIds).doesNotContain(otherFeedComment.getId());
  }

  private void persistMember(Member member) {
    em.persist(member.getAccountInfo());
    em.persist(member.getMemberDetails());
    em.persist(member.getProfile());
    em.persist(member);
  }
}